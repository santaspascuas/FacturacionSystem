# Comandos para Claude en product-service

Comandos comprobados el 2026-11-05 en este equipo (Windows 10, Windows PowerShell 5.1), ejecutados desde `D:\git\FacturacionSystem\product-service`. Si alguno deja de funcionar, corrígelo aquí.

## Reglas

- **La BD del usuario (`facturacion-product-db`) no se toca sin su permiso.** Arrancar el servicio o lanzar `mvnw test` sin redirigir la BD aplica en ella las migraciones pendientes. Para probar, usa la BD desechable.
- **Las migraciones ya aplicadas no se editan.** Flyway guarda su checksum y, si cambia, el servicio no arranca ("Migration checksum mismatch"). Los cambios van en una `V` nueva.
- **El `.env` no se imprime.** Dentro del contenedor, usa sus variables `$POSTGRES_USER` y `$POSTGRES_DB`.
- **Las variables `$env:` solo duran una llamada.** Ponlas en el mismo comando que las usa.

## Entorno

- git no está en el PATH: no hay `git status`, `git log` ni `git diff`.
- Java 21 está en el PATH y `JAVA_HOME` no está definido; `.\mvnw.cmd` funciona igualmente (Maven 3.10.0).
- Maven funciona offline (`-o`): `~/.m2` tiene todas las dependencias.
- Docker está disponible y la imagen `postgres:17-alpine` ya está descargada.
- La BD del usuario es el contenedor `facturacion-product-db` (puerto 55433), definido en `docker-compose.yml` como servicio `customer-db`. La levanta el usuario.

## Compilar

No toca ninguna BD.

```powershell
.\mvnw.cmd -o -q test-compile   # exit 0 = compila
```

## BD desechable

```powershell
docker run -d --rm --name pg-desechable -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=revision -e POSTGRES_DB=product_db -p 55499:5432 postgres:17-alpine
for ($i = 0; $i -lt 60; $i++) { docker exec pg-desechable pg_isready -h localhost -U postgres | Out-Null; if ($LASTEXITCODE -eq 0) { break }; Start-Sleep -Seconds 1 }
docker stop pg-desechable   # con --rm, pararla la borra
```

Espera siempre con `-h localhost`. Sin él, `pg_isready` da 0 con el servidor temporal de la inicialización, que después se reinicia.

## Tests del proyecto contra la BD desechable

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:55499/product_db"; $env:SPRING_DATASOURCE_USERNAME = "postgres"; $env:SPRING_DATASOURCE_PASSWORD = "revision"; $env:PRODUCT_DB_PORT = "55499"
.\mvnw.cmd -o -q test   # exit 0 = pasan
```

- Las variables de entorno mandan sobre `application.properties` y sobre el `.env`. `PRODUCT_DB_PORT` es una segunda red: si la URL no se aplicara, el placeholder también apuntaría a la desechable.
- `ProductServiceApplicationTests` arranca el contexto entero: aplica las migraciones en la desechable y valida las entidades contra ellas (`ddl-auto=validate`).

## Arrancar y parar el servicio

Primero el jar; no toca ninguna BD:

```powershell
.\mvnw.cmd -o -q package -DskipTests   # genera target\product-service-0.0.1-SNAPSHOT.jar
```

Contra la BD desechable, en el puerto 8099. Las variables van en la misma llamada que `Start-Process`:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:55499/product_db"; $env:SPRING_DATASOURCE_USERNAME = "postgres"; $env:SPRING_DATASOURCE_PASSWORD = "revision"; $env:PRODUCT_DB_PORT = "55499"; $env:SERVER_PORT = "8099"
$log = "$env:TEMP\product-service.log"   # Claude: mejor en el scratchpad de la sesión
$p = Start-Process java -ArgumentList "-jar", "target\product-service-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden -PassThru -RedirectStandardOutput $log -RedirectStandardError "$log.err"
for ($i = 0; $i -lt 90; $i++) { if (Get-NetTCPConnection -LocalPort 8099 -State Listen -ErrorAction SilentlyContinue) { break }; if ($p.HasExited) { break }; Start-Sleep -Seconds 1 }
```

