# 🎯 SentimentAPI Backend

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-Wrapper-red?style=flat-square)

**API REST Gateway para Análisis de Sentimientos con Sistema de Autenticación**

Hackathon ONE - No Country

</div>

---

## 📖 Descripción

API REST desarrollada en **Spring Boot 4.0.1** que actúa como gateway para consumir un modelo de Machine Learning de análisis de sentimientos (Python/FastAPI). Incluye sistema completo de autenticación de usuarios con PostgreSQL, validación robusta, manejo centralizado de errores y procesamiento tanto individual como por lotes de textos en español.

**Stack Tecnológico:**
- ☕ Java 17
- 🍃 Spring Boot 4.0.1
- 🐘 PostgreSQL 15+
- 🔧 Maven Wrapper
- 🔄 WebFlux (WebClient para comunicación HTTP reactiva)
- 🗄️ Spring Data JPA + Hibernate
- 🔐 BCrypt (encriptación de contraseñas)
- ✅ Jakarta Validation API 3.0.2
- 🎯 Lombok (reducción de boilerplate)

---

## 📁 Estructura del Proyecto

```
sentimentapi/
├── .mvn/wrapper/                    # Maven Wrapper
├── src/main/java/com/project/sentimentapi/
│   ├── configuration/               # Configuración de WebClient y endpoints
│   │   ├── ConectarApi.java        # Cliente WebFlux configurado
│   │   └── EndPointConfg.java      # Propiedades de configuración
│   ├── controller/                  # Endpoints REST
│   │   ├── SentimentApiController.java  # Análisis de sentimientos
│   │   └── UsuarioController.java       # Autenticación de usuarios
│   ├── service/                     # Lógica de negocio
│   │   ├── SentimentService.java
│   │   ├── SentimentServiceImplement.java
│   │   ├── UserService.java
│   │   └── UserServiceImplement.java
│   ├── repository/                  # Capa de persistencia
│   │   ├── UserRepository.java
│   │   ├── RolRepository.java
│   │   └── InteraccionRepository.java
│   ├── entity/                      # Entidades JPA
│   │   ├── User.java
│   │   ├── Rol.java
│   │   └── Interaccion.java
│   ├── dto/                         # Data Transfer Objects
│   │   ├── ResponseDto.java             # Respuesta de sentimientos
│   │   ├── SentimentsResponseDto.java   # Respuesta batch
│   │   ├── UserDto.java                 # Usuario general
│   │   ├── UserDtoRegistro.java         # Registro de usuario
│   │   └── UserDtoLogin.java            # Login de usuario
│   └── globalexceptionhandler/      # Manejo de excepciones
│       └── ExecptionHandler.java
├── src/main/resources/
│   └── application.properties       # Configuración de Spring Boot y BD
├── pom.xml                          # Dependencias Maven
├── mvnw / mvnw.cmd                 # Scripts Maven Wrapper
└── .gitignore                       # Exclusiones de Git
```

---

## 🗄️ Modelo de Base de Datos

### Diagrama de Relaciones

```
┌─────────────────────────────────┐
│           usuarios              │
├─────────────────────────────────┤
│ PK │ usuario_id (INTEGER)       │
│    │ nombre (VARCHAR)           │
│    │ apellido (VARCHAR)         │
│ UQ │ correo (VARCHAR)           │
│    │ contraseña (VARCHAR HASH)  │
│ FK │ rol_id → rol.rol_id        │
└──────────────┬──────────────────┘
               │ 1:1
               │
               ▼
┌─────────────────────────────────┐
│             rol                 │
├─────────────────────────────────┤
│ PK │ rol_id (INTEGER)           │
│    │ nombre_rol (VARCHAR)       │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│         interaccion             │
├─────────────────────────────────┤
│ PK │ id_interaccion (INTEGER)   │
│    │ comentario (TEXT[])        │
│    │ fecha_creacion (TIMESTAMP) │
│    │ reseña (TEXT[])            │
│ FK │ user_id → usuarios.id      │
└─────────────────────────────────┘
               ▲
               │ 1:N
               │
       ────────┘
```

