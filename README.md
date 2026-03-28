# LogInsight

A JavaFX desktop application for viewing, filtering, and analysing log files. You can open log files, filter by date/time/level/source/user/IP, view charts (logs over time, level distribution, top sources), add notes to log entries, and export reports as Text, JSON, or CSV.

## Requirements

- **Java 17**
- **Maven 3.6+**

## Run

From the project root:

```bash
mvn javafx:run
```

## Default login

- **Username:** `admin` — **Password:** `admin`
- Or: **Username:** `user` — **Password:** `user123`

## Log file format

The parser expects lines in this format:

```
yyyy-MM-dd HH:mm:ss  LEVEL  source  message...
```

Example:

```
2025-02-28 10:15:30  INFO  web-server  User login successful
```

Optional `User=` and `IP=` inside the message are extracted for filtering (e.g. `User=john IP=192.168.1.1`).

## Database

- **File:** `logmonitoring.db` (SQLite)
- **Location:** Created in the directory from which you run the app (e.g. project root if you run `mvn javafx:run` from there).
- **Contents:**
  - **users** — login accounts (username, password, created_at). Default users are inserted on first run.
  - **flagged_logs** — saved notes for log entries (timestamp, level, source, message, note, review_status). Used by Reports → “Review Saved Notes” and when adding notes.

See `DB_SCHEMA.md` for table definitions.

## Build only

```bash
mvn clean compile
```

## Demo Video

[![Watch the demo](https://img.youtube.com/vi/CeDiItiCf3Q/0.jpg)](https://youtu.be/CeDiItiCf3Q)
