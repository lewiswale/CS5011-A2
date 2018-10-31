import agents.LogicalAgent;

public class Part1 {
    public static void main(String[] args) {
        World w = World.EASY1;
        System.out.println("Map to be used:\n");
        System.out.println(w);

        LogicalAgent agent = new LogicalAgent(w.map);

        System.out.println("RANDOM PROBE");
        System.out.println("========================");
        while (!agent.isComplete()) {
            agent.randomProbe();
            System.out.println(agent);
        }

        winOrLose(agent);

        agent = new LogicalAgent(w.map);

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
        System.out.println(w);
    }

    public static void winOrLose(LogicalAgent agent) {
        if (agent.getLives() == 0) {
            System.out.println("NO MORE LIVES. YOU LOST.");
        } else {
            System.out.println("YOU WON!!!");
        }
    }
}
