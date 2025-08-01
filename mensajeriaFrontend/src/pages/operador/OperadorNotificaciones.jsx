import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Footer from '../../components/Footer'; 

export default function OperadorNotificaciones() {
  const [notificaciones, setNotificaciones] = useState([]);
  const [notificacionesFiltradas, setNotificacionesFiltradas] = useState([]);
  const [tiposNotificacion, setTiposNotificacion] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [pageSize] = useState(10);
  const [filtros, setFiltros] = useState({
    tipo: '',
    leida: '',
    fechaDesde: '',
    fechaHasta: ''
  });
  const [vista, setVista] = useState('lista');
  const [currentUserId, setCurrentUserId] = useState(null);
  const [empresaActual, setEmpresaActual] = useState(null);

  const token = localStorage.getItem('token');
  const additionalData = localStorage.getItem('x-additional-data');
  const headers = {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
    'x-additional-data': additionalData,
  };

  const decodeToken = (token) => {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      console.error("Token inválido", e);
      return {};
    }
  };

  const decoded = decodeToken(token);
  const mensajeriaId = decoded?.mensajeria_id;
  const usuarioId = decoded?.user_id;

  useEffect(() => {
    if (usuarioId) {
      setCurrentUserId(usuarioId);
      cargarDatos();
    } else {
      setError('No se pudo obtener el ID de usuario del token');
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (currentUserId && currentPage > 0) {
      cargarDatos();
    }
  }, [currentPage]);

  useEffect(() => {
    if (notificaciones.length > 0) {
      aplicarFiltros();
    }
  }, [filtros, notificaciones]);

  const cargarDatos = async () => {
    setLoading(true);
    setError(null);

    if (!usuarioId) {
      console.error('Usuario ID no disponible');
      setError('Usuario ID no disponible');
      setLoading(false);
      return;
    }

    try {
      
      const [
        notificacionesResponse,
        tiposNotificacionResponse,
        usuariosResponse
      ] = await Promise.all([
        axios.get(`/proxy/api/notificaciones/usuario/${usuarioId}`, {
          headers,
          params: {
            page: currentPage,
            size: pageSize,
            sort: 'fechaCreacion,desc'
          }
        }),
        axios.get('/proxy/api/tipos-notificacion', { headers }),
        axios.get('/proxy/api/usuarios', { headers })
      ]);

      const procesar = (res, setter, nombre) => {
        if (res.data?.success) {
          const datos = res.data.data;
          if (Array.isArray(datos?.content)) {
            setter(datos.content);
            return datos;
          } else if (Array.isArray(datos)) {
            setter(datos);
            return datos;
          } else if (datos && typeof datos === 'object') {
            // Si es un objeto con paginación
            const content = datos.content || [];
            setter(content);
            return datos;
          } else {
            console.error(`Formato inválido en ${nombre}:`, datos);
            setter([]);
            return null;
          }
        } else {
          console.error(`Error en ${nombre}:`, res.data?.error || 'Respuesta inválida');
          setter([]);
          return null;
        }
      };

      // Procesar respuestas
      const notificacionesData = procesar(notificacionesResponse, () => {}, 'notificaciones');
      const tiposData = procesar(tiposNotificacionResponse, setTiposNotificacion, 'tipos de notificación');
      const usuariosData = procesar(usuariosResponse, setUsuarios, 'usuarios');

      if (notificacionesData) {
        const notificacionesContent = notificacionesData.content || notificacionesData || [];
        

        // Transformar notificaciones
        const notificacionesTransformadas = notificacionesContent.map(notif => {
          const usuarioReal = usuariosData?.find(u => u.id === notif.usuarioId) || {};
          const tipoReal = tiposData?.find(t => t.id === notif.tipoNotificacionId) || {};

          return {
            id: notif.id,
            titulo: notif.titulo,
            mensaje: notif.mensaje,
            tipoNotificacion: {
              id: notif.tipoNotificacionId,
              nombre: formatearNombreTipo(tipoReal.nombre || notif.tipoNotificacionNombre),
              nombreOriginal: tipoReal.nombre || notif.tipoNotificacionNombre,
              icono: getIconoTipo(tipoReal.nombre || notif.tipoNotificacionNombre),
              color: getColorTipo(tipoReal.nombre || notif.tipoNotificacionNombre)
            },
            usuario: {
              id: notif.usuarioId,
              nombreUsuario: usuarioReal.nombreUsuario || `usuario_${notif.usuarioId}`,
              nombres: `${usuarioReal.nombres || 'Usuario'} ${usuarioReal.apellidos || ''}`.trim()
            },
            leida: notif.leida,
            fechaCreacion: notif.fechaCreacion,
            fechaLectura: notif.fechaLectura || null
          };
        });


        setNotificaciones(notificacionesTransformadas);
        setNotificacionesFiltradas(notificacionesTransformadas);
        setTotalPages(notificacionesData.totalPages || 1);
        setTotalElements(notificacionesData.totalElements || notificacionesTransformadas.length);
      }

    } catch (error) {
      console.error('❌ Error al cargar notificaciones:', error);
      console.error('Detalles del error:', error.response?.data);
      setError(`Error al cargar las notificaciones: ${error.message}`);
      setNotificaciones([]);
      setTiposNotificacion([]);
      setUsuarios([]);
    } finally {
      setLoading(false);
    }
  };

  const formatearNombreTipo = (nombre) => {
    if (!nombre) return '';
    
    const nombresEspeciales = {
      'alerta_arqueo': 'Alerta de arqueo',
      'pedido_asignado': 'Pedido asignado',
      'pedido_cambio': 'Cambio de pedido',
      'pedido_completado': 'Pedido completado'
    };
    
    if (nombresEspeciales[nombre.toLowerCase()]) {
      return nombresEspeciales[nombre.toLowerCase()];
    }
    
    return nombre
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, letra => letra.toUpperCase());
  };

  const getIconoTipo = (tipo) => {
    const iconos = {
      'Asignación': 'bi-person-check',
      'Alerta': 'bi-exclamation-triangle',
      'Completado': 'bi-check-circle',
      'Actualización': 'bi-arrow-repeat',
      'Información': 'bi-info-circle',
      'Advertencia': 'bi-exclamation-triangle-fill'
    };
    return iconos[tipo] || 'bi-bell';
  };

  const getColorTipo = (tipo) => {
    const colores = {
      'Asignación': '#28a745',
      'Alerta': '#dc3545',
      'Completado': '#20c997',
      'Actualización': '#fd7e14',
      'Información': '#17a2b8',
      'Advertencia': '#ffc107'
    };
    return colores[tipo] || '#6c757d';
  };

  const manejarFechaPersonalizada = (tipo, valor) => {
    setFiltros(prev => {
      const nuevaFecha = {
        ...prev,
        [tipo]: valor
      };
      
      if (nuevaFecha.fechaDesde && nuevaFecha.fechaHasta) {
        const fechaInicio = new Date(nuevaFecha.fechaDesde);
        const fechaFin = new Date(nuevaFecha.fechaHasta);
        
        if (fechaInicio > fechaFin) {
          if (tipo === 'fechaDesde') {
            nuevaFecha.fechaHasta = valor; 
          } else {
            nuevaFecha.fechaDesde = valor;
          }
        }
      }
      
      return nuevaFecha;
    });
  };

  const aplicarFiltros = () => {
    let filtradas = [...notificaciones];

    if (filtros.tipo) {
      filtradas = filtradas.filter(n => 
        n.tipoNotificacion.nombre === filtros.tipo
      );
    }

    if (filtros.leida !== '') {
      const esLeida = filtros.leida === 'true';
      filtradas = filtradas.filter(n => n.leida === esLeida);
    }

    if (filtros.fechaDesde) {
      filtradas = filtradas.filter(n => 
        new Date(n.fechaCreacion) >= new Date(filtros.fechaDesde)
      );
    }

    if (filtros.fechaHasta) {
      filtradas = filtradas.filter(n => 
        new Date(n.fechaCreacion) <= new Date(filtros.fechaHasta + 'T23:59:59')
      );
    }

    setNotificacionesFiltradas(filtradas);
  };

  const handleFiltroChange = (e) => {
    const { name, value } = e.target;
    setFiltros(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const limpiarFiltros = () => {
    setFiltros({
      tipo: '',
      leida: '',
      fechaDesde: '',
      fechaHasta: ''
    });
  };

  const formatearFecha = (fecha) => {
    return new Date(fecha).toLocaleString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

 const marcarComoLeida = async (notificacionId) => {
    try {
      const response = await axios.put('/proxy/api/notificaciones/marcar-leida', {
        notificacionId,
        usuarioId
      }, { headers });

      setNotificaciones(prev => 
        prev.map(n => 
          n.id === notificacionId 
            ? { ...n, leida: true, fechaLectura: new Date().toISOString() }
            : n
        )
      );

      // Actualizar también las notificaciones filtradas
      setNotificacionesFiltradas(prev => 
        prev.map(n => 
          n.id === notificacionId 
            ? { ...n, leida: true, fechaLectura: new Date().toISOString() }
            : n
        )
      );

    } catch (error) {
      console.error('❌ Error al marcar como leída:', error);
      console.error('Detalles del error:', error.response?.data);
      setError(`Error al marcar la notificación como leída: ${error.response?.data?.mensaje || error.message}`);
    }
  };

  const marcarTodasComoLeidas = async () => {
    try {
      
      const response = await axios.put(`/proxy/api/notificaciones/usuario/${usuarioId}/marcar-todas-leidas`, {}, { headers });

      setNotificaciones(prev => 
        prev.map(n => ({ 
          ...n, 
          leida: true, 
          fechaLectura: n.fechaLectura || new Date().toISOString() 
        }))
      );

      setNotificacionesFiltradas(prev => 
        prev.map(n => ({ 
          ...n, 
          leida: true, 
          fechaLectura: n.fechaLectura || new Date().toISOString() 
        }))
      );

    } catch (error) {
      console.error('❌ Error al marcar todas como leídas:', error);
      console.error('Detalles del error:', error.response?.data);
      setError(`Error al marcar todas las notificaciones como leídas: ${error.response?.data?.mensaje || error.message}`);
    }
  };

  const contarNoLeidas = async () => {
    try {
      
      const response = await axios.get(`/proxy/api/notificaciones/usuario/${usuarioId}/contar-no-leidas`, { headers });
      
      return response.data?.data || response.data?.count || 0;
    } catch (error) {
      console.error('❌ Error al contar no leídas desde servidor:', error);
      return contarNoLeidas();
    }
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  const handleReLogin = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('token');
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('token');
    
    window.location.href = '/login';
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '400px' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-danger m-4" role="alert">
        <i className="bi bi-exclamation-triangle me-2"></i>
        {error}
        <div className="mt-3">
          <button 
            className="btn btn-outline-danger btn-sm me-2"
            onClick={() => {
              setError(null);
              cargarDatos();
            }}
          >
            Reintentar
          </button>
          {(error.includes('Token') || error.includes('autenticación')) && (
            <button 
              className="btn btn-outline-primary btn-sm"
              onClick={handleReLogin}
            >
              Iniciar sesión
            </button>
          )}
        </div>
      </div>
    );
  }

   const renderTarjetasNotificaciones = () => (
    <div className="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
      {notificacionesFiltradas.map((notificacion) => (
        <div className="col" key={notificacion.id}>
          <div className={`card h-100 shadow-sm border-0 ${!notificacion.leida ? 'border-start border-primary border-4' : ''}`}>
            <div className="card-body">
              <div className="d-flex justify-content-between align-items-start mb-3">
                <div className="d-flex align-items-center">
                  <i
                    className={`${notificacion.tipoNotificacion.icono} me-2`}
                    style={{ 
                      fontSize: '1.5rem', 
                      color: notificacion.tipoNotificacion.color 
                    }}
                  ></i>
                  <div>
                    <h6 className="mb-0 fw-bold">{notificacion.titulo}</h6>
                    <small className="text-muted">{notificacion.tipoNotificacion.nombre}</small>
                  </div>
                </div>
                
                {!notificacion.leida && (
                  <button
                    onClick={() => marcarComoLeida(notificacion.id, currentUserId)}
                    className="btn btn-sm btn-outline-primary"
                    title="Marcar como leída"
                  >
                    <i className="bi bi-check"></i>
                  </button>
                )}
              </div>

              <div className="mb-3">
                <p className="text-muted mb-2" style={{ fontSize: '0.95rem' }}>
                  {notificacion.mensaje}
                </p>
              </div>

              <div className="mb-3">
                <div className="d-flex align-items-center mb-1">
                  <i className="bi bi-person-badge me-2" style={{ color: '#6f42c1' }}></i>
                  <small className="text-muted">Usuario:</small>
                </div>
                <div className="fw-semibold text-primary">
                  {`${notificacion.usuario.nombreUsuario} - ${notificacion.usuario.nombres}`}
                </div>
              </div>

              <div className="border-top pt-3">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <div className="d-flex align-items-center mb-1">
                      <i className="bi bi-calendar-event me-1" style={{ color: '#28a745' }}></i>
                      <small className="text-muted">
                        {formatearFecha(notificacion.fechaCreacion)}
                      </small>
                    </div>
                    {notificacion.leida && notificacion.fechaLectura && (
                      <div className="d-flex align-items-center">
                        <i className="bi bi-eye me-1" style={{ color: '#6c757d' }}></i>
                        <small className="text-muted">
                          Leída: {formatearFecha(notificacion.fechaLectura)}
                        </small>
                      </div>
                    )}
                  </div>
                  
                  <div>
                    {notificacion.leida ? (
                      <span className="badge bg-success bg-opacity-10 text-success">
                        <i className="bi bi-check-circle me-1"></i>
                        Leída
                      </span>
                    ) : (
                      <span className="badge bg-warning bg-opacity-10 text-warning">
                        <i className="bi bi-circle me-1"></i>
                        No leída
                      </span>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );

  const renderTablaNotificaciones = () => (
    <div className="table-responsive">
      <table className="table table-hover text-center align-middle">
        <thead className="table-light">
          <tr>
            <th className="text-center">Tipo</th>
            <th className="text-center">Título</th>
            <th className="text-center">Mensaje</th>
            <th className="text-center">Usuario</th>
            <th className="text-center">Estado</th>
            <th className="text-center">Fecha</th>
            <th className="text-center">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {notificacionesFiltradas.map((notificacion) => (
            <tr key={notificacion.id} className={!notificacion.leida ? 'table-primary' : ''}>
              <td>
                <div className="d-flex justify-content-center align-items-center">
                  <i
                    className={`${notificacion.tipoNotificacion.icono} me-2`}
                    style={{ fontSize: '1.2rem', color: notificacion.tipoNotificacion.color }}
                  ></i>
                  <span>{notificacion.tipoNotificacion.nombre}</span>
                </div>
              </td>
              <td className="fw-semibold">{notificacion.titulo}</td>
              <td>
                <small className="text-muted">{notificacion.mensaje.substring(0, 50)}...</small>
              </td>
              <td>
                <div className="d-flex justify-content-center align-items-center">
                  <i className="bi bi-person-badge me-2" style={{ color: '#6f42c1' }}></i>
                  <small>{notificacion.usuario.nombreUsuario}</small>
                </div>
              </td>
              <td>
                {notificacion.leida ? (
                  <span className="badge bg-success bg-opacity-10 text-success">
                    <i className="bi bi-check-circle me-1"></i>
                    Leída
                  </span>
                ) : (
                  <span className="badge bg-warning bg-opacity-10 text-warning">
                    <i className="bi bi-circle me-1"></i>
                    No leída
                  </span>
                )}
              </td>
              <td>
                <small className="text-muted">
                  {formatearFecha(notificacion.fechaCreacion)}
                </small>
              </td>
              <td>
                <div className="btn-group btn-group-sm justify-content-center">
                  <button
                    onClick={() => marcarComoLeida(notificacion.id, currentUserId)}
                    className="btn btn-outline-primary"
                    disabled={notificacion.leida}
                    title={notificacion.leida ? 'Ya está leída' : 'Marcar como leída'}
                  >
                    <i className="bi bi-check"></i>
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );


  return (
        <>
    <div className="container py-4" style={{ minHeight: 'calc(100vh - 120px)' }}>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
          <i className="bi bi-bell me-2" style={{ color: '#cca9bd' }}></i>
          Notificaciones
          {contarNoLeidas() > 0 && (
            <span className="badge bg-danger ms-2" style={{ fontSize: '14px' }}>
              {contarNoLeidas()}
            </span>
          )}
          {empresaActual && (
            <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
              <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
              {empresaActual.nombre}
            </span>
          )}
        </h3>
        <div className="text-muted">
          <i className="bi bi-info-circle me-1"></i>
          Mostrando {notificacionesFiltradas.length} de {totalElements} notificaciones
        </div>
      </div>

      {contarNoLeidas() > 0 && (
        <div className="mb-4">
          <button
            onClick={marcarTodasComoLeidas}
            className="btn btn-outline-primary"
          >
            <i className="bi bi-check-all me-2"></i>
            Marcar todas como leídas ({contarNoLeidas()})
          </button>
        </div>
      )}

      <div className="card shadow-sm mb-4">
        <div className="card-header bg-light">
          <div className="d-flex justify-content-between align-items-center">
            <h5 className="mb-0">
              <i className="bi bi-funnel me-2" style={{ color: '#6c757d' }}></i>
              Filtros de búsqueda
            </h5>
            <div className="btn-group" role="group">
              <input
                type="radio"
                className="btn-check"
                name="vista"
                id="vista-lista"
                checked={vista === 'lista'}
                onChange={() => setVista('lista')}
              />
              <label className="btn btn-outline-secondary" htmlFor="vista-lista">
                <i className="bi bi-list-ul"></i> Lista
              </label>
              
              <input
                type="radio"
                className="btn-check"
                name="vista"
                id="vista-tarjetas"
                checked={vista === 'tarjetas'}
                onChange={() => setVista('tarjetas')}
              />
              <label className="btn btn-outline-secondary" htmlFor="vista-tarjetas">
                <i className="bi bi-grid"></i> Tarjetas
              </label>
            </div>
          </div>
        </div>
        <div className="card-body">
          <div className="row g-3 mb-3">
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-calendar-check me-1" style={{ color: '#28a745' }}></i>
                Desde
              </label>
              <input
                type="date"
                name="fechaDesde"
                value={filtros.fechaDesde}
                onChange={(e) => manejarFechaPersonalizada('fechaDesde', e.target.value)}
                className={`form-control ${filtros.fechaDesde && filtros.fechaHasta && 
                  new Date(filtros.fechaDesde) > new Date(filtros.fechaHasta) ? 'is-invalid' : ''}`}
                max={filtros.fechaHasta || new Date().toISOString().split('T')[0]}
              />
            </div>
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-calendar-check me-1" style={{ color: '#ffa420' }}></i>
                Hasta
              </label>
              <input
                type="date"
                name="fechaHasta"
                value={filtros.fechaHasta}
                onChange={(e) => manejarFechaPersonalizada('fechaHasta', e.target.value)}
                className={`form-control ${filtros.fechaDesde && filtros.fechaHasta && 
                  new Date(filtros.fechaDesde) > new Date(filtros.fechaHasta) ? 'is-invalid' : ''}`}
                min={filtros.fechaDesde}
                max={new Date().toISOString().split('T')[0]}
              />
            </div>
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-tag me-1" style={{ color: '#007bff' }}></i>
                Tipo de notificación
              </label>
              <select
                name="tipo"
                value={filtros.tipo}
                onChange={handleFiltroChange}
                className="form-select"
              >
                <option value="" disabled hidden>Todos los tipos</option>
                {tiposNotificacion.map(tipo => (
                  <option key={tipo.id} value={tipo.nombre}>
                    {tipo.nombre}
                  </option>
                ))}
              </select>
            </div>
          </div>
          
          <div className="row g-3">
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-eye me-1" style={{ color: '#28a745' }}></i>
                Estado
              </label>
              <select
                name="leida"
                value={filtros.leida}
                onChange={handleFiltroChange}
                className="form-select"
              >
                <option value="" disabled hidden>Todas</option>
                <option value="false">No leídas</option>
                <option value="true">Leídas</option>
              </select>
            </div>
            <div className="col-md-4">
            </div>
            <div className="col-md-4 d-flex flex-column justify-content-end align-items-end">
              {(filtros.tipo || filtros.usuario || filtros.leida || filtros.fechaDesde || filtros.fechaHasta) && (
                <button 
                  onClick={limpiarFiltros} 
                  className="btn btn-outline-secondary"
                >
                  <i className="bi bi-x-circle me-1"></i>
                  Limpiar filtros
                </button>
              )}
            </div>
          </div>
        </div>
      </div>

      {filtros.fechaDesde && filtros.fechaHasta && 
        new Date(filtros.fechaDesde) > new Date(filtros.fechaHasta) && (
        <div className="row">
          <div className="col-12">
            <div className="text-danger small mt-2">
              <i className="bi bi-exclamation-triangle me-1"></i>
              La fecha de inicio debe ser anterior a la fecha final
            </div>
          </div>
        </div>
      )}

      {notificacionesFiltradas.length === 0 ? (
        <div className="text-center py-5">
          <i className="bi bi-bell-slash" style={{ fontSize: '4rem', color: '#dee2e6' }}></i>
          <h4 className="text-muted mt-3">
            {notificaciones.length === 0 ? 'No hay notificaciones' : 'No se encontraron resultados'}
          </h4>
          <p className="text-muted">
            {notificaciones.length === 0 
              ? 'Aún no tienes notificaciones.' 
              : 'Intenta ajustar los filtros de búsqueda.'
            }
          </p>
        </div>
      ) : (
        <>
          {vista === 'tarjetas' ? renderTarjetasNotificaciones() : renderTablaNotificaciones()}

          {totalPages > 1 && (
            <div className="d-flex justify-content-center mt-4">
              <nav aria-label="Paginación de notificaciones">
                <ul className="pagination">
                  <li className={`page-item ${currentPage === 0 ? 'disabled' : ''}`}>
                    <button
                      className="page-link"
                      onClick={() => handlePageChange(currentPage - 1)}
                      disabled={currentPage === 0}
                    >
                      <i className="bi bi-chevron-left"></i>
                    </button>
                  </li>
                  
                  {Array.from({ length: totalPages }, (_, i) => (
                    <li key={i} className={`page-item ${currentPage === i ? 'active' : ''}`}>
                      <button
                        className="page-link"
                        onClick={() => handlePageChange(i)}
                      >
                        {i + 1}
                      </button>
                    </li>
                  ))}
                  
                  <li className={`page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`}>
                    <button
                      className="page-link"
                      onClick={() => handlePageChange(currentPage + 1)}
                      disabled={currentPage === totalPages - 1}
                    >
                      <i className="bi bi-chevron-right"></i>
                    </button>
                  </li>
                </ul>
              </nav>
            </div>
          )}
        </>
      )}
    </div>

    <Footer />
    </>
  );
}