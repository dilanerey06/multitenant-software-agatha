import { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import Footer from '../../components/Footer.jsx';

const rolNombre = {
  SUPER_ADMIN: 'Super Administrador',
  ADMIN_MENSAJERIA: 'Administrador',
  OPERADOR: 'Operador',
  MENSAJERO: 'Mensajero'
};

const estadoNombre = {
  activo: 'Activo',
  inactivo: 'Inactivo',
  suspendido: 'Suspendido'
};

export default function AdminUsuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [empresas, setEmpresas] = useState([]);
  const [roles, setRoles] = useState([]);
  const [estados, setEstados] = useState([]);
  const [empresaActual, setEmpresaActual] = useState(null); 
  const [formVisible, setFormVisible] = useState(false);
  const [editingUsuario, setEditingUsuario] = useState(null);
  const [errores, setErrores] = useState({});
  const [loading, setLoading] = useState(true);
  const [filtroNombre, setFiltroNombre] = useState('');
  const [filtroRol, setFiltroRol] = useState(''); 
  const [filtroEstado, setFiltroEstado] = useState('');
  const formRef = useRef(null);
  const [usuarioActual, setUsuarioActual] = useState(null);
  const [vista, setVista] = useState('lista');

  const token = localStorage.getItem('token');
  const additionalData = localStorage.getItem('x-additional-data');

  const headers = {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
    'x-additional-data': additionalData
  };

  useEffect(() => {
    cargarDatos();
  }, []);

  const [usuarioActualId, setUsuarioActualId] = useState(null);

  const usuariosFiltrados = usuarios.filter(usuario => {
    const coincideNombre = usuario.nombreUsuario.toLowerCase().includes(filtroNombre.toLowerCase()) ||
                          usuario.email.toLowerCase().includes(filtroNombre.toLowerCase()) ||
                          (usuario.nombres && usuario.nombres.toLowerCase().includes(filtroNombre.toLowerCase())) ||
                          (usuario.apellidos && usuario.apellidos.toLowerCase().includes(filtroNombre.toLowerCase()));
    
    const coincideRol = filtroRol === '' || 
                       (usuario.rolId && usuario.rolId.toString() === filtroRol);
    
    const coincideEstado = filtroEstado === '' || 
                          (usuario.estadoId && usuario.estadoId.toString() === filtroEstado);
    
    return coincideNombre && coincideRol && coincideEstado;
  });

  const cargarDatos = async () => {
    setLoading(true);
    try {
      const [usuariosRes, empresasRes, rolesRes, estadosRes, meRes] = await Promise.all([
        axios.get('/proxy/api/usuarios', { headers }),
        axios.get('/proxy/api/empresas-mensajeria', { headers }),
        axios.get('/proxy/api/roles', { headers }),
        axios.get('/proxy/api/estados-general', { headers }),
        axios.get('/proxy/api/auth/me', { headers })
      ]);

      const usuarioActual = meRes.data.data;
      setUsuarioActual(usuarioActual);
      setUsuarioActualId(usuarioActual.id);

      if (usuarioActual.mensajeriaId) {
        const empresaActual = empresasRes.data.data?.find(emp => emp.id === usuarioActual.mensajeriaId);
        setEmpresaActual(empresaActual);
      }

      const usuariosFiltrados = (usuariosRes.data.data || []).filter(usuario => {
        return usuario.id !== usuarioActual.id &&
              usuario.mensajeriaId === usuarioActual.mensajeriaId;
      });

      setUsuarios(usuariosFiltrados);

      if (empresasRes.data.success === true) {
        setEmpresas(empresasRes.data.data);
      } else {
        console.error('Error al cargar empresas:', empresasRes.data);
      }

      if (rolesRes.data.success === true) {
        const rolesPermitidos = rolesRes.data.data.filter(rol => 
          rol.nombre === 'SUPER_ADMIN' ||
          rol.nombre === 'ADMIN_MENSAJERIA' || 
          rol.nombre === 'OPERADOR' || 
          rol.nombre === 'MENSAJERO'
        );
        setRoles(rolesPermitidos);
      } else {
        console.error('Error al cargar roles:', rolesRes.data);
      }

      if (estadosRes.data.success === true) {
        setEstados(estadosRes.data.data);
      } else {
        console.error('Error al cargar estados:', estadosRes.data);
      }

    } catch (error) {
      console.error('Error al cargar datos:', error);
    } finally {
      setLoading(false);
    }
  };

  const validarCampo = (name, value) => {
    let error = '';
    if (!value || !value.toString().trim()) {
      error = 'Este campo es obligatorio';
    } else {
      if (name === 'email') {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
          error = 'Email no es válido';
        }
      }
      if (name === 'nombreUsuario') {
        if (value.length < 3) {
          error = 'El nombre de usuario debe tener al menos 3 caracteres';
        }
      }
      if (name === 'nombres') {
        if (value.length < 2) {
          error = 'Los nombres deben tener al menos 2 caracteres';
        }
      }
      if (name === 'apellidos') {
        if (value.length < 2) {
          error = 'Los apellidos deben tener al menos 2 caracteres';
        }
      }
    }
    return error;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    if (name === 'rolId') {
      setEditingUsuario(prev => ({ ...prev, rolId: Number(value) }));
      setErrores(prev => ({
        ...prev,
        rolId: value ? '' : 'Debe seleccionar un rol'
      }));
    } else if (name === 'estadoId') {
      setEditingUsuario(prev => ({ ...prev, estadoId: Number(value) }));
      setErrores(prev => ({
        ...prev,
        estadoId: value ? '' : 'Debe seleccionar un estado'
      }));
    } else {
      setEditingUsuario(prev => ({ ...prev, [name]: value }));
      const errorCampo = validarCampo(name, value);
      setErrores(prev => ({ ...prev, [name]: errorCampo }));
    }
  };

  const validarFormulario = () => {
    const erroresValidacion = {};
    ['email', 'nombreUsuario', 'nombres', 'apellidos', 'rolId', 'estadoId'].forEach(campo => {
      let valor = editingUsuario?.[campo];
      const error = validarCampo(campo, valor);
      if (error) erroresValidacion[campo] = error;
    });
    setErrores(erroresValidacion);
    return Object.keys(erroresValidacion).length === 0;
  };

  const handleNuevo = () => {
    setEditingUsuario({
      nombreUsuario: '',
      nombres: '',
      apellidos: '',
      email: '',
      rolId: '',
      estadoId: '',
      mensajeriaId: empresaActual?.id || ''
    });
    setFormVisible(true);
    setErrores({});
    setTimeout(() => {
      formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  };

  const handleEditar = (usuario) => {
    const usuarioSinPass = { ...usuario };
    delete usuarioSinPass.password;
    setEditingUsuario(usuarioSinPass);
    setFormVisible(true);
    setErrores({});
    setTimeout(() => {
      formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  };

  const handleEliminar = (id) => {
    if (confirm('¿Eliminar usuario?')) {
      axios.delete(`/proxy/api/usuarios/${id}`, { headers })
        .then(() => {
          cargarDatos();
        })
        .catch(err => console.error('Error al eliminar usuario:', err));
    }
  };

  const handleGuardar = async (e) => {
    e.preventDefault();
    if (!validarFormulario()) return;

    try {
      let usuarioEnviar = { 
        ...editingUsuario,
        mensajeriaId: usuarioActual?.mensajeriaId 
      };

      const url = usuarioEnviar.id
        ? `/proxy/api/usuarios/${usuarioEnviar.id}`
        : '/proxy/api/usuarios';

      const method = usuarioEnviar.id ? axios.put : axios.post;

      await method(url, usuarioEnviar, { headers });

      setFormVisible(false);
      setEditingUsuario(null);
      setErrores({});

      if (usuarioEnviar.id) {
        alert('Usuario actualizado correctamente.');
      } else {
        alert('Usuario creado correctamente. La contraseña fue enviada al correo registrado.');
      }

      cargarDatos();

    } catch (error) {
      console.error('Error al guardar usuario:', error);
      alert(`Error al guardar usuario: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleResetPassword = async (id) => {
    if (confirm('¿Restablecer contraseña? Se generará una contraseña nueva y se enviará al correo.')) {
      try {
        await axios.post(`/proxy/api/usuarios/${id}/reset-password`, {}, { headers });
        alert('Contraseña restablecida y enviada por correo.');
      } catch (error) {
        console.error('Error al restablecer contraseña:', error);
        alert('Error al restablecer contraseña.');
      }
    }
  };

  const handleCancelar = () => {
    setFormVisible(false);
    setEditingUsuario(null);
    setErrores({});
  };

  const limpiarFiltros = () => {
    setFiltroNombre('');
    setFiltroRol('');
    setFiltroEstado('');
  };

  const obtenerNombreRol = (rolId) => {
    const rol = roles.find(r => r.id === rolId);
    if (rol) {
      return rolNombre[rol.nombre] || rol.nombre;
    }
    return 'Sin rol';
  };

  const obtenerNombreEstado = (estadoId) => {
    const estado = estados.find(e => e.id === estadoId);
    if (estado) {
      return estadoNombre[estado.nombre] || estado.nombre
    }
    return 'Sin estado';
  };

  const obtenerIconoRol = (rolId) => {
    const rol = roles.find(r => r.id === rolId);
    if (rol) {
      switch (rol.nombre) {
        case 'ADMIN_MENSAJERIA':
          return 'bi-person-gear';
        case 'OPERADOR':
          return 'bi-headset';
        case 'MENSAJERO':
          return 'bi-truck';
        default:
          return 'bi-person';
      }
    }
    return 'bi-person';
  };

  const obtenerColorRol = (rolId) => {
    const rol = roles.find(r => r.id === rolId);
    if (rol) {
      switch (rol.nombre) {
        case 'ADMIN_MENSAJERIA':
          return '#dc3545';
        case 'OPERADOR':
          return '#0d6efd';
        case 'MENSAJERO':
          return '#198254';
        default:
          return '#6c757d';
      }
    }
    return '#6c757d';
  };

  const renderTarjetasUsuarios = () => (
    <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      {usuariosFiltrados.map(usuario => (
        <div className="col" key={usuario.id}>
          <div className="card h-100 shadow-sm">
            <div className="card-body text-center">
              <div className="mb-3">
                <div 
                  className="rounded-circle mx-auto d-flex align-items-center justify-content-center"
                  style={{ 
                    width: '80px', 
                    height: '80px', 
                    backgroundColor: '#f8f9fa',
                    border: `3px solid ${obtenerColorRol(usuario.rolId)}`,
                    fontSize: '2rem',
                    color: obtenerColorRol(usuario.rolId)
                  }}
                >
                  <i className={`bi ${obtenerIconoRol(usuario.rolId)}`}></i>
                </div>
              </div>
              <h5 className="card-title mb-2">{usuario.nombreUsuario}</h5>
              
              {(usuario.nombres || usuario.apellidos) && (
                <div className="mb-2 text-start">
                  <div className="text-muted small">
                    <i className="bi bi-person me-1" style={{ color: '#28a745' }}></i>
                    Nombre completo:
                  </div>
                  <div className="fw-semibold">{`${usuario.nombres || ''} ${usuario.apellidos || ''}`.trim()}</div>
                </div>
              )}

              <div className="mb-2 text-start">
                <div className="text-muted small">
                  <i className="bi bi-envelope me-1" style={{ color: '#007bff' }}></i>
                  Email:
                </div>
                <div className="fw-semibold text-primary">{usuario.email}</div>
              </div>

              <div className="mb-2 text-start">
                <div className="text-muted small">
                  <i className={`bi ${obtenerIconoRol(usuario.rolId)} me-1`} style={{ color: obtenerColorRol(usuario.rolId) }}></i>
                  Rol:
                </div>
                <div style={{ color: obtenerColorRol(usuario.rolId) }}>
                  {obtenerNombreRol(usuario.rolId)}
                </div>
              </div>

              <div className="mb-2 text-start">
                <div className="text-muted small">
                  <i className="bi bi-check-circle me-1" style={{ color: '#17a2b8' }}></i>
                  Estado:
                </div>
                <div>{obtenerNombreEstado(usuario.estadoId)}</div>
              </div>

              <div className="mb-2 text-start">
                <div className="text-muted small">
                  <i className="bi bi-calendar-event me-1" style={{ color: '#28a745' }}></i>
                  Se unió el día:
                </div>
                <div>{new Date(usuario.fechaCreacion).toLocaleDateString('es-ES')}</div>
              </div>
                
            </div>
            <div className="card-footer d-flex justify-content-center gap-1 bg-light flex-wrap">
              <button
                className="btn btn-sm btn-outline-primary"
                onClick={() => handleEditar(usuario)}
              >
                <i className="bi bi-pencil-square me-1"></i>Editar
              </button>
              <button
                className="btn btn-sm btn-outline-danger"
                onClick={() => handleEliminar(usuario.id)}
              >
                <i className="bi bi-trash-fill me-1"></i>Eliminar
              </button>
              <button
                className="btn btn-sm btn-outline-success"
                onClick={() => handleResetPassword(usuario.id)}
              >
                <i className="bi bi-key me-1"></i>Reset
              </button>
            </div>
          </div>
        </div>
      ))}
    </div>
  );

  const renderListaUsuarios = () => (
    <div className="table-responsive">
      <table className="table table-hover text-center align-middle">
        <thead className="table-light">
          <tr>
            <th className="text-center">Usuario</th>
            <th className="text-center">Nombre completo</th>
            <th className="text-center">Email</th>
            <th className="text-center">Rol</th>
            <th className="text-center">Estado</th>
            <th className="text-center">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {usuariosFiltrados.map(usuario => (
            <tr key={usuario.id}>
              <td>
                <div className="d-flex justify-content-center align-items-center">
                  <div className="avatar-circle me-3">
                    <i 
                      className={`bi ${obtenerIconoRol(usuario.rolId)} fs-5`} 
                      style={{ color: obtenerColorRol(usuario.rolId) }}
                    ></i>
                  </div>
                  <div className="text-start">
                    <div className="fw-semibold">{usuario.nombreUsuario}</div>
                    <small className="text-muted">ID: {usuario.id}</small>
                  </div>
                </div>
              </td>
              <td>{`${usuario.nombres || ''} ${usuario.apellidos || ''}`.trim() || '-'}</td>
              <td>{usuario.email}</td>
              <td>
                <span 
                  className="badge bg-opacity-10"
                  style={{ 
                    backgroundColor: `${obtenerColorRol(usuario.rolId)}20`,
                    color: obtenerColorRol(usuario.rolId)
                  }}
                >
                  {obtenerNombreRol(usuario.rolId)}
                </span>
              </td>
              <td>
                <span className={`badge bg-${usuario.estadoId === 1 ? 'success' : 'danger'} bg-opacity-10 text-${usuario.estadoId === 1 ? 'success' : 'danger'}`}>
                  {obtenerNombreEstado(usuario.estadoId)}
                </span>
              </td>
              <td>
                <div className="btn-group btn-group-sm justify-content-center">
                  <button 
                    className="btn btn-outline-primary" 
                    onClick={() => handleEditar(usuario)}
                  >
                    <i className="bi bi-pencil"></i>
                  </button>
                  <button 
                    className="btn btn-outline-danger"
                    onClick={() => handleEliminar(usuario.id)}
                  >
                    <i className="bi bi-trash"></i>
                  </button>
                  <button 
                    className="btn btn-outline-success"
                    onClick={() => handleResetPassword(usuario.id)}
                  >
                    <i className="bi bi-key"></i>
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );


  if (loading) {
    return (
      <>
      <div className="container py-4">
        <h3 className="fw-fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
          <i className="bi bi-people me-2" style={{ color: '#6f42c1' }}></i>
          Gestión de usuarios
          {empresaActual && (
            <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
              <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
              {empresaActual.nombre}
            </span>
          )}
        </h3>
        <div className="text-center mt-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-3">Cargando usuarios...</p>
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
          <i className="bi bi-people me-2" style={{ color: '#6f42c1' }}></i>
          Gestión de usuarios
          {empresaActual && (
            <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
              <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
              {empresaActual.nombre}
            </span>
          )}
        </h3>
        <div className="d-flex align-items-center gap-3">
          <div className="text-muted">
            <i className="bi bi-info-circle me-1"></i>
            Mostrando {usuariosFiltrados.length} de {usuarios.length} usuarios
          </div>
          <button
            onClick={handleNuevo}
            className="btn btn-success"
          >
            <i className="bi bi-plus-lg me-2"></i>
            Nuevo usuario
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
            </div>
          </div>
        </div>
        <div className="card-body">
          <div className="row g-3 mb-3">
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-search me-2" style={{ color: '#6c757d' }}></i>
                Buscar usuario
              </label>
              <input
                type="text"
                className="form-control"
                placeholder="Buscar por nombre, apellido o email"
                value={filtroNombre}
                onChange={(e) => setFiltroNombre(e.target.value)}
              />
            </div>
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-person-badge me-2" style={{ color: '#6c757d' }}></i>
                Filtrar por rol
              </label>
              <select
                value={filtroRol}
                onChange={(e) => setFiltroRol(e.target.value)}
                className="form-select"
              >
                <option value="" disabled hidden>Todos los roles</option>
                {roles
                  .filter(rol => rol.nombre !== 'ADMIN_MENSAJERIA' & rol.nombre !== 'SUPER_ADMIN' )
                  .map(rol => (
                    <option key={rol.id} value={rol.id}>
                      {rolNombre[rol.nombre] || rol.nombre}
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
                    {estadoNombre[estado.nombre] || estado.nombre}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="row justify-content-end">
            <div className="col-md-6 d-flex justify-content-end align-items-center">
              {(filtroNombre || filtroRol || filtroEstado) && (
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
            <i className="bi bi-person-plus me-2"></i>
            {editingUsuario?.id ? 'Editar usuario' : 'Nuevo usuario'}
          </div>
          <div className="card-body">
            <form onSubmit={handleGuardar}>
              <div className="row">
                <div className="col-md-6 mb-3">
                  <label className="form-label">
                    <i className="bi bi-person me-1" style={{ color: '#6f42c1' }}></i>
                    Nombre de usuario <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    name="nombreUsuario"
                    value={editingUsuario?.nombreUsuario || ''}
                    onChange={handleInputChange}
                    className={`form-control ${errores.nombreUsuario ? 'is-invalid' : ''}`}
                    placeholder="Nombre del usuario"
                  />
                  {errores.nombreUsuario && (
                    <div className="invalid-feedback">{errores.nombreUsuario}</div>
                  )}
                </div>

                <div className="col-md-6 mb-3">
                  <label className="form-label">
                    <i className="bi bi-envelope me-1" style={{ color: '#007bff' }}></i>
                    Email <span className="text-danger">*</span>
                  </label>
                  <input
                    type="email"
                    name="email"
                    value={editingUsuario?.email || ''}
                    onChange={handleInputChange}
                    className={`form-control ${errores.email ? 'is-invalid' : ''}`}
                    placeholder="correo@ejemplo.com"
                  />
                  {errores.email && (
                    <div className="invalid-feedback">{errores.email}</div>
                  )}
                </div>
              </div>

              <div className="row">
                <div className="col-md-6 mb-3">
                  <label className="form-label">
                    <i className="bi bi-person-fill me-1" style={{ color: '#28a745' }}></i>
                    Nombres <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    name="nombres"
                    value={editingUsuario?.nombres || ''}
                    onChange={handleInputChange}
                    className={`form-control ${errores.nombres ? 'is-invalid' : ''}`}
                    placeholder="Nombres"
                  />
                  {errores.nombres && (
                    <div className="invalid-feedback">{errores.nombres}</div>
                  )}
                </div>

                <div className="col-md-6 mb-3">
                  <label className="form-label">
                    <i className="bi bi-person-fill me-1" style={{ color: '#28a745' }}></i>
                    Apellidos <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    name="apellidos"
                    value={editingUsuario?.apellidos || ''}
                    onChange={handleInputChange}
                    className={`form-control ${errores.apellidos ? 'is-invalid' : ''}`}
                    placeholder="Apellidos"
                  />
                  {errores.apellidos && (
                    <div className="invalid-feedback">{errores.apellidos}</div>
                  )}
                </div>
              </div>

              <div className="row">
                <div className="col-md-6 mb-3">
                  <label className="form-label">
                    <i className="bi bi-award-fill me-1" style={{ color: '#ff0080' }}></i>
                    Rol <span className="text-danger">*</span>
                  </label>
                  <select
                    name="rolId"
                    value={editingUsuario?.rolId || ''}
                    onChange={handleInputChange}
                    className={`form-select ${errores.rolId ? 'is-invalid' : ''}`}
                  >
                    <option value="" disabled hidden>Seleccionar rol</option>
                    {roles
                      .filter(rol => rol.nombre !== 'ADMIN_MENSAJERIA' & rol.nombre !== 'SUPER_ADMIN' )
                      .map(rol => (
                        <option key={rol.id} value={rol.id}>
                          {rolNombre[rol.nombre] || rol.nombre}
                        </option>
                    ))}
                  </select>
                  {errores.rolId && (
                    <div className="invalid-feedback">{errores.rolId}</div>
                  )}
                </div>

                <div className="col-md-6 mb-3">
                  <label className="form-label">
                    <i className="bi bi-check-circle me-1" style={{ color: '#17a2b8' }}></i>
                    Estado <span className="text-danger">*</span>
                  </label>
                  <select
                    name="estadoId"
                    value={editingUsuario?.estadoId || ''}
                    onChange={handleInputChange}
                    className={`form-select ${errores.estadoId ? 'is-invalid' : ''}`}
                  >
                    {estados.map(estado => (
                      <option key={estado.id} value={estado.id}> 
                        {estadoNombre[estado.nombre] || estado.nombre}
                      </option>
                    ))}
                  </select>
                  {errores.estadoId && (
                    <div className="invalid-feedback">{errores.estadoId}</div>
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
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {usuariosFiltrados.length === 0 ? (
        <div className="text-center py-5">
          <i className="bi bi-inbox" style={{ fontSize: '4rem', color: '#dee2e6' }}></i>
          <h4 className="text-muted mt-3">
            {usuarios.length === 0 ? 'No hay usuarios registrados' : 'No se encontraron usuarios'}
          </h4>
          <p className="text-muted">
            {usuarios.length === 0 
              ? 'Aún no se han registrado usuarios en el sistema.' 
              : 'Intenta ajustar los filtros de búsqueda. No se encontraron usuarios con los criterios seleccionados.'
            }
          </p>
        </div>
      ) : vista === 'lista' ? (
        renderListaUsuarios()
      ) : (
        renderTarjetasUsuarios()
      )}
    </div>
    <Footer />
    </>
  );
}