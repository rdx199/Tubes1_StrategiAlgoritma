public class GreedySelector extends MoveSelector {

    public GreedySelector() {
    }

    private double scoreState(State state) {
        // TODO: Scoring system
        double score = 0.0;

        int myPid = state.getMyPlayerID();
        int opponentPid = (myPid == 1) ? 2 : 1;

        Player myPlayer = state.getPlayerByID(myPid);
        for (Worm w : myPlayer.getWorms()) {
            // TODO: Dead worm?
            // TODO: Health?
            // TODO: On lava?
        }

        Player opponentPlayer = state.getPlayerByID(opponentPid);
        for (Worm w : opponentPlayer.getWorms()) {
            // TODO: Dead worm?
            // TODO: Health?
            // TODO: On lava?
        }

        // TODO: Distance to enemy?

        return score;
    }

    @Override
    public boolean isStateBetter(State state, State other) {
        return scoreState(state) > scoreState(other);
    }

}
