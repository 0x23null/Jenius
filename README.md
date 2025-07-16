# Jenius
PRO192 AI Agent

## Setup

Cần set biến api key trước khi chạy.
```bash
set GENAI_API_KEY=your_api_key_here
```

Hoặc dán thẳng API key vào class controller

```String apiKey = "api_key";```

Chạy chương trình bằng Maven:

```bash
mvn exec:java
```
Hoặc chạy bằng file ```run.bat```.

## Notes Management

Ghi chú được lưu trong tệp notes.json trong thư mục dự án, giúp chúng được giữ lại sau mỗi lần chạy.
Sử dụng các lệnh sau:

- `list-notes` – hiển thị tất cả ghi chú cùng với ID của chúng.
- `add-note "title"|"content"` – thêm ghi chú mới trực tiếp từ dòng lệnh.

Các thao tác khác như xóa hoặc tóm tắt ghi chú vẫn có thể được yêu cầu bằng 
ngôn ngữ tự nhiên — trợ lý sẽ tự động gọi đúng chức năng tương ứng cho bạn.
