import { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import Footer from '../../components/Footer';
import ModalMapa from '../../components/ModalMapa';

export default function AdminPedidos() {
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
  const [formVisible, setFormVisible] = useState(false);
  const [editingPedido, setEditingPedido] = useState(null);
  const [historialPedidos, setHistorialPedidos] = useState(null);
  const [tiposCambioPedido, setTiposCambioPedido] = useState(null);
  const [errores, setErrores] = useState({});
  const [loading, setLoading] = useState(true);
  const [filtroEstado, setFiltroEstado] = useState('');
  const [filtroCliente, setFiltroCliente] = useState('');
  const [busquedaCliente, setBusquedaCliente] = useState('');
  const [mostrarListaClientes, setMostrarListaClientes] = useState(false);
  const formRef = useRef(null);
  const clienteInputRef = useRef(null);
  const [mapaVisible, setMapaVisible] = useState(false);
  const [pedidoParaMapa, setPedidoParaMapa] = useState(null);
  const [coordenadas, setCoordenadas] = useState({ recogida: null, entrega: null });
  const [cargandoMapa, setCargandoMapa] = useState(false);
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
  const usuarioId = decoded?.usuario_id;
  const tenantId = decoded?.tenantId;

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
        axios.get('/proxy/api/pedidos', { headers }),
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


  const handleInputChange = (e) => {
    const { name, value } = e.target;
    
    setEditingPedido(prev => {
      const newPedido = { ...prev };
      
      if (['clienteId', 'tipoServicioId', 'tarifaId', 'mensajeroId', 'estadoId', 'tiempoEntregaMinutos'].includes(name)) {
        newPedido[name] = value ? parseInt(value) : null;
      }
      else if (['pesoKg', 'valorDeclarado', 'costoCompra'].includes(name)) {
        newPedido[name] = value ? parseFloat(value) : null;
      }
      else {
        newPedido[name] = value;
      }
      
      return newPedido;
    });

    if (errores[name]) {
      setErrores(prev => ({
        ...prev,
        [name]: null
      }));
    }
  };
  
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


const handleDireccionChange = (tipo, direccionId) => {
    
    if (direccionId === 'otra') {
        
        setUsarDireccionTemporal(prev => ({
            ...prev,
            [tipo]: true
        }));
        
        if (tipo === 'recogida') {
            setFormData(prev => ({
                ...prev,
                direccionRecogidaId: null,
                ciudadRecogida: '',
                barrioRecogida: '',
                direccionRecogidaTemporal: ''
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                direccionEntregaId: null,
                ciudadEntrega: '',
                barrioEntrega: '',
                direccionEntregaTemporal: ''
            }));
        }
        
        return;
    }
    
    if (tipo === 'recogida') {
        aplicarDireccionSeleccionada('recogida', direccionId, direccionesRecogida);
    } else if (tipo === 'entrega') {
        aplicarDireccionSeleccionada('entrega', direccionId, direccionesEntrega);
    }

    if (tipo === 'recogida' && formData.direccionEntregaId === direccionId) {
        alert('No puedes usar la misma dirección para recogida y entrega. Por favor selecciona una dirección diferente.');
        return;
    }
    
    if (tipo === 'entrega' && formData.direccionRecogidaId === direccionId) {
        alert('No puedes usar la misma dirección para recogida y entrega. Por favor selecciona una dirección diferente.');
        return;
    }

    const direcciones = tipo === 'recogida' ? direccionesRecogida : direccionesEntrega;
    const direccion = direcciones.find(d => d.id === parseInt(direccionId));

    if (direccion) {
        setUsarDireccionTemporal(prev => ({
            ...prev,
            [tipo]: false
        }));
        
        if (tipo === 'recogida') {
            setFormData(prev => ({
                ...prev,
                direccionRecogidaId: direccionId,
                ciudadRecogida: direccion.ciudad || '',
                barrioRecogida: direccion.barrio || '',
                direccionRecogidaTemporal: ''
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                direccionEntregaId: direccionId,
                ciudadEntrega: direccion.ciudad || '',
                barrioEntrega: direccion.barrio || '',
                direccionEntregaTemporal: ''
            }));
        }
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

const cargarPedidosRecientes = async (clienteId) => {
    try {
        const response = await fetch(`/proxy/api/pedidos/cliente/${clienteId}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
                'x-additional-data': additionalData,
            }
        });

        if (response.ok) {
            const data = await response.json();
            const pedidos = data.data || [];
            
            const pedidosRecientes = pedidos
                .sort((a, b) => new Date(b.fechaCreacion) - new Date(a.fechaCreacion))
                .slice(0, 5);
            
            setPedidosCliente(pedidosRecientes);
        }
    } catch (error) {
        console.error('Error al cargar pedidos recientes:', error);
    }
};

const handleClienteSeleccionado = (clienteId) => {
    if (clienteId) {
        setFormData(prev => ({
            ...prev,
            clienteId: clienteId,
            direccionRecogidaId: '',
            direccionEntregaId: '',
            ciudadRecogida: '',
            barrioRecogida: '',
            ciudadEntrega: '',
            barrioEntrega: ''
        }));
        
        cargarDireccionesCliente(clienteId);
    }
};

const handleClienteChange = async (e) => {
    const clienteId = e.target.value;
    
    const finalClienteId = clienteId === 'sin-cliente' ? null : clienteId;
    
    setFormData(prev => ({
        ...prev,
        clienteId: clienteId === 'sin-cliente' ? null : clienteId,
        direccionRecogidaId: '',
        direccionEntregaId: '',
        direccionRecogidaTemporal: '',
        direccionEntregaTemporal: '',
        ciudadRecogida: '',
        barrioRecogida: '',
        telefonoRecogida: '',
        ciudadEntrega: '',
        barrioEntrega: '',
        telefonoEntrega: ''
    }));

    if (clienteId && clienteId !== 'sin-cliente') {
        try {
            const cliente = clientes.find(c => c.id === parseInt(clienteId));
            setClienteSeleccionado(cliente);

            await Promise.all([
                cargarDireccionesCliente(clienteId),
                cargarPedidosRecientes(clienteId)
            ]);

            setShowPedidosModal(true);

        } catch (error) {
            console.error('Error al cargar información del cliente:', error);
        }
    } else {
        setClienteSeleccionado(null);
        setPedidosCliente([]);
        setDireccionesRecogida([]);
        setDireccionesEntrega([]);
    }
};

  const autocompletarConPedido = (pedido) => {
    
      const recogidaInfo = getCiudadBarrioRecogida(pedido);
      const entregaInfo = getCiudadBarrioEntrega(pedido);
      
      let finalFormData = { ...formData };
      let finalUsarDireccionTemporal = { recogida: false, entrega: false };
      
      finalFormData = {
          ...finalFormData,
          tipoPaquete: pedido.tipoPaquete || '',
          pesoKg: pedido.pesoKg || '',
          valorDeclarado: pedido.valorDeclarado || '',
          tiempoEntregaMinutos: pedido.tiempoEntregaMinutos || '',
          
          tipoServicioId: pedido.tipoServicioId || '',
          tarifaId: pedido.tarifaId || '',
          
          notas: pedido.notas || ''
      };

      let tipoServicioTexto = '';
      if (pedido.tipoServicioId) {
          if (pedido.tipoServicioNombre) {
              tipoServicioTexto = formatearNombre(pedido.tipoServicioNombre);
          } else {
              const tipoServicioEncontrado = tiposServicio.find(ts => ts.id === pedido.tipoServicioId);
              if (tipoServicioEncontrado) {
                  tipoServicioTexto = formatearNombre(tipoServicioEncontrado.nombre);
              }
          }
      }
          
      const obtenerInfoFallbackRecogida = (pedido) => {
          
          const info = getCiudadBarrioRecogida(pedido);
          const direccionTexto = getDireccionRecogida(pedido);
          
          let ciudad = '';
          let barrio = '';
          
          if (info && info.ciudad) ciudad = info.ciudad;
          if (info && info.barrio) barrio = info.barrio;
          
          if (!ciudad && pedido.ciudadRecogida) ciudad = pedido.ciudadRecogida;
          if (!barrio && pedido.barrioRecogida) barrio = pedido.barrioRecogida;
          
          if ((!ciudad || !barrio) && pedido.direccionRecogidaId && direcciones.length > 0) {
              const direccionGlobal = direcciones.find(d => d.id === parseInt(pedido.direccionRecogidaId) && d.esRecogida === true);
              if (direccionGlobal) {
                  if (!ciudad) ciudad = direccionGlobal.ciudad || '';
                  if (!barrio) barrio = direccionGlobal.barrio || '';
              } 
          }
          
          if ((!ciudad || !barrio) && pedido.direccionRecogidaId && direccionesRecogida.length > 0) {
              const direccionRecogida = direccionesRecogida.find(d => d.id === parseInt(pedido.direccionRecogidaId));
              if (direccionRecogida) {
                  if (!ciudad) ciudad = direccionRecogida.ciudad || '';
                  if (!barrio) barrio = direccionRecogida.barrio || '';
              }
          }
          
          if (!ciudad && pedido.direccionRecogida?.ciudad) ciudad = pedido.direccionRecogida.ciudad;
          if (!barrio && pedido.direccionRecogida?.barrio) barrio = pedido.direccionRecogida.barrio;
          
          if (!ciudad && pedido.recogida?.ciudad) ciudad = pedido.recogida.ciudad;
          if (!barrio && pedido.recogida?.barrio) barrio = pedido.recogida.barrio;
          
          if (!ciudad && recogidaInfo?.ciudad) ciudad = recogidaInfo.ciudad;
          if (!barrio && recogidaInfo?.barrio) barrio = recogidaInfo.barrio;
          
          const resultado = {
              direccion: direccionTexto,
              ciudad: ciudad || '',
              barrio: barrio || '',
              telefono: pedido.telefonoRecogida || ''
          };
          
          return resultado;
      };
      
      const obtenerInfoFallbackEntrega = (pedido) => {
          const info = getCiudadBarrioEntrega(pedido);
          const direccionTexto = getDireccionEntrega(pedido);
          
          let ciudad = '';
          let barrio = '';
          
          if (info && info.ciudad) ciudad = info.ciudad;
          if (info && info.barrio) barrio = info.barrio;
          
          if (!ciudad && pedido.ciudadEntrega) ciudad = pedido.ciudadEntrega;
          if (!barrio && pedido.barrioEntrega) barrio = pedido.barrioEntrega;
          
          if ((!ciudad || !barrio) && pedido.direccionEntregaId && direcciones.length > 0) {
              const direccionGlobal = direcciones.find(d => d.id === parseInt(pedido.direccionEntregaId) && d.esEntrega === true);
              if (direccionGlobal) {
                  if (!ciudad) ciudad = direccionGlobal.ciudad || '';
                  if (!barrio) barrio = direccionGlobal.barrio || '';
              } 
          }
          
          if ((!ciudad || !barrio) && pedido.direccionEntregaId && direccionesEntrega.length > 0) {
              const direccionEntrega = direccionesEntrega.find(d => d.id === parseInt(pedido.direccionEntregaId));
              if (direccionEntrega) {
                  if (!ciudad) ciudad = direccionEntrega.ciudad || '';
                  if (!barrio) barrio = direccionEntrega.barrio || '';
              }
          }
          
          if (!ciudad && pedido.direccionEntrega?.ciudad) ciudad = pedido.direccionEntrega.ciudad;
          if (!barrio && pedido.direccionEntrega?.barrio) barrio = pedido.direccionEntrega.barrio;
          
          if (!ciudad && pedido.entrega?.ciudad) ciudad = pedido.entrega.ciudad;
          if (!barrio && pedido.entrega?.barrio) barrio = pedido.entrega.barrio;
          
          if (!ciudad && entregaInfo?.ciudad) ciudad = entregaInfo.ciudad;
          if (!barrio && entregaInfo?.barrio) barrio = entregaInfo.barrio;
          
          const resultado = {
              direccion: direccionTexto,
              ciudad: ciudad || '',
              barrio: barrio || '',
              telefono: pedido.telefonoEntrega || ''
          };
          
          return resultado;
      };
      
      if (pedido.direccionRecogidaId) {
          const direccion = direccionesRecogida.find(d => d.id === parseInt(pedido.direccionRecogidaId));
          
          if (direccion) {
              const ciudad = direccion.direccion?.ciudad || direccion.ciudad;
              const barrio = direccion.direccion?.barrio || direccion.barrio;
              
              finalFormData = {
                  ...finalFormData,
                  direccionRecogidaId: String(pedido.direccionRecogidaId),
                  direccionRecogidaTemporal: '',
                  ciudadRecogida: ciudad || '',
                  barrioRecogida: barrio || '',
                  telefonoRecogida: direccion.telefono || pedido.telefonoRecogida || ''
              };
              
              finalUsarDireccionTemporal.recogida = false;
          } else {
              const infoFallback = obtenerInfoFallbackRecogida(pedido);
              
              finalFormData = {
                  ...finalFormData,
                  direccionRecogidaId: '',
                  direccionRecogidaTemporal: infoFallback.direccion,
                  ciudadRecogida: infoFallback.ciudad,
                  barrioRecogida: infoFallback.barrio,
                  telefonoRecogida: infoFallback.telefono
              };
              finalUsarDireccionTemporal.recogida = true;
          }
      } else {
          const infoFallback = obtenerInfoFallbackRecogida(pedido);
          
          finalFormData = {
              ...finalFormData,
              direccionRecogidaId: '',
              direccionRecogidaTemporal: infoFallback.direccion,
              ciudadRecogida: infoFallback.ciudad,
              barrioRecogida: infoFallback.barrio,
              telefonoRecogida: infoFallback.telefono
          };
          finalUsarDireccionTemporal.recogida = true;
      }
      
      if (pedido.direccionEntregaId) {
          const direccion = direccionesEntrega.find(d => d.id === parseInt(pedido.direccionEntregaId));
          
          if (direccion) {
              const ciudad = direccion.direccion?.ciudad || direccion.ciudad;
              const barrio = direccion.direccion?.barrio || direccion.barrio;
              
              finalFormData = {
                  ...finalFormData,
                  direccionEntregaId: String(pedido.direccionEntregaId),
                  direccionEntregaTemporal: '',
                  ciudadEntrega: ciudad || '',
                  barrioEntrega: barrio || '',
                  telefonoEntrega: direccion.telefono || pedido.telefonoEntrega || ''
              };
              
              finalUsarDireccionTemporal.entrega = false;
          } else {
              const infoFallback = obtenerInfoFallbackEntrega(pedido);
              
              finalFormData = {
                  ...finalFormData,
                  direccionEntregaId: '',
                  direccionEntregaTemporal: infoFallback.direccion,
                  ciudadEntrega: infoFallback.ciudad,
                  barrioEntrega: infoFallback.barrio,
                  telefonoEntrega: infoFallback.telefono
              };
              finalUsarDireccionTemporal.entrega = true;
          }
      } else {
          const infoFallback = obtenerInfoFallbackEntrega(pedido);
          
          finalFormData = {
              ...finalFormData,
              direccionEntregaId: '',
              direccionEntregaTemporal: infoFallback.direccion,
              ciudadEntrega: infoFallback.ciudad,
              barrioEntrega: infoFallback.barrio,
              telefonoEntrega: infoFallback.telefono
          };
          finalUsarDireccionTemporal.entrega = true;
      }
      
      setFormData(finalFormData);
      setUsarDireccionTemporal(finalUsarDireccionTemporal);
      
      if (tipoServicioTexto) {
          setTipoServicioBusqueda(tipoServicioTexto);
      }
      
      setTimeout(() => {
          alert('Datos del pedido cargados exitosamente');
      }, 100);
  };

  const handleDireccionRecogidaChange = (e) => {
    const direccionId = e.target.value;
    setEditingPedido(prev => ({
      ...prev,
      direccionRecogidaId: direccionId,
      direccionRecogidaTemporal: ''
    }));

    if (direccionId) {
      const direccion = direccionesRecogida.find(d => d.id === parseInt(direccionId));
      if (direccion) {
        setEditingPedido(prev => ({
          ...prev,
          ciudadRecogida: direccion.direccion.ciudad,
          barrioRecogida: direccion.direccion.barrio,
          telefonoRecogida: direccion.telefono || prev.telefonoRecogida
        }));
      }
      setUsarDireccionTemporal(prev => ({ ...prev, recogida: false }));
    }
  };

  const handleDireccionEntregaChange = (e) => {
    const direccionId = e.target.value;
    setEditingPedido(prev => ({
      ...prev,
      direccionEntregaId: direccionId,
      direccionEntregaTemporal: ''
    }));

    if (direccionId) {
      const direccion = direccionesEntrega.find(d => d.id === parseInt(direccionId));
      if (direccion) {
        setEditingPedido(prev => ({
          ...prev,
          ciudadEntrega: direccion.direccion.ciudad,
          barrioEntrega: direccion.direccion.barrio,
          telefonoEntrega: direccion.telefono || prev.telefonoEntrega
        }));
      }
      setUsarDireccionTemporal(prev => ({ ...prev, entrega: false }));
    }
  };


  const guardarDireccionTemporal = async (tipo, direccionData) => {
    try {
      const response = await fetch('/proxy/api/direcciones', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
          'x-additional-data': additionalData,
        },
        body: JSON.stringify({
          clienteId: editingPedido.clienteId,
          tipo: tipo,
          direccionCompleta: tipo === 'recogida' ? 
            editingPedido.direccionRecogidaTemporal : 
            editingPedido.direccionEntregaTemporal,
          ciudad: tipo === 'recogida' ? 
            editingPedido.ciudadRecogida : 
            editingPedido.ciudadEntrega,
          barrio: tipo === 'recogida' ? 
            editingPedido.barrioRecogida : 
            editingPedido.barrioEntrega,
          telefono: tipo === 'recogida' ? 
            editingPedido.telefonoRecogida : 
            editingPedido.telefonoEntrega
        })
      });

      if (response.ok) {
        const data = await response.json();
        await cargarDireccionesCliente(editingPedido.clienteId);
        
        setEditingPedido(prev => ({
          ...prev,
          [tipo === 'recogida' ? 'direccionRecogidaId' : 'direccionEntregaId']: data.data.id,
          [tipo === 'recogida' ? 'direccionRecogidaTemporal' : 'direccionEntregaTemporal']: ''
        }));

        setUsarDireccionTemporal(prev => ({
          ...prev,
          [tipo]: false
        }));
      }
    } catch (error) {
      console.error('Error al guardar dirección:', error);
    }
  };

 

    const handleNuevoPedido = () => {
      setFormVisible(true);
      setEditingPedido({
        cliente: { id: '' },
        mensajero: { id: '' },
        mensajeria: { id: '' },
        tipoServicioId: '',
        tarifaId: '',
        direccionRecogida: '',
        direccionEntrega: '',
        direccionRecogidaTemporal: '',
        direccionEntregaTemporal: '',
        ciudadRecogida: '',
        barrioRecogida: '',
        ciudadEntrega: '',
        barrioEntrega: '',
        telefonoRecogida: '',
        telefonoEntrega: '',
        tipoPaquete: '',
        pesoKg: '',
        valorDeclarado: '',
        costoCompra: '',
        notas: '',
        estadoId: '',
        tenantId: '',
      });
          
      setFormData({
        clienteId: '',
        mensajeriaId: '',
        tipoServicioId: '',
        tarifaId: '',
        direccionRecogidaId: '',
        direccionEntregaId: '',
        direccionRecogidaTemporal: '',
        direccionEntregaTemporal: '',
        ciudadRecogida: '',
        barrioRecogida: '',
        ciudadEntrega: '',
        barrioEntrega: '',
        telefonoRecogida: '',
        telefonoEntrega: '',
        tipoPaquete: '',
        pesoKg: '',
        valorDeclarado: '',
        costoCompra: '',
        notas: '',
        tenantId: '',
      });

      setDireccionesRecogida([]);
      setDireccionesEntrega([]);
      
      setErrores({});
      setTimeout(() => {
          formRef.current?.scrollIntoView({ behavior: 'smooth' });
      }, 100);
  };


  const resetearEstadosAdicionales = () => {
    setClienteSeleccionado(null);
    setPedidosCliente([]);
    setShowHistorial(false);
    setDireccionesRecogida([]);
    setDireccionesEntrega([]);
    setUsarDireccionTemporal({ recogida: false, entrega: false });
    setGuardarDireccionRecogida(false);
    setGuardarDireccionEntrega(false);
    setCiudadesUsadas({ recogida: [], entrega: [] });
    setBarriosUsados({ recogida: [], entrega: [] });
    setTiposPaqueteUsados([]);
    setShowPedidosSinCliente(false);
  };

const actualizarEstado = async (pedidoId, estadoNuevo) => {
  try {
    const requestBody = {
      estadoId: parseInt(estadoNuevo),
      tenantId: tenantId,
      usuarioId: usuarioId
    };
    
    
    const response = await fetch(`/proxy/api/pedidos/${pedidoId}/estado`, {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        'x-additional-data': additionalData,
      },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(`Error ${response.status}: ${errorData}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error al actualizar estado:', error);
    throw error;
  }
};

const handleEstadoChange = (e) => {
  const nuevoEstadoId = parseInt(e.target.value);
  
  setFormData(prev => ({
    ...prev,
    estadoId: nuevoEstadoId
  }));
};

  const handleGuardar = async (e) => {
    e.preventDefault();
    
    setLoading(true);
    
    try {
      const nuevosErrores = {};
      
      if (!formData.tipoServicioId) {
        nuevosErrores.tipoServicioId = 'El tipo de servicio es requerido';
      }
      if (!formData.direccionRecogidaId && !formData.direccionRecogidaTemporal) {
        nuevosErrores.direccionRecogidaTemporal = 'La dirección de recogida es requerida';
      }
      if (!formData.direccionEntregaId && !formData.direccionEntregaTemporal) {
        nuevosErrores.direccionEntregaTemporal = 'La dirección de entrega es requerida';
      }
      if (!formData.telefonoRecogida) {
        nuevosErrores.telefonoRecogida = 'El teléfono de recogida es requerido';
      }
      if (!formData.telefonoEntrega) {
        nuevosErrores.telefonoEntrega = 'El teléfono de entrega es requerido';
      }
      if (!formData.ciudadRecogida) {
        nuevosErrores.ciudadRecogida = 'La ciudad de recogida es requerida';
      }
      if (!formData.ciudadEntrega) {
        nuevosErrores.ciudadEntrega = 'La ciudad de entrega es requerida';
      }
      if (!formData.tipoPaquete) {
        nuevosErrores.tipoPaquete = 'El tipo de paquete es requerido';
      }

      if (Object.keys(nuevosErrores).length > 0) {
        setErrores(nuevosErrores);
        setLoading(false);
        return;
      }

      setErrores({});

      if (usarDireccionTemporal?.recogida && guardarDireccionRecogida) {
        await guardarDireccionTemporal('recogida');
      }
      if (usarDireccionTemporal?.entrega && guardarDireccionEntrega) {
        await guardarDireccionTemporal('entrega');
      }

      let pedidoData = {
        ...formData,
        id: editingPedido?.id,
        tarifaId: parseInt(formData.tarifaId),
        clienteId: parseInt(formData.clienteId),
        tipoServicioId: parseInt(formData.tipoServicioId),
        direccionRecogida: formData.direccionRecogidaId ? undefined : formData.direccionRecogidaTemporal,
        direccionEntrega: formData.direccionEntregaId ? undefined : formData.direccionEntregaTemporal,
        usuarioId:usuarioId
      };

      const { mensajeroId, estadoId, ...pedidoSinCamposEspeciales } = pedidoData;
      pedidoData = pedidoSinCamposEspeciales;
    

      const url = editingPedido?.id 
        ? `/proxy/api/pedidos/${editingPedido.id}` 
        : '/proxy/api/pedidos';
      const method = editingPedido?.id ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
          'x-additional-data': additionalData,
        },
        body: JSON.stringify(pedidoData)
      });


      if (response.ok) {
        const data = await response.json();
        
        const pedidoId = editingPedido?.id || data.data?.id;
        
        if (!pedidoId) {
          throw new Error('No se pudo obtener el ID del pedido');
        }

        if (formData.mensajeroId && formData.mensajeroId !== '') {
          try {
            await asignarMensajero(pedidoId, formData.mensajeroId);
            
            await new Promise(resolve => setTimeout(resolve, 500));
            
          } catch (error) {
          }
        }

        if (editingPedido?.id) {
          
          const estadoActualNumerico = parseInt(editingPedido.estadoId);
          const estadoNuevoNumerico = parseInt(formData.estadoId);
          
          if (estadoActualNumerico !== estadoNuevoNumerico) {
            try {
              
              const transicionesValidas = {
                1: [2, 5], 
                2: [3],    
                3: [4],    
                4: [],     
                5: []      
              };
              
              if (transicionesValidas[estadoActualNumerico]?.includes(estadoNuevoNumerico)) {
                await actualizarEstado(editingPedido.id, estadoNuevoNumerico)
              } 
              
            } catch (error) {
              console.error('❌ Error al actualizar estado:', error);
              if (!error.message.includes('Transición de estado no válida')) {
                alert('Error al actualizar el estado del pedido. Revisa la consola para más detalles.');
              }
            }
          }
        }

        setFormVisible(false);
        setEditingPedido(null);
        setFormData({});
        
        setClienteSeleccionado(null);
        setClienteBusqueda('');
        setDropdownClienteVisible(false);
        setTipoServicioBusqueda('');
        setDropdownTipoServicioVisible(false);
        setMensajeroBusqueda('');
        setDropdownMensajeroVisible(false);

        if (typeof resetearEstadosAdicionales === 'function') {
          resetearEstadosAdicionales();
        }

        await cargarDatos();

      } else {
        const errorData = await response.json();
        console.error('❌ Error del servidor:', errorData);
        alert(`Error del servidor: ${JSON.stringify(errorData)}`);
      }

    } catch (error) {
      console.error('❌ Error al guardar pedido:', error);
      alert(`Error al guardar pedido: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };


const asignarMensajero = async (pedidoId, mensajeroId) => {
  try {
    const response = await fetch(`/proxy/api/pedidos/${pedidoId}/asignar-mensajero`, {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        'x-additional-data': additionalData,
      },
      body: JSON.stringify({      
      mensajeroId: parseInt(mensajeroId), 
      usuarioId: usuarioId,
      tenantId: tenantId
      })
    });

    if (!response.ok) {
      throw new Error('Error al asignar mensajero');
    }

    return await response.json();
  } catch (error) {
    console.error('Error al asignar mensajero:', error);
    throw error;
  }
};

const validarYAjustarEstado = (mensajeroId, estadoActual) => {
  const esCreacion = !editingPedido?.id;
  const tieneMensajero = mensajeroId && mensajeroId !== '';
  
  if (esCreacion) {
    return tieneMensajero ? ESTADOS.ASIGNADO : ESTADOS.PENDIENTE;
  } else {
    const estadoActualNumerico = parseInt(estadoActual);
    
    if (estadoActualNumerico >= 1 && estadoActualNumerico <= 5) {
      if (!tieneMensajero && estadoActualNumerico === ESTADOS.ASIGNADO) {
        return ESTADOS.PENDIENTE;
      }
      
      return estadoActualNumerico;
    }
    
    return tieneMensajero ? ESTADOS.ASIGNADO : ESTADOS.PENDIENTE;
  }
};

useEffect(() => {
  const nuevoEstado = validarYAjustarEstado(formData.mensajeroId, formData.estadoId);
  
  if (formData.estadoId !== nuevoEstado) {
    setFormData(prev => ({
      ...prev,
      estadoId: nuevoEstado
    }));
  }
}, [formData.mensajeroId, editingPedido?.id]);

const handleMensajeroChange = (e) => {
  const mensajeroId = e.target.value;
  const nuevoEstado = validarYAjustarEstado(mensajeroId, formData.estadoId);
  
  setFormData(prev => ({
    ...prev,
    mensajeroId: mensajeroId,
    estadoId: nuevoEstado
  }));
};


const obtenerMensajeInformativo = () => {
  const esCreacion = !editingPedido?.id;
  const tieneMensajero = formData.mensajeroId && formData.mensajeroId !== '';
  
  if (esCreacion) {
    return tieneMensajero 
      ? "Estado se establecerá como 'Asignado' automáticamente"
      : "Estado se establecerá como 'Pendiente' automáticamente";
  } else {
    return tieneMensajero 
      ? "Estados disponibles: Asignado, En Tránsito, Entregado"
      : "Estados disponibles: Pendiente, Cancelado";
  }
};


  const handleEditar = async (pedido) => {
      
      setEditingPedido(pedido);
      setFormVisible(true);
      setErrores({});
      
      setDropdownClienteVisible(false);
      setDropdownTipoServicioVisible(false);
      setDropdownMensajeroVisible(false);
      
      const obtenerInfoFallbackRecogida = (pedido) => {
          
          const recogidaInfo = getCiudadBarrioRecogida(pedido);
          const direccionTexto = getDireccionRecogida(pedido);
          
          let ciudad = '';
          let barrio = '';
          
          if (recogidaInfo && recogidaInfo.ciudad) ciudad = recogidaInfo.ciudad;
          if (recogidaInfo && recogidaInfo.barrio) barrio = recogidaInfo.barrio;
          
          if (!ciudad && pedido.ciudadRecogida) ciudad = pedido.ciudadRecogida;
          if (!barrio && pedido.barrioRecogida) barrio = pedido.barrioRecogida;
          
          if ((!ciudad || !barrio) && pedido.direccionRecogidaId && direcciones.length > 0) {
              const direccionGlobal = direcciones.find(d => d.id === parseInt(pedido.direccionRecogidaId) && d.esRecogida === true);
              if (direccionGlobal) {
                  if (!ciudad) ciudad = direccionGlobal.ciudad || '';
                  if (!barrio) barrio = direccionGlobal.barrio || '';
              }
          }
          
          if ((!ciudad || !barrio) && pedido.direccionRecogidaId && direccionesRecogida.length > 0) {
              const direccionRecogida = direccionesRecogida.find(d => d.id === parseInt(pedido.direccionRecogidaId));
              if (direccionRecogida) {
                  if (!ciudad) ciudad = direccionRecogida.ciudad || '';
                  if (!barrio) barrio = direccionRecogida.barrio || '';
              }
          }
          
          if (!ciudad && pedido.direccionRecogida?.ciudad) ciudad = pedido.direccionRecogida.ciudad;
          if (!barrio && pedido.direccionRecogida?.barrio) barrio = pedido.direccionRecogida.barrio;
          
          if (!ciudad && pedido.recogida?.ciudad) ciudad = pedido.recogida.ciudad;
          if (!barrio && pedido.recogida?.barrio) barrio = pedido.recogida.barrio;
          
          const resultado = {
              direccion: direccionTexto,
              ciudad: ciudad || '',
              barrio: barrio || '',
              telefono: pedido.telefonoRecogida || ''
          }
          
          return resultado;
      };
      
      const obtenerInfoFallbackEntrega = (pedido) => {
          
          const entregaInfo = getCiudadBarrioEntrega(pedido);
          const direccionTexto = getDireccionEntrega(pedido);
          
          let ciudad = '';
          let barrio = '';
          
          if (entregaInfo && entregaInfo.ciudad) ciudad = entregaInfo.ciudad;
          if (entregaInfo && entregaInfo.barrio) barrio = entregaInfo.barrio;
          
          if (!ciudad && pedido.ciudadEntrega) ciudad = pedido.ciudadEntrega;
          if (!barrio && pedido.barrioEntrega) barrio = pedido.barrioEntrega;
          
          if ((!ciudad || !barrio) && pedido.direccionEntregaId && direcciones.length > 0) {
              const direccionGlobal = direcciones.find(d => d.id === parseInt(pedido.direccionEntregaId) && d.esEntrega === true);
              if (direccionGlobal) {
                  if (!ciudad) ciudad = direccionGlobal.ciudad || '';
                  if (!barrio) barrio = direccionGlobal.barrio || '';
              }
          }
          
          if ((!ciudad || !barrio) && pedido.direccionEntregaId && direccionesEntrega.length > 0) {
              const direccionEntrega = direccionesEntrega.find(d => d.id === parseInt(pedido.direccionEntregaId));
              if (direccionEntrega) {
                  if (!ciudad) ciudad = direccionEntrega.ciudad || '';
                  if (!barrio) barrio = direccionEntrega.barrio || '';
              }
          }
          
          if (!ciudad && pedido.direccionEntrega?.ciudad) ciudad = pedido.direccionEntrega.ciudad;
          if (!barrio && pedido.direccionEntrega?.barrio) barrio = pedido.direccionEntrega.barrio;
          
          if (!ciudad && pedido.entrega?.ciudad) ciudad = pedido.entrega.ciudad;
          if (!barrio && pedido.entrega?.barrio) barrio = pedido.entrega.barrio;
          
          const resultado = {
              direccion: direccionTexto,
              ciudad: ciudad || '',
              barrio: barrio || '',
              telefono: pedido.telefonoEntrega || ''
          };
          
          return resultado;
      };
      
      let usarDireccionRecogidaGuardada = false;
      let usarDireccionEntregaGuardada = false;
      let finalUsarDireccionTemporal = { recogida: false, entrega: false };
      
      let newFormData = {
          clienteId: pedido.clienteId || '',
          tipoServicioId: pedido.tipoServicioId || '',
          tarifaId: pedido.tarifaId || '',
          mensajeroId: pedido.mensajeroId || '',
          estadoId: pedido.estadoId || '',
          
          tipoPaquete: pedido.tipoPaquete || '',
          pesoKg: pedido.pesoKg || '',
          valorDeclarado: pedido.valorDeclarado || '',
          costoCompra: pedido.costoCompra || '',
          subtotal: pedido.subtotal || '',
          total: pedido.total || '',
          tiempoEntregaMinutos: pedido.tiempoEntregaMinutos || '',
          fechaEntrega: pedido.fechaEntrega ? 
              new Date(pedido.fechaEntrega).toISOString().slice(0, 16) : '',
          notas: pedido.notas || ''
      };
      
      if (pedido.direccionRecogidaId) {
          const direccion = direccionesRecogida.find(d => d.id === parseInt(pedido.direccionRecogidaId));
          
          if (direccion) {
              const ciudad = direccion.direccion?.ciudad || direccion.ciudad;
              const barrio = direccion.direccion?.barrio || direccion.barrio;
              
              newFormData = {
                  ...newFormData,
                  direccionRecogidaId: String(pedido.direccionRecogidaId),
                  direccionRecogidaTemporal: '',
                  ciudadRecogida: ciudad || '',
                  barrioRecogida: barrio || '',
                  telefonoRecogida: direccion.telefono || pedido.telefonoRecogida || ''
              };
              
              usarDireccionRecogidaGuardada = true;
              finalUsarDireccionTemporal.recogida = false;
          } else {
              
              const infoFallback = obtenerInfoFallbackRecogida(pedido);
              
              newFormData = {
                  ...newFormData,
                  direccionRecogidaId: '',
                  direccionRecogidaTemporal: infoFallback.direccion,
                  ciudadRecogida: infoFallback.ciudad,
                  barrioRecogida: infoFallback.barrio,
                  telefonoRecogida: infoFallback.telefono
              };
              
              usarDireccionRecogidaGuardada = false;
              finalUsarDireccionTemporal.recogida = true;
          }
      } else {
          const infoFallback = obtenerInfoFallbackRecogida(pedido);
          
          newFormData = {
              ...newFormData,
              direccionRecogidaId: '',
              direccionRecogidaTemporal: infoFallback.direccion,
              ciudadRecogida: infoFallback.ciudad,
              barrioRecogida: infoFallback.barrio,
              telefonoRecogida: infoFallback.telefono
          };
          
          usarDireccionRecogidaGuardada = false;
          finalUsarDireccionTemporal.recogida = true;
      }
      
      if (pedido.direccionEntregaId) {
          const direccion = direccionesEntrega.find(d => d.id === parseInt(pedido.direccionEntregaId));
          
          if (direccion) {
              const ciudad = direccion.direccion?.ciudad || direccion.ciudad;
              const barrio = direccion.direccion?.barrio || direccion.barrio;
              
              newFormData = {
                  ...newFormData,
                  direccionEntregaId: String(pedido.direccionEntregaId),
                  direccionEntregaTemporal: '',
                  ciudadEntrega: ciudad || '',
                  barrioEntrega: barrio || '',
                  telefonoEntrega: direccion.telefono || pedido.telefonoEntrega || ''
              };
              
              usarDireccionEntregaGuardada = true;
              finalUsarDireccionTemporal.entrega = false;
          } else {
              
              const infoFallback = obtenerInfoFallbackEntrega(pedido);
              
              newFormData = {
                  ...newFormData,
                  direccionEntregaId: '',
                  direccionEntregaTemporal: infoFallback.direccion,
                  ciudadEntrega: infoFallback.ciudad,
                  barrioEntrega: infoFallback.barrio,
                  telefonoEntrega: infoFallback.telefono
              };
              
              usarDireccionEntregaGuardada = false;
              finalUsarDireccionTemporal.entrega = true;
          }
      } else {
          const infoFallback = obtenerInfoFallbackEntrega(pedido);
          
          newFormData = {
              ...newFormData,
              direccionEntregaId: '',
              direccionEntregaTemporal: infoFallback.direccion,
              ciudadEntrega: infoFallback.ciudad,
              barrioEntrega: infoFallback.barrio,
              telefonoEntrega: infoFallback.telefono
          };
          
          usarDireccionEntregaGuardada = false;
          finalUsarDireccionTemporal.entrega = true;
      }
      
      setFormData(newFormData);
      setUsarDireccionTemporal(finalUsarDireccionTemporal);

      if (pedido.clienteId) {
          const cliente = clientes.find(c => c.id === pedido.clienteId);
          if (cliente) {
              setClienteBusqueda(`${cliente.nombre} - ${cliente.telefono}`);
              setClienteSeleccionado(cliente);
          } else {
              setClienteBusqueda('Sin cliente registrado');
          }
      } else {
          setClienteBusqueda('Sin cliente registrado');
      }
      
      if (pedido.tipoServicioId) {
          const tipoServicio = tiposServicio.find(ts => ts.id === pedido.tipoServicioId);
          if (tipoServicio) {
              setTipoServicioBusqueda(tipoServicio.nombre);
          } else {
              setTipoServicioBusqueda('');
          }
      } else {
          setTipoServicioBusqueda('');
      }
      
      const inicializarMensajero = (mensajerosList) => {
          if (pedido.mensajeroId) {
              const mensajero = mensajerosList.find(m => m.id === pedido.mensajeroId);
              if (mensajero) {
                  setMensajeroBusqueda(`${mensajero.nombres} ${mensajero.apellidos}`);
              } else {
                  setMensajeroBusqueda('Sin asignar');
              }
          } else {
              setMensajeroBusqueda('Sin asignar');
          }
      };
      
      setTimeout(() => {
          formRef.current?.scrollIntoView({ behavior: 'smooth' });
      }, 100);
      
      const empresaId = pedido.mensajeriaId;
      if (empresaId) {
          setMensajeros([]);
          try {
              const response = await axios.get(`/proxy/api/mensajeros/mensajeria/${empresaId}`, { headers });
              
              let data = [];
              if (Array.isArray(response.data)) {
                  data = response.data;
              } else if (Array.isArray(response.data.data)) {
                  data = response.data.data;
              } else {
                  console.warn('Respuesta inesperada para mensajeros en edición:', response.data);
              }
              
              setMensajeros(data);
              
              inicializarMensajero(data);
              
          } catch (error) {
              console.error('Error al cargar mensajeros para edición:', error);
              setMensajeros([]);
              setMensajeroBusqueda('Sin asignar');
          }
      } else {
          setMensajeros([]);
          setMensajeroBusqueda('Sin asignar');
      }
  };

  const handleEliminar = (id) => {
    if (confirm('¿Eliminar pedido?')) {
      axios.delete(`/proxy/api/pedidos/${id}`, { headers })
        .then(() => {
          cargarDatos();
        })
        .catch(err => console.error('Error al eliminar pedido:', err));
    }
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

useEffect(() => {
  const handleClickOutside = (event) => {
    if (clienteInputRef.current && !clienteInputRef.current.contains(event.target)) {
      setMostrarListaClientes(false);
    }
  };

  document.addEventListener('mousedown', handleClickOutside);
  return () => document.removeEventListener('mousedown', handleClickOutside);
}, []);

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

  useEffect(() => {
    if (!editingPedido?.id) {
      const nuevoEstado = formData.mensajeroId && formData.mensajeroId !== '' 
        ? ESTADOS.ASIGNADO 
        : ESTADOS.PENDIENTE;
      
      if (formData.estadoId !== nuevoEstado) {
        setFormData(prev => ({
          ...prev,
          estadoId: nuevoEstado
        }));
      }
    }
  }, [formData.mensajeroId, editingPedido?.id]);

  useEffect(() => {
    if (editingPedido?.id && tiposServicio.length > 0 && formData.tipoServicioId) {
      const tipoServicioSeleccionado = tiposServicio.find(
        servicio => servicio.id === formData.tipoServicioId
      );
      
      if (tipoServicioSeleccionado) {
        setTipoServicioBusqueda(formatearNombre(tipoServicioSeleccionado.nombre));
      }
    }
  }, [editingPedido?.id, formData.tipoServicioId, tiposServicio]);

  const esEstadoDeshabilitado = () => {
    return !editingPedido?.id;
  };

  const obtenerEstadosDisponibles = () => {
    const esCreacion = !editingPedido?.id;
    const tieneMensajero = formData.mensajeroId && formData.mensajeroId !== '';

    if (esCreacion) {
      const estadoAutomatico = tieneMensajero ? ESTADOS.ASIGNADO : ESTADOS.PENDIENTE;
      const result = estadosPedido.filter(estado => estado.id === estadoAutomatico);
      return result;
    } else {
      if (tieneMensajero) {
        const ids = [ESTADOS.ASIGNADO, ESTADOS.EN_TRANSITO, ESTADOS.ENTREGADO];
        const result = estadosPedido.filter(estado =>
          ids.includes(Number(estado.id))
        );
        return result;
      } else {
        const ids = [ESTADOS.PENDIENTE, ESTADOS.CANCELADO];
        const result = estadosPedido.filter(estado =>
          ids.includes(Number(estado.id))
        );
        return result;
      }
    }
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (clienteInputRef.current && !clienteInputRef.current.contains(event.target)) {
        setMostrarListaClientes(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

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

const handleCerrarModal = () => {
    setShowPedidosModal(false);
    setShowPedidosComponent(true);
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

 const PedidosRecientesModal = ({ show, onHide, clienteSeleccionado, pedidosCliente }) => {
    if (!show || !clienteSeleccionado || pedidosCliente.length === 0) {
        return null;
    }

    const handleBackdropClick = (e) => {
        if (e.target === e.currentTarget) {
            onHide();
        }
    };

    return (
        <div 
            className="modal-backdrop-custom" 
            style={{
                position: 'fixed',
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                backgroundColor: 'rgba(0,0,0,0.5)',
                zIndex: 1040
            }}
            onClick={handleBackdropClick}
        >
            <div 
                className="modal fade show" 
                style={{ display: 'block', zIndex: 1050 }} 
                tabIndex="-1"
            >
                <div className="modal-dialog modal-xl">
                    <div className="modal-content">
                        <div className="modal-header bg-info text-white">
                            <h5 className="modal-title">
                                <i className="bi bi-clock-history me-2"></i>
                                Pedidos Recientes de {clienteSeleccionado.nombre}
                            </h5>
                            <button 
                                type="button" 
                                className="btn-close btn-close-white" 
                                onClick={onHide}
                            ></button>
                        </div>

                        <div className="modal-body">
                            <div className="row">
                                {pedidosCliente.map(pedido => (
                                    <div key={pedido.id} className="col-md-6 mb-3">
                                        <div className="card border-left-primary h-100">
                                            <div className="card-body p-3">
                                                <div className="d-flex justify-content-between align-items-start mb-2">
                                                    <h6 className="card-title mb-0">
                                                        Pedido #{pedido.id}
                                                    </h6>
                                                    <span className={`badge bg-${getEstadoColor(pedido.estadoNombre)}`}>
                                                        {pedido.estadoNombre.replace('_', ' ')}
                                                    </span>
                                                </div>

                                                <div className="small text-muted mb-2">
                                                    {formatearFecha(pedido.fechaCreacion)}
                                                </div>

                                                <div className="mb-2">
                                                    <strong>Tipo:</strong> {pedido.tipoPaquete || 'No especificado'}
                                                </div>

                                                <div className="mb-2">
                                                    <strong>Servicio:</strong> {formatearNombre(pedido.tipoServicioNombre)}
                                                </div>

                                                <div className="mb-2">
                                                    <strong>Recogida:</strong><br />
                                                    <small>{getDireccionRecogida(pedido)}</small>
                                                </div>

                                                <div className="mb-3">
                                                    <strong>Entrega:</strong><br />
                                                    <small>{getDireccionEntrega(pedido)}</small>
                                                </div>

                                                <div className="d-flex justify-content-between align-items-center">
                                                    <span className="fw-bold text-success">
                                                        ${pedido.total?.toLocaleString()}
                                                    </span>
                                                    <button
                                                        type="button"
                                                        className="btn btn-sm btn-outline-primary"
                                                        onClick={() => {
                                                            autocompletarConPedido(pedido);
                                                            onHide();
                                                        }}
                                                        title="Usar datos de este pedido"
                                                    >
                                                        <i className="bi bi-arrow-repeat me-1"></i>
                                                        Usar datos
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        <div className="modal-footer">
                            <div className="me-auto">
                                <small className="text-muted">
                                    <i className="bi bi-info-circle me-1"></i>
                                    Estos pedidos también aparecen en la tarjeta de abajo para referencia
                                </small>
                            </div>
                            <button 
                                type="button" 
                                className="btn btn-secondary" 
                                onClick={onHide}
                            >
                                <i className="bi bi-x-circle me-1"></i>
                                Cerrar
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};


const PedidosRecientesComponent = () => {
    if (!clienteSeleccionado || pedidosCliente.length === 0) {
        return null;
    }
    
    return (
        <div className="card shadow-sm mb-4 mx-auto" style={{ maxWidth: '900px' }}>
            <div className="card-header bg-info text-white">
                <i className="bi bi-clock-history me-2"></i>
                Pedidos Recientes de {clienteSeleccionado.nombre}
            </div>
            <div className="card-body">
                <div className="row">
                    {pedidosCliente.map(pedido => (
                        <div key={pedido.id} className="col-md-6 mb-3">
                            <div className="card border-left-primary h-100">
                                <div className="card-body p-3">
                                    <div className="d-flex justify-content-between align-items-start mb-2">
                                        <h6 className="card-title mb-0">
                                            Pedido #{pedido.id}
                                        </h6>
                                        <span className={`badge bg-${getEstadoColor(pedido.estadoNombre)}`}>
                                            {pedido.estadoNombre.replace('_', ' ')}
                                        </span>
                                    </div>
                                    
                                    <div className="small text-muted mb-2">
                                        {formatearFecha(pedido.fechaCreacion)}
                                    </div>
                                    
                                    <div className="mb-2">
                                        <strong>Tipo:</strong> {pedido.tipoPaquete || 'No especificado'}
                                    </div>
                                    
                                    <div className="mb-2">
                                        <strong>Servicio:</strong> {formatearNombre(pedido.tipoServicioNombre)}
                                    </div>
                                    
                                    <div className="mb-2">
                                        <strong>Recogida:</strong><br />
                                        <small>{getDireccionRecogida(pedido)}</small>
                                    </div>
                                    
                                    <div className="mb-3">
                                        <strong>Entrega:</strong><br />
                                        <small>{getDireccionEntrega(pedido)}</small>
                                    </div>
                                    
                                    <div className="d-flex justify-content-between align-items-center">
                                        <span className="fw-bold text-success">
                                            ${pedido.total?.toLocaleString()}
                                        </span>
                                        <button
                                            type="button"
                                            className="btn btn-sm btn-outline-primary"
                                            onClick={() => autocompletarConPedido(pedido)}
                                            title="Usar datos de este pedido"
                                        >
                                            <i className="bi bi-arrow-repeat me-1"></i>
                                            Usar datos
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
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
            <th className="text-center">Estado</th>
            <th className="text-center">Mensajero</th>
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
                  <span
                    className="badge"
                    style={{
                      backgroundColor: estadoInfo.color + '20',
                      color: estadoInfo.color,
                    }}
                  >
                    {pedido.estadoNombre
                      ? formatearNombreEstado(pedido.estadoNombre)
                      : formatearNombreEstado(getEstadoInfo(pedido.estadoId).label)}
                  </span>
                </td>
                <td>
                  <div className="fw-semibold text-muted">
                    {pedido.mensajeroNombre || getMensajeroName(pedido.mensajeroId)}
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
                      className={`btn ${pedido.estado === 4 || pedido.estado === 5 ? 'btn-secondary' : 'btn-outline-primary'}`}
                      onClick={() => handleEditar(pedido)}
                      disabled={pedido.estadoId === 4 || pedido.estadoId === 5}
                    >
                      <i className="bi bi-pencil"></i>
                    </button>
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
                    <button
                      className="btn btn-outline-danger"
                      onClick={() => handleEliminar(pedido.id)}
                    >
                      <i className="bi bi-trash"></i>
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

                <div className="mb-2">
                  <small className="text-muted">
                    <i className="bi bi-person-badge me-1" style={{ color: '#6f42c1' }}></i>
                    Mensajero:
                  </small>
                  <div className="fw-semibold" style={{ color:'#6c757d' }}>
                    {pedido.mensajeroNombre || getMensajeroName(pedido.mensajeroId)}
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
                  className="btn btn-sm btn-outline-primary"
                  onClick={() => handleEditar(pedido)}
                >
                  <i className="bi bi-pencil-square me-1"></i>Editar
                </button>

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

                <button
                  className="btn btn-sm btn-outline-danger"
                  onClick={() => handleEliminar(pedido.id)}
                >
                  <i className="bi bi-trash-fill me-1"></i>Eliminar
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
            <button onClick={handleNuevoPedido} className="btn btn-success">
              <i className="bi bi-plus-lg me-2"></i>
              Nuevo pedido
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

        <PedidosRecientesModal
          show={showPedidosModal}
          onHide={handleCerrarModal}
          clienteSeleccionado={clienteSeleccionado}
          pedidosCliente={pedidosCliente}
        />

        {formVisible && (
          <div ref={formRef} className="card shadow-sm mb-4 mx-auto" style={{ maxWidth: '900px' }}>
            <div className="card-header bg-primary text-white">
              <i className="bi bi-box-seam me-2"></i>
              {editingPedido?.id ? 'Editar pedido' : 'Nuevo pedido'}
            </div>
            <div className="card-body">
              <form onSubmit={handleGuardar}>
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-person-fill me-1" style={{ color: '#6f42c1' }}></i>
                      Cliente <span className="text-danger">*</span>
                    </label>
                    <div className="dropdown">
                      <div className="input-group">
                        <input
                          type="text"
                          className="form-control"
                          placeholder="Buscar cliente"
                          value={clienteBusqueda}
                          onChange={(e) => {
                            setClienteBusqueda(e.target.value);
                            setDropdownClienteVisible(true);
                          }}
                          onFocus={() => setDropdownClienteVisible(true)}
                        />
                        <button
                          className="btn btn-outline-secondary"
                          type="button"
                          onClick={() => setDropdownClienteVisible(!dropdownClienteVisible)}
                        >
                          <i className="bi bi-chevron-down"></i>
                        </button>
                      </div>
                      
                      {dropdownClienteVisible && (
                        <div className="dropdown-menu show w-100" style={{ maxHeight: '200px', overflowY: 'auto' }}>
                          <div className="dropdown-item" 
                              onClick={() => {
                                setFormData({...formData, clienteId: null});
                                setClienteBusqueda('Sin cliente registrado');
                                setDropdownClienteVisible(false);
                              }}>
                            <i className="bi bi-person-x me-2"></i>
                            Sin cliente registrado
                          </div>
                          <div className="dropdown-divider"></div>
                          {clientes
                            .filter(cliente => 
                              cliente.nombre.toLowerCase().includes(clienteBusqueda.toLowerCase()) ||
                              cliente.telefono.includes(clienteBusqueda)
                            )
                            .map(cliente => (
                              <div key={cliente.id} 
                                  className="dropdown-item"
                                  onClick={() => {
                                    handleClienteChange({target: {value: cliente.id}});
                                    setClienteBusqueda(`${cliente.nombre} - ${cliente.telefono}`);
                                    setDropdownClienteVisible(false);
                                  }}>
                                <div>
                                  <strong>{cliente.nombre}</strong>
                                  <br />
                                  <small className="text-muted">
                                    <i className="bi bi-telephone me-1"></i>{cliente.telefono}
                                    {cliente.email && (
                                      <>
                                        <br />
                                        <i className="bi bi-envelope me-1"></i>{cliente.email}
                                      </>
                                    )}
                                  </small>
                                </div>
                              </div>
                            ))}
                          {clientes.filter(cliente => 
                            cliente.nombre.toLowerCase().includes(clienteBusqueda.toLowerCase()) ||
                            cliente.telefono.includes(clienteBusqueda)
                          ).length === 0 && clienteBusqueda && (
                            <div className="dropdown-item text-muted">
                              <i className="bi bi-search me-2"></i>
                              No se encontraron clientes
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                    <div className="form-text">
                    </div>
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-truck me-1" style={{ color: '#007bff' }}></i>
                      Tipo de servicio <span className="text-danger">*</span>
                    </label>
                    <div className="dropdown">
                      <div className="input-group">
                        <input
                          type="text"
                          className="form-control"
                          placeholder="Buscar tipo de servicio"
                          value={tipoServicioBusqueda}
                          onChange={(e) => {
                            setTipoServicioBusqueda(e.target.value);
                            setDropdownTipoServicioVisible(true);
                          }}
                          onFocus={() => setDropdownTipoServicioVisible(true)}
                        />
                        <button
                          className="btn btn-outline-secondary"
                          type="button"
                          onClick={() => setDropdownTipoServicioVisible(!dropdownTipoServicioVisible)}
                        >
                          <i className="bi bi-chevron-down"></i>
                        </button>
                      </div>
                      
                      {dropdownTipoServicioVisible && (
                        <div className="dropdown-menu show w-100" style={{ maxHeight: '200px', overflowY: 'auto' }}>
                          {tiposServicio
                            .filter(servicio => 
                              servicio.nombre.toLowerCase().includes(tipoServicioBusqueda.toLowerCase())
                            )
                            .map(servicio => (
                              <div key={servicio.id} 
                                  className="dropdown-item"
                                  onClick={() => {
                                    setFormData({...formData, tipoServicioId: servicio.id});
                                    setTipoServicioBusqueda(formatearNombre(servicio.nombre));
                                    setDropdownTipoServicioVisible(false);
                                  }}>
                                <div>
                                  <strong>{formatearNombre(servicio.nombre)}</strong>
                                </div>
                              </div>
                            ))}
                          {tiposServicio.filter(servicio => 
                            servicio.nombre.toLowerCase().includes(tipoServicioBusqueda.toLowerCase())
                          ).length === 0 && tipoServicioBusqueda && (
                            <div className="dropdown-item text-muted">
                              <i className="bi bi-search me-2"></i>
                              No se encontraron tipos de servicio
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                    <div className="form-text">
                    </div>
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-person-badge me-1" style={{ color: '#28a745' }}></i>
                      Mensajero
                    </label>
                    <div className="dropdown">
                      <div className="input-group">
                        <input
                          type="text"
                          className="form-control"
                          placeholder="Buscar mensajero"
                          value={mensajeroBusqueda}
                          onChange={(e) => {
                            setMensajeroBusqueda(e.target.value);
                            setDropdownMensajeroVisible(true);
                          }}
                          onFocus={() => setDropdownMensajeroVisible(true)}
                        />
                        <button
                          className="btn btn-outline-secondary"
                          type="button"
                          onClick={() => setDropdownMensajeroVisible(!dropdownMensajeroVisible)}
                        >
                          <i className="bi bi-chevron-down"></i>
                        </button>
                      </div>
                      
                      {dropdownMensajeroVisible && (
                        <div className="dropdown-menu show w-100" style={{ maxHeight: '200px', overflowY: 'auto' }}>
                          <div className="dropdown-item" 
                              onClick={() => {
                                handleMensajeroChange({target: {value: ''}});
                                setMensajeroBusqueda('Sin asignar');
                                setDropdownMensajeroVisible(false);
                              }}>
                            <i className="bi bi-person-x me-2"></i>
                            Sin asignar
                          </div>
                          <div className="dropdown-divider"></div>
                          {mensajeros
                            .filter(mensajero => mensajero.disponibilidad && mensajero.disponibilidad !== 0)
                            .filter(mensajero => 
                              `${mensajero.nombres} ${mensajero.apellidos}`.toLowerCase().includes(mensajeroBusqueda.toLowerCase()) ||
                              (mensajero.telefono && mensajero.telefono.includes(mensajeroBusqueda))
                            )
                            .map(mensajero => (
                              <div key={mensajero.id} 
                                  className="dropdown-item"
                                  onClick={() => {
                                    handleMensajeroChange({target: {value: mensajero.id}});
                                    setMensajeroBusqueda(`${mensajero.nombres} ${mensajero.apellidos}`);
                                    setDropdownMensajeroVisible(false);
                                  }}>
                                <div>
                                  <strong>{mensajero.nombres} {mensajero.apellidos}</strong>
                                  <br />
                                  <small className="text-muted">
                                    {mensajero.telefono && (
                                      <>
                                        <i className="bi bi-telephone me-1"></i>{mensajero.telefono}
                                        <br />
                                      </>
                                    )}
                                    <i className="bi bi-check-circle me-1" style={{ color: '#28a745' }}></i>
                                    Disponible
                                    {mensajero.vehiculo && (
                                      <>
                                        <br />
                                        <i className="bi bi-bicycle me-1"></i>{mensajero.vehiculo}
                                      </>
                                    )}
                                  </small>
                                </div>
                              </div>
                            ))}
                          {mensajeros
                            .filter(mensajero => mensajero.disponibilidad && mensajero.disponibilidad !== 0)
                            .filter(mensajero => 
                              `${mensajero.nombres} ${mensajero.apellidos}`.toLowerCase().includes(mensajeroBusqueda.toLowerCase()) ||
                              (mensajero.telefono && mensajero.telefono.includes(mensajeroBusqueda))
                            ).length === 0 && mensajeroBusqueda && mensajeroBusqueda !== 'Sin asignar' && (
                            <div className="dropdown-item text-muted">
                              <i className="bi bi-search me-2"></i>
                              No se encontraron mensajeros disponibles
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                    <div className="form-text">
                      <small>
                        <i className="bi bi-info-circle me-1"></i>
                        {obtenerMensajeInformativo()}
                      </small>
                    </div>
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-flag me-1" style={{ color: '#17a2b8' }}></i>
                      Estado
                    </label>
                    <select
                      className="form-select"
                      value={formData.estadoId || ''}
                      onChange={handleEstadoChange}
                      disabled={esEstadoDeshabilitado()}
                    >
                      <option value="" disabled hidden>Seleccionar estado</option>
                      {obtenerEstadosDisponibles().map(estado => (
                        <option key={estado.id} value={estado.id}>
                          {formatearNombreEstado(estado.nombre)}
                        </option>
                      ))}
                    </select>
                    <div className="form-text">
                      <small> <i className="bi bi-info-circle me-1"></i>
                        {!editingPedido?.id 
                          ? "En creación, el estado se establece automáticamente" 
                          : "Estados disponibles según el mensajero asignado"
                        }
                      </small>
                    </div>
                  </div>
                </div>

                <div className="row mt-4">
                  <div className="col-12">
                    <h6 className="border-bottom pb-2 mb-3">
                      <i className="bi bi-geo-alt me-2"></i>Información de Recogida
                    </h6>
                  </div>

                  {formData.direccionRecogidaId && !usarDireccionTemporal.recogida && direccionesRecogida.length > 0 && (
                    <div className="col-md-8 mb-3">
                      <label className="form-label">
                        <i className="bi bi-geo-alt me-1" style={{ color: '#dc3545' }}></i>
                        Dirección de recogida <span className="text-danger">*</span>
                      </label>
                      <select
                        className="form-select"
                        value={formData.direccionRecogidaId || ''}
                        onChange={(e) => {
                          handleDireccionChange('recogida', e.target.value);
                        }}
                        required
                      >
                        <option value="" disabled hidden>Seleccione una dirección</option>
                        {getDireccionesDisponiblesRecogida().map(direccion => (
                          <option key={direccion.id} value={direccion.id}>
                            {direccion.direccionCompleta} - {direccion.ciudad}
                            {direccion.esPredeterminadaRecogida && ' (Predeterminada Recogida)'}
                            {direccion.esPredeterminadaEntrega && ' (Predeterminada Entrega)'}
                          </option>
                        ))}
                        <option value="otra">
                          Otra dirección
                        </option>
                      </select>
                    </div>
                  )}

                  {(!formData.direccionRecogidaId || usarDireccionTemporal.recogida) && (
                    <div className="col-md-8 mb-3">
                      <label className="form-label">
                        <i className="bi bi-plus-circle me-1" style={{ color: '#6c757d' }}></i>
                        Dirección de recogida <span className="text-danger">*</span>
                      </label>
                      <input
                        type="text"
                        className="form-control"
                        value={formData.direccionRecogidaTemporal || ''}
                        onChange={(e) => setFormData({...formData, direccionRecogidaTemporal: e.target.value})}
                        placeholder="Ingrese la dirección completa de recogida"
                        required
                      />
                    </div>
                  )}

                  <div className="col-md-4 mb-3">
                    <label className="form-label">
                      <i className="bi bi-building me-1" style={{ color: '#fd7e14' }}></i>
                      Ciudad recogida
                    </label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.ciudadRecogida || ''}
                      onChange={(e) => setFormData({...formData, ciudadRecogida: e.target.value})}
                      placeholder="Ciudad"
                      readOnly={formData.direccionRecogidaId && !usarDireccionTemporal.recogida}
                    />
                  </div>

                  <div className="col-md-4 mb-3">
                    <label className="form-label">
                      <i className="bi bi-house me-1" style={{ color: '#20c997' }}></i>
                      Barrio recogida
                    </label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.barrioRecogida || ''}
                      onChange={(e) => setFormData({...formData, barrioRecogida: e.target.value})}
                      placeholder="Barrio"
                      readOnly={formData.direccionRecogidaId && !usarDireccionTemporal.recogida}
                    />
                  </div>

                  <div className="col-md-4 mb-3">
                    <label className="form-label">
                      <i className="bi bi-telephone me-1" style={{ color: '#e83e8c' }}></i>
                      Teléfono recogida <span className="text-danger">*</span>
                    </label>
                    <input
                      type="tel"
                      className="form-control"
                      value={formData.telefonoRecogida || ''}
                      onChange={(e) => setFormData({...formData, telefonoRecogida: e.target.value})}
                      placeholder="3001234567"
                      required
                    />
                  </div>
                </div>

                <div className="row mt-4">
                  <div className="col-12">
                    <h6 className="border-bottom pb-2 mb-3">
                      <i className="bi bi-arrow-right me-2"></i>Información de entrega
                    </h6>
                  </div>

                  {formData.direccionEntregaId && !usarDireccionTemporal.entrega && direccionesEntrega.length > 0 && (
                    <div className="col-md-8 mb-3">
                      <label className="form-label">
                        <i className="bi bi-geo-alt me-1" style={{ color: '#28a745' }}></i>
                        Dirección de entrega <span className="text-danger">*</span>
                      </label>
                      <select
                        className="form-select"
                        value={formData.direccionEntregaId || ''}
                        onChange={(e) => {
                          handleDireccionChange('entrega', e.target.value);
                        }}
                        required
                      >
                        <option value="" disabled hidden>Seleccione una dirección</option>
                        {getDireccionesDisponiblesEntrega().map(direccion => (
                          <option key={direccion.id} value={direccion.id}>
                            {direccion.direccionCompleta} - {direccion.ciudad}
                            {direccion.esPredeterminadaRecogida && ' (Predeterminada Recogida)'}
                            {direccion.esPredeterminadaEntrega && ' (Predeterminada Entrega)'}
                          </option>
                        ))}
                        <option value="otra">
                          Otra dirección
                        </option>
                      </select>
                    </div>
                  )}

                  {(!formData.direccionEntregaId || usarDireccionTemporal.entrega) && (
                    <div className="col-md-8 mb-3">
                      <label className="form-label">
                        <i className="bi bi-plus-circle me-1" style={{ color: '#6c757d' }}></i>
                        Dirección de entrega <span className="text-danger">*</span>
                      </label>
                      <input
                        type="text"
                        className="form-control"
                        value={formData.direccionEntregaTemporal || ''}
                        onChange={(e) => setFormData({...formData, direccionEntregaTemporal: e.target.value})}
                        placeholder="Ingrese la dirección completa de entrega"
                        required
                      />
                    </div>
                  )}

                  <div className="col-md-4 mb-3">
                    <label className="form-label">
                      <i className="bi bi-building me-1" style={{ color: '#fd7e14' }}></i>
                      Ciudad entrega
                    </label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.ciudadEntrega || ''}
                      onChange={(e) => setFormData({...formData, ciudadEntrega: e.target.value})}
                      placeholder="Ciudad"
                      readOnly={formData.direccionEntregaId && !usarDireccionTemporal.entrega}
                    />
                  </div>

                  <div className="col-md-4 mb-3">
                    <label className="form-label">
                      <i className="bi bi-house me-1" style={{ color: '#20c997' }}></i>
                      Barrio entrega
                    </label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.barrioEntrega || ''}
                      onChange={(e) => setFormData({...formData, barrioEntrega: e.target.value})}
                      placeholder="Barrio"
                      readOnly={formData.direccionEntregaId && !usarDireccionTemporal.entrega}
                    />
                  </div>

                  <div className="col-md-4 mb-3">
                    <label className="form-label">
                      <i className="bi bi-telephone me-1" style={{ color: '#e83e8c' }}></i>
                      Teléfono entrega <span className="text-danger">*</span>
                    </label>
                    <input
                      type="tel"
                      className="form-control"
                      value={formData.telefonoEntrega || ''}
                      onChange={(e) => setFormData({...formData, telefonoEntrega: e.target.value})}
                      placeholder="3001234567"
                      required
                    />
                  </div>
                </div>

                <div className="row mt-4">
                  <div className="col-12">
                    <h6 className="border-bottom pb-2 mb-3">
                      <i className="bi bi-currency-dollar me-2"></i>Información de tarifa
                    </h6>
                  </div>

                  <div className="col-md-12 mb-3">
                    <label className="form-label">
                      <i className="bi bi-tags me-1" style={{ color: '#ffc107' }}></i>
                      Tarifa <span className="text-danger">*</span>
                    </label>
                      <select
                      className="form-select"
                      value={formData.tarifaId || ''}
                      onChange={(e) => setFormData({...formData, tarifaId: e.target.value})}
                      disabled={editingPedido?.id} 
                      required
                    >
                      <option value="" disabled hidden>Seleccionar tarifa</option>
                      {tarifas.map(tarifa => (
                        <option key={tarifa.id} value={tarifa.id}>
                          {tarifa.nombre} - ${tarifa.valorFijo} - {tarifa.descripcion}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="row mt-4">
                  <div className="col-12">
                    <h6 className="border-bottom pb-2 mb-3">
                      <i className="bi bi-box me-2"></i>Información del paquete
                    </h6>
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-box-seam me-1" style={{ color: '#6f42c1' }}></i>
                      Tipo de paquete <span className="text-danger">*</span>
                    </label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.tipoPaquete || ''}
                      onChange={(e) => setFormData({...formData, tipoPaquete: e.target.value})}
                      placeholder="Ej: Comida preparada, Documentos, etc."
                      required
                    />
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-speedometer me-1" style={{ color: '#007bff' }}></i>
                      Peso (kg)
                    </label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-control"
                      value={formData.pesoKg || ''}
                      onChange={(e) => setFormData({...formData, pesoKg: e.target.value})}
                      placeholder="2.50"
                    />
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-currency-dollar me-1" style={{ color: '#28a745' }}></i>
                      Valor declarado
                    </label>
                    <input
                      type="number"
                      className="form-control"
                      value={formData.valorDeclarado || ''}
                      onChange={(e) => setFormData({...formData, valorDeclarado: e.target.value})}
                      placeholder="45000"
                      min = "0"
                    />
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-cart me-1" style={{ color: '#17a2b8' }}></i>
                      Costo de compra
                    </label>
                    <input
                      type="number"
                      className="form-control"
                      value={formData.costoCompra || ''}
                      onChange={(e) => setFormData({...formData, costoCompra: e.target.value})}
                      placeholder="0"
                      min = "0"
                    />
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-calculator me-1" style={{ color: '#6c757d' }}></i>
                      Subtotal
                    </label>
                    <input
                      type="number"
                      className="form-control"
                      value={formData.subtotal || ''}
                      readOnly
                      placeholder="Calculado automáticamente"
                      style={{ backgroundColor: '#f8f9fa' }}
                    />
                    <div className="form-text">
                      <small>Calculado automáticamente: Tarifa + Costo de compra</small>
                    </div>
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-cash-stack me-1" style={{ color: '#198754' }}></i>
                      Total
                    </label>
                    <input
                      type="number"
                      className="form-control"
                      value={formData.total || ''}
                      readOnly
                      placeholder="Calculado automáticamente"
                      style={{ backgroundColor: '#f8f9fa' }}
                    />
                    <div className="form-text">
                      <small>Calculado automáticamente: Subtotal - Descuento del cliente</small>
                    </div>
                  </div>

                  {formData.estadoId === '4' && (
                    <div className="col-md-6 mb-3">
                      <label className="form-label">
                        <i className="bi bi-clock me-1" style={{ color: '#fd7e14' }}></i>
                        Tiempo de entrega (minutos)
                      </label>
                      <input
                        type="number"
                        className="form-control"
                        value={formData.tiempoEntregaMinutos || ''}
                        onChange={(e) => setFormData({...formData, tiempoEntregaMinutos: e.target.value})}
                        placeholder="45"
                        min="1"
                      />
                      <div className="form-text">
                        <small>Solo visible cuando el pedido está entregado</small>
                      </div>
                    </div>
                  )}

                  <div className="col-md-6 mb-3">
                    <label className="form-label">
                      <i className="bi bi-calendar-event me-1" style={{ color: '#e83e8c' }}></i>
                      Fecha de entrega
                    </label>
                    <div 
                    className="form-control" 
                    style={{ backgroundColor: '#f8f9fa', cursor: 'not-allowed' }}
                  >
                    {formatearFecha(formData.fechaEntrega)}
                    </div>
                    <div className="form-text">
                      La fecha se establece automáticamente con la fecha actual
                    </div>
                  </div>

                  <div className="col-12 mb-3">
                    <label className="form-label">
                      <i className="bi bi-chat-square-text me-1" style={{ color: '#6f42c1' }}></i>
                      Notas
                    </label>
                    <textarea
                      className="form-control"
                      rows="3"
                      value={formData.notas || ''}
                      onChange={(e) => setFormData({...formData, notas: e.target.value})}
                      placeholder="Instrucciones especiales, observaciones, etc."
                    />
                  </div>
                </div>

                <div className="d-flex justify-content-end gap-2">
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={loading}
                  >
                    {loading ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2"></span>
                        Guardando
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
                      setEditingPedido(null);
                      setFormData({});
                      setClienteSeleccionado(null);
                      setClienteBusqueda('');
                      setDropdownClienteVisible(false);
                      setTipoServicioBusqueda('');
                      setDropdownTipoServicioVisible(false);
                      setMensajeroBusqueda('');
                      setDropdownMensajeroVisible(false);
                    }}
                  >
                    <i className="bi bi-x-circle me-1"></i>Cancelar
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {formVisible && <PedidosRecientesComponent />}

        {pedidosFiltrados.length === 0 ? (
          <div className="text-center py-5">
            <i className="bi bi-inbox" style={{ fontSize: '4rem', color: '#dee2e6' }}></i>
            <h4 className="text-muted mt-3">
              {pedidos.length === 0 ? 'No hay pedidos registrados' : 'No se encontraron resultados'}
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