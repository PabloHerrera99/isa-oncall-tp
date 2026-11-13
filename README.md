# OnCall · Extensión 6: Gestión de cambios

Trabajo práctico individual de **Ingeniería de Software Aplicada** — Universidad de Mendoza, 2026.

Parte del sistema **OnCall** (guardias e incidentes) generado en clase con JHipster, y lo extiende con la **opción 6 del menú: Gestión de cambios**, resuelta con la **Regla D: ejecución de la cadena de escalamiento**.

- Decisiones de diseño y verificación: [`docs/decisiones.md`](./docs/decisiones.md)
- Instrucciones originales de la cátedra (entorno, regeneración, problemas conocidos): [`docs/README-catedra.md`](./docs/README-catedra.md)

---

## El problema que me tocó

El trabajo parte del sistema OnCall y pide elegir una de diez extensiones; la asignación se hizo por la terminación del DNI. Me tocó la **opción 6: Gestión de cambios**, que se resuelve con la **Regla D**.

**La extensión.** Cada despliegue queda registrado y se puede correlacionar con los incidentes que aparecieron después. Entidades sugeridas: `Cambio`, `DespliegueDeServicio` y `CorrelacionCambioIncidente`.

**La regla D.** Un incidente que nadie reconoce después de N minutos escala al siguiente nivel de su política. Lo que la hace difícil, según la consigna:

- Depende de la regla C: para escalar hay que saber a quién, es decir, quién está de guardia en ese momento.
- Es asincrónica: algo tiene que despertar el proceso (un scheduler, una cola o un evento).
- Tiene que ser idempotente: si el proceso corre dos veces, no se notifica dos veces.
- Tiene estados de corte: reconocido, resuelto, silenciado y escalamiento agotado.
- Hay que decidir qué pasa cuando se acaban los niveles.

**La vuelta de tuerca de la opción 6.** Si un incidente se correlaciona con un cambio reciente, el escalamiento debería ir primero a quien hizo el cambio, no a quien está de guardia.

**Entregables:**

1. `oncall.jh` con las entidades nuevas integradas y relacionadas con el modelo base.
2. El código de la regla, en el backend, con su servicio y sus endpoints.
3. `docs/decisiones.md`: qué se asumió donde la consigna era ambigua y cómo se verificó.
4. Este repositorio, con una historia de commits legible.

---

## Qué agrega

### Modelo (`oncall.jh`)

| Entidad / campo              | Para qué                                                                                                                |
| ---------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| `Cambio`                     | Un cambio sobre producción (código, configuración, infraestructura, base de datos), con su `autor` y su nivel de riesgo |
| `DespliegueDeServicio`       | La llegada concreta de un cambio a un `Servicio`, con versión, entorno, inicio, fin y resultado                         |
| `CorrelacionCambioIncidente` | La sospecha de que un despliegue causó un `Incidente`: automática o manual; sugerida, confirmada o descartada           |
| `EjecucionDeEscalamiento`    | Cada escalón que ejecutó la cadena: auditoría y memoria del proceso (solo lectura desde la API)                         |
| `Incidente.silenciadoHasta`  | Pausa del escalamiento hasta un instante                                                                                |

```
Incidente ──────────────── Servicio            (ya existían)
    │                         │
    │ correlacion             │ despliegue
    ▼                         ▼
CorrelacionCambioIncidente ──► DespliegueDeServicio ──► Cambio ──► User (autor)

Incidente ◄── EjecucionDeEscalamiento ──► PasoEscalamiento, User (destinatario)
```

### Código escrito a mano

Todo en archivos propios, que `jhipster jdl --force` no pisa. Rutas bajo `src/main/java/ar/edu/um/isa/oncall/`:

