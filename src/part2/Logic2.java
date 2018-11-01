package part2;

import part1.World;

public class Logic2 {
    public static void main(String[] args) {
        World w = World.EASY1;
        ATSAgent agent = new ATSAgent(w.map);

        while (!agent.isComplete()) {
            System.out.println("\nSINGLE POINT STRATEGY\n========================");

            while (!agent.isComplete()) {
                boolean moveMadeInLoop = false;

                for (int i = 0; i < w.map.length; i++) {
                    for (int j = 0; j < w.map.length; j++) {
                        if (agent.isComplete()) {
                            break;
                        }
                        boolean moveMade = agent.makeMove(i, j);
                        if (!moveMadeInLoop && moveMade) {
                            moveMadeInLoop = moveMade;
                        }
                        System.out.println(agent);
                        System.out.println("========================");
                    }
                }

                if (!agent.isComplete() && !moveMadeInLoop) {
                    boolean newMoveMade = false;
                    while (!newMoveMade) {
                        newMoveMade = agent.randomProbe();
                        System.out.println(agent);
                        System.out.println("========================");
                    }
                }
            }

            System.out.println("FINAL OUTCOME");
            System.out.println(agent);
            System.out.println("========================");
            System.out.println("Actual map for comparison...");
            System.out.println(w);
        }
    }
}