### Entidades JPA

**User (usuarios)**
- `usuario_id`: Primary Key (auto-increment)
- `nombre`: Nombre del usuario (NOT NULL)
- `apellido`: Apellido del usuario (NOT NULL)
- `correo`: Email único (UNIQUE, NOT NULL)
- `contraseña`: Hash BCrypt de la contraseña (NOT NULL)
- `rol`: Relación 1:1 con Rol (CascadeType.ALL)
- `interacciones`: Relación 1:N con Interaccion

**Rol (rol)**
- `rol_id`: Primary Key (auto-increment)
- `nombre_rol`: Nombre del rol (ej: "ADMIN", "USER")

**Interaccion (interaccion)**
- `id_interaccion`: Primary Key (auto-increment)
- `comentario`: Lista de comentarios del usuario
- `fecha_creacion`: Timestamp automático (DEFAULT CURRENT_TIMESTAMP)
- `reseña`: Lista de reseñas
- `fk`: Foreign Key a User (ManyToOne)

---

## 🚀 Guía de Uso

### Prerrequisitos

- ☕ **Java 17** o superior
- 🐘 **PostgreSQL 15+** instalado y ejecutándose
- 🐍 **Python API** ejecutándose en `http://localhost:8000`
- 📦 Maven (incluido como wrapper, no requiere instalación)

### Paso 1: Configurar PostgreSQL

**Crear la base de datos:**

```sql
-- Conectarse a PostgreSQL
psql -U postgres

-- Crear base de datos
CREATE DATABASE hackathonone;

-- Conectarse a la base de datos
\c hackathonone

-- Crear tabla de roles (opcional, Hibernate lo hace automáticamente)
INSERT INTO rol (nombre_rol) VALUES ('ADMIN');
INSERT INTO rol (nombre_rol) VALUES ('USER');
```

### Paso 2: Configurar application.properties

Edita `src/main/resources/application.properties`:

```properties
spring.application.name=sentimentapi
server.servlet.context-path=/project/api/v2

# Configuración API Python
config.url=http://localhost:8000

# Conexión PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/hackathonone
spring.datasource.username=postgres
spring.datasource.password=root

# Configuración JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
```

⚠️ **Importante:** Cambia `spring.datasource.password` por tu contraseña de PostgreSQL.

### Paso 3: Iniciar la API Python

```bash
cd api/
uvicorn main:app --reload --port 8000
```

### Paso 4: Ejecutar la API Spring Boot

**Linux/Mac:**
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

> 💡 El Maven Wrapper descargará automáticamente Maven si no está instalado.

---

## 📡 Endpoints Disponibles

### 🔐 Autenticación de Usuarios

#### 1. Registrar Usuario

**Endpoint:** `POST /project/api/v2/usuario`

Registra un nuevo usuario con contraseña encriptada (BCrypt).

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "correo": "juan.perez@example.com",
  "contraseña": "miContraseñaSegura123"
}
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/usuario \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "correo": "juan.perez@example.com",
    "contraseña": "miContraseñaSegura123"
  }'
```

**Respuesta (200 OK):**
```json
{}
```

**Seguridad:**
- ✅ Contraseña hasheada con BCrypt + salt
- ✅ Email único en base de datos
- ✅ Rol "USER" asignado automáticamente (rol_id=2)

---

#### 2. Login de Usuario

**Endpoint:** `GET /project/api/v2/usuario/{correo}/{contraseña}`

Autentica un usuario validando sus credenciales.

**Parámetros de Ruta:**
- `correo`: Email del usuario
- `contraseña`: Contraseña en texto plano (se valida contra el hash)

**Ejemplo con cURL:**
```bash
curl -X GET "http://localhost:8080/project/api/v2/usuario/juan.perez@example.com/miContraseñaSegura123"
```

**Respuesta (200 OK):**
```json
{}
```

⚠️ **Nota de Seguridad:** Este endpoint expone credenciales en la URL. Se recomienda migrar a `POST /usuario/login` con body JSON en producción.

---

### 💬 Análisis de Sentimientos

#### 3. Análisis Individual

**Endpoint:** `POST /project/api/v2/sentiment/analyze`

Analiza un único texto y retorna el sentimiento detectado.

**Headers:**
```
Content-Type: text/plain
```

**Body (raw text):**
```
El producto es excelente y llegó muy rápido
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/sentiment/analyze \
  -H "Content-Type: text/plain" \
  -d "El servicio al cliente fue excepcional"