| Archivo                                    | Qué es                                                                            |
| ------------------------------------------ | --------------------------------------------------------------------------------- |
| `config/ReglasConfiguration.java`          | Reloj (`Clock`) que usan las reglas                                               |
| `repository/GuardiaVigenteRepository.java` | Turnos que cubren un instante, en orden de prioridad                              |
| `service/GuardiaVigenteService.java`       | Quién está de guardia (regla C mínima, la que necesita la D)                      |
| `repository/EscalamientoRepository.java`   | Consultas de la cadena de escalamiento                                            |
| `service/EscalamientoService.java`         | **La regla D**, con el aviso previo al autor del cambio                           |
| `service/EscalamientoScheduler.java`       | Despierta la cadena cada 60 s                                                     |
| `web/rest/GuardiaVigenteResource.java`     | Endpoint de guardia vigente                                                       |
| `web/rest/EscalamientoResource.java`       | Endpoints de escalamiento                                                         |
| `service/dto/*`                            | `GuardiaVigenteDTO`, `EscalonDTO`, `ResultadoEscalamientoDTO`, `EstadoDeCorteDTO` |

---

## Endpoints

Todos requieren autenticación (token JWT).

| Método | Ruta                                                           | Qué hace                                                    |
| ------ | -------------------------------------------------------------- | ----------------------------------------------------------- |
| `GET`  | `/api/guardia-vigente?rotacionId={id}&en={instante}`           | Quién está de guardia. `en` es opcional; por defecto, ahora |
| `POST` | `/api/escalamientos/{incidenteId}/evaluar`                     | Evalúa el incidente ahora, sin esperar al scheduler         |
| `GET`  | `/api/escalamientos/{incidenteId}`                             | Historial de escalones del incidente                        |
| `POST` | `/api/escalamientos/{incidenteId}/reconocer`                   | Reconoce el incidente: corta la cadena                      |
| `POST` | `/api/escalamientos/{incidenteId}/silenciar?minutos={1..1440}` | Pausa la cadena                                             |

`evaluar` responde qué decidió la regla:

| `accion`    | Significado                                                            |
| ----------- | ---------------------------------------------------------------------- |
| `ESCALADO`  | Se ejecutó un escalón: se avisó a alguien o se registró un hueco       |
| `EN_ESPERA` | Todavía no venció la espera del escalón anterior: no se hizo nada      |
| `CORTADO`   | El incidente está reconocido, mitigado, resuelto, cerrado o silenciado |
| `TERMINADO` | La cadena ya terminó (agotada o sin política)                          |

## Configuración

| Propiedad                                         | Por defecto | Qué es                                                               |
| ------------------------------------------------- | ----------- | -------------------------------------------------------------------- |
| `oncall.escalamiento.scheduler-activo`            | `true`      | Prende o apaga el proceso automático                                 |
| `oncall.escalamiento.intervalo-ms`                | `60000`     | Cada cuánto despierta el proceso                                     |
| `oncall.escalamiento.retardo-inicial-ms`          | `30000`     | Cuánto espera después de arrancar la aplicación                      |
| `oncall.escalamiento.ventana-correlacion-minutos` | `120`       | Hasta cuánto antes de la detección se busca un despliegue sospechoso |
| `oncall.escalamiento.espera-autor-minutos`        | `5`         | Cuánto se espera al autor del cambio antes de pasar a la guardia     |

Se pueden pisar con variables de entorno, por ejemplo `ONCALL_ESCALAMIENTO_SCHEDULER_ACTIVO=false`.

---

## Cómo levantarlo

Requisitos: Docker, Git, VS Code con la extensión Dev Containers. Detalle en [`docs/README-catedra.md`](./docs/README-catedra.md).

```bash
git clone https://github.com/PabloHerrera99/isa-oncall-tp.git
cd isa-oncall-tp
code .            # → Reopen in Container
```

Dentro del Dev Container:

```bash
docker compose -f src/main/docker/postgresql.yml up -d
./mvnw            # backend, puerto 8080
npm start         # frontend, puerto 9000 (en otra terminal)
```

Entrar a `http://localhost:9000` con `admin` / `admin`.

Si se modifica `oncall.jh`, regenerar con `jhipster jdl oncall.jh --force` y recrear la base con `docker compose -f src/main/docker/postgresql.yml down -v` antes de volver a levantarla.

---

## Probarlo a mano

**1. Levantar con el scheduler apagado**, para decidir uno cuándo avanza la cadena:

