# Performance Tuning Guide

## Database Connection Pool Configuration

### Current Configuration
```properties
quarkus.datasource.jdbc.min-size=5
quarkus.datasource.jdbc.max-size=20
```

### Recommended Tuning Based on Load

#### Low Traffic (< 50 concurrent users)
```properties
quarkus.datasource.jdbc.min-size=5
quarkus.datasource.jdbc.max-size=20
quarkus.datasource.jdbc.initial-size=5
quarkus.datasource.jdbc.acquisition-timeout=10
```

#### Medium Traffic (50-200 concurrent users)
```properties
quarkus.datasource.jdbc.min-size=10
quarkus.datasource.jdbc.max-size=50
quarkus.datasource.jdbc.initial-size=10
quarkus.datasource.jdbc.acquisition-timeout=15
```

#### High Traffic (> 200 concurrent users)
```properties
quarkus.datasource.jdbc.min-size=20
quarkus.datasource.jdbc.max-size=100
quarkus.datasource.jdbc.initial-size=20
quarkus.datasource.jdbc.acquisition-timeout=20
```

### Additional Pool Optimizations
```properties
# Validación de conexiones
quarkus.datasource.jdbc.validate-on-borrow=true
quarkus.datasource.jdbc.validation-query-sql=SELECT 1

# Tiempo máximo de vida de una conexión (30 minutos)
quarkus.datasource.jdbc.max-lifetime=30m

# Tiempo máximo de inactividad (10 minutos)
quarkus.datasource.jdbc.idle-removal-interval=10m

# Detección de fugas de conexiones
quarkus.datasource.jdbc.leak-detection-interval=10m
```

## JVM Optimization

### Heap Memory Settings
```bash
# Development
java -Xms512m -Xmx1g -jar target/quarkus-app/quarkus-run.jar

# Production (adjust based on available memory)
java -Xms2g -Xmx4g -jar target/quarkus-app/quarkus-run.jar
```

### GC Tuning (G1GC - Default)
```bash
java -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:InitiatingHeapOccupancyPercent=45 \
     -Xms2g -Xmx4g \
     -jar target/quarkus-app/quarkus-run.jar
```

### For Low-Latency Applications (ZGC)
```bash
# Java 21+
java -XX:+UseZGC \
     -Xms2g -Xmx4g \
     -jar target/quarkus-app/quarkus-run.jar
```

## Application Configuration

### HTTP Thread Pool
```properties
# Ajustar según carga esperada
quarkus.thread-pool.core-threads=8
quarkus.thread-pool.max-threads=100
quarkus.thread-pool.queue-size=1000
```

### Request Timeout
```properties
# Timeout para requests HTTP (30 segundos)
quarkus.http.io-threads=8
quarkus.http.limits.max-body-size=10M
```

## Database Query Optimization

### Prepared Statement Caching
PostgreSQL JDBC driver automatically caches prepared statements. To tune:

```properties
# URL connection parameters
quarkus.datasource.jdbc.url=jdbc:postgresql://host:5432/db?\
  preparedStatementCacheQueries=256&\
  preparedStatementCacheSizeMiB=5
```

### Index Recommendations

Based on common queries, ensure these indexes exist:

```sql
-- Cliente searches
CREATE INDEX IF NOT EXISTS idx_cliente_nit ON t_cliente(nit);
CREATE INDEX IF NOT EXISTS idx_cliente_nombre ON t_cliente(nombre_cliente);
CREATE INDEX IF NOT EXISTS idx_cliente_zona ON t_cliente(cod_zona);

-- NotaEntrega searches  
CREATE INDEX IF NOT EXISTS idx_nota_entrega_cliente ON t_nota_entrega(cod_cliente);
CREATE INDEX IF NOT EXISTS idx_nota_entrega_fecha ON t_nota_entrega(fecha);

-- Articulo searches
CREATE INDEX IF NOT EXISTS idx_articulo_linea ON t_articulo(cod_linea);
CREATE INDEX IF NOT EXISTS idx_articulo_nombre ON t_articulo(descripcion);

-- DetalleNotaEntrega for batch loading
CREATE INDEX IF NOT EXISTS idx_detalle_nota_entrega ON t_detalle_nota_entrega(cod_nota_entrega);
```

