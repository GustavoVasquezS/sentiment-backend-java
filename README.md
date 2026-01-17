# 🎯 SentimentAPI Backend

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-Wrapper-red?style=flat-square)

**API REST Gateway para Análisis de Sentimientos**

Hackathon ONE - No Country

</div>

---

## 📖 Descripción

API REST desarrollada en **Spring Boot 4.0.1** que actúa como gateway para consumir un modelo de Machine Learning de análisis de sentimientos (Python/FastAPI). Proporciona validación de entrada robusta, manejo centralizado de errores y procesamiento tanto individual como por lotes de textos en español.

**Stack Tecnológico:**
- ☕ Java 17
- 🍃 Spring Boot 4.0.1
- 🔧 Maven Wrapper
- 🔄 WebFlux (WebClient para comunicación HTTP reactiva)
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
│   │   └── SentimentApiController.java
│   ├── service/                     # Lógica de negocio
│   │   ├── SentimentService.java
│   │   └── SentimentServiceImplement.java
│   ├── dto/                         # Data Transfer Objects
│   │   ├── ResponseDto.java
│   │   └── SentimentsResponseDto.java
│   └── globalexceptionhandler/      # Manejo de excepciones
│       └── ExecptionHandler.java
├── src/main/resources/
│   └── application.properties       # Configuración de Spring Boot
├── pom.xml                          # Dependencias Maven
├── mvnw / mvnw.cmd                 # Scripts Maven Wrapper
└── .gitignore                       # Exclusiones de Git
```

### 📦 Componentes Principales

#### **Configuration**
- `ConectarApi.java`: Configuración de WebClient reactivo para comunicación con la API Python
- `EndPointConfg.java`: Gestión de URLs mediante `@ConfigurationProperties`

#### **Controller**
- `SentimentApiController.java`: Expone 2 endpoints REST
  - `POST /sentiment/analyze`: Análisis individual
  - `POST /sentiment/analyze/batch`: Análisis por lotes
- Validaciones declarativas con Jakarta Validation

#### **Service Layer**
- `SentimentService.java`: Interfaz del servicio
- `SentimentServiceImplement.java`: Implementación con WebClient
  - Manejo de comunicación HTTP reactiva
  - Procesamiento de respuestas JSON
  - Manejo de errores de conexión

#### **DTO**
- `ResponseDto.java`: Respuesta individual (`prevision`, `probabilidad`)
- `SentimentsResponseDto.java`: Respuesta por lotes con lista de resultados

#### **Global Exception Handler**
- `ExecptionHandler.java`: Manejo centralizado de `ConstraintViolationException`
- Respuestas HTTP 400 estructuradas para errores de validación

---

## 🚀 Guía de Uso

### Prerrequisitos

- ☕ **Java 17** o superior
- 🐍 **Python API** ejecutándose en `http://127.0.0.1:8000`
- 📦 Maven (incluido como wrapper, no requiere instalación)

### Paso 1: Configurar la URL de la API Python

Edita `src/main/resources/application.properties`:

```properties
config.url=http://127.0.0.1:8000
```

### Paso 2: Iniciar la API Python

```bash
cd api/
uvicorn main:app --reload --port 8000
```

### Paso 3: Ejecutar la API Spring Boot

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

### 1. Análisis Individual

**Endpoint:** `POST /sentiment/analyze`

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
curl -X POST http://localhost:8080/sentiment/analyze \
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

### 2. Análisis por Lotes

**Endpoint:** `POST /sentiment/analyze/batch`

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
curl -X POST http://localhost:8080/sentiment/analyze/batch \
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

### ResponseDto (Análisis Individual)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `prevision` | String | Sentimiento detectado: "Positivo", "Negativo" o "Neutral" |
| `probabilidad` | Double | Nivel de confianza del modelo (0.0 - 1.0) |

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

**Caso 2: Texto fuera de rango**
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

### Validaciones Automáticas
- ✅ **Análisis individual**: 5-2000 caracteres
- ✅ **Análisis por lotes**: 5-20000 caracteres
- ✅ Mensajes de error descriptivos
- ✅ Validación de campos no vacíos

### Procesamiento por Lotes
- 📦 **Entrada**: Múltiples textos separados por `\n`
- 🔄 **Procesamiento**: División automática y análisis paralelo
- 📊 **Salida**: Lista consolidada con total de resultados

### Comunicación Reactiva
- 🔄 **WebClient**: Cliente HTTP no bloqueante de Spring WebFlux
- ⚡ **Asíncrono**: Mejor rendimiento y escalabilidad
- 🛡️ **Resiliente**: Manejo robusto de errores de red

### Manejo de Errores
- 🛡️ **Global Exception Handler**: Captura centralizada de excepciones
- 📝 **Respuestas estructuradas**: JSON consistente para todos los errores
- 🔍 **Tipos de error**: Validación (400), Conectividad (502)

### Configuración Externalizada
- ⚙️ **`@ConfigurationProperties`**: URL configurable vía `application.properties`
- 🔧 **Fácil deployment**: Cambio de entornos sin recompilar
- 📄 **Documentado**: Configuración clara y mantenible

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────┐                                          
│     Cliente     │                                          
│  (Postman, cURL,│                                          
│   Aplicación)   │                                          
└────────┬────────┘                                          
         │ HTTP POST                                         
         │ (text/plain)                                      
         ▼                                                   
