FROM node:18-alpine

WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm ci --only=production

# Copiar el código fuente
COPY . .

# Exponer el puerto
EXPOSE 8082

# Crear usuario no-root
RUN addgroup -g 1001 -S nodejs
RUN adduser -S nodejs -u 1001

# Cambiar ownership de los archivos
RUN chown -R nodejs:nodejs /app

# Cambiar a usuario no-root
USER nodejs

# Comando para iniciar la aplicación
CMD ["node", "server.js"]