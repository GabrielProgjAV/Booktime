📚 Booktime AI

**Versión 1.0.0 — Estable**

Aplicación Android desarrollada con Jetpack Compose que integra inteligencia artificial con Gemini para mejorar la experiencia de lectura de libros PDF.

La aplicación permite:

Leer PDFs dentro de la app
Chatear con una IA contextual
Obtener resúmenes y análisis
Guardar historial de conversaciones
Extraer texto del PDF automáticamente
Responder usando contexto real del libro
🚀 Tecnologías utilizadas
Frontend
Kotlin
Jetpack Compose
Material 3
Backend / Servicios
Firebase Firestore
Firebase Authentication
Gemini API
IA
Prompt Engineering
Context Injection
Mini-RAG básico
PDFs
PdfRenderer
PDFBox Android
🧠 Funcionamiento de la IA
La IA utiliza Gemini API para generar respuestas relacionadas con el libro que el usuario está leyendo.

El sistema:

Extrae texto del PDF
Busca fragmentos relevantes según la pregunta del usuario
Envía contexto específico a Gemini
Genera respuestas contextualizadas
Esto permite respuestas más precisas y evita respuestas genéricas.

📄 Funcionalidades principales
✅ Lector PDF
Renderizado de páginas
Scroll vertical
Carga optimizada de páginas
✅ Chat IA
Preguntas personalizadas
Resumen automático
Explicación de personajes
Temas principales
✅ Historial persistente
Conversaciones guardadas en Firebase
Recuperación automática del historial
✅ Mini-RAG
Búsqueda básica de fragmentos relevantes
Contexto dinámico según la pregunta
✅ UI moderna
Diseño dark mode
Burbujas de chat
Auto scroll
Componentes Material 3
🔥 Arquitectura general
ChatBottomSheet
Maneja:

interfaz del chat
prompts IA
envío de mensajes
renderizado visual
GeminiRepository
Encargado de:

conexión con Gemini API
requests HTTP usando Retrofit
manejo de respuestas
ChatRepository
Encargado de:

guardar chats en Firestore
recuperar historial
PdfTextExtractor
Extrae texto del PDF usando PDFBox.

PdfContextHelper
Busca fragmentos relevantes del PDF para mejorar el contexto enviado a la IA.

⚡ Manejo de errores
La aplicación incluye:

control de peticiones repetidas
manejo de errores de red
validación de contexto
limitación de spam
respuestas fuera de contexto
📌 Posibles mejoras futuras
Streaming de respuestas IA
Embeddings reales
RAG avanzado
OCR para PDFs escaneados
Búsqueda semántica
Sincronización multiusuario
Sistema de favoritos
Modo offline con IA local
👨‍💻 Autor
Proyecto desarrollado como aplicación de lectura inteligente con integración de inteligencia artificial contextual, originado como trabajo en equipo en la universidad.

Este repositorio es mantenido por Gabriel Ramos (GabrielProgjAV), quien desarrolló principalmente el módulo de chatbot IA: la integración con Gemini, el sistema de contexto (Mini-RAG), el historial de conversaciones en Firebase, la UI del chat y la integración con el lector de PDF.
