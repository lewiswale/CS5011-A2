package part2;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;
import part1.World;

public class Logic2 {
    public static void main(String[] args) throws ParserException, TimeoutException, ContradictionException {
        World w = World.HARD1;
        ATSAgent agent = new ATSAgent(w.map);

        while (!agent.isComplete()) {
            System.out.println("\nSINGLE POINT STRATEGY\n========================");

            boolean moveMadeInLoop = false;

            for (int i = 0; i < w.map.length; i++) {
                for (int j = 0; j < w.map.length; j++) {
                    if (agent.isComplete()) {
                        break;
                    }
                    boolean moveMade = agent.makeSPSMove(i, j);
                    if (!moveMadeInLoop && moveMade) {
                        moveMadeInLoop = moveMade;
                    }
                    System.out.println(agent);
                    System.out.println("========================");
                }
            }

            agent.makeATSMove();
        }

        System.out.println("FINAL OUTCOME");
        System.out.println(agent);
        System.out.println("========================");
        System.out.println("Actual map for comparison...");
        System.out.println(w);
    }
}
