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
            score += w.getHealth() * 50;
            Map map = state.getMap();
            if (map.getCell(w.getPos()).type == Map.CellType.LAVA)
                score -= 1000.0;
            Coord pos = new Coord(w.getPos());
            for (int x = -1; x < 2; x++)
                for (int y = -1; y < 2; y++) {
                    try {
                        Map.Cell cell = map.getCell(pos.getX() + x,
                                pos.getY() + y);
                        if (cell.type == Map.CellType.DIRT)
                            score -= 1;
                    } catch (IndexOutOfBoundsException e) {
                    }
                }
            try {
                WormExt we = (WormExt) w;
                score += we.getBananaCount() * 20;
                score += we.getSnowballCount() * 20;
            } catch (ClassCastException e) {
            }
        }

        Player opponentPlayer = state.getPlayerByID(opponentPid);
        for (Worm w : opponentPlayer.getWorms()) {
            if (w.getHealth() <= 0)
                score += 1000.0;
            score -= w.getHealth() * 50;
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
                        || (posw.distance(pos) < to.getPos().distance(posw))) {
                    to = w2;
                }
            }
            if (to == null)
                continue;
            Coord posw2 = to.getPos();
            double dist = posw2.distance(posw);
            assert dist > 0.0;
            if (dist >= 2)
                score -= dist * 8;
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
        double thisScore = scoreState(state);
        double otherScore = scoreState(other);
        return thisScore > otherScore;
    }

}
