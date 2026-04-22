# fornitore-service

Microservizio Spring Boot per la gestione dei fornitori (`FORNITORE`) in un contesto multi-tenant su SQL Server/Azure SQL.

## Panoramica

Il servizio espone API REST per:

- recuperare un fornitore per ID
- recuperare fornitori paginati
- recuperare fornitori in formato dropdown
- creare un fornitore
- aggiornare un fornitore

Il routing multi-tenant avviene leggendo il claim `tenantId` dal JWT nell'header `Authorization`.

## Stack tecnologico

- Java 17
- Spring Boot 3.4.5
- Spring Web
- Spring Data JPA (Hibernate)
- Spring Validation
- Spring Actuator
- SQL Server JDBC Driver
- MapStruct
- Lombok
- Maven

Riferimenti principali:
- `pom.xml`
- `src/main/resources/application.yaml`
- `src/main/resources/application-azure.yml`

## Struttura progetto (principale)

- `src/main/java/unisa/poultryfarm/fornitoreservice/application/rest/FornitoreRestController.java`
- `src/main/java/unisa/poultryfarm/fornitoreservice/business/service/SupplierService.java`
- `src/main/java/unisa/poultryfarm/fornitoreservice/persistence/entity/Fornitore.java`
- `src/main/java/unisa/poultryfarm/fornitoreservice/persistence/repository/FornitoreRepository.java`
- `src/main/java/unisa/poultryfarm/fornitoreservice/application/multitenancy/*`

## Configurazione base

Da `application.yaml`:

- Porta: `8080`
- Context path: `/api/v1`
- Nome applicazione: `fornitore-service`
- Dialect Hibernate: `org.hibernate.dialect.SQLServerDialect`

Quindi la base URL locale e':

`http://localhost:8080/api/v1`

### Configurazione Azure / DataSource

Da `application-azure.yml` + `DataSourceConfig`:

- viene usato `app.datasource.server-name`
- autenticazione SQL Server configurata con `ActiveDirectoryManagedIdentity`
- esclusa autoconfigurazione JDBC standard Spring (`DataSourceAutoConfiguration`)

Valore atteso (esempio nel repo):
- `sql-saas-poultryfarm-dev.database.windows.net`

## Multi-tenancy: come funziona

Flusso sintetico:

1. `JwtTenantFilter` legge `Authorization: Bearer <jwt>`
2. Estrae `tenantId` dal payload JWT (senza validazione firma nel filtro)
3. Salva il tenant in `TenantContext` (ThreadLocal)
4. Hibernate usa `SchemaTenantResolver` + `SchemaMultiTenantConnectionProvider`
5. `CatalogRepository` risolve `tenantId -> (database_name, schema_name)` interrogando `db-catalog`
6. Viene creato/cachato un `DataSource` per tenant

Classi chiave:
- `JwtTenantFilter`
- `TenantContext`
- `SchemaTenantResolver`
- `SchemaMultiTenantConnectionProvider`
- `CatalogRepository`

## API REST

Controller: `FornitoreRestController`  
Base path controller: `/fornitori`  
Con context path globale: `/api/v1`

### 1) Get fornitore by id

- **GET** `/api/v1/fornitori/{id}`
- **200** se trovato
- **404** se non trovato

### 2) Get fornitori paginati

- **GET** `/api/v1/fornitori/paginated?pageNumber=0&pageSize=10`
- default: `pageNumber=0`, `pageSize=10` (da `BaseRestController`)

Risposta tipo:
```json
{
  "supplierEntities": [
    {
      "id": 1,
      "codiceProvenienza": "SUP-001",
      "partitaIva": "IT12345678901",
      "telefono": "0811234567",
      "indirizzo": "Via Roma 1"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 1
  }
}
```

### 3) Get dropdown fornitori

- **GET** `/api/v1/fornitori/dropdown`
- ritorna lista di:
    - `id`
    - `codiceProvenienza`

### 4) Crea fornitore

- **POST** `/api/v1/fornitori`
- body (`SupplierDto`):
    - `codiceProvenienza` (**required**, `@NotBlank`)
    - `partitaIva` (**required**, `@NotBlank`)
    - `telefono` (opzionale)
    - `indirizzo` (opzionale)
- **201** se creato

Esempio body:
```json
{
  "codiceProvenienza": "SUP-002",
  "partitaIva": "IT98765432109",
  "telefono": "0891234567",
  "indirizzo": "Via Napoli 10"
}
```

### 5) Aggiorna fornitore

- **PUT** `/api/v1/fornitori`
- body (`UpdateSupplierDto`):
    - `id`
    - campi di `SupplierDto`
- **200** se aggiornato
- **404** se `id` non trovato

## Esempi `curl`

> Nota: il filtro richiede un JWT con claim `tenantId`.

```bash
curl -X GET "http://localhost:8080/api/v1/fornitori/1" \
  -H "Authorization: Bearer <JWT_CON_tenantId>"
```

```bash
curl -X GET "http://localhost:8080/api/v1/fornitori/paginated?pageNumber=0&pageSize=10" \
  -H "Authorization: Bearer <JWT_CON_tenantId>"
```

```bash
curl -X POST "http://localhost:8080/api/v1/fornitori" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_CON_tenantId>" \
  -d '{
    "codiceProvenienza":"SUP-002",
    "partitaIva":"IT98765432109",
    "telefono":"0891234567",
    "indirizzo":"Via Napoli 10"
  }'
```

## Prerequisiti

- JDK 17
- Maven 3.9+ (oppure `./mvnw`)
- Accesso a SQL Server/Azure SQL configurato per Managed Identity
- Catalog DB con tabella:
    - `dbo.TENANTS(tenant_id, database_name, schema_name)`

## Avvio locale

### 1) Compila

```bash
./mvnw clean package
```

### 2) Avvia (profilo default)

```bash
./mvnw spring-boot:run
```

### 3) Avvia con profilo `azure`

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=azure
```

Se necessario, imposta il server SQL via env var Spring relaxed binding:

```bash
export APP_DATASOURCE_SERVER_NAME="sql-saas-poultryfarm-dev.database.windows.net"
./mvnw spring-boot:run -Dspring-boot.run.profiles=azure
```

## Docker

Nel repository e' presente un `Dockerfile` multi-stage.

Build immagine:
```bash
docker build -t fornitore-service:local .
```

Run container:
```bash
docker run --rm -p 8080:8080 \
  -e APP_DATASOURCE_SERVER_NAME="sql-saas-poultryfarm-dev.database.windows.net" \
  fornitore-service:local
```

## Test

Attualmente e' presente un test base di bootstrap contesto Spring:

- `src/test/java/unisa/poultryfarm/fornitoreservice/FornitoreServiceApplicationTests.java`

Esecuzione:

```bash
./mvnw test
```

## Note operative

- Il filtro `JwtTenantFilter` rifiuta richieste senza `tenantId` (`401`).
- La validazione firma JWT non e' fatta nel filtro applicativo; e' demandata a monte (es. APIM), come indicato nei commenti del codice.
- La cache dei `DataSource` per tenant e' mantenuta in memoria nel provider multi-tenant.
- L'entity `Fornitore` mappa la tabella `dbo.FORNITORE`.
```