package agents;

public class KnowledgeSpace {
    private String value;
    private boolean inpected;

    public KnowledgeSpace() {
        this.value = "x";
        this.inpected = false;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isInpected() {
        return inpected;
    }

    public void setInpected(boolean inpected) {
        this.inpected = inpected;
    }
}
