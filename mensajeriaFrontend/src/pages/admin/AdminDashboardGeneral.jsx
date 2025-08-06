import React, { useState, useEffect, useCallback  } from 'react';
import axios from 'axios';
import Footer from '../../components/Footer'; 
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, PieChart, Pie, Cell } from 'recharts';

export default function AdminDashboardGeneral() {
    const [dashboardData, setDashboardData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filtroTiempo, setFiltroTiempo] = useState('hoy');
    const [fechaPersonalizada, setFechaPersonalizada] = useState({
      inicio: '',
      fin: ''
    });
    const [tipoGrafico, setTipoGrafico] = useState('barras');

    const obtenerToken = () => {
      return localStorage.getItem('token');
    };

    useEffect(() => {
      cargarDashboard();
    }, [filtroTiempo, fechaPersonalizada]); 

    const cargarDashboard = async () => {
      try {
        setLoading(true);
        setError(null);

        if (filtroTiempo === 'personalizado' && (!fechaPersonalizada.inicio || !fechaPersonalizada.fin)) {
          setLoading(false);
          return;
        }
        
        const token = obtenerToken();
        
        if (!token) {
          setError('No se encontró token de autorización');
          setLoading(false);
          return;
        }
        
        const additionalData = localStorage.getItem('x-additional-data');

        const config = {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
            "x-additional-data": additionalData,
          }
        };

        let dashboardResponse;
        
        switch (filtroTiempo) {
          case 'hoy':
            dashboardResponse = await axios.get('/proxy/api/dashboard/metricas-rapidas', config);
            break;
            
          case 'semana':
            dashboardResponse = await axios.get(
              `/proxy/api/dashboard/metricas-semana`, 
              config
            );
            break;
            
          case 'mes':
            dashboardResponse = await axios.get(
              `/proxy/api/dashboard/metricas-mes`, 
              config
            );
            break;
            
          case 'personalizado':
            if (fechaPersonalizada.inicio && fechaPersonalizada.fin) {
              dashboardResponse = await axios.get(
                `/proxy/api/dashboard/metricas-periodo?fechaInicio=${fechaPersonalizada.inicio}&fechaFin=${fechaPersonalizada.fin}`, 
                config
              );
            } else {
              dashboardResponse = await axios.get('/proxy/api/dashboard/metricas-rapidas', config);
            }
            break;
            
          default:
            dashboardResponse = await axios.get('/proxy/api/dashboard/completo', config);
        }

        const requests = [
          axios.get('/proxy/api/dashboard/general', config),
          axios.get('/proxy/api/dashboard/estadisticas-tenant', config),
          axios.get('/proxy/api/dashboard/mensajeros/estadisticas', config),
          axios.get('/proxy/api/dashboard/mensajeros/ranking', config),
          axios.get('/proxy/api/dashboard/arqueos/resumen', config)
        ];

        const responses = await Promise.allSettled(requests);
        
        const [
          generalResponse,
          estadisticasTenantResponse,
          estadisticasMensajerosResponse,
          rankingMensajerosResponse,
          arqueosResponse
        ] = responses;

        const dashboardData = {
          dashboardGeneral: generalResponse.status === 'fulfilled' ? 
            (generalResponse.value.data.data || []) : [],
          estadisticasTenant: estadisticasTenantResponse.status === 'fulfilled' ? 
            (estadisticasTenantResponse.value.data.data || {}) : {},
          metricasRapidas: filtroTiempo === 'hoy' ? (dashboardResponse.data.data || {}) : {},
          metricasPeriodo: filtroTiempo !== 'hoy' ? (dashboardResponse.data.data || {}) : {},
          filtroActual: filtroTiempo,
          estadisticasMensajeros: estadisticasMensajerosResponse.status === 'fulfilled' ? 
            (estadisticasMensajerosResponse.value.data.data || []) : [],
          rankingMensajeros: rankingMensajerosResponse.status === 'fulfilled' ? 
            (rankingMensajerosResponse.value.data.data || []) : [],
          resumenArqueos: arqueosResponse.status === 'fulfilled' ? 
            (arqueosResponse.value.data.data || []) : []
        };

        setDashboardData(dashboardData);
        
      } catch (err) {
        console.error('Error completo:', err);
        setError(manejarError(err));
      } finally {
        setLoading(false);
      }
    };

    const manejarError = (error) => {
      if (error.response) {
        console.error('Error response:', error.response.data);
        return `Error ${error.response.status}: ${error.response.data?.message || 'Error del servidor'}`;
      } else if (error.request) {
        console.error('Error request:', error.request);
        return 'Error de conexión. Verifique su conexión a internet.';
      } else {
        console.error('Error config:', error.message);
        return 'Error inesperado: ' + error.message;
      }
    };

    const obtenerMetricas = () => {
      if (!dashboardData) return {
        totalPedidos: 0,
        entregados: 0,
        activos: 0,
        ingresos: 0,
        tasaExito: 0,
        tiempoPromedio: 0
      };

      if (filtroTiempo === 'hoy') {
        return {
          totalPedidos: dashboardData.metricasRapidas.pedidosHoy || 0,
          entregados: dashboardData.metricasRapidas.pedidosEntregadosHoy || 0,
          activos: dashboardData.metricasRapidas.pedidosActivos || 0,
          ingresos: dashboardData.metricasRapidas.ingresosHoy || 0,
          tasaExito: dashboardData.metricasRapidas.tasaExitoHoy || 0,
          tiempoPromedio: dashboardData.metricasRapidas.tiempoPromedio || 0
        };
      } else {
        const totalPedidos = dashboardData.metricasPeriodo.pedidosPeriodo || 0;
        const entregados = dashboardData.metricasPeriodo.pedidosEntregados || 0;
        
        return {
          totalPedidos,
          entregados,
          activos: totalPedidos - entregados,
          ingresos: dashboardData.metricasPeriodo.ingresosPeriodo || 0,
          tasaExito: dashboardData.metricasPeriodo.tasaExito || 0,
          tiempoPromedio: dashboardData.metricasPeriodo.tiempoPromedio || 0
        };
      }
    };

    const formatearMoneda = (valor) => {
      if (!valor && valor !== 0) return '$0';
      return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0
      }).format(valor);
    };

    const formatearFecha = (fecha) => {
      if (!fecha) return 'Fecha no disponible';
      
      // 'T12:00:00' para evitar problemas de zona horaria
      return new Date(fecha + 'T12:00:00').toLocaleDateString('es-CO', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    };

    const manejarFechaPersonalizada = (tipo, valor) => {
      setFechaPersonalizada(prev => {
        const nuevaFecha = {
          ...prev,
          [tipo]: valor
        };
        
        if (nuevaFecha.inicio && nuevaFecha.fin) {
          const fechaInicio = new Date(nuevaFecha.inicio);
          const fechaFin = new Date(nuevaFecha.fin);
          
          if (fechaInicio > fechaFin) {
            if (tipo === 'inicio') {
              nuevaFecha.fin = valor; 
            } else {
              nuevaFecha.inicio = valor;
            }
          }
        }
        
        return nuevaFecha;
      });
    };
    
    const cargarDashboardDebounced = useCallback(
      debounce(() => {
        cargarDashboard();
      }, 300),
      []
    );

    function debounce(func, wait) {
      let timeout;
      return function executedFunction(...args) {
        const later = () => {
          clearTimeout(timeout);
          func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
      };
    }

    const aplicarFiltroPersonalizado = () => {
      if (fechaPersonalizada.inicio && fechaPersonalizada.fin) {
        const fechaInicio = new Date(fechaPersonalizada.inicio);
        const fechaFin = new Date(fechaPersonalizada.fin);
        
        if (fechaInicio <= fechaFin) {
          setLoading(true);
          cargarDashboard();
        }
      }
    };

    if (loading) {
      return (
        <div className="container py-4">
          <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '400px' }}>
            <div className="text-center">
              <div className="spinner-border text-primary mb-3" style={{ width: '3rem', height: '3rem' }}></div>
              <h5 className="text-muted">Cargando dashboard...</h5>
            </div>
          </div>
        </div>
      );
    }

    if (error) {
      return (
        <div className="container py-4">
          <div className="alert alert-danger text-center">
            <i className="bi bi-exclamation-triangle me-2"></i>
            {error}
            <button className="btn btn-outline-danger ms-3" onClick={cargarDashboard}>
              Reintentar
            </button>
          </div>
        </div>
      );
    }

    if (!dashboardData) {
      return null;
    }

    const dashboardGeneral = dashboardData?.dashboardGeneral?.[0] || {};
    const metricas = obtenerMetricas();
    
    const datosGrafico = [
      {
        name: 'Entregados',
        value: metricas.entregados,
        color: '#28a745'
      },
      {
        name: 'Activos',
        value: metricas.activos,
        color: '#ffc107'
      },
      {
        name: 'Cancelados',
        value: dashboardGeneral.cancelados || 0,
        color: '#dc3545'
      }
    ];

    return (
      <>
      <div className="container py-4" style={{ minHeight: 'calc(100vh - 120px)' }}>
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
            <i className="bi bi-speedometer2 me-2" style={{ color: '#ff0080' }}></i>
            Dashboard general
            {dashboardGeneral?.empresa && (
              <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
                <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
                {dashboardGeneral.empresa}
              </span>
            )}
          </h3>
          <div className="text-muted">
            <i className="bi bi-calendar-event me-1"></i>
            {formatearFecha(dashboardGeneral?.fecha)}
          </div>
        </div>

        <div className="card shadow-sm mb-4">
        <div className="card-header bg-light">
          <h5 className="mb-0">
            <i className="bi bi-clock me-2" style={{ color: '#6c757d' }}></i>
            Período de análisis
          </h5>
        </div>
        <div className="card-body">
          <div className="row g-3 align-items-end">
            <div className="col-md-6">
              <div className="btn-group w-100" role="group">
                <button
                  className={`btn ${filtroTiempo === 'hoy' ? 'btn-primary' : 'btn-outline-primary'}`}
                  onClick={() => setFiltroTiempo('hoy')}
                >
                  <i className="bi bi-calendar-day me-1"></i>
                  Hoy
                </button>
                <button
                  className={`btn ${filtroTiempo === 'semana' ? 'btn-primary' : 'btn-outline-primary'}`}
                  onClick={() => setFiltroTiempo('semana')}
                >
                  <i className="bi bi-calendar-week me-1"></i>
                  Semana
                </button>
                <button
                  className={`btn ${filtroTiempo === 'mes' ? 'btn-primary' : 'btn-outline-primary'}`}
                  onClick={() => setFiltroTiempo('mes')}
                >
                  <i className="bi bi-calendar-month me-1"></i>
                  Mes
                </button>
                <button
                  className={`btn ${filtroTiempo === 'personalizado' ? 'btn-primary' : 'btn-outline-primary'}`}
                  onClick={() => setFiltroTiempo('personalizado')}
                >
                  <i className="bi bi-calendar-range me-1"></i>
                  Personalizado
                </button>
              </div>
            </div>
            
            {filtroTiempo === 'personalizado' && (
              <div className="col-md-6">
                <div className="d-flex gap-2 align-items-end">
                  <div className="flex-fill">
                    <label className="form-label small mb-1">Desde</label>
                    <input
                      type="date"
                      className={`form-control ${fechaPersonalizada.inicio && fechaPersonalizada.fin && 
                        new Date(fechaPersonalizada.inicio) > new Date(fechaPersonalizada.fin) ? 'is-invalid' : ''}`}
                      value={fechaPersonalizada.inicio}
                      onChange={(e) => manejarFechaPersonalizada('inicio', e.target.value)}
                      max={fechaPersonalizada.fin || new Date().toISOString().split('T')[0]}
                    />
                  </div>
                  <div className="flex-fill">
                    <label className="form-label small mb-1">Hasta</label>
                    <input
                      type="date"
                      className={`form-control ${fechaPersonalizada.inicio && fechaPersonalizada.fin && 
                        new Date(fechaPersonalizada.inicio) > new Date(fechaPersonalizada.fin) ? 'is-invalid' : ''}`}
                      value={fechaPersonalizada.fin}
                      onChange={(e) => manejarFechaPersonalizada('fin', e.target.value)}
                      min={fechaPersonalizada.inicio}
                      max={new Date().toISOString().split('T')[0]}
                    />
                  </div>
                  <div>
                    <button 
                      className="btn btn-success"
                      onClick={aplicarFiltroPersonalizado}
                      disabled={!fechaPersonalizada.inicio || !fechaPersonalizada.fin || 
                        new Date(fechaPersonalizada.inicio) > new Date(fechaPersonalizada.fin)}
                      title={!fechaPersonalizada.inicio || !fechaPersonalizada.fin ? 
                        'Selecciona ambas fechas' : 
                        new Date(fechaPersonalizada.inicio) > new Date(fechaPersonalizada.fin) ? 
                        'La fecha de inicio debe ser anterior a la fecha final' : 
                        'Aplicar filtro personalizado'}
                    >
                      <i className="bi bi-search"></i>
                    </button>
                  </div>
                </div>
                {fechaPersonalizada.inicio && fechaPersonalizada.fin && 
                  new Date(fechaPersonalizada.inicio) > new Date(fechaPersonalizada.fin) && (
                  <div className="text-danger small mt-2">
                    <i className="bi bi-exclamation-triangle me-1"></i>
                    La fecha de inicio debe ser anterior a la fecha final
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>

        <div className="row g-4 mb-4">
          <div className="col-md-3">
            <div className="card h-100 shadow-sm border-0" style={{ borderLeft: '4px solid #007bff' }}>
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 className="text-muted mb-0">Total de pedidos</h6>
                    <h3 className="fw-bold text-primary mb-0">{metricas.totalPedidos}</h3>
                  </div>
                  <div className="text-primary" style={{ fontSize: '2.5rem' }}>
                    <i className="bi bi-box-seam"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-3">
            <div className="card h-100 shadow-sm border-0" style={{ borderLeft: '4px solid #28a745' }}>
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 className="text-muted mb-0">Entregados</h6>
                    <h3 className="fw-bold text-success mb-0">{metricas.entregados}</h3>
                    <small className="text-muted">
                      {metricas.totalPedidos ? 
                        `${((metricas.entregados / metricas.totalPedidos) * 100).toFixed(1)}%` : 
                        '0%'
                      }
                    </small>
                  </div>
                  <div className="text-success" style={{ fontSize: '2.5rem' }}>
                    <i className="bi bi-check-circle"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-3">
            <div className="card h-100 shadow-sm border-0" style={{ borderLeft: '4px solid #ffc107' }}>
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 className="text-muted mb-0">Activos</h6>
                    <h3 className="fw-bold text-warning mb-0">{metricas.activos}</h3>
                    <small className="text-muted">En proceso</small>
                  </div>
                  <div className="text-warning" style={{ fontSize: '2.5rem' }}>
                    <i className="bi bi-clock"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-3">
            <div className="card h-100 shadow-sm border-0" style={{ borderLeft: '4px solid #dc3545' }}>
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 className="text-muted mb-0">Cancelados</h6>
                    <h3 className="fw-bold text-danger mb-0">{dashboardGeneral?.cancelados || 0}</h3>
                    <small className="text-muted">
                      {metricas.totalPedidos ? 
                        `${(((dashboardGeneral?.cancelados || 0) / metricas.totalPedidos) * 100).toFixed(1)}%` : 
                        '0%'
                      }
                    </small>
                  </div>
                  <div className="text-danger" style={{ fontSize: '2.5rem' }}>
                    <i className="bi bi-x-circle"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="row g-4 mb-4">
          <div className="col-md-4">
            <div className="card h-100 shadow-sm border-0">
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 className="text-muted mb-0">
                      <i className="bi bi-currency-dollar me-1" style={{ color: '#28a745' }}></i>
                      Ingresos
                    </h6>
                    <h4 className="fw-bold text-success mb-0">
                      {formatearMoneda(metricas.ingresos)}
                    </h4>
                  </div>
                  <div className="text-success" style={{ fontSize: '2rem' }}>
                    <i className="bi bi-graph-up"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-4">
            <div className="card h-100 shadow-sm border-0">
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 className="text-muted mb-0">
                      <i className="bi bi-receipt me-1" style={{ color: '#6f42c1' }}></i>
                      Ticket promedio
                    </h6>
                    <h4 className="fw-bold text-primary mb-0">
                      {formatearMoneda(dashboardGeneral?.ticketPromedio || 0)}
                    </h4>
                  </div>
                  <div className="text-primary" style={{ fontSize: '2rem' }}>
                    <i className="bi bi-calculator"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-4">
            <div className="card h-100 shadow-sm border-0">
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 className="text-muted mb-0">
                      <i className="bi bi-people me-1" style={{ color: '#ff6600' }}></i>
                      Mensajeros activos
                    </h6>
                    <h4 className="fw-bold mb-0" style={{ color: '#ff6600' }}>
                      {dashboardGeneral?.mensajerosActivos || 0}
                    </h4>
                  </div>
                  <div style={{ fontSize: '2rem', color: '#ff6600' }}>
                    <i className="bi bi-person-check"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="row g-4 mb-4">
          <div className="col-md-8">
            <div className="card shadow-sm h-100">
              <div className="card-header bg-light d-flex justify-content-between align-items-center">
                <h5 className="mb-0">
                  <i className="bi bi-pie-chart me-2" style={{ color: '#6c757d' }}></i>
                  Distribución de pedidos
                </h5>
                <div className="btn-group btn-group-sm">
                  <button 
                    className={`btn ${tipoGrafico === 'barras' ? 'btn-primary' : 'btn-outline-primary'}`}
                    onClick={() => setTipoGrafico('barras')}
                  >
                    <i className="bi bi-bar-chart"></i> Barras
                  </button>
                  <button 
                    className={`btn ${tipoGrafico === 'torta' ? 'btn-primary' : 'btn-outline-primary'}`}
                    onClick={() => setTipoGrafico('torta')}
                  >
                    <i className="bi bi-pie-chart"></i> Torta
                  </button>
                </div>
              </div>
              <div className="card-body">
                {datosGrafico.some(d => d.value > 0) ? (
                  <ResponsiveContainer width="100%" height={300}>
                    {tipoGrafico === 'torta' ? (
                      <PieChart>
                        <Pie
                          data={datosGrafico}
                          cx="50%"
                          cy="50%"
                          innerRadius={60}
                          outerRadius={100}
                          paddingAngle={5}
                          dataKey="value"
                          label={({ name, value }) => `${name}: ${value}`}
                        >
                          {datosGrafico.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={entry.color} />
                          ))}
                        </Pie>
                        <Tooltip />
                        <text x="50%" y="50%" textAnchor="middle" dominantBaseline="middle">
                          <tspan x="50%" dy="-0.5em" fontSize="24" fontWeight="bold">
                            {datosGrafico.reduce((sum, d) => sum + d.value, 0)}
                          </tspan>
                          <tspan x="50%" dy="1.2em" fontSize="14" fill="#666">
                            Total Pedidos
                          </tspan>
                        </text>
                      </PieChart>
                    ) : (
                      <BarChart data={datosGrafico}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="name" />
                        <YAxis />
                        <Tooltip />
                        <Bar dataKey="value" fill="#8884d8">
                          {datosGrafico.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={entry.color} />
                          ))}
                        </Bar>
                      </BarChart>
                    )}
                  </ResponsiveContainer>
                ) : (
                  <div className="text-center py-5">
                    <i className="bi bi-pie-chart" style={{ fontSize: '3rem', color: '#ccc' }}></i>
                    <p className="text-muted mt-2">No hay datos disponibles para mostrar</p>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="col-md-4">
            <div className="card shadow-sm h-100">
              <div className="card-header bg-light">
                <h5 className="mb-0">
                  <i className="bi bi-stopwatch me-2" style={{ color: '#6c757d' }}></i>
                  Métricas de rendimiento
                </h5>
              </div>
              <div className="card-body">
                <div className="row text-center mb-4">
                  <div className="col-6 mb-3">
                    <div>
                      <h3 className="fw-bold text-info mb-0">
                        {metricas?.tiempoPromedio?.toFixed(1) || 0}
                      </h3>
                      <small className="text-muted">
                        <i className="bi bi-clock me-1"></i>
                        Tiempo promedio (min)
                      </small>
                    </div>
                  </div>
                  <div className="col-6 mb-3">
                    <h3 className="fw-bold text-success mb-0">
                      {metricas.tasaExito?.toFixed(1) || 0}%
                    </h3>
                    <small className="text-muted">
                      <i className="bi bi-bullseye me-1"></i>
                      Tasa de éxito
                    </small>
                  </div>
                </div>

                {dashboardData?.rankingMensajeros && dashboardData.rankingMensajeros.length > 0 ? (
                  <div className="mt-4">
                    <h6 className="text-center mb-3">
                      <i className="bi bi-trophy me-2" style={{ color: '#ffc107' }}></i>
                      Top mensajeros
                    </h6>
                    <div className="row">
                      {dashboardData.rankingMensajeros.slice(0, 3).map((mensajero, index) => (
                        <div className="col-12 mb-2" key={mensajero.id || index}>
                          <div className="d-flex align-items-center justify-content-between p-2 bg-light rounded">
                            <div className="d-flex align-items-center">
                              <span className={`badge ${index === 0 ? 'bg-warning' : index === 1 ? 'bg-secondary' : 'bg-success'} me-2`}>
                                #{mensajero.rankingDesempeno || index + 1}
                              </span>
                              <span className="small">{`${mensajero.nombres || ''} ${mensajero.apellidos || ''}`}</span>
                            </div>
                            <span className="text-muted small">
                              {mensajero.categoriaDesempeno || 'N/A'}
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                ) : (
                  <div className="text-center py-3">
                    <i className="bi bi-person-x" style={{ fontSize: '2rem', color: '#ccc' }}></i>
                    <p className="text-muted small mt-2">No hay datos de mensajeros</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    <Footer />
    </>
  );
}