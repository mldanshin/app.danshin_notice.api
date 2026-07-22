## Режим dev

java -jar app.jar --spring.profiles.active=dev

## Зависимости

Для работы приложения должны быть доступны сервисы:

1. danshin_genealogy API

## Тестирование

Для тестирования нужны работающие сервисы:

1. danshin_genealogy API

Команда тестирования отправки писем
curl -X POST http://localhost:8060/api/admin/send-now/6?date=2026-08-22 \
-H "Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYWlsQGRhbnNoaW4ubmV0IiwidXVpZCI6IjFiN2I2MzBhLWIzMzEtNDMzYi1iNWI1LTI2YWI0YjZjNTFiOCIsInJvbGVzIjpbIkFETUlOIl0sImlhdCI6MTc4NjcwNjk0NywiZXhwIjo0OTQwMzkzMzQ3fQ.I-L3yMPCXPNUIagUGgM1Crx54dzXmBh85zlHFexeS2w"

Команда тестирования входящего вебхука
curl -X POST http://localhost:8060/api/channel/webhook/telegram \
-H "Content-Type: application/json" \
-d '{
"message": {
"chat": {
"id": 1370345612,
"username": "testuser"
},
"text": "/start 1b7b630a-b331-433b-b5b5-26ab4b6c51b8"
}
}'
