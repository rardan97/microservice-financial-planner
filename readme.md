# Microservices: Simple Finance Planner

microservice-finance-planner adalah backend microservice untuk mengelola perencanaan keuangan pribadi secara sederhana dan efektif, khususnya untuk pekerja. Sistem ini memungkinkan pengguna mencatat transaksi pemasukan dan pengeluaran, menyusun rencana keuangan bulanan, menetapkan target tabungan, serta melakukan evaluasi otomatis setelah periode berjalan.


## Technologies
- Spring Boot
- Spring Security 
- Spring Cloud Gateway
- JWT Authentication
- resilience4j
- PostgreSQL
- Eureka Server
- Docker
- Swagger



## Architecture Diagram
<p align="center">
  <img src="flow.png" alt="Architecture Diagram" width="700"/>
</p>





## Microservices Overview
#### api-gateway : 
- Sebagai pintu gerbang utama untuk routing request ke service-service backend, mengelola otorisasi, load balancing, dan monitoring.

#### eureka-server : 
- Bertugas sebagai service discovery, memungkinkan setiap service untuk menemukan dan berkomunikasi dengan service lainnya secara dinamis.

#### auth-service : 
- Mengelola autentikasi dan otorisasi pengguna, termasuk registrasi, login, logout, dan manajemen token JWT.

#### financial-plan-service : 
- Menangani pembuatan, pengelolaan, dan penghapusan rencana keuangan bulanan pengguna.

#### transaction-service : 
- Mengelola pencatatan dan pengelolaan transaksi pemasukan dan pengeluaran

#### financial-saving-targets-service : 
- Bertanggung jawab untuk menetapkan dan mengelola target tabungan spesifik pada tiap rencana keuangan.

#### financial-evaluation-service : 
- Melakukan evaluasi otomatis terhadap capaian keuangan setelah periode rencana berjalan, menghitung selisih pemasukan-pengeluaran serta pencapaian target tabungan.


## Komunikasi Antar Service

### financial-plan-service - GET /getFinancialPlanById/{planId}

Mengambil detail financial plan berdasarkan planId dengan memanggil service downstream secara sinkron:

- transaction-service
- financial-saving-targets-service

Semua komunikasi via API Gateway dengan service discovery dari Eureka Server dan fault tolerance dari Resilience4j (circuit breaker, retry).


### financial-plan-service - DELETE /deleteFinancialPlan/{planId}

Menghapus financial plan dan data terkait di service lain secara berurutan:

- Memanggil endpoint delete di transaction-service dan financial-saving-targets-service.
- Jika ada kegagalan, dilakukan rollback menggunakan endpoint rollback di service terkait.

Semua komunikasi via API Gateway, Eureka Server, dan Resilience4j.


### financial-evaluation-service

Melakukan evaluasi finansial dengan komunikasi ke:

- financial-plan-service
- transaction-service
- financial-saving-targets-service

Memakai API Gateway, Eureka Server, dan Resilience4j untuk kestabilan.

## Mekanisme Teknis Utama

- API Gateway: Routing, load balancing, otorisasi, monitoring request antar service.
- Eureka Server: Service discovery untuk menemukan alamat service secara dinamis.
- Resilience4j: Fault tolerance (circuit breaker, retry, fallback, bulkhead) agar komunikasi antar service stabil dan aman dari kegagalan cascading.
- Rollback: Untuk menjaga konsistensi data jika proses hapus di service downstream gagal.


## Database
Setiap service menggunakan database PostgreSQL terpisah:
- authdb
- financialplandb
- financialsavingtargetsdb
- transactiondb
- financialevaluationsdb


## Configuration

