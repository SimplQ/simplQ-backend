# SimplQ Backend

[![Build Status](https://travis-ci.org/SimplQ/simplQ-backend.svg?branch=master)](https://travis-ci.org/SimplQ/simplQ-backend)

[SimplQ](https://simplq.me) is a completely web based queue management solution that anyone can use to create instant queues. 

## Development Envirmonment Setup Instructions

The project is written in Java, and deployed on AWS. These steps are to be followed when you are running the project for the first time.

1. Install Java 11 and maven. 
2. Clone this project
3. Build the jar:

```
mvn package
```
4. Run a development DB server:

```
docker run --name simplq-db -p 5432:5432 -e POSTGRES_PASSWORD=password -e POSTGRES_USER=admin -e POSTGRES_DB=simplq -d postgres
```

5. Run the jar:

```
java -jar simplq/target/simplq-0.0.1-SNAPSHOT.jar 
```

We follow Google's [Java Style Guidelines](https://github.com/google/styleguide). For Intellij, you can install the [google-java-format](https://plugins.jetbrains.com/plugin/8527-google-java-format) plugin, Eclipse and other IDE users can find more instructions [here](https://github.com/google/google-java-format).

### Testing locally

Test APIs through Postman - A test postman collection is available [here](https://www.getpostman.com/collections/252a096a86fc550fb5fb).

Run Integration Tests: 

```
mvn test
```

## Running in production

Generate the jar:
```
mvn package
```

By default, the jar uses an in-memory H2 database. This is for development purposes only, and so for a production setup, set up a postgres DB and pass the DB connection parameters as environment variables:

```
export DB_USERNAME=<user-name>
export DB_PASSWORD=<password>
export DB_URL=jdbc:postgresql://<host>:<port>/<db>
export TOKEN_URL=<base-token-url>
java -Dspring.profiles.active=prod -jar simplq/target/simplq-0.0.1-SNAPSHOT.jar 
```

We use [Liquidbase](https://www.liquibase.org/) for DB migrations. To run migrations:
```
mvn liquibase:update -Ddb.username=<username> -Ddb.password=<password> -Ddb.url=jdbc:postgresql://<host>:<port>/<db>
```

# Contributing

Feel free to fork and improve, and do send a pull request. We will be delighed to work with you. 

