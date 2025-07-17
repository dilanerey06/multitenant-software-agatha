import { Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import RoleNavbar from './components/RoleNavbar';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import PrivateRoute from './components/PrivateRoute';
import Home from './pages/Home';


// Páginas de admin
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminClientes from './pages/admin/AdminClientes';
import AdminHistorial from './pages/admin/AdminHistorialPedidos';
import AdminPedidos from './pages/admin/AdminPedidos';
import AdminUsuarios from './pages/admin/AdminUsuarios';
import AdminInfo from './pages/admin/AdminInformacion';
import AdminArqueoCaja from './pages/admin/AdminArqueoCaja';
import AdminDashboardGeneral from './pages/admin/AdminDashboardGeneral';
import AdminMensajeros from './pages/admin/AdminMensajeros';
import AdminTarifas from './pages/admin/AdminTarifas';
import AdminNotificaciones from './pages/admin/AdminNotificaciones';


// Páginas de operador
import OperadorDashboard from './pages/operador/OperadorDashboard';
import OperadorClientes from './pages/operador/OperadorClientes';
import OperadorHistorial from './pages/operador/OperadorHistorialPedidos';
import OperadorPedidos from './pages/operador/OperadorPedidos';
import OperadorMensajeros from './pages/operador/OperadorMensajeros';
import OperadorInfo from './pages/operador/OperadorInformacion';
import OperadorArqueoCaja from './pages/operador/OperadorArqueoCaja';
import OperadorDashboardGeneral from './pages/operador/OperadorDashboardGeneral';
import OperadorTarifas from './pages/operador/OperadorTarifas';
import OperadorNotificaciones from './pages/operador/OperadorNotificaciones';

// Páginas de mensajero
import MensajeroDashboard from './pages/mensajero/MensajeroDashboard';
import MensajeroHistorial from './pages/mensajero/MensajeroHistorialPedidos';
import MensajeroPedidos from './pages/mensajero/MensajeroPedidos';
import MensajeroInfo from './pages/mensajero/MensajeroInformacion';
import MensajeroNotificaciones from './pages/mensajero/MensajeroNotificaciones';

const App = () => {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Determina la ruta base según el rol
  const getRoleBasePath = (role) => {
    if (!role) return '';
    if (role.includes('admin')) return 'admin';
    if (role.includes('operador')) return 'operador';
    if (role.includes('mensajero')) return 'mensajero';
    return '';
  };

  return (
    <>
      {/* Navbar pública o navbar según rol */}
      {!auth.user ? <Navbar /> : <RoleNavbar userRole={auth.role} onLogout={handleLogout} />}

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />

        {/* Rutas protegidas para admin */}
        <Route element={<PrivateRoute allowedRoles={['ROLE_ADMIN_MENSAJERIA']} />}>
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/admin/clientes" element={<AdminClientes />} />
          <Route path="/admin/pedidos" element={<AdminPedidos />} />
          <Route path="/admin/historial_pedido" element={<AdminHistorial />} />
          <Route path="/admin/arqueo_caja" element={<AdminArqueoCaja />} />
          <Route path="/admin/tarifas" element={<AdminTarifas />} />
          <Route path="/admin/dahsboard_general" element={<AdminDashboardGeneral />} />
          <Route path="/admin/mensajeros" element={<AdminMensajeros />} />
          <Route path="/admin/usuarios" element={<AdminUsuarios />} />
          <Route path="/admin/info" element={<AdminInfo />} />
          <Route path="/admin/notificaciones" element={<AdminNotificaciones />} />
        </Route>

        {/* Rutas protegidas para operador */}
        <Route element={<PrivateRoute allowedRoles={['ROLE_OPERADOR']} />}>
          <Route path="/operador" element={<OperadorDashboard />} />
          <Route path="/operador/clientes" element={<OperadorClientes />} />
          <Route path="/operador/pedidos" element={<OperadorPedidos />} />
          <Route path="/operador/historial_pedido" element={<OperadorHistorial />} />
          <Route path="/operador/arqueo_caja" element={<OperadorArqueoCaja />} />
          <Route path="/operador/tarifas" element={<OperadorTarifas />} />
          <Route path="/operador/dahsboard_general" element={<OperadorDashboardGeneral />} />
          <Route path="/operador/mensajeros" element={<OperadorMensajeros />} />
          <Route path="/operador/info" element={<OperadorInfo />} />
          <Route path="/operador/notificaciones" element={<OperadorNotificaciones />} />
        </Route>


        {/* Rutas protegidas para mensajero */}
        <Route element={<PrivateRoute allowedRoles={['ROLE_MENSAJERO']} />}>
          <Route path="/mensajero" element={<MensajeroDashboard />} />
          <Route path="/mensajero/pedidos" element={<MensajeroPedidos />} />
          <Route path="/mensajero/historial_pedido" element={<MensajeroHistorial />} />
          <Route path="/mensajero/info" element={<MensajeroInfo />} />
          <Route path="/mensajero/notificaciones" element={<MensajeroNotificaciones />} />
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