import { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import Footer from '../../components/Footer.jsx';

const estadoNombre = {
  1: 'Activo',
  2: 'Inactivo', 
  3: 'Suspendido',
  4: 'Bloqueado'
};

const planNombre = {
  1: 'Básico',
  2: 'Profesional',
  3: 'Empresarial', 
  4: 'Ilimitado'
};

export default function SuperTenants() {
  const [tenants, setTenants] = useState([]);
  const [planes, setPlanes] = useState([]);
  const [estados, setEstados] = useState([]);
  const [formVisible, setFormVisible] = useState(false);
  const [editingTenant, setEditingTenant] = useState(null);
  const [errores, setErrores] = useState({});
  const [loading, setLoading] = useState(true);
  const [filtroNombre, setFiltroNombre] = useState('');
  const [filtroPlan, setFiltroPlan] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('');
  const [vista, setVista] = useState('lista');
  const formRef = useRef(null);
  const [modalAdminVisible, setModalAdminVisible] = useState(false);
  const [administradores, setAdministradores] = useState([]);
  const [adminSeleccionado, setAdminSeleccionado] = useState('');
  const [tenantParaAsociar, setTenantParaAsociar] = useState(null);

  const token = localStorage.getItem('token');
  const headers = {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json'
  };

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    setLoading(true);
    
    try {
      const [tenantsRes, planesRes, estadosRes] = await Promise.all([
        axios.get('/tenant/api/tenants', { headers }),
        axios.get('/tenant/api/planes', { headers }),
        axios.get('/tenant/api/estados', { headers })
      ]);

      setTenants(tenantsRes.data, []);
      setPlanes(planesRes.data, []);
      setEstados(estadosRes.data, []);

    } catch (error) {
      console.error('Error al cargar datos:',error.message); 
    } finally {
      setLoading(false);
    }
  };

  const tenantsFiltrados = tenants.filter(tenant => {
    const coincideNombre = !filtroNombre || 
      tenant.nombreEmpresa.toLowerCase().includes(filtroNombre.toLowerCase()) ||
      tenant.emailContacto.toLowerCase().includes(filtroNombre.toLowerCase());

    const coincidePlan = !filtroPlan || tenant.planId.toString() === filtroPlan;
    const coincideEstado = !filtroEstado || tenant.estadoId.toString() === filtroEstado;

    return coincideNombre && coincidePlan && coincideEstado;
  });

  const validarCampo = (name, value) => {
    let error = '';
    
    // Validación específica para planId
    if (name === 'planId') {
      if (!value) {
        error = 'El plan es obligatorio';
      }
      return error;
    }
    
    // Validación general para campos de texto
    if (!value || !value.toString().trim()) {
      error = 'Este campo es obligatorio';
    } else {
      if (name === 'emailContacto') {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
          error = 'Email no es válido';
        }
      }
      if (name === 'nombreEmpresa') {
        if (value.length < 3) {
          error = 'El nombre de la empresa debe tener al menos 3 caracteres';
        }
      }
    }
    return error;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    
    const processedValue = name === 'planId' ? 
      (value === '' ? null : parseInt(value, 10)) : 
      value;
    
    setEditingTenant(prev => ({ ...prev, [name]: processedValue }));
    
    const errorCampo = validarCampo(name, processedValue);
    setErrores(prev => ({ ...prev, [name]: errorCampo }));
  };

  const validarFormulario = () => {
    
    const erroresValidacion = {};
    
    ['nombreEmpresa', 'emailContacto', 'planId'].forEach(campo => {
      let valor = editingTenant?.[campo];
      const error = validarCampo(campo, valor);
      if (error) {
        erroresValidacion[campo] = error;
      }
    });
    
    
    setErrores(erroresValidacion);
    const esValido = Object.keys(erroresValidacion).length === 0;
    
    return esValido;
  };

  const handleGuardar = async (e) => {
    e.preventDefault();
    
    if (!validarFormulario()) {
      return;
    }

    try {
      const url = editingTenant.id 
        ? `/tenant/api/tenants/${editingTenant.id}`
        : '/tenant/api/tenants';
      
      const method = editingTenant.id ? axios.put : axios.post;

      const response = await method(url, editingTenant, { headers });
      
      const empresaData = {
        tenantId: editingTenant.id ? editingTenant.id : response.data.id, 
        nombre: editingTenant.nombreEmpresa,
        direccion: '', 
        telefono: '', 
        email: editingTenant.emailContacto,
        estadoId: 1 
      };
      
      
      try {
        if (editingTenant.id) {
          await axios.put(`/proxy/api/empresas-mensajeria/${editingTenant.id}`, empresaData, { headers });
        } else {
          await axios.post('/proxy/api/empresas-mensajeria', empresaData, { headers });
        }
        
      } catch (empresaError) {
        console.error('Error al procesar empresa de mensajería:', empresaError);
        console.error('Response data:', empresaError.response?.data);
        const action = editingTenant.id ? 'actualizar' : 'crear';
        alert(`Tenant ${editingTenant.id ? 'actualizado' : 'creado'} correctamente, pero hubo un error al ${action} la empresa de mensajería: ${empresaError.response?.data?.error || empresaError.message}`);
      }
      
      setFormVisible(false);
      setEditingTenant(null);
      setErrores({});
      
      alert(`Tenant ${editingTenant.id ? 'actualizado' : 'creado'} correctamente`);
      cargarDatos();
      
    } catch (error) {
      console.error('Error al guardar tenant:', error);
      console.error('Response data:', error.response?.data);
      alert(`Error al guardar tenant: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleNuevo = () => {
    setEditingTenant({
      nombreEmpresa: '',
      emailContacto: '',
      estado: 'ACTIVO', 
      planId: '', 
      idAdminMensajeria: null, 
    });
    setFormVisible(true);
    setErrores({});
    setTimeout(() => {
      formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  };

  const handleEditar = (tenant) => {
    setEditingTenant({ ...tenant });
    setFormVisible(true);
    setErrores({});
    setTimeout(() => {
      formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  };

  const handleEliminar = async (id) => {
    if (confirm('¿Eliminar tenant?')) {
      try {
        try {
          await axios.delete(`/proxy/api/empresas-mensajeria/${id}`, { headers });
        } catch (empresaError) {
          console.error('Error al eliminar empresa de mensajería:', empresaError);
          console.error('Response data:', empresaError.response?.data);
          const errorMsg = empresaError.response?.data?.error || empresaError.message;
          console.warn(`Advertencia: Error al eliminar empresa de mensajería: ${errorMsg}`);
        }
        
        await axios.delete(`/tenant/api/tenants/${id}`, { headers });
        cargarDatos();
        alert('Tenant eliminado correctamente');
        
      } catch (error) {
        console.error('Error al eliminar tenant:', error);
        console.error('Response data:', error.response?.data);
        alert(`Error al eliminar tenant: ${error.response?.data?.message || error.message}`);
      }
    }
  };

  const cargarAdministradoresDisponibles = async () => {
    try {
      const meRes = await axios.get('/proxy/api/auth/me', { headers });
      const usuarioActual = meRes.data.data;
      
      const usuariosRes = await axios.get('/proxy/api/usuarios/admin-mensajeria', { headers });
      
      const usuariosFiltrados = (usuariosRes.data.data || []).filter(usuario => {
        return usuario.id !== usuarioActual.id && 
              usuario.rolId === 2 && 
              !usuario.empresaId; 
      });
      
      setAdministradores(usuariosFiltrados);
    } catch (error) {
      console.error('Error al cargar administradores:', error);
      alert('Error al cargar administradores disponibles');
    }
  };

  const handleAsociarAdmin = (tenant) => {
    setTenantParaAsociar(tenant);
    setAdminSeleccionado('');
    setModalAdminVisible(true);
    cargarAdministradoresDisponibles();
  };

  const handleDesasociarAdmin = async (tenant) => {
    try {
      await axios.put(`/tenant/api/tenants/${tenant.id}/admin-mensajeria`, {
        adminMensajeriaId: null
      }, { headers });
      
      setTenants(prevTenants => 
        prevTenants.map(t => 
          t.id === tenant.id 
            ? { ...t, idAdminMensajeria: null, adminNombre: null }
            : t
        )
      );
      
      alert('Administrador desasociado correctamente');
      
    } catch (error) {
      console.error('Error al desasociar administrador:', error);
      alert('Error al desasociar administrador');
    }
  };

  const confirmarAsociacion = async () => {
    try {
      await axios.put(`/tenant/api/tenants/${tenantParaAsociar.id}/admin-mensajeria`, {
        adminMensajeriaId: parseInt(adminSeleccionado)
      }, { headers });
      
      await axios.put(`/proxy/api/usuarios/${adminSeleccionado}`, {
        mensajeriaId: tenantParaAsociar.id
      }, { headers });
      
      setTenants(prevTenants => 
        prevTenants.map(t => 
          t.id === tenantParaAsociar.id 
            ? { 
                ...t, 
                idAdminMensajeria: adminSeleccionado,
                adminNombre: administradores.find(admin => admin.id === adminSeleccionado)?.nombres + ' ' + 
                            administradores.find(admin => admin.id === adminSeleccionado)?.apellidos
              }
            : t
        )
      );
      
      setModalAdminVisible(false);
      setTenantParaAsociar(null);
      setAdminSeleccionado('');
      
      alert('Administrador asociado correctamente');
      
    } catch (error) {
      console.error('Error al asociar administrador:', error);
      alert('Error al asociar administrador');
    }
  };

  const handleCancelar = () => {
    setFormVisible(false);
    setEditingTenant(null);
    setErrores({});
  };

  const handleActivar = async (id) => {
    try {
      await axios.put(`/tenant/api/tenants/${id}/activate`, {}, { headers });
      cargarDatos();
      alert('Tenant activado correctamente');
    } catch (error) {
      console.error('Error al activar tenant:', error);
      alert('Error al activar tenant');
    }
  };

  const handleDesactivar = async (id) => {
    try {
      await axios.put(`/tenant/api/tenants/${id}/deactivate`, {}, { headers });
      cargarDatos();
      alert('Tenant desactivado correctamente');
    } catch (error) {
      console.error('Error al desactivar tenant:', error);
      alert('Error al desactivar tenant');
    }
  };

  const limpiarFiltros = () => {
    setFiltroNombre('');
    setFiltroPlan('');
    setFiltroEstado('');
  };

  const obtenerColorEstado = (estadoId) => {
    const nombreEstado = estadoNombre[estadoId];
    switch (nombreEstado) {
      case 'Activo': return '#28a745';
      case 'Inactivo': return '#6c757d';
      case 'Suspendido': return '#dc3545';
      case 'Bloqueado': return '#ffc107';
      default: return '#6c757d';
    }
  };

  const obtenerColorPlan = (planId) => {
    const nombrePlan = planNombre[planId];
    switch (nombrePlan) {
      case 'Básico': return '#17a2b8';
      case 'Profesional': return '#007bff';
      case 'Empresarial': return '#6f42c1';
      case 'Ilimitado': return '#fd7e14';
      default: return '#6c757d';
    }
  };

  const obtenerIconoPlan = (planId) => {
    const nombrePlan = planNombre[planId];
    switch (nombrePlan) {
      case 'Básico': return 'bi-person';
      case 'Profesional': return 'bi-briefcase';
      case 'Empresarial': return 'bi-building';
      case 'Ilimitado': return 'bi-gem';
      default: return 'bi-box';
    }
  };

  const calcularDiasDesdeCreacion = (fechaCreacion) => {
    const fechaActual = new Date();
    const fechaCreacionDate = new Date(fechaCreacion);
    const diferenciaTiempo = fechaActual - fechaCreacionDate;
    const diasTranscurridos = Math.floor(diferenciaTiempo / (1000 * 60 * 60 * 24));
    return diasTranscurridos < 0 ? 0 : diasTranscurridos;
  };

  const formatearFecha = (fecha) => {
    const fechaDate = new Date(fecha);
    return fechaDate.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  const obtenerPlanPorId = (planId) => {
    return planes.find(plan => plan.id === planId) || {};
  };

  const renderTarjetasTenants = () => {
    return (
      <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
        {tenantsFiltrados.map(tenant => {
          const planCompleto = obtenerPlanPorId(tenant.planId);
          const diasDesdeCreacion = calcularDiasDesdeCreacion(tenant.fechaCreacion);
          
          return (
            <div className="col" key={tenant.id}>
              <div className="card h-100 shadow-sm">
                <div className="card-body">
                  <div className="d-flex align-items-center mb-3">
                    <div 
                      className="rounded-circle me-3 d-flex align-items-center justify-content-center"
                      style={{ 
                        width: '50px', 
                        height: '50px', 
                        backgroundColor: `${obtenerColorPlan(tenant.planId)}20`,
                        color: obtenerColorPlan(tenant.planId)
                      }}
                    >
                      <i className={`bi ${obtenerIconoPlan(tenant.planId)} fs-4`}></i>
                    </div>
                    <div className="flex-grow-1">
                      <h5 className="card-title mb-1">{tenant.nombreEmpresa}</h5>
                      <span 
                        className="badge"
                        style={{ 
                          backgroundColor: `${obtenerColorEstado(tenant.estadoId)}20`,
                          color: obtenerColorEstado(tenant.estadoId)
                        }}
                      >
                        {tenant.estadoNombre}
                      </span>
                    </div>
                  </div>

                  <div className="mb-2">
                    <div className="text-muted small">
                      <i className="bi bi-envelope me-1" style={{ color: '#007bff' }}></i>
                      Email:
                    </div>
                    <div className="fw-semibold small">{tenant.emailContacto}</div>
                  </div>

                  <div className="mb-2">
                    <div className="text-muted small">
                      <i className={`bi ${obtenerIconoPlan(tenant.planId)} me-1`} style={{ color: obtenerColorPlan(tenant.planId) }}></i>
                      Plan:
                    </div>
                    <div style={{ color: obtenerColorPlan(tenant.planId) }}>
                      {planNombre[tenant.planId] || tenant.planNombre}
                    </div>
                  </div>

                  {/* Mostrar información del administrador si está asignado */}
                  {tenant.idAdminMensajeria && (
                    <div className="mb-2">
                      <div className="text-muted small">
                        <i className="bi bi-person-check me-1" style={{ color: '#28a745' }}></i>
                        Administrador:
                      </div>
                      <div className="fw-semibold small" style={{ color: '#28a745' }}>
                        {tenant.adminNombre || `Admin ID: ${tenant.idAdminMensajeria}`}
                      </div>
                    </div>
                  )}

                  <div className="row text-center mb-2">
                    <div className="col-6">
                      <div className="text-muted small">Usuarios disponibles</div>
                      <div className="fw-bold">
                        {planCompleto.limiteUsuarios === 0 ? 
                          'Ilimitado' : 
                          `${planCompleto.limiteUsuarios}`
                        }
                      </div>
                    </div>
                    <div className="col-6">
                      <div className="text-muted small">Pedidos/mes</div>
                      <div className="fw-bold">
                        {planCompleto.limitePedidosMes === 0 ? 
                          'Ilimitado' : 
                          `${planCompleto.limitePedidosMes}`
                        }
                      </div>
                    </div>
                  </div>

                  <div className="row text-center mb-2">
                    <div className="col-6">
                      <div className="text-muted small">
                        <i className="bi bi-calendar-event me-1" style={{ color: '#ffc107' }}></i>
                        Días desde creación:
                      </div>
                      <div className="fw-semibold small">
                        {diasDesdeCreacion} {diasDesdeCreacion === 1 ? 'día' : 'días'} - {formatearFecha(tenant.fechaCreacion)}
                      </div>
                    </div>
                    <div className="col-6">
                      <div className="text-muted small">
                        <i className="bi bi-lightning me-1" style={{ color: '#17a2b8' }}></i>
                        Pedidos simultáneos:
                      </div>
                      <div className="fw-semibold small">
                        {planCompleto.limitePedidosSimultaneos === 0 ? 
                          'Ilimitado' : 
                          `${planCompleto.limitePedidosSimultaneos}`
                        }
                      </div>
                    </div>
                  </div>
                </div>
                <div className="card-footer d-flex justify-content-center gap-1 bg-light flex-wrap">
                  <button
                    className="btn btn-sm btn-outline-primary"
                    onClick={() => handleEditar(tenant)}
                  >
                    <i className="bi bi-pencil-square me-1"></i>Editar
                  </button>
                  
                    
                        {tenant.idAdminMensajeria ? (
                          <button
                            className="btn btn-sm btn-outline-orange"
                            onClick={() => handleDesasociarAdmin(tenant)}
                            title="Desasociar Administrador"
                          >
                            <i className="bi bi-person-dash me-1"></i>Desasociar
                          </button>
                        ) : (
                          <button
                            className="btn btn-sm btn-outline-info"
                            onClick={() => handleAsociarAdmin(tenant)}
                            title="Asociar Administrador"
                          >
                            <i className="bi bi-person-plus me-1"></i>Asociar
                          </button>
                        )}

                  
                  {tenant.estadoId === 1 ? ( 
                    <button
                      className="btn btn-sm btn-outline-warning"
                      onClick={() => handleDesactivar(tenant.id)}
                    >
                      <i className="bi bi-pause-circle me-1"></i>Desactivar
                    </button>
                  ) : (
                    <button
                      className="btn btn-sm btn-outline-success"
                      onClick={() => handleActivar(tenant.id)}
                    >
                      <i className="bi bi-play-circle me-1"></i>Activar
                    </button>
                  )}
                  <button
                    className="btn btn-sm btn-outline-danger"
                    onClick={() => handleEliminar(tenant.id)}
                  >
                    <i className="bi bi-trash-fill me-1"></i>Eliminar
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    );
  };

  const renderEstadisticas = () => (
      <div className="row g-3">
          <div className="col-md-3">
              <div className="card text-center h-100 border-0 shadow-sm">
                  <div className="card-body py-3">
                      <i className="bi bi-check-circle fs-3 text-success mb-2"></i>
                      <h4 className="fw-bold text-success mb-1">
                          {tenants.filter(t => t.estadoId === 1).length}
                      </h4>
                      <p className="text-muted small mb-0">Activos</p>
                  </div>
              </div>
          </div>

          <div className="col-md-3">
              <div className="card text-center h-100 border-0 shadow-sm">
                  <div className="card-body py-3">
                      <i className="bi bi-pause-circle fs-3 text-secondary mb-2"></i>
                      <h4 className="fw-bold text-secondary mb-1">
                          {tenants.filter(t => t.estadoId === 2).length}
                      </h4>
                      <p className="text-muted small mb-0">Inactivos</p>
                  </div>
              </div>
          </div>

          <div className="col-md-3">
              <div className="card text-center h-100 border-0 shadow-sm">
                  <div className="card-body py-3">
                      <i className="bi bi-exclamation-triangle fs-3 text-danger mb-2"></i>
                      <h4 className="fw-bold text-danger mb-1">
                          {tenants.filter(t => t.estadoId === 3).length}
                      </h4>
                      <p className="text-muted small mb-0">Suspendidos</p>
                  </div>
              </div>
          </div>

          <div className="col-md-3">
              <div className="card text-center h-100 border-0 shadow-sm">
                  <div className="card-body py-3">
                      <i className="bi bi-slash-circle fs-3 text-warning mb-2"></i>
                      <h4 className="fw-bold text-warning mb-1">
                          {tenants.filter(t => t.estadoId === 4).length}
                      </h4>
                      <p className="text-muted small mb-0">Bloqueados</p>
                  </div>
              </div>
          </div>
      </div>
  );

  const renderListaTenants = () => {
    return (
      <div className="table-responsive">
        <table className="table table-hover text-center align-middle">
          <thead className="table-light">
            <tr>
              <th>Empresa</th>
              <th>Contacto</th>
              <th>Plan</th>
              <th>Estado</th>
              <th>Usuarios disponibles</th>
              <th>Pedidos/mes</th>
              <th>Pedidos simultáneos</th>
              <th>Días activo</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {tenantsFiltrados.map(tenant => {
              const planCompleto = obtenerPlanPorId(tenant.planId);
              const diasDesdeCreacion = calcularDiasDesdeCreacion(tenant.fechaCreacion);
              
              return (
                <tr key={tenant.id}>
                  <td>
                    <div className="d-flex align-items-center justify-content-start">
                      <div 
                        className="rounded-circle me-2 d-flex align-items-center justify-content-center"
                        style={{ 
                          width: '40px', 
                          height: '40px', 
                          backgroundColor: `${obtenerColorPlan(tenant.planId)}20`,
                          color: obtenerColorPlan(tenant.planId)
                        }}
                      >
                        <i className={`bi ${obtenerIconoPlan(tenant.planId)}`}></i>
                      </div>
                      <div className="text-start">
                        <div className="fw-semibold">{tenant.nombreEmpresa}</div>
                        <small className="text-muted">ID: {tenant.id}</small>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div className="text-start">
                      <div className="fw-semibold">{tenant.emailContacto}</div>
                      <small className="text-muted">
                        Creado: {formatearFecha(tenant.fechaCreacion)}
                      </small>
                    </div>
                  </td>
                  <td>
                    <span 
                      className="badge"
                      style={{ 
                        backgroundColor: `${obtenerColorPlan(tenant.planId)}20`,
                        color: obtenerColorPlan(tenant.planId)
                      }}
                    >
                      {planNombre[tenant.planId] || tenant.planNombre}
                    </span>
                  </td>
                  <td>
                    <span 
                      className="badge"
                      style={{ 
                        backgroundColor: `${obtenerColorEstado(tenant.estadoId)}20`,
                        color: obtenerColorEstado(tenant.estadoId)
                      }}
                    >
                      {tenant.estadoNombre}
                    </span>
                  </td>
                  <td>
                    <div className="text-center">
                      <div className="fw-bold">
                        {planCompleto.limiteUsuarios === 0 ? 
                          'Ilimitado' : 
                          planCompleto.limiteUsuarios
                        }
                      </div>
                      <small className="text-muted">usuarios</small>
                    </div>
                  </td>
                  <td>
                    <div className="text-center">
                      <div className="fw-bold">
                        {planCompleto.limitePedidosMes === 0 ? 
                          'Ilimitado' : 
                          planCompleto.limitePedidosMes
                        }
                      </div>
                      <small className="text-muted">pedidos</small>
                    </div>
                  </td>
                  <td>
                    <div className="text-center">
                      <div className="fw-bold">
                        {planCompleto.limitePedidosSimultaneos === 0 ? 
                          'Ilimitado' : 
                          planCompleto.limitePedidosSimultaneos
                        }
                      </div>
                      <small className="text-muted">simultáneos</small>
                    </div>
                  </td>
                  <td>
                    <div className="text-center">
                      <div className="fw-semibold">
                        {diasDesdeCreacion} {diasDesdeCreacion === 1 ? 'día' : 'días'}
                      </div>
                      <small className="text-muted">
                        desde {formatearFecha(tenant.fechaCreacion)}
                      </small>
                    </div>
                  </td>
                  <td>
                    <div className="btn-group btn-group-sm">
                      <button 
                        className="btn btn-outline-primary" 
                        onClick={() => handleEditar(tenant)}
                        title="Editar"
                      >
                        <i className="bi bi-pencil"></i>
                      </button>
                      
                      {tenant.idAdminMensajeria ? (
                      <button
                        className="btn btn-outline-orange"
                        onClick={() => handleDesasociarAdmin(tenant)}
                        title="Desasociar Administrador"
                      >
                        <i className="bi bi-person-dash"></i>
                      </button>
                    ) : (
                      <button
                        className="btn btn-outline-info"
                        onClick={() => handleAsociarAdmin(tenant)}
                        title="Asociar Administrador"
                      >
                        <i className="bi bi-person-plus"></i>
                      </button>
                    )}

                      {tenant.estadoId === 1 ? ( 
                        <button 
                          className="btn btn-outline-warning"
                          onClick={() => handleDesactivar(tenant.id)}
                          title="Desactivar"
                        >
                          <i className="bi bi-pause"></i>
                        </button>
                      ) : (
                        <button 
                          className="btn btn-outline-success"
                          onClick={() => handleActivar(tenant.id)}
                          title="Activar"
                        >
                          <i className="bi bi-play"></i>
                        </button>
                      )}
                      <button 
                        className="btn btn-outline-danger"
                        onClick={() => handleEliminar(tenant.id)}
                        title="Eliminar"
                      >
                        <i className="bi bi-trash"></i>
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
  };

  const renderModalAsociarAdmin = () => {
    return (
      <div className={`modal fade ${modalAdminVisible ? 'show' : ''}`} 
          style={{ display: modalAdminVisible ? 'block' : 'none' }}
          tabIndex="-1">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">
                <i className="bi bi-person-plus me-2"></i>
                Asociar Administrador
              </h5>
              <button 
                type="button" 
                className="btn-close" 
                onClick={() => setModalAdminVisible(false)}
              ></button>
            </div>
            <div className="modal-body">
              {tenantParaAsociar && (
                <div className="mb-3">
                  <h6 className="text-muted">Tenant seleccionado:</h6>
                  <div className="d-flex align-items-center">
                    <div 
                      className="rounded-circle me-2 d-flex align-items-center justify-content-center"
                      style={{ 
                        width: '30px', 
                        height: '30px', 
                        backgroundColor: `${obtenerColorPlan(tenantParaAsociar.planId)}20`,
                        color: obtenerColorPlan(tenantParaAsociar.planId)
                      }}
                    >
                      <i className={`bi ${obtenerIconoPlan(tenantParaAsociar.planId)}`}></i>
                    </div>
                    <strong>{tenantParaAsociar.nombreEmpresa}</strong>
                  </div>
                </div>
              )}
              
              <div className="mb-3">
                <label className="form-label">Seleccionar Administrador:</label>
                <select 
                  className="form-select" 
                  value={adminSeleccionado}
                  onChange={(e) => setAdminSeleccionado(e.target.value)}
                >
                  <option value="" disabled hidden>Selecciona un administrador</option>
                  {administradores.map(admin => (
                    <option key={admin.id} value={admin.id}>
                      {admin.nombres} {admin.apellidos} - {admin.email}
                    </option>
                  ))}
                </select>
              </div>
              
              {administradores.length === 0 && (
                <div className="alert alert-info">
                  <i className="bi bi-info-circle me-2"></i>
                  No hay administradores disponibles para asociar.
                </div>
              )}
            </div>
            <div className="modal-footer">
              <button 
                type="button" 
                className="btn btn-primary"
                onClick={confirmarAsociacion}
                disabled={!adminSeleccionado}
              >
                <i className="bi bi-check-lg me-2"></i>
                Asociar
              </button>
              <button 
                type="button" 
                className="btn btn-secondary" 
                onClick={() => setModalAdminVisible(false)}
              >
                <i className="bi bi-x-circle me-1"></i>
                Cancelar
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  };

  if (loading) {
    return (
      <>
        <div className="container py-4">
          <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
            <i className="bi bi-building me-2" style={{ color: '#6f42c1' }}></i>
            Gestión de tenants
          </h3>
          <div className="text-center mt-5">
            <div className="spinner-border text-primary" role="status" />
            <p className="mt-3">Cargando tenants...</p>
          </div>
        </div>
        <Footer />
      </>
    );
  }

  return (
    <>
      <div className="container py-4">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
            <i className="bi bi-building me-2" style={{ color: '#6f42c1' }}></i>
            Gestión de tenants
          </h3>
          <div className="d-flex align-items-center gap-3">
            <div className="text-muted">
              <i className="bi bi-info-circle me-1"></i>
              Mostrando {tenantsFiltrados.length} de {tenants.length} tenants
            </div>
            <button
              onClick={handleNuevo}
              className="btn btn-success"
            >
              <i className="bi bi-plus-lg me-2"></i>
              Nuevo tenant
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
                  <i className="bi bi-search me-2" style={{ color: '#6c757d' }}></i>
                  Buscar tenant
                </label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Buscar por empresa o email"
                  value={filtroNombre}
                  onChange={(e) => setFiltroNombre(e.target.value)}
                />
              </div>
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-box me-2" style={{ color: '#6c757d' }}></i>
                  Filtrar por plan
                </label>
                <select
                  value={filtroPlan}
                  onChange={(e) => setFiltroPlan(e.target.value)}
                  className="form-select"
                >
                  <option value="" disabled hidden>Todos los planes</option>
                  {planes.map(plan => (
                    <option key={plan.id} value={plan.id}>
                      {planNombre[plan.id] || plan.nombre}
                    </option>
                  ))}
                </select>
              </div>
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-check-circle me-2" style={{ color: '#6c757d' }}></i>
                  Filtrar por estado
                </label>
                <select
                  value={filtroEstado}
                  onChange={(e) => setFiltroEstado(e.target.value)}
                  className="form-select"
                >
                  <option value="" disabled hidden>Todos los estados</option>
                  {estados.map(estado => (
                    <option key={estado.id} value={estado.id}>
                      {estadoNombre[estado.id] || estado.nombre}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="row justify-content-end">
              <div className="col-md-6 d-flex justify-content-end align-items-center">
                {(filtroNombre || filtroPlan || filtroEstado) && (
                  <button
                    className="btn btn-outline-secondary"
                    onClick={limpiarFiltros}
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
              <i className="bi bi-building me-2"></i>
              {editingTenant?.id ? 'Editar tenant' : 'Nuevo tenant'}
            </div>
            <div className="card-body">
              <form onSubmit={handleGuardar}>
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-building me-1" style={{ color: '#6f42c1' }}></i>
                      Nombre de la empresa <span className="text-danger">*</span>
                    </label>
                    <input
                      type="text"
                      name="nombreEmpresa"
                      value={editingTenant?.nombreEmpresa || ''}
                      onChange={handleInputChange}
                      className={`form-control ${errores.nombreEmpresa ? 'is-invalid' : ''}`}
                      placeholder="Nombre de la empresa"
                    />
                    {errores.nombreEmpresa && (
                      <div className="invalid-feedback">{errores.nombreEmpresa}</div>
                    )}
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-envelope me-1" style={{ color: '#007bff' }}></i>
                      Email de contacto <span className="text-danger">*</span>
                    </label>
                    <input
                      type="email"
                      name="emailContacto"
                      value={editingTenant?.emailContacto || ''}
                      onChange={handleInputChange}
                      className={`form-control ${errores.emailContacto ? 'is-invalid' : ''}`}
                      placeholder="correo@empresa.com"
                    />
                    {errores.emailContacto && (
                      <div className="invalid-feedback">{errores.emailContacto}</div>
                    )}
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-award me-1" style={{ color: '#6f42c1' }}></i>
                      Plan <span className="text-danger">*</span>
                    </label>
                    <select
                      name="planId"
                      value={editingTenant?.planId || ''}
                      onChange={handleInputChange}
                      className={`form-select ${errores.planId ? 'is-invalid' : ''}`}
                    >
                      <option value="" disabled hidden>Seleccione un plan</option>
                      {planes.map(plan => (
                        <option key={plan.id} value={plan.id}>
                          {planNombre[plan.nombre] || plan.nombre}
                        </option>
                      ))}
                    </select>
                    {errores.planId && (
                      <div className="invalid-feedback">{errores.planId}</div>
                    )}
                  </div>
                </div>

                <div className="d-flex justify-content-end gap-2">
                  <button type="submit" className="btn btn-primary">
                    <i className="bi bi-save me-1"></i> Guardar
                  </button>
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={handleCancelar}
                  >
                   <i className="bi bi-x-circle me-1"></i>
                    Cancelar
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}


        {tenantsFiltrados.length === 0 ? (
        <div className="text-center py-5">
            <i className="bi bi-inbox" style={{ fontSize: '4rem', color: '#dee2e6' }}></i>
            <h4 className="text-muted mt-3">
            {tenants.length === 0 ? 'No hay tenants registrados' : 'No se encontraron tenants'}
            </h4>
            <p className="text-muted">
            {tenants.length === 0 
                ? 'Aún no se han registrado tenants en el sistema.' 
                : 'Intenta ajustar los filtros de búsqueda. No se encontraron tenants con los criterios seleccionados.'
            }
            </p>
        </div>

        ) : vista === 'lista' ? (
        renderListaTenants()
        ) : vista === 'tarjetas' ? (
        renderTarjetasTenants()
        ) : (
        <>
        {renderEstadisticas()}
        </>
        )}
      </div>      
      <Footer />

      {renderModalAsociarAdmin()}
      {modalAdminVisible && (
        <div className="modal-backdrop fade show"></div>
      )}
    </>
  );
}