import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingleThreadedIterator extends MoveIterator {

    private static class CmdMoveIter implements Iterator<Command> {
        Worm w;
        Command cmd;
        Coord.Direction dir;

        public CmdMoveIter(Worm w) {
            this.w = w;
            cmd = new Command();
            dir = Coord.Direction.N;
            Coord c = new Coord(w.getPos());
            c.moveToDirection(dir);
            cmd.setMove(c);
        }

        @Override
        public boolean hasNext() {
            return dir != Coord.Direction.NW;
        }

        @Override
        public Command next() throws NoSuchElementException {
            switch (dir) {
            case N:
                dir = Coord.Direction.NE;
                break;
            case NE:
                dir = Coord.Direction.E;
                break;
            case E:
                dir = Coord.Direction.SE;
                break;
            case SE:
                dir = Coord.Direction.S;
                break;
            case S:
                dir = Coord.Direction.SW;
                break;
            case SW:
                dir = Coord.Direction.W;
                break;
            case W:
                dir = Coord.Direction.NW;
                break;
            case NW:
                throw new NoSuchElementException();
            }
            Coord c = new Coord(w.getPos());
            c.moveToDirection(dir);
            cmd.setMove(c);
            return cmd;
        }
    }

    private static class SingleWormIterator implements Iterator<Command> {
        Worm w;
        Iterator<Command> subiter;

        enum IterType {
            MOVE, DONE
        };

        IterType it;

        public SingleWormIterator(Worm w) {
            this.w = w;
            subiter = new CmdMoveIter(w);
            it = IterType.MOVE;
        }

        @Override
        public boolean hasNext() {
            return it != IterType.DONE;
        }

        @Override
        public Command next() throws NoSuchElementException {
            if ((subiter != null) && (subiter.hasNext())) {
                Command cmd = subiter.next();
                cmd.setWormId(w.getID());
                return cmd;
            }
            switch (it) {
            case MOVE:
                it = IterType.DONE;
                subiter = null;
                return new Command();
            case DONE:
            }
            throw new NoSuchElementException();
        }
    }

    private static class PlayerIterator implements Iterator<Command> {
        Worm[] arr;
        int ix;
        Iterator<Command> subiter;

        public PlayerIterator(Player p) {
            arr = p.getWorms();
            ix = 0;
            subiter = null;
        }

        @Override
        public boolean hasNext() {
            return ix != arr.length;
        }

        @Override
        public Command next() throws NoSuchElementException {
            if ((subiter != null) && (subiter.hasNext())) {
                return subiter.next();
            }
            subiter = new SingleWormIterator(arr[ix]);
            ix += 1;
            return this.next();
        }
    }

    public SingleThreadedIterator() {
    }

    @Override
    public Command iterateMove(State state, MoveSelector selector) {
        HashMap<Integer, Command> cmd = new HashMap<Integer, Command>();

        try {
            Command bestCmd = new Command();
            State best = (State) state.clone();
            State last = (State) best.clone();
            State cur = (State) best.clone();

            int myPid = state.getMyPlayerID();
            Iterator<Command> itp = new PlayerIterator(
                    state.getPlayerByID(myPid));
            while (itp.hasNext()) {
                last.copyFrom(best);
                cmd.put(myPid, itp.next());
                int opponentPid = (myPid == 1) ? 2 : 1;
                Iterator<Command> ito = new PlayerIterator(
                        state.getPlayerByID(opponentPid));
                while (ito.hasNext()) {
                    cmd.put(opponentPid, ito.next());
                    cur.copyFrom(state);
                    try {
                        CommandExecutor.execute(cur, cmd);
                        if (selector.isStateBetter(last, cur)) {
                            State temp = cur;
                            cur = last;
                            last = temp;
                        }
                    } catch (CommandExecutor.InvalidCommandException e) {
                    }
                }
                if (selector.isStateBetter(last, best)) {
                    bestCmd = (Command) cmd.get(myPid).clone();
                    State temp = best;
                    best = last;
                    last = temp;
                }
            }

            return bestCmd;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
