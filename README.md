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

Interact naturally to manage your notes. Jenius uses AI function calling to add
or delete notes based on your requests. When a note is added, the content is
summarized to a short title automatically. Use the `list-notes` command at any
time to display all saved notes.
