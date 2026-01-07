# Sistema de Gestión de Garantías

Sistema de escritorio desarrollado en JavaFX para la gestión de garantías de productos vendidos, integrado con Odoo ERP y MongoDB.

## 📋 Descripción

Esta aplicación permite gestionar las garantías de productos vendidos a través de facturas registradas en Odoo. El sistema sincroniza automáticamente las facturas de venta desde Odoo y permite crear, consultar y administrar garantías almacenadas en MongoDB.

**Nota:** En este proyecto los productos serán del mundo del ciclismo (bicicletas, cascos, ruedas, componentes, etc.). Todas las referencias, datos de ejemplo y vistas iniciales estarán orientadas a productos de ciclismo.

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 24 | Lenguaje principal |
| **JavaFX** | 21 | Interfaz gráfica de usuario |
| **Odoo** | 17+ | ERP origen de facturas |
| **MongoDB** | 6.0+ | Base de datos para garantías |
| **Maven** | 3.9+ | Gestión de dependencias y build |
| **Gson** | 2.10+ | Procesamiento JSON |

## 📁 Estructura del Proyecto

```
src/main/java/com/example/garantias/
├── AplicacionGarantias.java      # Punto de entrada principal
├── Launcher.java                  # Lanzador de la aplicación
│
├── controller/
│   ├── ControladorLogin.java      # Controlador de autenticación
│   └── ControladorPrincipal.java  # Controlador de vista principal
│
├── model/
│   ├── Factura.java               # Modelo de factura de Odoo
│   ├── LineaFactura.java          # Líneas/productos de factura
│   ├── Garantia.java              # Modelo de garantía
│   └── SesionOdoo.java            # Gestión de sesión Odoo
│
└── service/
    ├── ServicioOdoo.java          # Comunicación con API Odoo
    └── ServicioMongoDB.java       # Operaciones con MongoDB

src/main/resources/com/example/garantias/view/
├── vista-login.fxml               # Vista de inicio de sesión
└── vista-principal.fxml           # Vista principal con pestañas
```

## 🔄 Flujo de la Aplicación

### 1. Inicio de Sesión

```
┌─────────────────────────────────────────────────────────────┐
│                    VISTA DE LOGIN                           │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  Usuario:     [___________________]                  │   │
│  │  Contraseña:  [___________________]                  │   │
│  │                                                      │   │
│  │           [ Iniciar Sesión ]                         │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
            ┌───────────────────────────────┐
            │  1. Conectar a MongoDB        │
            │  2. Autenticar en Odoo        │
            │  3. Guardar sesión            │
            └───────────────────────────────┘
```

**Proceso:**
1. Al abrir la aplicación, se conecta automáticamente a MongoDB
2. El usuario ingresa credenciales de Odoo (usuario y contraseña)
3. La aplicación autentica via JSON-RPC contra Odoo
4. Se almacena la sesión (cookies) para mantener la conexión
5. Si es exitoso, se abre la vista principal

### 2. Vista Principal - Pestaña Facturas

```
┌─────────────────────────────────────────────────────────────────────┐
│  Sistema de Garantías     Usuario: admin | BD: odoo  [Cerrar Sesión]│
├─────────────────────────────────────────────────────────────────────┤
│  [Facturas] [Garantías] [Estadísticas]                              │
├─────────────────────────────────────────────────────────────────────┤
│  [Actualizar Facturas]                                              │
├────────────────────────────────┬────────────────────────────────────┤
│     FACTURAS DE VENTA          │      LÍNEAS DE FACTURA             │
│  ┌──────────────────────────┐  │  ┌──────────────────────────────┐  │
│  │ Número | Cliente | Total │  │  │ Producto | Cant | Garantía   │  │
│  ├──────────────────────────┤  │  ├──────────────────────────────┤  │
│  │ INV001 | Cliente1| 100€  │◄─┼──│ Laptop   |  1   | [✓ Creada] │  │
│  │ INV002 | Cliente2| 250€  │  │  │ Mouse    |  2   | [✓ Creada] │  │
│  │ INV003 | Cliente3| 500€  │  │  │ Teclado  |  1   | [✓ Creada] │  │
│  └──────────────────────────┘  │  └──────────────────────────────┘  │
└────────────────────────────────┴────────────────────────────────────┘
```

