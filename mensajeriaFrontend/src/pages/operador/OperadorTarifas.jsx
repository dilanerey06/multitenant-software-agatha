import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import Footer from '../../components/Footer.jsx';

const BASE_URL = '/proxy/api/tarifas';

const tarifaService = {
  getAuthHeaders: () => {
    const token = localStorage.getItem('token');
    const additionalData = localStorage.getItem('x-additional-data');
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      'x-additional-data': additionalData
    };
  },

  getMensajeriaId: () => {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.mensajeriaId || payload.mensajeria_id;
    } catch (error) {
      console.error('Error al decodificar token:', error);
      return null;
    }
  },

  obtenerTodas: async () => {
    const response = await axios.get(BASE_URL, {
      headers: tarifaService.getAuthHeaders()
    });
    return response.data;
  },

  obtenerPorId: async (id) => {
    const response = await axios.get(`${BASE_URL}/${id}`, {
      headers: tarifaService.getAuthHeaders()
    });
    return response.data;
  },

  crear: async (tarifaData) => {
    const response = await axios.post(BASE_URL, tarifaData, {
      headers: tarifaService.getAuthHeaders()
    });
    return response.data;
  },

  actualizar: async (id, tarifaData) => {
    const response = await axios.put(`${BASE_URL}/${id}`, tarifaData, {
      headers: tarifaService.getAuthHeaders()
    });
    return response.data;
  },

  toggleEstado: async (id, activa) => {
    const response = await axios.patch(`${BASE_URL}/${id}/estado?activa=${activa}`, null, {
      headers: tarifaService.getAuthHeaders()
    });
    return response.data;
  },

  activar: async (id) => {
    const response = await axios.patch(`${BASE_URL}/${id}/activar`, null, {
      headers: tarifaService.getAuthHeaders()
    });
    return response.data;
  },

  desactivar: async (id) => {
    const response = await axios.patch(`${BASE_URL}/${id}/desactivar`, null, {
      headers: tarifaService.getAuthHeaders()
    });
    return response.data;
  },

  eliminar: async (id) => {
    const response = await axios.delete(`${BASE_URL}/${id}`, {
      headers: tarifaService.getAuthHeaders()
    });
    return response.data;
  }
};

