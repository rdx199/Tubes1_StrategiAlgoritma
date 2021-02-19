import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.JSONObject;

public class Main {
    private static class DummySelector extends MoveSelector {

        public DummySelector() {
        }

        @Override
        public boolean isStateBetter(State state, State other) {
            return true;
        }

    }

    private static final String ROUNDS_DIRECTORY = "rounds";
    private static final String STATE_FILE_NAME = "state.json";

    /**
     * Read the current state, feed it to the bot, get the output and print it
     * to stdout
     *
     * @param args the args
     **/
    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);

        try {
            while (true) {
                int roundNumber = sc.nextInt();

                try {
                    String statePath = String.format("./%s/%d/%s",
                            ROUNDS_DIRECTORY, roundNumber, STATE_FILE_NAME);
                    String str = new String(
                            Files.readAllBytes(Paths.get(statePath)));
                    JSONObject json = new JSONObject(str);
                    State state = new State(json);

                    MoveIterator it = new SingleThreadedIterator();
                    Command command = it.iterateMove(state,
                            new GreedySelector());

                    System.out.format("C;%d;%s\n", roundNumber,
                            command.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.format("C;%d;nothing\n", roundNumber);
                }
            }
        } finally {
            sc.close();
        }
    }
}