```

**Respuesta (200 OK):**
```json
{
  "prevision": "Positivo",
  "probabilidad": 0.9456
}
```

---

#### 4. Análisis por Lotes

**Endpoint:** `POST /project/api/v2/sentiment/analyze/batch`

Analiza múltiples textos en una sola petición (separados por saltos de línea).

**Headers:**
```
Content-Type: text/plain
```

**Body (raw text, separado por `\n`):**
```
El producto es excelente
La calidad es mala
El servicio fue aceptable
```

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v2/sentiment/analyze/batch \
  -H "Content-Type: text/plain" \
  -d $'El producto es excelente\nLa calidad es mala\nEl servicio fue aceptable'
```

**Respuesta (200 OK):**
```json
{
  "results": [
    {
      "prevision": "Positivo",
      "probabilidad": 0.9456
    },
    {
      "prevision": "Negativo",
      "probabilidad": 0.8723
    },
    {
      "prevision": "Neutral",
      "probabilidad": 0.6891
    }
  ],
  "total": 3
}
```

---

## 📊 Estructura de Respuestas

### UserDtoRegistro (Registro)

| Campo | Tipo | Descripción | Requerido |
|-------|------|-------------|-----------|
| `nombre` | String | Nombre del usuario | ✅ |
| `apellido` | String | Apellido del usuario | ✅ |
| `correo` | String | Email único | ✅ |
| `contraseña` | String | Contraseña (mín. 8 caracteres) | ✅ |

### UserDtoLogin (Login)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Integer | ID del usuario |
| `nombre` | String | Nombre del usuario |
| `apellido` | String | Apellido del usuario |
| `correo` | String | Email del usuario |

### ResponseDto (Análisis Individual)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `prevision` | String | Sentimiento: "Positivo", "Negativo" o "Neutral" |
| `probabilidad` | Double | Nivel de confianza (0.0 - 1.0) |

### SentimentsResponseDto (Análisis por Lotes)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `results` | List\<ResponseDto\> | Lista de resultados individuales |
| `total` | Integer | Cantidad total de textos analizados |

---

## ⚠️ Respuestas de Error

### 400 Bad Request - Validación Fallida

**Caso 1: Texto vacío**
```json
{
  "Error": [
    "Se ha ingresado un mensaje vacio"
  ]
}
```

**Caso 2: Texto fuera de rango (análisis individual)**
```json
{
  "Error": [
    "El texto ingresado debe contener 5 o 2000 carácteres"
  ]
}
```

**Caso 3: Lote fuera de rango**
```json
{
  "Error": [
    "El texto ingresado debe contener 5 o 20000 carácteres"
  ]
}
```

### 502 Bad Gateway - API Python no disponible

```
Hubo un error al comunicarse con otro servidor
```

---

## ✨ Características Principales

### 🔐 Sistema de Autenticación
- ✅ **Registro de usuarios** con validación de datos
- ✅ **Encriptación BCrypt** con salt automático
- ✅ **Login seguro** con validación de contraseñas hasheadas
- ✅ **Roles de usuario** (Admin, User)
- ✅ **Persistencia en PostgreSQL** con JPA/Hibernate

### 💾 Base de Datos
- ✅ **PostgreSQL** como base de datos relacional
- ✅ **Hibernate ORM** con generación automática de esquema
- ✅ **Relaciones JPA**: User 1:1 Rol, User 1:N Interaccion
- ✅ **Timestamps automáticos** en tabla Interaccion
- ✅ **Restricciones de integridad**: UNIQUE email, NOT NULL

