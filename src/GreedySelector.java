public class GreedySelector extends MoveSelector {

    public GreedySelector() {
    }

    /*
     * Create score for the state
     */
    private double scoreState(State state) {
        double score = 0.0;

        int myPid = state.getMyPlayerID();
        int opponentPid = (myPid == 1) ? 2 : 1;

        Player myPlayer = state.getPlayerByID(myPid);
        for (Worm w : myPlayer.getWorms()) {
            if (w.getHealth() <= 0)
                score -= 1000.0;
            score += w.getHealth();
            if (state.getMap().getCell(w.getPos()).type == Map.CellType.LAVA)
                score -= 500.0;
            try {
                WormExt we = (WormExt) w;
                score += we.getBananaCount() * 3;
                score += we.getSnowballCount() * 5;
            } catch (ClassCastException e) {
            }
        }

        Player opponentPlayer = state.getPlayerByID(opponentPid);
        for (Worm w : opponentPlayer.getWorms()) {
            if (w.getHealth() <= 0)
                score += 1000.0;
            score -= w.getHealth();
            if (state.getMap().getCell(w.getPos()).type == Map.CellType.LAVA)
                score += 500.0;
        }

        for (Worm w : myPlayer.getWorms()) {
            if (w.getHealth() <= 0)
                continue;
            Coord posw = w.getPos();
            Worm to = null;
            for (Worm w2 : opponentPlayer.getWorms()) {
                if (w2.getHealth() <= 0)
                    continue;
                Coord pos = w2.getPos();
                if ((to == null)
                        || (posw.distance(pos) < to.getPos().distance(pos))) {
                    to = w2;
                }
            }
            if (to == null)
                continue;
            Coord posw2 = to.getPos();
            double dist = posw2.distance(posw);
            assert dist > 0.0;
            if (dist >= 1.5)
                score += 30 / dist;
            int dx = posw.getX() - posw2.getX();
            int dy = posw.getY() - posw2.getY();
            if ((dx == 0) || (dy == 0) || (dx == dy) || (dx == -dy))
                score += 50;
        }

        return score;
    }

    /*
     * Return true if state is better
     */
    @Override
    public boolean isStateBetter(State state, State other) {
        return scoreState(state) > scoreState(other);
    }

}
