import { Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import RoleNavbar from './components/RoleNavbar';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import PrivateRoute from './components/PrivateRoute';
import Home from './pages/Home';

// Páginas de super
import SuperTenants from './pages/super/SuperTenants';
import SuperDashboard from './pages/super/SuperDashboard';
import SuperUsuarios from './pages/super/SuperUsuarios';
import SuperInfo from './pages/super/SuperInformacion';


const App = () => {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getRoleBasePath = (role) => {
    if (!role) return '';
    if (role.includes('super')) return 'super';
    return '';
  };

  return (
    <>
      {/* Navbar pública o navbar según rol */}
      {!auth.user ? <Navbar /> : <RoleNavbar userRole={auth.role} onLogout={handleLogout} />}

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />

        {/* Rutas protegidas para super */}
        <Route element={<PrivateRoute allowedRoles={['ROLE_SUPER_ADMIN']} />}>
          <Route path="/super" element={<SuperDashboard />} />
          <Route path="/super/tenants" element={<SuperTenants />} />
          <Route path="/super/usuarios" element={<SuperUsuarios />} />
          <Route path="/super/info" element={<SuperInfo />} />
        </Route>

        {/* Redirección catch-all */}
        <Route
          path="*"
          element={<Navigate to={auth.user ? `/${getRoleBasePath(auth.role)}` : '/login'} />}
        />
      </Routes>
    </>
  );
};

export default App;