### 💬 Análisis de Sentimientos
- ✅ **Análisis individual**: 5-2000 caracteres
- ✅ **Análisis por lotes**: 5-20000 caracteres
- ✅ Mensajes de error descriptivos
- ✅ Validación de campos no vacíos

### 🔄 Procesamiento por Lotes
- 📦 **Entrada**: Múltiples textos separados por `\n`
- 🔄 **Procesamiento**: División automática y análisis paralelo
- 📊 **Salida**: Lista consolidada con total de resultados

### ⚡ Comunicación Reactiva
- 🔄 **WebClient**: Cliente HTTP no bloqueante de Spring WebFlux
- ⚡ **Asíncrono**: Mejor rendimiento y escalabilidad
- 🛡️ **Resiliente**: Manejo robusto de errores de red

### 🛡️ Manejo de Errores
- 🛡️ **Global Exception Handler**: Captura centralizada de excepciones
- 📝 **Respuestas estructuradas**: JSON consistente para todos los errores
- 🔍 **Tipos de error**: Validación (400), Conectividad (502)

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────┐                                          
│     Cliente     │                                          
│  (Postman, cURL,│                                          
│   Aplicación)   │                                          
└────────┬────────┘                                          
         │ HTTP POST/GET                                         
         │                                      
         ▼                                                   
┌──────────────────────────────────────────────────────────┐         
│              Spring Boot API Gateway (v2)                │         
│  ┌────────────────────────────────────────────────────┐  │         
│  │         UsuarioController                          │  │         
│  │  • POST /usuario (registro)                        │  │         
│  │  • GET /usuario/{correo}/{contraseña} (login)      │  │         
│  └──────────────┬─────────────────────────────────────┘  │         
│                 │                                         │         
│  ┌──────────────▼─────────────────────────────────────┐  │         
│  │      UserService + BCrypt Security                 │  │         
│  │  • Registro con hash de contraseñas               │  │         
│  │  • Validación de login con BCrypt.checkpw()       │  │         
│  └──────────────┬─────────────────────────────────────┘  │         
│                 │                                         │         
│  ┌──────────────▼─────────────────────────────────────┐  │         
│  │      UserRepository (Spring Data JPA)              │  │         
│  └──────────────┬─────────────────────────────────────┘  │         
│                 │                                         │         
│                 ▼                                         │
│  ┌──────────────────────────────────────────────────┐    │
│  │         PostgreSQL Database                      │    │
│  │  • usuarios (user data + hashed passwords)       │    │
│  │  • rol (user roles)                              │    │
│  │  • interaccion (user interactions)               │    │
│  └──────────────────────────────────────────────────┘    │
│                                                           │
│  ┌────────────────────────────────────────────────────┐  │         
│  │      SentimentApiController                        │  │         
│  │  • POST /sentiment/analyze                         │  │         
│  │  • POST /sentiment/analyze/batch                   │  │         
│  └──────────────┬─────────────────────────────────────┘  │         
│                 │                                         │         
│  ┌──────────────▼─────────────────────────────────────┐  │         
│  │      Jakarta Validation                            │  │         
│  │  • @NotBlank, @Size                                │  │         
│  └──────────────┬─────────────────────────────────────┘  │         
│                 │                                         │         
│  ┌──────────────▼─────────────────────────────────────┐  │         
│  │      SentimentServiceImplement                     │  │         
│  │  • consultarSentimiento()                          │  │         
│  │  • consultarSentimientos()                         │  │         
│  └──────────────┬─────────────────────────────────────┘  │         
│                 │                                         │         
│  ┌──────────────▼─────────────────────────────────────┐  │         
│  │      WebClient (Spring WebFlux)                    │  │         
│  │  • Comunicación HTTP reactiva                      │  │         
│  └──────────────┬─────────────────────────────────────┘  │         
└─────────────────┼──────────────────────────────────────────┘         
                  │ HTTP POST (application/json)                       
                  ▼                                          
         ┌────────────────┐                                 
         │  Python API    │                                 
         │   (FastAPI)    │                                 
         │ /sentiment     │                                 
         │ /sentiment/batch│                                
         └────────┬───────┘                                 
                  │                                          
                  ▼                                          
         ┌────────────────┐                                 
         │   Modelo ML    │                                 
         │  (Sentimientos)│                                 
         └────────────────┘                                 