**Proceso:**
1. Al hacer clic en "Actualizar Facturas":
   - Se consultan facturas de venta (`out_invoice`) desde Odoo
   - Para cada factura se cargan sus líneas (productos)
   - **Automáticamente** se crean garantías de 12 meses para cada producto
2. Al seleccionar una factura, se muestran sus líneas en el panel derecho
3. Cada línea muestra si ya tiene garantía creada

### 3. Vista Principal - Pestaña Garantías

```
┌─────────────────────────────────────────────────────────────────────┐
│  [Facturas] [Garantías] [Estadísticas]                              │
├─────────────────────────────────────────────────────────────────────┤
│  [Actualizar]  Buscar: [____________]  Estado: [Todas ▼]            │
├─────────────────────────────────────────────────────────────────────┤
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │ Producto  │ Cliente  │ Inicio    │ Fin       │ Estado │ Días │  │
│  ├───────────────────────────────────────────────────────────────┤  │
│  │ Laptop    │ Cliente1 │ 01/01/26  │ 01/01/27  │ Activa │ 359  │  │
│  │ Mouse     │ Cliente2 │ 15/12/25  │ 15/12/26  │ Activa │ 342  │  │
│  │ Impresora │ Cliente3 │ 01/06/25  │ 01/06/26  │ Por Exp│  15  │  │
│  │ Monitor   │ Cliente4 │ 01/01/25  │ 01/01/26  │ Expirada│  0  │  │
│  └───────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

**Funcionalidades:**
- **Buscar**: Filtrar por producto, cliente o número de serie
- **Filtrar por estado**: Todas, Activas, Por Expirar, Expiradas
- **Ver detalles**: Información completa de la garantía
- **Eliminar**: Borrar garantía de MongoDB

**Estados de Garantía:**
| Estado | Descripción | Condición |
|--------|-------------|-----------|
| 🟢 **Activa** | Garantía vigente | Más de 30 días restantes |
| 🟡 **Por Expirar** | Próxima a vencer | Menos de 30 días restantes |
| 🔴 **Expirada** | Garantía vencida | Fecha fin pasada |

### 4. Vista Principal - Pestaña Estadísticas

```
┌─────────────────────────────────────────────────────────────────────┐
│  [Facturas] [Garantías] [Estadísticas]                              │
├─────────────────────────────────────────────────────────────────────┤
│  [Actualizar Estadísticas]  Período: [Todo ▼]                       │
├─────────────────────────────────────────────────────────────────────┤
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │Total Vent│  │ Facturas │  │Gar.Activ │  │Gar.Expir │            │
│  │ 5,000€   │  │    25    │  │    18    │  │    7     │            │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘            │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────┐  ┌─────────────────────────┐          │
│  │   VENTAS POR CLIENTE    │  │  ESTADO DE GARANTÍAS    │          │
│  │   ████████              │  │      ┌────┐             │          │
│  │   ████                  │  │     /      \            │          │
│  │   ██████████            │  │    │Activas│            │          │
│  │   [Gráfico de Barras]   │  │     \______/            │          │
│  └─────────────────────────┘  └─────────────────────────┘          │
│  ┌───────────────────────────────────────────────────────┐         │
│  │              VENTAS MENSUALES                         │         │
│  │         ___/\___                                      │         │
│  │    ___/        \___                                   │         │
│  │   [Gráfico de Líneas]                                 │         │
│  └───────────────────────────────────────────────────────┘         │
└─────────────────────────────────────────────────────────────────────┘
```

**Gráficos disponibles:**
1. **Ventas por Cliente** (Gráfico de Barras): Top clientes por monto
2. **Estado de Garantías** (Gráfico Circular): Distribución Activas/Por Expirar/Expiradas
3. **Ventas Mensuales** (Gráfico de Líneas): Tendencia de ventas en el tiempo

## 🔌 Integración con Sistemas

### Comunicación con Odoo (JSON-RPC)

```
┌─────────────┐         JSON-RPC          ┌─────────────┐
│  JavaFX     │ ◄──────────────────────► │   Odoo      │
│  App        │    /web/session/auth      │   ERP       │
│             │    /web/dataset/call_kw   │             │
└─────────────┘                           └─────────────┘
```

**Endpoints utilizados:**
- `/web/session/authenticate` - Autenticación
- `/web/dataset/call_kw/account.move/search` - Buscar facturas
- `/web/dataset/call_kw/account.move/read` - Leer facturas
- `/web/dataset/call_kw/account.move.line/read` - Leer líneas

### Comunicación con MongoDB

```
┌─────────────┐        MongoDB Driver     ┌─────────────┐
│  JavaFX     │ ◄──────────────────────► │  MongoDB    │
│  App        │     garantias_db          │  Server     │
│             │     warranties            │             │
└─────────────┘                           └─────────────┘
```

**Colección `warranties`:**
```json
{
  "_id": ObjectId,
  "facturaId": 123,
  "nombreFactura": "INV/2026/0001",
  "lineaFacturaId": 456,
  "productoId": 789,
  "nombreProducto": "Laptop HP",
  "nombreCliente": "Empresa S.L.",
  "fechaCompra": ISODate,
  "fechaInicioGarantia": ISODate,
  "fechaFinGarantia": ISODate,
  "mesesGarantia": 12,
  "estado": "ACTIVA",
  "notas": "...",
  "numeroSerie": "SN123456"
}
```

## ⚙️ Configuración

### Requisitos Previos

1. **Java 24** instalado
2. **Odoo 17+** ejecutándose en `http://localhost:8069`
3. **MongoDB** ejecutándose en `localhost:27017`

