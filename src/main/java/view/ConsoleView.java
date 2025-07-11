package view;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleView {
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void displayWelcomeMessage() {
        System.out.println("""
                                  _________   ________  _______
                                 / / ____/ | / /  _/ / / / ___/
                            __  / / __/ /  |/ // // / / /\\__ \\ 
                           / /_/ / /___/ /|  // // /_/ /___/ / 
                           \\____/_____/_/ |_/___/\\____//____/  
                                                               """);
        System.out.println("Type 'exit' or 'quit' to end the conversation.");
        System.out.println("Encoding: " + System.getProperty("file.encoding") + "\n");
    }

    public String getUserInput() {
        System.out.print("> ");
        return scanner.nextLine();
    }

    public void displayResponse(String response) {
        System.out.println("Jenius: " + response + "\n");
    }

    public void displayError(String error) {
        System.out.println("\nError: " + error + "\n");
    }

    public void displayHistory(List<Map<String, String>> history) {
        System.out.println("\n--- Conversation History ---");
        for (Map<String, String> message : history) {
            System.out.println(message.get("role") + ": " + message.get("content"));
        }
        System.out.println("----------------------------\n");
    }
}

