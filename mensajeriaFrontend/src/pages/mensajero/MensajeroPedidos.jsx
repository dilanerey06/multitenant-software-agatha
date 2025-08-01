import { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import Footer from '../../components/Footer';
import ModalMapa from '../../components/ModalMapa';

export default function MensajeroPedidos() {
  const [pedidos, setPedidos] = useState([]);
  const [pedidosFiltrados, setPedidosFiltrados] = useState([]);
  const [empresas, setEmpresas] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [showPedidosComponent, setShowPedidosComponent] = useState(false);
  const [clientesFiltrados, setClientesFiltrados] = useState([]);
  const [showPedidosSinCliente, setShowPedidosSinCliente] = useState(false);
  const [clienteSeleccionado, setClienteSeleccionado] = useState(null);
  const [pedidosCliente, setPedidosCliente] = useState([]);
  const [direccionesRecogida, setDireccionesRecogida] = useState([]);
  const [direccionesEntrega, setDireccionesEntrega] = useState([]);
  const [mensajeros, setMensajeros] = useState([]);
  const [historialPedidos, setHistorialPedidos] = useState(null);
  const [tiposCambioPedido, setTiposCambioPedido] = useState(null);
  const [errores, setErrores] = useState({});
  const [loading, setLoading] = useState(true);
  const [filtroEstado, setFiltroEstado] = useState('');
  const [filtroCliente, setFiltroCliente] = useState('');
  const [busquedaCliente, setBusquedaCliente] = useState('');
  const [mostrarListaClientes, setMostrarListaClientes] = useState(false);
  const formRef = useRef(null);
  const [mapaVisible, setMapaVisible] = useState(false);
  const [pedidoParaMapa, setPedidoParaMapa] = useState(null);
  const [coordenadas, setCoordenadas] = useState({ recogida: null, entrega: null });
  const [cargandoMapa, setCargandoMapa] = useState(false);
  const clienteInputRef = useRef(null);
  const [direcciones, setDirecciones] = useState([]);
  const [tiposServicio, setTiposServicio] = useState([]);
  const [estadosPedido, setEstadosPedido] = useState([]);
  const [clienteBusqueda, setClienteBusqueda] = useState('');
  const [dropdownClienteVisible, setDropdownClienteVisible] = useState(false);
const [tipoServicioBusqueda, setTipoServicioBusqueda] = useState('');
const [dropdownTipoServicioVisible, setDropdownTipoServicioVisible] = useState(false);
const [mensajeroBusqueda, setMensajeroBusqueda] = useState('');
const [dropdownMensajeroVisible, setDropdownMensajeroVisible] = useState(false);
  const [tarifas, setTarifas] = useState([]);
  const [loadingReferencia, setLoadingReferencia] = useState(false); 
  const [showHistorial, setShowHistorial] = useState(false);
  const [usarDireccionTemporal, setUsarDireccionTemporal] = useState({ 
    recogida: false, 
    entrega: false 
  });
  const [guardarDireccionRecogida, setGuardarDireccionRecogida] = useState(false);
  const [guardarDireccionEntrega, setGuardarDireccionEntrega] = useState(false);
const [showPedidosModal, setShowPedidosModal] = useState(false);
  const [ciudadesUsadas, setCiudadesUsadas] = useState({ 
    recogida: [], 
    entrega: [] 
  });
  const [barriosUsados, setBarriosUsados] = useState({ 
    recogida: [], 
    entrega: [] 
  });
  const [tiposPaqueteUsados, setTiposPaqueteUsados] = useState([]);
  const [vista, setVista] = useState('lista');
  const [mostrarModalHistorial, setMostrarModalHistorial] = useState(false);
  const [pedidoSeleccionado, setPedidoSeleccionado] = useState(null);
  const [historialPedidoSeleccionado, setHistorialPedidoSeleccionado] = useState([]);
  const [formData, setFormData] = useState({});

  const token = localStorage.getItem('token');
  const additionalData = localStorage.getItem('x-additional-data');
  const headers = {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
    'x-additional-data': additionalData,
  };

  useEffect(() => {
    cargarDatos();
  }, []);

  const decodeToken = (token) => {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      console.error("Token inválido", e);
      return {};
    }
  };

  const decoded = decodeToken(token);
  const mensajeriaId = decoded?.mensajeria_id;
  const usuarioId = decoded?.user_id;

  const formatearFecha = (fecha) => {
    const fechaObj = fecha ? new Date(fecha) : new Date();
    return fechaObj.toLocaleString('es-CO', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      timeZone: 'America/Bogota'
    });
  };

  const cargarDatos = async () => {
    setLoading(true);
    setLoadingReferencia(true);
    if (!usuarioId) {
      console.error('Usuario ID no disponible');
      setLoading(false);
      return;
    }

    try {
      const [
        pedidosResponse,
        clientesResponse,
        empresasResponse,
        usuariosResponse,
        mensajerosResponse,
        tiposServicioResponse,
        tarifasResponse,
        direccionesResponse,
        estadosPedidoResponse,
        historialPedidoResponse,
        tiposCambioPedidoResponse
      ] = await Promise.all([
        axios.get(`/proxy/api/pedidos/mensajero/${usuarioId}`, { headers }),
        axios.get('/proxy/api/clientes', { headers }),
        axios.get('/proxy/api/empresas-mensajeria', { headers }),
        axios.get('/proxy/api/usuarios', { headers }),
        axios.get('/proxy/api/mensajeros', { headers }),
        axios.get('/proxy/api/tipos-servicio', { headers }),
        axios.get('/proxy/api/tarifas', { headers }),
        axios.get('/proxy/api/direcciones', { headers }),
        axios.get('/proxy/api/estados-pedido', { headers }),
        axios.get('/proxy/api/historial-pedidos', { headers }),
        axios.get('/proxy/api/tipos-cambio-pedido', { headers })
      ]);

      const procesar = (res, setter, nombre) => {
        if (res.data?.success) {
          const datos = res.data.data;
          if (Array.isArray(datos?.content)) setter(datos.content);
          else if (Array.isArray(datos)) setter(datos);
          else {
            console.error(`Formato inválido en ${nombre}:`, datos);
            setter([]);
          }
        } else {
          console.error(`Error en ${nombre}:`, res.data?.error || 'Respuesta inválida');
          setter([]);
        }
      };

      procesar(pedidosResponse, setPedidos, 'pedidos');
      procesar(clientesResponse, setClientes, 'clientes');
      procesar(empresasResponse, setEmpresas, 'empresas');
      procesar(usuariosResponse, setUsuarios, 'usuarios')
      procesar(mensajerosResponse, setMensajeros, 'mensajeros');
      procesar(tiposServicioResponse, setTiposServicio, 'tipos de servicio');
      procesar(tarifasResponse, setTarifas, 'tarifas');
      procesar(direccionesResponse, setDirecciones, 'direcciones');
      procesar(estadosPedidoResponse, setEstadosPedido, 'estados de pedido');
      procesar(historialPedidoResponse, setHistorialPedidos, 'historial de pedidos');
      procesar(tiposCambioPedidoResponse, setTiposCambioPedido, 'tipos de cambio pedido');

    } catch (error) {
      console.error('Error general al cargar datos:', error);
      setPedidos([]);
      setClientes([]);
      setEmpresas([]);
      setUsuarios([]);
      setMensajeros([]);
      setTiposServicio([]);
      setTarifas([]);
      setDirecciones([]);
      setEstadosPedido([]);
      setHistorialPedidos([]);
      setTiposCambioPedido([]);
    } finally {
      setLoading(false);
      setLoadingReferencia(false);
    }
  };

  const formatearNombreEstado = (nombre) => {
    if (!nombre) return '';

    let texto = nombre.toLowerCase().replace(/_/g, ' ');
    texto = texto.replace('transito', 'tránsito');

    return texto.charAt(0).toUpperCase() + texto.slice(1);
  };

 const getDireccionRecogida = (pedido) => {
    if (pedido.direccionRecogidaTemporal) {
        return pedido.direccionRecogidaTemporal;
    }
    
    if (pedido.direccionRecogidaId && direcciones.length > 0) {
        const direccion = direcciones.find(d => d.id === pedido.direccionRecogidaId && d.esRecogida === true);
        return direccion ? direccion.direccionCompleta : 'Dirección no encontrada';
    }
    
    return 'Dirección no especificada';
};

