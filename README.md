🌟 SentimentAPI Backend - Sistema de Análisis de Sentimientos
📋 Descripción
API REST desarrollada en Spring Boot 3.x que actúa como gateway para el sistema de análisis de sentimientos de reseñas Amazon en español. Este microservicio Java consume el modelo de Machine Learning expuesto por la API Python (FastAPI) y proporciona una capa adicional de validación, manejo de errores y transformación de datos.
Desarrollado para: Hackathon ONE - Simulación de entorno laboral tech
Stack Tecnológico: Java 17, Spring Boot 4.0.1, Maven, REST Template
🎯 Características Principales

✅ Validación robusta de entrada con Jakarta Validation
🔄 Integración con modelo ML mediante RestTemplate
🎨 Transformación visual de estrellas a caracteres Unicode (★)
🛡️ Manejo global de excepciones con respuestas estructuradas
📊 DTO pattern para desacoplamiento de capas
🚀 Context path configurable para versionado de API


🏗️ Estructura del Proyecto
sentimentapi/
├── src/
│   ├── main/
│   │   ├── java/com/project/sentimentapi/
│   │   │   ├── SentimentapiApplication.java          # Clase principal Spring Boot
│   │   │   ├── controller/
│   │   │   │   └── SentimentApiController.java       # Endpoints REST
│   │   │   ├── service/
│   │   │   │   ├── SentimentService.java             # Interfaz de servicio
│   │   │   │   └── SentimentServiceImplement.java    # Lógica de negocio
│   │   │   ├── dto/
│   │   │   │   └── ResponseDto.java                  # Objeto de transferencia
│   │   │   └── globalexceptionhandler/
│   │   │       └── ExceptionHandler.java             # Manejo centralizado de errores
│   │   └── resources/
│   │       └── application.properties                # Configuración de Spring
│   └── test/
│       └── java/com/project/sentimentapi/
│           └── SentimentapiApplicationTests.java
├── pom.xml                                           # Dependencias Maven
├── mvnw / mvnw.cmd                                   # Maven Wrapper
└── .mvn/                                             # Configuración Maven

🚀 Cómo Usar
Prerrequisitos

Java 17 o superior instalado
Maven 3.9.x (incluido como wrapper en el proyecto)
API Python del modelo ML ejecutándose en http://127.0.0.1:8000

Instalación y Ejecución
Opción 1: Maven Wrapper (Recomendado)
bash# Linux/Mac
./mvnw clean install
./mvnw spring-boot:run

# Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run
Opción 2: Maven Local
bashmvn clean install
mvn spring-boot:run
```

La aplicación se iniciará en:
```
http://localhost:8080/project/api/v1

📡 Endpoints Disponibles
1. Health Check
httpGET /project/api/v1/sentiment/analyze
```

**Respuesta:**
```
"retornando mensaje de prueba"
2. Análisis de Sentimiento
httpPOST /project/api/v1/sentiment/analyze
Content-Type: text/plain

El producto es excelente, superó mis expectativas
Validaciones:

✅ Texto no vacío
✅ Longitud: 5-500 caracteres

Respuesta Exitosa (200 OK):
json{
  "prevision": "Positivo",
  "probabilidad": 0.9234,
  "calificación": "★ ★ ★ ★ ★"
}
Respuesta de Error (400 Bad Request):
json{
  "Error": [
    "El texto ingresado debe contener 5 o 500 carácteres",
    "Se ha ingresado un mensaje vacio"
  ]
}
Respuesta de Error de Servidor (502 Bad Gateway):
json"Hubo un error al comunicarse con otro servidor"

💻 Ejemplos de Uso
cURL
bash# Sentimiento positivo
curl -X POST http://localhost:8080/project/api/v1/sentiment/analyze \
  -H "Content-Type: text/plain" \
  -d "Producto increíble, llegó rápido y en perfectas condiciones"

# Sentimiento negativo
curl -X POST http://localhost:8080/project/api/v1/sentiment/analyze \
  -H "Content-Type: text/plain" \
  -d "Pésima calidad, no funciona como se describe"
Java (HttpClient)
javaHttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:8080/project/api/v1/sentiment/analyze"))
    .header("Content-Type", "text/plain")
    .POST(HttpRequest.BodyPublishers.ofString("Excelente servicio"))
    .build();

HttpResponse<String> response = client.send(request, 
    HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
JavaScript (Fetch)
javascriptconst response = await fetch('http://localhost:8080/project/api/v1/sentiment/analyze', {
  method: 'POST',
  headers: { 'Content-Type': 'text/plain' },
  body: 'El producto cumple con lo prometido'
});

const data = await response.json();
console.log(`${data.prevision} - ${data.calificación}`);
```

---

## 🎯 Características Destacadas

### 1. **Arquitectura en Capas**
```
Controller → Service → RestTemplate → Python API
                ↓
              DTO
                ↓
        Exception Handler
2. Sistema de Calificación Visual
El backend convierte automáticamente las estrellas numéricas en representación Unicode:
java// Entrada: estrellas = 4
// Salida: calificación = "★ ★ ★ ★"
3. Validación Declarativa
java@NotBlank(message = "Se ha ingresado un mensaje vacio")
@Size(min = 5, max = 500, message = "El texto ingresado debe contener 5 o 500 carácteres")
4. Manejo Resiliente de Errores

✅ Timeout automático en conexión con Python API
✅ Respuestas HTTP semánticas (200, 400, 502)
✅ Logging de excepciones para debugging

5. Configuración Flexible
properties# application.properties
server.servlet.context-path=/project/api/v1  # Versionado de API
# Configurable para múltiples entornos

🔧 Configuración Avanzada
Cambiar Puerto del Servidor
properties# application.properties
server.port=9090
Modificar URL de Python API
java// SentimentServiceImplement.java
String urlPython = "http://localhost:8000/sentiment";  // Cambiar aquí
Ajustar Validaciones
java// SentimentApiController.java
@Size(min = 10, max = 1000)  // Personalizar límites

🔗 Integración con Python API
Este backend requiere que la API Python esté ejecutándose. Secuencia de inicio:
bash# Terminal 1: Iniciar Python API (FastAPI)
cd api/
uvicorn main:app --reload --port 8000

# Terminal 2: Iniciar Spring Boot API
./mvnw spring-boot:run
```

**Flujo de datos:**
```
Cliente → Spring Boot:8080 → Python FastAPI:8000 → Modelo ML → Respuesta

🧪 Testing
bash# Ejecutar tests
./mvnw test

# Ejecutar con cobertura
./mvnw test jacoco:report

📦 Dependencias Clave
DependenciaVersiónPropósitoSpring Boot Starter WebMVC4.0.1Framework RESTLombok-Reducir boilerplateJakarta Validation3.0.2Validación de datosSpring Boot Starter Validation4.0.1Integración validación

👥 Créditos
Proyecto: Hackathon ONE - No Country
Equipo: Desarrollo Full Stack
Fecha: Enero 2026

📄 Licencia
Proyecto educativo y demostrativo para simulación laboral.

🆘 Troubleshooting
Error: "Hubo un error al comunicarse con otro servidor"
Solución: Verificar que Python API esté ejecutándose:
bashcurl http://127.0.0.1:8000/health
Error: Port already in use
Solución: Cambiar puerto en application.properties o terminar proceso:
bash# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
