import { Link, useLocation } from 'react-router-dom';
import logo from '../images/logo.png';

const RoleNavbar = ({ userRole, onLogout }) => {
  const location = useLocation(); 

  const roleNavItems = {
    ROLE_SUPER_ADMIN: [
      { label: "Tenants", path: "/super/tenants" },
      { label: "Administradores", path: "/super/usuarios" },
      { label: "Información SuperAdmin", path: "/super/info" }
    ]
  };

  const navItems = roleNavItems[userRole] || [];

  const getDashboardPath = () => {
    if (userRole === 'ROLE_SUPER_ADMIN') return '/super';
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