const getDireccionEntrega = (pedido) => {
    if (pedido.direccionEntregaTemporal) {
        return pedido.direccionEntregaTemporal;
    }
    
    if (pedido.direccionEntregaId && direcciones.length > 0) {
        const direccion = direcciones.find(d => d.id === pedido.direccionEntregaId && d.esEntrega === true);
        return direccion ? direccion.direccionCompleta : 'Dirección no encontrada';
    }
    
    return 'Dirección no especificada';
};

const getCiudadBarrioRecogida = (pedido) => {
    if (pedido.ciudadRecogida || pedido.barrioRecogida) {
        return {
            ciudad: pedido.ciudadRecogida || null,
            barrio: pedido.barrioRecogida || null
        };
    }
    
    if (pedido.direccionRecogidaId && direccionesRecogida.length > 0) {
        const direccion = direccionesRecogida.find(d => d.id === pedido.direccionRecogidaId);
        return {
            ciudad: direccion?.ciudad || null,
            barrio: direccion?.barrio || null
        };
    }
    
    return { ciudad: null, barrio: null };
};

const getCiudadBarrioEntrega = (pedido) => {
    if (pedido.ciudadEntrega || pedido.barrioEntrega) {
        return {
            ciudad: pedido.ciudadEntrega || null,
            barrio: pedido.barrioEntrega || null
        };
    }
    
    if (pedido.direccionEntregaId && direccionesEntrega.length > 0) {
        const direccion = direccionesEntrega.find(d => d.id === pedido.direccionEntregaId);
        return {
            ciudad: direccion?.ciudad || null,
            barrio: direccion?.barrio || null
        };
    }
    
    return { ciudad: null, barrio: null };
};


  const getUsuarioName = (usuarioId) => {
    if (!usuarioId || usuarios.length === 0) return 'N/A';
    const usuario = usuarios.find(e => e.id === usuarioId);
    return usuario 
      ? `${usuario.nombreUsuario} - ${usuario.nombres} ${usuario.apellidos}` 
      : 'N/A';
  };


  const getEmpresaName = (empresaId) => {
    if (!empresaId || empresas.length === 0) return 'N/A';
    const empresa = empresas.find(e => e.id === empresaId);
    return empresa ? empresa.nombre : 'N/A';
  };

  const getClienteName = (clienteId) => {
    if (!clienteId || clientes.length === 0) return 'No registrado';
    const cliente = clientes.find(c => c.id === clienteId);
    return cliente ? cliente.nombre : 'N/A';
  };

  const getMensajeroName = (mensajeroId) => {
    if (!mensajeroId) return 'Sin asignar';
    const mensajero = mensajeros.find(m => m.id === mensajeroId);
    return mensajero ? mensajero.nombreUsuario : 'Sin asignar';
  };

  const getTipoServicioName = (tipoServicioId) => {
    if (!tipoServicioId) return 'N/A';
    const tipoServicio = tiposServicio.find(t => t.id === tipoServicioId);
    return tipoServicio ? tipoServicio.nombre : 'N/A';
  };

  const formatearNombre = (nombre) => {
    return nombre
      .replace(/_/g, ' ')
      .charAt(0).toUpperCase() + nombre.replace(/_/g, ' ').slice(1);
  };

  const getTarifaInfo = (tarifaId) => {
    if (!tarifaId) return null;
    const tarifa = tarifas.find(t => t.id === tarifaId);
    return tarifa 
      ? `$${tarifa.valorFijo.toLocaleString('es-CO')}` 
      : 'Sin tarifa';
  };


  const getDireccionInfo = (direccionId) => {
    if (!direccionId) return null;
    const direccion = direcciones.find(d => d.id === direccionId);
    return direccion || null;
  };

  const getEstadoInfo = (estadoId) => {
    if (!estadoId) return { label: 'Sin estado', color: '#6c757d' };
    
    if (estadosPedido.length > 0) {
      const estado = estadosPedido.find(e => e.id === estadoId);
      return estado ? {
        label: estado.nombre,
        color: getColorPorEstado(estado.nombre),
        descripcion: estado.descripcion
      } : { label: 'Estado desconocido', color: '#6c757d' };
    }
    
    const estadosFallback = {
      1: { label: 'Pendiente', color: '#ffc107' },
      2: { label: 'Asignado', color: '#17a2b8' },
      3: { label: 'En tránsito', color: '#007bff' },
      4: { label: 'Entregado', color: '#28a745' },
      5: { label: 'Cancelado', color: '#dc3545' }
    };
    
    return estadosFallback[estadoId] || { label: 'Sin estado', color: '#6c757d' };
  };

  const getColorPorEstado = (nombreEstado) => {
    const colores = {
      'pendiente': '#ffc107',
      'asignado': '#17a2b8', 
      'en_transito': '#007bff',
      'entregado': '#28a745',
      'cancelado': '#dc3545'
    };
    return colores[nombreEstado?.toLowerCase()] || '#6c757d';
  };

  useEffect(() => {
    aplicarFiltros();
  }, [pedidos, filtroEstado, filtroCliente]);

  useEffect(() => {
    if (busquedaCliente.trim() === '') {
      setClientesFiltrados(clientes);
    } else {
      const filtrados = clientes.filter(cliente =>
        cliente.nombre.toLowerCase().includes(busquedaCliente.toLowerCase()) ||
        cliente.email?.toLowerCase().includes(busquedaCliente.toLowerCase()) ||
        cliente.telefono?.includes(busquedaCliente)
      );
      setClientesFiltrados(filtrados);
    }
  }, [busquedaCliente, clientes]);


  