export default function OperadorTarifas() {
  const formRef = useRef(null); 
  const [tarifas, setTarifas] = useState([]);
  const [tarifasFiltradas, setTarifasFiltradas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [mensajeriaNombre, setMensajeriaNombre] = useState('');
  const [mensajeriaId, setMensajeriaId] = useState(null);
  const [formVisible, setFormVisible] = useState(false);
  const [modalMode, setModalMode] = useState('create');
  const [selectedTarifa, setSelectedTarifa] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [resumen, setResumen] = useState({
    totalTarifas: 0,
    activaCount: 0,
    inactivaCount: 0,
    tarifaPorDefecto: null
  });
  const [vista, setVista] = useState('lista'); 

  const [erroresFiltros, setErroresFiltros] = useState({
    valorMinimo: '',
    valorMaximo: ''
  });

  const validarFiltros = (filtrosActuales) => {
    const errores = {
      valorMinimo: '',
      valorMaximo: ''
    };

    const valorMin = parseFloat(filtrosActuales.valorMinimo);
    const valorMax = parseFloat(filtrosActuales.valorMaximo);

    if (filtrosActuales.valorMinimo && valorMin < 0) {
      errores.valorMinimo = 'El valor mínimo no puede ser negativo';
    }

    if (filtrosActuales.valorMaximo && valorMax < 0) {
      errores.valorMaximo = 'El valor máximo no puede ser negativo';
    }

    if (filtrosActuales.valorMinimo && filtrosActuales.valorMaximo && 
        !isNaN(valorMin) && !isNaN(valorMax) && valorMax <= valorMin) {
      errores.valorMaximo = 'El valor máximo debe ser mayor que el mínimo';
    }

    setErroresFiltros(errores);
    return Object.values(errores).every(error => error === '');
  };

  const [filtros, setFiltros] = useState({
    nombre: '',
    estado: '',
    valorMinimo: '',
    valorMaximo: ''
  });

  const [formData, setFormData] = useState({
    nombre: '',
    valorFijo: '',
    descripcion: '',
    activa: true
  });

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      const mensajeriaIdFromToken = tarifaService.getMensajeriaId();
      setMensajeriaId(mensajeriaIdFromToken);
    }
    
    cargarTarifas();
  }, []);

  const cargarTarifas = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await tarifaService.obtenerTodas();
      const tarifasData = response.data || response;
      
      setTarifas(tarifasData);
      setTarifasFiltradas(tarifasData);
      calcularResumen(tarifasData);
    } catch (err) {
      console.error('Error al cargar tarifas:', err);
      setError('Error al cargar las tarifas: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const calcularResumen = (tarifasData) => {
    const totalTarifas = tarifasData.length;
    const activaCount = tarifasData.filter(t => t.activa).length;
    const inactivaCount = totalTarifas - activaCount;
    const tarifaPorDefecto = tarifasData.find(t => t.esDefault)?.nombre || 
                           (tarifasData.length > 0 ? tarifasData[0].nombre : null);
    
    setResumen({
      totalTarifas,
      activaCount,
      inactivaCount,
      tarifaPorDefecto
    });
  };

  const handleFiltroChange = (e) => {
    const { name, value } = e.target;
    const nuevosFiltros = { ...filtros, [name]: value };
    setFiltros(nuevosFiltros);
    
    const esValido = validarFiltros(nuevosFiltros);
    if (esValido) {
      aplicarFiltros(nuevosFiltros);
    }
  };

  const aplicarFiltros = (filtrosActuales) => {
    let resultado = [...tarifas];

    if (filtrosActuales.nombre) {
      resultado = resultado.filter(tarifa => 
        tarifa.nombre.toLowerCase().includes(filtrosActuales.nombre.toLowerCase())
      );
    }

    if (filtrosActuales.estado) {
      const esActiva = filtrosActuales.estado === 'activa';
      resultado = resultado.filter(tarifa => tarifa.activa === esActiva);
    }

    if (filtrosActuales.valorMinimo) {
      resultado = resultado.filter(tarifa => 
        parseFloat(tarifa.valorFijo) >= parseFloat(filtrosActuales.valorMinimo)
      );
    }

    if (filtrosActuales.valorMaximo) {
      resultado = resultado.filter(tarifa => 
        parseFloat(tarifa.valorFijo) <= parseFloat(filtrosActuales.valorMaximo)
      );
    }

    setTarifasFiltradas(resultado);
  };

  const limpiarFiltros = () => {
    setFiltros({
      nombre: '',
      estado: '',
      valorMinimo: '',
      valorMaximo: ''
    });
    setErroresFiltros({
      valorMinimo: '',
      valorMaximo: ''
    });
    setTarifasFiltradas(tarifas);
  };

  useEffect(() => {
    const obtenerNombreMensajeria = async () => {
      if (!mensajeriaId) return;
      
      try {
        
        const token = localStorage.getItem('token'); 
        const additionalData = localStorage.getItem('x-additional-data');

        const response = await fetch(`/proxy/api/empresas-mensajeria/${mensajeriaId}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
            'x-additional-data': additionalData,
          }
        });
        
        
        if (response.ok) {
          const responseData = await response.json();
          const empresaData = responseData.data || responseData;
          const nombre = empresaData.nombre || empresaData.name || empresaData.razonSocial || empresaData.empresa || 'Sin nombre';
          setMensajeriaNombre(nombre);
        } else {
          console.error('Error en la respuesta:', response.status, response.statusText);
          const errorData = await response.text();
          console.error('Error data:', errorData);
        }
      } catch (error) {
        console.error('Error al obtener el nombre:', error);
      }
    };

    obtenerNombreMensajeria();
  }, [mensajeriaId]);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      const mensajeriaIdFromToken = tarifaService.getMensajeriaId();
      setMensajeriaId(mensajeriaIdFromToken);
    }
    
    cargarTarifas();
  }, []);

  const formatearFecha = (fecha) => {
    return new Date(fecha).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatearMoneda = (valor) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP'
    }).format(valor);
  };

  const handleOpenForm = (mode, tarifa = null) => {
    setModalMode(mode);
    setSelectedTarifa(tarifa);
    setError(null);
    
    if (mode === 'create') {
      setFormData({
        nombre: '',
        valorFijo: '',
        descripcion: '',
        activa: true
      });
    } else if (tarifa) {
      setFormData({
        nombre: tarifa.nombre,
        valorFijo: tarifa.valorFijo.toString(),
        descripcion: tarifa.descripcion || '',
        activa: tarifa.activa
      });
    }
    
    setFormVisible(true);
    setTimeout(() => {
      formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  };

  const handleCloseForm = () => {
    setFormVisible(false);
    setSelectedTarifa(null);
    setError(null);
    setFormData({
      nombre: '',
      valorFijo: '',
      descripcion: '',
      activa: true
    });
  };

  const handleFormChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.nombre.trim() || !formData.valorFijo) {
      setError('Nombre y valor fijo son obligatorios');
      return;
    }

    if (parseFloat(formData.valorFijo) < 0) {
      setError('El valor debe ser positivo');
      return;
    }
    
    setSubmitting(true);
    setError(null);
    
    try {
      const dataToSend = {
        mensajeriaId: mensajeriaId,
        nombre: formData.nombre.trim(),
        valorFijo: parseFloat(formData.valorFijo),
        descripcion: formData.descripcion.trim(),
        activa: formData.activa
      };
      
      let response;
      
      if (modalMode === 'create') {
        response = await tarifaService.crear(dataToSend);
      } else if (modalMode === 'edit') {
        response = await tarifaService.actualizar(selectedTarifa.id, dataToSend);
      }
      
      await cargarTarifas();
      handleCloseForm();
      
    } catch (err) {
      console.error('Error al procesar tarifa:', err);
      setError('Error al procesar la tarifa: ' + err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggleEstado = async (id, estadoActual) => {
    try {
      const nuevoEstado = !estadoActual;
      await tarifaService.toggleEstado(id, nuevoEstado);
      await cargarTarifas();
    } catch (err) {
      console.error('Error al cambiar estado:', err);
      setError('Error al cambiar el estado: ' + err.message);
    }
  };

  const handleEliminar = async (id) => {
    if (window.confirm('¿Estás seguro de que deseas eliminar esta tarifa?')) {
      try {
        const response = await tarifaService.eliminar(id);
        const mensaje = response?.message || 'Operación completada.';
        alert(mensaje); 
        await cargarTarifas();
      } catch (err) {
        console.error('Error al eliminar tarifa:', err);
        const mensajeError =
          err.response?.data?.error || err.response?.data?.message || err.message;
        setError('Error al eliminar la tarifa: ' + mensajeError);
      }
    }
  };

  const renderTarjetasTarifas = () => (
    <div className="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
      {tarifasFiltradas.map((tarifa) => (
        <div className="col" key={tarifa.id}>
          <div className="card h-100 shadow-sm border-0">
            <div className="card-body">
              <div className="d-flex justify-content-between align-items-start mb-3">
                <div className="d-flex align-items-center">
                  <i className="bi bi-cash-coin me-2 fs-4" style={{ color: '#cca9bd' }}></i>
                  <div>
                    <h5 className="mb-0 fw-bold">{tarifa.nombre}</h5>
                    <small className="text-muted">ID: {tarifa.id}</small>
                  </div>
                </div>
                <div>
                  {tarifa.activa ? (
                    <span className="badge bg-success bg-opacity-10 text-success">
                      Activa
                    </span>
                  ) : (
                    <span className="badge bg-danger bg-opacity-10 text-danger">
                      Inactiva
                    </span>
                  )}
                </div>
              </div>

              <div className="mb-3">
                <h4 className="fw-bold text-primary">
                  {formatearMoneda(tarifa.valorFijo)}
                </h4>
                <small className="text-muted">Valor fijo</small>
              </div>

              {tarifa.descripcion && (
                <div className="mb-3">
                  <p className="text-muted small">{tarifa.descripcion}</p>
                </div>
              )}

              <div className="text-muted small mb-4">
                <i className="bi bi-calendar me-1"></i>
                {formatearFecha(tarifa.fechaCreacion || tarifa.createdAt)}
              </div>

              <div className="d-flex justify-content-between align-items-center border-top pt-3">
                <div className="btn-group">
                  <button
                    onClick={() => handleOpenForm('edit', tarifa)}
                    className="btn btn-sm btn-outline-success"
                    title="Editar"
                  >
                    <i className="bi bi-pencil"></i>
                  </button>
                  <button
                    onClick={() => handleEliminar(tarifa.id)}
                    className="btn btn-sm btn-outline-danger"
                    title="Eliminar"
                  >
                    <i className="bi bi-trash"></i>
                  </button>
                </div>
                <button
                  onClick={() => handleToggleEstado(tarifa.id, tarifa.activa)}
                  className={`btn btn-sm ${tarifa.activa ? 'btn-outline-danger' : 'btn-outline-success'}`}
                >
                  {tarifa.activa ? (
                    <>
                      <i className="bi bi-x-circle me-1"></i>
                      Desactivar
                    </>
                  ) : (
                    <>
                      <i className="bi bi-check-circle me-1"></i>
                      Activar
                    </>
                  )}
                </button>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );

  const renderTablaTarifas = () => (
    <div className="table-responsive">
      <table className="table table-hover text-center">
        <thead className="table-light">
          <tr>
            <th className="text-center">Nombre</th>
            <th className="text-center">Valor</th>
            <th className="text-center">Estado</th>
            <th className="text-center">Descripción</th>
            <th className="text-center">Fecha Creación</th>
            <th className="text-center">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {tarifasFiltradas.map((tarifa) => (
            <tr key={tarifa.id}>
              <td>
                <div className="d-flex justify-content-center align-items-center">
                  <i className="bi bi-cash-coin me-2" style={{ color: '#cca9bd' }}></i>
                  <div className="text-start">
                    <div className="fw-semibold">{tarifa.nombre}</div>
                    <small className="text-muted">ID: {tarifa.id}</small>
                  </div>
                </div>
              </td>
              <td className="fw-bold text-primary">
                {formatearMoneda(tarifa.valorFijo)}
              </td>
              <td>
                {tarifa.activa ? (
                  <span className="badge bg-success bg-opacity-10 text-success">
                    Activa
                  </span>
                ) : (
                  <span className="badge bg-danger bg-opacity-10 text-danger">
                    Inactiva
                  </span>
                )}
              </td>
              <td>
                <small className="text-muted">{tarifa.descripcion || 'N/A'}</small>
              </td>
              <td>
                <small className="text-muted">
                  {formatearFecha(tarifa.fechaCreacion || tarifa.createdAt)}
                </small>
              </td>
              <td>
                <div className="btn-group btn-group-sm justify-content-center">
                  <button
                    onClick={() => handleOpenForm('edit', tarifa)}
                    className="btn btn-outline-primary"
                  >
                    <i className="bi bi-pencil"></i>
                  </button>
                  <button
                    onClick={() => handleToggleEstado(tarifa.id, tarifa.activa)}
                    className="btn btn-outline-orange"
                  >
                    <i className={`bi ${tarifa.activa ? 'bi-pause' : 'bi-play'}`}></i>
                  </button>
                  <button
                    onClick={() => handleEliminar(tarifa.id)}
                    className="btn btn-outline-danger"
                  >
                    <i className="bi bi-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );


  const renderEstadisticas = () => (
    <div className="row g-3">
      <div className="col-md-3">
        <div className="card text-center h-100 border-0 shadow-sm">
          <div className="card-body py-3">
            <i className="bi bi-cash-stack fs-3 text-primary mb-2"></i>
            <h4 className="fw-bold text-primary mb-1">{resumen.totalTarifas}</h4>
            <p className="text-muted small mb-0">Total Tarifas</p>
          </div>
        </div>
      </div>
      <div className="col-md-3">
        <div className="card text-center h-100 border-0 shadow-sm">
          <div className="card-body py-3">
            <i className="bi bi-check-circle fs-3 text-success mb-2"></i>
            <h4 className="fw-bold text-success mb-1">{resumen.activaCount}</h4>
            <p className="text-muted small mb-0">Activas</p>
          </div>
        </div>
      </div>
      <div className="col-md-3">
        <div className="card text-center h-100 border-0 shadow-sm">
          <div className="card-body py-3">
            <i className="bi bi-x-circle fs-3 text-danger mb-2"></i>
            <h4 className="fw-bold text-danger mb-1">{resumen.inactivaCount}</h4>
            <p className="text-muted small mb-0">Inactivas</p>
          </div>
        </div>
      </div>
      <div className="col-md-3">
        <div className="card text-center h-100 border-0 shadow-sm">
          <div className="card-body py-3">
            <i className="bi bi-star fs-3 text-warning mb-2"></i>
            <h4 className="fw-bold text-warning mb-1">{resumen.tarifaPorDefecto || 'Ninguna'}</h4>
            <p className="text-muted small mb-0">Tarifa por defecto</p>
          </div>
        </div>
      </div>
    </div>
  );

  if (loading) {
    <>
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: 'calc(100vh - 120px)' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
      </div>
    );
      <Footer />
    </>
  }

  return (
    <>
      <div className="container py-4" style={{ minHeight: 'calc(100vh - 120px)' }}>
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
            <i className="bi bi-cash-coin me-2" style={{ color: '#e5be01' }}></i>
            Gestión de tarifas
            {mensajeriaId && (
              <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
                <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
                {mensajeriaNombre}
              </span>
            )}
          </h3>
          <div className="d-flex align-items-center gap-3">
            <div className="text-muted">
              <i className="bi bi-info-circle me-1"></i>
              Mostrando {tarifasFiltradas.length} de {tarifas.length} tarifas
            </div>
            <button
              onClick={() => handleOpenForm('create')}
              className="btn btn-success"
            >
              <i className="bi bi-plus-lg me-2"></i>
              Nueva Tarifa
            </button>
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
                  Nombre
                </label>
                <input
                  type="text"
                  name="nombre"
                  value={filtros.nombre}
                  onChange={handleFiltroChange}
                  className="form-control"
                  placeholder="Buscar por nombre"
                />
              </div>
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-currency-dollar me-1" style={{ color: '#28a745' }}></i>
                  Valor Mínimo
                </label>
                <input
                  type="number"
                  name="valorMinimo"
                  value={filtros.valorMinimo}
                  onChange={handleFiltroChange}
                  className={`form-control ${erroresFiltros.valorMinimo ? 'is-invalid' : ''}`}
                  placeholder="0"
                  min="0"
                  step="0.01"
                />
                {erroresFiltros.valorMinimo && (
                  <div className="invalid-feedback">
                    {erroresFiltros.valorMinimo}
                  </div>
                )}
              </div>
              
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-currency-dollar me-1" style={{ color: '#ffa420' }}></i>
                  Valor Máximo
                </label>
                <input
                  type="number"
                  name="valorMaximo"
                  value={filtros.valorMaximo}
                  onChange={handleFiltroChange}
                  className={`form-control ${erroresFiltros.valorMaximo ? 'is-invalid' : ''}`}
                  placeholder="$"
                  min="0"
                  step="0.01"
                />
                {erroresFiltros.valorMaximo && (
                  <div className="invalid-feedback">
                    {erroresFiltros.valorMaximo}
                  </div>
                )}
              </div>
            </div>
            
            <div className="row g-3">
              
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-toggle-on me-1" style={{ color: '#4b3621' }}></i>
                  Estado
                </label>
                <select
                  name="estado"
                  value={filtros.estado}
                  onChange={handleFiltroChange}
                  className="form-control"
                >
                  <option value="" disabled hidden>Todos</option>
                  <option value="activa">Activas</option>
                  <option value="inactiva">Inactivas</option>
                </select>
              </div>
              <div className="col-md-4"></div>
              <div className="col-md-4 d-flex flex-column justify-content-end align-items-end">
                {(filtros.nombre || filtros.estado || filtros.valorMinimo || filtros.valorMaximo) && (
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
          <div ref={formRef} className="card shadow-sm mb-4 mx-auto" style={{ maxWidth: '600px' }}>
            <div className="card-header bg-primary text-white">
              <i className="bi bi-cash-coin me-2"></i>
              {modalMode === 'create' ? 'Nueva tarifa' : 'Editar tarifa'}
            </div>
            <div className="card-body">
              <form onSubmit={handleSubmit}>
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-tag me-1" style={{ color: '#6f42c1' }}></i>
                      Nombre <span className="text-danger">*</span>
                    </label>
                    <input
                      type="text"
                      name="nombre"
                      value={formData.nombre}
                      onChange={handleFormChange}
                      className={`form-control ${error && !formData.nombre ? 'is-invalid' : ''}`}
                      placeholder="Nombre de la tarifa"
                      required
                      maxLength={100}
                      disabled={submitting}
                    />
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-currency-dollar me-1" style={{ color: '#007bff' }}></i>
                      Valor fijo <span className="text-danger">*</span>
                    </label>
                    <input
                      type="number"
                      name="valorFijo"
                      value={formData.valorFijo}
                      onChange={handleFormChange}
                      className={`form-control ${error && !formData.valorFijo ? 'is-invalid' : ''}`}
                      placeholder="0.00"
                      required
                      min="0"
                      step="0.01"
                      disabled={submitting}
                    />
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-check-circle me-1" style={{ color: '#28a745' }}></i>
                      Estado 
                    </label>
                    <select
                      name="activa"
                      value={formData.activa}
                      onChange={handleFormChange}
                      className="form-select"
                      disabled={submitting}
                    >
                      <option value={true}>Activa</option>
                      <option value={false}>Inactiva</option>
                    </select>
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-info-circle me-1" style={{ color: '#17a2b8' }}></i>
                      Descripción
                    </label>
                    <input
                      type="text"
                      name="descripcion"
                      value={formData.descripcion}
                      onChange={handleFormChange}
                      className="form-control"
                      placeholder="Descripción opcional"
                      maxLength={200}
                      disabled={submitting}
                    />
                  </div>
                </div>

                <div className="d-flex justify-content-end gap-2">
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={submitting}
                  >
                    {submitting ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" role="status">
                          <span className="visually-hidden">Cargando...</span>
                        </span>
                        Guardando...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-save me-1"></i>
                        {modalMode === 'create' ? 'Guardar' : 'Guardar'}
                      </>
                    )}
                  </button>
                  <button
                    type="button"
                    onClick={handleCloseForm}
                    className="btn btn-secondary"
                    disabled={submitting}
                  >
                    Cancelar
                  </button>
                </div>
              </form>
            </div>
            {error && (
              <div className="alert alert-danger mx-3 mt-3 mb-0">
                <i className="bi bi-exclamation-triangle me-2"></i>
                {error}
              </div>
            )}
          </div>
        )}

        {tarifasFiltradas.length === 0 ? (
          <div className="text-center py-5">
            <i className="bi bi-cash-coin" style={{ fontSize: '4rem', color: '#dee2e6' }}></i>
            <h4 className="text-muted mt-3">
              {tarifas.length === 0 ? 'No hay tarifas registradas' : 'No se encontraron resultados'}
            </h4>
            <p className="text-muted">
              {tarifas.length === 0 
                ? 'Comienza creando tu primera tarifa.' 
                : 'Intenta ajustar los filtros de búsqueda.'
              }
            </p>
          </div>
        ) : (
          <>
            {vista === 'tarjetas' && renderTarjetasTarifas()}
            {vista === 'lista' && renderTablaTarifas()}
            {vista === 'estadisticas' && renderEstadisticas()}
          </>
        )}
      </div>
     <Footer />
    </>
  );
}