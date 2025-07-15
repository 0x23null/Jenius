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

Use the following commands inside the application:

- `add-note <title> <content>` – create a new note.
- `list-notes` – display all notes.
- `delete-note <title>` – remove a note by title.
