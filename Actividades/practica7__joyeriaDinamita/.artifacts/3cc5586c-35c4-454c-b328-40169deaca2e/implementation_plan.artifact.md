# Dos Pantallas: Catálogo y Detalles con Comentarios

Crear una aplicación con dos pantallas usando Jetpack Compose. La primera pantalla muestra un catálogo de productos en una cuadrícula y la segunda permite ver detalles y agregar comentarios que se apilan.

## User Review Required

> [!NOTE]
> La navegación se realizará pasando el ID del producto entre pantallas. Se utilizará un estado local (`rememberSaveable`) para mantener la lista de comentarios en la segunda pantalla durante la sesión de la actividad.

## Proposed Changes

### [UI Components]

#### [MODIFY] [MainActivity.kt](file:///C:/Users/lorea/AndroidStudioProjects/JoyeriaDinamita/app/src/main/java/com/example/joyeriadinamita/MainActivity.kt)
Reemplazar el contenido para incluir la lógica de navegación y las dos pantallas solicitadas.

#### [NEW] [ProductModels.kt](file:///C:/Users/lorea/AndroidStudioProjects/JoyeriaDinamita/app/src/main/java/com/example/joyeriadinamita/ProductModels.kt)
Definir las clases de datos `Product` y `Comment`.

## Verification Plan

### Automated Tests
- No se requieren pruebas automatizadas específicas por ahora, se verificará visualmente.

### Manual Verification
1. Abrir la aplicación y verificar que aparezca la cuadrícula de productos (2 por fila).
2. Hacer clic en una tarjeta y verificar la transición a la pantalla de detalles.
3. En la pantalla de detalles, ingresar texto en los dos campos y presionar el botón azul.
4. Verificar que los comentarios aparezcan abajo y se apilen correctamente (el más nuevo arriba).
5. Regresar a la pantalla principal y probar con otro producto.
