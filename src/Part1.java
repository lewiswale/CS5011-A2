import agents.Agent;

public class Part1 {
    public static void main(String[] args) {
        World w = World.EASY1;
        System.out.println(w);

        Agent agent = new Agent(w.map);
        System.out.println(agent);
        System.out.println("========================");

        while (!agent.isComplete()) {
            System.out.println("RANDOM MOVE");
            agent.makeRandomMove();
            System.out.println(agent);
            System.out.println("========================");
        }

        System.out.println("FINAL OUTCOME");
        System.out.println(agent);
    }
}
