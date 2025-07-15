# Jenius
PRO192 AI Agent

## Setup

Before running the application, set the environment variable `GENAI_API_KEY` with your API key:

--Cần set biến api key trước khi chạy--
```bash
set GENAI_API_KEY=your_api_key_here
```

Hoặc thay thẳng API key vào class controller

String apiKey = "paste_api_key";

Run the application using Maven:

```bash
mvn exec:java
```

## Notes Management

Notes are stored in `notes.json` in the project directory so they persist
between runs. Only one command is required:

- `list-notes` – display all notes with their IDs.

For adding, deleting, searching or summarizing notes, simply ask Jenius in
natural language and the assistant will use its built-in functions to manage
the notes for you.
