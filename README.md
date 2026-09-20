# 📚 Booktime AI

**Version 1.0.0 — Stable**

An Android application built with Jetpack Compose that integrates AI (Gemini) to enhance the PDF book-reading experience.

The app allows users to:

- Read PDFs inside the app
- Chat with a context-aware AI
- Get summaries and analysis
- Save conversation history
- Automatically extract text from the PDF
- Get answers grounded in the actual content of the book

## 🚀 Tech Stack

**Frontend**
- Kotlin
- Jetpack Compose
- Material 3

**Backend / Services**
- Firebase Firestore
- Firebase Authentication
- Gemini API

**AI**
- Prompt Engineering
- Context Injection
- Basic Mini-RAG

**PDF**
- PdfRenderer
- PDFBox Android

## 🧠 How the AI Works

The AI uses the Gemini API to generate answers related to the book the user is currently reading.

The system:

1. Extracts text from the PDF
2. Searches for fragments relevant to the user's question
3. Sends specific context to Gemini
4. Generates a context-aware answer

This produces more accurate answers and avoids generic responses.

## 📄 Main Features

**✅ PDF Reader**
- Page rendering
- Vertical scroll
- Optimized page loading

**✅ AI Chat**
- Custom questions
- Automatic summaries
- Character explanations
- Main themes

**✅ Persistent History**
- Conversations saved in Firebase
- Automatic history retrieval

**✅ Mini-RAG**
- Basic relevant-fragment search
- Dynamic context based on the question

**✅ Modern UI**
- Dark mode design
- Chat bubbles
- Auto scroll
- Material 3 components

## 🔥 Architecture Overview

**ChatBottomSheet**
Handles:
- Chat interface
- AI prompts
- Message sending
- Visual rendering

**GeminiRepository**
In charge of:
- Connection to the Gemini API
- HTTP requests using Retrofit
- Response handling

**ChatRepository**
In charge of:
- Saving chats to Firestore
- Retrieving history

**PdfTextExtractor**
Extracts text from the PDF using PDFBox.

**PdfContextHelper**
Searches for relevant PDF fragments to improve the context sent to the AI.

## ⚡ Error Handling

The app includes:

- Duplicate request throttling
- Network error handling
- Context validation
- Spam limiting
- Out-of-context response handling

## 📌 Possible Future Improvements

- AI response streaming
- Real embeddings
- Advanced RAG
- OCR for scanned PDFs
- Semantic search
- Multi-user sync
- Favorites system
- Offline mode with local AI

## 👨‍💻 Author

This project started as a team assignment at university, built as a smart reading application with contextual AI integration.

This repository is maintained by **Gabriel Ramos** ([GabrielProgjAV](https://github.com/GabrielProgjAV)), who mainly developed the AI chatbot module: the Gemini integration, the context system (Mini-RAG), the conversation history in Firebase, the chat UI, and the integration with the PDF reader.