### api-gateway (application.yml)
```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: auth-service
              uri: lb://auth-service
              predicates:
                - Path=/api/auth/**
              filters:
                - RewritePath=/api/auth/(?<segment>.*), /api/auth/${segment}

            - id: financial-plan-service
              uri: lb://financial-plan-service
              predicates:
                - Path=/api/financial-plan/**
              filters:
                - RewritePath=/api/financial-plan/(?<segment>.*), /api/financial-plan/${segment}

            - id: financial-saving-targets-service
              uri: lb://financial-saving-targets-service
              predicates:
                - Path=/api/financial-saving-targets/**
              filters:
                - RewritePath=/api/financial-saving-targets/(?<segment>.*), /api/financial-saving-targets/${segment}

            - id: transaction-service
              uri: lb://transaction-service
              predicates:
                - Path=/api/transaction/**
                - Path=/api/category/**
              filters:
                - RewritePath=/api/transaction/(?<segment>.*), /api/transaction/${segment}
                - RewritePath=/api/category/(?<segment>.*), /api/category/${segment}

            - id: financial-evaluations-service
              uri: lb://financial-evaluations-service
              predicates:
                - Path=/api/financial-evaluations/**
              filters:
                - RewritePath=/api/financial-evaluations/(?<segment>.*), /api/financial-evaluations/${segment}

springdoc:
  swagger-ui:
    urls:
      - url: /api/auth/v3/api-docs
        name: Auth Service

      - url: /api/financial-plan/v3/api-docs
        name: Financial Plan Service

      - url: /api/financial-saving-targets/v3/api-docs
        name: Financial Saving Targets Service

      - url: /api/transaction/v3/api-docs
        name: transaction Service

      - url: /api/financial-evaluations/v3/api-docs
        name: financial evaluations Service


blackcode:
  app:
    jwtSecret: ${blackcode.app.jwtSecret}
    jwtExpirationMs: ${blackcode.app.jwtExpirationMs}
    jwtRefreshExpirationMs: ${blackcode.app.jwtRefreshExpirationMs}


logging:
  level:
    org.springframework.cloud.gateway: DEBUG

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:http://eurekaserver:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true

```

### eureka-server (application.yml)

```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

management:
  endpoints:
    web:
      exposure:
        include: health, info
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.netflix.discovery: DEBUG

eureka:
  client:
    register-with-eureka: false
    fetch-registry: true

```

### auth-service (application.yml)

```yaml
server:
  port: 8081

spring:
  application:
    name: auth-service
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

blackcode:
  app:
    jwtSecret: ${blackcode.app.jwtSecret}
    jwtExpirationMs: ${blackcode.app.jwtExpirationMs}
    jwtRefreshExpirationMs: ${blackcode.app.jwtRefreshExpirationMs}


springdoc:
  api-docs:
    path: /api/auth/v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:http://eurekaserver:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true
```


### financial-plan-service (application.yml)
```yaml 

server:
  port: 8082

internal:
  api:
    secret: secret-key-123

spring:
  application:
    name: financial-plan-service
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

resilience4j:
  circuitbreaker:
    configs:
      default:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        permittedNumberOfCallsInHalfOpenState: 3
    instances:
      financialSavingTargetService:
        baseConfig: default
      transactionalService:
        baseConfig: default


  retry:
    instances:
      financialSavingTargetService:
        maxRetryAttempts: 3
        waitDuration: 2s
        retryExceptions:
          - org.springframework.web.reactive.function.client.WebClientRequestException
          - java.util.concurrent.TimeoutException
      transactionalService:
        maxRetryAttempts: 3
        waitDuration: 2s
        retryExceptions:
          - org.springframework.web.reactive.function.client.WebClientRequestException
          - java.util.concurrent.TimeoutException

management:
  endpoints:
    web:
      exposure:
        include: health,info,circuitbreakerevents

springdoc:
  api-docs:
    path: /api/financial-plan/v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICE_DEFAULTZONE:http://eurekaserver:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true
```


### financial-saving-targets-service (application.yml)
```yaml 

server:
  port: 8083

internal:
  api:
    secret: secret-key-123

spring:
  application:
    name: financial-saving-targets-service
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

springdoc:
  api-docs:
    path: /api/financial-saving-targets/v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICE_DEFAULTZONE:http://eurekaserver:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true

```

