package part1;

public class Logic1 {
    public static void main(String[] args) {
        World w = World.EASY1;
        System.out.println("Map to be used:\n");
        System.out.println(w);

        SPSAgent agent = new SPSAgent(w.map);

        System.out.println("RANDOM PROBE");
        System.out.println("========================");
        while (!agent.isComplete()) {
            agent.randomProbe();
            System.out.println(agent);
        }

        winOrLose(agent);

        agent = new SPSAgent(w.map);

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

        winOrLose(agent);

        System.out.println("FINAL OUTCOME");
        System.out.println(agent);
        System.out.println("========================");
        System.out.println("Actual map for comparison...");
        System.out.println(w);
    }

    public static void winOrLose(SPSAgent agent) {
        if (agent.getLives() == 0) {
            System.out.println("NO MORE LIVES. YOU LOST.");
        } else {
            System.out.println("YOU WON!!!");
        }
    }
}
