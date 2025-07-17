import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import Footer from '../../components/Footer';

export default function OperadorMensajeros() {
  const [mensajeros, setMensajeros] = useState([]);
  const [tiposVehiculo, setTiposVehiculo] = useState([]);
  const [estadosGeneral, setEstadosGeneral] = useState([]);
  const [editingMensajero, setEditingMensajero] = useState(null);
  const [formVisible, setFormVisible] = useState(false);
  const [errores, setErrores] = useState({});
  const [mensaje, setMensaje] = useState(null);
  const [empresaActual, setEmpresaActual] = useState(null);
  const [loading, setLoading] = useState(false);
  const formRef = useRef(null);
  const [error, setError] = useState(null);
  const [filtroNombre, setFiltroNombre] = useState('');
  const [filtroTipoVehiculo, setFiltroTipoVehiculo] = useState('');
  const [filtroDisponibilidad, setFiltroDisponibilidad] = useState('');


  const [paginacion, setPaginacion] = useState({
    pagina: 0,
    tamano: 20,
    totalElementos: 0,
    totalPaginas: 0
  });
  const [vista, setVista] = useState('lista');
  const [estadisticas, setEstadisticas] = useState({
    totalMensajeros: 0,
    disponibles: 0,
    ocupados: 0,
    inactivos: 0
  });


  const getAuthHeaders = () => {
    const token = localStorage.getItem('token');
    const additionalData = localStorage.getItem('x-additional-data');
    if (!token) {
      console.error('❌ Token no disponible');
      throw new Error('Token no disponible');
    }
    
    return {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
      'x-additional-data': additionalData

    };
  };

  const getTipoVehiculoDescripcion = (tipoVehiculoId) => {
    const tiposVehiculoMap = {
      1: 'Motocicleta',
      2: 'Bicicleta', 
      3: 'Automóvil',
      4: 'A pie'
    };
    return tiposVehiculoMap[tipoVehiculoId] || 'Desconocido';
  };

  const getEstadoGeneralDescripcion = (estadoId) => {
    const estadosMap = {
      1: 'Activo',
      2: 'Inactivo',
      3: 'Suspendido'
    };
    return estadosMap[estadoId] || 'Desconocido';
  };

const obtenerEmpresaActual = async () => {
  try {
    const headers = getAuthHeaders();
    const token = headers.Authorization.split(' ')[1];
    const decoded = JSON.parse(atob(token.split('.')[1]));
    const empresaIdUsuario = decoded.mensajeria_id;
    const response = await axios.get('/proxy/api/empresas-mensajeria', { headers });
    const empresas = response.data?.data || [];

    const empresaUsuario = empresas.find(e => e.id === empresaIdUsuario);
    setEmpresaActual(empresaUsuario || null);
  } catch (error) {
    console.error('❌ Error al obtener empresa actual:', error);
  }
};

  useEffect(() => {
    cargarDatosIniciales();
  }, []);

  useEffect(() => {
    cargarMensajeros();
  }, [paginacion.pagina, paginacion.tamano]);

  const cargarDatosIniciales = async () => {
  try {
    setLoading(true);
    const headers = getAuthHeaders();

    const [tiposVehiculoRes, estadosRes] = await Promise.all([
      axios.get('/proxy/api/tipos-vehiculo', { headers }),
      axios.get('/proxy/api/estados-general', { headers }) 
    ]);

    setTiposVehiculo(tiposVehiculoRes.data?.data || []);
    setEstadosGeneral(estadosRes.data?.data || []); 

    await obtenerEmpresaActual();
    await cargarEstadisticas();
  } catch (error) {
    console.error('❌ Error al cargar datos iniciales:', error);
    setError(`Error al cargar los datos iniciales: ${error.response?.data?.message || error.message}`);
  } finally {
    setLoading(false);
  }
};

  const cargarEstadisticas = async () => {
    try {
      const headers = getAuthHeaders();
      const response = await axios.get('/proxy/api/mensajeros', { 
        headers,
        params: { size: 1000 }
      });
      
      const mensajeros = response.data?.data?.content || [];
      
      const totalMensajeros = mensajeros.length;
      const activos = mensajeros.filter(m => m.estadoId === 1).length;
      const disponibles = mensajeros.filter(m => {
        const isDisponible = m.disponibilidad === true || m.disponibilidad === 1 || m.disponibilidad === '1';
        const numPedidosActivos = parseInt(m.pedidosActivos) || 0;
        return m.estadoId === 1 && isDisponible && numPedidosActivos === 0;
      }).length;
      
      const ocupados = mensajeros.filter(m => {
        const isDisponible = m.disponibilidad === true || m.disponibilidad === 1 || m.disponibilidad === '1';
        const numPedidosActivos = parseInt(m.pedidosActivos) || 0;
        return m.estadoId === 1 && isDisponible && numPedidosActivos > 0;
      }).length;
      
      const noDisponibles = mensajeros.filter(m => {
        const isDisponible = m.disponibilidad === true || m.disponibilidad === 1 || m.disponibilidad === '1';
        return m.estadoId === 1 && !isDisponible;
      }).length;
      
      setEstadisticas({
        totalMensajeros,
        disponibles,
        ocupados,
        inactivos: noDisponibles
      });
    } catch (error) {
      console.error('❌ Error al cargar estadísticas:', error);
      setEstadisticas({
        totalMensajeros: 0,
        disponibles: 0,
        ocupados: 0,
        inactivos: 0
      });
    }
  };

const handleInputChange = (e) => {
  const { name, value, type, checked } = e.target;

  setEditingMensajero(prev => ({
    ...prev,
    [name]: type === 'checkbox' ? checked :
             name === 'tipoVehiculoId' || name === 'estadoId' ? Number(value) : value
  }));

  if (errores[name]) {
    setErrores(prev => ({
      ...prev,
      [name]: ''
    }));
  }
};

  const validarFormulario = (mensajero) => {
    const nuevosErrores = {};

    if (!mensajero.tipoVehiculoId) {
      nuevosErrores.tipoVehiculoId = 'El tipo de vehículo es requerido';
    }

    return nuevosErrores;
  };

  const token = localStorage.getItem('token');

const decodeToken = (token) => {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (e) {
    console.error('Token inválido', e);
    return {};
  }
};

const decoded = decodeToken(token);

  const handleGuardar = async (e) => {
  e.preventDefault();
  
  const nuevosErrores = validarFormulario(editingMensajero);
  if (Object.keys(nuevosErrores).length > 0) {
    setErrores(nuevosErrores);
    return;
  }

  setLoading(true);
  
  try {
    const headers = getAuthHeaders();
    
    const mensajeroData = {
      tipoVehiculoId: parseInt(editingMensajero.tipoVehiculoId),
      estadoId: parseInt(editingMensajero.estadoId),
      disponibilidad: editingMensajero.disponibilidad === true || editingMensajero.disponibilidad === 'true',
      tenantId: decoded?.tenant_id
    };

    let response;
    
    if (editingMensajero.id) {
      console.log('🟡 Payload que se envía:', mensajeroData);

      response = await axios.put(
        `/proxy/api/mensajeros/${editingMensajero.id}`,
        mensajeroData,
        { headers }
      );
      
      setMensaje({
        tipo: 'success',
        texto: 'Mensajero actualizado exitosamente'
      });
    }

    await cargarMensajeros();
    setEditingMensajero(null);
    setFormVisible(false);
    setErrores({});
    window.scrollTo({ top: 0, behavior: 'smooth' });

  } catch (error) {
    console.error('❌ Error al guardar mensajero:', error);
    
    if (error.response?.status === 400) {
      const errorData = error.response.data;
      if (errorData.validationErrors) {
        setErrores(errorData.validationErrors);
      } else {
        setMensaje({
          tipo: 'error',
          texto: errorData.message || 'Error de validación en los datos enviados'
        });
      }
    } else {
      setMensaje({
        tipo: 'error',
        texto: `Error al actualizar el mensajero: ${error.response?.data?.message || error.message}`
      });
    }
  } finally {
    setLoading(false);
  }
};


  const handleEditar = (mensajero) => {
    setEditingMensajero({
      id: mensajero.id,
      nombres: mensajero.nombres || '',
      apellidos: mensajero.apellidos || '',
      email: mensajero.email || '',
      disponibilidad: mensajero.disponibilidad === true || mensajero.disponibilidad === 1 || mensajero.disponibilidad === '1',
      maxPedidosSimultaneos: mensajero.maxPedidosSimultaneos || '',
      tipoVehiculoId: mensajero.tipoVehiculoId || '', 
      estadoId: mensajero.estadoId || '',
      pedidosActivos: mensajero.pedidosActivos || 0,
      totalEntregas: mensajero.totalEntregas || 0,
      fechaUltimaEntrega: mensajero.fechaUltimaEntrega
    });
    
    setFormVisible(true);
    setErrores({});
    
    setTimeout(() => {
      formRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, 100);
  };

  const handleCancelar = () => {
    setFormVisible(false);
    setEditingMensajero(null);
    setErrores({});
  };

  const handleCambiarDisponibilidad = async (mensajeroId, disponibilidadActual) => {
    const mensajero = mensajeros.find(m => m.id === mensajeroId);

    if (!mensajero || mensajero.estadoId !== 1) {
      setMensaje({
        tipo: 'error',
        texto: 'Solo se puede cambiar la disponibilidad de mensajeros activos'
      });
      return;
    }

    setLoading(true);

    try {
      const headers = getAuthHeaders();

      const isDisponible = disponibilidadActual === 1 || disponibilidadActual === true || disponibilidadActual === '1';
      const nuevaDisponibilidad = isDisponible ? 0 : 1;

      const requestData = {
        mensajeroId: Number(mensajeroId),
        disponibilidad: nuevaDisponibilidad
      };
      

      await axios.put(
        '/proxy/api/mensajeros/disponibilidad',
        requestData,
        { headers }
      );

      
      setMensaje({
        tipo: 'success',
        texto: `Disponibilidad ${nuevaDisponibilidad === 1 ? 'activada' : 'desactivada'} exitosamente`
      });

      await cargarMensajeros();

    } catch (error) {
      const errorMessage =
        error.response?.data?.error ||
        error.response?.data?.message ||
        error.message ||
        'Error desconocido';

      setMensaje({
        tipo: 'error',
        texto: `Error al cambiar disponibilidad: ${errorMessage}`
      });
    } finally {
      setLoading(false);
    }
  };

  const cargarMensajeros = async () => {
  try {
    setLoading(true);
    const headers = getAuthHeaders();

    const params = {
      page: paginacion.pagina,
      size: paginacion.tamano
    };

    const response = await axios.get('/proxy/api/mensajeros', { headers, params });
    
    const data = response.data?.data;

    setMensajeros(data?.content || []);
    setPaginacion(prev => ({
      ...prev,
      totalElementos: data?.totalElements || 0,
      totalPaginas: data?.totalPages || 0
    }));

  } catch (error) {
    console.error('❌ Error al cargar mensajeros:', error);
    setError(`Error al cargar los mensajeros: ${error.response?.data?.message || error.message}`);
  } finally {
    setLoading(false);
  }
};

  const mensajerosFiltrados = mensajeros.filter(mensajero => {
    const coincideNombre = mensajero.nombres.toLowerCase().includes(filtroNombre.toLowerCase()) ||
                          mensajero.apellidos.toLowerCase().includes(filtroNombre.toLowerCase()) ||
                          mensajero.email.toLowerCase().includes(filtroNombre.toLowerCase());
    
    const coincideTipoVehiculo = filtroTipoVehiculo === '' || 
                                (mensajero.tipoVehiculoId && mensajero.tipoVehiculoId.toString() === filtroTipoVehiculo);
    
    const coincideDisponibilidad = filtroDisponibilidad === '' || 
                                  (filtroDisponibilidad === 'true' && (mensajero.disponibilidad === true || mensajero.disponibilidad === 1 || mensajero.disponibilidad === '1')) ||
                                  (filtroDisponibilidad === 'false' && !(mensajero.disponibilidad === true || mensajero.disponibilidad === 1 || mensajero.disponibilidad === '1'));
    
    return coincideNombre && coincideTipoVehiculo && coincideDisponibilidad;
  });

  const limpiarFiltros = () => {
    setFiltroNombre('');
    setFiltroTipoVehiculo('');
    setFiltroDisponibilidad('');
  };

  const actualizarDisponibilidad = async (mensajeroId, disponibilidad) => {
    try {
      const headers = getAuthHeaders();
      
      const isDisponible = disponibilidad === true || disponibilidad === 1 || disponibilidad === '1';
      
      const request = { mensajeroId, disponibilidad: isDisponible };

      await axios.put('/proxy/api/mensajeros/disponibilidad', request, { headers });

      await cargarMensajeros();
      await cargarEstadisticas();
    } catch (error) {
      setError('Error al actualizar la disponibilidad');
    }
  };

  const cambiarPagina = (nuevaPagina) => {
    setPaginacion(prev => ({
      ...prev,
      pagina: nuevaPagina
    }));
  };

  const formatearFecha = (fecha) => {
    if (!fecha) return 'N/A';
    return new Date(fecha).toLocaleString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getEstadoDisponibilidad = (disponibilidad, pedidosActivos) => {
    const disponible = disponibilidad === 1 || disponibilidad === true || disponibilidad === '1';
    const tienePedidos = parseInt(pedidosActivos) > 0;

    if (disponible) {
      if (tienePedidos) {
        return { texto: 'Ocupado', color: 'warning', icono: 'bi-clock' };
      } else {
        return { texto: 'Disponible', color: 'success', icono: 'bi-check-circle' };
      }
    }

    return { texto: 'No disponible', color: 'danger', icono: 'bi-x-circle' };
  };

  const renderTarjetasMensajeros = () => (
    <div className="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
      {mensajerosFiltrados.map((mensajero) => {
        const estadoDisp = getEstadoDisponibilidad(mensajero.disponibilidad, mensajero.pedidosActivos);
        
        return (
          <div className="col" key={mensajero.id}>
            <div className="card h-100 shadow-sm border-0">
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <div className="d-flex align-items-center">
                    <div className="avatar-circle me-3">
                      <i className="bi bi-person fs-5" style={{ color: '#6f42c1' }}></i>
                    </div>
                    <div>
                      <h6 className="mb-0 fw-bold">{mensajero.nombres} {mensajero.apellidos}</h6>
                      <small className="text-muted">ID: {mensajero.id}</small>
                    </div>
                  </div>
                  <span className={`badge bg-${estadoDisp.color} bg-opacity-10 text-${estadoDisp.color}`}>
                    <i className={`bi ${estadoDisp.icono} me-1`}></i>
                    {estadoDisp.texto}
                  </span>
                </div>

                <div className="mb-3">
                  <div className="d-flex align-items-center mb-2">
                    <i className="bi bi-envelope me-2" style={{ color: '#007bff' }}></i>
                    <small className="text-muted">{mensajero.email}</small>
                  </div>
                  <div className="d-flex align-items-center mb-2">
                    <i className="bi bi-truck me-2" style={{ color: '#28a745' }}></i>
                    <small className="text-muted">{getTipoVehiculoDescripcion(mensajero.tipoVehiculoId)}</small>
                  </div>
                  <div className="d-flex align-items-center">
                    <i className="bi bi-award me-2" style={{ color: '#ffc107' }}></i>
                    <small className="text-muted">{mensajero.totalEntregas} entregas</small>
                  </div>
                </div>

                <div className="row g-2 mb-3">
                  <div className="col-6">
                    <div className="text-center p-2 bg-light rounded">
                      <div className="fw-bold text-primary">{mensajero.pedidosActivos}</div>
                      <small className="text-muted">Activos</small>
                    </div>
                  </div>
                  <div className="col-6">
                    <div className="text-center p-2 bg-light rounded">
                      <div className="fw-bold text-success">{mensajero.maxPedidosSimultaneos}</div>
                      <small className="text-muted">Máx. simult.</small>
                    </div>
                  </div>
                </div>

                <div className="border-top pt-3">
                  <div className="d-flex justify-content-between align-items-center">
                    <small className="text-muted">
                      <i className="bi bi-clock me-1"></i>
                      Última entrega: {formatearFecha(mensajero.fechaUltimaEntrega)}
                    </small>
  
                    <div className="btn-group btn-group-sm">
                      <button 
                        className="btn btn-outline-primary"
                        onClick={() => handleEditar(mensajero)}
                      >
                        <i className="bi bi-pencil"></i>
                      </button>
                      <button 
                        className="btn btn-outline-secondary"
                        onClick={() => {
                          const isDisponible = mensajero.disponibilidad === true || mensajero.disponibilidad === 1 || mensajero.disponibilidad === '1';
                          actualizarDisponibilidad(mensajero.id, !isDisponible);
                        }}
                      >
                        <i className={`bi ${
                          (mensajero.disponibilidad === true || mensajero.disponibilidad === 1 || mensajero.disponibilidad === '1') 
                            ? 'bi-pause' 
                            : 'bi-play'
                        }`}></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );

  const renderTablaMensajeros = () => (
    <div className="table-responsive">
      <table className="table table-hover text-center align-middle">
        <thead className="table-light">
          <tr>
            <th className="text-center">Mensajero</th>
            <th className="text-center">Email</th>
            <th className="text-center">Vehículo</th>
            <th className="text-center">Estado</th>
            <th className="text-center">Disponibilidad</th>
            <th className="text-center">Pedidos</th>
            <th className="text-center">Entregas</th>
            <th className="text-center">Última actividad</th>
            <th className="text-center">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {mensajerosFiltrados.map((mensajero) => {
            const estadoDisp = getEstadoDisponibilidad(mensajero.disponibilidad, mensajero.pedidosActivos);

            return (
              <tr key={mensajero.id}>
                <td>
                  <div className="d-flex justify-content-center align-items-center">
                    <div className="avatar-circle me-3">
                      <i className="bi bi-person fs-5" style={{ color: '#6f42c1' }}></i>
                    </div>
                    <div className="text-start">
                      <div className="fw-semibold">{mensajero.nombres} {mensajero.apellidos}</div>
                      <small className="text-muted">ID: {mensajero.id}</small>
                    </div>
                  </div>
                </td>
                <td>{mensajero.email}</td>
                <td>
                  <div className="d-flex justify-content-center align-items-center">
                    <i className="bi bi-truck me-2" style={{ color: '#28a745' }}></i>
                    {getTipoVehiculoDescripcion(mensajero.tipoVehiculoId)}
                  </div>
                </td>
                <td>
                  <span className={`badge ${
                    mensajero.estadoId === 1 ? 'bg-success' : 
                    mensajero.estadoId === 2 ? 'bg-secondary' : 
                    'bg-warning'
                  } bg-opacity-10 text-${
                    mensajero.estadoId === 1 ? 'success' : 
                    mensajero.estadoId === 2 ? 'secondary' : 
                    'warning'
                  }`}>
                    {getEstadoGeneralDescripcion(mensajero.estadoId)}
                  </span>
                </td>
                <td>
                  <span className={`badge bg-${estadoDisp.color} bg-opacity-10 text-${estadoDisp.color}`}>
                    <i className={`bi ${estadoDisp.icono} me-1`}></i>
                    {estadoDisp.texto}
                  </span>
                </td>
                <td>
                  <span className="badge bg-primary bg-opacity-10 text-primary">
                    {mensajero.pedidosActivos}/{mensajero.maxPedidosSimultaneos}
                  </span>
                </td>
                <td>
                  <div className="d-flex justify-content-center align-items-center">
                    <i className="bi bi-award me-2" style={{ color: '#ffc107' }}></i>
                    {mensajero.totalEntregas}
                  </div>
                </td>
                <td>
                  <small className="text-muted">
                    {formatearFecha(mensajero.fechaUltimaEntrega)}
                  </small>
                </td>
                <td>
                  <div className="btn-group btn-group-sm justify-content-center">
                    <button 
                      className="btn btn-outline-primary"
                      onClick={() => handleEditar(mensajero)}
                    >
                      <i className="bi bi-pencil"></i>
                    </button>
                    <button 
                      className="btn btn-outline-secondary"
                      onClick={() => handleCambiarDisponibilidad(mensajero.id, mensajero.disponibilidad)}
                      disabled={mensajero.estadoId !== 1 || mensajero.pedidosActivos > 0}
                      title={
                        mensajero.estadoId !== 1
                          ? `El mensajero debe estar activo para cambiar disponibilidad (Estado: ${getEstadoGeneralDescripcion(mensajero.estadoId)})`
                          : mensajero.pedidosActivos > 0
                            ? 'No se puede cambiar disponibilidad si el mensajero tiene pedidos activos'
                            : 'Cambiar disponibilidad'
                      }
                    >
                      <i className={`bi ${
                        (mensajero.disponibilidad === true || mensajero.disponibilidad === 1 || mensajero.disponibilidad === '1') 
                          ? 'bi-pause' 
                          : 'bi-play'
                      }`}></i>
                    </button>
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );


  const renderEstadisticas = () => (
    <div className="row g-3">
      <div className="col-md-3">
        <div className="card text-center h-100 border-0 shadow-sm">
          <div className="card-body py-3">
            <i className="bi bi-people fs-3 text-primary mb-2"></i>
            <h4 className="fw-bold text-primary mb-1">{estadisticas.totalMensajeros}</h4>
            <p className="text-muted small mb-0">Total Mensajeros</p>
          </div>
        </div>
      </div>
      <div className="col-md-3">
        <div className="card text-center h-100 border-0 shadow-sm">
          <div className="card-body py-3">
            <i className="bi bi-check-circle fs-3 text-success mb-2"></i>
            <h4 className="fw-bold text-success mb-1">{estadisticas.disponibles}</h4>
            <p className="text-muted small mb-0">Disponibles</p>
          </div>
        </div>
      </div>
      <div className="col-md-3">
        <div className="card text-center h-100 border-0 shadow-sm">
          <div className="card-body py-3">
            <i className="bi bi-clock fs-3 text-warning mb-2"></i>
            <h4 className="fw-bold text-warning mb-1">{estadisticas.ocupados}</h4>
            <p className="text-muted small mb-0">Ocupados</p>
          </div>
        </div>
      </div>
      <div className="col-md-3">
        <div className="card text-center h-100 border-0 shadow-sm">
          <div className="card-body py-3">
            <i className="bi bi-x-circle fs-3 text-danger mb-2"></i>
            <h4 className="fw-bold text-danger mb-1">{estadisticas.inactivos}</h4>
            <p className="text-muted small mb-0">Inactivos</p>
          </div>
        </div>
      </div>
    </div>
  );

  const renderPaginacion = () => {
    const itemsPorPagina = 20;
    const totalFiltrados = mensajerosFiltrados.length;
    const totalPaginasFiltradas = Math.ceil(totalFiltrados / itemsPorPagina);
    
    if (totalPaginasFiltradas <= 1) return null;

    paginas.push(
      <li key="prev" className={`page-item ${paginaActual === 0 ? 'disabled' : ''}`}>
        <button 
          className="page-link" 
          onClick={() => cambiarPagina(paginaActual - 1)}
          disabled={paginaActual === 0}
        >
          Anterior
        </button>
      </li>
    );

    for (let i = 0; i < totalPaginas; i++) {
      if (i === paginaActual || 
          i === 0 || 
          i === totalPaginas - 1 || 
          (i >= paginaActual - 1 && i <= paginaActual + 1)) {
        paginas.push(
          <li key={i} className={`page-item ${i === paginaActual ? 'active' : ''}`}>
            <button className="page-link" onClick={() => cambiarPagina(i)}>
              {i + 1}
            </button>
          </li>
        );
      } else if (i === paginaActual - 2 || i === paginaActual + 2) {
        paginas.push(
          <li key={i} className="page-item disabled">
            <span className="page-link">...</span>
          </li>
        );
      }
    }

    paginas.push(
      <li key="next" className={`page-item ${paginaActual === totalPaginas - 1 ? 'disabled' : ''}`}>
        <button 
          className="page-link" 
          onClick={() => cambiarPagina(paginaActual + 1)}
          disabled={paginaActual === totalPaginas - 1}
        >
          Siguiente
        </button>
      </li>
    );

    return (
      <nav className="d-flex justify-content-between align-items-center mt-4">
        <div className="text-muted">
          Mostrando {paginacion.pagina * paginacion.tamano + 1} a {Math.min((paginacion.pagina + 1) * paginacion.tamano, paginacion.totalElementos)} de {paginacion.totalElementos} resultados
        </div>
        <ul className="pagination mb-0">
          {paginas}
        </ul>
      </nav>
    );
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
      <div className="container py-4">
        <div className="alert alert-danger" role="alert">
          <i className="bi bi-exclamation-triangle me-2"></i>
          {error}
          <button 
            className="btn btn-outline-danger ms-3"
            onClick={() => {
              setError(null);
              cargarDatosIniciales();
            }}
          >
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  return (
   <>
    <div className="container py-4" style={{ minHeight: 'calc(100vh - 120px)' }}>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
          <i className="bi bi-people me-2" style={{ color: '#cca9bd' }}></i>
          Gestión de mensajeros
          {empresaActual && (
            <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
              <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
              {empresaActual.nombre}
            </span>
          )}
        </h3>
        <div className="d-flex align-items-center gap-2">
          <div className="text-muted">
            <i className="bi bi-info-circle me-1"></i>
            Mostrando {mensajerosFiltrados.length} de {mensajeros.length} mensajeros
          </div>
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

              <input
                type="radio"
                className="btn-check"
                name="vista"
                id="vista-estadisticas"
                checked={vista === 'estadisticas'}
                onChange={() => setVista('estadisticas')}
              />
              <label className="btn btn-outline-secondary" htmlFor="vista-estadisticas">
                <i className="bi bi-graph-up"></i> Estadísticas
              </label>
            </div>
          </div>
        </div>
        <div className="card-body">
          <div className="row g-3 mb-3">
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-search me-1" style={{ color: '#007bff' }}></i>
                Buscar mensajero
              </label>
              <input
                type="text"
                value={filtroNombre}
                onChange={(e) => setFiltroNombre(e.target.value)}
                className="form-control"
                placeholder="Buscar por nombre, apellido o email"
              />
            </div>
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-truck me-1" style={{ color: '#28a745' }}></i>
                Tipo de vehículo
              </label>
                <select
                  value={filtroTipoVehiculo}
                  onChange={(e) => setFiltroTipoVehiculo(e.target.value)}
                  className="form-select"
                >
                  <option value="" disabled hidden>Todos los vehículos</option>
                  {tiposVehiculo.map(tipo => (
                    <option key={tipo.id} value={tipo.id}>{tipo.descripcion}</option>
                  ))}
                </select>
            </div>
            <div className="col-md-4">
                            <label className="form-label fw-semibold">
                <i className="bi bi-clock me-1" style={{ color: '#ffc107' }}></i>
                Disponibilidad
              </label>
              <select
                value={filtroDisponibilidad}
                onChange={(e) => setFiltroDisponibilidad(e.target.value)}
                className="form-select"
              >
                <option value="" disabled hidden>Todas las disponibilidades</option>
                <option value="true">Disponible</option>
                <option value="false">No disponible</option>
              </select>
            </div>
          </div>
          
          <div className="row g-3">
            <div className="col-md-4">

            </div>
            <div className="col-md-4"></div>
            <div className="col-md-4 d-flex flex-column justify-content-end align-items-end">
               {(filtroNombre || filtroTipoVehiculo || filtroDisponibilidad) && (
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

      {formVisible && (
        <div ref={formRef} className="card shadow-sm mb-4 mx-auto" style={{ maxWidth: '800px' }}>
          <div className="card-header bg-primary text-white">
            <i className="bi bi-person-badge me-2"></i>
            Editar mensajero
          </div>

          <form onSubmit={handleGuardar} className="p-4">
            <div className="row mb-3">
              <div className="col-md-6">
                <label className="form-label">
                  <i className="bi bi-person me-1 text-primary"></i>
                  Nombres
                </label>
                <input
                  type="text"
                  value={editingMensajero?.nombres || ''}
                  className="form-control"
                  readOnly
                  style={{ backgroundColor: '#f8f9fa' }}
                />
              </div>
              <div className="col-md-6">
                <label className="form-label">
                  <i className="bi bi-person me-1 text-primary"></i>
                  Apellidos
                </label>
                <input
                  type="text"
                  value={editingMensajero?.apellidos || ''}
                  className="form-control"
                  readOnly
                  style={{ backgroundColor: '#f8f9fa' }}
                />
              </div>
            </div>

            <div className="row mb-3">
              <div className="col-md-6">
                <label className="form-label">
                  <i className="bi bi-envelope me-1 text-success"></i>
                  Email
                </label>
                <input
                  type="email"
                  value={editingMensajero?.email || ''}
                  className="form-control"
                  readOnly
                  style={{ backgroundColor: '#f8f9fa' }}
                />
              </div>
              <div className="col-md-6">
                <div className="row">
                  <div className="col-6">
                    <label className="form-label">
                      <i className="bi bi-check-circle me-1" style={{ color: '#6f42c1' }}></i>
                      Estado
                    </label>
                    <div className="d-flex align-items-center" style={{ height: '38px' }}>
                      <small className="text-muted">{getEstadoGeneralDescripcion(editingMensajero?.estadoId)}</small>
                    </div>
                  </div>
                  <div className="col-6">
                    <label className="form-label">
                      <i className="bi bi-activity me-1 text-info"></i>
                      Disponibilidad
                    </label>
                    <div className="d-flex align-items-center" style={{ height: '38px' }}>
                      <span className={`badge ${editingMensajero?.disponibilidad ? 'bg-success' : 'bg-danger'}`}>
                        <i className={`bi ${editingMensajero?.disponibilidad ? 'bi-check-circle' : 'bi-x-circle'} me-1`}></i>
                        {editingMensajero?.disponibilidad ? 'Disponible' : 'No disponible'}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {editingMensajero?.id && (
              <div className="row mb-3">
                <div className="col-md-4">
                  <label className="form-label">
                    <i className="bi bi-box me-1 text-secondary"></i>
                    Pedidos activos
                  </label>
                  <input
                    type="number"
                    value={editingMensajero.pedidosActivos || 0}
                    className="form-control"
                    readOnly
                    style={{ backgroundColor: '#f8f9fa' }}
                  />
                </div>
                <div className="col-md-4">
                  <label className="form-label">
                    <i className="bi bi-trophy me-1 text-secondary"></i>
                    Total entregas
                  </label>
                  <input
                    type="number"
                    value={editingMensajero.totalEntregas || 0}
                    className="form-control"
                    readOnly
                    style={{ backgroundColor: '#f8f9fa' }}
                  />
                </div>
                <div className="col-md-4">
                  <label className="form-label">
                    <i className="bi bi-clock-history me-1 text-secondary"></i>
                    Última entrega
                  </label>
                  <input
                    type="text"
                    value={
                      editingMensajero.fechaUltimaEntrega
                        ? new Date(editingMensajero.fechaUltimaEntrega).toLocaleDateString('es-ES')
                        : 'Sin entregas'
                    }
                    className="form-control"
                    readOnly
                    style={{ backgroundColor: '#f8f9fa' }}
                  />
                </div>
              </div>
            )}

            <div className="row mb-4">
              <div className="col-md-6">
                <label className="form-label">
                  <i className="bi bi-truck me-1 text-warning"></i>
                  Tipo de vehículo <span className="text-danger">*</span>
                </label>
                <select
                  name="tipoVehiculoId"
                  value={editingMensajero?.tipoVehiculoId || ''}
                  onChange={handleInputChange}
                  className={`form-select ${errores.tipoVehiculoId ? 'is-invalid' : ''}`}
                >
                  <option value="" disabled hidden>Seleccione un tipo de vehículo</option>
                  {tiposVehiculo.map(tipo => (
                    <option key={tipo.id} value={tipo.id}>{tipo.descripcion}</option>
                  ))}
                </select>
                {errores.tipoVehiculoId && <div className="invalid-feedback">{errores.tipoVehiculoId}</div>}
              </div>
              <div className="col-md-6">
                <label className="form-label">
                  <i className="bi bi-boxes me-1 text-orange"></i>
                  Máximo pedidos simultáneos
                </label>
                <input
                  type="number"
                  name="maxPedidosSimultaneos"
                  value={editingMensajero?.maxPedidosSimultaneos || ''}
                  className="form-control"
                  readOnly
                  style={{ backgroundColor: '#f8f9fa' }}
                />
                <small className="form-text text-muted">
                  Este valor se configura automáticamente
                </small>
              </div>
            </div>

            <div className="d-flex justify-content-end gap-2">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                    Actualizando...
                  </>
                ) : (
                  <>
                    <i className="bi bi-save me-1"></i>
                    Guardar
                  </>
                )}
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={handleCancelar}
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      {vista === 'estadisticas' && renderEstadisticas()}
      {vista === 'tarjetas' && renderTarjetasMensajeros()}
      {vista === 'lista' && renderTablaMensajeros()}

      {vista !== 'estadisticas' && renderPaginacion()}
    </div>
    <Footer />
  </>
  );
}