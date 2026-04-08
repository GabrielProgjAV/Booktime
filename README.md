# 📚 Booktime App

Aplicación Android desarrollada con Jetpack Compose que permite a los usuarios registrarse, iniciar sesión y gestionar sus preferencias de lectura.

---

## 🚀 Requisitos

Antes de comenzar, asegúrate de tener instalado:

* Android Studio (última versión recomendada)
* JDK 17 o superior
* Gradle (incluido en Android Studio)
* Cuenta de Firebase

---

## 📥 Clonar el repositorio

```bash
git clone https://github.com/danielf-canon/Booktime.git
cd Booktime
```

---

## 🔥 Configuración de Firebase

⚠️ **Este paso es obligatorio para que la app funcione correctamente.**

1. Ve a Firebase Console
2. Crea un proyecto (o usa uno existente)
3. Agrega una app Android con el mismo `applicationId` del proyecto
4. Descarga el archivo:

```
google-services.json
```

5. Coloca ese archivo en:

```
app/google-services.json
```

---

## ⚙️ Abrir el proyecto

1. Abre Android Studio
2. Selecciona **"Open Project"**
3. Elige la carpeta del repositorio

---

## 🛠️ Build del proyecto

Una vez abierto:

* Espera a que Gradle sincronice automáticamente
* Si no lo hace, ejecuta:

```
Build > Make Project
```

---

## ▶️ Ejecutar la aplicación

1. Conecta un dispositivo Android o inicia un emulador
2. Presiona:

```
Run ▶
```

o usa:

```
Shift + F10
```

---

## 🔐 Funcionalidades principales

* Registro de usuario con Firebase Authentication
* Inicio de sesión
* Recuperación de contraseña 
* Flujo de onboarding
* Navegación con animaciones

---

## ⚠️ Notas importantes

* El archivo `google-services.json` no está incluido por seguridad
* Asegúrate de habilitar en Firebase:

```
Authentication → Sign-in method → Email/Password
```

---

## 👨‍💻 Tecnologías usadas

* Kotlin
* Jetpack Compose
* Firebase Authentication
* Android SDK

---

## 📌 Estado del proyecto

🚧 En desarrollo — funcionalidades principales implementadas
