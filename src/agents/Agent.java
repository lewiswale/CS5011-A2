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
                knowledge[i][j] = new KnowledgeSpace();
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

    public KnowledgeSpace[][] getKnowledge() {
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
        if (x > 0) {
            knowledge[x-1][y].setValue(map[x-1][y]);
        }

        if (y > 0) {
            knowledge[x][y-1].setValue(map[x][y-1]);
        }

        if (x > 0 && y > 0) {
            knowledge[x-1][y-1].setValue(map[x-1][y-1]);
        }

        if (x < map.length-1) {
            knowledge[x+1][y].setValue(map[x+1][y]);
        }

        if (y < map[0].length-1) {
            knowledge[x][y+1].setValue(map[x][y+1]);
        }

        if (x < map.length-1 && y < map[0].length-1) {
            knowledge[x+1][y+1].setValue(map[x+1][y+1]);
        }

        if (x > 0 && y < map[0].length-1) {
            knowledge[x-1][y+1].setValue(map[x-1][y+1]);
        }

        if (x < map.length-1 && y > 0) {
            knowledge[x+1][y-1].setValue(map[x+1][y-1]);
        }
    }

    public void makeRandomMove() {
        Random r = new Random();
        int x = r.nextInt(getKnowledge().length);
        int y = r.nextInt(getKnowledge().length);

        String value = getMap()[x][y];
        String currentKnowledge = getKnowledge()[x][y].getValue();

        if (currentKnowledge.equals("x") || !getKnowledge()[x][y].isInpected()) {
            System.out.println("Reveal (" + y + ", " + x + ")");
            getKnowledge()[x][y].setInpected(true);

            if (value.equals("d")) {
                setDaggersFound(getDaggersFound()+1);
                setLives(getLives()-1);
            }

            if (value.equals("g")) {
                setLives(getLives()+1);
                revealNeighbours(x, y);
            }

            if (value.equals("0")) {
                revealNeighbours(x, y);
            }

            getKnowledge()[x][y].setValue(value);
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
