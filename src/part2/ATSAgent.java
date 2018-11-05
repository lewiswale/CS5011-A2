package part2;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import part1.KnowledgeSpace;

import java.util.ArrayList;
import java.util.Random;

public class ATSAgent {
    private int lives;
    private int daggerCount;
    private int daggersFound;
    private String[][] map;
    private KnowledgeSpace[][] knowledge;
    private int currentX, currentY;
    private int randomProbeCount = 0;
    private int ATSCount = 0;
    private ArrayList<Literal> literals = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> clauses = new ArrayList<>();

    /**
     * Constructor for ATS Agent
     * @param map map agent will solve
     */
    public ATSAgent(String[][] map) {
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
     * Make Single Point move
     * @param x x-coordinate for given cell
     * @param y y-coordinate for given cell
     * @return true if move was made, false otherwise
     */
    public boolean makeSPSMove(int x, int y) {
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
     * Attempts to make a Satisfiability check. If no valid move is found, resorts to a random probe.
     * @throws ParserException
     * @throws ContradictionException
     * @throws TimeoutException
     */
    public void makeATSMove() throws ParserException, ContradictionException, TimeoutException {
        literals = new ArrayList<>();
        clauses = new ArrayList<>();
        String KBU = buildKBU();
        System.out.println("KBU in DIMACS form:");
        System.out.println(KBU);
        System.out.println();
        parseDIMACS(KBU);
        if (!checkSafeProbe()) {
            while (!randomProbe()) {}
        }
    }

    /**
     * Loops through all literals, checking the satisifiability of each one
     * @return true if a literal fails to satisfy the formula, false if all satisfy the formula
     * @throws TimeoutException
     * @throws ContradictionException
     */
    public boolean checkSafeProbe() throws TimeoutException, ContradictionException {
        for (int n = 0; n < literals.size(); n++) {
            if (n > 0) {
                clauses.remove(clauses.size() - 1);
            }
            ArrayList<Integer> aim = new ArrayList<>();
            aim.add((n + 1));
            clauses.add(aim);

            ISolver solver = SolverFactory.newDefault();
            solver.newVar(literals.size()+1);
            solver.setExpectedNumberOfClauses(clauses.size());

            for (int i = 0; i < clauses.size(); i++) {
                ArrayList clause = clauses.get(i);
                int[] arrayClause = new int[clause.size()];
                for (int j = 0; j < clause.size(); j++) {
                    arrayClause[j] = (int) clause.get(j);
                }
                solver.addClause(new VecInt(arrayClause));
            }

            IProblem problem = solver;

            System.out.println("========================");
            System.out.println("Proving ~D(" + literals.get(n).y + ", " + literals.get(n).x + ")");

            if (!problem.isSatisfiable()) {
                System.out.println("Formula is NOT satisfiable. Cell is safe.");
                System.out.println("========================");
                ATSCount++;
                probeCell(n);
                return true;
            } else {
                System.out.println("Formula is satisfiable. Cannot be sure cell is safe.");
                System.out.println("========================");
            }
        }
        System.out.println("Cannot be certain any cell is safe. Must resort to random probe.");
        System.out.println("========================");
        return false;
    }

    /**
     * Extracts cell information from given literal
     * @param n literal index
     */
    public void probeCell(int n) {
        Literal literal = literals.get(n);
        int x = literal.x;
        int y = literal.y;
        currentX = x;
        currentY = y;
        System.out.println("Satisfiabilty Test Inspection on (" + y + ", " + x + ")");
        knowledge[x][y].setValue(map[x][y]);
        inspectValue(knowledge[x][y]);
        knowledge[x][y].setInspected(true);
        System.out.println(this);
    }

    /**
     * Build a list of clauses in DIMACS form in order to build SAT4J ISolver.
     * @param cnf DIMACS KBU
     */
    public void parseDIMACS(String cnf) {
        StringBuilder sb = new StringBuilder();
        boolean inClause = false;
        int negative = 1;
        boolean ignoreSpace = false;
        ArrayList<Integer> clause =  new ArrayList<>();

        for (int i = 0; i < cnf.length(); i++) {
            char currentChar = cnf.charAt(i);
            if (currentChar == '(') {
                inClause = true;
                clause = new ArrayList<>();
                sb = new StringBuilder();
                continue;
            }

            if (inClause) {
                if (currentChar == '~') {
                    negative = -1;
                } else if (currentChar == ' ') {
                    if (!ignoreSpace) {
                        int literal = Integer.parseInt(sb.toString()) * negative;
                        clause.add(literal);
                        negative = 1;
                        sb = new StringBuilder();
                    }
                } else if (currentChar == ')') {
                    ignoreSpace = false;
                    inClause = false;
                    int literal = Integer.parseInt(sb.toString()) * negative;
                    clause.add(literal);
                    clauses.add(clause);
                    negative = 1;
                } else if (currentChar == '|') {
                    if (!ignoreSpace) {
                        ignoreSpace = true;
                    } else {
                        int literal = Integer.parseInt(sb.toString()) * negative;
                        clause.add(literal);
                        negative = 1;
                        sb = new StringBuilder();
                    }
                } else {
                    sb.append(currentChar);
                }
            }
        }
    }

    /**
     * Build CNF KBU from propositional cell formulas
     * @return CNF in String format
     * @throws ParserException
     */
    public String buildKBU() throws ParserException {
        ArrayList<String> formulas = new ArrayList<>();
        for (int i = 0; i < knowledge.length; i++) {
            for (int j = 0; j < knowledge[0].length; j++) {
                KnowledgeSpace current = knowledge[i][j];
                if (countCoveredNeighbours(current) > 0 && !current.getValue().equals("x") && !current.getValue().equals("D")) {
                    formulas.add(getCellFormula(current));  //Get propositional logic formulas for each cell
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < formulas.size(); i++) {     //Build single formula (KBU)
            sb.append("(");
            sb.append(formulas.get(i));
            sb.append(")");
            if (i < formulas.size()-1) {
                sb.append(" & ");
            }
        }

        FormulaFactory f = new FormulaFactory();
        PropositionalParser p = new PropositionalParser(f);
        Formula formula = p.parse(sb.toString());
        String cnf = formula.cnf().toString();              //Convert KBU to CNF

        System.out.println("Unique Literals: ");

        for (int i = 0; i < literals.size(); i++) {
            System.out.println((i+1) + ": " + literals.get(i).name);
        }

        System.out.println("Before CNF:");
        System.out.println(sb.toString());

        System.out.println("CNF KBU:");
        System.out.println(cnf);
        System.out.println();

        boolean buildingLiteral = false;
        for (int i = 0; i < cnf.length(); i++) {            //Converting CNF to DIMACS CNF
            char currentChar = cnf.charAt(i);
            if (currentChar == '@') {
                buildingLiteral = true;
                sb = new StringBuilder();
            }

            if (buildingLiteral) {
                if (currentChar != ' ' && currentChar != ')') {
                    sb.append(currentChar);
                } else {
                    buildingLiteral = false;
                    String currentLiteral = sb.toString();
                    for (int j = 0; j < literals.size(); j++) {
                        if (literals.get(j).name.equals(currentLiteral)) {
                            cnf = cnf.substring(0, i - currentLiteral.length()) + (j+1) + cnf.substring(i);
                            i -= currentLiteral.length();
                            break;
                        }
                    }
                }
            }
        }

        return cnf;
    }

    /**
     * Build propositional logic formula for a given cell
     * @param current current cell
     * @return String containing formula for current cell
     */
    public String getCellFormula(KnowledgeSpace current) {
        int x = current.getX();
        int y = current.getY();

        int xStart = Math.max(x-1, 0);
        int yStart = Math.max(y-1, 0);
        int xEnd = Math.min(x+1, knowledge.length-1);
        int yEnd = Math.min(y+1, knowledge.length-1);

        ArrayList<Literal> vars = new ArrayList<>();

        for (int i = xStart; i <= xEnd; i++) {
            for (int j = yStart; j <= yEnd; j++) {
                KnowledgeSpace neighbour = knowledge[i][j];
                if (neighbour.getValue().equals("x")) {
                    Literal literal = new Literal();
                    literal.x = i;
                    literal.y = j;
                    boolean matchFound = false;
                    for (int k = 0; k < literals.size(); k++) {
                        Literal l = literals.get(k);
                        if (l.x == literal.x && l.y == literal.y) {
                            literal.name = l.name;
                            matchFound = true;
                            break;
                        }
                    }
                    if (!matchFound) {
                        literal.name = "@RESERVED_CNF_" + literals.size();      //Must use specific naming format so LogicNG doesn't break
                        literals.add(literal);
                    }
                    vars.add(literal);
                }
            }
        }

        int numberOfVars = vars.size();
        String[] formulas = new String[numberOfVars];
        StringBuilder sb;

        for (int i = 0; i < numberOfVars; i++) {
            sb = new StringBuilder();
            for (int j = 0; j < numberOfVars; j++) {
                if (j != i) {
                    sb.append("~");
                }
                sb.append(vars.get(j).name);
                if (j < numberOfVars-1) {
                    sb.append(" & ");
                }
            }
            formulas[i] = sb.toString();
        }

        sb = new StringBuilder();
        for (int i = 0; i < numberOfVars; i++) {
            sb.append("(");
            sb.append(formulas[i]);
            sb.append(")");
            if (i < numberOfVars - 1) {
                sb.append(" | ");
            }
        }

        return sb.toString();
    }

    /**
     * Perform a random probe
     * @return true once a covered cell has been probed, false otherwise
     */
    public boolean randomProbe() {
        Random r = new Random();
        int x = r.nextInt(knowledge.length);
        int y = r.nextInt(knowledge.length);

        currentX = x;
        currentY = y;


        KnowledgeSpace current = knowledge[x][y];

        if (current.getValue().equals("x")) {
            System.out.println("Random Probe at (" + y + ", " + x + ")");
            current.setValue(map[x][y]);
            inspectValue(current);
            current.setInspected(true);
            randomProbeCount++;
            return true;
        }

        return false;
    }

    /**
     * Checks win conditions
     * @return true if game over, false otherwise
     */
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

    /**
     * Reveals all neighbours of given cell
     * @param x x-coordinate for given cell
     * @param y y-coordinate for given cell
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
                        revealNeighbours(knowledge[i][j].getX(), knowledge[i][j].getY());
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
                            return true;
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
                            return true;
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
     * @return agent life count
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
        sb.append("\nATS moves made = " + ATSCount);
        sb.append("\nRandom moves made = " + randomProbeCount);

        return sb.toString();
    }
}
