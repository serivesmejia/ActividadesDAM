📝 Descripción breve
RoverAcción es una aplicación móvil para Android diseñada para la comunidad Scout (sección Rover) que facilita la creación, gestión y colaboración en proyectos de impacto social orientados a los Objetivos de Desarrollo Sostenible (ODS) de la ONU. La plataforma permite a los jóvenes Rovers estructurar sus iniciativas comunitarias y conectar con otros miembros para sumarse como voluntarios.

👥 Integrantes
Dafne Pineda
Citlaly Zeferino
Sebastian Erives

🛠️ Tecnologías utilizadas
•Lenguaje: Kotlin
•Interfaz de Usuario (UI): Jetpack Compose con Material Design 3 (Material3)
•Base de Datos & Backend: Firebase Firestore (Base de datos NoSQL en tiempo real)
•Persistencia de Sesión: SharedPreferences (SessionManager)
•Programación Asíncrona: Kotlin Coroutines, StateFlow y callbackFlow
•Notificaciones: Android NotificationManagerCompat (Canales de notificación y permisos para Android 13+)
•Gestión del Proyecto: Gradle (Kotlin DSL) con Firebase BoM

⭐ Principales funcionalidades
1.Autenticación y Registro Scout:
◦Registro de usuarios con validaciones de formulario (correo, contraseña, usuario) y verificación de la CUM (Clave Única de Membresía Scout).
◦Verificación en tiempo real para evitar duplicados de CUM, correo o usuario.
◦Inicio de sesión con persistencia de datos ("Recordarme").
2.Gestión Integral de Proyectos (CRUD):
◦Creación y Edición: Los creadores pueden publicar, modificar o eliminar proyectos detallando su ODS, objetivo general y específicos, antecedentes, justificación, alcance, recursos y resultados esperados.
◦Visualización Dinámica: Vista detallada del proyecto organizada en secciones desplegables/colapsables.
3.Sistema de Voluntariado:
◦Exploración global de proyectos comunitarios.
◦Opción para unirse o retirarse como voluntario de un proyecto con un solo toque.
◦Contador de voluntarios en tiempo real por proyecto.
4.Notificaciones del Sistema:
◦Alertas locales en tiempo real para el responsable del proyecto cuando un nuevo voluntario se suma a su iniciativa.
5.Perfil de Usuario:
◦Edición de datos personales (nombre, usuario, correo, teléfono).
◦Organización por pestañas entre Proyectos propios y Proyectos de voluntariado.
6.Personalización de Interfaz (UX):
◦Alternancia entre Modo Claro y Modo Oscuro (Dark Theme).

Carpeta con los videos:
https://utmedu-my.sharepoint.com/:f:/g/personal/al07077540_tecmilenio_mx/IgBgxrhKBub7QaZv6GNzBK-xATYRv-ws99PRgN2m1NnmGKI?e=S2bIQA


Landing page:
https://app-roveraccion.vercel.app/
