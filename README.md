
# Shopping Cart microservices

### Este es un proyecto de e-commerce implementado con una arquitectura de microservicios, utilizando Spring Boot y Docker.

### Prerrequisitos

* Docker y Docker Compose.

* Git.

### Cómo Levantar el Entorno

## Sigue estos simples pasos para tener todo funcionando.

## 1. Clona el Repositorio
```
git clone https://github.com/danielangel22/test-fakestore-microservices.git
cd test-fakestore-microservices
```

## 2. Inicia todos los servicios con Docker Compose
Este comando construirá y levantará todos los contenedores en segundo plano.
```
docker-compose up --build -d
```

***
## Cómo Probar la API
***
Para probar la API, puedes usar una herramienta como Postman o explorar la documentación interactiva de Swagger.

Documentación Swagger

Cada servicio tiene su propia documentación de API. Simplemente abre los siguientes enlaces en tu navegador:

Auth Service: http://localhost:8081/swagger-ui.html

Product Service: http://localhost:8082/swagger-ui.html

Order Service: http://localhost:8083/swagger-ui.html

Payment Service: http://localhost:8084/swagger-ui.html

***
### Cómo Detener el Entorno
***
Para apagar todos los contenedores, ejecuta:

```
docker-compose down
```
Para eliminar los datos de la base de datos y Redis, usa el flag -v:
```
docker-compose down -v
```