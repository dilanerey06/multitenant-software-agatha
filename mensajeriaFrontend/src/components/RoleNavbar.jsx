import { Link, useLocation } from 'react-router-dom';
import logo from '../images/logo.png';

const RoleNavbar = ({ userRole, onLogout }) => {
  const location = useLocation(); 

  const roleNavItems = {
    ROLE_ADMIN_MENSAJERIA: [
      { label: "Clientes", path: "/admin/clientes" },
      { label: "Pedidos", path: "/admin/pedidos" },
    //  { label: "Historial de pedidos", path: "/admin/historial_pedido" },
      { label: "Estadísticas", path: "/admin/dahsboard_general"},
      { label: "Tarifas", path: "/admin/tarifas"},
      { label: "Arqueos de caja", path: "/admin/arqueo_caja"},
      { label: "Usuarios", path: "/admin/usuarios" },
      { label: "Mensajeros", path: "/admin/mensajeros"},
      { label: "Mi información", path: "/admin/info" },
      { label: "", icon: "bi-bell", path: "/admin/notificaciones" }
    ],
    ROLE_OPERADOR: [
      { label: "Clientes", path: "/operador/clientes" },
      { label: "Pedidos", path: "/operador/pedidos" },
    //  { label: "Estadísticas", path: "/operador/dahsboard_general"},
      { label: "Tarifas", path: "/operador/tarifas"},
      { label: "Arqueos de caja", path: "/operador/arqueo_caja"},
     // { label: "Historial de pedidos", path: "/operador/historial_pedido" },
      { label: "Mensajeros", path: "/operador/mensajeros" },
      { label: "Mi información", path: "/operador/info" },
      { label: "", icon: "bi-bell", path: "/operador/notificaciones" }
    ],
    ROLE_MENSAJERO: [
      { label: "Mis pedidos", path: "/mensajero/pedidos" },
     // { label: "Historial de mis pedidos", path: "/mensajero/historial_pedido" },
      { label: "Mi información", path: "/mensajero/info" },
      { label: "", icon: "bi-bell", path: "/mensajero/notificaciones" }
    ]
  };

  const navItems = roleNavItems[userRole] || [];

  const getDashboardPath = () => {
    if (userRole === 'ROLE_ADMIN_MENSAJERIA') return '/admin';
    if (userRole === 'ROLE_OPERADOR') return '/operador';
    if (userRole === 'ROLE_MENSAJERO') return '/mensajero';
    return '/';
  };

  const isActiveLink = (path) => {
    return location.pathname === path;
  };

  return (
    <nav
      style={{
        backgroundColor: "#e1ddd3",
        padding: "0.75rem 2rem",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        boxShadow: "0 2px 8px rgba(20, 38, 99, 0.4)",
      }}
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "3rem",
          flexGrow: 1,
        }}
      >
        <Link
          to={getDashboardPath()}
          style={{
            display: "flex",
            alignItems: "center",
            textDecoration: "none",
            color: "#374151",
            gap: "0.5rem",
            paddingRight: "1rem",
            borderRight: "1px solid #999",
          }}
        >
          <img
            src={logo}
            alt="Logo"
            style={{
              height: "80px",
              width: "80px",
              objectFit: "cover",
            }}
          />
        </Link>

        <ul
          style={{
            display: "flex",
            listStyle: "none",
            gap: "2rem",
            margin: 0,
            padding: 0,
          }}
        >
          {navItems.map((item) => {
            const isActive = isActiveLink(item.path);
            return (
              <li key={item.path}>
                <Link
                  to={item.path}
                  style={{
                    color: isActive ? "#4a90e2" : "#374151",
                    textDecoration: "none",
                    fontWeight: isActive ? "700" : "500",
                    transition: "color 0.3s ease",
                    borderBottom: isActive ? "2px solid #4a90e2" : "none",
                    paddingBottom: "4px",
                    display: "flex",
                    alignItems: "center",
                    gap: "4px"
                  }}
                  onMouseEnter={(e) => {
                    if (!isActive) e.target.style.color = "#4a90e2";
                  }}
                  onMouseLeave={(e) => {
                    if (!isActive) e.target.style.color = "#374151";
                  }}
                >
                  {item.icon && <i className={`bi ${item.icon}`}></i>}
                  {item.label}
                </Link>
              </li>
            );
          })}

        </ul>
      </div>

      <button
        onClick={onLogout}
        style={{
          backgroundColor: "#111827",
          color: "#e1ddd3",
          padding: "0.5rem 1.2rem",
          borderRadius: "20px",
          border: "none",
          fontWeight: "600",
          cursor: "pointer",
          boxShadow: "0 2px 6px rgba(0,0,0,0.15)",
          transition: "all 0.3s ease",
          whiteSpace: "nowrap",
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
        Cerrar Sesión
      </button>
    </nav>
  );
};

export default RoleNavbar;