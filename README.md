# Ktor Starter project

This is the Vitec Memorix Ktor starter project. This project is meant as a starting point for small projects and coding assignments.

This project contains the following components:

- Language: Kotlin (targeting jvm)- https://kotlinlang.org/
- HTTP Server: Ktor - https://ktor.io
- Database abstraction layer: Exposed Framework - https://github.com/JetBrains/Exposed
- Dependency Injection: Koin - https://insert-koin.io/
- Build tool: Gradle - https://gradle.org/

## Using this template

On https://github.com/vitec-memorix/ktor-starter:
- In the top right, above the file list, click on "Use this template"
- Select "Create a new repository"
- Follow the steps to create a new repository from the template

## Setup

Copy the example .env file and fill in a database password:
```shell
cp .env.dist .env
```
You might want to change the database hostname to something more specific in both ```.env``` and ```docker-compose.yaml``` to prevent conflicts with other projects.

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

## Running the application
```shell
./gradlew run
```