## Logging Configuration

### Production Logging
```properties
# Reducir logging en producción
quarkus.log.level=INFO
quarkus.log.category."com.yahveh".level=INFO

# Log de queries SQL (solo para debugging)
# quarkus.log.category."org.hibernate.SQL".level=DEBUG
```

### Development Logging
```properties
quarkus.log.level=DEBUG
quarkus.log.category."com.yahveh".level=DEBUG

# Ver queries ejecutadas
quarkus.log.category."org.jboss.logging".level=INFO
```

## Monitoring and Metrics

### Enable Metrics Endpoint
```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
</dependency>
```

```properties
# application.properties
quarkus.micrometer.enabled=true
quarkus.micrometer.export.prometheus.enabled=true
```

### Key Metrics to Monitor
- Request latency (p50, p95, p99)
- Database connection pool usage
- Active threads
- GC pause times
- Error rates

Access metrics at: `http://localhost:8080/q/metrics`

## Load Testing

### Example with Apache Bench
```bash
# Test endpoint
ab -n 1000 -c 10 -H "Authorization: Bearer TOKEN" \
   http://localhost:8080/api/clientes
```

### Example with wrk
```bash
# 10 threads, 100 connections, 30 seconds
wrk -t10 -c100 -d30s \
    -H "Authorization: Bearer TOKEN" \
    http://localhost:8080/api/clientes
```

## Native Image Compilation

For optimal startup time and memory usage:

```bash
# Build native image
./mvnw package -Dnative

# Run native image
./target/yahveh-1.0.0-SNAPSHOT-runner
```

Benefits:
- Instant startup (< 100ms)
- Lower memory footprint (~30MB vs ~200MB JVM)
- Better container density

## Docker Optimization

### Multi-stage Build
```dockerfile
FROM registry.access.redhat.com/ubi8/openjdk-21:latest AS builder
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

FROM registry.access.redhat.com/ubi8/openjdk-21-runtime:latest
COPY --from=builder /app/target/quarkus-app /deployments
EXPOSE 8080
CMD ["java", "-jar", "/deployments/quarkus-run.jar"]
```

### Runtime Flags
```bash
docker run -e JAVA_OPTS="-Xms1g -Xmx2g" \
           -e QUARKUS_DATASOURCE_JDBC_MAX_SIZE=50 \
           -p 8080:8080 \
           yahveh-backend:latest
```

## Performance Testing Checklist

Before deploying performance improvements:

- [ ] Run load tests with realistic data volumes
- [ ] Monitor database query performance
- [ ] Check connection pool utilization
- [ ] Verify GC behavior under load
- [ ] Test error handling under stress
- [ ] Validate timeout configurations
- [ ] Check for memory leaks with extended runs
- [ ] Review application logs for warnings

## Common Performance Anti-Patterns to Avoid

### ❌ Anti-pattern: Lazy Loading in Loops
```java
// BAD - N+1 queries
for (NotaEntrega nota : notas) {
    List<Detalle> detalles = repository.findByNota(nota.getId());
}
```

### ✅ Good: Batch Loading
```java
// GOOD - Single query
Map<Integer, List<Detalle>> detalles = 
    repository.findByNotasBatch(notaIds);
```

### ❌ Anti-pattern: Inefficient String Building
```java
// BAD - Creates many objects
String result = "";
for (String s : list) {
    result += s + ",";
}
```

### ✅ Good: StringBuilder
```java
// GOOD - Efficient
StringBuilder sb = new StringBuilder();
for (String s : list) {
    sb.append(s).append(",");
}
String result = sb.toString();
```

### ❌ Anti-pattern: No Connection Timeout
```java
// BAD - Can hang indefinitely
Connection conn = dataSource.getConnection();
```

### ✅ Good: With Timeout
```java
// GOOD - Configure in application.properties
quarkus.datasource.jdbc.acquisition-timeout=10
```

## Support

For performance issues or tuning assistance, contact the development team with:
- Load test results
- Application logs
- Database query statistics
- JVM metrics
