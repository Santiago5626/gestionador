# Gestionador - Gestor de Activos y Préstamos

Una aplicación Android moderna para la gestión de clientes, préstamos y activos financieros, desarrollada en Kotlin con Firebase como backend.

## 📱 Características Principales

### Gestión de Clientes
- ✅ Crear, editar y eliminar clientes
- ✅ Lista de clientes con función de búsqueda
- ✅ Detalles completos del cliente (cédula, nombre, dirección, teléfono)
- ✅ Vista de préstamos asociados a cada cliente

### Gestión de Préstamos
- ✅ Crear préstamos con diferentes tipos (diario, semanal, mensual)
- ✅ Seguimiento de cuotas y pagos
- ✅ Cálculo automático de intereses (para préstamos mensuales)
- ✅ Historial de pagos y saldo actual
- 🔄 Generación de PDF estilo "cartón de pagos" (próximamente)

### Gestión de Activos
- ✅ Registro de ingresos, gastos e inversiones
- ✅ Categorización automática de movimientos
- ✅ Cálculo de balance total en tiempo real
- 🔄 Estadísticas y reportes visuales (próximamente)

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **Plataforma**: Android (mínimo API 26 - Android 8.0)
- **Arquitectura**: MVVM con ViewModels y StateFlow
- **Base de datos**: Firebase Firestore
- **Autenticación**: Firebase Authentication
- **UI**: Material Design Components
- **Navegación**: Navigation Component
- **Binding**: View Binding

## 📋 Dependencias Principales

```gradle
// Firebase
implementation 'com.google.firebase:firebase-firestore-ktx'
implementation 'com.google.firebase:firebase-auth-ktx'

// Navigation
implementation 'androidx.navigation:navigation-fragment-ktx'
implementation 'androidx.navigation:navigation-ui-ktx'

// Material Design
implementation 'com.google.android.material:material'

// Lifecycle & ViewModel
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android'
```

## 🏗️ Estructura del Proyecto

```
app/src/main/
├── java/com/gestionador/
│   ├── data/
│   │   ├── models/          # Modelos de datos (Cliente, Prestamo, Activo, Abono)
│   │   └── repository/      # Repositorio Firebase
│   ├── ui/
│   │   ├── client/         # Fragmentos y ViewModels de clientes
│   │   ├── loan/           # Fragmentos y ViewModels de préstamos
│   │   └── asset/          # Fragmentos y ViewModels de activos
│   └── MainActivity.kt
└── res/
    ├── layout/             # Layouts XML
    ├── navigation/         # Gráficos de navegación
    ├── menu/              # Menús de navegación
    └── values/            # Colores, strings, temas
```

## 🚀 Configuración del Proyecto

### 1. Prerrequisitos
- Android Studio Arctic Fox o superior
- JDK 8 o superior
- Cuenta de Firebase

### 2. Configuración de Firebase
1. Crear un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Agregar una aplicación Android con el package name `com.gestionador`
3. Descargar el archivo `google-services.json`
4. Colocar el archivo en la carpeta `app/`
5. Habilitar Firestore Database y Authentication en la consola

### 3. Instalación
1. Clonar el repositorio
2. Abrir el proyecto en Android Studio
3. Agregar el archivo `google-services.json` de Firebase
4. Sincronizar el proyecto con Gradle
5. Ejecutar la aplicación

## 📊 Modelos de Datos

### Cliente
```kotlin
data class Cliente(
    val id: String = "",
    val cedula: String = "",
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = ""
)
```

### Préstamo
```kotlin
data class Prestamo(
    val id: String = "",
    val clienteId: String = "",
    val tipo: TipoPrestamo = TipoPrestamo.DIARIO,
    val fechaInicial: Long = 0L,
    val numeroCuota: Int = 0,
    val valorCuotaPactada: Double = 0.0,
    val porcentajeInteres: Double = 0.0,
    val montoTotal: Double = 0.0,
    val saldoRestante: Double = 0.0,
    val estado: EstadoPrestamo = EstadoPrestamo.ACTIVO
)
```

### Activo
```kotlin
data class Activo(
    val id: String = "",
    val fecha: Long = 0L,
    val montoIngresado: Double = 0.0,
    val descripcion: String = "",
    val categoria: CategoriaActivo = CategoriaActivo.INGRESO
)
```

## 🔐 Permisos Requeridos

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

## 🎨 Características de UI/UX

- **Material Design 3**: Interfaz moderna y consistente
- **Bottom Navigation**: Navegación intuitiva entre módulos
- **Cards y Chips**: Presentación clara de información
- **FloatingActionButton**: Acceso rápido a funciones de creación
- **Estados de carga**: Feedback visual durante operaciones
- **Validación de formularios**: Entrada de datos segura

## 🔄 Funcionalidades Futuras

- [ ] Autenticación completa con Firebase Auth
- [ ] Generación de PDFs para reportes de préstamos
- [ ] Gráficas y estadísticas avanzadas
- [ ] Notificaciones push para recordatorios de pagos
- [ ] Backup automático de datos
- [ ] Exportación de datos a Excel/CSV
- [ ] Modo oscuro
- [ ] Soporte para múltiples monedas

## 🤝 Contribución

Las contribuciones son bienvenidas. Para cambios importantes:

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Contacto

Para preguntas o sugerencias sobre el proyecto, puedes contactar al equipo de desarrollo.

---

**Nota**: Este proyecto está en desarrollo activo. Algunas funcionalidades pueden estar en proceso de implementación.
