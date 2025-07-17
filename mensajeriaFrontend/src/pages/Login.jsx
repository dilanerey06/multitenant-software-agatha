import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import logo from '../images/logo.png';
import Footer from '../components/Footer.jsx';

const Login = () => {
  const [formData, setFormData] = useState({ identificador: '', password: '' });
  const [loginSuccess, setLoginSuccess] = useState(false);
  const navigate = useNavigate();
  const { auth, login } = useAuth();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('/courier/api/auth/login', formData);
      // console.log("response.data ",response.data)
      // console.log("response.data.data ",response.data.data)
      // const token = response.data.data.token;
      const resData = response.data.data;
      // agregar esto al localStorage cuando se haga login y eliminarlo del localStorage
      // const additionalData = {
      //   mensajeriaId : response.data.data.mensajeriaId,
      //   tenantId: response.data.data.tenantId
      // }
      // const additionalDataString = JSON.stringify(additionalData);
      console.log(resData)
      login(resData);
      setLoginSuccess(true);
      alert('Login exitoso');
    } catch (error) {
      alert('Credenciales inválidas');
    }
  };

  useEffect(() => {
    
    if (loginSuccess && auth.user && auth.role) {  
      if (auth.role === 'ROLE_SUPER_ADMIN') {
        navigate('/super'); 
      } else if (auth.role === 'ROLE_ADMIN_MENSAJERIA') {
        navigate('/admin');
      } else if (auth.role === 'ROLE_OPERADOR') {
        navigate('/operador');
      } else if (auth.role === 'ROLE_MENSAJERO') {
        navigate('/mensajero');
      } 
    }
  }, [loginSuccess, auth, navigate]);

  return (
    <>
      <div className="login-container">
        <div
          style={{
            background: 'white',
            padding: '2rem',
            borderRadius: '1rem',
            boxShadow: '0 4px 20px rgba(0, 0, 0, 0.1)',
            width: '100%',
            maxWidth: '480px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: '1.5rem',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <h2 style={{ margin: 0, fontSize: '2rem', color: 'var(--color-primary-dark)' }}>
              Inicio de sesión
            </h2>
            <img
              src={logo}
              alt="Logo"
              style={{ width: '160px', height: '120px', borderRadius: '50%' }}
            />
          </div>

          <form onSubmit={handleSubmit}>
            <label>
              Usuario o correo:
              <div style={{ position: 'relative', width: '100%' }}>
                <i 
                  className="bi bi-person-fill" 
                  style={{ 
                    position: 'absolute', 
                    left: '15px', 
                    top: '58%', 
                    transform: 'translateY(-50%)', 
                    color: '#8A6642',
                    fontSize: '18px'
                  }}
                ></i>
                <input
                  type="text"
                  name="identificador"
                  value={formData.identificador}
                  onChange={handleChange}
                  required
                  placeholder="Nombre de usuario o correo"
                  style={{ 
                    paddingLeft: '45px', 
                    width: '100%',
                    minWidth: '350px',
                    height: '45px'
                  }}
                />
              </div>
            </label>
            <label>
              Contraseña:
              <div style={{ position: 'relative', width: '100%' }}>
                <i 
                  className="bi bi-lock-fill" 
                  style={{ 
                    position: 'absolute', 
                    left: '15px', 
                    top: '58%', 
                    transform: 'translateY(-50%)', 
                    color: '#FFD700',
                    fontSize: '18px'
                  }}
                ></i>
                <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  required
                  placeholder="*********************************"
                  style={{ 
                    paddingLeft: '45px', 
                    paddingTop: '15px', 
                    width: '100%',
                    minWidth: '350px',
                    height: '45px'
                  }}
                />
              </div>
            </label>
          <button
            type="submit"
            style={{
              marginTop: '15px',
              backgroundColor: "#111827",
              color: "#e1ddd3",
              padding: "0.5rem 1.2rem",
              borderRadius: "20px",
              textDecoration: "none",
              fontWeight: "600",
              transition: "all 0.3s ease",
              boxShadow: "0 2px 6px rgba(0,0,0,0.15)",
              whiteSpace: "nowrap",
              border: "none",
              width: "100%", 
            }}
            onMouseEnter={(e) => {
              e.target.style.backgroundColor = "#4a90e2";
              e.target.style.color = "white";
            }}
            onMouseLeave={(e) => {
              e.target.style.backgroundColor = "#111827";
              e.target.style.color = "#e1ddd3";
            }}
          >
            Ingresar
          </button>

          </form>
        </div>
      </div>

      <Footer />
    </>
  );
};

export default Login;