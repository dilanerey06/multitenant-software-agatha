# 🚚 Sistema Multitenant de Mensajería Express

## 📌 Descripción

Sistema tecnológico integral basado en arquitectura multitenant diseñado específicamente para satisfacer las necesidades operativas de pequeñas y medianas empresas de mensajería express. La plataforma permite que múltiples empresas compartan la misma infraestructura tecnológica manteniendo sus datos y procesos completamente aislados.

## ✨ Características Principales

### 🎯 Funcionalidades del Sistema
```bash
# Arqueos de caja con reportes detallados de ingresos y egresos.
# Dashboard general con estadísticas comprehensivas y métricas de rendimiento.
# Gestión completa de clientes con funcionalidades de descuentos.
# Administración de mensajeros con control de vehículos y disponibilidad.
# Gestión integral de pedidos con historial de cambios y visualización de rutas.
# Sistema de tarifas configurable con opciones de activación/desactivación.
# Administración de usuarios con asignación de roles específicos.
# Centro de notificaciones contextualizado por rol.
# Gestión de perfiles personalizados.
```

### 🏗️ Arquitectura
```bash
# Arquitectura de microservicios con servicios independientes.
# Patrón multitenant con aislamiento completo de datos por empresa.
# Proxy middleware para gestión centralizada de peticiones.
# Interfaces frontend especializadas por tipo de usuario.
# Containerización completa con Docker.
```

## 🚀 Stack Tecnológico

### Backend
```bash
# Spring Boot 3.5.3 con Java 17.
# Spring Data JPA para persistencia.
# Spring Security con autenticación JWT.
# SpringDoc OpenAPI para documentación automática.
# iText PDF y Apache POI para generación de reportes.
```

### Frontend
```bash
# React 19.1.0 con Vite 6.3.5.
# Bootstrap 5.3.6 para interfaz de usuario.
# Axios 1.9.0 para comunicación HTTP.
# React Router DOM 7.6.0 para navegación.
# Recharts 3.0.2 para visualización de datos.
# Leaflet 1.9.4 para mapas interactivos.
```

### Base de Datos
```bash
# MySQL 8.0.41.
# Patrón de base de datos compartida con esquema discriminador.
```

### DevOps
```bash
# Docker & Docker Compose para containerización.
# Node.js Express para proxy middleware.
# GitHub para control de versiones.
```

## 🔧 Requisitos del Sistema

```bash
# Docker 20.10 o superior.
# Docker Compose 3.8 o superior.
# Git.
# 8GB RAM mínimo recomendado.
# Puertos disponibles: 3000, 3001, 3307, 3308, 8080, 8081, 8082.
```

## ⚙️ Instalación y Configuración

### 1️⃣ Clonar el Repositorio
```bash
git clone https://github.com/dilanerey06/multitenant-software-agatha.git
cd multitenant-software-agatha
```

### 2️⃣ Configurar Variables de Entorno
```bash
# Crear archivo .env en la raíz del proyecto
MYSQL_ROOT_PASSWORD=tu_password_root
MYSQL_USER=tu_usuario
MYSQL_PASSWORD=tu_password
JWT_SECRET=tu_jwt_secret_key
JWT_EXPIRATION_MS=86400000
```

### 3️⃣ Verificar Requisitos del Sistema
```bash
# Verificar Docker
docker --version

# Verificar Docker Compose
docker-compose --version

# Verificar puertos disponibles
netstat -tuln | grep -E ':(3000|3001|3307|3308|8080|8081|8082)'

# Verificar RAM disponible
free -h
```

### 4️⃣ Construir e Iniciar los Servicios
```bash
# Construir e iniciar todos los contenedores
docker-compose up -d --build

```

### 5️⃣ Verificar la Instalación
```bash
# Verificar servicios frontend
curl http://localhost:3000  # Frontend Mensajería
curl http://localhost:3001  # Frontend Tenants

# Verificar APIs
curl http://localhost:8080/actuator/health  # API Tenant
curl http://localhost:8081/actuator/health  # API Courier
curl http://localhost:8082/health          # Middleware Service

# Verificar bases de datos
docker exec -it mysql-tenant mysql -u root -p -e "SHOW DATABASES;"
docker exec -it mysql-mensajeria mysql -u root -p -e "SHOW DATABASES;"
```

## 🗄️ Estructura del Proyecto

