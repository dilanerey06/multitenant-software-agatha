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
  EXPOSE 8081

  # Comando de arranque
  ENTRYPOINT ["java", "-jar", "app.jar"]