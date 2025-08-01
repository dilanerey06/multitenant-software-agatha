import { useState, useEffect } from 'react';
import axios from 'axios';
import superImg from '../../images/uis.jpeg';
import Footer from '../../components/Footer.jsx';

export default function SuperInformacion() {
  const [usuario, setUsuario] = useState(null);
  const [form, setForm] = useState({
    email: '',
    confirmEmail: '',
    nombreUsuario: '',
    confirmNombre: '',
    password: '',
    confirmPassword: ''
  });
  const [errores, setErrores] = useState({});
  const [loading, setLoading] = useState(true);
  const [mensaje, setMensaje] = useState(null);
  const [modoEdicion, setModoEdicion] = useState(false);
  const [mostrarPassword, setMostrarPassword] = useState(false);
  const [mostrarConfirmPassword, setMostrarConfirmPassword] = useState(false);

  const token = localStorage.getItem('token');
  const headers = { Authorization: `Bearer ${token}` };

  useEffect(() => {
    axios.get('/proxy/api/auth/me', { headers })
      .then(res => {
        const userData = res.data.data;
        setUsuario(userData);
        setForm(prev => ({
          ...prev,
          email: userData.email || '',
          nombreUsuario: userData.nombreUsuario || '',
          confirmEmail: '',
          confirmNombre: '',
          password: '',
          confirmPassword: ''
        }));
      })
      .catch(error => {
        console.error('Error al cargar usuario:', error);
        const errorMessage = error.response?.data?.error || 'Error al cargar la información del usuario';
        setMensaje(errorMessage);
      })
      .finally(() => setLoading(false));
  }, []);

  const validarCampo = (name, value) => {
    if (!value.trim()) return 'Este campo es obligatorio';
    if (name === 'email' || name === 'confirmEmail') {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(value)) return 'Email no es válido';
    }
    if (name === 'nombreUsuario' && value.length < 3) {
      return 'El nombre de usuario debe tener al menos 3 caracteres';
    }
    return '';
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    setErrores(prev => ({ ...prev, [name]: validarCampo(name, value) }));
    setMensaje(null);
  };

  const validarFormulario = () => {
    const nuevosErrores = {};

    if (form.email !== usuario.email) {
      const err = validarCampo('email', form.email);
      if (err) nuevosErrores.email = err;
      if (form.confirmEmail !== form.email) {
        nuevosErrores.confirmEmail = 'Los emails no coinciden';
      }
    }

    if (form.nombreUsuario !== usuario.nombreUsuario) {
      const err = validarCampo('nombreUsuario', form.nombreUsuario);
      if (err) nuevosErrores.nombreUsuario = err;
      if (form.confirmNombre !== form.nombreUsuario) {
        nuevosErrores.confirmNombre = 'Los nombres no coinciden';
      }
    }

    if (form.password.trim()) {
      if (form.password.length < 6) {
        nuevosErrores.password = 'La contraseña debe tener al menos 6 caracteres';
      }
      if (form.password !== form.confirmPassword) {
        nuevosErrores.confirmPassword = 'Las contraseñas no coinciden';
      }
    }

    setErrores(nuevosErrores);
    console.log("Errores de validación:", nuevosErrores);
    return Object.keys(nuevosErrores).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("Intentando guardar cambios...");

    const cambiosRealizados =
      form.email !== usuario.email ||
      form.nombreUsuario !== usuario.nombreUsuario ||
      form.password.trim();

    if (!cambiosRealizados) {
      setMensaje("No hiciste ningún cambio.");
      return;
    }

    if (!validarFormulario()) {
      setMensaje("Revisa los errores del formulario.");
      return;
    }

    const cambiosSensibles =
      form.email !== usuario.email || form.nombreUsuario !== usuario.nombreUsuario|| form.password.trim();

    if (cambiosSensibles) {
      const confirmar = window.confirm(
        'Has cambiado tu email, usuario o contraseña. Por seguridad deberás volver a iniciar sesión. ¿Deseas continuar?'
      );

      if (!confirmar) {
        setMensaje('Cambios cancelados por el usuario.');
        return; 
      }
    }

    try {
      const dataEnviar = {
        email: form.email,
        nombreUsuario: form.nombreUsuario,
        ...(form.password.trim() && { password: form.password })
      };

      const response = await axios.put('/proxy/api/auth/me', dataEnviar, { headers });

      if (response.data.success) {
        setMensaje(response.data.message || 'Usuario actualizado correctamente');

        if (cambiosSensibles) {
          setTimeout(() => {
            localStorage.removeItem('token');
            window.location.href = '/login';
          }, 1500);
        } else {
          setUsuario(response.data.data);
          setModoEdicion(false);
          setForm(prev => ({
            ...prev,
            email: response.data.data.email,
            nombreUsuario: response.data.data.nombreUsuario,
            confirmEmail: '',
            confirmNombre: '',
            password: '',
            confirmPassword: ''
          }));
        }
      } else {
        setMensaje(response.data.error || 'Error al actualizar usuario');
      }
    } catch (error) {
      console.error('Error al actualizar usuario:', error);
      let errorMessage = 'Error al actualizar usuario';
      
      if (error.response?.data?.error) {
        errorMessage = error.response.data.error;
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      setMensaje(errorMessage);
    }
  };

  const cancelarEdicion = () => {
    setModoEdicion(false);
    setErrores({});
    setMensaje(null);
    setForm(prev => ({
      ...prev,
      email: usuario.email,
      nombreUsuario: usuario.nombreUsuario,
      confirmEmail: '',
      confirmNombre: '',
      password: '',
      confirmPassword: ''
    }));
    setMostrarPassword(false);
    setMostrarConfirmPassword(false);
  };

  if (loading) return <div className="p-4">Cargando información...</div>;
  if (!usuario) return <div className="p-4 text-danger">No se pudo cargar el usuario.</div>;

  const fechaCreacion = new Date(usuario.fechaCreacion).toLocaleDateString('es-ES', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });

  return (
    <>
      <main className="d-flex justify-content-center py-5">
        <div className="card shadow p-5" style={{ maxWidth: '600px', width: '100%', backgroundColor: '#f9f9f9', borderRadius: '25px' }}>
          <div className="text-center mb-4">
            <h3 className="fw-bold">¡Hola, {usuario.nombres} {usuario.apellidos}!</h3>
          </div>

          <div className="text-center">
            <img
              src={superImg}
              alt="Perfil"
              style={{
                width: '130px',
                height: '130px',
                borderRadius: '50%',
                objectFit: 'cover',
                border: '3px solid #ccc',
                backgroundColor: 'white'
              }}
            />
          </div>

          <div className="d-flex justify-content-end mb-3">
            <span
              role="button"
              title="Editar información"
              onClick={() => setModoEdicion(!modoEdicion)}
              className="fs-4"
              style={{ cursor: 'pointer' }}
            >
               <i className="bi bi-pencil-fill"></i> 
            </span>
          </div>

          {!modoEdicion ? (
            <>
              <p>
                <i className="bi bi-envelope me-2" style={{ color: '#007bff' }}></i>
                <strong>Email:</strong> {usuario.email}
              </p>
              <p>
                <i className="bi bi-person me-1" style={{ color: '#6f42c1' }}></i>
                <strong>Usuario:</strong> {usuario.nombreUsuario}
              </p>
              <p>
                <i className="bi bi-lock-fill me-2" style={{ color: '#FFD700' }}></i>
                <strong>Contraseña:</strong> ********
              </p>
            </>
          ) : (
            <form onSubmit={handleSubmit} noValidate>
              <div className="mb-3">
                <label className="form-label">
                  <i className="bi bi-envelope me-1" style={{ color: '#007bff' }}></i>
                  Email
                </label>
                <input 
                  type="email" 
                  name="email" 
                  value={form.email} 
                  onChange={handleChange}
                  className={`form-control ${errores.email ? 'is-invalid' : ''}`} 
                />
                {errores.email && <div className="invalid-feedback">{errores.email}</div>}
              </div>
              
              {form.email !== usuario.email && (
                <div className="mb-3">
                  <label className="form-label">
                    <i className="bi bi-envelope-check me-1" style={{ color: '#28a745' }}></i>
                    Confirmar nuevo email
                  </label>
                  <input 
                    type="email" 
                    name="confirmEmail" 
                    value={form.confirmEmail} 
                    onChange={handleChange}
                    className={`form-control ${errores.confirmEmail ? 'is-invalid' : ''}`} 
                  />
                  {errores.confirmEmail && <div className="invalid-feedback">{errores.confirmEmail}</div>}
                </div>
              )}

              <div className="mb-3">
                <label className="form-label">
                  <i className="bi bi-person me-1" style={{ color: '#6f42c1' }}></i>
                  Nombre de usuario
                </label>
                <input 
                  type="text" 
                  name="nombreUsuario" 
                  value={form.nombreUsuario} 
                  onChange={handleChange}
                  className={`form-control ${errores.nombreUsuario ? 'is-invalid' : ''}`} 
                />
                {errores.nombreUsuario && <div className="invalid-feedback">{errores.nombreUsuario}</div>}
              </div>
              
              {form.nombreUsuario !== usuario.nombreUsuario && (
                <div className="mb-3">
                  <label className="form-label">
                    <i className="bi bi-person-check me-1" style={{ color: '#5a32a3' }}></i>
                    Confirmar nuevo nombre de usuario
                  </label>
                  <input 
                    type="text" 
                    name="confirmNombre" 
                    value={form.confirmNombre} 
                    onChange={handleChange}
                    className={`form-control ${errores.confirmNombre ? 'is-invalid' : ''}`} 
                  />
                  {errores.confirmNombre && <div className="invalid-feedback">{errores.confirmNombre}</div>}
                </div>
              )}

              <div className="mb-3">
                <label className="form-label">
                  <i className="bi bi-key me-1" style={{ color: '#fd7e14' }}></i>
                  Nueva contraseña (opcional)
                </label>
                <div className="input-group">
                  <input
                    type={mostrarPassword ? 'text' : 'password'}
                    name="password"
                    value={form.password}
                    onChange={handleChange}
                    className={`form-control ${errores.password ? 'is-invalid' : ''}`}
                  />
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={() => setMostrarPassword(!mostrarPassword)}
                    tabIndex={-1}
                  >
                    <i className={`bi ${mostrarPassword ? 'bi-eye-slash' : 'bi-eye'}`}></i>
                  </button>
                </div>
                {errores.password && <div className="invalid-feedback d-block">{errores.password}</div>}
              </div>

              <div className="mb-3">
                <label className="form-label">
                  <i className="bi bi-key-fill me-1" style={{ color: '#dc3545' }}></i>
                  Confirmar contraseña
                </label>
                <div className="input-group">
                  <input
                    type={mostrarConfirmPassword ? 'text' : 'password'}
                    name="confirmPassword"
                    value={form.confirmPassword}
                    onChange={handleChange}
                    className={`form-control ${errores.confirmPassword ? 'is-invalid' : ''}`}
                  />
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={() => setMostrarConfirmPassword(!mostrarConfirmPassword)}
                    tabIndex={-1}
                  >
                    <i className={`bi ${mostrarConfirmPassword ? 'bi-eye-slash' : 'bi-eye'}`}></i>
                  </button>
                </div>
                {errores.confirmPassword && <div className="invalid-feedback d-block">{errores.confirmPassword}</div>}
              </div>

              <div className="d-flex justify-content-between">
                <button type="submit" className="btn btn-success">Guardar</button>
                <button type="button" className="btn btn-secondary" onClick={cancelarEdicion}>
                  Cancelar
                </button>
              </div>
            </form>
          )}

          {mensaje && (
            <div className={`alert mt-3 ${mensaje.includes('Error') || mensaje.includes('error') ? 'alert-danger' : 'alert-success'}`}>
              <i className={`bi ${mensaje.includes('Error') || mensaje.includes('error') ? 'bi-exclamation-triangle' : 'bi-check-circle'} me-2`}></i>
              {mensaje}
            </div>
          )}
        </div>
      </main>
      <Footer />
    </>
  );
}