# Собирает JAR с Swagger
mvn clean package

# Запускает с профилем dev (Swagger доступен)
java -jar target/XmlParser.jar --spring.profiles.active=dev





# Собирает JAR без Swagger (имя: XmlParser-prod.jar)
mvn clean package -Pprod

# Запускает в продакшен-режиме
java -jar target/XmlParser-prod.jar --spring.profiles.active=prod