### Configuración por Defecto

| Parámetro | Valor | Archivo |
|-----------|-------|---------|
| URL Odoo | `http://localhost:8069` | `ControladorLogin.java` |
| Base datos Odoo | `odoo` | `ControladorLogin.java` |
| MongoDB URI | `mongodb://admin:admin_password@localhost:27017` | `ServicioMongoDB.java` |
| Base datos MongoDB | `garantias_db` | `ServicioMongoDB.java` |
| Meses garantía | `12` | `ControladorPrincipal.java` |
| Productos | `Ciclismo` | `README.md` |

### Docker (Servicios)

El proyecto trae un `docker-compose.yml` con servicios para **Odoo (v17)**, **PostgreSQL** y **MongoDB** (más una interfaz opcional `mongo-express`). A continuación el ejemplo de configuración incluido:

```yaml
services:
  # Base de datos PostgreSQL para Odoo
  db:
    image: postgres:15
    container_name: odoo_db
    environment:
      POSTGRES_DB: postgres
      POSTGRES_USER: odoo
      POSTGRES_PASSWORD: odoo_password
    volumes:
      - odoo_db_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    restart: unless-stopped

  # Servidor Odoo Community
  odoo:
    image: odoo:17.0
    container_name: odoo_server
    depends_on:
      - db
    environment:
      HOST: db
      USER: odoo
      PASSWORD: odoo_password
    ports:
      - "8069:8069"
    volumes:
      - odoo_data:/var/lib/odoo
      - odoo_addons:/mnt/extra-addons
    restart: unless-stopped

  # MongoDB para almacenar garantías
  mongodb:
    image: mongo:7.0
    container_name: garantias_mongodb
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: admin_password
      MONGO_INITDB_DATABASE: garantias_db
    volumes:
      - mongodb_data:/data/db
    ports:
      - "27017:27017"
    restart: unless-stopped

  # Mongo Express - Interfaz web para MongoDB (opcional)
  mongo-express:
    image: mongo-express:1.0.2
    container_name: mongo_express
    depends_on:
      - mongodb
    environment:
      ME_CONFIG_MONGODB_ADMINUSERNAME: admin
      ME_CONFIG_MONGODB_ADMINPASSWORD: admin_password
      ME_CONFIG_MONGODB_URL: mongodb://admin:admin_password@mongodb:27017/
      ME_CONFIG_BASICAUTH: false
    ports:
      - "8081:8081"
    restart: unless-stopped
```

