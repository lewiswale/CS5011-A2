package part1;
import java.util.Random;

public class SPSAgent {
    private int lives;
    private int daggerCount;
    private int daggersFound;
    private String[][] map;
    private KnowledgeSpace[][] knowledge;
    private int currentX, currentY;
    private int randomProbeCount = 0;

    /**
     * Constructor for SPS Agent
     * @param map map for agent to solve.
     */
    public SPSAgent(String[][] map) {
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

    /**
     * Method for agent to make single point inspection on a given cell.
     * @param x x-coordinate for cell to be inspected
     * @param y y-coordinate for cell to be inspected
     * @return true if cell has been flagged or probed, false if no move was made.
     */
    public boolean makeMove(int x, int y) {
        currentX = x;
        currentY = y;
        KnowledgeSpace current = knowledge[x][y];
        System.out.println("Single Point Inspection on (" + y + ", " + x + ")");

        if (current.getValue().equals("x")) {
            if (x == 0 && y == 0) {
                current.setValue(map[x][y]);
                inspectValue(current);
                current.setInspected(true);
                return true;
            }

            if (!current.isInspected()) {
                if (AllFreeNeighbours(current)) {
                    current.setValue(map[x][y]);
                    inspectValue(current);
                    current.setInspected(true);
                    return true;
                } else if (AllMarkedNeighbours(current)){
                    current.setValue("D");
                    current.setFlagged(true);
                    daggersFound++;
                    System.out.println("Flagged a dagger.");
                    return true;
                }
            }
        } else {
            inspectValue(current);
            current.setInspected(true);
            return false;
        }

        return false;
    }

    /**
     * Method to randomly probe a cell.
     * @return true if covered cell is selected, else false.
     */
    public boolean randomProbe() {
        Random r = new Random();
        int x = r.nextInt(knowledge.length);
        int y = r.nextInt(knowledge.length);

        currentX = x;
        currentY = y;

        System.out.println("Random Probe at (" + y + ", " + x + ")");

        KnowledgeSpace current = knowledge[x][y];

        if (current.getValue().equals("x")) {
            current.setValue(map[x][y]);
            inspectValue(current);
            current.setInspected(true);
            randomProbeCount++;
            return true;
        }

        return false;
    }

    /**
     * Checks completeness of map.
     * @return true if game is over, else false.
     */
    public boolean isComplete() {
        if (lives == 0) {
            return true;
        }

        boolean complete = true;
        int coveredCount = 0;

        for (int i = 0; i < knowledge.length; i++) {
            for (int j = 0; j < knowledge[0].length; j++) {
                if (knowledge[i][j].getValue().equals("x")) {
                    complete = false;
                    coveredCount++;
                }

                if (knowledge[i][j].getValue().equals("D") || knowledge[i][j].getValue().equals("d")) {
                    coveredCount++;
                }
            }
        }

        if (coveredCount == daggerCount) {
            complete = true;
            for (int i = 0; i < knowledge.length; i++) {
                for (int j = 0; j < knowledge.length; j++) {
                    if (knowledge[i][j].getValue().equals("x")) {
                        knowledge[i][j].setValue("D");
                        knowledge[i][j].setFlagged(true);
                    }
                }
            }
        }

        return complete;
    }

    /**
     * Reveals all neighbouring cells of given cell.
     * @param x x-coordinate of current cell
     * @param y y-coordinate of current cell
     */
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
                        if (knowledge[i][j].getValue().equals("g")) {
                            System.out.println("Gold found! +1 Lives.");
                            lives++;
                            knowledge[i][j].setInspected(true);
                        }
                        revealNeighbours(knowledge[i][j].getX(), knowledge[i][j].getY());   //Recursively reveals all safe cells
                    }
                }
            }
        }
    }

    /**
     * Checks cells neighbours to see if one has the same amount of covered neighbours as daggers left to be found.
     * @param current current cell
     * @return true if cell is dagger, false if safe
     */
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
                            return true;        //Cell is a dagger. Flag it.
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks all cells neighbours to see if one has had all its daggers found.
     * @param current current cell
     * @return true if cell is certain to be safe, false otherwise
     */
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
                            return true;        //Neighbouring cell already has clued amount of daggers flagged. Cell is safe.
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Probes given cell
     * @param space current cell
     * @return true if cell is a clue, false otherwise
     */
    public boolean inspectValue(KnowledgeSpace space) {
        if (space.getValue().equals("d")) {
            if (!space.isInspected()) {
                System.out.println("Dagger found. -1 Lives.");
                lives--;
                daggersFound++;
            }
            return false;
        }

        if (space.getValue().equals("g")) {
            if (!space.isInspected()) {
                System.out.println("Gold found! +1 Lives.");
                lives++;
                revealNeighbours(space.getX(), space.getY());
            }
            return false;
        }

        if (space.getValue().equals("0")) {
            if (!space.isInspected()) {
                revealNeighbours(space.getX(), space.getY());
            }
            return false;
        }

        if (space.getValue().equals("D")) {
            return false;
        }

        return true;
    }

    /**
     * Counts amount of covered neighbours to given cell
     * @param space current cell
     * @return amount of covered neighbours
     */
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

    /**
     * Counts amount of flagged neighbours to current cell
     * @param space current cell
     * @return amount of flagged neighbours
     */
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

    /**
     * @return agents current life count
     */
    public int getLives() {
        return lives;
    }

    /**
     * toString override for agent
     * @return agents current knowledge and attributes
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================\n");

        for (int i = 0; i < knowledge.length; i++) {
            for (int j = 0; j < knowledge[0].length; j++) {
                if (i == currentX && j == currentY) {
                    sb.append("[");
                    sb.append(knowledge[i][j].getValue());
                    sb.append("]");
                } else {
                    sb.append(" ");
                    sb.append(knowledge[i][j].getValue());
                    sb.append(" ");
                }
                sb.append("\t");
            }
            sb.append("\t\n");
        }

        sb.append("\nDagger Count = " + daggerCount);
        sb.append("\nDaggers Found = " + daggersFound);
        sb.append("\nLives = " + lives);
        sb.append("\nRandom Probes used = " + randomProbeCount);

        return sb.toString();
    }
}