const aplicarDireccionSeleccionada = (tipo, direccionId, direcciones) => {
    
    const direccionSeleccionada = direcciones.find(d => d.id.toString() === direccionId);
    
    if (direccionSeleccionada) {
        if (tipo === 'recogida') {
            
            setFormData(prev => {
                const newData = {
                    ...prev,
                    direccionRecogidaId: direccionId,
                    ciudadRecogida: direccionSeleccionada.ciudad || '',
                    barrioRecogida: direccionSeleccionada.barrio || '',
                    direccionRecogidaTemporal: ''
                };
                return newData;
            });
        } else if (tipo === 'entrega') {
            
            setFormData(prev => {
                const newData = {
                    ...prev,
                    direccionEntregaId: direccionId,
                    ciudadEntrega: direccionSeleccionada.ciudad || '',
                    barrioEntrega: direccionSeleccionada.barrio || '',
                    direccionEntregaTemporal: ''
                };
                return newData;
            });
        }
    } 
};

const cargarDireccionesCliente = async (clienteId) => {
    try {
        const response = await fetch(`/proxy/api/cliente-direccion/cliente/${clienteId}/direcciones`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
                'x-additional-data': additionalData,
            }
        });

        if (response.ok) {
            const data = await response.json();
            const direcciones = data.data || [];
            
            setDireccionesRecogida(direcciones);
            setDireccionesEntrega(direcciones);

            if (!editingPedido?.id) {
                const direccionRecogidaDefault = direcciones.find(d => d.esRecogida === true);
                const direccionEntregaDefault = direcciones.find(d => d.esEntrega === true);

                
                setTimeout(() => {
                    if (direccionRecogidaDefault) {
                        aplicarDireccionSeleccionada('recogida', direccionRecogidaDefault.id.toString(), direcciones);
                    }
                    
                    if (direccionEntregaDefault) {
                        aplicarDireccionSeleccionada('entrega', direccionEntregaDefault.id.toString(), direcciones);
                    } else {
                        const direccionEntregaAlternativa = direcciones.find(d => 
                            d.id !== direccionRecogidaDefault?.id
                        );
                        
                        if (direccionEntregaAlternativa) {
                            aplicarDireccionSeleccionada('entrega', direccionEntregaAlternativa.id.toString(), direcciones);
                        }
                    }
                }, 100);
            }
        }
    } catch (error) {
        console.error('Error al cargar direcciones:', error);
    }
};


useEffect(() => {
}, [formData.clienteId, formData.direccionRecogidaId, formData.direccionEntregaId]);


const getDireccionesDisponiblesRecogida = () => {
    if (!direccionesRecogida || direccionesRecogida.length === 0) return [];
    
    const direccionEntregaSeleccionada = formData.direccionEntregaId;
    
    return direccionesRecogida.filter(direccion => {
        if (direccionEntregaSeleccionada && direccion.id.toString() === direccionEntregaSeleccionada) {
            return false;
        }
        return true;
    });
};

const getDireccionesDisponiblesEntrega = () => {
    if (!direccionesEntrega || direccionesEntrega.length === 0) return [];
    
    const direccionRecogidaSeleccionada = formData.direccionRecogidaId;
    
    return direccionesEntrega.filter(direccion => {
        if (direccionRecogidaSeleccionada && direccion.id.toString() === direccionRecogidaSeleccionada) {
            return false;
        }
        return true;
    });
};

  const [filtros, setFiltros] = useState({
  pedidoId: '',
  estado: '',
  cliente: '',
  fechaDesde: '',
  fechaHasta: ''
});


  const estadosDisponibles = [
    { value: 'PENDIENTE', label: 'Pendiente', color: '#ffc107' },
    { value: 'ASIGNADO', label: 'Asignado', color: '#007bff' },
    { value: 'EN_TRANSITO', label: 'En tránsito', color: '#ff6600' },
    { value: 'ENTREGADO', label: 'Entregado', color: '#28a745' },
    { value: 'CANCELADO', label: 'Cancelado', color: '#FF0000' }
  ];

  useEffect(() => {
  aplicarFiltros();
}, [pedidos, filtros]);


const aplicarFiltros = () => {
  
  let filtrado = [...pedidos];

  if (filtros.pedidoId) {
    filtrado = filtrado.filter(pedido =>
      pedido.id?.toString().includes(filtros.pedidoId)
    );
  }

  if (filtros.estado) {
    filtrado = filtrado.filter(pedido => {
      const estadoMap = {
        'PENDIENTE': 1,
        'ASIGNADO': 2, 
        'EN_TRANSITO': 3,
        'ENTREGADO': 4,
        'CANCELADO': 5
      };
      
      const estadoIdBuscado = estadoMap[filtros.estado];
      
      return pedido.estadoId === estadoIdBuscado;
    });
  }

  if (filtros.cliente) {
    filtrado = filtrado.filter(pedido => {
      const busqueda = filtros.cliente.toLowerCase();
      
      const cliente = clientes.find(c => c.id === pedido.clienteId);
      
      if (!cliente) return false;
      
      const nombreCliente = cliente.nombre?.toLowerCase() || '';
      const emailCliente = cliente.email?.toLowerCase() || '';
      const telefonoCliente = cliente.telefono || '';
      
      
      return nombreCliente.includes(busqueda) || 
             emailCliente.includes(busqueda) || 
             telefonoCliente.includes(busqueda);
    });
  }

  if (filtros.fechaDesde) {
    const fechaDesde = new Date(filtros.fechaDesde);
    filtrado = filtrado.filter(pedido =>
      new Date(pedido.fechaCreacion) >= fechaDesde
    );
  }

  if (filtros.fechaHasta) {
    const fechaHasta = new Date(filtros.fechaHasta);
    fechaHasta.setHours(23, 59, 59, 999);
    filtrado = filtrado.filter(pedido =>
      new Date(pedido.fechaCreacion) <= fechaHasta
    );
  }

  setPedidosFiltrados(filtrado);
};