Instrucciones rápidas:

1. Levanta los contenedores:

```bash
docker-compose down ; docker-compose up -d
```

2. **Odoo** estará disponible en `http://localhost:8069` (la primera vez hará la instalación guiada). Para crear la base llamada `garantias` rellena el formulario con:
   - Master Password: `admin`
   - Database Name: `garantias`
   - Email: `admin@example.com`
   - Password: `admin`
   - Marca **Demo Data** si quieres datos de ejemplo (recomendado para pruebas)

3. **Postgres** escucha en `localhost:5432` (usuario `odoo` / `odoo_password`).
4. **MongoDB** en `mongodb://admin:admin_password@localhost:27017` y **Mongo Express** en `http://localhost:8081`.
5. Si cambias credenciales, actualiza `src/main/resources/application.properties` o las referencias en código.

## 🚀 Ejecución

### Iniciar servicios

```bash
# Iniciar MongoDB con Docker
docker-compose up -d

# O iniciar contenedor existente
docker start garantias_mongodb
```

### Compilar y ejecutar

```bash
# Compilar
.\mvnw.cmd compile

# Ejecutar
.\mvnw.cmd javafx:run
```

## 📊 Modelo de Datos

### Clase Garantia

```java
public class Garantia {
    private ObjectId id;           // ID MongoDB
    private int facturaId;         // ID factura en Odoo
    private String nombreFactura;  // Ej: "INV/2026/0001"
    private int lineaFacturaId;    // ID línea en Odoo
    private int productoId;        // ID producto en Odoo
    private String nombreProducto; // Nombre del producto
    private String nombreCliente;  // Cliente de la factura
    private LocalDate fechaCompra; // Fecha de la factura
    private LocalDate fechaInicioGarantia;
    private LocalDate fechaFinGarantia;
    private int mesesGarantia;     // Duración (default: 12)
    private String estado;         // ACTIVA, POR_EXPIRAR, EXPIRADA
    private String notas;
    private String numeroSerie;
}
```

## 🔐 Gestión de Sesión

La aplicación mantiene la sesión de Odoo usando:
- **CookieManager**: Almacena cookies de sesión HTTP
- **Reautenticación automática**: Si la sesión expira, se reautentica
- **Singleton SesionOdoo**: Mantiene datos de usuario activo

---

## 🔄 Flujo de Datos Detallado

### Diagrama General del Flujo

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              FLUJO DE DATOS                                  │
└─────────────────────────────────────────────────────────────────────────────┘

     ┌──────────┐                                           ┌──────────┐
     │  ODOO    │                                           │ MongoDB  │
     │  (ERP)   │                                           │  (NoSQL) │
     └────┬─────┘                                           └────┬─────┘
          │                                                      │
          │ JSON-RPC                                             │ Driver
          │ (HTTP POST)                                          │ MongoDB
          │                                                      │
          ▼                                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           CAPA DE SERVICIOS                                  │
│  ┌─────────────────────────────┐    ┌─────────────────────────────────────┐ │
│  │      ServicioOdoo           │    │        ServicioMongoDB              │ │
│  │  ─────────────────────────  │    │  ─────────────────────────────────  │ │
│  │  • autenticar()             │    │  • conectar()                       │ │
│  │  • obtenerFacturas()        │    │  • guardarGarantía()                │ │
│  │  • obtenerLineasFactura()   │    │  • obtenerGarantías()               │ │
│  │  • reautenticar()           │    │  • eliminarGarantía()               │ │
│  └─────────────────────────────┘    │  • contarGarantíasActivas()         │ │
│                                      └─────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
          │                                                      │
          │                                                      │
          ▼                                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CAPA DE MODELOS                                 │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐  ┌──────────────┐  │
