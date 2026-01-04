# 🎯 SentimentAPI Backend

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?style=flat-square)

**API REST Gateway para Análisis de Sentimientos**

Hackathon ONE - No Country

</div>

---

## 📖 Descripción

API REST en **Spring Boot** que consume el modelo ML de análisis de sentimientos (Python/FastAPI) y proporciona validación, manejo de errores y transformación de datos para reseñas Amazon en español.

**Stack:** Java 17 | Spring Boot 4.0.1 | Maven | RestTemplate

---

## 📁 Estructura
```
sentimentapi/
├── src/main/java/com/project/sentimentapi/
│   ├── controller/          # Endpoints REST y validaciones
│   ├── service/             # Lógica de negocio e integración con Python API
│   ├── dto/                 # Objetos de transferencia de datos
│   └── globalexceptionhandler/  # Manejo centralizado de excepciones
├── src/main/resources/
│   └── application.properties   # Configuración de Spring Boot
└── pom.xml                      # Dependencias Maven
```

### 📦 Descripción de Componentes

- **controller/**: Contiene `SentimentApiController.java` que define los endpoints REST y aplica validaciones de entrada mediante anotaciones Jakarta Validation.

- **service/**: Incluye `SentimentService.java` (interfaz) y `SentimentServiceImplement.java` (implementación) que gestiona la comunicación con la API Python usando RestTemplate y transforma las respuestas.

- **dto/**: Define `ResponseDto.java`, el objeto que mapea la respuesta del modelo ML y controla qué campos se exponen en el JSON de respuesta.

- **globalexceptionhandler/**: Contiene `ExceptionHandler.java` que captura todas las excepciones de validación y errores de conexión, devolviendo respuestas HTTP estructuradas y consistentes.

---

## 🚀 Cómo Usar

### Prerrequisitos
- ☕ Java 17+
- 🐍 Python API corriendo en `http://127.0.0.1:8000`

### Paso 1: Iniciar Python API (Modelo ML)
```bash
cd api/
uvicorn main:app --reload --port 8000
```

### Paso 2: Iniciar Spring Boot API
```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Paso 3: Usar el Endpoint

**Endpoint:** `POST http://localhost:8080/project/api/v1/sentiment/analyze`

**Ejemplo con cURL:**
```bash
curl -X POST http://localhost:8080/project/api/v1/sentiment/analyze \
  -H "Content-Type: text/plain" \
  -d "Producto increíble, superó mis expectativas"
```

**Ejemplo con Postman:**
```
Method: POST
URL: http://localhost:8080/project/api/v1/sentiment/analyze
Headers:
  Content-Type: text/plain
Body (raw):
  Producto increíble, superó mis expectativas
```

**Respuesta:**
```json
{
  "prevision": "Positivo",
  "probabilidad": 0.9234,
  "calificación": "★ ★ ★ ★ ★"
}
```

---

## ✨ Características Destacadas

- ✅ **Validación** automática de entrada (5-500 caracteres)
- ⭐ **Transformación visual** de estrellas numéricas a Unicode (★)
- 🔄 **Integración** con modelo ML mediante RestTemplate
- 🛡️ **Manejo robusto** de errores (400 Bad Request, 502 Bad Gateway)
- 📊 **Arquitectura en capas:** Controller → Service → Python API → Modelo ML

---

<div align="center">

**Proyecto Hackathon ONE** | Integrado con [API Python/ML](../README.md)

</div>
