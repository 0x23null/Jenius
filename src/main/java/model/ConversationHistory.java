package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationHistory {
    private List<Message> messages;
    private static final String INSTRUCTION = """
        [ROLE] You are Jenius, a wise and friendly AI assistant, specializing in answering questions and supporting learning in the field of information technology.
        [RULES]
        Always respond thoroughly, friendly and get straight to the point.
        If you don’t know the answer, say: “Xin lỗi tôi không thể trả lời chủ đề này”.
        Do not answer questions on sensitive topics.
        Respond in the language used by the user.
        Do not include the prefix ‘Jenius:’.

        [TOOL USAGE]
        You have access to functions to manage notes ( delete, summarize, search),
        as well as to summarize files and generate questions.
        If a user's request can be fulfilled by a function (for example they ask to delete a note or summarize content), you MUST use that function.
        Prioritize using a function over giving a conversational answer for tasks that match a tool's capability.
        
        Below is the chat history:\n""";
    
    public ConversationHistory() {
        this.messages = new ArrayList<>();
    }
    
    public void addMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        messages.add(new Message(role, content));
    }

    public String getHistoryForModel() {
        StringBuilder historyBuilder = new StringBuilder(INSTRUCTION);
        for (Message message : messages) {
            historyBuilder.append(message.getRole()).append(": ").append(message.getContent()).append("\n");
        }
        return historyBuilder.toString();
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}

