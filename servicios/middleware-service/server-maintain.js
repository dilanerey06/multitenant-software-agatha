const express = require('express');
const axios = require('axios');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 8082;

// URLs de los servicios
const TENANT_SERVICE_URL = process.env.TENANT_SERVICE_URL || 'http://tenant-app:8080';
const COURIER_SERVICE_URL = process.env.COURIER_SERVICE_URL || 'http://courier-app:8081';

// Middleware
app.use(cors());
app.use(express.json());

// Función para validar con tenant-app
async function validateWithTenant(additionalData, authToken) {
  const {tenantId} = additionalData;

  // console.log("Middleware starts validateWithTenant request to ----> " +`${TENANT_SERVICE_URL}/api/tenants/${tenantId}/info`);

  try {
    const response = await axios.get(`${TENANT_SERVICE_URL}/api/tenants/${tenantId}/info`, {
      headers: {
        'Authorization': `${authToken}`,
        'Content-Type': 'application/json'
      },
      timeout: 10000 // 10 segundos timeout
    });

    // condicional que valida si la informacion del tenant que se devuelve esta activo
    
    console.log('Respuesta validacion en server.js -> ',response.data.estado);
    if(response.data.estado == 'ACTIVO') {
      console.warn('SI ESTA ACTIVO EL TENANT')
      return true;  
    } else {
      return false;
    }

  } catch (error) {
    if (error.response) {
      // La petición fue hecha y el servidor respondió con un código de error
      console.error('Error desde el servicio tenant:', error.response.status);
    } else if (error.request) {
      // La petición fue hecha pero no se recibió respuesta
      console.error('No se recibio respuesta del servicio tenant');
    } else {
      // Error al configurar la petición
      console.error('Error al configurar la petición:', error.message);
    }
    return false;
  }
}

// Middleware para extraer datos adicionales de headers
function extractAdditionalData(req) {
  // Buscar headers que contengan datos adicionales
  if (req.headers['x-additional-data']) {
    try {
      return JSON.parse(req.headers['x-additional-data']);
    } catch (error) {
      console.error('Error parsing additional data:', error);
      return {};
    }
  }
}

// "x-additional-data": {
//   "mensajeriaId": 1, 
//   "tenantdId": 1,
//   "nombreUsuario": "adminTest",
//   "rol": "ADMIN_MENSAJERIA"}

// Proxy middleware para todas las rutas
app.all('*', async (req, res) => {
  try {
    // Extraer token de autorización
    const authToken = req.headers.authorization;
    // console.log("middleware request received headers: ", req.headers);
    
    // console.log(authToken)
    if (!authToken || !authToken.startsWith('Bearer ')) {
      return res.status(401).json({
        error: 'Token de autorización requerido',
        code: 'AUTH_TOKEN_MISSING'
      });
    }
    
    // Extraer datos adicionales
    const additionalData = extractAdditionalData(req);
    // console.log("middleware token received: ", authToken, "additionalData: ", additionalData, "additionalData type: ", typeof additionalData);
    
    // Validar con tenant-app si hay datos adicionales
    if (Object.keys(additionalData).length > 0) {
      const isValid = await validateWithTenant(additionalData, authToken);
      
      if (!isValid) {
        return res.status(403).json({
          error: 'Validación fallida con el servicio tenant',
          code: 'TENANT_VALIDATION_FAILED'
        });
      }
    }
    // console.log("se valido correctamente los datos adicionales en el tenant service");
    
    // return res.status(200).json({"message": "se valido correctamente los datos adicionales en el tenant service"});
    
    // Preparar headers para courier-app (remover headers adicionales)
    const courierHeaders = { ...req.headers };
    delete courierHeaders['x-additional-data'];
    // delete courierHeaders['x-id-random'];
    // delete courierHeaders['x-otro-campo'];
    delete courierHeaders['host'];
    
    // Construir URL para courier-app
    const courierUrl = `${COURIER_SERVICE_URL}${req.path}`;

    // return res.status(200).json({"message": "se valido correctamente los datos adicionales en el tenant service y se mapeo la nueva solicitud"});
    
    // Hacer petición a courier-app
    const courierResponse = await axios({
      method: req.method,
      url: courierUrl,
      headers: courierHeaders,
      data: req.body,
      body: req.body,
      params: req.query,
      timeout: 30000, // 30 segundos timeout
      validateStatus: function (status) {
        return status < 500; // Resolver solo si el status es menor a 500
      }
    });
    
    // Devolver respuesta de courier-app
    res.status(courierResponse.status).json(courierResponse.data);
    
  } catch (error) {
    console.error('Error en proxy middleware:', error.message);
    
    if (error.code === 'ECONNREFUSED') {
      return res.status(503).json({
        error: 'Servicio no disponible',
        code: 'SERVICE_UNAVAILABLE'
      });
    }
    
    if (error.code === 'ECONNABORTED') {
      return res.status(504).json({
        error: 'Timeout en la petición',
        code: 'REQUEST_TIMEOUT'
      });
    }
    
    res.status(500).json({
      error: 'Error interno del servidor',
      code: 'INTERNAL_SERVER_ERROR'
    });
  }
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'OK', service: 'middleware' });
});

app.listen(PORT, () => {
  console.log(`Middleware service running on port ${PORT}`);
  console.log(`Tenant service URL: ${TENANT_SERVICE_URL}`);
  console.log(`Courier service URL: ${COURIER_SERVICE_URL}`);
});