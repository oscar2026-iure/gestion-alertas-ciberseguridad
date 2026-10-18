# Sistema de gestión y seguimiento de alertas de ciberseguridad

**Autor:** Oscar J. L. V.  
**Asignatura:** Persistencia de Datos  
**Unidad 1:** Asociación objeto-relacional  
**Estado:** Prototipo académico en desarrollo

## Presentación

Este proyecto nace de mi interés por aplicar los conocimientos de Ingeniería de Software al área de ciberseguridad. Su propósito es organizar el registro de alertas y facilitar la consulta de su estado, severidad y técnico responsable.

Durante el desarrollo comencé con una conexión entre Java y PostgreSQL para guardar y consultar una alerta. Posteriormente incorporé una pantalla que permite realizar las operaciones desde un formulario, sin modificar el código en cada ejecución.

## Necesidad que aborda

El seguimiento de alertas requiere conocer qué se detectó, cuándo se registró, quién está encargado de atenderlo y en qué estado se encuentra.

Como propuesta inicial, el sistema reúne estos datos en una base relacional. Esto permite conservar la información después de cerrar el programa y recuperarla cuando se vuelve a utilizar.

El proyecto es un ejercicio académico y no representa una plataforma institucional oficialmente implementada.

## Funcionalidades

| Operación | Descripción |
|---|---|
| Registrar | Crear una alerta con descripción, severidad, técnico responsable y fecha automática. |
| Consultar | Mostrar las alertas y seleccionar un registro para revisar sus datos. |
| Actualizar | Modificar la descripción, severidad, responsable o estado de una alerta vigente. |
| Anular | Invalidar un registro ingresado por error o duplicidad, conservando la información y el motivo. |

La consulta muestra hasta 200 registros, ordenados desde el más reciente.

## Estados de atención

- **REGISTRADA:** la alerta fue ingresada.
- **EN_ANALISIS:** se está revisando la información.
- **EN_TRATAMIENTO:** se están realizando acciones para atenderla.
- **CERRADA:** se registró la finalización de su atención.

En esta versión, el usuario selecciona el estado. Todavía no se implementan requisitos de evidencia ni un flujo de aprobación del cierre.

## Anulación y conservación de registros

Cerrar una alerta y anular un registro tienen significados diferentes.

Una alerta cerrada terminó su atención y continúa siendo un registro vigente. Un registro anulado fue invalidado, por ejemplo, porque estaba duplicado.

La anulación cambia el campo `activo` a `false` y guarda un motivo. No elimina físicamente la fila de PostgreSQL.

La casilla **Mostrar también registros anulados** permite incluirlos en la consulta. Marcarla no modifica ni reactiva los registros.

## Técnico responsable

El formulario permite seleccionar:

- Oscar Leg.
- Hernán Val.
- Luis Lima.
- Técnico Vacante.

La opción **Técnico Vacante** permite registrar una alerta que todavía no tiene una persona asignada. Este listado es una selección de responsables; no constituye un sistema de usuarios o autenticación.

## Tecnologías utilizadas

| Tecnología | Aplicación en el proyecto |
|---|---|
| Java | Desarrollo de las clases y operaciones. |
| Java Swing | Formulario, botones y tabla de consulta. |
| Jakarta Persistence | Anotaciones e interfaces de persistencia. |
| Hibernate ORM | Mapeo de entidades y operaciones sobre la base de datos. |
| PostgreSQL | Almacenamiento de los registros. |
| Maven | Administración de dependencias y compilación. |
| Apache NetBeans | Entorno de desarrollo. |
| pgAdmin | Verificación de tablas y datos almacenados. |

## Organización del código

El proyecto Maven está dentro de la carpeta `gestion-alertas-publicar`.

Sus archivos principales son:

- `pom.xml`: dependencias y configuración de compilación.
- `src/main/java/ec/monitoreo/alertas/Alerta.java`: entidad que representa los datos de una alerta.
- `src/main/java/ec/monitoreo/alertas/Gestion_alertas.java`: pantalla y operaciones del prototipo.
- `src/main/resources/META-INF/persistence.xml`: configuración de la unidad de persistencia `AlertasPU`.

## Configuración y ejecución

1. Instalar un JDK compatible, Apache NetBeans y PostgreSQL.
2. Descargar el repositorio y descomprimirlo si se descargó como ZIP.
3. En NetBeans, abrir la carpeta `gestion-alertas-publicar`, donde se encuentra `pom.xml`.
4. Crear en PostgreSQL una base de datos llamada `bd_grupociberdefensa`.
5. Editar `persistence.xml` con el usuario, contraseña, servidor y puerto del entorno local.
6. Compilar el proyecto y permitir que Maven descargue las dependencias.
7. Ejecutar la clase `ec.monitoreo.alertas.Gestion_alertas`.

La configuración de compilación utiliza Java 21. La contraseña real de PostgreSQL debe configurarse localmente y no publicarse en el repositorio.

La propiedad `hibernate.hbm2ddl.auto=update` permite crear o ajustar la tabla durante la práctica. No crea la base de datos.

## Uso de la pantalla

1. Pulsar **Nuevo / Limpiar** para preparar el formulario.
2. Ingresar la descripción, seleccionar la severidad y elegir al técnico.
3. Pulsar **Registrar**.
4. Seleccionar una fila para cargar sus datos.
5. Modificar los campos y pulsar **Actualizar**.
6. Para invalidar un registro, pulsar **Anular** e indicar el motivo.
7. Marcar **Mostrar también registros anulados** cuando sea necesario consultarlos.

El botón **Nuevo / Limpiar** solo limpia el formulario; no borra información almacenada.

## Aprendizajes

Con esta práctica aprendí a relacionar una clase Java con una tabla mediante anotaciones como `@Entity`, `@Id` y `@Column`. También trabajé con la configuración de persistencia, el registro y la consulta mediante `EntityManager`, y las transacciones con `commit` y `rollback`.

Una dificultad durante la configuración fue la autenticación con PostgreSQL. Revisar los mensajes de ejecución me permitió identificar el problema y comprobar después el almacenamiento desde pgAdmin.

La interfaz gráfica es una ampliación del ejercicio inicial. Para desarrollarla y resolver dudas utilicé el material de clase, documentación técnica y asistencia de inteligencia artificial. Continúo estudiando los componentes incorporados para comprenderlos y explicarlos.

## Alcance y mejoras pendientes

Esta versión permite demostrar las operaciones CRUD mediante una pantalla y datos de prueba. Conserva los registros anulados, pero todavía no incorpora un historial completo de modificaciones.

Como mejoras posteriores se consideran:

- Historial de seguimiento con fecha, usuario y acción.
- Autenticación y permisos.
- Búsquedas por fecha, severidad, responsable y estado.
- Paginación y reportes.
- Controles de concurrencia.
- Separación de la interfaz y el acceso a datos.
- Ejecución de consultas en segundo plano para mantener la pantalla disponible.

El prototipo requiere estas y otras verificaciones antes de considerar su utilización institucional.

## Documentación consultada

- Material de la Unidad 1 y guía práctica de Persistencia de Datos.
- [Documentación de Hibernate ORM](https://hibernate.org/orm/documentation/)
- [Jakarta Persistence](https://jakarta.ee/specifications/persistence/)
- [Documentación de PostgreSQL](https://www.postgresql.org/docs/)
- [Documentación de Java Swing](https://docs.oracle.com/javase/tutorial/uiswing/)