```

---

## ⚙️ Configuración

### application.properties

```properties
# Nombre de la aplicación
spring.application.name=sentimentapi

# Context path de la API (v2)
server.servlet.context-path=/project/api/v2

# URL de la API Python
config.url=http://localhost:8000

# Configuración PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/hackathonone
spring.datasource.username=postgres
spring.datasource.password=root

# Configuración JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# Puerto del servidor (opcional)
server.port=8080
```

### Variables de Configuración

| Propiedad | Descripción | Valor por Defecto |
|-----------|-------------|-------------------|
| `config.url` | URL base de la API Python | `http://localhost:8000` |
| `server.servlet.context-path` | Prefijo de todos los endpoints | `/project/api/v2` |
| `server.port` | Puerto del servidor Spring Boot | `8080` |
| `spring.datasource.url` | URL de conexión PostgreSQL | `jdbc:postgresql://localhost:5432/hackathonone` |
| `spring.jpa.hibernate.ddl-auto` | Estrategia de generación de esquema | `update` |
| `spring.jpa.show-sql` | Mostrar SQL en consola | `true` |

---

## 🧪 Testing y Desarrollo

### Compilar el Proyecto

```bash
# Linux/Mac
./mvnw clean compile

# Windows
mvnw.cmd clean compile
```

### Ejecutar Tests

```bash
# Linux/Mac
./mvnw test

# Windows
mvnw.cmd test
```

### Empaquetar como JAR

```bash
# Linux/Mac
./mvnw clean package

# Windows
mvnw.cmd clean package

# Ejecutar JAR
java -jar target/sentimentapi-0.0.1-SNAPSHOT.jar
```

### Limpiar Build

```bash
# Linux/Mac
./mvnw clean

# Windows
mvnw.cmd clean
```

---

## 🛠️ Dependencias del Proyecto

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| spring-boot-starter-webmvc | 4.0.1 | Framework web MVC |
| spring-boot-starter-webflux | 4.0.1 | WebClient reactivo |
| spring-boot-starter-data-jpa | 4.0.1 | ORM con Hibernate |
| spring-boot-starter-validation | 4.0.1 | Validación de beans |
| postgresql | Latest | Driver JDBC PostgreSQL |
| jakarta.validation-api | 3.0.2 | API de validación Jakarta |
| jbcrypt | 0.4 | Encriptación de contraseñas |
| lombok | Latest | Reducción de boilerplate |

---

## 🐛 Troubleshooting

### Error: "Hubo un error al comunicarse con otro servidor"

**Causa:** La API Python no está disponible o la URL está mal configurada.

**Solución:**
1. Verifica que la API Python esté ejecutándose:
   ```bash
   curl http://localhost:8000/docs
   ```
2. Revisa `application.properties` y confirma la URL correcta
3. Verifica conectividad de red

---

### Error: "Connection refused" a PostgreSQL

**Causa:** PostgreSQL no está ejecutándose o la configuración es incorrecta.

**Solución:**
1. Inicia PostgreSQL:
   ```bash
   # Linux
   sudo systemctl start postgresql
   
   # macOS
   brew services start postgresql
   
   # Windows
   net start postgresql-x64-15
   ```
2. Verifica que la base de datos `hackathonone` existe
3. Confirma usuario y contraseña en `application.properties`

---

### Error: "Permission denied" al ejecutar mvnw

**Causa:** El script no tiene permisos de ejecución (Linux/Mac).

**Solución:**
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

---

### Error: "Port 8080 already in use"

**Causa:** Otro proceso está usando el puerto 8080.

**Solución 1 - Cambiar puerto:**
```properties
# application.properties
server.port=8081
```