```bash
multitenant-software-agatha/
├── servicios/
│   ├── tenant/                 # Servicio de gestión de tenants
│   ├── mensajeriaa/           # Servicio de mensajería
│   └── middleware-service/     # Proxy middleware
├── mensajeriaFrontend/        # Frontend para operaciones de mensajería
├── tenantsFrontend/           # Frontend para administración de tenants
├── sql/
│   ├── schemaTenant.sql       # Esquema de base de datos tenant
│   └── schemaMensajeria.sql   # Esquema de base de datos mensajería
├── docker-compose.yml         # Configuración de contenedores
├── .env                       # Variables de entorno
└── README.md
```

## 🔌 Endpoints

### Arquitectura de Servicios
```bash
# Flujo de comunicación:
# Frontend (3000,3001) -> Middleware (8082) -> APIs (8080,8081) -> MySQL (3307,3308)

┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Frontend      │
│   Mensajería    │    │   Tenants       │
│   (Port 3000)   │    │   (Port 3001)   │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          └──────────┬───────────┘
                     │
        ┌────────────▼────────────┐
        │   Middleware Service    │
        │     (Port 8082)         │
        └────────────┬────────────┘
                     │
        ┌────────────▼────────────┐
        │                         │
   ┌────▼────┐            ┌──────▼──────┐
   │ Tenant  │            │   Courier   │
   │ Service │            │   Service   │
   │(Port    │            │ (Port 8081) │
   │ 8080)   │            │             │
   └────┬────┘            └──────┬──────┘
        │                        │
   ┌────▼────┐            ┌──────▼──────┐
   │ MySQL   │            │   MySQL     │
   │ Tenant  │            │ Mensajería  │
   │(Port    │            │ (Port 3308) │
   │ 3307)   │            │             │
   └─────────┘            └─────────────┘
```

## 🔒 Seguridad

```bash
# Características de seguridad implementadas:
# - Autenticación JWT para todos los servicios.
# - Aislamiento de datos por tenant.
# - Validación de entrada en todas las APIs.
# - HTTPS ready para producción.
# - Control de acceso basado en roles.
```

## 📊 Monitoreo y Logs

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f tenant-app
docker-compose logs -f courier-app
docker-compose logs -f middleware-service

# Verificar salud de los servicios
docker-compose ps

# Ver uso de recursos
docker stats

# Monitorear base de datos
docker exec -it mysql-tenant mysqladmin processlist -u root -p
docker exec -it mysql-mensajeria mysqladmin processlist -u root -p
```

## 🚦 Comandos de Gestión

```bash
# Parar todos los servicios
docker-compose down

# Parar y eliminar volúmenes (elimina datos)
docker-compose down -v

# Reiniciar un servicio específico
docker-compose restart tenant-app
docker-compose restart courier-app
docker-compose restart middleware-service

# Reconstruir un servicio
docker-compose up -d --build tenant-app

# Actualizar un servicio
docker-compose pull tenant-app
docker-compose up -d tenant-app

# Backup de base de datos
docker exec mysql-tenant mysqldump -u root -p --all-databases > backup-tenant.sql
docker exec mysql-mensajeria mysqldump -u root -p --all-databases > backup-mensajeria.sql

# Restaurar backup
docker exec -i mysql-tenant mysql -u root -p < backup-tenant.sql
docker exec -i mysql-mensajeria mysql -u root -p < backup-mensajeria.sql
```

## 🛠️ Desarrollo Local

Puedes ejecutar el proyecto de dos formas:

### Opción 1: Desarrollo Manual 
```bash
# Solo ejecutar bases de datos para desarrollo local
docker-compose up -d mysql-tenant mysql-mensajeria

# Configurar backend (en otra terminal)
cd servicios/tenant
./mvnw spring-boot:run

cd servicios/mensajeriaa
./mvnw spring-boot:run

# Configurar frontend (en otra terminal)
cd mensajeriaFrontend
npm install && npm run dev

cd tenantsFrontend
npm install && npm run dev
```

### Opción 2: Con Docker Compose completo
```bash
# Limpiar cache y reconstruir todo
docker-compose build --no-cache

# Iniciar todos los servicios
docker-compose up
```

## 📄 Información del Proyecto

```bash
# Licencia: Uso académico
# Repositorio: https://github.com/dilanerey06/multitenant-software-agatha

# Autores:
# - Dilan Esteban Rey Sepúlveda (2190397)
# - Silvia Alejandra Cárdenas Santos (2210102)

# Tecnologías principales:
# Backend: Spring Boot 3.5.3, Java 17, MySQL 8.0.41
# Frontend: React 19.1.0, Vite 6.3.5, Bootstrap 5.3.6
# DevOps: Docker, Docker Compose, Node.js Express
```
---