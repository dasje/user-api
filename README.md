# User API

This is an api server for storing and fetching users. It is based on the Vitec Memorix Ktor starter project.

This project contains the following components:

- Language: Kotlin (targeting jvm)- https://kotlinlang.org/
- HTTP Server: Ktor - https://ktor.io
- Database abstraction layer: Exposed Framework - https://github.com/JetBrains/Exposed
- Dependency Injection: Koin - https://insert-koin.io/
- Build tool: Gradle - https://gradle.org/

## Setup

Copy the example .env file and fill in a database password:
```shell
cp .env.dist .env
```
To build and run the server outside of the Docker environment, run only the service 'user_db' in docker-compose.
The db host name in the .env is by default 'localhost'. This maps to the database container from outside the user-api network.
To run the server with database dependency, it is not necessary to change the host name in the env file. This is overridden in the service setup in the docker-compose file.

You might also want to change the database port to ```5433``` to prevent conflicts with local postgres instances.

## Starting the server

The server is containerized with Docker and runs in a Docker network with the necessary depenedencies (see below). The server can be started using the following command:
```shell
docker compose up
```

## Manual testing with Swagger

This project uses Swagger Docs to detail the API and allow manual testing of end points. 

Once the server is running, navigate to `/swagger` to access the Swagger UI.

## Depencies

Dependent services (in this case the PostgreSQL database) are managed using Docker Compose.

### Starting
```shell
docker compose up -d
```

### Stopping
```shell
docker compose stop
```

### Resetting
Beware: this will delete all the data in your database!
```shell
docker compose down -v
```