### transaction-service (application.yml)
```yaml 
server:
  port: 8084

internal:
  api:
    secret: secret-key-123

spring:
  application:
    name: transaction-service
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

resilience4j:
  circuitbreaker:
    configs:
      default:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        permittedNumberOfCallsInHalfOpenState: 3
    instances:
      financialPlanService:
        baseConfig: default

  retry:
    instances:
      financialPlanService:
        maxRetryAttempts: 3
        waitDuration: 2s
        retryExceptions:
          - org.springframework.web.reactive.function.client.WebClientRequestException
          - java.util.concurrent.TimeoutException

management:
  endpoints:
    web:
      exposure:
        include: health,info,circuitbreakerevents

springdoc:
  api-docs:
    path: /api/transaction/v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICE_DEFAULTZONE:http://eurekaserver:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true
```


### financial-evaluations-service (application.yml)

```yaml 
server:
  port: 8085

internal:
  api:
    secret: secret-key-123

spring:
  application:
    name: financial-evaluations-service
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

resilience4j:
  circuitbreaker:
    configs:
      default:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        permittedNumberOfCallsInHalfOpenState: 3
    instances:
      financialPlanService:
        baseConfig: default
      financialSavingTargetService:
        baseConfig: default
      transactionalService:
        baseConfig: default

  retry:
    instances:
      financialPlanService:
        maxRetryAttempts: 3
        waitDuration: 2s
        retryExceptions:
          - org.springframework.web.reactive.function.client.WebClientRequestException
          - java.util.concurrent.TimeoutException
      financialSavingTargetService:
        maxRetryAttempts: 3
        waitDuration: 2s
        retryExceptions:
          - org.springframework.web.reactive.function.client.WebClientRequestException
          - java.util.concurrent.TimeoutException
      transactionalService:
        maxRetryAttempts: 3
        waitDuration: 2s
        retryExceptions:
          - org.springframework.web.reactive.function.client.WebClientRequestException
          - java.util.concurrent.TimeoutException

management:
  endpoints:
    web:
      exposure:
        include: health,info,circuitbreakerevents

springdoc:
  api-docs:
    path: /api/financial-evaluations-service/v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICE_DEFAULTZONE:http://eurekaserver:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true
```



## Docker & Deployment

### Run Project
```
docker-compose up --build -d
```

### Stop Project
```
docker-compose down
```

## API Documentation (Swagger UI)

Akses dokumentasi API melalui:
```
http://localhost:8080/swagger-ui/index.html
```





## API Endpoints

Semua request via API Gateway:
```
http://localhost:8080
```

### Endpoint: auth-service 

Endpoint: auth-service
Base URL: /api/auth/

Method  | Header                             | Endpoint         | Description
--------|----------------------------------|------------------|---------------------------------
POST    |                                  | /login           | User Login, mendapatkan JWT
POST    |                                  | /registration    | Registrasi user baru
POST    | Authorization: Bearer &lt;token&gt; | /refresh-token   | Refresh token JWT
POST    | Authorization: Bearer &lt;token&gt; | /logout          | Logout user berdasarkan header


### Endpoint: financial-plan-service
Endpoint: financial-plan-service
Base URL: /api/financial-plan/

Method  | Header                             | Endpoint                     | Description
--------|----------------------------------|------------------------------|-------------------------------------------
GET     | Authorization: Bearer &lt;token&gt; | /getFinancialPlanAll          | Ambil semua financial plan user
GET     | Authorization: Bearer &lt;token&gt; | /getFinancialPlanById/{planId}| Ambil financial plan berdasarkan ID
POST    | Authorization: Bearer &lt;token&gt; | /createFinancialPlan          | Buat financial plan baru
PUT     | Authorization: Bearer &lt;token&gt; | /updateFinancialPlan/{planId} | Update financial plan berdasarkan ID
DELETE  | Authorization: Bearer &lt;token&gt; | /deleteFinancialPlan/{planId} | Hapus financial plan berdasarkan ID



### Endpoint: financial-saving-targets-service
Endpoint: financial-saving-targets-service
Base URL: /api/financial-saving-targets/