┌─────────────────────────────────────────────────┐         
│         Spring Boot API (Gateway)               │         
│  ┌───────────────────────────────────────────┐ │         
│  │      SentimentApiController               │ │         
│  │  • /sentiment/analyze                     │ │         
│  │  • /sentiment/analyze/batch               │ │         
│  └──────────────┬────────────────────────────┘ │         
│                 │                                │         
│  ┌──────────────▼────────────────────────────┐ │         
│  │      Jakarta Validation                   │ │         
│  │  • @NotBlank                              │ │         
│  │  • @Size(min=5, max=2000)                │ │         
│  └──────────────┬────────────────────────────┘ │         
│                 │                                │         
│  ┌──────────────▼────────────────────────────┐ │         
│  │      SentimentServiceImplement            │ │         
│  │  • consultarSentimiento()                 │ │         
│  │  • consultarSentimientos()                │ │         
│  └──────────────┬────────────────────────────┘ │         
│                 │                                │         
│  ┌──────────────▼────────────────────────────┐ │         
│  │      WebClient (Spring WebFlux)           │ │         
│  │  • Comunicación HTTP reactiva             │ │         
│  │  • Manejo de errores                      │ │         
│  └──────────────┬────────────────────────────┘ │         
└─────────────────┼────────────────────────────────┘         
                  │ HTTP POST                                
                  │ (application/json)                       
                  ▼                                          
         ┌────────────────┐                                 
         │  Python API    │                                 
         │   (FastAPI)    │                                 
         │                │                                 
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

### Flujo de Datos

**Análisis Individual:**
1. Cliente → Envía texto (text/plain)
2. Controller → Valida longitud (5-2000 chars)
3. Service → Construye JSON: `{"text": "..."}`
4. WebClient → POST a `/sentiment`
5. Python API → Procesa con modelo ML
6. Service → Mapea a `ResponseDto`
7. Controller → Retorna JSON al cliente

**Análisis por Lotes:**
1. Cliente → Envía textos separados por `\n`
2. Controller → Valida longitud (5-20000 chars)
3. Service → Divide por `\n` y construye: `{"texts": ["...", "..."]}`
4. WebClient → POST a `/sentiment/batch`
5. Python API → Procesa múltiples textos
6. Service → Mapea a `SentimentsResponseDto`
7. Controller → Retorna JSON con array de resultados

---

## ⚙️ Configuración

### application.properties

```properties
# URL de la API Python
config.url=http://127.0.0.1:8000

# Puerto del servidor Spring Boot
server.port=8080

# Nivel de logging (opcional)
logging.level.com.project.sentimentapi=INFO
```

### Variables de Configuración

| Propiedad | Descripción | Valor por Defecto |
|-----------|-------------|-------------------|
| `config.url` | URL base de la API Python | `http://127.0.0.1:8000` |
| `server.port` | Puerto del servidor Spring Boot | `8080` |

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
| spring-boot-starter-validation | 4.0.1 | Validación de beans |
| jakarta.validation-api | 3.0.2 | API de validación Jakarta |
| lombok | Latest | Reducción de boilerplate |

---

## 🐛 Troubleshooting

### Error: "Hubo un error al comunicarse con otro servidor"

**Causa:** La API Python no está disponible o la URL está mal configurada.

**Solución:**
1. Verifica que la API Python esté ejecutándose:
   ```bash
   curl http://127.0.0.1:8000/docs
   ```
2. Revisa `application.properties` y confirma la URL correcta
3. Verifica conectividad de red

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

### Error: Validación no funciona

**Causa:** Falta `@Validated` en el controlador.

**Solución:** El controlador ya tiene `@Validated`, verifica que el texto cumpla las restricciones:
- Individual: 5-2000 caracteres
- Lote: 5-20000 caracteres

---

## 📋 Requisitos del Sistema

| Componente | Requisito |
|------------|-----------|
| **Java** | 17 o superior |
| **Maven** | Incluido (Maven Wrapper) |
| **RAM** | 512 MB mínimo |
| **Espacio en Disco** | 200 MB para dependencias |
| **Sistema Operativo** | Linux, macOS, Windows |

---

## 📝 Ejemplos Avanzados

### Postman Collection

**Análisis Individual:**
```
POST http://localhost:8080/sentiment/analyze
Content-Type: text/plain

La atención al cliente fue excelente y el producto llegó en perfecto estado
```

**Análisis por Lotes:**
```
POST http://localhost:8080/sentiment/analyze/batch
Content-Type: text/plain

El producto es de muy buena calidad
El envío tardó demasiado tiempo
El precio es razonable para lo que ofrece
```

### Script de Testing (Bash)

```bash
#!/bin/bash

# Test análisis individual
echo "Testing análisis individual..."
curl -X POST http://localhost:8080/sentiment/analyze \
  -H "Content-Type: text/plain" \
  -d "Excelente producto" \
  | jq

# Test análisis por lotes
echo -e "\nTesting análisis por lotes..."
curl -X POST http://localhost:8080/sentiment/analyze/batch \
  -H "Content-Type: text/plain" \
  -d $'Muy bueno\nTerrible experiencia\nNada especial' \
  | jq
```

---

## 🤝 Contribuciones

Este proyecto fue desarrollado como parte del **Hackathon ONE - No Country**.

### Cómo Contribuir

1. Fork el repositorio
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -m 'Agrega nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto está bajo licencia Apache 2.0 (heredada de Spring Boot).

---

<div align="center">

**Proyecto Hackathon ONE - No Country**

Integrado con [API Python/ML](../api/README.md)

Desarrollado con ❤️ usando Spring Boot

</div>
