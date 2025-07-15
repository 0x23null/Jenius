package view;

import java.util.List;
import java.util.Scanner;
import model.Message;
import model.Note;

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

    public void displayHistory(List<Message> history) {
        System.out.println("\n--- Conversation History ---");
        for (Message message : history) {
            System.out.println(message.getRole() + ": " + message.getContent());
        }
        System.out.println("----------------------------\n");
    }

    public void displayNotes(List<Note> notes) {
        System.out.println("\n--- Notes ---");
        for (Note note : notes) {
            System.out.println(note.getTitle() + ": " + note.getContent());
        }
        System.out.println("--------------\n");
    }
}

