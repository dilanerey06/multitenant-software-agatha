# # Dockerfile para TenantApplication
# # Ubicación: ./proyectos/tenant/Dockerfile

# FROM openjdk:21-jdk-slim

# # Crear directorio de trabajo
# WORKDIR /app

# # Copiar archivos de Maven
# COPY pom.xml .
# COPY mvnw .
# COPY .mvn .mvn

# # Dar permisos de ejecución a mvnw
# RUN chmod +x ./mvnw

# # Descargar dependencias
# RUN ./mvnw dependency:go-offline -B

# # Copiar código fuente
# COPY src ./src

# # Compilar la aplicación
# RUN ./mvnw clean package -DskipTests

# # Exponer puerto
# EXPOSE 8080

# # Comando para ejecutar la aplicación
# CMD ["java", "-jar", "target/*.jar"]

# --- Fase de construcción (builder) ---
  FROM openjdk:21-jdk-slim AS builder

  WORKDIR /app

  # Copiar solo los archivos necesarios para descargar dependencias
  COPY pom.xml .
  COPY mvnw .
  COPY .mvn .mvn

  # Dar permisos y descargar dependencias (cache)
  RUN chmod +x mvnw && \
      ./mvnw dependency:go-offline -B

  # Copiar el código fuente
  COPY src ./src

  # Compilar y empaquetar la aplicación (omitir tests)
  RUN ./mvnw clean package -DskipTests

  # --- Fase final (imagen liviana) ---
  FROM openjdk:21-jdk-slim

  WORKDIR /app

  # Copiar SOLO el .jar desde la fase builder
  COPY --from=builder /app/target/*.jar app.jar

  # Exponer puerto (documentativo)
  EXPOSE 8080

  # Comando de arranque
  ENTRYPOINT ["java", "-jar", "app.jar"]