package part2;

import org.logicng.io.parsers.ParserException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;
import part1.World;
public class Logic2 {
    public static void main(String[] args) throws ParserException, TimeoutException, ContradictionException {
        World w = getWorld(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        ATSAgent agent = new ATSAgent(w.map);

        System.out.println("\nSINGLE POINT AND SATISFIABILITY TEST STRATEGY\n========================");
        while (!agent.isComplete()) {

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

            if (!moveMadeInLoop) {
                agent.makeATSMove();
                System.out.println(agent);
                System.out.println("========================");
            }
        }

        winOrLose(agent);

        System.out.println("FINAL OUTCOME");
        System.out.println(agent);
        System.out.println("========================");
        System.out.println("Actual map for comparison...");
        System.out.println(w);
    }

    public static World getWorld(int diff, int n) {
        switch (diff) {
            case 1:
                switch (n) {
                    case 1:
                        return World.EASY1;
                    case 2:
                        return World.EASY2;
                    case 3:
                        return World.EASY3;
                    case 4:
                        return World.EASY4;
                    case 5:
                        return World.EASY5;
                    case 6:
                        return World.EASY6;
                    case 7:
                        return World.EASY7;
                    case 8:
                        return World.EASY8;
                    case 9:
                        return World.EASY9;
                    case 10:
                        return World.EASY10;
                }
                break;
            case 2:
                switch (n) {
                    case 1:
                        return World.MEDIUM1;
                    case 2:
                        return World.MEDIUM2;
                    case 3:
                        return World.MEDIUM3;
                    case 4:
                        return World.MEDIUM4;
                    case 5:
                        return World.MEDIUM5;
                    case 6:
                        return World.MEDIUM6;
                    case 7:
                        return World.MEDIUM7;
                    case 8:
                        return World.MEDIUM8;
                    case 9:
                        return World.MEDIUM9;
                    case 10:
                        return World.MEDIUM10;
                }
                break;
            case 3:
                switch (n) {
                    case 1:
                        return World.HARD1;
                    case 2:
                        return World.HARD2;
                    case 3:
                        return World.HARD3;
                    case 4:
                        return World.HARD4;
                    case 5:
                        return World.HARD5;
                    case 6:
                        return World.HARD6;
                    case 7:
                        return World.HARD7;
                    case 8:
                        return World.HARD8;
                    case 9:
                        return World.HARD9;
                    case 10:
                        return World.HARD10;
                }
                break;
        }
        return null;
    }

    public static void winOrLose(ATSAgent agent) {
        if (agent.getLives() == 0) {
            System.out.println("NO MORE LIVES. YOU LOST.");
        } else {
            System.out.println("YOU WON!!!");
        }
    }
}
