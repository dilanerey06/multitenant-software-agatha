import logo from "../../images/logo.png";
import Footer from "../../components/Footer.jsx";

const SuperDashboard = () => {
  return (
    <>
      <main className="home-container">
        <section className="intro text-center py-5">
          <div className="d-flex align-items-center justify-content-center gap-3 mb-4">
            <h1 className="display-4 fw-bold mb-0">Panel de super administrador</h1>
            <img
              src={logo}
              alt="Logo Agatha"
              style={{
                width: "120px",
                height: "120px",
                borderRadius: "50%",
                objectFit: "cover",
                border: "3px solid var(--color-primary-dark)",
                backgroundColor: "white",
                boxShadow: "0 4px 15px rgba(0,0,0,0.2)"
              }}
            />
          </div>
          <p className="lead px-3 px-md-5">
            Centro de control maestro de Agatha. Gestiona todos los tenants, controla arquitectura multi-inquilino y supervisa el ecosistema completo.
          </p>
        </section>

        <section className="full-width-section my-4 p-4 rounded-4 text-center" style={{ backgroundColor: "white", boxShadow: "0 6px 20px rgba(0,0,0,0.1)" }}>
          <h2 className="mb-4 fw-bold">
            <i className="bi bi-diagram-3 me-2" style={{ color: 'var(--color-primary-dark)' }}></i>
            Control Multi-tenant
          </h2>
          <div className="row g-4 justify-content-center">
            <div className="col-lg-6 col-xl-4">
              <div className="card shadow border-0 h-100" style={{ backgroundColor: "#e8f4f8", borderLeft: "4px solid #0dcaf0" }}>
                <div className="card-body">
                  <h5 className="card-title">
                    <i className="bi bi-building-gear me-2" style={{ color: '#0dcaf0' }}></i>
                    Gestión de tenants
                  </h5>
                  <p className="card-text">
                    Crea, modifica y elimina tenants. Configura bases de datos independientes, dominios personalizados y recursos específicos para cada inquilino.
                  </p>
                  <div className="mt-3">
                    <small className="text-muted">
                      <i className="bi bi-check-circle-fill me-1"></i>
                      Aislamiento completo de datos
                    </small>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-lg-6 col-xl-4">
              <div className="card shadow border-0 h-100" style={{ backgroundColor: "#fff3e0", borderLeft: "4px solid #ff6600" }}>
                <div className="card-body">
                  <h5 className="card-title">
                    <i className="bi bi-people-fill me-2" style={{ color: '#ff6600' }}></i>
                    Usuarios Multi-tenant
                  </h5>
                  <p className="card-text">
                    Administra usuarios administradores de los tenants. Gestiona permisos cross-tenant y supervisa accesos.
                  </p>
                  <div className="mt-3">
                    <small className="text-muted">
                      <i className="bi bi-shield-check me-1"></i>
                      Control de acceso granular
                    </small>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-lg-6 col-xl-4">
              <div className="card shadow border-0 h-100" style={{ backgroundColor: "#f3e5f5", borderLeft: "4px solid #6610f2" }}>
                <div className="card-body">
                  <h5 className="card-title">
                    <i className="bi bi-server me-2" style={{ color: '#6610f2' }}></i>
                    Arquitectura del sistema
                  </h5>
                  <p className="card-text">
                    Monitorea recursos, bases de datos por tenant, balanceadores de carga y la salud general de la infraestructura multi-inquilino.
                  </p>
                  <div className="mt-3">
                    <small className="text-muted">
                      <i className="bi bi-graph-up me-1"></i>
                      Escalabilidad automática
                    </small>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-lg-6 col-xl-4">
              <div className="card shadow border-0 h-100" style={{ backgroundColor: "#e8f5e8", borderLeft: "4px solid #198754" }}>
                <div className="card-body">
                  <h5 className="card-title">
                    <i className="bi bi-graph-down-arrow me-2" style={{ color: '#198754' }}></i>
                    Métricas y análisis
                  </h5>
                  <p className="card-text">
                    Visualiza estadísticas de uso por tenant, consumo de recursos, rendimiento y análisis comparativo entre inquilinos.
                  </p>
                  <div className="mt-3">
                    <small className="text-muted">
                      <i className="bi bi-bar-chart-fill me-1"></i>
                      Reportes en tiempo real
                    </small>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-lg-6 col-xl-4">
              <div className="card shadow border-0 h-100" style={{ backgroundColor: "#fdf2f2", borderLeft: "4px solid #dc3545" }}>
                <div className="card-body">
                  <h5 className="card-title">
                    <i className="bi bi-exclamation-diamond-fill me-2" style={{ color: '#dc3545' }}></i>
                    Seguridad global
                  </h5>
                  <p className="card-text">
                    Controla políticas de seguridad transversales, auditorías de acceso, detección de amenazas y backup automático por tenant.
                  </p>
                  <div className="mt-3">
                    <small className="text-muted">
                      <i className="bi bi-lock-fill me-1"></i>
                      Protección multi-capa
                    </small>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-lg-6 col-xl-4">
              <div className="card shadow border-0 h-100" style={{ backgroundColor: "#fff8e1", borderLeft: "4px solid #ffc107" }}>
                <div className="card-body">
                  <h5 className="card-title">
                    <i className="bi bi-gear-wide-connected me-2" style={{ color: '#ffc107' }}></i>
                    Configuración avanzada
                  </h5>
                  <p className="card-text">
                    Personaliza configuraciones globales, templates de tenant, políticas de facturación y parámetros de sistema.
                  </p>
                  <div className="mt-3">
                    <small className="text-muted">
                      <i className="bi bi-wrench me-1"></i>
                      Flexibilidad total
                    </small>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <Footer />
      </main>
    </>
  );
};

export default SuperDashboard;