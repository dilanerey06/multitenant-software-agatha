import { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import Footer from '../../components/Footer'; 
import ModalMapaCliente from '../../components/ModalMapaCliente';

export default function AdminClientes() {
  const [clientes, setClientes] = useState([]);
  const [clientesFiltrados, setClientesFiltrados] = useState([]);
  const [empresas, setEmpresas] = useState([]);
  const [formVisible, setFormVisible] = useState(false);
  const [editingCliente, setEditingCliente] = useState(null);
  const [errores, setErrores] = useState({});
  const [loading, setLoading] = useState(true);
  const [filtroNombre, setFiltroNombre] = useState('');
  const formRef = useRef(null);
  const [mapaVisible, setMapaVisible] = useState(false);
  const [clienteParaMapa, setClienteParaMapa] = useState(null);
  const [coordenadas, setCoordenadas] = useState({ recogida: null, entrega: null });
  const [cargandoMapa, setCargandoMapa] = useState(false);
  const [estados, setEstados] = useState([]);
  const [filtroEstado, setFiltroEstado] = useState('');
  const [vista, setVista] = useState('lista'); 
  const [pedidosSinCliente, setPedidosSinCliente] = useState([]);
  const [modalPedidosVisible, setModalPedidosVisible] = useState(false);
  const [loadingPedidos, setLoadingPedidos] = useState(false);

  const estadoNombre = {
    activo: 'Activo',
    inactivo: 'Inactivo',
    suspendido: 'Suspendido'
  };

  const token = localStorage.getItem('token');
  const additionalData = localStorage.getItem('x-additional-data')
  const headers = {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
    'x-additional-data': additionalData
  };

  useEffect(() => {
    cargarDatos();
  }, []);

  useEffect(() => {
    aplicarFiltro();
  }, [clientes, filtroNombre, filtroEstado]);

  const decodeToken = (token) => {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      console.error("Token inválido", e);
      return {};
    }
  };

  const decoded = decodeToken(token);
  const mensajeriaId = decoded?.mensajeria_id

  const aplicarFiltro = () => {
    let filtrados = Array.isArray(clientes) ? clientes : [];
    
    if (filtroNombre) {
      filtrados = filtrados.filter(cliente =>
        cliente.nombre && cliente.nombre.toLowerCase().includes(filtroNombre.toLowerCase())
      );
    }
    
    if (filtroEstado) {
      filtrados = filtrados.filter(cliente => 
        cliente.estadoId === parseInt(filtroEstado)
      );
    }
    
    setClientesFiltrados(Array.isArray(filtrados) ? filtrados : []);
  };

  const limpiarFiltros = () => {
    setFiltroNombre('');
    setFiltroEstado('');
  };

  const cargarDatos = async () => {
    setLoading(true);
    try {
      const [clientesResponse, empresasResponse, estadosResponse] = await Promise.all([
        axios.get('/proxy/api/clientes', { headers }),
        axios.get('/proxy/api/empresas-mensajeria', { headers }),
        axios.get('/proxy/api/estados-general', { headers }),
      ]);

      if (clientesResponse.data && clientesResponse.data.success === true) {
        const clientesData = clientesResponse.data.data;
        
        if (clientesData && clientesData.content && Array.isArray(clientesData.content)) {
          setClientes(clientesData.content); 
        } 
        else if (Array.isArray(clientesData)) {
          setClientes(clientesData);
        } else {
          console.error('Los datos de clientes no tienen el formato esperado:', clientesData);
          setClientes([]);
        }
      } else {
        console.error('Error al cargar clientes:', clientesResponse.data?.error || 'Respuesta inválida');
        setClientes([]);
      }

      if (estadosResponse.data && estadosResponse.data.success === true) {
        const estadosData = estadosResponse.data.data;
        if (Array.isArray(estadosData)) {
          setEstados(estadosData);
        } else {
          console.error('Los datos de estados no son un array:', estadosData);
          setEstados([]);
        }
      } else {
        console.error('Error al cargar estados:', estadosResponse.data?.error || 'Respuesta inválida');
        setEstados([]);
      }

      if (empresasResponse.data && empresasResponse.data.success === true) {
        const empresasData = empresasResponse.data.data;
        if (Array.isArray(empresasData)) {
          setEmpresas(empresasData);
        } else {
          console.error('Los datos de empresas no son un array:', empresasData);
          setEmpresas([]);
        }
      } else {
        console.error('Error al cargar empresas:', empresasResponse.data?.error || 'Respuesta inválida');
        setEmpresas([]);
      }
    } catch (error) {
      console.error('Error general al cargar datos:', error);
      setClientes([]);
      setEmpresas([]);
    } finally {
      setLoading(false);
    }
  };

  const validarCampo = (name, value) => {
    let error = '';
    if (!value || !value.toString().trim()) {
      error = 'Este campo es obligatorio';
    } else if (name === 'email') {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(value)) {
        error = 'Email no es válido';
      }
    } else if (name === 'descuentoPorcentaje') {
      const descuento = parseFloat(value);
      if (isNaN(descuento)) {
        error = 'Debe ser un número válido';
      } else if (descuento < 0) {
        error = 'No puede ser negativo';
      } else if (descuento > 100) {
        error = 'No puede ser mayor a 100%';
      }
    }
    return error;
  };

  const obtenerNombreEstado = (estadoId) => {
    const estado = estados.find(e => e.id === estadoId);
    if (estado) {
      return estadoNombre[estado.nombre] || estado.nombre
    }
    return 'Sin estado';
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    
    if (name === 'empresaMensajeria') {
      setEditingCliente(prev => ({
        ...prev,
        empresaMensajeria: { id: Number(value) }
      }));
      setErrores(prev => ({
        ...prev,
        empresaMensajeria: value ? '' : 'Debe seleccionar una empresa'
      }));
    } else {
      setEditingCliente(prev => ({ ...prev, [name]: value }));
      const errorCampo = validarCampo(name, value);
      setErrores(prev => ({ ...prev, [name]: errorCampo }));
    }
  };

  const handleEliminar = (id) => {
    if (confirm('¿Eliminar cliente?')) {
      axios.delete(`/proxy/api/clientes/${id}`, { headers })
        .then(() => {
          cargarDatos();
        })
        .catch(err => console.error('Error al eliminar cliente:', err));
    }
  };

  const handleEditar = (cliente) => {
    setEditingCliente({
      ...cliente,
      direcciones: (cliente.direcciones || []).map(direccion => ({
        ...direccion,
        id: direccion.id,
        direccion: {
          ...direccion.direccion,
          id: direccion.direccion?.id,
          estadoId: direccion.direccion?.estado?.id || direccion.direccion?.estadoId || 1
        }
      }))
    });
    setFormVisible(true);
    setErrores({});
    setTimeout(() => {
      formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  };

  const agregarDireccion = () => {
    const nuevaDireccion = {
      direccion: {
        ciudad: '',
        barrio: '',
        direccionCompleta: '',
        esRecogida: false,
        esEntrega: false,
        estadoId: 1
      },
      esPredeterminadaRecogida: false,
      esPredeterminadaEntrega: false
    };

    setEditingCliente(prev => ({
      ...prev,
      direcciones: [...(prev?.direcciones || []), nuevaDireccion]
    }));
  };

  const eliminarDireccion = (index) => {
    setEditingCliente(prev => ({
      ...prev,
      direcciones: prev.direcciones.filter((_, i) => i !== index)
    }));
  };

  const handleDireccionChange = (index, campo, valor) => {
    setEditingCliente(prev => {
      const nuevasDirecciones = [...prev.direcciones];
      
      if (campo === 'esPredeterminadaRecogida' || campo === 'esPredeterminadaEntrega') {
        if (valor) {
          nuevasDirecciones.forEach((dir, i) => {
            if (i !== index) {
              dir[campo] = false;
            }
          });
        }
        nuevasDirecciones[index][campo] = valor;
        
        if (campo === 'esPredeterminadaRecogida') {
          if (!nuevasDirecciones[index].direccion) {
            nuevasDirecciones[index].direccion = {};
          }
          nuevasDirecciones[index].direccion.esRecogida = valor;
        }
        
        if (campo === 'esPredeterminadaEntrega') {
          if (!nuevasDirecciones[index].direccion) {
            nuevasDirecciones[index].direccion = {};
          }
          nuevasDirecciones[index].direccion.esEntrega = valor;
        }
      } 
      else if (campo === 'esRecogida' || campo === 'esEntrega') {
        if (!nuevasDirecciones[index].direccion) {
          nuevasDirecciones[index].direccion = {};
        }
        nuevasDirecciones[index].direccion[campo] = valor;
        
        if (!valor) {
          if (campo === 'esRecogida') {
            nuevasDirecciones[index].esPredeterminadaRecogida = false;
          }
          if (campo === 'esEntrega') {
            nuevasDirecciones[index].esPredeterminadaEntrega = false;
          }
        }
      }
      else {
        if (!nuevasDirecciones[index].direccion) {
          nuevasDirecciones[index].direccion = {};
        }
        nuevasDirecciones[index].direccion[campo] = valor;
      }

      return {
        ...prev,
        direcciones: nuevasDirecciones
      };
    });
  };

  const handleGuardar = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrores({});

    try {
      const nuevosErrores = {};
      
      if (!editingCliente?.nombre?.trim()) {
        nuevosErrores.nombre = 'El nombre es requerido';
      }
      
      if (!editingCliente?.telefono?.trim()) {
        nuevosErrores.telefono = 'El teléfono es requerido';
      }

      if (editingCliente?.direcciones && editingCliente.direcciones.length > 0) {
        editingCliente.direcciones.forEach((clienteDireccion, index) => {
          if (!clienteDireccion.direccion?.ciudad?.trim()) {
            nuevosErrores[`direccion_${index}_ciudad`] = 'La ciudad es requerida';
          }
          if (!clienteDireccion.direccion?.direccionCompleta?.trim()) {
            nuevosErrores[`direccion_${index}_direccionCompleta`] = 'La dirección completa es requerida';
          }
        });
      }

      if (Object.keys(nuevosErrores).length > 0) {
        setErrores(nuevosErrores);
        setLoading(false);
        return;
      }

      const clienteData = {
        nombre: editingCliente.nombre.trim(),
        telefono: editingCliente.telefono.trim(),
        descuentoPorcentaje: editingCliente.descuentoPorcentaje ? 
          parseFloat(editingCliente.descuentoPorcentaje) : 0,
        estadoId: parseInt(editingCliente.estadoId) || 1,
        direcciones: editingCliente.direcciones?.map(clienteDireccion => ({
          direccion: {
            ciudad: clienteDireccion.direccion.ciudad.trim(),
            barrio: clienteDireccion.direccion.barrio?.trim() || '',
            direccionCompleta: clienteDireccion.direccion.direccionCompleta.trim(),
            estadoId: 1
          },
          esPredeterminadaRecogida: clienteDireccion.esPredeterminadaRecogida || false,
          esPredeterminadaEntrega: clienteDireccion.esPredeterminadaEntrega || false
        })) || []
      };

      const token = localStorage.getItem('token');
      const additionalData = localStorage.getItem('x-additional-data');
      if (!token) {
        throw new Error('No hay token de autenticación. Por favor, inicie sesión nuevamente.');
      }

      const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
        'x-additional-data': additionalData,
      };

      let response;
      let url;
      
      if (editingCliente?.id) {
        url = `/proxy/api/clientes/${editingCliente.id}`;
        response = await fetch(url, {
          method: 'PUT',
          headers: headers,
          body: JSON.stringify({...clienteData, id: editingCliente.id})
        });
      } else {
        url = '/proxy/api/clientes';
        response = await fetch(url, {
          method: 'POST',
          headers: headers,
          body: JSON.stringify(clienteData)
        });
      }

      if (!response.ok) {
        const errorText = await response.text();
        
        if (response.status === 401 || response.status === 403) {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          localStorage.removeItem('x-additional-data');
          alert('Sesión expirada. Por favor, inicie sesión nuevamente.');
          window.location.href = '/login';
          return;
        }
        
        throw new Error(`HTTP ${response.status}: ${errorText}`);
      }

      const contentType = response.headers.get('content-type');
      let result;
      
      if (contentType && contentType.includes('application/json')) {
        result = await response.json();
      } else {
        result = { success: true, message: 'Cliente guardado exitosamente' };
      }

      alert('Cliente guardado exitosamente');
      
      if (typeof cargarDatos === 'function') {
        await cargarDatos();
      }
      
      setFormVisible(false);
      setEditingCliente(null);
      setErrores({});

    } catch (error) {
      console.error('Error completo al guardar cliente:', error);
      
      let errorMessage = 'Error desconocido';
      
      if (error.message.includes('HTTP 400')) {
        errorMessage = 'Datos inválidos. Revise la información ingresada.';
      } else if (error.message.includes('HTTP 401') || error.message.includes('HTTP 403')) {
        errorMessage = 'Sesión expirada. Por favor, inicie sesión nuevamente.';
      } else if (error.message.includes('HTTP 409')) {
        try {
          const match = error.message.match(/\{.*\}/);
          if (match) {
            const errorObj = JSON.parse(match[0]);
            errorMessage = errorObj.error || 'Ya existe un cliente con ese teléfono.';
          }
        } catch {
          errorMessage = 'Ya existe un cliente con ese teléfono.';
        }
      } else if (error.message.includes('HTTP 500')) {
        errorMessage = 'Error interno del servidor. Contacte al administrador.';
      } else if (error.message.includes('fetch')) {
        errorMessage = 'Error de conexión. Verifique que el servidor esté funcionando.';
      } else if (error.message.includes('token')) {
        errorMessage = error.message;
      } else {
        errorMessage = error.message;
      }
      
      alert('Error: ' + errorMessage);
    } finally {
      setLoading(false);
    }
  }

  const getEmpresaName = (empresaId) => {
    if (!empresaId || empresas.length === 0) return 'N/A';
    const empresa = empresas.find(e => e.id === empresaId);
    return empresa ? empresa.nombre : 'N/A';
  };

  const obtenerPedidosSinCliente = async () => {
    setLoadingPedidos(true);
    try {
      const response = await fetch('/proxy/api/pedidos/sin-cliente', {
      method: 'GET',
      headers: headers
    });
      if (response.ok) {
        const data = await response.json();
        setPedidosSinCliente(data.data || []);
        setModalPedidosVisible(true);
      } else {
        console.error('Error al obtener pedidos sin cliente');
      }
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoadingPedidos(false);
    }
  };

  const agregarDireccionDesdePedido = (pedido) => {
    const nuevasDirecciones = [];
    
    if (pedido.direccionRecogidaTemporal) {
      nuevasDirecciones.push({
        direccion: {
          ciudad: pedido.ciudadRecogida,
          barrio: pedido.barrioRecogida,
          direccionCompleta: pedido.direccionRecogidaTemporal,
          esRecogida: true,
          esEntrega: false
        },
        esPredeterminadaRecogida: true, 
        esPredeterminadaEntrega: false
      });
    }
    
    if (pedido.direccionEntregaTemporal && 
        pedido.direccionEntregaTemporal !== pedido.direccionRecogidaTemporal) {
      nuevasDirecciones.push({
        direccion: {
          ciudad: pedido.ciudadEntrega,
          barrio: pedido.barrioEntrega,
          direccionCompleta: pedido.direccionEntregaTemporal,
          esRecogida: false,
          esEntrega: true
        },
        esPredeterminadaRecogida: false,
        esPredeterminadaEntrega: true 
      });
    }
    
    if (pedido.direccionRecogidaTemporal === pedido.direccionEntregaTemporal) {
      nuevasDirecciones[0].direccion.esRecogida = true;
      nuevasDirecciones[0].direccion.esEntrega = true;
      nuevasDirecciones[0].esPredeterminadaRecogida = true;
      nuevasDirecciones[0].esPredeterminadaEntrega = true;
    }
    
    setEditingCliente(prev => ({
      ...prev,
      direcciones: [...(prev.direcciones || []), ...nuevasDirecciones]
    }));
    
    setModalPedidosVisible(false);
  };

  const mostrarMapa = async (cliente) => {
    setCargandoMapa(true);
    setClienteParaMapa(cliente);
    setMapaVisible(true);
    
    try {
      const direccionRecogida = cliente.direcciones?.find(cd => cd.esPredeterminadaRecogida);
      const direccionEntrega = cliente.direcciones?.find(cd => cd.esPredeterminadaEntrega);
      
      if (!direccionRecogida || !direccionEntrega) {
        alert('El cliente no tiene direcciones predeterminadas configuradas para recogida y/o entrega.');
        return;
      }
      
      const direccionRecogidaCompleta = direccionRecogida.direccion?.direccionCompleta;
      const direccionEntregaCompleta = direccionEntrega.direccion?.direccionCompleta;
      const ciudadRecogida = direccionRecogida.direccion?.ciudad;
      const ciudadEntrega = direccionEntrega.direccion?.ciudad;
      
      if (!direccionRecogidaCompleta || !direccionEntregaCompleta) {
        alert('Las direcciones predeterminadas no tienen información completa.');
        return;
      }
      
      const [coordRecogida, coordEntrega] = await Promise.all([
        geocodificarDireccionMejorada(direccionRecogidaCompleta, ciudadRecogida),
        geocodificarDireccionMejorada(direccionEntregaCompleta, ciudadEntrega)
      ]);
      
      if (!coordRecogida && !coordEntrega) {
        alert('No se pudieron encontrar las ubicaciones en el mapa. Verifique las direcciones.');
        return;
      }

      if (!coordRecogida || !coordEntrega) {
        alert('Solo se pudo encontrar una de las ubicaciones. El mapa puede no mostrar la ruta completa.');
      }
      
      setCoordenadas({
        recogida: coordRecogida,
        entrega: coordEntrega
      });
      
    } catch (error) {
      console.error('Error durante la geocodificación:', error);
      alert('Error al cargar las ubicaciones en el mapa. Intente nuevamente.');
    } finally {
      setCargandoMapa(false);
    }
  };
    
  const geocodificarDireccionMejorada = async (direccion, ciudad) => {
    const limpiarDireccion = (dir) => {
      return dir
        .replace(/,\s*Local\s+\d+/gi, '') 
        .replace(/,\s*Piso\s+\d+/gi, '') 
        .replace(/,\s*Oficina\s+\d+/gi, '') 
        .replace(/,\s*Apartamento\s+\d+/gi, '') 
        .replace(/,\s*Apto\s+\d+/gi, '') 
        .replace(/,\s*Torre\s+[A-Z]\d*/gi, '') 
        .replace(/,\s*Bloque\s+\d+/gi, '') 
        .replace(/\s+/g, ' ') 
        .trim();
    };

    const generarVariacionesBusqueda = (direccion, ciudad) => {
      const direccionLimpia = limpiarDireccion(direccion);
      const ciudadNormalizada = ciudad ? ciudad.trim() : '';
      
      const variaciones = [];
      
      if (ciudadNormalizada) {
        const ciudadLimpia = ciudadNormalizada.toLowerCase();
        
        if (!direccionLimpia.toLowerCase().includes(ciudadLimpia)) {
          variaciones.push(
            `${direccionLimpia}, ${ciudadNormalizada}, Colombia`,
            `${direccionLimpia}, ${ciudadNormalizada}`
          );
          
          if (ciudadLimpia.includes('bucaramanga')) {
            variaciones.push(
              `${direccionLimpia}, Bucaramanga, Santander, Colombia`,
              `${direccionLimpia}, Bucaramanga, Santander`
            );
          } else if (ciudadLimpia.includes('bogota') || ciudadLimpia.includes('bogotá')) {
            variaciones.push(
              `${direccionLimpia}, Bogotá, Cundinamarca, Colombia`,
              `${direccionLimpia}, Bogotá DC, Colombia`
            );
          } else if (ciudadLimpia.includes('medellin') || ciudadLimpia.includes('medellín')) {
            variaciones.push(
              `${direccionLimpia}, Medellín, Antioquia, Colombia`
            );
          } else if (ciudadLimpia.includes('cali')) {
            variaciones.push(
              `${direccionLimpia}, Cali, Valle del Cauca, Colombia`
            );
          } else if (ciudadLimpia.includes('barranquilla')) {
            variaciones.push(
              `${direccionLimpia}, Barranquilla, Atlántico, Colombia`
            );
          }
        }
      }
      
      variaciones.push(
        direccion,
        direccionLimpia
      );
      
      if (!direccionLimpia.toLowerCase().includes('colombia')) {
        variaciones.push(`${direccionLimpia}, Colombia`);
      }
      
      variaciones.push(
        direccionLimpia.replace('#', 'No. '),
        direccionLimpia.replace('#', '-'),
        direccionLimpia.replace('Avenida', 'Av'),
        direccionLimpia.replace('Carrera', 'Cra'),
        direccionLimpia.replace('Calle', 'Cl')
      );

      return [...new Set(variaciones.filter(v => v && v.trim().length > 0))];
    };

    const formatosDireccion = generarVariacionesBusqueda(direccion, ciudad);
    
    for (let i = 0; i < formatosDireccion.length; i++) {
      const formato = formatosDireccion[i];
      
      try {
        const coordenadas = await intentarGeocodificacion(formato);
        if (coordenadas) {
          return coordenadas;
        }
        
        await new Promise(resolve => setTimeout(resolve, 500));
        
      } catch (error) {
        continue;
      }
    }

    return null;
  };

  const geocodificarConNominatim = async (direccion) => {
    const url = `https://nominatim.openstreetmap.org/search?` + 
      `format=json&` +
      `q=${encodeURIComponent(direccion)}&` +
      `limit=1&` +
      `countrycodes=co&` +
      `addressdetails=1`;

    const response = await fetch(url, {
      headers: {
        'User-Agent': 'DeliveryApp/1.0' 
      }
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    const data = await response.json();

    if (data && data.length > 0) {
      const resultado = data[0];
      
      const lat = parseFloat(resultado.lat);
      const lng = parseFloat(resultado.lon);
      
      if (lat >= -4.2 && lat <= 12.5 && lng >= -81.8 && lng <= -66.8) {
        return { lat, lng };
      }
    }

    return null;
  };

  const geocodificarConPhoton = async (direccion) => {
    const url = `https://photon.komoot.io/api/?` +
      `q=${encodeURIComponent(direccion)}&` +
      `limit=1&` +
      `lang=es`;

    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    const data = await response.json();

    if (data && data.features && data.features.length > 0) {
      for (const feature of data.features) {
        const properties = feature.properties;
        
        if (properties.country === 'Colombia' || properties.country === 'CO') {
          const [lng, lat] = feature.geometry.coordinates;
          
          if (lat >= -4.2 && lat <= 12.5 && lng >= -81.8 && lng <= -66.8) {
            return { lat, lng };
          }
        }
      }
    }

    return null;
  };

  const intentarGeocodificacion = async (direccion) => {
    try {
      const coordenadas = await geocodificarConNominatim(direccion);
      if (coordenadas) return coordenadas;
    } catch (error) {
      console.warn('Error con Nominatim:', error.message);
    }

    try {
      const coordenadas = await geocodificarConPhoton(direccion);
      if (coordenadas) return coordenadas;
    } catch (error) {
      console.warn('Error con Photon:', error.message);
    }

    return null;
  };

  const renderTablaClientes = () => (
    <div className="table-responsive">
      <table className="table table-hover align-middle">
        <thead className="table-light text-center">
          <tr>
            <th>Cliente</th>
            <th>Teléfono</th>
            <th>Estado</th>
            <th>Direcciones</th>
            <th>Descuento</th>
            <th>Último pedido</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {clientesFiltrados.map((cliente) => (
            <tr key={cliente.id}>
              <td className="align-middle">
                <div className="d-flex align-items-center">
                  <div className="avatar-circle me-3">
                    <i className="bi bi-person fs-5" style={{ color: '#20b2aa' }}></i>
                  </div>
                  <div>
                    <div className="fw-semibold">{cliente.nombre}</div>
                    <small className="text-muted">ID: {cliente.id}</small>
                  </div>
                </div>
              </td>

              <td className="text-center align-middle">{cliente.telefono}</td>

              <td className="text-center align-middle">
                <span className={`badge bg-${cliente.estadoId === 1 ? 'success' : 'danger'} bg-opacity-10 text-${cliente.estadoId === 1 ? 'success' : 'danger'}`}>
                  {obtenerNombreEstado(cliente.estadoId)}
                </span>
              </td>

              <td className="align-middle">
                <div style={{ minWidth: '250px' }}>
                  {cliente.direcciones?.map((dir) => {
                    if (dir.esPredeterminadaRecogida || dir.esPredeterminadaEntrega) {
                      return (
                        <div key={dir.id} className="mb-1 small">
                          <div className="d-flex align-items-center">
                            {dir.esPredeterminadaRecogida && (
                              <span className="badge bg-success me-2">
                                <i className="bi bi-box-arrow-up"></i>
                              </span>
                            )}
                            {dir.esPredeterminadaEntrega && (
                              <span className="badge bg-primary me-2">
                                <i className="bi bi-box-arrow-in-down"></i>
                              </span>
                            )}
                            <div className="text-truncate" title={dir.direccion?.direccionCompleta}>
                              {dir.direccion?.direccionCompleta}, {dir.direccion?.ciudad}
                            </div>
                          </div>
                        </div>
                      );
                    }
                    return null;
                  })}
                  {cliente.direcciones?.length > 2 && (
                    <small className="text-muted">
                      +{cliente.direcciones.length - cliente.direcciones.filter(dir => dir.esPredeterminadaRecogida || dir.esPredeterminadaEntrega).length} más
                    </small>
                  )}
                </div>
              </td>

              <td className="text-center align-middle">
                {cliente.descuentoPorcentaje != null ? (
                  <span className="badge bg-success bg-opacity-10 text-success">
                    {cliente.descuentoPorcentaje}%
                  </span>
                ) : 'N/A'}
              </td>

              <td className="text-center align-middle">
                <small className="text-muted">
                  {cliente.ultimoPedido ? new Date(cliente.ultimoPedido).toLocaleDateString() : 'N/A'}
                </small>
              </td>

              <td className="text-center align-middle">
                <div className="btn-group btn-group-sm">
                  <button 
                    className="btn btn-outline-primary" 
                    onClick={() => handleEditar(cliente)}
                  >
                    <i className="bi bi-pencil"></i>
                  </button>
                  {cliente.direcciones?.length > 0 && (
                    <button 
                      className="btn btn-outline-info"
                      onClick={() => mostrarMapa(cliente)}
                    >
                      <i className="bi bi-map"></i>
                    </button>
                  )}
                  <button 
                    className="btn btn-outline-danger"
                    onClick={() => handleEliminar(cliente.id)}
                  >
                    <i className="bi bi-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );


  const renderTarjetasClientes = () => (
    <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      {clientesFiltrados.map((cliente) => (
        <div className="col" key={cliente.id}>
          <div className="card h-100 shadow-sm">
            <div className="card-body">
              <div className="text-center mb-3">
                <i className="bi bi-person-circle" style={{ fontSize: '3rem', color: '#20b2aa' }}></i>
              </div>

              <h5 className="card-title text-center mb-3">{cliente.nombre}</h5>

              <div className="mb-2">
                <small className="text-muted">
                  <i className="bi bi-telephone me-1" style={{ color: '#28a745' }}></i>
                  Teléfono:
                </small>
                <div className="fw-semibold">{cliente.telefono}</div>
              </div>

              <div className="mb-2">
                <small className="text-muted">
                  <i className="bi bi-circle-fill me-1" style={{ 
                    color: cliente.estadoId === 1 ? '#28a745' : '#dc3545' 
                  }}></i>
                  Estado:
                </small>
                <div>{obtenerNombreEstado(cliente.estadoId)}</div>
              </div>

              {cliente.descuentoPorcentaje !== undefined && cliente.descuentoPorcentaje !== null && (
                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-percent me-1" style={{ color: '#ffc107' }}></i>
                    Descuento:
                  </small>
                  <div className="fw-semibold text-success">
                    {cliente.descuentoPorcentaje}%
                  </div>
                </div>
              )}

              {cliente.direcciones && cliente.direcciones.length > 0 && (
                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-geo-alt me-1" style={{ color: '#dc3545' }}></i>
                    Direcciones:
                  </small>
                  <div style={{ 
                    ...(cliente.direcciones.length > 2 && { 
                      maxHeight: '120px', 
                      overflowY: 'auto' 
                    })
                  }}>
                    {cliente.direcciones
                      .slice()
                      .sort((a, b) => {
                        if (a.esPredeterminadaRecogida) return -1;
                        if (b.esPredeterminadaRecogida) return 1;
                        if (a.esPredeterminadaEntrega) return -1;
                        if (b.esPredeterminadaEntrega) return 1;
                        return 0;
                      })
                      .map((clienteDireccion, index) => {
                        const direccion = clienteDireccion.direccion;
                        const esNoPredeterminada =
                          !clienteDireccion.esPredeterminadaRecogida &&
                          !clienteDireccion.esPredeterminadaEntrega;

                        return (
                          <div key={clienteDireccion.id || index} className="small mb-1">
                            <div className="d-flex align-items-start">
                              <div className="flex-grow-1">
                                {direccion && (
                                  <>
                                    <div className="text-truncate" title={direccion.direccionCompleta}>
                                      <strong>
                                        {direccion.ciudad && `${direccion.ciudad}, `}
                                        {direccion.barrio}
                                      </strong>
                                    </div>
                                    <div className="text-muted small text-truncate">
                                      {direccion.direccionCompleta}
                                    </div>
                                    <div className="text-muted" style={{ fontSize: '0.7rem' }}>
                                      {esNoPredeterminada && (
                                        <span className="me-2">
                                          <i className="bi bi-geo me-1"></i>
                                          Otra
                                        </span>
                                      )}
                                      {direccion.esRecogida && (
                                        <span className="me-2">
                                          <i className="bi bi-box-arrow-up me-1"></i>
                                          Predeterminada recogida
                                        </span>
                                      )}
                                      {direccion.esEntrega && (
                                        <span>
                                          <i className="bi bi-box-arrow-in-down me-1"></i>
                                          Predeterminada entrega
                                        </span>
                                      )}
                                    </div>
                                  </>
                                )}
                              </div>
                              <div className="ms-2">
                                {clienteDireccion.esPredeterminadaRecogida && (
                                  <span
                                    className="badge bg-success me-1"
                                    title="Dirección predeterminada de recogida"
                                  >
                                    <i className="bi bi-arrow-up-circle"></i>
                                  </span>
                                )}
                                {clienteDireccion.esPredeterminadaEntrega && (
                                  <span
                                    className="badge bg-primary"
                                    title="Dirección predeterminada de entrega"
                                  >
                                    <i className="bi bi-arrow-down-circle"></i>
                                  </span>
                                )}
                              </div>
                            </div>
                          </div>
                        );
                      })}
                  </div>
                </div>
              )}

              {cliente.ultimoPedido && (
                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-clock me-1" style={{ color: '#6c757d' }}></i>
                    Último pedido:
                  </small>
                  <div className="small">
                    {new Date(cliente.ultimoPedido).toLocaleDateString()}
                  </div>
                </div>
              )}
            </div>

            <div className="card-footer d-flex justify-content-center gap-1 bg-light">
              <button
                className="btn btn-sm btn-outline-primary"
                onClick={() => handleEditar(cliente)}
              >
                <i className="bi bi-pencil-square me-1"></i>Editar
              </button>
              {cliente.direcciones && cliente.direcciones.length > 0 && (
                <button
                  className="btn btn-sm btn-outline-info"
                  onClick={() => mostrarMapa(cliente)}
                  title="Ver direcciones en mapa"
                >
                  <i className="bi bi-map me-1"></i>Mapa
                </button>
              )}
              <button
                className="btn btn-sm btn-outline-danger"
                onClick={() => handleEliminar(cliente.id)}
              >
                <i className="bi bi-trash-fill me-1"></i>Eliminar
              </button>
            </div>
          </div>
        </div>
      ))}
    </div>
  );

  if (loading) {
    return (
      <>
        <div className="container py-4" style={{ minHeight: 'calc(100vh - 120px)' }}>
          <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
            <i className="bi bi-hammer me-2" style={{ color: '#20b2aa' }}></i>
            Gestión de clientes

            {mensajeriaId && (
              <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
                <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
                {getEmpresaName(mensajeriaId)}
              </span>
            )}
          </h3>
          <div className="text-center mt-5">
            <div className="spinner-border text-primary" role="status" />
            <p className="mt-3">Cargando clientes...</p>
          </div>
        </div>

        <Footer />
      </>
    );
  }

  return (
    <>
      <div className="container py-4" style={{ minHeight: 'calc(100vh - 120px)' }}>
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
            <i className="bi bi-hammer me-2" style={{ color: '#20b2aa' }}></i>
            Gestión de clientes
            {mensajeriaId && (
              <span className="text-muted ms-3" style={{ fontSize: '30px' }}>
                <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
                {getEmpresaName(mensajeriaId)}
              </span>
            )}
          </h3>
          <div className="d-flex align-items-center gap-3">
              <div className="text-muted">
                <i className="bi bi-info-circle me-1"></i>
                Mostrando {clientesFiltrados.length} de {clientes.length} clientes
              </div>
              <button
                onClick={() => {
                  setFormVisible(true);
                  setEditingCliente({
                    nombre: '',
                    telefono: '',
                    direcciones: []
                  });
                  setErrores({});
                  setTimeout(() => {
                    formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
                  }, 100);
                }}
                className="btn btn-success"
              >
                <i className="bi bi-plus-lg me-2"></i>
                Nuevo cliente
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
                  <i className="bi bi-search me-1" style={{ color: '#6c757d' }}></i>
                  Buscar cliente
                </label>
                <input
                  type="text"
                  value={filtroNombre}
                  onChange={(e) => setFiltroNombre(e.target.value)}
                  className="form-control"
                  placeholder="Buscar por nombre de cliente"
                />
              </div>
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-circle-fill me-1" style={{ color: '#007bff' }}></i>
                  Estado
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
                        <div className="col-md-4 d-flex flex-column justify-content-end align-items-end">
                {(filtroNombre || filtroEstado) && (
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

        {formVisible && (
          <div ref={formRef} className="card shadow-sm mb-4 mx-auto" style={{maxWidth:'800px'}}>
            <div className="card-header bg-primary text-white">
              <i className="bi bi-person-plus me-2"></i>
              {editingCliente?.id ? 'Editar cliente' : 'Nuevo cliente'}
            </div>
            <div className="card-body">
              <form onSubmit={handleGuardar}>
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-person me-1" style={{color:'#20b2aa'}}></i>
                      Nombre <span className="text-danger">*</span>
                    </label>
                    <input 
                      type="text" 
                      name="nombre" 
                      value={editingCliente?.nombre || ''} 
                      onChange={handleInputChange} 
                      className={`form-control ${errores.nombre ? 'is-invalid' : ''}`} 
                      placeholder="Nombre del cliente"
                      required
                    />
                    {errores.nombre && (
                      <div className="invalid-feedback">{errores.nombre}</div>
                    )}
                  </div>
                  
                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-telephone me-1" style={{color:'#28a745'}}></i>
                      Teléfono <span className="text-danger">*</span>
                    </label>
                    <input 
                      type="text" 
                      name="telefono" 
                      value={editingCliente?.telefono || ''} 
                      onChange={handleInputChange} 
                      className={`form-control ${errores.telefono ? 'is-invalid' : ''}`} 
                      placeholder="Teléfono del cliente"
                      required
                    />
                    {errores.telefono && (
                      <div className="invalid-feedback">{errores.telefono}</div>
                    )}
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-percent me-1" style={{color:'#ffc107'}}></i>
                      Descuento (%)
                    </label>
                    <input 
                      type="number" 
                      name="descuentoPorcentaje" 
                      value={editingCliente?.descuentoPorcentaje || ''} 
                      onChange={handleInputChange} 
                      className={`form-control ${errores.descuentoPorcentaje ? 'is-invalid' : ''}`} 
                      placeholder="0.00"
                      step="0.01"
                      min="0"
                      max="100"
                    />
                    {errores.descuentoPorcentaje && (
                      <div className="invalid-feedback">{errores.descuentoPorcentaje}</div>
                    )}
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-circle-fill me-1" style={{color:'#007bff'}}></i>
                      Estado 
                    </label>
                    <select 
                        name="estadoId" 
                        value={editingCliente?.estadoId || ''} 
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

                <div className="mb-4">
                  <div className="d-flex justify-content-between align-items-center mb-3">
                    <h6 className="mb-0">
                      <i className="bi bi-geo-alt me-1" style={{color:'#dc3545'}}></i>
                      Direcciones del cliente
                    </h6>
                    <div className="d-flex gap-2">
                    {!editingCliente?.id && (
                      <button 
                        type="button" 
                        className="btn btn-sm btn-outline-info" 
                        onClick={obtenerPedidosSinCliente}
                        disabled={loadingPedidos}
                      >
                        {loadingPedidos ? (
                          <>
                            <span className="spinner-border spinner-border-sm me-1"></span>
                            Cargando...
                          </>
                        ) : (
                          <>
                            <i className="bi bi-box-arrow-up me-1"></i>
                            Direcciones pedidos sin cliente
                          </>
                        )}
                      </button>
                    )}
                    <button 
                      type="button" 
                      className="btn btn-sm btn-outline-success" 
                      onClick={agregarDireccion}
                    >
                      <i className="bi bi-plus-circle me-1"></i>
                      Agregar dirección
                    </button>
                  </div>
                  </div>

                  {editingCliente?.direcciones && editingCliente.direcciones.length > 0 ? (
                    editingCliente.direcciones.map((clienteDireccion, index) => (
                      <div key={index} className="card mb-3 border-light">
                        <div className="card-body">
                          <div className="d-flex justify-content-between align-items-center mb-2">
                            <h6 className="card-title mb-0">Dirección {index + 1}</h6>
                            <button 
                              type="button" 
                              className="btn btn-sm btn-outline-danger" 
                              onClick={() => eliminarDireccion(index)}
                            >
                              <i className="bi bi-trash"></i>
                            </button>
                          </div>

                          <div className="row">
                            <div className="col-md-6 mb-3">
                              <label className="form-label">Ciudad <span className="text-danger">*</span></label>
                              <input 
                                type="text" 
                                value={clienteDireccion.direccion?.ciudad || ''} 
                                onChange={(e) => handleDireccionChange(index, 'ciudad', e.target.value)} 
                                className="form-control" 
                                placeholder="Ciudad"
                                required
                              />
                            </div>
                            
                            <div className="col-md-6 mb-3">
                              <label className="form-label">Barrio</label>
                              <input 
                                type="text" 
                                value={clienteDireccion.direccion?.barrio || ''} 
                                onChange={(e) => handleDireccionChange(index, 'barrio', e.target.value)} 
                                className="form-control" 
                                placeholder="Barrio"
                              />
                            </div>
                          </div>

                          <div className="mb-3">
                            <label className="form-label">Dirección Completa <span className="text-danger">*</span></label>
                            <textarea 
                              value={clienteDireccion.direccion?.direccionCompleta || ''} 
                              onChange={(e) => handleDireccionChange(index, 'direccionCompleta', e.target.value)} 
                              className="form-control" 
                              rows="2"
                              placeholder="Dirección completa"
                              required
                            />
                          </div>

                          <div className="row">
                            <div className="col-md-6 mb-3">
                              <div className="form-check">
                                <input 
                                  className="form-check-input" 
                                  type="checkbox" 
                                  id={`recogida_${index}`}
                                  checked={clienteDireccion.esPredeterminadaRecogida || false} 
                                  onChange={(e) => handleDireccionChange(index, 'esPredeterminadaRecogida', e.target.checked)} 
                                />
                                <label className="form-check-label" htmlFor={`recogida_${index}`}>
                                  <i className="bi bi-box-arrow-up me-1"></i>
                                  Predeterminada para recogida
                                </label>
                              </div>
                            </div>
                            
                            <div className="col-md-6 mb-3">
                              <div className="form-check">
                                <input 
                                  className="form-check-input" 
                                  type="checkbox" 
                                  id={`entrega_${index}`}
                                  checked={clienteDireccion.esPredeterminadaEntrega || false} 
                                  onChange={(e) => handleDireccionChange(index, 'esPredeterminadaEntrega', e.target.checked)} 
                                />
                                <label className="form-check-label" htmlFor={`entrega_${index}`}>
                                  <i className="bi bi-box-arrow-in-down me-1"></i>
                                  Predeterminada para entrega
                                </label>
                              </div>
                            </div>
                          </div>

                          <div className="row">
                            <div className="col-12">
                              <small className="text-muted">
                                <i className="bi bi-info-circle me-1"></i>
                                {clienteDireccion.direccion?.esRecogida && clienteDireccion.direccion?.esEntrega ? 
                                  'Esta dirección está habilitada para recogida y entrega' :
                                  clienteDireccion.direccion?.esRecogida ? 
                                    'Esta dirección está habilitada solo para recogida' :
                                    clienteDireccion.direccion?.esEntrega ?
                                      'Esta dirección está habilitada solo para entrega' :
                                      'Esta dirección no está habilitada para ningún servicio'
                                }
                              </small>
                            </div>
                          </div>
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="alert alert-info">
                      <i className="bi bi-info-circle me-2"></i>
                      No hay direcciones agregadas. Haga clic en "Agregar dirección" para añadir una.
                    </div>
                  )}
                </div>

                <div className="d-flex justify-content-end gap-2">
                  <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                        Guardando...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-save me-1"></i>
                        Guardar
                      </>
                    )}
                  </button>
                  <button 
                    type="button" 
                    className="btn btn-secondary" 
                    onClick={() => {
                      setFormVisible(false);
                      setEditingCliente(null);
                      setErrores({});
                    }}
                  >
                    Cancelar
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Modal para mostrar pedidos sin cliente */}
        {modalPedidosVisible && (
          <div className="modal fade show" style={{display: 'block', backgroundColor: 'rgba(0,0,0,0.5)'}} tabIndex="-1">
            <div className="modal-dialog modal-lg">
              <div className="modal-content">
                <div className="modal-header bg-info text-white">
                  <h5 className="modal-title">
                    <i className="bi bi-box-arrow-up me-2"></i>
                    Direcciones de pedidos sin cliente
                  </h5>
                  <button 
                    type="button" 
                    className="btn-close btn-close-white" 
                    onClick={() => setModalPedidosVisible(false)}
                  ></button>
                </div>
                <div className="modal-body">
                  {pedidosSinCliente.length > 0 ? (
                    <div className="row">
                      {pedidosSinCliente.map((pedido) => (
                        <div key={pedido.id} className="col-md-6 mb-3">
                          <div className="card h-100 border-info">
                            <div className="card-header bg-light">
                              <h6 className="card-title mb-0">
                                <i className="bi bi-box-seam-fill me-2" style={{ color: '#252850' }}></i>
                                Pedido #{pedido.id}
                              </h6>
                            </div>
                            <div className="card-body">
                              <div className="mb-2">
                                <strong>
                                  <i className="bi bi-box-arrow-up me-1 text-success"></i>
                                  Recogida:
                                </strong>
                                <p className="mb-1 small">{pedido.direccionRecogidaTemporal}</p>
                                <small className="text-muted">
                                  {pedido.ciudadRecogida}
                                  {pedido.barrioRecogida && ` - ${pedido.barrioRecogida}`}
                                </small>
                              </div>
                              
                              <div className="mb-3">
                                <strong>
                                  <i className="bi bi-box-arrow-in-down me-1 text-primary"></i>
                                  Entrega:
                                </strong>
                                <p className="mb-1 small">{pedido.direccionEntregaTemporal}</p>
                                <small className="text-muted">
                                  {pedido.ciudadEntrega}
                                  {pedido.barrioEntrega && ` - ${pedido.barrioEntrega}`}
                                </small>
                              </div>
                              
                              <button 
                                className="btn btn-sm btn-outline-info w-100"
                                onClick={() => agregarDireccionDesdePedido(pedido)}
                              >
                                <i className="bi bi-plus-circle me-1"></i>
                                Agregar direcciones
                              </button>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="alert alert-info text-center">
                      <i className="bi bi-info-circle me-2"></i>
                      No hay pedidos sin cliente disponibles.
                    </div>
                  )}
                </div>
                <div className="modal-footer">
                  <button 
                    type="button" 
                    className="btn btn-secondary" 
                    onClick={() => setModalPedidosVisible(false)}
                  >
                    Cerrar
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {clientesFiltrados.length === 0 ? (
          <div className="text-center py-5">
            <i className="bi bi-inbox" style={{ fontSize: '4rem', color: '#dee2e6' }}></i>
            <h4 className="text-muted mt-3">
              {clientes.length === 0 ? 'No hay clientes registrados' : 'No se encontraron resultados'}
            </h4>
            <p className="text-muted">
              {clientes.length === 0 
                ? 'Aún no se han registrado clientes en el sistema.' 
                : `Intenta ajustar el filtro de búsqueda. No se encontraron clientes con "${filtroNombre}".`
              }
            </p>
          </div>
        ) : vista === 'tarjetas' ? (
          renderTarjetasClientes()
        ) : (
          renderTablaClientes()
        )}

        <ModalMapaCliente
          mapaVisible={mapaVisible}
          cargandoMapa={cargandoMapa}
          clienteParaMapa={clienteParaMapa}
          coordenadas={coordenadas}
          setMapaVisible={setMapaVisible}
          setClienteParaMapa={setClienteParaMapa}
          setCoordenadas={setCoordenadas}
          setCargandoMapa={setCargandoMapa}
        />
      </div>
      <Footer />
    </>
  );
}