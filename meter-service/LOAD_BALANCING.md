# Meter load-balancing demo

Run both instances from this `meter-service` directory. They use the same jar,
service name, and existing `./data/meterdb` database. Start the primary first.
Do not copy the project or database for the second instance.

Build with Java 17 before starting either jar:

```powershell
.\mvnw.cmd verify
```

Primary, port 8081 (existing configuration):

```powershell
java -jar target\meter-service-0.0.1-SNAPSHOT.jar
```

Second terminal, same directory, port 8091:

```powershell
java -jar target\meter-service-0.0.1-SNAPSHOT.jar --server.port=8091 --spring.jpa.hibernate.ddl-auto=validate '--spring.datasource.url=jdbc:h2:file:./data/meterdb;AUTO_SERVER=TRUE;IFEXISTS=TRUE'
```

`IFEXISTS=TRUE` prevents accidentally creating a different database. Schema
validation prevents the secondary from changing the existing schema. Both JVMs
must use the same H2 version and physical database path. Keep the existing
`AUTO_SERVER=TRUE` option; do not disable file locking or delete `.lock.db`.

Eureka at `http://localhost:8761` should list two **UP** instances of
**METER-SERVICE**, with distinct instance IDs ending in `8081` and `8091`.
Discovery and load-balancer caches may need about a minute to refresh.
The Gateway's existing `lb://METER-SERVICE` route selects instances using
Spring Cloud LoadBalancer's default round-robin strategy.

After obtaining a JWT through the existing login endpoint, set `$token` locally
and inspect repeated authenticated requests:

```powershell
$headers = @{ Authorization = "Bearer $token" }
1..20 | ForEach-Object {
    $response = Invoke-WebRequest -UseBasicParsing -Uri 'http://localhost:8080/api/meters/MTR-1001' -Headers $headers
    $response.Headers['X-GridPulse-Instance']
}
```

The response header reports the actual Meter listener port, `8081` or `8091`.
CRUD JSON payloads and JWT rules are unchanged. Shared H2 means a write handled
by either instance is visible to the other. Usage Service keeps its existing
discovery-based Meter client.

This is a local SOA demonstration, not database high availability. H2 automatic
mixed mode depends on the JVM currently owning the embedded database; owner
shutdown can roll back transactions in other connections. Stop the secondary
first, then the primary, with no writes in flight. Do not rebuild/replace a jar
while instances are running from it. Stop both Meter instances before rebuilding.

References: [H2 automatic mixed mode](https://h2database.github.io/html/features.html#auto_mixed_mode),
[Spring Cloud LoadBalancer](https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/loadbalancer.html).
