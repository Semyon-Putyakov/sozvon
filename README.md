# Sozvon

Пет-проект мессенджера с текстовыми чатами

## Возможности

- регистрация и вход по JWT;
- личные и групповые чаты;
- создание, редактирование, удаление и поиск сообщений;
- управление участниками чатов;


## Архитектура

Проект состоит из нескольких сервисов:

| Компонент | Технологии | Порт |
| --- | --- | --- |
| Frontend | HTML, CSS, JavaScript, Node.js/Express | `3000` |
| API | Java 17, Spring Boot, Spring Security| `1010` |
| DB service | Java 17, Spring Boot, Spring Data JPA | `2020`, только внутри Docker-сети |
| PostgreSQL | PostgreSQL 17 | только внутри Docker-сети |
| Kafka | Kafka + ZooKeeper | только внутри Docker-сети |

API и DB service обмениваются запросами через Kafka. PostgreSQL доступен DB service и инициализируется файлом `api/init/sozvon.sql`.

## Требования

Для запуска через Docker:

- Docker Engine или Docker Desktop;
- Docker Compose;
- минимум 4 GB свободной оперативной памяти, рекомендуется 8 GB.

Для запуска без Docker понадобятся Java 17, Maven, Node.js 20, PostgreSQL 17, Kafka и ZooKeeper.

## Быстрый запуск

1. Создайте файл `api/.env`:

```dotenv
DB_NAME=sozvon
DB_USER=postgres
DB_PASSWORD=change-me
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
JWT_SECRET=replace-with-a-random-secret-at-least-32-characters
```

2. Запустите все сервисы:

```bash
cd api
docker compose up --build -d
```

3. Откройте приложение:

```text
http://localhost:3000
```

API будет доступен по адресу `http://localhost:1010`.

## Управление контейнерами

Просмотр состояния:

```bash
docker compose -f api/compose.yaml ps
```

Просмотр логов:

```bash
docker compose -f api/compose.yaml logs -f
```

Остановка:

```bash
docker compose -f api/compose.yaml down
```

Остановка с удалением базы данных:

```bash
docker compose -f api/compose.yaml down -v
```

Последняя команда необратимо удаляет Docker volume с данными PostgreSQL.

## Основные API-маршруты

- `POST /authentication/registration` - регистрация;
- `POST /authentication/login` - вход;
- `/api/users` - пользователи;
- `/api/chats` - чаты и их участники;
- `/api/messages` - сообщения;


Маршруты `/api/**` требуют заголовок:

```text
Authorization: Bearer <JWT>
```

## Развертывание

Текущая конфигурация рассчитана прежде всего на локальный запуск. Для публичного размещения нужно:

1. Выделить Linux-сервер минимум с 2 vCPU и 4 GB RAM.
2. Установить Docker и Docker Compose.
3. Настроить домен и HTTPS через Nginx или Caddy.
4. Заменить `http://localhost:1010` в `front/test/api.js` и `front/test/webSocket.js` на публичный адрес API.
5. Добавить публичный домен в CORS-настройки `SecurityConfig` и `WebSocketConfig`.
6. Использовать HTTPS/WSS: браузеры ограничивают доступ к микрофону на небезопасных страницах.
7. Добавить TURN-сервер, например Coturn. Один STUN не гарантирует соединение пользователей за NAT и корпоративными сетями.
8. Настроить резервное копирование PostgreSQL и ротацию логов.

### Бесплатный хостинг

Текущий Docker Compose требует больше ресурсов, чем обычно дают бесплатные PaaS-тарифы.

- Google Cloud, AWS, Azure, Oracle Cloud и DigitalOcean подходят для временного запуска на trial, но обычно требуют платежную верификацию.
- Render позволяет запускать небольшие сервисы без добавления карты, однако бесплатному контейнеру доступно только 512 MB RAM, а бесплатная PostgreSQL удаляется после ограниченного периода.
- Для размещения проекта на бесплатном PaaS потребуется убрать Kafka и отдельный DB service, подключив API напрямую к PostgreSQL.

## Безопасность

- Не коммитьте `.env`, приватные SSH-ключи, токены и пароли.
- Для `JWT_SECRET` используйте случайную строку длиной не менее 32 символов.
- Перед публичным запуском смените все демонстрационные пароли и данные.
- Если секрет или приватный ключ уже попал в Git, простого удаления файла недостаточно: секрет нужно отозвать, заменить и удалить из истории репозитория.

## Структура проекта

```text
.
├── api/          # публичный API, JWT, WebSocket и WebRTC-сигналинг
├── db/           # работа с PostgreSQL и обработка Kafka-запросов
└── front/test/   # браузерный интерфейс и Express static server
```