const handleFiltroChange = (e) => {
  const { name, value } = e.target;
  setFiltros(prev => ({
    ...prev,
    [name]: value
  }));
};

const handleBusquedaCliente = (e) => {
  const valor = e.target.value;
  setBusquedaCliente(valor);
  
  setFiltros(prev => ({
    ...prev,
    cliente: valor
  }));

  if (valor.length > 0) {
    const clientesFiltrados = clientes.filter(cliente => {
      const busqueda = valor.toLowerCase();
      const nombre = cliente.nombre?.toLowerCase() || cliente.name?.toLowerCase() || '';
      const email = cliente.email?.toLowerCase() || cliente.correo?.toLowerCase() || '';
      const telefono = cliente.telefono || cliente.phone || '';
      
      return nombre.includes(busqueda) ||
             email.includes(busqueda) ||
             telefono.includes(busqueda);
    });
    setClientesFiltrados(clientesFiltrados);
    setMostrarListaClientes(false);
  } else {
    setClientesFiltrados([]);
    setMostrarListaClientes(false);
    setClienteSeleccionado(null);
  }
};

const seleccionarCliente = (cliente) => {
  setClienteSeleccionado(cliente);
  setBusquedaCliente(cliente.nombre);
  setFiltros(prev => ({
    ...prev,
    cliente: cliente.nombre
  }));
  setMostrarListaClientes(false);
};

const limpiarSeleccionCliente = () => {
  setClienteSeleccionado(null);
  setBusquedaCliente('');
  setFiltros(prev => ({
    ...prev,
    cliente: ''
  }));
  setMostrarListaClientes(false);
};

const limpiarFiltros = () => {
  setFiltros({
    pedidoId: '',
    estado: '',
    cliente: '',
    fechaDesde: '',
    fechaHasta: '',
    totalMinimo: '',
    totalMaximo: ''
  });
  setBusquedaCliente('');
  setClienteSeleccionado(null);
  setMostrarListaClientes(false);
};

const manejarFechaPersonalizada = (tipo, valor) => {
  setFiltros(prev => {
    const nuevaFecha = {
      ...prev,
      [tipo]: valor
    };
    
    if (nuevaFecha.fechaDesde && nuevaFecha.fechaHasta) {
      const fechaInicio = new Date(nuevaFecha.fechaDesde);
      const fechaFin = new Date(nuevaFecha.fechaHasta);
      
      if (fechaInicio > fechaFin) {
        if (tipo === 'fechaDesde') {
          nuevaFecha.fechaHasta = valor;
        } else {
          nuevaFecha.fechaDesde = valor;
        }
      }
    }
    
    return nuevaFecha;
  });
};

  const ESTADOS = {
    PENDIENTE: 1,
    ASIGNADO: 2,
    EN_TRANSITO: 3,
    ENTREGADO: 4,
    CANCELADO: 5
  };
  
  const obtenerHistorialPedido = (pedidoId) => {
    return historialPedidos.filter(historial => historial.pedidoId === pedidoId || historial.pedido_id === pedidoId)
      .map(historial => {
        const fecha = historial.fechaCambio || historial.fecha;
        const tipoId = historial.tipoCambioId || historial.tipo_cambio_id;
        const valorAnterior = historial.valorAnterior || historial.valor_anterior;
        const valorNuevo = historial.valorNuevo || historial.valor_nuevo;
        const usuarioId = historial.usuarioId || historial.usuario_id;
        
        const obtenerTipoNombre = (id) => {
          const tipos = {
            1: 'cambio_estado',
            2: 'cambio_cliente', 
            3: 'cambio_mensajero',
            4: 'cambio_direccion_recogida',
            5: 'cambio_direccion_entrega',
            6: 'cambio_telefono_recogida',
            7: 'cambio_telefono_entrega',
            8: 'cambio_notas',
            9: 'cambio_tipo_servicio',
            10: 'cambio_tipo_paquete',
            11: 'cambio_tarifa',
            12: 'cambio_total'
          };
          
          const nombre = tipos[id] || 'N/A';
          return nombre.replace(/_/g, ' ')
            .toLowerCase()
            .replace(/^\w/, c => c.toUpperCase());
        };
        
        const obtenerEstadoNombre = (id) => {
          if (!id) return 'N/A';
          
          const estados = {
            1: 'pendiente',
            2: 'asignado',
            3: 'en_transito', 
            4: 'entregado',
            5: 'cancelado'
          };
          
          const nombre = estados[id] || 'N/A';
          return nombre.replace(/_/g, ' ')
            .toLowerCase()
            .replace(/^\w/, c => c.toUpperCase());
        };
        
        const obtenerMensajeroNombre = (id) => {
        if (!id || id === 'NULL' || id === 'null') return 'N/A';
        
        const mensajero = mensajeros.find(m => m.id === parseInt(id));
        if (mensajero) {
          const nombreCompleto = `${mensajero.nombres || ''} ${mensajero.apellidos || ''}`.trim();
          const nombreConId = nombreCompleto ? `${nombreCompleto} - ${mensajero.id}` : `Sin nombre - ${mensajero.id}`;
          return nombreConId;
        }
        
        return getUsuarioName(id);
      };
        
        const obtenerNombreUsuario = (id) => {
          if (!id) return 'N/A';
          
          const mensajero = mensajeros.find(m => m.id === id);
          if (mensajero) {
            return mensajero.nombre;
          }
          
          return getUsuarioName(id);
        };
        
        let valorAnteriorFormateado = valorAnterior;
        let valorNuevoFormateado = valorNuevo;
        let estadoAnteriorInfo = null;
        let estadoNuevoInfo = null;
        
        if (tipoId == 1) { 
          valorAnteriorFormateado = obtenerEstadoNombre(valorAnterior);
          valorNuevoFormateado = obtenerEstadoNombre(valorNuevo);
          estadoAnteriorInfo = getEstadoInfoPorNombre(valorAnteriorFormateado);
          estadoNuevoInfo = { color: '#28a745', label: valorNuevoFormateado };
        } else if (tipoId == 3) { 
          valorAnteriorFormateado = obtenerMensajeroNombre(valorAnterior);
          valorNuevoFormateado = obtenerMensajeroNombre(valorNuevo);
          estadoAnteriorInfo = { color: '#6c757d', label: valorAnteriorFormateado };
          estadoNuevoInfo = { color: '#28a745', label: valorNuevoFormateado };
        } else {
          estadoAnteriorInfo = { color: '#6c757d', label: valorAnterior || 'N/A' };
          estadoNuevoInfo = { color: '#28a745', label: valorNuevo || 'N/A' };
        }
        
        return {
          ...historial,
          fecha,
          tipoId,
          tipoNombre: obtenerTipoNombre(tipoId),
          valorAnterior: valorAnteriorFormateado,
          valorNuevo: valorNuevoFormateado,
          usuarioId,
          usuarioNombre: obtenerNombreUsuario(usuarioId),
          estadoAnteriorInfo,
          estadoNuevoInfo
        };
      })
      .sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
  };

  const getTipoCambioNombre = (tipoId) => {
    const tipo = tiposCambioPedido.find(t => t.id === tipoId);
    if (!tipo) return 'N/A';

    const formateado = tipo.nombre
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/^\w/, c => c.toUpperCase());

    return formateado;
  };

  const getEstadoInfoPorNombre = (nombreEstado) => {
  if (!nombreEstado) return { label: 'Sin estado', color: '#6c757d' };
  
  const estadosMap = {
      'pendiente': { label: 'Pendiente', color: '#ffc107' },
      'asignado': { label: 'Asignado', color: '#17a2b8' },
      'en_transito': { label: 'En tránsito', color: '#007bff' },
      'entregado': { label: 'Entregado', color: '#28a745' },
      'cancelado': { label: 'Cancelado', color: '#dc3545' }
    };
    
    return estadosMap[nombreEstado] || { label: nombreEstado, color: '#6c757d' };
  };

  const handleVerHistorial = (pedido) => {
    setPedidoSeleccionado(pedido);
    const historial = obtenerHistorialPedido(pedido.id);
    setHistorialPedidoSeleccionado(historial);
    setMostrarModalHistorial(true);
  };

  const getEstadoColor = (estadoNombre) => {
    const colores = {
        'pendiente': 'warning',
        'asignado': 'info',
        'en_transito': 'primary',
        'entregado': 'success',
        'cancelado': 'danger'
    };
    return colores[estadoNombre] || 'secondary';
};


