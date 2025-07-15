# Jenius
PRO192 AI Agent

## Setup

Before running the application, set the environment variable `GENAI_API_KEY` with your API key:

Cần set biến api key trước khi chạy
```bash
set GENAI_API_KEY=your_api_key_here
```

Hoặc thay thẳng API key vào class controller

String apiKey = "paste_api_key";

Run the application using Maven:

```bash
mvn exec:java
```
Or the application using ```run.bat``` file.

## Notes Management

Notes are stored in `notes.json` in the project directory so they persist
between runs. Use the following commands:

- `list-notes` – display all notes with their IDs.
- `add-note "title"|"content"` – add a new note directly from the command line.

Other note actions like deleting or summarizing can still be requested in
natural language and the assistant will call the appropriate functions for you.
