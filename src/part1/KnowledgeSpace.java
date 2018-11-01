package part1;

public class KnowledgeSpace {
    private String value;
    private boolean inspected;
    private boolean flagged;
    private int x;
    private int y;

    public KnowledgeSpace(int x, int y) {
        this.x = x;
        this.y = y;
        this.value = "x";
        this.inspected = false;
        this.flagged = false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isInspected() {
        return inspected;
    }

    public void setInspected(boolean inspected) {
        this.inspected = inspected;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
}