const mostrarMapa = async (pedido) => {
    setCargandoMapa(true);
    
    const direccionRecogida = getDireccionRecogida(pedido);
    const direccionEntrega = getDireccionEntrega(pedido);
    
    const pedidoConDirecciones = {
        ...pedido,
        direccionRecogida,
        direccionEntrega
    };
    
    setPedidoParaMapa(pedidoConDirecciones);
    setMapaVisible(true);
    
    try {
      const ciudadBarrioRecogida = getCiudadBarrioRecogida(pedido);
      const ciudadBarrioEntrega = getCiudadBarrioEntrega(pedido);
      
      if (!direccionRecogida || !direccionEntrega || 
          direccionRecogida === 'Dirección no especificada' || 
          direccionEntrega === 'Dirección no especificada' ||
          direccionRecogida === 'Dirección no encontrada' || 
          direccionEntrega === 'Dirección no encontrada') {
        alert('El pedido no tiene direcciones completas configuradas para recogida y/o entrega.');
        return;
      }
      
      const [coordRecogida, coordEntrega] = await Promise.all([
        geocodificarDireccionMejorada(direccionRecogida, ciudadBarrioRecogida?.ciudad),
        geocodificarDireccionMejorada(direccionEntrega, ciudadBarrioEntrega?.ciudad)
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

  const renderTablaPedidos = () => (
    <div className="table-responsive">
      <table className="table table-hover text-center align-middle">
        <thead className="table-light">
          <tr>
            <th className="text-center">ID</th>
            <th className="text-center">Cliente</th>
            <th className="text-center">Recogida</th>
            <th className="text-center">Entrega</th>
            <th className="text-center">Teléfono recogida</th>
            <th className="text-center">Teléfono entrega</th>
            <th className="text-center">Total</th>
            <th className="text-center">Fecha</th>
            <th className="text-center">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {pedidosFiltrados.map((pedido) => {
            const estadoInfo = getEstadoInfo(pedido.estadoId);
            const direccionRecogida = getDireccionRecogida(pedido);
            const direccionEntrega = getDireccionEntrega(pedido);

            return (
              <tr key={pedido.id}>
                <td>#{pedido.id}</td>
                <td>
                  <div className="fw-semibold">{pedido.clienteNombre || getClienteName(pedido.clienteId)}</div>
                  <small className="text-muted">{pedido.telefonoRecogida}</small>
                </td>
                <td>
                  <div
                    style={{ maxWidth: '200px' }}
                    title={direccionRecogida}
                  >
                    {direccionRecogida}
                  </div>
                </td>
                <td>
                  <div
                    style={{ maxWidth: '200px' }}
                    title={direccionEntrega}
                  >
                    {direccionEntrega}
                  </div>
                </td>
                <td>
                  <div className="fw-semibold text-primary">
                    {pedido.telefonoRecogida || 'N/A'}
                  </div>
                </td>
                <td>
                  <div className="fw-semibold" style={{ color: '#6f42c1' }}>
                    {pedido.telefonoEntrega || 'N/A'}
                  </div>
                </td>
                <td className="fw-bold text-success">
                  ${pedido.total ? pedido.total.toLocaleString('es-CO') : '0'}
                </td>
                <td>
                  <small className="text-muted">
                    {pedido.fechaCreacion ? formatearFecha(pedido.fechaCreacion) : 'N/A'}
                  </small>
                </td>
                <td>
                  <div className="btn-group btn-group-sm justify-content-center">
                    <button
                      className="btn btn-outline-info"
                      onClick={() => mostrarMapa(pedido)}
                    >
                      <i className="bi bi-map"></i>
                    </button>
                    <button
                      className="btn btn-outline-secondary"
                      onClick={() => handleVerHistorial(pedido)}
                      title="Ver historial"
                    >
                      <i className="bi bi-clock-history"></i>
                    </button>
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );


  const ModalHistorialPedido = () => (
    <div className={`modal fade ${mostrarModalHistorial ? 'show' : ''}`} 
        style={{ display: mostrarModalHistorial ? 'block' : 'none' }}
        tabIndex="-1">
      <div className="modal-dialog modal-xl">
        <div className="modal-content">
          <div className="modal-header bg-info text-white">
            <h5 className="modal-title">
              <i className="bi bi-clock-history me-2"></i>
              Historial del pedido #{pedidoSeleccionado?.id}
            </h5>
            <button
              type="button"
              className="btn-close btn-close-white"
              onClick={() => setMostrarModalHistorial(false)}
            ></button>
          </div>
          <div className="modal-body">
            {pedidoSeleccionado && (
              <div className="card border-0 shadow-sm mb-4">
                <div className="card-header bg-light">
                  <h6 className="mb-0">
                    <i className="bi bi-info-circle me-2"></i>
                    Información del pedido
                  </h6>
                </div>
                <div className="card-body">
                  <div className="row">
                    <div className="col-md-6">
                      <div className="mb-2">
                        <i className="bi bi-person me-1" style={{ color: '#6f42c1' }}></i>
                        <strong>Cliente:</strong> {pedidoSeleccionado.clienteNombre || getClienteName(pedidoSeleccionado.clienteId)}
                      </div>
                    </div>
                    <div className="col-md-6">
                      <div className="mb-2">
                        <i className="bi bi-flag me-1" style={{ color: '#28a745' }}></i>
                        <strong>Estado actual:</strong> 
                        <span className="badge ms-2" style={{
                          backgroundColor: getEstadoInfo(pedidoSeleccionado.estadoId).color + '20',
                          color: getEstadoInfo(pedidoSeleccionado.estadoId).color
                        }}>
                          {pedidoSeleccionado.estadoNombre
                            ? formatearNombreEstado(pedidoSeleccionado.estadoNombre)
                            : formatearNombreEstado(getEstadoInfo(pedidoSeleccionado.estadoId).label)}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            )}
            
            <div className="card border-0 shadow-sm">
              <div className="card-header bg-light">
                <h6 className="mb-0">
                  <i className="bi bi-list-task me-2"></i>
                  Historial de cambios
                </h6>
              </div>
              <div className="card-body">
                {historialPedidoSeleccionado.length > 0 ? (
                  <div className="table-responsive">
                    <table className="table table-hover">
                      <thead className="table-light">
                        <tr>
                          <th className="text-center">
                            <i className="bi bi-calendar me-1" style={{ color: '#6c757d' }}></i>
                            Fecha
                          </th>
                          <th className="text-center">
                            <i className="bi bi-arrow-left-right me-1" style={{ color: '#17a2b8' }}></i>
                            Tipo de cambio
                          </th>
                          <th className="text-center">
                            <i className="bi bi-arrow-left me-1" style={{ color: '#ffc107' }}></i>
                            Valor anterior
                          </th>
                          <th className="text-center">
                            <i className="bi bi-arrow-right me-1" style={{ color: '#28a745' }}></i>
                            Valor nuevo
                          </th>
                          <th className="text-center">
                            <i className="bi bi-person-badge me-1" style={{ color: '#6f42c1' }}></i>
                            Usuario
                          </th>
                        </tr>
                      </thead>
                      <tbody>
                        {historialPedidoSeleccionado.map((historial, index) => (
                          <tr key={index}>
                            <td className="text-center">
                              <small className="fw-medium">
                                {historial.fecha ? formatearFecha(historial.fecha) : 'N/A'}
                              </small>
                            </td>
                            <td className="text-center">
                              <span className="badge bg-info">
                                {historial.tipoNombre}
                              </span>
                            </td>
                            <td className="text-center">
                              {historial.valorAnterior ? (
                                <span className="badge" style={{
                                  backgroundColor: historial.estadoAnteriorInfo.color + '20',
                                  color: historial.estadoAnteriorInfo.color
                                }}>
                                  {historial.estadoAnteriorInfo.label}
                                </span>
                              ) : (
                                <span className="text-muted">-</span>
                              )}
                            </td>
                            <td className="text-center">
                              <span className="badge" style={{
                                backgroundColor: historial.estadoNuevoInfo.color + '20',
                                color: historial.estadoNuevoInfo.color
                              }}>
                                {historial.estadoNuevoInfo.label}
                              </span>
                            </td>
                            <td className="text-center">
                              <small className="text-muted fw-medium">
                                {historial.usuarioNombre}
                              </small>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="text-center py-4">
                    <i className="bi bi-inbox" style={{ fontSize: '2rem', color: '#6c757d' }}></i>
                    <p className="text-muted mt-2 mb-0">No hay historial disponible para este pedido</p>
                  </div>
                )}
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setMostrarModalHistorial(false)}
            >
              <i className="bi bi-x-circle me-1"></i>
              Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>
  );

  const renderTarjetasPedidos = () => (
    <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      {pedidosFiltrados.map((pedido) => {
        const estadoInfo = getEstadoInfo(pedido.estadoId);
        
        return (
          <div className="col" key={pedido.id}>
            <div className="card h-100 shadow-sm">
              <div className="card-body">
                <div className="text-center mb-3">
                  <i className="bi bi-box-seam-fill" style={{ fontSize: '3rem', color: '#252850' }}></i>
                </div>
                
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <h5 className="card-title mb-0">Pedido #{pedido.id}</h5>
                  <span 
                    className="badge px-2 py-1" 
                    style={{ backgroundColor: estadoInfo.color, fontSize: '0.75rem' }}
                    title={estadoInfo.descripcion}
                  >
                    {pedido.estadoNombre
                      ? formatearNombreEstado(pedido.estadoNombre)
                      : formatearNombreEstado(getEstadoInfo(pedido.estadoId).label)}
                  </span>
                </div>
                
                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-person-fill me-1" style={{ color: '#20b2aa' }}></i>
                    Cliente:
                  </small>
                  <div>
                    {pedido.clienteNombre || getClienteName(pedido.clienteId)}
                  </div>
                </div>

                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-geo-alt me-1" style={{ color: '#dc3545' }}></i>
                    Recogida:
                  </small>
                  <div className="text-truncate" title={getDireccionRecogida(pedido)}>
                    {getDireccionRecogida(pedido)}
                  </div>
                  {(() => {
                    const ciudadBarrio = getCiudadBarrioRecogida(pedido);
                    return ciudadBarrio.ciudad && (
                      <small className="text-muted">
                        {ciudadBarrio.ciudad}{ciudadBarrio.barrio && ` - ${ciudadBarrio.barrio}`}
                      </small>
                    );
                  })()}
                </div>

                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-geo-alt me-1" style={{ color: '#6f42c1' }}></i>
                    Entrega:
                  </small>
                  <div className="text-truncate" title={getDireccionEntrega(pedido)}>
                    {getDireccionEntrega(pedido)}
                  </div>
                  {(() => {
                    const ciudadBarrio = getCiudadBarrioEntrega(pedido);
                    return ciudadBarrio.ciudad && (
                      <small className="text-muted">
                        {ciudadBarrio.ciudad}{ciudadBarrio.barrio && ` - ${ciudadBarrio.barrio}`}
                      </small>
                    );
                  })()}
                </div>

                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-telephone me-1" style={{ color: '#28a745' }}></i>
                    Tel. Recogida:
                  </small>
                  <div>{pedido.telefonoRecogida || 'No especificado'}</div>
                </div>

                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-telephone me-1" style={{ color: '#ffc107' }}></i>
                    Tel. Entrega:
                  </small>
                  <div>{pedido.telefonoEntrega || 'No especificado'}</div>
                </div>

                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-box me-1" style={{ color: '#252850' }}></i>
                    Tipo:
                  </small>
                  <div>{pedido.tipoPaquete || 'No especificado'}</div>
                </div>

                {pedido.pesoKg && (
                  <div className="mb-2">
                    <small className="text-muted">
                      <i className="bi bi-bag me-1" style={{ color: '#795548' }}></i>
                      Peso:
                    </small>
                    <div>{pedido.pesoKg} kg</div>
                  </div>
                )}

                {pedido.valorDeclarado && pedido.valorDeclarado > 0 && (
                  <div className="mb-2">
                    <small className="text-muted">
                      <i className="bi bi-currency-dollar me-1" style={{ color: '#4caf50' }}></i>
                      Valor Declarado:
                    </small>
                    <div>${pedido.valorDeclarado.toLocaleString('es-CO')}</div>
                  </div>
                )}

                {Number(pedido.costoCompra) > 0 && (
                  <div className="mb-2">
                    <small className="text-muted">
                      <i className="bi bi-cart-fill me-1" style={{ color: '#e91e63' }}></i>
                      Costo Compra:
                    </small>
                    <div>${Number(pedido.costoCompra).toLocaleString('es-CO')}</div>
                  </div>
                )}

                {pedido.subtotal && pedido.subtotal > 0 && (
                  <div className="mb-2">
                    <small className="text-muted">
                      <i className="bi bi-calculator me-1" style={{ color: '#9c27b0' }}></i>
                      Subtotal:
                    </small>
                    <div>${pedido.subtotal.toLocaleString('es-CO')}</div>
                  </div>
                )}

                {pedido.tarifaId && (
                  <div className="mb-2">
                    <small className="text-muted">
                      <i className="bi bi-tags me-1" style={{ color: '#607d8b' }}></i>
                      Tarifa:
                    </small>
                    <div>{getTarifaInfo(pedido.tarifaId)}</div>
                  </div>
                )}

                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-cash me-1" style={{ color: '#ff9800' }}></i>
                    Total:
                  </small>
                  <div className="fw-bold text-success">
                    ${pedido.total ? pedido.total.toLocaleString('es-CO') : '0'}
                  </div>
                </div>

                {pedido.tipoServicioNombre && (
                  <div className="mb-2">
                    <small className="text-muted">
                      <i className="bi bi-truck me-1" style={{ color: '#2196f3' }}></i>
                      Servicio:
                    </small>
                    <div>{formatearNombre(pedido.tipoServicioNombre)}</div>
                  </div>
                )}

                {pedido.tiempoEntregaMinutos && (
                  <div className="mb-2">
                    <small className="text-muted">
                      <i className="bi bi-stopwatch me-1" style={{ color: '#ff5722' }}></i>
                      Tiempo Entrega:
                    </small>
                    <div>
                      {Math.floor(pedido.tiempoEntregaMinutos / 60)}h {pedido.tiempoEntregaMinutos % 60}m
                    </div>
                  </div>
                )}

                {pedido.fechaEntrega && (
                  <div className="mb-2">
                    <small className="text-muted">
                      <i className="bi bi-calendar-check me-1" style={{ color: '#4caf50' }}></i>
                      Fecha Entrega:
                    </small>
                    <div>{formatearFecha(pedido.fechaEntrega)}</div>
                  </div>
                )}

                {pedido.notas && (
                  <div className="mb-2">
                    <small className="text-muted">
                      <i className="bi bi-chat-text me-1" style={{ color: '#6c757d' }}></i>
                      Notas:
                    </small>
                    <div className="text-truncate" title={pedido.notas}>
                      {pedido.notas}
                    </div>
                  </div>
                )}

                {pedido.fechaCreacion && (
                  <p className="text-muted text-center mt-3" style={{ fontSize: '0.75rem' }}>
                    <span className="me-2">Creado el</span>
                    <i className="bi bi-calendar-event me-1" style={{ color: '#28a745' }}></i>
                    <span className="me-2">{formatearFecha(pedido.fechaCreacion).split(' ')[0].replace(',', '')}</span>
                    <i className="bi bi-clock me-1" style={{ color: '#6c757d' }}></i>
                    <span>{formatearFecha(pedido.fechaCreacion).split(' ')[1]}</span>
                  </p>
                )}
              </div>
              
              <div className="card-footer d-flex justify-content-center gap-1 bg-light">
                <button
                  className="btn btn-sm btn-outline-info"
                  onClick={() => mostrarMapa(pedido)}
                  title="Ver ruta en mapa"
                >
                  <i className="bi bi-map me-1"></i>Ruta
                </button>

                <button
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => handleVerHistorial(pedido)}
                  title="Ver historial"
                >
                  <i className="bi bi-clock-history me-1"></i>Historial
                </button>
              </div>

            </div>
          </div>
        );
      })}
    </div>
  );

  if (loading) {
    return (
      <>
        <div className="container py-4" style={{ minHeight: 'calc(100vh - 120px)' }}>
          <h3 className="fw-bold text-muted d-flex align-items-center" style={{ fontSize: '30px' }}>
            <i className="bi bi-box-seam-fill me-2" style={{ color: '#252850' }}></i>
            Gestión de pedidos
            {mensajeriaId && (
              <span className="ms-3 d-flex align-items-center">
                <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
                {getEmpresaName(mensajeriaId)}
              </span>
            )}
          </h3>

          <div className="text-center mt-5">
            <div className="spinner-border text-primary" role="status" />
            <p className="mt-3">Cargando pedidos...</p>
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
            <i className="bi bi-box-seam-fill me-2" style={{ color: '#252850' }}></i>
            Gestión de pedidos
            {mensajeriaId && (
              <span className="ms-3 d-flex align-items-center">
                <i className="bi bi-buildings me-1" style={{ color: '#ff6600' }}></i>
                {getEmpresaName(mensajeriaId)}
              </span>
            )}
          </h3>
          <div className="d-flex align-items-center gap-3">
            <div className="text-muted">
                  <i className="bi bi-info-circle me-1"></i>
                  Mostrando {pedidosFiltrados.length} de {pedidos.length} pedidos
            </div>
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
                  <i className="bi bi-hash me-1" style={{ color: '#007bff' }}></i>
                  ID pedido
                </label>
                <input
                  type="text"
                  name="pedidoId"
                  value={filtros.pedidoId}
                  onChange={handleFiltroChange}
                  className="form-control"
                  placeholder="Buscar por ID"
                />
              </div>
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-flag me-1" style={{ color: '#6c757d' }}></i>
                  Estado
                </label>
                <select
                  name="estado"
                  value={filtros.estado}
                  onChange={handleFiltroChange}
                  className="form-select"
                >
                  <option value="">Todos los estados</option>
                  {estadosDisponibles.map(estado => (
                    <option key={estado.value} value={estado.value}>{estado.label}</option>
                  ))}
                </select>
              </div>
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-person me-1" style={{ color: '#6f42c1' }}></i>
                  Cliente
                </label>
                <div className="position-relative" ref={clienteInputRef}>
                  <div className="input-group">
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Buscar por nombre o teléfono"
                      value={busquedaCliente}
                      onChange={handleBusquedaCliente}
                      onFocus={() => setMostrarListaClientes(false)}
                    />
                    {clienteSeleccionado && (
                      <button
                        className="btn btn-outline-secondary"
                        type="button"
                        onClick={limpiarSeleccionCliente}
                        title="Limpiar selección"
                      >
                        <i className="bi bi-x"></i>
                      </button>
                    )}
                  </div>

                  {mostrarListaClientes && clientesFiltrados.length > 0 && (
                    <div
                      className="position-absolute w-100 bg-white border rounded shadow-lg mt-1"
                      style={{ zIndex: 1000, maxHeight: '200px', overflowY: 'auto' }}
                    >
                      {clientesFiltrados.slice(0, 10).map(cliente => (
                        <div
                          key={cliente.id}
                          className="px-3 py-2 border-bottom cursor-pointer hover-bg-light"
                          onClick={() => seleccionarCliente(cliente)}
                          style={{
                            cursor: 'pointer',
                            transition: 'background-color 0.2s'
                          }}
                          onMouseEnter={(e) => e.target.style.backgroundColor = '#f8f9fa'}
                          onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                        >
                          <div className="fw-semibold">{cliente.nombre}</div>
                          {cliente.email && (
                            <small className="text-muted">{cliente.email}</small>
                          )}
                          {cliente.telefono && (
                            <small className="text-muted ms-2">Tel: {cliente.telefono}</small>
                          )}
                        </div>
                      ))}
                      {clientesFiltrados.length > 10 && (
                        <div className="px-3 py-2 text-muted text-center">
                          <small>... y {clientesFiltrados.length - 10} más</small>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="row g-3 mb-3">
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-calendar-check me-1" style={{ color: '#28a745' }}></i>
                  Desde
                </label>
                <input
                  type="date"
                  name="fechaDesde"
                  value={filtros.fechaDesde}
                  onChange={(e) => manejarFechaPersonalizada('fechaDesde', e.target.value)}
                  className={`form-control ${filtros.fechaDesde && filtros.fechaHasta &&
                    new Date(filtros.fechaDesde) > new Date(filtros.fechaHasta) ? 'is-invalid' : ''}`}
                  max={filtros.fechaHasta || new Date().toISOString().split('T')[0]}
                />
              </div>
              <div className="col-md-4">
                <label className="form-label fw-semibold">
                  <i className="bi bi-calendar-check me-1" style={{ color: '#ffa420' }}></i>
                  Hasta
                </label>
                <input
                  type="date"
                  name="fechaHasta"
                  value={filtros.fechaHasta}
                  onChange={(e) => manejarFechaPersonalizada('fechaHasta', e.target.value)}
                  className={`form-control ${filtros.fechaDesde && filtros.fechaHasta &&
                    new Date(filtros.fechaDesde) > new Date(filtros.fechaHasta) ? 'is-invalid' : ''}`}
                  min={filtros.fechaDesde}
                  max={new Date().toISOString().split('T')[0]}
                />
              </div>
              <div className="col-md-2 d-flex flex-column justify-content-end">
                {(filtros.pedidoId || filtros.estado || filtros.cliente || filtros.fechaDesde ||
                  filtros.fechaHasta) && (
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

          {filtros.fechaDesde && filtros.fechaHasta &&
            new Date(filtros.fechaDesde) > new Date(filtros.fechaHasta) && (
              <div className="card-footer">
                <div className="text-danger small">
                  <i className="bi bi-exclamation-triangle me-1"></i>
                  La fecha de inicio debe ser anterior a la fecha final
                </div>
              </div>
            )}
        </div>

        {pedidosFiltrados.length === 0 ? (
          <div className="text-center py-5">
            <i className="bi bi-inbox" style={{ fontSize: '4rem', color: '#dee2e6' }}></i>
            <h4 className="text-muted mt-3">
              {pedidos.length === 0 ? 'No tienes pedidos registrados' : 'No se encontraron resultados'}
            </h4>
            <p className="text-muted">
              {pedidos.length === 0 
                ? 'Aún no se han registrado pedidos en el sistema.' 
                : 'Intenta ajustar los filtros de búsqueda. No se encontraron pedidos que coincidan con los criterios seleccionados.'
              }
            </p>
          </div>
        ) : vista === 'tarjetas' ? (
          renderTarjetasPedidos()
        ) : (
          renderTablaPedidos()
        )}

        <ModalMapa 
          mapaVisible={mapaVisible}
          cargandoMapa={cargandoMapa}
          pedidoParaMapa={pedidoParaMapa}
          coordenadas={coordenadas}
          setMapaVisible={setMapaVisible}
          setPedidoParaMapa={setPedidoParaMapa}
          setCoordenadas={setCoordenadas}
          setCargandoMapa={setCargandoMapa}
        />

          {mostrarModalHistorial && (
            <ModalHistorialPedido 
              pedido={pedidoSeleccionado}
              onClose={() => setMostrarModalHistorial(false)}
            />
          )}
      </div>
      <Footer />
    </>
  );
}