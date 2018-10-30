package agents;

import java.util.Random;

public class Agent {
    private int lives;
    private int daggerCount;
    private int daggersFound;
    private String[][] map;
    private KnowledgeSpace[][] knowledge;

    public Agent(String[][] map) {
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

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getDaggerCount() {
        return daggerCount;
    }

    public void setDaggerCount(int daggerCount) {
        this.daggerCount = daggerCount;
    }

    public KnowledgeSpace[][] knowledge() {
        return knowledge;
    }

    public String[][] getMap() {
        return map;
    }

    public void setMap(String[][] map) {
        this.map = map;
    }

    public int getDaggersFound() {
        return daggersFound;
    }

    public void setDaggersFound(int daggersFound) {
        this.daggersFound = daggersFound;
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
                knowledge[i][j].setValue(map[i][j]);
                if (knowledge[i][j].getValue().equals("0")) {
                    revealNeighbours(knowledge[i][j].getX(), knowledge[i][j].getY());
                }
            }
        }
    }

    public void makeRandomMove() {
        Random r = new Random();
        int x = r.nextInt(knowledge.length);
        int y = r.nextInt(knowledge.length);

        String value = map[x][y];
        String currentKnowledge = knowledge[x][y].getValue();

        if (currentKnowledge.equals("x") || !knowledge[x][y].isInspected()) {
            System.out.println("Reveal (" + y + ", " + x + ")");
            knowledge[x][y].setInspected(true);

            if (value.equals("d")) {
                daggersFound++;
                lives--;
            }

            if (value.equals("g")) {
                lives++;
                revealNeighbours(x, y);
            }

            if (value.equals("0")) {
                revealNeighbours(x, y);
            }

            knowledge[x][y].setValue(value);
        }
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

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
