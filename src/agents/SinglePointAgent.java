package agents;

import java.util.Random;

public class SinglePointAgent {
    private int lives;
    private int daggerCount;
    private int daggersFound;
    private String[][] map;
    private KnowledgeSpace[][] knowledge;

    public SinglePointAgent(String[][] map) {
        this.lives = 1;
        this.map = map;
        this.knowledge = new KnowledgeSpace[map.length][map[0].length];

        int daggers = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                knowledge[i][j] = new KnowledgeSpace(i, j);
                if (map[i][j].equals("d")) {
                    daggers++;
                }
            }
        }

        this.daggerCount = daggers;
        this.daggersFound = 0;
    }

    public boolean isComplete() {
        if (lives == 0) {
            return true;
        }

        boolean complete = true;

        for (int i = 0; i < knowledge.length; i++) {
            for (int j = 0; j < knowledge[0].length; j++) {
                if (knowledge[i][j].getValue().equals("x")) {
                    complete = false;
                    return complete;
                }
            }
        }

        return complete;
    }

    public void revealNeighbours(int x, int y) {
        int xStart = Math.max(x-1, 0);
        int yStart = Math.max(y-1, 0);
        int xEnd = Math.min(x+1, knowledge.length-1);
        int yEnd = Math.min(y+1, knowledge.length-1);

        for (int i = xStart; i <= xEnd; i++) {
            for (int j = yStart; j <= yEnd; j++) {
                if (knowledge[i][j].getValue().equals("x")) {
                    knowledge[i][j].setValue(map[i][j]);
                    if (knowledge[i][j].getValue().equals("0") || knowledge[i][j].getValue().equals("g")) {
                        revealNeighbours(knowledge[i][j].getX(), knowledge[i][j].getY());
                    }
                }
            }
        }
    }

    public void makeSinglePointCheck(int x, int y) {
        System.out.println("Inspect (" + y + ", " + x + ")");
        KnowledgeSpace current = knowledge[x][y];

        if (current.getValue().equals("x")) {
            current.setValue(map[x][y]);
        }

        if (!current.getValue().equals("D") && !current.isInspected()) {
            current.setInspected(true);
            boolean isClue = inspectValue(current);

            if (isClue) {
                int clue = Integer.parseInt(current.getValue());

                if (clue == countFlaggedNeighbours(current)) {
                    revealNeighbours(current.getX(), current.getY());
                } else if (clue == countCoveredAndFlaggedNeighbours(current)) {
                    flagCoveredNeighbours(current);
                }

            }
        }
    }

    public void makeMove(int x, int y) {
        KnowledgeSpace current = knowledge[x][y];
        System.out.println("Inspecting " + y + ", " + x);

        if (current.getValue().equals("x")) {
            if (x == 0 && y == 0) {
                current.setValue(map[x][y]);
                current.setInspected(true);
                inspectValue(current);
            }

            if (!current.isInspected()) {
                if (AllFreeNeighbours(current)) {
                    current.setValue(map[x][y]);
                    current.setInspected(true);
                    inspectValue(current);
                } else if (AllMarkedNeighbours(current)){
                    current.setValue("D");
                    current.setFlagged(true);
                }
            }
        } else {
            inspectValue(current);
            current.setInspected(true);
        }
    }

    public boolean randomProbe() {
        Random r = new Random();
        int x = r.nextInt(knowledge.length);
        int y = r.nextInt(knowledge.length);

        KnowledgeSpace current = knowledge[x][y];

        if (current.getValue().equals("x")) {
            current.setValue(map[x][y]);
            current.setInspected(true);
            inspectValue(current);
            return true;
        }

        return false;
    }

    public boolean AllMarkedNeighbours(KnowledgeSpace current) {
        int x = current.getX();
        int y = current.getY();

        int xStart = Math.max(x-1, 0);
        int yStart = Math.max(y-1, 0);
        int xEnd = Math.min(x+1, knowledge.length-1);
        int yEnd = Math.min(y+1, knowledge.length-1);

        for (int i = xStart; i <= xEnd; i++) {
            for (int j = yStart; j <= yEnd; j++) {
                KnowledgeSpace neighbour = knowledge[i][j];
                if (!neighbour.getValue().equals("x") && !neighbour.getValue().equals("D")) {
                    if (inspectValue(neighbour)) {
                        int clue = Integer.parseInt(neighbour.getValue());
                        int coveredNeighbours = countCoveredNeighbours(neighbour);

                        if (coveredNeighbours == clue - countFlaggedNeighbours(neighbour)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean AllFreeNeighbours(KnowledgeSpace current) {
        int x = current.getX();
        int y = current.getY();

        int xStart = Math.max(x-1, 0);
        int yStart = Math.max(y-1, 0);
        int xEnd = Math.min(x+1, knowledge.length-1);
        int yEnd = Math.min(y+1, knowledge.length-1);

        for (int i = xStart; i <= xEnd; i++) {
            for (int j = yStart; j <= yEnd; j++) {
                KnowledgeSpace neighbour = knowledge[i][j];
                if (!neighbour.getValue().equals("x") && !neighbour.getValue().equals("D")) {
                    if (inspectValue(neighbour)) {
                        int clue = Integer.parseInt(neighbour.getValue());

                        if (clue == countFlaggedNeighbours(neighbour)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean inspectValue(KnowledgeSpace space) {
        boolean isClue = false;

        if (space.getValue().equals("d")) {
            lives--;
            daggersFound++;
            return isClue;
        }

        if (space.getValue().equals("g")) {
            lives++;
            revealNeighbours(space.getX(), space.getY());
            return isClue;
        }

        if (space.getValue().equals("0")) {
            revealNeighbours(space.getX(), space.getY());
            return isClue;
        }

        isClue = true;
        return isClue;
    }

    public int countCoveredAndFlaggedNeighbours(KnowledgeSpace space) {
        int x = space.getX();
        int y = space.getY();
        int count = 0;

        int xStart = Math.max(x-1, 0);
        int yStart = Math.max(y-1, 0);
        int xEnd = Math.min(x+1, knowledge.length-1);
        int yEnd = Math.min(y+1, knowledge.length-1);

        for (int i = xStart; i <= xEnd; i++) {
            for (int j = yStart; j <= yEnd; j++) {
                if (knowledge[i][j].getValue().equals("x") || knowledge[i][j].getValue().equals("D")
                || knowledge[i][j].getValue().equals("d")) {
                    count++;
                }
            }
        }

        return count;
    }

    public int countCoveredNeighbours(KnowledgeSpace space) {
        int x = space.getX();
        int y = space.getY();
        int count = 0;

        int xStart = Math.max(x-1, 0);
        int yStart = Math.max(y-1, 0);
        int xEnd = Math.min(x+1, knowledge.length-1);
        int yEnd = Math.min(y+1, knowledge.length-1);

        for (int i = xStart; i <= xEnd; i++) {
            for (int j = yStart; j <= yEnd; j++) {
                if (knowledge[i][j].getValue().equals("x")) {
                    count++;
                }
            }
        }

        return count;
    }

    public void flagCoveredNeighbours(KnowledgeSpace space) {
        int x = space.getX();
        int y = space.getY();

        int xStart = Math.max(x-1, 0);
        int yStart = Math.max(y-1, 0);
        int xEnd = Math.min(x+1, knowledge.length-1);
        int yEnd = Math.min(y+1, knowledge.length-1);

        for (int i = xStart; i <= xEnd; i++) {
            for (int j = yStart; j <= yEnd; j++) {
                if (knowledge[i][j].getValue().equals("x")) {
                    knowledge[i][j].setFlagged(true);
                    knowledge[i][j].setValue("D");
                }
            }
        }
    }

    public int countFlaggedNeighbours(KnowledgeSpace space) {
        int x = space.getX();
        int y = space.getY();
        int count = 0;

        int xStart = Math.max(x-1, 0);
        int yStart = Math.max(y-1, 0);
        int xEnd = Math.min(x+1, knowledge.length-1);
        int yEnd = Math.min(y+1, knowledge.length-1);

        for (int i = xStart; i <= xEnd; i++) {
            for (int j = yStart; j <= yEnd; j++) {
                if (knowledge[i][j].isFlagged() || knowledge[i][j].getValue().equals("d")) {
                    count++;
                }
            }
        }

        return count;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================\n");

        for (int i = 0; i < knowledge.length; i++) {
            for (int j = 0; j < knowledge[0].length; j++) {
                sb.append(knowledge[i][j].getValue());
                sb.append("\t");
            }
            sb.append("\t\n");
        }

        sb.append("\nDagger Count = " + daggerCount);
        sb.append("\nDaggers Found = " + daggersFound);
        sb.append("\nLives = " + lives);

        return sb.toString();
    }
}
