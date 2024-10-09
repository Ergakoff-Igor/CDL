# Construction Docks Log
Запуск контейнера базы данных:
``` shell
docker run --name actslog-db -p 5432:5432 -e POSTGRES_DB=acts_log -e POSTGRES_USER=root -e POSTGRES_PASSWORD=55555 postgres:16 
```

Запуск контейнера Keycloak:
```shell
docker run --name actslog-keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -v /config/keycloak/import:/opt/keycloak/data/import quay.io/keycloak/keycloak:23.0.4 start-dev --import-realm
```

```shell
docker run --name actslog-keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -v C:\Users\Ierga\IdeaProjects\CDL\config\keycloak\import:/opt/keycloak/data/import quay.io/keycloak/keycloak:23.0.4 start-dev --import-realm
```