import React, { useState, useEffect } from 'react';
import Footer from '../../components/Footer';

export default function AdminArqueoCaja() {
  const [arqueos, setArqueos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState('create');
  const [selectedArqueo, setSelectedArqueo] = useState(null);
  const [showIngresoModal, setShowIngresoModal] = useState(false);
  const [ingresosPorArqueo, setIngresosPorArqueo] = useState({});
  const [vista, setVista] = useState('lista');
  const [reporteForm, setReporteForm] = useState({
  fechaDesde: '',
  fechaHasta: '',
  formato: 'pdf', 
  incluirDetalles: false
});


const [arqueosOriginales, setArqueosOriginales] = useState([]);
const [arqueosFiltrados, setArqueosFiltrados] = useState([]);

  const [paginacionManual, setPaginacionManual] = useState({
    paginaActual: 0,
    elementosPorPagina: 50
  });

  const paginarArqueos = (arqueosList) => {
    const inicio = paginacionManual.paginaActual * paginacionManual.elementosPorPagina;
    const fin = inicio + paginacionManual.elementosPorPagina;
    return arqueosList.slice(inicio, fin);
  };

  const [filtros, setFiltros] = useState({
    fechaDesde: '',
    fechaHasta: '',
    turnoId: '',
    estadoId: '',
    usuarioId: ''
  });

  const [erroresFiltros, setErroresFiltros] = useState({
    fechaDesde: '',
    fechaHasta: ''
  });
  
  const [erroresReporte, setErroresReporte] = useState({
  fechaDesde: '',
  fechaHasta: ''
});

function decodeJWT(token) {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('Token JWT inválido');
    }
    
    const payload = parts[1];
    const paddedPayload = payload + '='.repeat((4 - payload.length % 4) % 4);
    
    const decoded = atob(paddedPayload);
    
    return JSON.parse(decoded);
  } catch (error) {
    console.error('Error decodificando JWT:', error);
    return null;
  }
}

const token = localStorage.getItem('token');

if (token) {
  const decodedToken = decodeJWT(token);
  
  if (decodedToken) {
    
    const tenantId = decodedToken.tenant_id
    const empresaId = decodedToken.mensajeria_id;
    const currentUserId = decodedToken.user_id;
    
    if (!currentUserId) {
      console.error('No se pudo obtener el ID del usuario del token');
    }
    
    
  } else {
    console.error('No se pudo decodificar el token');
  }
} else {
  console.error('No se encontró token en localStorage');
}

function getUserDataFromToken() {
  const token = localStorage.getItem('token');
  
  if (!token) {
    console.error('No hay token disponible');
    return { empresaId: 1, currentUserId: null };
  }
  
  const decodedToken = decodeJWT(token);
  
  if (!decodedToken) {
    console.error('Token inválido');
    return { empresaId: 1, currentUserId: null };
  }
  
  return {
    tenantId: decodedToken.tenant_id,
    empresaId: decodedToken.mensajeria_id,
    currentUserId: decodedToken.user_id,
    roles: decodedToken.roles || [],
    username: decodedToken.sub,
    exp: decodedToken.exp,
    iat: decodedToken.iat
  };
}

  const userData = getUserDataFromToken();
  const tenantId = userData.tenantId;
  const empresaId = userData.empresaId;
  const currentUserId = userData.currentUserId;


const getFechaMaxima = () => {
  return new Date().toISOString().split('T')[0];
};

const validarFiltros = (filtrosActuales = filtros) => {
  const errores = {
    fechaDesde: '',
    fechaHasta: ''
  };

  const hoy = new Date().toISOString().split('T')[0];
  
  if (filtrosActuales.fechaDesde && filtrosActuales.fechaDesde > hoy) {
    errores.fechaDesde = 'La fecha desde no puede ser mayor a hoy';
  }
  
  if (filtrosActuales.fechaHasta && filtrosActuales.fechaHasta > hoy) {
    errores.fechaHasta = 'La fecha hasta no puede ser mayor a hoy';
  }
  
  if (filtrosActuales.fechaDesde && filtrosActuales.fechaHasta && 
      filtrosActuales.fechaDesde > filtrosActuales.fechaHasta) {
    errores.fechaHasta = 'La fecha hasta debe ser mayor o igual a la fecha desde';
  }

  setErroresFiltros(errores);
  return Object.values(errores).every(error => error === '');
};

const validarReporte = (reporteActual = reporteForm) => {
  const errores = {
    fechaDesde: '',
    fechaHasta: ''
  };

  const hoy = new Date().toISOString().split('T')[0];
  
  if (!reporteActual.fechaDesde) {
    errores.fechaDesde = 'La fecha desde es requerida';
  } else if (reporteActual.fechaDesde > hoy) {
    errores.fechaDesde = 'La fecha desde no puede ser mayor a hoy';
  }
  
  if (!reporteActual.fechaHasta) {
    errores.fechaHasta = 'La fecha hasta es requerida';
  } else if (reporteActual.fechaHasta > hoy) {
    errores.fechaHasta = 'La fecha hasta no puede ser mayor a hoy';
  }
  
  if (reporteActual.fechaDesde && reporteActual.fechaHasta && 
      reporteActual.fechaDesde > reporteActual.fechaHasta) {
    errores.fechaHasta = 'La fecha hasta debe ser mayor o igual a la fecha desde';
  }

  setErroresReporte(errores);
  return Object.values(errores).every(error => error === '');
};

const handleFiltroChange = (e) => {
  const { name, value } = e.target;
  const nuevosFiltros = {
    ...filtros,
    [name]: value
  };
  
  setFiltros(nuevosFiltros);
  
  if (name === 'fechaDesde' || name === 'fechaHasta') {
    validarFiltros(nuevosFiltros);
  }
};

const handleReporteChange = (e) => {
  const { name, value, type, checked } = e.target;
  const nuevoReporte = {
    ...reporteForm,
    [name]: type === 'checkbox' ? checked : value
  };
  
  setReporteForm(nuevoReporte);
  
  if (name === 'fechaDesde' || name === 'fechaHasta') {
    validarReporte(nuevoReporte);
  }
};

