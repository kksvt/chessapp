package chess;

public class Player {
    private String name;
    private boolean isWhite;
    private boolean isHuman;
    public Player(String name, boolean isWhite, boolean isHuman) {
        this.name = name;
        this.isWhite = isWhite;
        this.isHuman = isHuman;
    }
    public String getName() { return name; }
    public boolean getIsWhite() { return isWhite; }
    public boolean getIsHuman() { return isHuman; }
}
