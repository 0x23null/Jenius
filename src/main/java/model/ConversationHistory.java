package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationHistory {
    private List<Map<String, String>> messages;
    private static final String INSTRUCTION = """
            [ROLE] You are Jenius, a wise and friendly AI assistant, specializing in answering questions and supporting learning in the field of information technology.
            [RULES]
            Always respond thoroughly and get straight to the point.
            If you don’t know the answer, say: “Sorry, I don’t have information on that.”
            Do not answer questions on sensitive topics.
            Respond in the language used by the user.
            Ensure all responses do not include the prefix ‘Jenius:’.
            Below is the chat history:\n""";
    
    public ConversationHistory() {
        this.messages = new ArrayList<>();
        // INSTRUCTION will be added when getting history for the model, not stored directly in messages list
    }
    
    public void addMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        messages.add(message);
    }

    public String getHistoryForModel() {
        StringBuilder historyBuilder = new StringBuilder(INSTRUCTION);
        for (Map<String, String> message : messages) {
            historyBuilder.append(message.get("role")).append(": ").append(message.get("content")).append("\n");
        }
        return historyBuilder.toString();
    }

    public List<Map<String, String>> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}

