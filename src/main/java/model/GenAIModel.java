package model;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenAIModel {
    private final Client client;
    public final ConversationHistory history;

    public GenAIModel(String apiKey) {
        this.client = Client.builder().apiKey(apiKey).build();
        this.history = new ConversationHistory();
    }

    public String explainConcept(String concept) {
        return generateResponse(concept, "user");
    }

    public String summarizeContent(String content) {
        String prompt = "Summarize the following content in 100-150 words:\n" + content;
        return generateResponse(prompt, "user");
    }

    public String generateQuestions(String topicOrContent) {
        String prompt = "Generate 5 multiple-choice questions based on " + topicOrContent + " with 4 answer options each.";
        return generateResponse(prompt, "user");
    }

    public String generateResponse(String currentPrompt, String role) {
        try {
            // Construct the full prompt including instruction and history for the model
            String fullPrompt = history.getHistoryForModel() + currentPrompt;

            GenerateContentResponse response = client.models.generateContent("gemini-2.0-flash-001", fullPrompt, null);
            String result = response.text();
            
            return result;
        } catch (Exception e) {
            return "Error generating response: " + e.getMessage();
        }
    }

    public ConversationHistory getHistory() {
        return history;
    }
}

