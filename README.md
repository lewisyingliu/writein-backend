# Write In App using React.js with Spring Boot

## Dependency

– If you want to use MariaDB:

```xml
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
</dependency>
```

– or HSQLDB:

```xml

<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.6.0</version>
    <scope>runtime</scope>
</dependency>
```

## Configure Spring Datasource, JPA, App properties

Open `src/main/resources/application.properties`

- For MariaDB
```
spring.datasource.url= jdbc:mariadb://localhost:3306/writeindb?useSSL=false
spring.datasource.username=crm
spring.datasource.password=crm
spring.jpa.hibernate.ddl-auto= update
```
- For HSQLDB
```
spring.datasource.url=jdbc:hsqldb:file:./data/writeindb
spring.datasource.username=sa 
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```
## App Properties
writein.app.jwtSecret= WriteInAppSecretKey
writein.app.jwtExpirationMs= 86400000
```

## Run Spring Boot application

```
mvn spring-boot:run
```
