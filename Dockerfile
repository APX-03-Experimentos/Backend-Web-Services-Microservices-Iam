# Etapa 1: Construcción
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Configurar encoding UTF-8 explícitamente
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8
ENV JAVA_TOOL_OPTIONS -Dfile.encoding=UTF-8

# Copiar primero el POM para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Verificar el encoding del archivo problemático (para debug)
RUN echo "=== Verificando application.properties ===" && \
    file src/main/resources/application.properties && \
    ls -la src/main/resources/application.properties && \
    echo "=== Primeras líneas del archivo ===" && \
    head -5 src/main/resources/application.properties

# Construir la aplicación con encoding explícito
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Configurar encoding en la imagen final también
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8
ENV JAVA_TOOL_OPTIONS -Dfile.encoding=UTF-8

# Copiar el JAR de la aplicación
COPY --from=build /app/target/*.jar app.jar

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]