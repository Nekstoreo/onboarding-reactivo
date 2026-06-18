# Guía Rápida de Revisión (Cheat Sheet) - Taller Onboarding Reactivo

Esta guía sirve como material de consulta rápida para sustentar las decisiones de diseño y la ubicación de las implementaciones durante la revisión del taller.

---

## 1. Resiliencia & Circuit Breaker (Resilience4j)

* **¿Dónde se aplica?**
  Se aplica sobre la consulta del API externa en [RestConsumer.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/bancolombia/onboarding/consumer/RestConsumer.java#L19-L45) mediante la anotación `@CircuitBreaker(name = "reqresCircuitBreaker")`.
* **¿Dónde se configura?**
  En el archivo general de propiedades: [application.yaml](file:///home/nekstoreo/Workspace/onboarding-reactivo/applications/app-service/src/main/resources/application.yaml#L50-L58) bajo la propiedad `resilience4j.circuitbreaker.instances.reqresCircuitBreaker`.
* **Concepto rápido:**
  El Circuit Breaker actúa como un fusible eléctrico. Si la API de ReqRes empieza a fallar constantemente (más del 50% de error configurado), el circuito pasa a estado **OPEN** (abierto). En este estado, rechaza llamadas inmediatas y devuelve un comportamiento de contingencia (fallback) sin sobrecargar el servicio ni bloquear hilos valiosos del Event Loop de Netty.

---

## 2. Trazabilidad, Excepciones & Manejo de Errores

* **Excepción de Negocio (Dominio):**
  Definida en [UserNotFoundException.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/domain/model/src/main/java/co/com/bancolombia/onboarding/model/user/UserNotFoundException.java#L1-L10). Al estar en el dominio puro, es una excepción de Java estándar desacoplada de Spring Boot.
* **Manejador Global de Excepciones (Entry Point):**
  Implementado en [GlobalExceptionHandler.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/onboarding/api/config/GlobalExceptionHandler.java#L19-L38). Intercepta reactivamente los errores en la capa de transporte HTTP WebFlux, asignando un `404 Not Found` ante un `UserNotFoundException` o un `500 Internal Server Error` ante cualquier fallo genérico.
* **Trazabilidad:**
  Se captura el identificador único de petición `requestId` directamente desde el request en curso (`exchange.getRequest().getId()`) y se añade tanto al JSON de respuesta de error como al log de advertencia/error de Log4j2, permitiendo la correlación exacta entre clientes y logs del servidor.

---

## 3. Programación Reactiva: `map` vs `flatMap` & `.block()`

* **¿Cuándo usar `map`?**
  Para transformaciones de datos **sincrónicas** elementales. Transforma un tipo de dato `T` en `U` de inmediato dentro de la misma tubería de ejecución (sin retornar otro Publisher/Mono/Flux).
  * *Ejemplo:* En [ProcessUserEventUseCase.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/domain/usecase/src/main/java/co/com/bancolombia/onboarding/usecase/ProcessUserEventUseCase.java#L12-L24) para pasar a mayúsculas los textos del usuario (`toBuilder().firstName(...).build()`).
* **¿Cuándo usar `flatMap`?**
  Para transformaciones **asincrónicas** donde la función de transformación devuelve a su vez un publicador reactivo (`Mono<V>` o `Flux<V>`). Evita el anidamiento (`Mono<Mono<V>>`) "aplanándolo" en un único `Mono<V>`, permitiendo que la segunda operación se ejecute sin bloquear el hilo.
  * *Ejemplo:* En [CreateUserUseCase.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/domain/usecase/src/main/java/co/com/bancolombia/onboarding/usecase/CreateUserUseCase.java#L16-L23) para encadenar secuencialmente llamadas externas que retornan reactividad (`fetchUser` $\rightarrow$ `save` $\rightarrow$ `publishUserCreated`).
* **¿Por qué está prohibido el uso de `.block()` y `.subscribe()` en el UseCase?**
  - **`.block()`** detiene el hilo actual de ejecución del procesador hasta que el resultado esté disponible, lo que sabotea la escalabilidad reactiva (Event Loop) y puede provocar un congelamiento total (deadlock).
  - **`.subscribe()`** dispara la ejecución del stream de manera aislada antes de que llegue a la capa de salida HTTP (WebFlux), rompiendo el flujo reactivo continuo y perdiendo el control del ciclo de vida de la petición.

---

## 4. Estrategia de Caché (Cache-Aside)

* **¿Cómo opera?**
  Implementado en [GetUserByIdUseCase.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/domain/usecase/src/main/java/co/com/bancolombia/onboarding/usecase/GetUserByIdUseCase.java#L15-L22).
  1. Busca al usuario en Redis (`userCacheGateway.findById`).
  2. Si es *Cache Hit*, lo retorna inmediatamente.
  3. Si es *Cache Miss* (`switchIfEmpty`), va a PostgreSQL (`userGateway.findById`), y si existe, lo almacena en Redis (`userCacheGateway.save`) antes de retornarlo.
  4. Si no existe en ningún lado, propaga una excepción.
* **Componente de Caché:**
  Utiliza [ReactiveRedisTemplateAdapter.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/onboarding/redis/template/ReactiveRedisTemplateAdapter.java#L12-L23) usando el cliente asíncrono no bloqueante de Redis.

---

## 5. Procesamiento Asíncrono (SQS $\rightarrow$ DynamoDB)

* **Emisión (Sender):**
  En [SQSSender.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/infrastructure/driven-adapters/sqs-sender/src/main/java/co/com/bancolombia/onboarding/sqs/sender/SQSSender.java#L23-L29) se inyecta el `SqsAsyncClient` para enviar un mensaje no bloqueante a la cola SQS configurada.
* **Escucha (Listener):**
  El módulo [SQSListener.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/infrastructure/entry-points/sqs-listener/src/main/java/co/com/bancolombia/onboarding/sqs/listener/helper/SQSListener.java#L24-L32) realiza un sondeo paralelo no bloqueante sobre SQS. 
* **Procesamiento:**
  [SQSProcessor.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/infrastructure/entry-points/sqs-listener/src/main/java/co/com/bancolombia/onboarding/sqs/listener/SQSProcessor.java#L22-L36) recibe el mensaje, deserializa el JSON, llama a [ProcessUserEventUseCase.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/domain/usecase/src/main/java/co/com/bancolombia/onboarding/usecase/ProcessUserEventUseCase.java) para transformarlo a mayúsculas, y lo escribe en DynamoDB mediante [UserDynamoDBAdapter.java](file:///home/nekstoreo/Workspace/onboarding-reactivo/infrastructure/driven-adapters/dynamo-db/src/main/java/co/com/bancolombia/onboarding/dynamodb/UserDynamoDBAdapter.java#L11-L17).
* **Confirmación:**
  Si todo el procesamiento es exitoso, la cola elimina el mensaje (`SqsAsyncClient.deleteMessage`). Si hay un error, el mensaje permanece en la cola para reintentarse.