│  │   Factura     │  │ LineaFactura  │  │   Garantía    │  │ SesionOdoo   │  │
│  │  ───────────  │  │  ───────────  │  │  ───────────  │  │ ───────────  │  │
│  │  id           │  │  id           │  │  id (ObjectId)│  │ url          │  │
│  │  nombre       │  │  productoId   │  │  facturaId    │  │ baseDatos    │  │
│  │  cliente      │  │  nombre       │  │  productoId   │  │ usuarioId    │  │
│  │  fecha        │  │  cantidad     │  │  fechaInicio  │  │ contraseña   │  │
│  │  montoTotal   │  │  precio       │  │  fechaFin     │  │              │  │
│  │  líneas[]     │  │  facturaId    │  │  estado       │  │              │  │
│  └───────────────┘  └───────────────┘  └───────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
          │                                                      │
          │                                                      │
          ▼                                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           CAPA DE CONTROLADORES                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │                      ControladorPrincipal                               ││
│  │  • Recibe eventos de UI (clics, selecciones)                            ││
│  │  • Coordina llamadas a servicios                                        ││
│  │  • Transforma datos entre Odoo y MongoDB                                ││
│  │  • Actualiza las vistas (tablas, gráficos)                              ││
│  └─────────────────────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────────────────────┐│
│  │                      ControladorLogin                                   ││
│  │  • Gestiona autenticación                                               ││
│  │  • Valida credenciales                                                  ││
│  │  • Inicia sesión en SesionOdoo                                          ││
│  └─────────────────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CAPA DE VISTAS (FXML)                           │
│  ┌───────────────────────────────┐  ┌───────────────────────────────────┐   │
│  │      vista-login.fxml         │  │      vista-principal.fxml         │   │
│  │  • Campo usuario              │  │  • TabPane (3 pestañas)           │   │
│  │  • Campo contraseña           │  │  • Tablas de facturas/garantías   │   │
│  │  • Botón iniciar sesión       │  │  • Gráficos de estadísticas       │   │
│  └───────────────────────────────┘  └───────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

### Flujo 1: Autenticación

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         FLUJO DE AUTENTICACIÓN                            │
└──────────────────────────────────────────────────────────────────────────┘

  USUARIO              CONTROLADOR           SERVICIO              ODOO
     │                     │                    │                    │
     │  1. Ingresa         │                    │                    │
     │  usuario/contraseña │                    │                    │
     │────────────────────►│                    │                    │
     │                     │                    │                    │
     │                     │ 2. autenticar()    │                    │
     │                     │───────────────────►│                    │
     │                     │                    │                    │
     │                     │                    │ 3. POST JSON-RPC   │
     │                     │                    │ /web/session/auth  │
     │                     │                    │───────────────────►│
     │                     │                    │                    │
     │                     │                    │ 4. {uid, cookies}  │
     │                     │                    │◄───────────────────│
     │                     │                    │                    │
     │                     │ 5. uid + sesión    │                    │
     │                     │◄───────────────────│                    │
     │                     │                    │                    │
     │                     │ 6. Guardar en      │                    │
     │                     │    SesionOdoo      │                    │
     │                     │    (Singleton)     │                    │
     │                     │                    │                    │
     │ 7. Abrir vista      │                    │                    │
     │    principal        │                    │                    │
     │◄────────────────────│                    │                    │
     │                     │                    │                    │

