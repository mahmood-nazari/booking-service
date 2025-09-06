# Booking Service — README

 این فایل راهنمای کامل راه‌اندازی، پیکربندی و کار با سرویس رزرواست


تکنولوژی های استفاده شده:

spring boot, java, grafana, prometheus, redis, mySql, swagger, docker, docker-compose, alert-manager

---

## فهرست مطالب

* [پیش‌نیازها](#پیشنیازها)
* [راه‌اندازی سریع با Docker Compose](#راهاندازی-سریع-با-docker-compose)
* [data.sql اجرا](#اجرا data.sql)
* [ API (Swagger)](#api-swagger)
* [احراز هویت (JWT)](#احراز-هویت-jwt)
* [نمونهٔ درخواست‌ها (cURL)](#نمونهٔ-درخواستها-curl)
* [سلامت و متریک‌ها (Actuator/Prometheus)](#سلامت-و-متریکها-actuatorprometheus)
* [Grafana](#grafana)

* [allert-manager](#alert-manager)

---


## پیش‌نیازها

* Docker + Docker Compose
* available ports --> 8080 (App)، 3306 (MySQL)، 6379 (Redis)، 9090 (Prometheus)، 3000 (Grafana)

---

## راه‌اندازی سریع با داکر کامپوز

1. ریپازیتوری را کلون کنید و به روت پروژه بروید.

2. بیلد و اجرا:

```bash
docker compose up -d --build
```

* **Application**: `http://localhost:8080`
* **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
* **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
* **Prometheus**: `http://localhost:9090`
* **Grafana**: `http://localhost:3000` (username: admin / password: 1234)

برای توقف:

```bash
docker compose down
```

یا با پاکسازی کامل:

```bash
docker compose down -v
```

---

## data.sql داده‌ی نمونه

فایل مثالی شامل جدول‌ها و داده‌های اولیه:

* اجرای اسکریپت های زیر به ترتیب در روت پروژه:

```bash
docker compose cp data.sql mysql:/tmp/data.sql
```
```bash
docker compose exec -T mysql sh -c 'mysql -ubooking -pbooking booking < /tmp/data.sql'
```

---

## API (Swagger)

* UI: `http://localhost:8080/swagger-ui/index.html`
* JSON: `http://localhost:8080/v3/api-docs`
---

## نمونهٔ درخواست‌ها (cURL)

execute in terminal

### 1) لاگین و دریافت توکن

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"user1","password":"hashed_password_123"}' | jq -r .token)

echo $TOKEN
```

### 2) لیست اسلات‌های آزاد

```bash
curl -i 'http://localhost:8080/api/slots?page=0&size=10&sortBy=startTime&dir=ASC' \
  -H "Authorization: Bearer $TOKEN"
```

### 3) رزرو یک اسلات

```bash
curl -i -X POST http://localhost:8080/api/reservations \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"slotId":1}'
```

### 4) لغو رزرو

```bash
curl -i -X DELETE http://localhost:8080/api/reservations/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## سلامت و متریک‌ها (Actuator/Prometheus)

* Health: `http://localhost:8080/actuator/health` → `{ "status": "UP" }`
* Prometheus endpoint: `http://localhost:8080/actuator/prometheus`
* Prometheus dashboard: `http://localhost:9090`
### مثال کوئری‌ها در Prometheus

* نرخ خطای رنج 400:

```promql
sum(rate(http_server_requests_seconds_count{status=~"4.."}[5m]))
  /
sum(rate(http_server_requests_seconds_count[5m]))
```

* تاخیر p95 بر اساس مسیر:

```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (le, uri))
```

---

## Grafana

* UI: `http://localhost:3000` (username: admin / password: 1234)

اگر هیچ دشبوردی ساخته نشده بود لطفا این راهکار رو انجام بدین:
از مسیر زیر:

monitoring/grafana/dashboards/booking-grafana-dashboard.json

فایل جیسون رو کپی کنید در جای دلخواه و سپس با استفاده از کامند زیر گرافانا را ریستارت کنید
```bash
docker compose restart grafana
```
سپس فایل کپی شده را مجدد در مسیر سابق گذاشته و دوباره گرافانا را ریستارت کنید.

اگر در دشبورد تمام متریک ها بصورت <نو دیتا> بودند مراحل زیر را به ترتیب اجرا کنید:

```bash
curl -u admin:1234 \
-H "Content-Type: application/json" \
-X POST http://localhost:3000/api/datasources \
-d '{
"name": "Prometheus",
"type": "prometheus",
"access": "proxy",
"url": "http://prometheus:9090",
"isDefault": true,
"uid": "prometheus",
"jsonData": {
"httpMethod": "POST"
}
}'
```

```bash
docker compose restart grafana
```

## allert-manager
برای این سرویس باید تعداد خطاهای شما در یک دقیقه بصورت مداوم بالا برود و سپس با توجه به کانفیگی که انجام شده، یک بات برای اینکار درست شده و در تلگرام به چت‌آی‌دی که در فایل کانفیگ گذاشته شده پیام میده. در حال حاضر چت ‌ای‌دی خودم اونجا ست شده است.

