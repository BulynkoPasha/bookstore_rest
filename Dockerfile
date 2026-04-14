## ============================================================
## Dockerfile для Spring Boot бэкенда
## Используем многоэтапную сборку (multi-stage build):
## Этап 1 — собираем JAR через Maven
## Этап 2 — запускаем только JAR без Maven и исходников
## ============================================================
#
## ---- Этап 1: Сборка ----
#FROM maven:3.9.6-eclipse-temurin-17 AS builder
#
#WORKDIR /app
#
## Сначала копируем только pom.xml и скачиваем зависимости
## Это кешируется Docker — повторная сборка будет быстрее
#COPY pom.xml .
#RUN mvn dependency:go-offline -q
#
## Копируем исходники и собираем JAR
#COPY src ./src
#RUN mvn package -DskipTests -q
#
## ---- Этап 2: Запуск ----
#FROM eclipse-temurin:17-jre-alpine
#
#WORKDIR /app
#
## Копируем только JAR из этапа сборки
#COPY --from=builder /app/target/*.jar app.jar
#
## Открываем порт бэкенда
#EXPOSE 8080
#
## Запускаем приложение
#ENTRYPOINT ["java", "-jar", "app.jar"]