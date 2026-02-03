Actúa como un desarrollador senior Android especializado en Kotlin, arquitectura limpia y diseño moderno.
Quiero desarrollar una aplicación Android de fichaje personal (control horario) para uso individual, 
con un enfoque totalmente personalizable, intuitivo y visualmente moderno.

🧱 TECNOLOGÍAS Y BASE:
- Lenguaje: Kotlin
- UI: Jetpack Compose (Material 3)
- Arquitectura: MVVM + Clean Architecture
- Persistencia: Room (base de datos local)
- Gestión de estado: ViewModel + StateFlow
- Fechas y horas: java.time
- Navegación: Navigation Compose
- Gráficas: librería moderna compatible con Compose (ej. MPAndroidChart o similar adaptado)

🎯 OBJETIVO GENERAL:
Crear una app que permita fichar entradas y salidas, gestionar horarios personalizados y mostrar 
estadísticas de horas trabajadas de forma diaria, semanal y mensual, con reinicios automáticos 
según la configuración del usuario.

🕒 FUNCIONALIDADES CLAVE:
1. Sistema de fichaje:
   - Botón de "Fichar entrada"
   - Botón de "Fichar salida"
   - Registro automático de fecha y hora
   - Control de estado (si estás fichado o no)
   - Evitar fichajes inconsistentes (salida sin entrada, doble entrada, etc.)

2. Horarios personalizables:
   - Definir horario semanal (por días)
   - Posibilidad de múltiples turnos
   - Horarios flexibles o fijos
   - Excepciones (festivos, días libres)

3. Estadísticas avanzadas:
   - Horas trabajadas hoy
   - Horas de la semana actual
   - Horas del mes actual
   - Comparación con el horario esperado
   - Gráficas claras y visuales
   - Resúmenes automáticos

4. Ciclos de reinicio configurables:
   - Reinicio semanal (ej. lunes)
   - Reinicio mensual (primer día del mes)
   - Ciclos personalizados definidos por el usuario
   - Historial guardado de ciclos anteriores

5. Calendario integrado:
   - Vista mensual con días trabajados
   - Visualización de entradas/salidas por día
   - Colores según cumplimiento de horario
   - Acceso rápido al detalle diario

6. Base de datos personal:
   - Entidades bien definidas (Fichaje, Jornada, Horario, Ciclo, Estadísticas)
   - Migraciones preparadas
   - Repositorios desacoplados
   - Datos 100% locales (sin backend)

🎨 DISEÑO Y UX:
- Estilo moderno y minimalista
- Material You (dinámico)
- Modo oscuro y claro
- Animaciones suaves
- UI clara y sin sobrecarga visual
- Pantalla principal tipo “dashboard”
- Feedback visual al fichar

📁 ESTRUCTURA DEL PROYECTO:
- data (Room, DAOs, entities)
- domain (modelos, casos de uso)
- ui (pantallas, componentes Compose)
- viewmodel
- navigation
- utils (fechas, cálculos de horas)

🧪 CALIDAD Y BUENAS PRÁCTICAS:
- Código limpio y bien comentado
- Evitar lógica en la UI
- Funciones reutilizables
- Preparar el proyecto para futuras ampliaciones
- Tests unitarios en ViewModels y lógica de dominio

📌 COMIENZA:
- Crea primero la estructura base del proyecto
- Luego las entidades de Room
- Después el sistema de fichaje
- Continúa con estadísticas y calendario
- Finaliza con detalles de diseño y UX
