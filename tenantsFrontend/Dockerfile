FROM node:20.18.0-alpine AS builder

# Establecer directorio de trabajo
WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm install

# Copiar el resto del código
COPY . .

# Construir la aplicación para producción
RUN npm run build

# Etapa 2: Servir con nginx
FROM nginx:alpine

# Copiar los archivos construidos
# COPY --from=builder /app/build /usr/share/nginx/html
COPY --from=builder /app/dist /usr/share/nginx/html
# Copiar configuración de nginx con proxy
COPY nginx.conf /etc/nginx/nginx.conf

# Exponer puerto 80
EXPOSE 80

# Comando para ejecutar nginx
CMD ["nginx", "-g", "daemon off;"]