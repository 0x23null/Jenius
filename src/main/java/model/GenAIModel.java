package model;

import com.google.genai.Client;
import com.google.genai.types.AutomaticFunctionCallingConfig;
import com.google.genai.types.Candidate;
import com.google.genai.types.Content;
import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.Tool;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GenAIModel {
    private final Client client;
    public final ConversationHistory history;

    public GenAIModel(String apiKey) {
        this.client = Client.builder().apiKey(apiKey).build();
        this.history = new ConversationHistory();
    }

    public String explainConcept(String concept) {
        return generateResponse(concept).text();
    }

    public String summarizeContent(String content) {
        String prompt = "Summarize the following content in 100-150 words:\n" + content;
        return generateResponse(prompt).text();
    }

    public String generateQuestions(String input) {
        String prompt = "Generate 5 multiple-choice questions based on " + input + " with 4 answer options each.";
        return generateResponse(prompt).text();
    }

    public String summarizeFile(String path) {
        try {
            String content = Files.readString(Path.of(path));
            return summarizeContent(content);
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    public GenerateContentResponse generateResponse(String currentPrompt) {
        try {
            String fullPrompt = history.getHistoryForModel() + currentPrompt;

            Tool tool = Tool.builder()
                    .functions(List.of(
                            GenAIModel.class.getMethod("summarizeFile", String.class),
                            GenAIModel.class.getMethod("generateQuestions", String.class)
                    ))
                    .build();

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .tools(List.of(tool))
                    .automaticFunctionCalling(
                            AutomaticFunctionCallingConfig.builder()
                                    .disable(true)
                                    .build())
                    .build();

            return client.models.generateContent("gemini-2.0-flash-001", fullPrompt, config);
        } catch (Exception e) {
            return GenerateContentResponse.builder()
                    .candidates(List.of(
                            Candidate.builder()
                                    .content(Content.builder()
                                            .parts(List.of(Part.fromText("Error generating response: " + e.getMessage())))
                                            .build())
                                    .build()))
                    .build();
        }
    }

    public ConversationHistory getHistory() {
        return history;
    }
}