**Solución 2 - Liberar puerto:**
```bash
# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

### Error: Tabla "rol" vacía, no se puede registrar usuario

**Causa:** La tabla `rol` no tiene datos iniciales.

**Solución:**
```sql
-- Conectarse a PostgreSQL
psql -U postgres -d hackathonone

-- Insertar roles
INSERT INTO rol (nombre_rol) VALUES ('ADMIN');
INSERT INTO rol (nombre_rol) VALUES ('USER');
```

---

## 📋 Requisitos del Sistema

| Componente | Requisito |
|------------|-----------|
| **Java** | 17 o superior |
| **PostgreSQL** | 15 o superior |
| **Maven** | Incluido (Maven Wrapper) |
| **RAM** | 1 GB mínimo |
| **Espacio en Disco** | 500 MB para dependencias |
| **Sistema Operativo** | Linux, macOS, Windows |

---

## 📝 Ejemplos de Uso Completo

### 1. Flujo de Registro y Login

```bash
# 1. Registrar usuario
curl -X POST http://localhost:8080/project/api/v2/usuario \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María",
    "apellido": "González",
    "correo": "maria.gonzalez@example.com",
    "contraseña": "MiPassword2024!"
  }'

# 2. Login
curl -X GET "http://localhost:8080/project/api/v2/usuario/maria.gonzalez@example.com/MiPassword2024!"

# 3. Analizar sentimiento
curl -X POST http://localhost:8080/project/api/v2/sentiment/analyze \
  -H "Content-Type: text/plain" \
  -d "El producto superó mis expectativas"
```

### 2. Postman Collection

**Registro de Usuario:**
```
POST http://localhost:8080/project/api/v2/usuario
Content-Type: application/json

{
  "nombre": "Carlos",
  "apellido": "Rodríguez",
  "correo": "carlos.rodriguez@example.com",
  "contraseña": "Segura123!"
}
```

**Login:**
```
GET http://localhost:8080/project/api/v2/usuario/carlos.rodriguez@example.com/Segura123!
```

**Análisis Individual:**
```
POST http://localhost:8080/project/api/v2/sentiment/analyze
Content-Type: text/plain

La atención al cliente fue excepcional y el producto llegó en perfecto estado
```

**Análisis por Lotes:**
```
POST http://localhost:8080/project/api/v2/sentiment/analyze/batch
Content-Type: text/plain

El producto es de muy buena calidad
El envío tardó demasiado tiempo
El precio es razonable para lo que ofrece
```

---

## 🔒 Consideraciones de Seguridad

### ⚠️ Mejoras Recomendadas para Producción

1. **Endpoint de Login:**
   - Migrar de GET a POST para evitar exposición de credenciales en URL
   - Implementar JWT para sesiones stateless
   - Agregar rate limiting para prevenir brute force

2. **Validaciones:**
   - Agregar `@Email` en campo correo
   - Implementar validación de complejidad de contraseña
   - Agregar `@Size(min=8, max=100)` en contraseña

3. **Base de Datos:**
   - Usar variables de entorno para credenciales
   - Implementar cifrado a nivel de columna para datos sensibles
   - Configurar SSL para conexión a PostgreSQL

4. **API:**
   - Implementar CORS correctamente
   - Agregar HTTPS en producción
   - Implementar auditoría de acciones de usuarios

---

## 🤝 Contribuciones

Este proyecto fue desarrollado como parte del **Hackathon ONE - No Country**.

### Cómo Contribuir

1. Fork el repositorio
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -m 'feat: agrega nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto está bajo licencia Apache 2.0 (heredada de Spring Boot).

---

## 🔄 Changelog

### v2.0.0 (Actual)
- ✅ Agrega sistema de autenticación con PostgreSQL
- ✅ Implementa registro de usuarios con BCrypt
- ✅ Crea entidades JPA: User, Rol, Interaccion
- ✅ Implementa repositorios Spring Data JPA
- ✅ Actualiza context path a `/project/api/v2`

### v1.0.0
- ✅ API Gateway para análisis de sentimientos
- ✅ Endpoints individual y batch
-
