import { useEffect, useState } from 'react';
import axios from 'axios';
import Footer from '../../components/Footer';

export default function AdminHistorialPedidos() {
  const [historial, setHistorial] = useState([]);
  const [historialFiltrado, setHistorialFiltrado] = useState([]);
  const [loading, setLoading] = useState(true);
  const [empresaActualNombre, setEmpresaActualNombre] = useState('N/A');
  const [error, setError] = useState('');
  const [filtros, setFiltros] = useState({
    pedidoId: '',
    tipoCambio: '',
    usuario: '',
    fechaDesde: '',
    fechaHasta: ''
  });
  const [vista, setVista] = useState('lista'); 

  const token = localStorage.getItem('token');
  const additionalData = localStorage.getItem('x-additional-data');
  const headers = {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
    "x-additional-data": additionalData,
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
  const mensajeriaId = decoded?.mensajeria_id

  useEffect(() => {
    const fetchHistorialCompleto = async () => {
      setLoading(true);
      setError('');
      
      try {
        const historialRes = await axios.get('/proxy/api/historial-pedidos', { headers });

        const historialData = historialRes.data.data || historialRes.data;
        const primeraFila = historialData[0];
        
        if (primeraFila && primeraFila.tipoCambioNombre && primeraFila.usuarioNombre) {
          
          try {
            const usuariosRes = await axios.get('/proxy/api/usuarios', { headers });
            const usuariosData = usuariosRes.data.data || usuariosRes.data;
            
            const usuariosMap = new Map(
              Array.isArray(usuariosData) ? usuariosData.map(usuario => [usuario.id, usuario]) : []
            );
            
            const historialProcesado = historialData.map(item => {
              const usuario = usuariosMap.get(item.usuarioId);
              
              return {
                ...item,
                tipoCambio: item.tipoCambioNombre || 'Tipo no encontrado',
                usuario: {
                  nombreUsuario: usuario?.nombreUsuario || 'Usuario no encontrado',
                  nombres: usuario?.nombres || '',
                  apellidos: usuario?.apellidos || ''
                },
                pedido: {
                  id: item.pedidoId
                },
                fechaCambio: item.fecha || item.fechaCambio,
                valorAnterior: item.valorAnterior,
                valorNuevo: item.valorNuevo
              };
            });
            
            setHistorial(historialProcesado);
            setHistorialFiltrado(historialProcesado);
          } catch (err) {
            console.warn('No se pudo cargar usuarios, usando datos disponibles:', err.message);
            const historialProcesado = historialData.map(item => ({
              ...item,
              tipoCambio: item.tipoCambioNombre || 'Tipo no encontrado',
              usuario: {
                nombreUsuario: 'N/A', 
                nombres: item.usuarioNombre || '',
                apellidos: ''
              },
              pedido: {
                id: item.pedidoId
              },
              fechaCambio: item.fecha || item.fechaCambio,
              valorAnterior: item.valorAnterior,
              valorNuevo: item.valorNuevo
            }));
            
            setHistorial(historialProcesado);
            setHistorialFiltrado(historialProcesado);
          }
        } else {
          
          const llamadasAdicionales = [];
          
          const cargarTiposCambio = axios.get('/proxy/api/tipos-cambio-pedido', { headers })
            .catch(err => {
              console.warn('No se pudo cargar tipos de cambio:', err.message);
              return { data: [] };
            });
          
          const cargarUsuarios = axios.get('/proxy/api/usuarios', { headers })
            .catch(err => {
              console.warn('No se pudo cargar usuarios:', err.message);
              return { data: [] };
            });
          
          const cargarPedidos = axios.get('/proxy/api/pedidos', { headers })
            .catch(err => {
              console.warn('No se pudo cargar pedidos:', err.message);
              return { data: [] };
            });

          const [tiposCambioRes, usuariosRes, pedidosRes] = await Promise.all([
            cargarTiposCambio,
            cargarUsuarios,
            cargarPedidos
          ]);

          const tiposCambioData = tiposCambioRes.data.data || tiposCambioRes.data;
          const usuariosData = usuariosRes.data.data || usuariosRes.data;
          const pedidosData = pedidosRes.data.data || pedidosRes.data;

          const tiposCambioMap = new Map(
            Array.isArray(tiposCambioData) ? tiposCambioData.map(tipo => [tipo.id, tipo]) : []
          );
          
          const usuariosMap = new Map(
            Array.isArray(usuariosData) ? usuariosData.map(usuario => [usuario.id, usuario]) : []
          );
          
          const pedidosMap = new Map(
            Array.isArray(pedidosData) ? pedidosData.map(pedido => [pedido.id, pedido]) : []
          );

          const historialEnriquecido = historialData.map(item => {
            const tipoCambio = tiposCambioMap.get(item.tipoCambioId);
            const usuario = usuariosMap.get(item.usuarioId);
            const pedido = pedidosMap.get(item.pedidoId);

            const obtenerNombreUsuario = () => {
              if (usuario) {
                return usuario.nombreUsuario || usuario.nombre_usuario || usuario.username || 'Usuario no encontrado';
              }
              return item.usuarioNombre || item.usuarioNombreUsuario || item.usuario_nombre || 'Usuario no encontrado';
            };

            const obtenerNombres = () => {
              if (usuario) {
                return usuario.nombres || usuario.nombre || '';
              }
              return item.usuarioNombres || item.nombres || '';
            };

            const obtenerApellidos = () => {
              if (usuario) {
                return usuario.apellidos || usuario.apellido || '';
              }
              return item.usuarioApellidos || item.apellidos || '';
            };

            return {
              ...item,
              tipoCambio: tipoCambio?.nombre || item.tipoCambioNombre || 'Tipo no encontrado',
              usuario: {
                nombreUsuario: obtenerNombreUsuario(),
                nombres: obtenerNombres(),
                apellidos: obtenerApellidos()
              },
              pedido: {
                id: pedido?.id || item.pedidoId
              },
              fechaCambio: item.fecha || item.fechaCambio,
              valorAnterior: item.valorAnterior,
              valorNuevo: item.valorNuevo
            };
          });

          setHistorial(historialEnriquecido);
          setHistorialFiltrado(historialEnriquecido);
        }

      } catch (err) {
        console.error('Error al obtener datos:', err);
        setError('Error al conectar con el servidor: ' + (err.response?.data?.message || err.message));
      } finally {
        setLoading(false);
      }
      if (mensajeriaId) {
        try {
          const empresaRes = await axios.get(`/proxy/api/empresas-mensajeria/${mensajeriaId}`, { headers });
          const empresaNombre = empresaRes.data.data?.nombre || empresaRes.data.nombre;
          setEmpresaActualNombre(empresaNombre || 'Desconocida');
        } catch (e) {
          console.warn('No se pudo cargar la empresa actual:', e.message);
        }
      }
    };

    fetchHistorialCompleto();
  }, []);

  useEffect(() => {
    aplicarFiltros();
  }, [historial, filtros]);

  const aplicarFiltros = () => {
    let filtrado = [...historial];

    if (filtros.pedidoId) {
      filtrado = filtrado.filter(h => 
        h.pedido?.id?.toString().includes(filtros.pedidoId)
      );
    }

    if (filtros.tipoCambio) {
      filtrado = filtrado.filter(h => 
        h.tipoCambio?.toLowerCase().includes(filtros.tipoCambio.toLowerCase())
      );
    }

    if (filtros.usuario) {
      const busqueda = filtros.usuario.toLowerCase();
      filtrado = filtrado.filter(h => {
        const nombreUsuario = h.usuario?.nombreUsuario?.toLowerCase() || '';
        const nombres = h.usuario?.nombres?.toLowerCase() || '';
        const apellidos = h.usuario?.apellidos?.toLowerCase() || '';
        const nombreCompleto = `${nombres} ${apellidos}`.toLowerCase();
        
        return nombreUsuario.includes(busqueda) || 
              nombres.includes(busqueda) || 
              apellidos.includes(busqueda) ||
              nombreCompleto.includes(busqueda);
      });
    }

    if (filtros.fechaDesde) {
      const fechaDesde = new Date(filtros.fechaDesde);
      filtrado = filtrado.filter(h => 
        new Date(h.fechaCambio) >= fechaDesde
      );
    }

    if (filtros.fechaHasta) {
      const fechaHasta = new Date(filtros.fechaHasta);
      fechaHasta.setHours(23, 59, 59, 999);
      filtrado = filtrado.filter(h => 
        new Date(h.fechaCambio) <= fechaHasta
      );
    }

    setHistorialFiltrado(filtrado);
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
      pedidoId: '',
      tipoCambio: '',
      usuario: '',
      fechaDesde: '',
      fechaHasta: ''
    });
  };

  const getTipoCambioIcon = (tipoCambio) => {
    const iconMap = {
      'Estado': 'bi-flag',
      'Asignación': 'bi-person-check',
      'Mensajero': 'bi-truck',
      'Dirección': 'bi-geo-alt',
      'Producto': 'bi-box',
      'Cantidad': 'bi-calculator',
      'Precio': 'bi-currency-dollar',
      'Fecha': 'bi-calendar',
      'Cliente': 'bi-person',
      'Observaciones': 'bi-chat-text',
      'Teléfono': 'bi-telephone',
      'Email': 'bi-envelope'
    };
    
    return { 
      icon: iconMap[tipoCambio] || 'bi-arrow-repeat', 
      color: '#cca9bd' 
    };
  };

  const formatearFecha = (fecha) => {
    if (!fecha) return 'Fecha no disponible';
    
    try {
      const date = new Date(fecha);
      return date.toLocaleString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (error) {
      console.error('Error al formatear fecha:', error);
      return 'Fecha inválida';
    }
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

  const renderTarjetasHistorial = () => (
    <div className="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
      {historialFiltrado.map((h) => {
        const tipoInfo = getTipoCambioIcon(h.tipoCambio);
        return (
          <div className="col" key={h.id}>
            <div className="card h-100 shadow-sm border-0">
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <div className="d-flex align-items-center">
                    <i
                      className={`${tipoInfo.icon} me-2`}
                      style={{ fontSize: '1.5rem', color: "#cca9bd" }}
                    ></i>
                    <div>
                      <h6 className="mb-0 fw-bold">{h.tipoCambio}</h6>
                      <small className="text-muted">Pedido #{h.pedido?.id ?? 'N/A'}</small>
                    </div>
                  </div>

                  <div className="text-muted small d-flex flex-column align-items-end">
                    <div className="d-flex align-items-center mb-1" style={{ gap: '0.25rem' }}>
                      <i className="bi bi-calendar-event" style={{ color: '#28a745' }}></i>
                      <span>{formatearFecha(h.fechaCambio).split(' ')[0].replace(',', '')}</span>
                    </div>
                    <div className="d-flex align-items-center" style={{ gap: '0.25rem' }}>
                      <i className="bi bi-clock" style={{ color: '#6c757d' }}></i>
                      <span>{formatearFecha(h.fechaCambio).split(' ')[1]}</span>
                    </div>
                  </div>
                </div>

                <div className="mb-3">
                  <div className="d-flex align-items-center mb-2">
                    <i className="bi bi-person-badge me-2" style={{ color: '#6f42c1' }}></i>
                    <small className="text-muted">Cambio realizado por:</small>
                  </div>
                    <div className="fw-semibold text-primary">
                      {`${h.usuario?.nombreUsuario || 'N/A'} - ${h.usuario?.nombres || ''} ${h.usuario?.apellidos || ''}`}
                    </div>
                </div>

                <div className="border-top pt-3">
                  <div className="d-flex align-items-center mb-2">
                    <i className="bi bi-arrow-left-right me-2" style={{ color: '#6c757d' }}></i>
                    <small className="text-muted">Cambio realizado:</small>
                  </div>
                  <div className="change-display">
                    {h.valorAnterior != null && h.valorAnterior !== '' ? (
                      <div className="d-flex flex-column gap-1">
                        <div className="d-flex align-items-center">
                          <span className="badge bg-danger bg-opacity-10 text-danger me-2">
                            <i className="bi bi-dash-circle me-1"></i>
                            Anterior
                          </span>
                          <span className="text-decoration-line-through text-muted">
                            {h.valorAnterior}
                          </span>
                        </div>
                        <div className="d-flex align-items-center">
                          <span className="badge bg-success bg-opacity-10 text-success me-2">
                            <i className="bi bi-check-circle me-1"></i>
                            Nuevo
                          </span>
                          <span className="fw-bold text-success">
                            {h.valorNuevo}
                          </span>
                        </div>
                      </div>
                    ) : (
                      <div className="d-flex align-items-center">
                        <span className="badge bg-primary bg-opacity-10 text-primary me-2">
                          <i className="bi bi-plus-circle me-1"></i>
                          Valor
                        </span>
                        <span className="fw-bold text-primary">
                          {h.valorNuevo ?? '-'}
                        </span>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );

  const renderListaHistorial = () => (
    <div className="table-responsive">
      <table className="table table-hover">
        <thead className="table-light">
          <tr>
            <th>ID Pedido</th>
            <th>Tipo de cambio</th>
            <th>Usuario</th>
            <th>Fecha</th>
            <th>Cambio</th>
          </tr>
        </thead>
        <tbody>
          {historialFiltrado.map((h) => {
            const tipoInfo = getTipoCambioIcon(h.tipoCambio);
            return (
              <tr key={h.id}>
                <td>#{h.pedido?.id ?? 'N/A'}</td>
                <td>
                  <div className="d-flex align-items-center">
                    <i className={`${tipoInfo.icon} me-2`} style={{ color: tipoInfo.color }}></i>
                    {h.tipoCambio}
                  </div>
                </td>
                <td>
                  <div className="fw-semibold text-primary">
                      {`${h.usuario?.nombreUsuario || 'N/A'} - ${h.usuario?.nombres || ''} ${h.usuario?.apellidos || ''}`}
                  </div>
                </td>
                <td>
                  <div className="text-muted small">
                    <div>{formatearFecha(h.fechaCambio).split(' ')[0].replace(',', '')}</div>
                    <div>{formatearFecha(h.fechaCambio).split(' ')[1]}</div>
                  </div>
                </td>
                <td>
                  <div className="change-display">
                    {h.valorAnterior != null && h.valorAnterior !== '' ? (
                      <div className="d-flex flex-column gap-1">
                        <div className="d-flex align-items-center">
                          <span className="badge bg-danger bg-opacity-10 text-danger me-2">
                            <i className="bi bi-dash-circle me-1"></i>
                            Anterior
                          </span>
                          <span className="text-decoration-line-through text-muted">
                            {h.valorAnterior}
                          </span>
                        </div>
                        <div className="d-flex align-items-center">
                          <span className="badge bg-success bg-opacity-10 text-success me-2">
                            <i className="bi bi-check-circle me-1"></i>
                            Nuevo
                          </span>
                          <span className="fw-bold text-success">
                            {h.valorNuevo}
                          </span>
                        </div>
                      </div>
                    ) : (
                      <div className="d-flex align-items-center">
                        <span className="badge bg-primary bg-opacity-10 text-primary me-2">
                          <i className="bi bi-plus-circle me-1"></i>
                          Valor
                        </span>
                        <span className="fw-bold text-primary">
                          {h.valorNuevo ?? '-'}
                        </span>
                      </div>
                    )}
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );

  if (loading) {
    return (
      <>
        <div className="container py-4" style={{ minHeight: 'calc(100vh - 120px)' }}>
          <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
            <i className="bi bi-clock-history me-2" style={{ color: '#cca9bd' }}></i>
            Historial de pedidos
            {mensajeriaId && (
              <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
                <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
                {empresaActualNombre}
              </span>
            )}
          </h3>
          <div className="text-center mt-5">
            <div className="spinner-border text-primary" role="status" />
            <p className="mt-3">Cargando historial...</p>
          </div>
        </div>
        <Footer />
      </>
    );
  }

  if (error) {
    return (
      <>
        <div className="container py-4">
          <div className="alert alert-danger">
            <i className="bi bi-exclamation-triangle me-2"></i>
            {error}
          </div>
        </div>
        <Footer />
      </>
    );
  }

  return (
    <>
      <div className="container py-4" style={{ minHeight: 'calc(100vh - 120px)' }}>
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
            <i className="bi bi-clock-history me-2" style={{ color: '#cca9bd' }}></i>
            Historial de pedidos
            {mensajeriaId && (
              <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
                <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
                {empresaActualNombre}
              </span>
            )}
          </h3>
          <div className="text-muted">
            <i className="bi bi-info-circle me-1"></i>
            Mostrando {historialFiltrado.length} de {historial.length} registros
          </div>
        </div>

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
                  <i className="bi bi-hash me-1" style={{ color: '#007bff' }}></i>
                  ID pedido
                </label>
                <input
                  type="text"
                  name="pedidoId"
                  value={filtros.pedidoId}
                  onChange={handleFiltroChange}
                  className="form-control"
                  placeholder="Buscar por ID"
                />
              </div>
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-tag me-1" style={{ color: '#4b3621' }}></i>
                  Tipo de cambio
                </label>
                <input
                  type="text"
                  name="tipoCambio"
                  value={filtros.tipoCambio}
                  onChange={handleFiltroChange}
                  className="form-control"
                  placeholder="Estado, Asignación, Mensajero, etc."
                />
              </div>
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-person me-1" style={{ color: '#6f42c1' }}></i>
                  Usuario
                </label>
                <input
                  type="text"
                  name="usuario"
                  value={filtros.usuario}
                  onChange={handleFiltroChange}
                  className="form-control"
                  placeholder="Buscar por usuario, nombre o apellido"
                />
              </div>
            </div>
            
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
              <div className="col-md-4 d-flex flex-column justify-content-end align-items-end">
                {(filtros.pedidoId || filtros.tipoCambio || filtros.usuario || filtros.fechaDesde || filtros.fechaHasta) && (
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

        {historialFiltrado.length === 0 ? (
          <div className="text-center py-5">
            <i className="bi bi-inbox" style={{ fontSize: '4rem', color: '#dee2e6' }}></i>
            <h4 className="text-muted mt-3">
              {historial.length === 0 ? 'No hay cambios registrados' : 'No se encontraron resultados'}
            </h4>
            <p className="text-muted">
              {historial.length === 0 
                ? 'Aún no se han registrado cambios en los pedidos.' 
                : 'Intenta ajustar los filtros de búsqueda.'
              }
            </p>
          </div>
        ) : vista === 'lista' ? (
          renderListaHistorial()
        ) : (
          renderTarjetasHistorial()
        )}
      </div>
      <Footer />
    </>
  );
}