```bash
docker compose -f src/main/docker/postgresql.yml up -d
ONCALL_ESCALAMIENTO_SCHEDULER_ACTIVO=false ONCALL_ESCALAMIENTO_ESPERA_AUTOR_MINUTOS=0 ./mvnw
```

**2. Cargar los datos desde la aplicación** (`http://localhost:9000`, menú _Entidades_):

| Entidad                  | Valores                                                                          |
| ------------------------ | -------------------------------------------------------------------------------- |
| Rotación                 | activa, cualquier equipo                                                         |
| Turno de guardia         | esa rotación, responsable `user`, desde hace una hora hasta mañana               |
| Servicio                 | criticidad `TIER1`, entorno `PRODUCCION`, activo                                 |
| Política de escalamiento | ese servicio, repetir veces `1`                                                  |
| Paso de escalamiento 1   | orden `1`, espera `0`, canal `SMS`, esa política, esa rotación                   |
| Paso de escalamiento 2   | orden `2`, espera `1`, canal `EMAIL`, esa política, destinatario directo `admin` |
| Cambio                   | autor `admin`, creado hace una hora                                              |
| Despliegue de servicio   | ese cambio y ese servicio, iniciado **hace 20 minutos**, resultado `EXITOSO`     |
| Incidente                | estado `ABIERTO`, detectado **ahora**, con el servicio creado arriba             |

**3. Evaluar desde una terminal** (reemplazar `ID` por el id del incidente que muestra la lista):

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/authenticate -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin"}' | jq -r .id_token)
ev() { curl -s -H "Authorization: Bearer $TOKEN" -X POST http://localhost:8080/api/escalamientos/$1/evaluar \
  | jq -c '{accion, escalones: [.escalonesEjecutados[] | {vuelta, nivel, motivo, resultado, a: .destinatarioLogin}]}'; }

ev ID   # nivel 0: AUTOR_DEL_CAMBIO -> admin
ev ID   # nivel 1: GUARDIA -> user
ev ID   # nivel 2: DESTINATARIO_DIRECTO -> admin
ev ID   # EN_ESPERA (idempotencia)
```

Pasado un minuto, seguir evaluando: vuelta 1 (niveles 1 y 2) y, un minuto después, `AGOTADO`. Evaluar otra vez devuelve `TERMINADO` sin registrar nada nuevo.

**4. Cortes:**

```bash
curl -s -H "Authorization: Bearer $TOKEN" -X POST http://localhost:8080/api/escalamientos/ID/reconocer | jq
curl -s -H "Authorization: Bearer $TOKEN" -X POST "http://localhost:8080/api/escalamientos/ID/silenciar?minutos=30" | jq
```

Después de cualquiera de los dos, `ev ID` devuelve `CORTADO`.

**5. Mirar el resultado en la aplicación:**

- _Correlacion Cambio Incidente_: la correlación `AUTOMATICA` / `SUGERIDA` de 20 minutos que creó la regla
- _Ejecucion De Escalamiento_: los escalones
- _Notificacion_: los avisos `PENDIENTE`
- _Evento De Incidente_: la línea de tiempo del incidente

Las capturas de esta prueba están en [`docs/capturas/`](./docs/capturas/).

---

## Estructura del repositorio

```
isa-oncall-tp/
├── .devcontainer/              # entorno de la cátedra: Java 21 + Node 22 + Docker + JHipster 9.2
├── docs/
│   ├── decisiones.md           # decisiones de la extensión y la regla D
│   ├── capturas/               # evidencia de la prueba manual
│   ├── README-catedra.md       # instrucciones originales de la cátedra
│   └── modelo.md               # el modelo base explicado
├── src/                        # aplicación generada + reglas escritas a mano
├── oncall.jh                   # modelo JDL con la extensión
└── README.md
```

## Versiones

|             |       |
| ----------- | ----- |
| JHipster    | 9.2.0 |
| Spring Boot | 4.0.x |
| Java        | 21    |
| Angular     | 21.x  |
| Node        | 22    |
| PostgreSQL  | 17    |