const esTurnoEnHorario = (turno, horaActual) => {
  const { hora_inicio, hora_fin } = turno;
  
  const convertirAMinutos = (hora) => {
    const [horas, minutos] = hora.split(':').map(Number);
    return horas * 60 + minutos;
  };
  
  const inicioMinutos = convertirAMinutos(hora_inicio);
  const finMinutos = convertirAMinutos(hora_fin);
  const actualMinutos = convertirAMinutos(horaActual);
  
  if (inicioMinutos > finMinutos) {
    return actualMinutos >= inicioMinutos || actualMinutos < finMinutos;
  }
  
  return actualMinutos >= inicioMinutos && actualMinutos < finMinutos;
};

  const obtenerTurnosDisponibles = async (fecha = new Date(), arqueoEnEdicion = null) => {
    try {
      const tiposTurnoResponse = await fetch(`${API_BASE_URL}/tipos-turno`, {
        headers: getAuthHeaders()
      });
      
      if (!tiposTurnoResponse.ok) {
        throw new Error('Error al obtener tipos de turno');
      }
      
      const tiposTurnoData = await tiposTurnoResponse.json();
      const tiposTurno = tiposTurnoData.success ? tiposTurnoData.data : [];
      
      const horaActual = fecha.toTimeString().split(' ')[0];
      const fechaString = fecha.toISOString().split('T')[0];
      
      const turnosDisponibles = [];
      
      for (const turno of tiposTurno) {
        try {
          const arqueoResponse = await fetch(
            `${API_BASE_URL}/arqueo-caja/actual?turnoId=${turno.id}&fecha=${fechaString}`,
            {
              headers: getAuthHeaders()
            }
          );
          
          const arqueoData = await arqueoResponse.json();
          
          const yaExisteArqueo = arqueoResponse.ok && arqueoData.success && arqueoData.data;
          
          const turnoParaValidacion = {
            hora_inicio: turno.horaInicio,
            hora_fin: turno.horaFin
          };
          const estaEnHorario = esTurnoEnHorario(turnoParaValidacion, horaActual);
  
          const esElTurnoEnEdicion = arqueoEnEdicion && turno.id === arqueoEnEdicion.turnoId;
          
          if ((!yaExisteArqueo && estaEnHorario) || esElTurnoEnEdicion) {
            turnosDisponibles.push(turno);
          }
          
        } catch (error) {
          console.error(`Error al verificar arqueo para turno ${turno.id}:`, error);
          const turnoParaValidacion = {
            hora_inicio: turno.horaInicio,
            hora_fin: turno.horaFin
          };
          const estaEnHorario = esTurnoEnHorario(turnoParaValidacion, horaActual);
          const esElTurnoEnEdicion = arqueoEnEdicion && turno.id === arqueoEnEdicion.turnoId;
          
          if (estaEnHorario || esElTurnoEnEdicion) {
            turnosDisponibles.push(turno);
          }
        }
      }
      
      return turnosDisponibles;
    } catch (error) {
      console.error('Error al obtener turnos disponibles:', error);
      return [];
    }
  };

  const useTurnosDisponibles = (arqueoEnEdicion = null) => {
    const [turnosDisponibles, setTurnosDisponibles] = useState([]);
    const [loading, setLoading] = useState(true);
    
    const cargarTurnos = async () => {
      setLoading(true);
      const turnos = await obtenerTurnosDisponibles(new Date(), arqueoEnEdicion);
      setTurnosDisponibles(turnos);
      setLoading(false);
    };
    
    useEffect(() => {
      cargarTurnos();
      
      const intervalo = setInterval(cargarTurnos, 60000);
      
      return () => clearInterval(intervalo);
    }, [arqueoEnEdicion]); 
    
    return { turnosDisponibles, loading, recargarTurnos: cargarTurnos };
  };

const validarFormulario = () => {
  const errores = [];
  
  if (!reporteForm.fechaDesde) {
    errores.push('La fecha desde es requerida');
  }
  
  if (!reporteForm.fechaHasta) {
    errores.push('La fecha hasta es requerida');
  }
  
  if (reporteForm.fechaDesde && reporteForm.fechaHasta) {
    if (new Date(reporteForm.fechaDesde) > new Date(reporteForm.fechaHasta)) {
      errores.push('La fecha desde no puede ser mayor a la fecha hasta');
    }
  }
  
  if (!reporteForm.formato) {
    errores.push('El formato es requerido');
  }
  
  const userData = getUserDataFromToken();
  if (!userData.tenantId || !userData.empresaId) {
    errores.push('Error de autenticación: Por favor, inicie sesión nuevamente');
  }
  
  return errores;
};


