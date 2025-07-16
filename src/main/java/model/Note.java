package model;

public class Note {
    private int id;
    private String title;
    private String content;

    public Note() {
        
    }
    
    public Note(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