- El servicio sigue vivo en las llamadas siguientes. Si no llega a escuchar, mira el final de `$log`.
- Contra la BD del usuario, solo cuando lo pida (por ejemplo, con "corre"): el mismo `Start-Process` sin las variables. Escucha en el 8082 y aplica las migraciones pendientes.

Para pararlo, usa el dueño del puerto. El PID de `Start-Process` es el lanzador de `javapath`, no la JVM:

```powershell
Stop-Process -Id (Get-NetTCPConnection -LocalPort 8099 -State Listen).OwningProcess
```

## Llamar a los endpoints

En PowerShell 5.1, `Invoke-RestMethod` lanza una excepción con los 4xx y 5xx y lee mal los acentos. Esta función devuelve el código y el cuerpo en UTF-8 en los dos casos. Pégala al principio de cada llamada:

```powershell
function Llamar([string]$Metodo, [string]$Ruta, [string]$Tenant, [string]$Json, [int]$Puerto = 8099) {
	$p = @{ Method = $Metodo; Uri = "http://localhost:$Puerto$Ruta"; UseBasicParsing = $true; ContentType = "application/json; charset=utf-8" }
	if ($Tenant) { $p.Headers = @{ "X-Tenant-Id" = $Tenant } }
	if ($Json) { $p.Body = [Text.Encoding]::UTF8.GetBytes($Json) }
	try { $r = Invoke-WebRequest @p; $codigo = [int]$r.StatusCode; $bytes = $r.RawContentStream.ToArray() }
	catch {
		$resp = $_.Exception.Response; if ($null -eq $resp) { throw }
		$codigo = [int]$resp.StatusCode; $flujo = $resp.GetResponseStream(); if ($flujo.CanSeek) { $flujo.Position = 0 }
		$ms = New-Object IO.MemoryStream; $flujo.CopyTo($ms); $bytes = $ms.ToArray()
	}
	"HTTP $codigo " + [Text.Encoding]::UTF8.GetString($bytes)
}
Llamar Post /api/category/register 11111111-1111-1111-1111-111111111111 '{"name":"Cafés"}'
```

Todos los endpoints llevan la cabecera `X-Tenant-Id` con un UUID:

| Endpoint | Cuerpo | Respuesta correcta |
|---|---|---|
| `POST /api/usertpv/register` | `{"username":"...","password":"..."}` | 201 |
| `POST /api/usertpv/login` | `{"username":"...","password":"..."}` | 200 |
| `POST /api/category/register` | `{"name":"..."}` | 201 |
| `GET /api/category/all` | — | 200 |
| `PUT /api/category/{publicId}` | `{"name":"...","active":true}` | 200 |

## Consultar la BD del usuario (solo lectura)

```powershell
"select version, description, checksum, success from flyway_schema_history order by installed_rank;" | docker exec -i facturacion-product-db sh -c 'PGOPTIONS=-cdefault_transaction_read_only=on psql -U $POSTGRES_USER -d $POSTGRES_DB'
```

- El SQL entra por la entrada estándar, así que sus comillas no pasan por los argumentos de PowerShell 5.1, que rompe las comillas dobles anidadas.
- `PGOPTIONS` abre la sesión en solo lectura: un `CREATE` o un `INSERT` falla con "read-only transaction".
- En la desechable, cambia el nombre del contenedor; ahí puedes quitar `PGOPTIONS`.

## Copiar el código a otra carpeta

`Copy-Item` falla con rutas de más de 260 caracteres; `robocopy` no:

```powershell
robocopy src\main\java <destino>\src\main\java /MIR   # exit menor que 8 = bien (1 = copió ficheros)
```

Sirve para montar en el scratchpad un proyecto Maven de prueba con el código del servicio sin añadir ficheros a `src/test`. Ese proyecto no lleva `application.properties`: la URL de la BD desechable se fija en el test, así que no puede llegar a la BD del usuario.