const isTokenExpired = () => {
  const userData = getUserDataFromToken();
  if (!userData.exp) return true;
  
  const currentTime = Date.now() / 1000;
  return userData.exp < currentTime;
};


  const formatearEstado = (nombre) => {
    const formatos = {
      'abierto': 'Abierto',
      'cerrado': 'Cerrado',
      'con_diferencia': 'Con diferencia'
    };
    return formatos[nombre] || nombre;
  };

  const formatearTurno = (nombre) => {
    return nombre.charAt(0).toUpperCase() + nombre.slice(1).toLowerCase();
  };

  const [tiposTurno, setTiposTurno] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [estadosArqueo, setEstadosArqueo] = useState([]);
  const [ingresos, setIngresos] = useState([]);
  const [tiposIngreso, setTiposIngreso] = useState([]);

  const obtenerFechaActual = () => {
    const hoy = new Date();
    const year = hoy.getFullYear();
    const month = String(hoy.getMonth() + 1).padStart(2, '0');
    const day = String(hoy.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const arqueoEnEdicion = modalMode === 'edit' ? selectedArqueo : null;
  const { turnosDisponibles, loading: turnosLoading, recargarTurnos } = useTurnosDisponibles(arqueoEnEdicion);

  const [formData, setFormData] = useState({
    fecha: obtenerFechaActual(),
    turnoId: '',
    efectivoInicio: '',
    efectivoReal: '',
    egresos: '',
    observaciones: ''
  });

  const [ingresoForm, setIngresoForm] = useState({
    tipoIngresoId: '',
    monto: '',
    descripcion: '',
    pedidoId: ''
  });

  const API_BASE_URL = '/proxy/api';
  const getAuthHeaders = () => ({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${localStorage.getItem('token')}`,
    'X-Empresa-Id': localStorage.getItem('empresaId') || '1',
    'x-additional-data': localStorage.getItem('x-additional-data'),
  });

useEffect(() => {
    loadInitialData();
    loadArqueos();
  }, []);

  useEffect(() => {
    if (arqueosOriginales.length > 0) {
      aplicarFiltros();
    }
  }, [filtros, arqueosOriginales]);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      
      const [turnosRes, usuariosRes, estadosRes, tiposIngresoRes] = await Promise.all([
        fetch(`${API_BASE_URL}/tipos-turno`, { headers: getAuthHeaders() }),
        fetch(`${API_BASE_URL}/usuarios`, { headers: getAuthHeaders() }),
        fetch(`${API_BASE_URL}/estados-arqueo`, { headers: getAuthHeaders() }),
        fetch(`${API_BASE_URL}/tipos-ingreso-arqueo`, { headers: getAuthHeaders() })
      ]);

      if (!turnosRes.ok || !usuariosRes.ok || !estadosRes.ok || !tiposIngresoRes.ok) {
        throw new Error('Error al cargar datos iniciales');
      }

      const [turnosJson, usuariosJson, estadosJson, tiposIngresoJson] = await Promise.all([
        turnosRes.json(),
        usuariosRes.json(), 
        estadosRes.json(),
        tiposIngresoRes.json()
      ]);

      const turnos = turnosJson.data;
      const usuarios = usuariosJson.data;
      const estados = estadosJson.data;
      const tiposIngreso = tiposIngresoJson.data;

      if (!Array.isArray(turnos) || !Array.isArray(usuarios) || !Array.isArray(estados) || !Array.isArray(tiposIngreso)) {
        throw new Error('Los datos iniciales no tienen el formato esperado');
      }

      setTiposTurno(turnos);
      setUsuarios(usuarios);
      setEstadosArqueo(estados);
      setTiposIngreso(tiposIngreso);
      
    } catch (error) {
      console.error('Error loading initial data:', error);
      setError('Error al cargar datos iniciales');
    } finally {
      setLoading(false);
    }
  };

  const loadArqueos = async () => {
    try {
      setLoading(true);

      const params = new URLSearchParams({
        page: '0',
        size: '100', 
        sort: 'fecha,desc'
      });

      const response = await fetch(`${API_BASE_URL}/arqueo-caja/consultar?${params}`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({}) 
      });

      if (!response.ok) {
        throw new Error('Error al cargar arqueos');
      }

      const data = await response.json();
      const pageData = data.data; 

      setArqueosOriginales(pageData.content || []);
      setArqueosFiltrados(pageData.content || []);
      
      const paginados = paginarArqueos(pageData.content || []);
      setArqueos(paginados);

      const ingresosPromises = paginados.map(arqueo => loadIngresosPorArqueo(arqueo.id));
      await Promise.all(ingresosPromises);

    } catch (error) {
      console.error('Error loading arqueos:', error);
      setError('Error al cargar arqueos');
    } finally {
      setLoading(false);
    }
  };

  const filtrarArqueos = (arqueosOriginales) => {
    return arqueosOriginales.filter(arqueo => {
      if (filtros.fechaDesde) {
        const fechaArqueo = new Date(arqueo.fecha);
        const fechaDesde = new Date(filtros.fechaDesde);
        if (fechaArqueo < fechaDesde) return false;
      }

      if (filtros.fechaHasta) {
        const fechaArqueo = new Date(arqueo.fecha);
        const fechaHasta = new Date(filtros.fechaHasta);
        if (fechaArqueo > fechaHasta) return false;
      }

      if (filtros.turnoId) {
        if (arqueo.turnoId?.toString() !== filtros.turnoId) return false;
      }

      if (filtros.estadoId) {
        if (arqueo.estadoId?.toString() !== filtros.estadoId) return false;
      }

      if (filtros.usuarioId) {
        const usuario = usuarios.find(u => u.id === arqueo.usuarioId);
        if (!usuario) return false;
        
        const nombreCompleto = `${usuario.nombres || ''} ${usuario.apellidos || ''}`.trim();
        const nombreUsuario = usuario.nombreUsuario || '';
        const email = usuario.email || '';
        
        const textoBusqueda = filtros.usuarioId.toLowerCase();
        const coincideUsuario = nombreCompleto.toLowerCase().includes(textoBusqueda) ||
                              nombreUsuario.toLowerCase().includes(textoBusqueda) ||
                              email.toLowerCase().includes(textoBusqueda);
        
        if (!coincideUsuario) return false;
      }
      return true;
    });
  };

  const aplicarFiltros = () => {
    if (!validarFiltros()) {
      setError('Por favor corrija los errores en los filtros');
      return;
    }
    
    const filtrados = filtrarArqueos(arqueosOriginales);
    setArqueosFiltrados(filtrados);
    setPaginacionManual(prev => ({ ...prev, paginaActual: 0 }));
    const paginados = paginarArqueos(filtrados);
    setArqueos(paginados);
  };

    const handlePageChangeLocal = (nuevaPagina) => {
    setPaginacionManual(prev => ({ ...prev, paginaActual: nuevaPagina }));
    
    const paginados = paginarArqueos(arqueosFiltrados);
    setArqueos(paginados);
  };

  const limpiarFiltros = () => {
    setFiltros({
      fechaDesde: '',
      fechaHasta: '',
      turnoId: '',
      estadoId: '',
      usuarioId: ''
    });
    setErroresFiltros({
      fechaDesde: '',
      fechaHasta: ''
    });
    
    setArqueosFiltrados(arqueosOriginales);
    setPaginacionManual(prev => ({ ...prev, paginaActual: 0 }));
    
    const paginados = paginarArqueos(arqueosOriginales);
    setArqueos(paginados);
  };


  const loadIngresos = async (arqueoId) => {
    try {
      const response = await fetch(`${API_BASE_URL}/arqueo-caja/${arqueoId}/ingresos`, {
        headers: getAuthHeaders()
      });

      if (!response.ok) {
        throw new Error('Error al cargar ingresos');
      }

      const data = await response.json();
      setIngresos(data.data);
      
      setIngresosPorArqueo(prev => ({
        ...prev,
        [arqueoId]: data.data
      }));
    } catch (error) {
      console.error('Error loading ingresos:', error);
      setError('Error al cargar ingresos del arqueo');
    }
  };

  const loadIngresosPorArqueo = async (arqueoId) => {
    try {
      const response = await fetch(`${API_BASE_URL}/arqueo-caja/${arqueoId}/ingresos`, {
        headers: getAuthHeaders()
      });

      if (!response.ok) {
        throw new Error('Error al cargar ingresos');
      }

      const data = await response.json();
      setIngresosPorArqueo(prev => ({
        ...prev,
        [arqueoId]: data.data
      }));
    } catch (error) {
      console.error('Error loading ingresos for arqueo:', error);

    }
  };

  const validateForm = () => {
  const errors = [];
  
  if (!formData.turnoId) {
    errors.push('El turno es obligatorio');
  }
  
  if (!formData.efectivoInicio || isNaN(parseFloat(formData.efectivoInicio)) || parseFloat(formData.efectivoInicio) < 0) {
    errors.push('El efectivo inicial es obligatorio y debe ser mayor o igual a 0');
  }
  
  if (formData.egresos && formData.egresos !== '' && (isNaN(parseFloat(formData.egresos)) || parseFloat(formData.egresos) < 0)) {
    errors.push('Los egresos deben ser mayor o igual a 0');
  }
  
  if (formData.efectivoReal && formData.efectivoReal !== '' && (isNaN(parseFloat(formData.efectivoReal)) || parseFloat(formData.efectivoReal) < 0)) {
    errors.push('El efectivo real debe ser mayor o igual a 0');
  }
  
  if (modalMode === 'edit' && !formData.fecha) {
    errors.push('La fecha es obligatoria');
  }

  return errors;
};

  const validateIngresoForm = () => {
    const errors = [];
    
    if (!ingresoForm.tipoIngresoId) {
      errors.push('El tipo de ingreso es obligatorio');
    }
    
    if (!ingresoForm.monto || parseFloat(ingresoForm.monto) <= 0) {
      errors.push('El monto debe ser mayor a cero');
    }
    
    if (!ingresoForm.descripcion.trim()) {
      errors.push('La descripción es obligatoria');
    }

    return errors;
  };

const handleSubmit = async () => {
  try {
    const errors = validateForm();
    if (errors.length > 0) {
      setError(errors.join(', '));
      return;
    }

    if (modalMode === 'create' && formData.turnoId) {
      const turnoSeleccionado = turnosDisponibles.find(t => t.id === parseInt(formData.turnoId));
      
      if (!turnoSeleccionado) {
        setError('Por favor selecciona un turno válido');
        return;
      }
      
      const horaActual = new Date().toTimeString().split(' ')[0];
      const turnoParaValidacion = {
        hora_inicio: turnoSeleccionado.horaInicio,
        hora_fin: turnoSeleccionado.horaFin
      };
      
      if (!esTurnoEnHorario(turnoParaValidacion, horaActual)) {
        setError(`No puedes crear un arqueo para el turno ${turnoSeleccionado.nombre} fuera de su horario`);
        return;
      }
    }

    setLoading(true);
    
    const arqueoData = modalMode === 'create' 
      ? {
          fecha: formData.fecha,
          turnoId: formData.turnoId ? parseInt(formData.turnoId) : null,
          efectivoInicio: formData.efectivoInicio ? parseFloat(formData.efectivoInicio) : null,
          observaciones: formData.observaciones.trim() || null,
          egresos: formData.egresos ? parseFloat(formData.egresos) : null,
          efectivoReal: formData.efectivoReal ? parseFloat(formData.efectivoReal) : null
        }
      : {
          ...formData,
          turnoId: formData.turnoId ? parseInt(formData.turnoId) : null,
          efectivoInicio: formData.efectivoInicio ? parseFloat(formData.efectivoInicio) : null,
          efectivoReal: formData.efectivoReal ? parseFloat(formData.efectivoReal) : null,
          egresos: formData.egresos ? parseFloat(formData.egresos) : null,
          observaciones: formData.observaciones.trim() || null
        };
    
    const url = modalMode === 'create' 
      ? `${API_BASE_URL}/arqueo-caja/crear`
      : `${API_BASE_URL}/arqueo-caja/${selectedArqueo.id}`;
    
    const method = modalMode === 'create' ? 'POST' : 'PUT';

    const response = await fetch(url, {
      method,
      headers: getAuthHeaders(),
      body: JSON.stringify(arqueoData)
    });

    if (!response.ok) {
      let errorData;
      const contentType = response.headers.get('content-type');
      
      if (contentType && contentType.includes('application/json')) {
        try {
          errorData = await response.json();
        } catch (parseError) {
          console.error('Error parsing JSON response:', parseError);
          errorData = { message: 'Error de formato en la respuesta del servidor' };
        }
      } else {
        try {
          const errorText = await response.text();
          errorData = { message: errorText || 'Error desconocido del servidor' };
        } catch (textError) {
          console.error('Error obteniendo texto de respuesta:', textError);
          errorData = { message: 'Error desconocido del servidor' };
        }
      }

      let errorMessage = 'Error al procesar el arqueo';
      if (errorData.message) {
        errorMessage = errorData.message;
      } else if (errorData.errors && Array.isArray(errorData.errors)) {
        errorMessage = errorData.errors.join(', ');
      } else if (errorData.details) {
        errorMessage = JSON.stringify(errorData.details);
      }

      throw new Error(errorMessage);
    }

    const responseData = await response.json();

    setSuccess(`Arqueo ${modalMode === 'create' ? 'creado' : 'actualizado'} exitosamente`);
    setShowModal(false);
    resetForm();
    loadArqueos();
    
  } catch (error) {
    console.error('Error completo en handleSubmit:', error);
    console.error('Error stack:', error.stack);
    setError(error.message || 'Error al procesar el arqueo');
  } finally {
    setLoading(false);
  }
};


  const handleIngresoSubmit = async () => {
    try {
      const errors = validateIngresoForm();
      if (errors.length > 0) {
        setError(errors.join(', '));
        return;
      }

      setLoading(true);
      
      const ingresoData = {
        ...ingresoForm,
        monto: parseFloat(ingresoForm.monto),
        arqueoId: selectedArqueo.id
      };

      const response = await fetch(`${API_BASE_URL}/arqueo-caja/ingresos/registrar`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(ingresoData)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Error al agregar ingreso');
      }

      setSuccess('Ingreso agregado exitosamente');
      resetIngresoForm();
      loadIngresos(selectedArqueo.id);
      loadArqueos();
      
    } catch (error) {
      console.error('Error adding ingreso:', error);
      setError(error.message || 'Error al agregar ingreso');
    } finally {
      setLoading(false);
    }
  };


  const cerrarArqueo = async (id) => {
    if (window.confirm('¿Está seguro de cerrar este arqueo?')) {
      try {
        setLoading(true);
        
        const response = await fetch(`${API_BASE_URL}/arqueo-caja/${id}/cerrar`, {
          method: 'PUT',
          headers: getAuthHeaders()
        });

        if (!response.ok) {
          throw new Error('Error al cerrar el arqueo');
        }

        setSuccess('Arqueo cerrado exitosamente');
        loadArqueos();
      } catch (error) {
        console.error('Error closing arqueo:', error);
        setError('Error al cerrar el arqueo');
      } finally {
        setLoading(false);
      }
    }
  };

  const eliminarIngreso = async (ingresoId) => {
    if (window.confirm('¿Está seguro de eliminar este ingreso?')) {
      try {
        setLoading(true);
        
        const response = await fetch(`${API_BASE_URL}/ingresos-arqueo/${ingresoId}`, {
          method: 'DELETE',
          headers: getAuthHeaders()
        });

        if (!response.ok) {
          throw new Error('Error al eliminar ingreso');
        }

        setSuccess('Ingreso eliminado exitosamente');
        loadIngresos(selectedArqueo.id);
        loadArqueos();
      } catch (error) {
        console.error('Error deleting ingreso:', error);
        setError('Error al eliminar ingreso');
      } finally {
        setLoading(false);
      }
    }
  };

  const resetForm = () => {
    setFormData({
      fecha: new Date().toISOString().split('T')[0],
      turnoId: '',
      efectivoInicio: '',
      efectivoReal: '',
      egresos: '',
      observaciones: ''
    });
    setSelectedArqueo(null);
  };

  const totalPaginasFiltradas = Math.ceil(arqueosFiltrados.length / paginacionManual.elementosPorPagina);

  const hasAdminMensajeriaRole = () => {
    const currentUser = usuarios.find(user => String(user.id) === String(currentUserId));
    const esAdmin = currentUser?.rolId === 2;
    return esAdmin;
  };


  const resetIngresoForm = () => {
    setIngresoForm({
      tipoIngresoId: '',
      monto: '',
      descripcion: '',
      pedidoId: ''
    });
  };

  const openModal = (mode, arqueo = null) => {
  setModalMode(mode);
  if (arqueo) {
    setSelectedArqueo(arqueo);
    setFormData({
      fecha: arqueo.fecha,
      turnoId: arqueo.turnoId?.toString() || '',
      efectivoInicio: arqueo.efectivoInicio?.toString() || '',
      efectivoReal: arqueo.efectivoReal?.toString() || '',
      egresos: arqueo.egresos?.toString() || '',
      observaciones: arqueo.observaciones || ''
    });
  } else {
    resetForm();
  }
  setShowModal(true);
  
  setTimeout(() => {
    const modalElement = document.querySelector('.card.shadow-sm.mb-4.mx-auto');
    if (modalElement) {
      modalElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }, 100);
};

  const openIngresoModal = (arqueo) => {
    setSelectedArqueo(arqueo);
    loadIngresos(arqueo.id);
    resetIngresoForm();
    setShowIngresoModal(true);
  };


  const formatearFecha = (fecha) => {
    return new Date(fecha).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatearMoneda = (valor) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP'
    }).format(valor);
  };

  const calcularTotales = () => {
  const total = arqueosFiltrados.reduce((acc, arqueo) => {
    acc.efectivoInicio += arqueo.efectivoInicio || 0;
    acc.totalIngresos += arqueo.totalIngresos || 0;
    acc.efectivoReal += arqueo.efectivoReal || 0;
    acc.diferencia += arqueo.diferencia || 0;
    acc.egresos += arqueo.egresos || 0;
    return acc;
  }, {
    efectivoInicio: 0,
    totalIngresos: 0,
    efectivoReal: 0,
    diferencia: 0,
    egresos: 0
  });

  return total;
};

  const totales = calcularTotales();

  const [nombreEmpresa, setNombreEmpresa] = useState('');
  useEffect(() => {
    const fetchNombreEmpresa = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/empresas-mensajeria/${empresaId}`, {
          headers: getAuthHeaders(),
        });
        if (response.ok) {
          const data = await response.json();
          setNombreEmpresa(data.data.nombre || ''); 
        }
      } catch (error) {
        console.error('Error al cargar nombre de empresa:', error);
      }
    };

    if (empresaId) {
      fetchNombreEmpresa();
    }
  }, [empresaId]);


  return (
    <>
   <div className="container py-4" style={{ minHeight: 'calc(100vh - 120px)' }}>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
          <i className="bi bi-cash-stack me-2" style={{ color: '#2d572c' }}></i>
          Gestión de arqueos
          {empresaId && (
              <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
                <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
                {nombreEmpresa}
              </span>
            )}
        </h3>
        <div className="d-flex align-items-center gap-3">
          <div className="text-muted">
            <i className="bi bi-info-circle me-1"></i>
            Mostrando {arqueosFiltrados.length} de {arqueosOriginales.length} arqueos
          </div>
          <button
            onClick={() => openModal('create')}
            disabled={loading}
            className="btn btn-success"
              >
                <i className="bi bi-plus-lg me-2"></i>
            Nuevo arqueo
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
                <i className="bi bi-calendar me-1" style={{ color: '#28a745' }}></i>
                Fecha Desde
              </label>
              <input
                type="date"
                name="fechaDesde"
                value={filtros.fechaDesde}
                onChange={handleFiltroChange}
                className={`form-control ${erroresFiltros.fechaDesde ? 'is-invalid' : ''}`}
                max={getFechaMaxima()}
              />
              {erroresFiltros.fechaDesde && (
                <div className="invalid-feedback">{erroresFiltros.fechaDesde}</div>
              )}
            </div>
            
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-calendar me-1" style={{ color: '#ffa420' }}></i>
                Fecha Hasta
              </label>
              <input
                type="date"
                name="fechaHasta"
                value={filtros.fechaHasta}
                onChange={handleFiltroChange}
                className={`form-control ${erroresFiltros.fechaHasta ? 'is-invalid' : ''}`}
                max={getFechaMaxima()}
                min={filtros.fechaDesde || ''}
              />
              {erroresFiltros.fechaHasta && (
                <div className="invalid-feedback">{erroresFiltros.fechaHasta}</div>
              )}
            </div>
            
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-clock me-1" style={{ color: '#007bff' }}></i>
                Turno
              </label>
              <select
                name="turnoId"
                value={filtros.turnoId}
                onChange={handleFiltroChange}
                className="form-select"
              >
                <option value="" disabled hidden>Todos los turnos</option>
                {tiposTurno.map(turno => (
                  <option key={turno.id} value={turno.id}>
                    {formatearTurno(turno.nombre)}
                  </option>
                ))}
              </select>
            </div>
          </div>
          
          <div className="row g-3">
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-flag me-1" style={{ color: '#6f42c1' }}></i>
                Estado
              </label>
              <select
                name="estadoId"
                value={filtros.estadoId}
                onChange={handleFiltroChange}
                className="form-select"
              >
                <option value="" disabled hidden>Todos los estados</option>
                {estadosArqueo.map(estado => (
                  <option key={estado.id} value={estado.id}>
                    {formatearEstado(estado.nombre)}
                  </option>
                ))}
              </select>
            </div>
            
            <div className="col-md-4">
              <label className="form-label fw-semibold">
                <i className="bi bi-person me-1" style={{ color: '#6f42c1' }}></i>
                Usuario
              </label>
                <input
                type="text"
                className="form-control"
                placeholder="Buscar por nombre"
                value={filtros.usuarioId}
                onChange={handleFiltroChange}
                name="usuarioId"
              />
            </div>
            
            <div className="col-md-4 d-flex align-items-end justify-content-end">
              {Object.values(filtros).some(v => v) && (
                <button
                  onClick={limpiarFiltros}
                  className="btn btn-outline-secondary"
                >
                  <i className="bi bi-x-circle me-1"></i>
                  Limpiar filtros
                </button>
              )}
            </div>
          </div>
        </div>
      </div>

      {showModal && (
        <div className="card shadow-sm mb-4 mx-auto" style={{ maxWidth: '800px' }}>
          <div className="card-header bg-primary text-white">
            <i className="bi bi-cash-stack me-2"></i>
            {modalMode === 'create' ? 'Nuevo arqueo' : modalMode === 'edit' ? 'Editar arqueo' : 'Detalle del Arqueo'}
          </div>
          
          {modalMode === 'view' && selectedArqueo ? (
            <div className="p-4">
            </div>
          ) : (
            <form onSubmit={(e) => { e.preventDefault(); handleSubmit(); }} className="p-4">
              <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label">
                    <i className="bi bi-calendar me-1" style={{ color: '#28a745' }}></i>
                    Fecha <span className="text-danger">*</span>
                  </label>
                  <input
                    type="date"
                    name="fecha"
                    value={formData.fecha}
                    onChange={(e) => setFormData({...formData, fecha: e.target.value})}
                    className="form-control"
                    disabled
                    required
                  />
                </div>
                <div className="col-md-6">
                  <label className="form-label">
                    <i className="bi bi-clock me-1" style={{ color: '#007bff' }}></i>
                    Turno <span className="text-danger">*</span>
                  </label>
                  <select
                    name="turnoId"
                    value={formData.turnoId}
                    onChange={(e) => setFormData({...formData, turnoId: parseInt(e.target.value) })}
                    className="form-select"
                    disabled={modalMode === 'edit' || turnosLoading}
                    required
                  >
                    <option value="" disabled hidden>
                      {turnosLoading ? 'Cargando turnos...' : 'Seleccionar turno'}
                    </option>
                    {turnosDisponibles.map(turno => (
                      <option key={turno.id} value={turno.id}>
                        {formatearTurno(turno.nombre)} ({turno.horaInicio} - {turno.horaFin})
                      </option>
                    ))}
                  </select>
                  
                  {turnosDisponibles.length === 0 && !turnosLoading && modalMode !== 'edit' && (
                    <small className="text-muted mt-2 d-block">
                      <i className="bi bi-info-circle me-1"></i>
                      No hay turnos disponibles en este momento. Los turnos solo están disponibles durante sus horarios establecidos y si no tienen arqueo creado.
                    </small>
                  )}                  
                </div>
                <div className="col-md-6">
                  <label className="form-label">
                    <i className="bi bi-cash-coin me-1" style={{ color: '#28a745' }}></i>
                    Efectivo Inicio <span className="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    name="efectivoInicio"
                    value={formData.efectivoInicio}
                    onChange={(e) => setFormData({...formData, efectivoInicio: e.target.value})}
                    className="form-control"
                    placeholder="0.00"
                    required
                    min="0"
                    step="0.01"
                    disabled={modalMode === 'edit'}
                  />
                </div>
                <div className="col-md-6">
                  <label className="form-label">
                    <i className="bi bi-calculator me-1" style={{ color: '#007bff' }}></i>
                    Efectivo Real
                  </label>
                  <input
                    type="number"
                    name="efectivoReal"
                    value={formData.efectivoReal}
                    onChange={(e) => setFormData({...formData, efectivoReal: e.target.value})}
                    className="form-control"
                    placeholder="0.00"
                    min="0"
                    step="0.01"
                  />
                </div>
                <div className="col-md-6">
                  <label className="form-label">
                    <i className="bi bi-arrow-down-circle me-1" style={{ color: '#dc3545' }}></i>
                    Egresos
                  </label>
                  <input
                    type="number"
                    name="egresos"
                    value={formData.egresos}
                    onChange={(e) => setFormData({...formData, egresos: e.target.value})}
                    className="form-control"
                    placeholder="0.00"
                    min="0"
                    step="0.01"
                  />
                </div>
                <div className="col-md-6">
                  <label className="form-label">
                    <i className="bi bi-file-text me-1" style={{ color: '#6c757d' }}></i>
                    Observaciones
                  </label>
                  <input
                    type='text'
                    name="observaciones"
                    value={formData.observaciones}
                    onChange={(e) => setFormData({...formData, observaciones: e.target.value})}
                    className="form-control"
                    placeholder="Observaciones adicionales"
                    maxLength={200}
                  />
                </div>
                <div className="col-12 d-flex justify-content-end gap-2 mt-4">
                  <button 
                    type="submit" 
                    className="btn btn-primary" 
                    disabled={loading}
                  >
                    <i className="bi bi-save me-1"></i>
                    {loading ? 'Guardando...' : 'Guardar'}
                  </button>
                  <button 
                    type="button" 
                    className="btn btn-secondary" 
                    onClick={() => setShowModal(false)}
                  >
                    <i className="bi bi-x-circle me-1"></i>
                    Cancelar
                  </button>
                </div>
              </div>
            </form>
          )}
        </div>
      )}

      {vista === 'estadisticas' && (
        <div className="row g-4 mb-4">
          <div className="col" style={{flex: '1 1 20%'}}>
            <div className="card border-0 shadow-sm">
              <div className="card-body text-center">
                <div className="d-flex justify-content-center mb-2">
                    <i className="bi bi-cash-coin text-success fs-4"></i>
                </div>
                <h6 className="text-muted mb-1">Efectivo Inicial</h6>
                <h4 className="text-success mb-0">{formatearMoneda(totales.efectivoInicio)}</h4>
              </div>
            </div>
          </div>
          <div className="col" style={{flex: '1 1 20%'}}>
            <div className="card border-0 shadow-sm">
              <div className="card-body text-center">
                <div className="d-flex justify-content-center mb-2">
                    <i className="bi bi-graph-up text-info fs-4"></i>
                </div>
                <h6 className="text-muted mb-1">Total Ingresos</h6>
                <h4 className="text-info mb-0">{formatearMoneda(totales.totalIngresos)}</h4>
              </div>
            </div>
          </div>
          <div className="col" style={{flex: '1 1 20%'}}>
            <div className="card border-0 shadow-sm">
              <div className="card-body text-center">
                <div className="d-flex justify-content-center mb-2">
                  
                    <i className="bi bi-calculator text-primary fs-4"></i>
                  
                </div>
                <h6 className="text-muted mb-1">Efectivo Real</h6>
                <h4 className="text-primary mb-0">{formatearMoneda(totales.efectivoReal)}</h4>
              </div>
            </div>
          </div>
            <div className="col" style={{flex: '1 1 20%'}}>
              <div className="card border-0 shadow-sm">
                <div className="card-body text-center">
                  <div className="d-flex justify-content-center mb-2">
                      <i className="bi bi-arrow-down-circle text-danger fs-4"></i>
                  </div>
                  <h6 className="text-muted mb-1">Total Egresos</h6>
                  <h4 className="text-danger mb-0">{formatearMoneda(totales.egresos)}</h4>
                </div>
              </div>
            </div>
            <div className="col" style={{flex: '1 1 20%'}}>
              <div className="card border-0 shadow-sm">
                <div className="card-body text-center">
                  <div className="d-flex justify-content-center mb-2">
                    <i className={`bi ${totales.diferencia >= 0 ? 'bi-arrow-up text-success' : 'bi-arrow-down text-danger'} fs-4`}></i>
                  </div>
                  <h6 className="text-muted mb-1">Diferencia</h6>
                  <h4 className={`mb-0 ${totales.diferencia >= 0 ? 'text-success' : 'text-danger'}`}>
                    {formatearMoneda(totales.diferencia)}
                  </h4>
                </div>
              </div>
            </div>

        </div>
      )}

      {vista === 'lista' && (
        <div className="px-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Cargando...</span>
              </div>
            </div>
          ) : arqueos.length === 0 ? (
            <div className="text-center py-5">
              <i className="bi bi-inbox" style={{ fontSize: '4rem', color: '#dee2e6' }}></i>
              <h4 className="text-muted mt-3">No hay arqueos registrados</h4>
              <p className="text-muted">No se encontraron resultados con los filtros aplicados.</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover text-center align-middle w-100">
                <thead className="table-light">
                  <tr>
                    <th className="text-center">Arqueo</th>
                    <th className="text-center">Fecha</th>
                    <th className="text-center">Usuario</th>
                    <th className="text-center">Turno</th>
                    <th className="text-center">Estado</th>
                    <th className="text-center">Efectivo Inicial</th>
                    <th className="text-center">Total Ingresos</th>
                    <th className="text-center">Efectivo Real</th>
                    <th className="text-center">Egresos</th>
                    <th className="text-center">Diferencia</th>
                    <th className="text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {arqueos.map((arqueo) => {
                    const isOwner = currentUserId === arqueo.usuarioId;
                    return (
                      <tr key={arqueo.id}>
                        <td>
                          <div className="d-flex justify-content-center align-items-center">
                            <i className="bi bi-cash-stack me-2" style={{ color: '#2d572c' }}></i>
                            #{arqueo.id}
                          </div>
                        </td>
                        <td>
                          {new Intl.DateTimeFormat('es-ES', {
                            year: 'numeric',
                            month: '2-digit',
                            day: '2-digit'
                          }).format(new Date(arqueo.fecha))}
                        </td>
                        <td>{arqueo.usuarioNombre}</td>
                        <td>{formatearTurno(arqueo.turnoNombre)}</td>
                        <td>{formatearEstado(arqueo.estadoNombre)}</td>
                        <td>
                          <span className="fw-semibold text-success">
                            {formatearMoneda(arqueo.efectivoInicio)}
                          </span>
                        </td>
                        <td>
                          <span className="fw-semibold text-info">
                            {formatearMoneda(arqueo.totalIngresos)}
                          </span>
                        </td>
                        <td>
                          <span className="fw-semibold text-primary">
                            {formatearMoneda(arqueo.efectivoReal)}
                          </span>
                        </td>
                        <td>
                          <span className="fw-semibold text-danger">
                            {formatearMoneda(arqueo.egresos)}
                          </span>
                        </td>
                        <td>
                          <span className={`fw-semibold ${arqueo.diferencia >= 0 ? 'text-success' : 'text-danger'}`}>
                            {formatearMoneda(arqueo.diferencia)}
                          </span>
                        </td>
                        <td>
                          <div className="btn-group btn-group-sm justify-content-center">
                            <button
                              className="btn btn-outline-secondary"
                              onClick={() => openModal('edit', arqueo)}
                              disabled={arqueo.estadoId === 2 || !isOwner}
                              title={!isOwner ? "Solo el propietario puede editar" : "Editar arqueo"}
                            >
                              <i className="bi bi-pencil"></i>
                            </button>
                            <button
                              className="btn btn-outline-info"
                              onClick={() => openIngresoModal(arqueo)}
                              title={isOwner ? 'Gestionar ingresos' : 'Ver ingresos'}
                            >
                              <i className="bi bi-graph-up"></i>
                            </button>
                            {arqueo.estadoId === 1 && (
                            <button
                              className="btn btn-outline-danger"
                              onClick={() => hasAdminMensajeriaRole() ? cerrarArqueo(arqueo.id) : null}
                              disabled={!hasAdminMensajeriaRole()}
                              title={!hasAdminMensajeriaRole() ? "Solo usuarios con rol admin_mensajeria pueden cerrar el arqueo" : "Cerrar arqueo"}
                            >
                              <i className="bi bi-x-lg"></i>
                            </button>
                          )}
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {vista === 'tarjetas' && (
        loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Cargando...</span>
            </div>
          </div>
        ) : arqueos.length === 0 ? (
          <div className="text-center py-5">
            <i className="bi bi-inbox" style={{ fontSize: '4rem', color: '#dee2e6' }}></i>
            <h4 className="text-muted mt-3">No hay arqueos registrados</h4>
            <p className="text-muted">No se encontraron resultados con los filtros aplicados.</p>
          </div>
        ) : (
          <div className="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
            {arqueos.map((arqueo) => {
              const isOwner = currentUserId === arqueo.usuarioId;

              return (
                <div className="col" key={arqueo.id}>
                  <div className="card h-100 shadow-sm border-0">
                    <div className="card-body">
                      <div className="d-flex justify-content-between align-items-start mb-3">
                        <div>
                          <h6 className="mb-1 fw-bold">
                            <i className="bi bi-cash-stack me-1" style={{ color: '#cca9bd' }}></i>
                            Arqueo #{arqueo.id}
                          </h6>
                        </div>                          
                            {new Intl.DateTimeFormat('es-ES', {
                                  year: 'numeric',
                                  month: '2-digit',
                                  day: '2-digit'
                                }).format(new Date(arqueo.fecha))}                                                                       
                      </div>

                      <div className="mb-3">
                        <div className="row g-2">
                          <div className="col-6">
                            <div className="text-center p-2 bg-light rounded">
                              <div className="text-muted small">Estado</div>
                              <div className="fw-bold">
                                {formatearEstado(arqueo.estadoNombre)}
                              </div>
                            </div>
                          </div>
                          <div className="col-6">
                            <div className="text-center p-2 bg-light rounded">
                              <div className="text-muted small">Turno</div>
                              <div className="fw-bold">{formatearTurno(arqueo.turnoNombre)}</div>
                            </div>
                          </div>
                        </div>
                      </div>

                      <div className="mb-3">
                        <div className="d-flex align-items-center mb-2">
                          <i className="bi bi-person me-2" style={{ color: '#6f42c1' }}></i>
                          <small className="text-muted">Persona responsable:</small>
                        </div>
                        <div className="fw-semibold text-primary">{arqueo.usuarioNombre}</div>
                      </div>

                      <div className="mb-3">
                        <div className="row g-2 text-center">
                          <div className="col-6">
                            <div className="border rounded p-2">
                              <div className="text-success small">Efectivo Inicio</div>
                              <div className="fw-bold">{formatearMoneda(arqueo.efectivoInicio)}</div>
                            </div>
                          </div>
                          <div className="col-6">
                            <div className="border rounded p-2">
                              <div className="text-info small">Total Ingresos</div>
                              <div className="fw-bold">{formatearMoneda(arqueo.totalIngresos)}</div>
                            </div>
                          </div>
                        </div>
                      </div>

                      <div className="mb-3">
                        <div className="row g-2 text-center">
                          <div className="col-6">
                            <div className="border rounded p-2">
                              <div className="text-primary small">Efectivo Real</div>
                              <div className="fw-bold">{formatearMoneda(arqueo.efectivoReal)}</div>
                            </div>
                          </div>
                          <div className="col-6">
                            <div className="border rounded p-2">
                              <div className="text-danger small">Egresos</div>
                              <div className="fw-bold">{formatearMoneda(arqueo.egresos)}</div>
                            </div>
                          </div>
                        </div>
                      </div>

                      <div className="mb-3">
                        <div className="row g-2 text-center justify-content-center">
                          <div className="col-6">
                            <div className="border rounded p-2">
                              <div className={`small ${arqueo.diferencia >= 0 ? 'text-success' : 'text-danger'}`}>
                                Diferencia
                              </div>
                              <div className={`fw-bold ${arqueo.diferencia >= 0 ? 'text-success' : 'text-danger'}`}>
                                {formatearMoneda(arqueo.diferencia)}
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>

                      {arqueo.observaciones && (
                        <div className="mb-3">
                          <div className="d-flex align-items-center mb-1">
                            <i className="bi bi-file-text me-2" style={{ color: '#6c757d' }}></i>
                            <small className="text-muted">Observaciones:</small>
                          </div>
                          <div className="small text-muted">{arqueo.observaciones}</div>
                        </div>
                      )}

                      <div className="d-flex gap-2 flex-wrap">
                        <button
                          className="btn btn-outline-secondary btn-sm flex-fill"
                          onClick={() => openModal('edit', arqueo)}
                          disabled={arqueo.estadoId === 2 || !isOwner}
                          title={!isOwner ? "Solo el propietario puede editar" : ""}
                        >
                          <i className="bi bi-pencil me-1"></i>
                          Editar
                        </button>
                        <button
                          className="btn btn-outline-info btn-sm flex-fill"
                          onClick={() => openIngresoModal(arqueo)}
                        >
                          <i className="bi bi-graph-up me-1"></i>
                          {isOwner ? 'Ingresos' : 'Ver Ingresos'}
                        </button>
                        {arqueo.estadoId === 1 && (
                          <button
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => isOwner ? cerrarArqueo(arqueo.id) : null}
                            disabled={!isOwner}
                            title={!isOwner ? "Solo el propietario puede cerrar el arqueo" : "Cerrar arqueo"}
                          >
                            <i className="bi bi-x-lg me-1"></i>
                            Cerrar Arqueo
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )
      )}

      {totalPaginasFiltradas > 1 && (
        <div className="d-flex justify-content-center mt-4">
          <nav>
            <ul className="pagination">
              <li className={`page-item ${paginacionManual.paginaActual === 0 ? 'disabled' : ''}`}>
                <button
                  className="page-link"
                  onClick={() => handlePageChangeLocal(paginacionManual.paginaActual - 1)}
                  disabled={paginacionManual.paginaActual === 0}
                >
                  Anterior
                </button>
              </li>
              {Array.from({ length: totalPaginasFiltradas }, (_, i) => (
                <li key={i} className={`page-item ${paginacionManual.paginaActual === i ? 'active' : ''}`}>
                  <button
                    className="page-link"
                    onClick={() => handlePageChangeLocal(i)}
                  >
                    {i + 1}
                  </button>
                </li>
              ))}
              <li className={`page-item ${paginacionManual.paginaActual === totalPaginasFiltradas - 1 ? 'disabled' : ''}`}>
                <button
                  className="page-link"
                  onClick={() => handlePageChangeLocal(paginacionManual.paginaActual + 1)}
                  disabled={paginacionManual.paginaActual === totalPaginasFiltradas - 1}
                >
                  Siguiente
                </button>
              </li>
            </ul>
          </nav>
        </div>
      )}

      {showIngresoModal && selectedArqueo && (
        <div
          className="card shadow-sm mb-4 mx-auto"
          style={{
            maxWidth: '95%',
            position: 'fixed',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            zIndex: 1050,
            width: '90vw'
          }}
        >
          <div className="card-header bg-info text-white">
            <i className="bi bi-graph-up me-2"></i>
            Ingresos del arqueo #{selectedArqueo.id}
            {selectedArqueo.estadoId === 2 && (
              <span className="badge bg-secondary ms-2">Solo lectura</span>
            )}
          </div>

          <div className="card-body">
            <div className="row">
              {selectedArqueo.estadoId !== 2 && currentUserId === selectedArqueo.usuarioId && (
                <div className="col-md-4">
                  <div className="card border-0 shadow-sm h-100">
                    <div className="card-header bg-light">
                      <h6 className="mb-0 text-center">Agregar ingreso</h6>
                    </div>
                    <div className="card-body">
                      <div className="mb-3">
                        <label className="form-label">
                          <i className="bi bi-tag me-1" style={{ color: '#6f42c1' }}></i>
                          Tipo de ingreso <span className="text-danger">*</span>
                        </label>
                        <select
                          value={ingresoForm.tipoIngresoId}
                          onChange={(e) =>
                            setIngresoForm({ ...ingresoForm, tipoIngresoId: e.target.value })
                          }
                          className="form-select"
                        >
                        <option value="" disabled hidden>Seleccionar tipo</option>
                        {tiposIngreso.map((tipo) => (
                          <option key={tipo.id} value={tipo.id}>
                            {tipo.nombre
                              .toLowerCase()
                              .replace(/_/g, ' ')
                              .replace(/^\w/, c => c.toUpperCase())
                            }
                          </option>
                          ))}
                        </select>
                      </div>

                      <div className="mb-3">
                        <label className="form-label">
                          <i className="bi bi-cash-coin me-1" style={{ color: '#28a745' }}></i>
                          Monto <span className="text-danger">*</span>
                        </label>
                        <input
                          type="number"
                          value={ingresoForm.monto}
                          onChange={(e) => setIngresoForm({ ...ingresoForm, monto: e.target.value })}
                          className="form-control"
                          step="0.01"
                        />
                      </div>

                      <div className="mb-3">
                        <label className="form-label">
                          <i className="bi bi-file-text me-1" style={{ color: '#6c757d' }}></i>
                          Descripción <span className="text-danger">*</span>
                        </label>
                        <textarea
                          value={ingresoForm.descripcion}
                          onChange={(e) =>
                            setIngresoForm({ ...ingresoForm, descripcion: e.target.value })
                          }
                          className="form-control"
                          rows="3"
                        />
                      </div>

                      <div className="mb-3">
                        <label className="form-label">
                          <i className="bi bi-receipt me-1" style={{ color: '#ff0080' }}></i>
                          Pedido ID
                        </label>
                        <input
                          type="text"
                          value={ingresoForm.pedidoId}
                          onChange={(e) => setIngresoForm({ ...ingresoForm, pedidoId: e.target.value })}
                          className="form-control"
                        />
                      </div>

                      <button
                        onClick={handleIngresoSubmit}
                        className="btn btn-primary w-100"
                        disabled={loading}
                      >
                        <i className="bi bi-plus-circle me-1"></i>
                        Agregar Ingreso
                      </button>
                    </div>
                  </div>
                </div>
              )}

              {currentUserId !== selectedArqueo.usuarioId && (
                <div className="col-12 mb-3">
                  <div className="alert alert-info text-center">
                    <i className="bi bi-info-circle me-2"></i>
                    <strong>Vista de solo lectura:</strong> Este arqueo pertenece a {selectedArqueo.usuarioNombre}. Solo puedes visualizar la información.
                  </div>
                </div>
              )}

              <div
                className={
                  selectedArqueo.estadoId === 2 || currentUserId !== selectedArqueo.usuarioId
                    ? 'col-12'
                    : 'col-md-8'
                }
              >
                <div className="card border-0 shadow-sm h-100">
                  <div className="card-header bg-light text-center">
                    <h6 className="mb-0">Lista de ingresos</h6>
                  </div>
                  <div className="card-body">
                    {ingresos.length === 0 ? (
                      <div className="text-center py-4">
                        <p className="text-muted">No hay ingresos registrados</p>
                      </div>
                    ) : (
                      <div className="table-responsive">
                        <table className="table table-striped table-hover text-center align-middle w-100">
                          <thead className="table-light">
                            <tr>
                              <th><i className="bi bi-tag me-1" style={{ color: '#6f42c1' }}></i>Tipo</th>
                              <th><i className="bi bi-cash-coin me-1" style={{ color: '#28a745' }}></i>Monto</th>
                              <th><i className="bi bi-file-text me-1" style={{ color: '#6c757d' }}></i>Descripción</th>
                              <th><i className="bi bi-receipt me-1" style={{ color: '#ff0080' }}></i>Pedido ID</th>
                              {selectedArqueo.estadoId !== 2 && currentUserId === selectedArqueo.usuarioId && (
                                <th>Acciones</th>
                              )}
                            </tr>
                          </thead>
                          <tbody>
                            {ingresos.map((ingreso) => (
                              <tr key={ingreso.id}>
                                <td>
                                  {ingreso.tipoIngresoNombre
                                    .toLowerCase()
                                    .replace(/_/g, ' ')
                                    .replace(/^\w/, c => c.toUpperCase())
                                  }
                                </td>
                                <td>{formatearMoneda(ingreso.monto)}</td>
                                <td>{ingreso.descripcion}</td>
                                <td>{ingreso.pedidoId || '-'}</td>
                                {selectedArqueo.estadoId !== 2 && currentUserId === selectedArqueo.usuarioId && (
                                  <td>
                                    <button
                                      onClick={() => eliminarIngreso(ingreso.id)}
                                      className="btn btn-outline-danger btn-sm"
                                    >
                                      <i className="bi bi-trash"></i>
                                    </button>
                                  </td>
                                )}
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="card-footer d-flex justify-content-end">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setShowIngresoModal(false)}
            >
              Cerrar
            </button>
          </div>
        </div>
      )}

    </div>
        <Footer />
    </>
  );
}