```

**Datos que fluyen:**
1. `usuario`, `contraseña` → desde UI al controlador
2. `url`, `baseDatos`, `usuario`, `contraseña` → al servicio
3. JSON-RPC con `{db, login, password}` → a Odoo
4. `{uid, session_id, cookies}` → desde Odoo
5. `SesionOdoo` almacena: url, baseDatos, usuarioId, nombreUsuario, contraseña

---

### Flujo 2: Carga de Facturas y Creación de Garantías

```
┌──────────────────────────────────────────────────────────────────────────┐
│              FLUJO DE CARGA DE FACTURAS + CREACIÓN DE GARANTÍAS           │
└──────────────────────────────────────────────────────────────────────────┘

  USUARIO         CONTROLADOR         ODOO SERVICE       MONGODB SERVICE
     │                 │                    │                    │
     │ 1. Click        │                    │                    │
     │ "Actualizar"    │                    │                    │
     │────────────────►│                    │                    │
     │                 │                    │                    │
     │                 │ 2. obtenerFacturas()                    │
     │                 │───────────────────►│                    │
     │                 │                    │                    │
     │                 │                    │ 3. search()        │
     │                 │                    │ account.move       │
     │                 │                    │ [move_type=out_inv]│
     │                 │                    │───────► ODOO       │
     │                 │                    │◄─────── [ids]      │
     │                 │                    │                    │
     │                 │                    │ 4. read()          │
     │                 │                    │ account.move       │
     │                 │                    │ [name, partner,...]│
     │                 │                    │───────► ODOO       │
     │                 │                    │◄─────── [facturas] │
     │                 │                    │                    │
     │                 │                    │ 5. read()          │
     │                 │                    │ account.move.line  │
     │                 │                    │ [product, qty,...] │
     │                 │                    │───────► ODOO       │
     │                 │                    │◄─────── [líneas]   │
     │                 │                    │                    │
     │                 │ 6. List<Factura>   │                    │
     │                 │    con líneas      │                    │
     │                 │◄───────────────────│                    │
     │                 │                    │                    │
     │                 │ 7. Para cada línea:                     │
     │                 │    ¿existe garantía?                    │
     │                 │────────────────────────────────────────►│
     │                 │                    │                    │
     │                 │                    │    8. Buscar en    │
     │                 │                    │    warranties por  │
     │                 │                    │    lineaFacturaId  │
     │                 │                    │                    │
     │                 │◄────────────────────────────────────────│
     │                 │                    │    9. null/Garantía│
     │                 │                    │                    │
     │                 │ 10. Si no existe:  │                    │
     │                 │     crear Garantía │                    │
     │                 │     (12 meses)     │                    │
     │                 │────────────────────────────────────────►│
     │                 │                    │                    │
     │                 │                    │   11. Insert en    │
     │                 │                    │   MongoDB          │
     │                 │                    │                    │
     │ 12. Actualizar  │                    │                    │
     │     tablas UI   │                    │                    │
     │◄────────────────│                    │                    │
     │                 │                    │                    │

```

**Transformación de datos Odoo → MongoDB:**

```
ODOO (account.move)                    MONGODB (warranties)
────────────────────                   ────────────────────
id: 123                        ───►    facturaId: 123
name: "INV/2026/0001"          ───►    nombreFactura: "INV/2026/0001"
partner_id: [1, "Cliente S.L."]───►    nombreCliente: "Cliente S.L."
invoice_date: "2026-01-07"     ───►    fechaCompra: ISODate(...)
                                       fechaInicioGarantia: ISODate(...)
                                       fechaFinGarantia: ISODate(...+12 meses)

ODOO (account.move.line)
────────────────────────
id: 456                        ───►    lineaFacturaId: 456
product_id: [10, "Laptop HP"]  ───►    productoId: 10
                                       nombreProducto: "Laptop HP"
