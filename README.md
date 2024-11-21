# Construction Docks Log
Запуск контейнера базы данных:
``` shell
docker run --name actslog-db -p 5432:5432 -e POSTGRES_DB=acts_log -e POSTGRES_USER=root -e POSTGRES_PASSWORD=55555 postgres:16 
```

Запуск контейнера Keycloak:
* Относительный путь импорта рилма:
```shell
docker run --name actslog-keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -v /config/keycloak/import:/opt/keycloak/data/import quay.io/keycloak/keycloak:23.0.4 start-dev --import-realm
```
* Абсолютный путь импорта рилма:
```shell
docker run --name actslog-keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -v C:\Users\Ierga\IdeaProjects\CDL\config\keycloak\import:/opt/keycloak/data/import quay.io/keycloak/keycloak:23.0.4 start-dev --import-realm
```

[Файл настройки рилма Keycloak](config/keycloak/import/actsLog.json)

Пользователи:

| Роль                  | Логин        | Пароль  |
|:----------------------|:-------------|:-------:|
| Подрядчик             | i.ergakov    |  55555  |
| Строительный контроль | v.kononov    |  55555  |
| Инженер ПТО           | s.orlenko    |  55555  |
| Сметчик               | i.fadin      |  55555  |
| Администратор         | k.shcherbak  |  55555  |
| Гость                 | v.maslakov   |  55555  |

Ссылка на главную страницу приложения:
```http request
http://localhost:8080/catalogue/acts/list
```

Ссылка на REST-контроллер:
```http request
http://localhost:8081/catalogue-api/acts
```

Ссылка на документацию REST-Сервиса (Swagger OpenAPI)
```http request
http://localhost:8081/swagger-ui/index.html
```