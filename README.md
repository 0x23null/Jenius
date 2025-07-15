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

Use natural language to ask Jenius to remember something or delete a note.
The assistant will automatically generate a short title for each note.

- `list-notes` – display all notes with their numeric IDs. Use these IDs when asking to delete or generate questions from a note.
You can request actions like "Xóa note 2" (delete note 2) or "Tạo câu hỏi từ note 1" and the AI will call the appropriate functions.
