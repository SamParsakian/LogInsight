# Database schema

SQLite file: `logmonitoring.db` (created in process working directory on first run).

---

## Table: users

```sql
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);
```

Used for login and User Management. Default rows: `admin`/`admin`, `user`/`user123` (inserted when table is empty).

---

## Table: flagged_logs

```sql
CREATE TABLE IF NOT EXISTS flagged_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp TEXT NOT NULL,
    level TEXT NOT NULL,
    source TEXT NOT NULL,
    message TEXT NOT NULL,
    user TEXT,
    src_ip TEXT,
    note TEXT,
    review_status INTEGER DEFAULT 0,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);
```

Used for notes on log entries. Uniqueness: `(timestamp, level, source, message)`. Save note → update if exists, else insert.