Method  | Header                             | Endpoint                                     | Description
--------|----------------------------------|----------------------------------------------|-------------------------------------------
GET     | Authorization: Bearer &lt;token&gt; | /getFinancialSavingTargetByPlan/{planId}     | Ambil semua saving target berdasarkan planId
GET     | Authorization: Bearer &lt;token&gt; | /getFinancialSavingTargetsById/{planId}/{targetId} | Ambil saving target berdasarkan planId dan targetId
POST    | Authorization: Bearer &lt;token&gt; | /createSavingTarget/{planId}                  | Buat saving target baru di plan tertentu
PUT     | Authorization: Bearer &lt;token&gt; | /updateSavingTarget/{planId}/{targetId}      | Update saving target berdasarkan planId dan targetId
DELETE  | Authorization: Bearer &lt;token&gt; | /deleteSavingTargetByPlan/{planId}            | Hapus semua saving target di plan tertentu
DELETE  | Authorization: Bearer &lt;token&gt; | /deleteSavingTargetById/{planId}/{targetId}   | Hapus saving target berdasarkan planId dan targetId
POST    | Authorization: Bearer &lt;token&gt; | /rollbackSavingTarget                          | Rollback transaksi saving target



### Endpoint: transaction-service

Endpoint: transaction-service
Base URL: /api/transaction/

Method  | Header                             | Endpoint                                   | Description
--------|----------------------------------|--------------------------------------------|-------------------------------------------
GET     | Authorization: Bearer &lt;token&gt; | /getTransactionByPlan/{planId}              | Ambil semua transaksi berdasarkan planId
GET     | Authorization: Bearer &lt;token&gt; | /getTransactionAllById/{planId}/{transactionId} | Ambil transaksi berdasarkan planId dan transactionId
POST    | Authorization: Bearer &lt;token&gt; | /createTransaction/{planId}                  | Buat transaksi baru di plan tertentu
PUT     | Authorization: Bearer &lt;token&gt; | /updateTransaction/{planId}/{transactionId} | Update transaksi berdasarkan planId dan transactionId
DELETE  | Authorization: Bearer &lt;token&gt; | /deleteTransactionByPlan/{planId}            | Hapus semua transaksi di plan tertentu
DELETE  | Authorization: Bearer &lt;token&gt; | /deleteTransactionById/{planId}/{transactionId} | Hapus transaksi berdasarkan planId dan transactionId
POST    | Authorization: Bearer &lt;token&gt; | /rollbackTransactionByPlan                    | Rollback transaksi berdasarkan plan



### Endpoint: transaction-service - Category
Base URL: /api/transaction/category/

Method  | Header                              | Endpoint                      | Description
--------|--------------------------------------|-------------------------------|---------------------------------------
GET     | Authorization: Bearer &lt;token&gt;    | /getCategoryAll               | Mendapatkan semua kategori
GET     | Authorization: Bearer &lt;token&gt;   | /getCategoryById/{categoryId}| Mendapatkan kategori berdasarkan ID
POST    | Authorization: Bearer &lt;token&gt;    | /createCategory              | Membuat kategori baru
PUT     | Authorization: Bearer &lt;token&gt;    | /updateCategory/{categoryId} | Mengupdate kategori berdasarkan ID
DELETE  | Authorization: Bearer &lt;token&gt;    | /deleteCategory/{categoryId} | Menghapus kategori berdasarkan ID



### Endpoint: financial-evaluation-service
Endpoint: financial-evaluations-service
Base URL: /api/financial-evaluations/

Method  | Header                             | Endpoint                        | Description
--------|----------------------------------|--------------------------------|-----------------------------------------------
GET     | Authorization: Bearer &lt;token&gt;     | /getEvaluationsAll             | Mendapatkan semua financial evaluations
GET     | Authorization: Bearer &lt;token&gt;     | /getEvaluationsPlan/{planId}  | Mendapatkan financial evaluation berdasarkan plan ID
POST    | Authorization: Bearer &lt;token&gt;     | /createEvaluationsPlan/{planId}| Membuat financial evaluation baru berdasarkan plan ID