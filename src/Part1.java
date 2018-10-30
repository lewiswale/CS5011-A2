import agents.Agent;
import agents.SinglePointAgent;

public class Part1 {
    public static void main(String[] args) {
        World w = World.EASY2;
        System.out.println(w);

//        Agent agent = new Agent(w.map);
//        System.out.println(agent);
//        System.out.println("========================");
//
//        while (!agent.isComplete()) {
//            System.out.println("RANDOM MOVE");
//            agent.makeRandomMove();
//            System.out.println(agent);
//            System.out.println("========================");
//        }

        SinglePointAgent agent = new SinglePointAgent(w.map);

        while (!agent.isComplete()) {
            for (int i = 0; i < w.map.length; i++) {
                for (int j = 0; j < w.map.length; j++) {
                    if (agent.isComplete()) {
                        break;
                    }
                    agent.makeMove(j, i);
                    System.out.println(agent);
                }
            }

            if (!agent.isComplete()) {
                boolean newMoveMade = false;
                while (!newMoveMade) {
                    newMoveMade = agent.randomProbe();
                }
            }
        }

        System.out.println("FINAL OUTCOME");
        System.out.println(agent);
        System.out.println(w);
    }
}
