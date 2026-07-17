# QREventSystem — Backend

A Spring Boot REST API for creating events and tracking attendee check-ins via a unique, per-event code (designed to be shared/scanned as a QR code by a companion frontend).

Live app frontend: https://qr-event-system-theta.vercel.app

## How It Works

1. An organizer creates an **Event** (title, location, date, time) via `POST /event`.
2. The backend auto-generates a unique, URL-friendly **event code** by slugifying the title and appending a random 4-digit number (e.g. `product-launch-4821`), retrying until it's unique.
3. That event code is what gets encoded into a QR code / shareable link on the frontend for attendees to check in.
4. Attendees check in via `POST /attendance` with their name, email, and the event code.
5. Duplicate check-ins (same email + event code) are rejected.
6. Organizers can look up an event by its code, and list all attendance records for an event.

## Tech Stack

- **Java 17**, **Spring Boot 4.0**
- **Spring Web MVC** (`spring-boot-starter-webmvc`)
- **Spring Data JPA** with **MySQL** (`mysql-connector-j`)
- **Lombok** for boilerplate reduction on entities
- **Maven** build, packaged and run via a multi-stage **Docker** image

## Project Structure

```
src/main/java/com/nishtha/QREventSystem/
├── QrEventSystemApplication.java     # Spring Boot entry point
├── Config/
│   └── CorsConfig.java               # CORS: allows the deployed frontend origin
├── controller/
│   ├── EventController.java          # /event - create & look up events
│   └── AttendanceController.java     # /attendance - check in & list attendance
├── services/
│   ├── EventService.java             # Event creation, unique code generation, lookups
│   └── AttendanceService.java        # Check-in logic, duplicate prevention
├── repository/
│   ├── EventRepository.java          # JPA repository for Event
│   └── AttendanceRepository.java     # JPA repository for Attendance
└── entity/
    ├── Event.java                    # id, eventCode, title, location, date, time
    └── Attendance.java               # id, eventCode, name, email
```

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/event` | Create a new event; returns the event including its generated `eventCode` |
| `GET` | `/event/code/{code}` | Fetch an event by its event code |
| `POST` | `/attendance` | Check an attendee in to an event (rejects duplicate email+eventCode) |
| `GET` | `/attendance/{eventCode}` | List all attendance records for an event |

## Configuration

Configured via `src/main/resources/application.properties`, backed by environment variables:

| Variable | Purpose |
|---|---|
| `DB_URL` | JDBC URL for the MySQL database |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |

`spring.jpa.hibernate.ddl-auto=update` means the schema (tables for `Event` and `Attendance`) is created/updated automatically on startup — no manual migrations needed for local development.

## Running Locally

**Prerequisites:** Java 17, Maven (or the included `mvnw` wrapper), and a running MySQL instance.

```bash
git clone https://github.com/nishthagupta09/QREventSystem_Backend.git
cd QREventSystem_Backend

export DB_URL=jdbc:mysql://localhost:3306/qreventsystem
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password

./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### With Docker

```bash
docker build -t qr-event-system .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/qreventsystem \
  -e DB_USERNAME=your_db_user \
  -e DB_PASSWORD=your_db_password \
  qr-event-system
```

## Notes

- `AttendanceController` and `EventController` currently allow CORS from `*` (wildcard) at the controller level, while `CorsConfig` restricts the global mapping to the deployed frontend origin — worth reconciling these before production hardening.
- Duplicate check-in attempts throw a generic `RuntimeException`, which isn't currently mapped to a clean HTTP error response (e.g. `409 Conflict`).

## Future Improvements

- Global exception handling for cleaner error responses (e.g. `409` on duplicate check-in, `404` on unknown event code)
- Server-side QR code image generation (currently the event code alone is returned; encoding it into an actual QR image appears to be a frontend concern)
- Authentication for organizers so events can't be created/queried anonymously
- Pagination for attendance lists on large events
