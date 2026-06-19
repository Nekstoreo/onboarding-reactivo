# Onboarding Reactivo - Microservicio de Usuarios

Este proyecto consiste en un microservicio reactivo desarrollado bajo los principios de Clean Architecture y Domain-Driven Design (DDD). Su función principal es la gestión de usuarios, integrando llamadas a APIs externas, mecanismos de caché en memoria y persistencia de eventos asíncronos en colas de mensajería y bases de datos NoSQL.

## Arquitectura y Capas del Proyecto

El proyecto se estructura en los siguientes módulos gradle:

1. **domain/model**: Capa de negocio más interna que contiene los modelos de datos y la definición de los puertos (interfaces Gateway).
2. **domain/usecase**: Orquesta la lógica del negocio implementando los casos de uso específicos. No depende de ninguna biblioteca externa ni tecnología de persistencia.
3. **infrastructure/driven-adapters**: Adaptadores de salida que comunican la aplicación con bases de datos, APIs externas o mensajería (PostgreSQL/R2DBC, Redis, DynamoDB, SQS, WebClient).
4. **infrastructure/entry-points**: Adaptadores de entrada que reciben peticiones externas (API REST WebFlux y SQS Listener).
5. **applications/app-service**: Módulo principal que configura las dependencias de Spring Boot, expone las configuraciones generales e inicia la aplicación.

## Stack Tecnológico

- **Java**: Versión 25
- **Framework Principal**: Spring Boot 4.0
- **Paradigma**: Programación Reactiva (Spring WebFlux, Project Reactor)
- **Persistencia SQL**: PostgreSQL con R2DBC (Acceso reactivo)
- **Caché**: Redis (Estrategia Cache-Aside reactiva)
- **Mensajería**: AWS SQS (LocalStack)
- **Persistencia NoSQL**: DynamoDB (LocalStack)
- **Gestor de Dependencias**: Gradle

## Requisitos Previos

- Java Development Kit (JDK) 25
- Docker y Docker Compose
- Herramienta cliente para APIs (Postman, curl o similar)

## Configuración del Entorno Local

El entorno local se gestiona a través de contenedores Docker pre-configurados.

1. **Iniciar contenedores**:
   Desde la raíz del proyecto, (opcionalmente exportando tu token de LocalStack para habilitar funciones en la nube) ejecute:
   ```bash
   export LOCALSTACK_AUTH_TOKEN="tu-token-aqui" # Opcional
   docker compose up -d
   ```
   Esto iniciará PostgreSQL, Redis y LocalStack.

2. **Inicialización de AWS LocalStack**:
   Al iniciar el contenedor de LocalStack, se ejecutan de manera automática los scripts ubicados en `infra-init/aws` para crear la cola SQS y la tabla DynamoDB necesarias. En caso de requerir la creación manual de estos recursos dentro del contenedor, ejecute:
   ```bash
   docker exec onboarding-localstack awslocal sqs create-queue --queue-name user-created-events
   docker exec onboarding-localstack awslocal dynamodb create-table --table-name users-uppercase --key-schema AttributeName=id,KeyType=HASH --attribute-definitions AttributeName=id,AttributeType=S --billing-mode PAY_PER_REQUEST
   ```

## Ejecución del Microservicio

Para ejecutar la aplicación localmente vinculando la infraestructura de contenedores, active el perfil `local` mediante el siguiente comando Gradle:

```bash
./gradlew :app-service:bootRun --args='--spring.profiles.active=local'
```

La aplicación se levantará e iniciará el servidor Netty escuchando en el puerto `8080`.

## Pruebas y Cobertura de Código

### Ejecución de Pruebas Unitarias y de Integración:
```bash
./gradlew test
```

### Análisis de Calidad en SonarQube:
Para reportar la cobertura y ejecutar el análisis estático en la instancia de SonarQube local:
1. Asegúrese de haber compilado previamente el proyecto:
   ```bash
   ./gradlew compileJava classes
   ```
2. Ejecute el scanner de Sonar con el token provisto:
   ```bash
   ./gradlew sonar \
     -Dsonar.projectKey=Onboarding \
     -Dsonar.projectName='Onboarding' \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.token=sqp_675b0bbe25aa684833c086646de8195553d685fc
     ```

## Acceso a Bases de Datos locales

Para auditar e inspeccionar los datos almacenados durante la ejecución local, conéctate a los motores usando los siguientes parámetros:

### 1. PostgreSQL (Base de datos relacional)
* **Host**: `localhost`
* **Puerto**: `5432`
* **Base de Datos**: `usersdb`
* **Esquema**: `public` (tabla `users` creada automáticamente)
* **Usuario**: `postgres`
* **Contraseña**: `postgres`
* *Herramienta recomendada: DBeaver, pgAdmin, IntelliJ Database tool, o CLI: `psql -h localhost -U postgres -d usersdb`*

### 2. Redis (Caché en memoria)
* **Host**: `localhost`
* **Puerto**: `6379`
* *Herramienta recomendada: Redis Insight, Another Redis Desktop Manager, o CLI: `redis-cli -h localhost -p 6379`*

### 3. DynamoDB (Base de datos NoSQL)
* **Endpoint local**: `http://localhost:4566`
* **Región**: `us-east-1`
* **Tabla**: `users-uppercase` (creada automáticamente en el arranque)
* *Herramienta recomendada: AWS NoSQL Workbench (apuntando al endpoint local `http://localhost:4566`) o AWS CLI:*
  ```bash
  aws --profile localstack dynamodb scan --table-name users-uppercase
  ```

---

## Interacción con LocalStack (AWS Local)

Para pruebas, monitoreo y depuración manual de los recursos de AWS simulados localmente (SQS y DynamoDB), puedes interactuar a través de la terminal o de forma gráfica.

### 1. Configuración de la AWS CLI para LocalStack
Para poder usar la interfaz de línea de comandos de AWS (`aws cli`) apuntando al entorno local, se recomienda configurar manualmente los archivos de configuración en tu máquina:

1. Agrega las credenciales simuladas (*dummy*) en tu archivo `~/.aws/credentials`:
   ```ini
   [localstack]
   aws_access_key_id = test
   aws_secret_access_key = test
   ```

2. Agrega la configuración del perfil y el endpoint en tu archivo `~/.aws/config`:
   ```ini
   [profile localstack]
   region = us-east-1
   output = json
   endpoint_url = http://localhost.localstack.cloud:4566
   ```
   
### 2. Comandos útiles de AWS CLI (Perfil LocalStack)
Una vez configurado el perfil, puedes interactuar directamente con los servicios locales:

* **Listar colas SQS existentes**:
  ```bash
  aws --profile localstack sqs list-queues
  ```
* **Recibir/Visualizar mensajes de la cola (SQS)**:
  ```bash
  aws --profile localstack sqs receive-message --queue-url "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/user-created-events" --max-number-of-messages 10
  ```

---

### 3. Visualizar recursos en LocalStack Cloud (Consola Web)
LocalStack ofrece una consola web gratuita para gestionar gráficamente tu motor local:

1. Abre tu navegador e ingresa a: **[https://app.localstack.cloud](https://app.localstack.cloud)**.
2. La aplicación detectará automáticamente tu motor local y verás en la esquina inferior izquierda el indicador `LocalStack Status: Running`.
3. Navega por el menú lateral (ej: **SQS** o **DynamoDB**) para visualizar colas, tablas, mensajes e interactuar de manera gráfica.