quantity: 1.0                          (usado para validar)
price_total: 500.00                    (no se guarda)
```

---

### Flujo 3: Consulta de Garantías

```
┌──────────────────────────────────────────────────────────────────────────┐
│                      FLUJO DE CONSULTA DE GARANTÍAS                       │
└──────────────────────────────────────────────────────────────────────────┘

  USUARIO              CONTROLADOR              MONGODB
     │                      │                      │
     │ 1. Seleccionar       │                      │
     │    pestaña Garantías │                      │
     │─────────────────────►│                      │
     │                      │                      │
     │                      │ 2. obtenerTodas()    │
     │                      │─────────────────────►│
     │                      │                      │
     │                      │                      │ 3. db.warranties
     │                      │                      │    .find({})
     │                      │                      │
     │                      │ 4. List<Garantía>    │
     │                      │◄─────────────────────│
     │                      │                      │
     │                      │ 5. Para cada una:    │
     │                      │    actualizarEstado()│
     │                      │    (calcular si      │
     │                      │    expiró)           │
     │                      │                      │
     │ 6. Mostrar en tabla  │                      │
     │◄─────────────────────│                      │
     │                      │                      │
     │ 7. Filtrar por       │                      │
     │    estado "Activas"  │                      │
     │─────────────────────►│                      │
     │                      │                      │
     │                      │ 8. obtenerPorEstado  │
     │                      │    ("ACTIVA")        │
     │                      │─────────────────────►│
     │                      │                      │
     │                      │                      │ 9. db.warranties
     │                      │                      │    .find({estado:
     │                      │                      │     "ACTIVA"})
     │                      │                      │
     │                      │ 10. List<Garantía>   │
     │                      │◄─────────────────────│
     │                      │                      │
     │ 11. Actualizar tabla │                      │
     │◄─────────────────────│                      │

```

---

### Flujo 4: Estadísticas

```
┌──────────────────────────────────────────────────────────────────────────┐
│                       FLUJO DE ESTADÍSTICAS                               │
└──────────────────────────────────────────────────────────────────────────┘

                         DATOS ORIGEN                    GRÁFICO DESTINO
                         ────────────                    ───────────────

┌─────────────────┐
│  listaFacturas  │      Agrupar por                   ┌─────────────────┐
│  (de Odoo)      │      nombreCliente    ───────────► │  BarChart       │
│                 │      Sumar montoTotal              │  Ventas/Cliente │
└─────────────────┘                                    └─────────────────┘


┌─────────────────┐
│ servicioMongoDB │      contarActivas()               ┌─────────────────┐
│                 │      contarExpiradas()  ─────────► │  PieChart       │
│                 │      contarPorExpirar()            │  Estados        │
└─────────────────┘                                    └─────────────────┘


┌─────────────────┐
│  listaFacturas  │      Agrupar por                   ┌─────────────────┐
│  (de Odoo)      │      mes/año          ───────────► │  LineChart      │
│                 │      Sumar montoTotal              │  Ventas/Mes     │
└─────────────────┘                                    └─────────────────┘

```

**Cálculo de datos para gráficos:**

```java
// Ventas por Cliente (BarChart)
Map<String, Double> ventasPorCliente = listaFacturas.stream()
    .collect(Collectors.groupingBy(
        Factura::getNombreCliente,
        Collectors.summingDouble(Factura::getMontoTotal)
    ));

// Estado de Garantías (PieChart)
long activas = servicioMongoDB.contarGarantiasActivas();
long expiradas = servicioMongoDB.contarGarantiasExpiradas();
long porExpirar = servicioMongoDB.contarGarantiasPorExpirar();

// Ventas Mensuales (LineChart)
Map<YearMonth, Double> ventasPorMes = listaFacturas.stream()
    .filter(f -> f.getFechaFactura() != null)
    .collect(Collectors.groupingBy(
        f -> YearMonth.from(f.getFechaFactura()),
        Collectors.summingDouble(Factura::getMontoTotal)
    ));
```

---

### Resumen de Transformaciones de Datos

| Origen | Transformación | Destino |
|--------|----------------|---------|
| Odoo `account.move` | `mapearAFactura()` | `Factura` (modelo Java) |
| Odoo `account.move.line` | `obtenerLineasFactura()` | `LineaFactura` (modelo Java) |
| `Factura` + `LineaFactura` | `new Garantía(factura, linea, 12)` | `Garantía` (modelo Java) |
| `Garantía` | `garantíaADocumento()` | MongoDB `Document` |
| MongoDB `Document` | `documentoAGarantía()` | `Garantía` (modelo Java) |
| `List<Factura>` | agrupación + suma | Datos para gráficos |
| `List<Garantía>` | conteo por estado | Datos para PieChart |

## 📝 Licencia

Proyecto privado - Todos los derechos reservados.

---

Desarrollado con ❤️ usando JavaFX
