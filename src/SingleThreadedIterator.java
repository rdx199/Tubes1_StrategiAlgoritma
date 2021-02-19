import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingleThreadedIterator extends MoveIterator {

    private static class SingleWormIterator implements Iterator<Command> {

        private static class CmdMoveIter implements Iterator<Command> {
            Coord orig;
            Command cmd;
            Coord.Direction dir;

            public CmdMoveIter(Coord from) {
                orig = new Coord(from);
                cmd = new Command();
                dir = Coord.Direction.N;
                Coord c = new Coord(orig);
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
                Coord c = new Coord(orig);
                c.moveToDirection(dir);
                cmd.setMove(c);
                return cmd;
            }
        }

        private static class CmdDigIter implements Iterator<Command> {
            Coord orig;
            Command cmd;
            Coord.Direction dir;

            public CmdDigIter(Coord from) {
                orig = new Coord(from);
                cmd = new Command();
                dir = Coord.Direction.N;
                Coord c = new Coord(orig);
                c.moveToDirection(dir);
                cmd.setDig(c);
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
                Coord c = new Coord(orig);
                c.moveToDirection(dir);
                cmd.setDig(c);
                return cmd;
            }
        }

        private static class CmdShootIter implements Iterator<Command> {
            Command cmd;
            Coord.Direction dir;

            public CmdShootIter() {
                cmd = new Command();
                dir = Coord.Direction.N;
                cmd.setShoot(dir);
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
                cmd.setShoot(dir);
                return cmd;
            }
        }

        private static class CmdBananaIter implements Iterator<Command> {
            WormExt w;
            Command cmd;

            static final int yst = -5;
            static final int endy = 6;
            static final int[] dlx = { 0, 1, -3, 4, -4, 5, -4, 5, -4, 5, -5, 6,
                -4, 5, -4, 5, -4, 5, -3, 4, 0, 1, };

            int x, endx, y;

            public CmdBananaIter(Worm worm) {
                try {
                    w = (WormExt) worm;
                    if (w.getBananaCount() <= 0)
                        throw new ClassCastException();
                    cmd = new Command();
                    y = yst;
                    x = dlx[(y - yst) * 2];
                    endx = dlx[(y - yst) * 2 + 1];
                } catch (ClassCastException e) {
                    cmd = null;
                }
            }

            @Override
            public boolean hasNext() {
                return (cmd != null)
                        && ((y != (endy - 1)) || (x != (endx - 1)));
            }

            @Override
            public Command next() throws NoSuchElementException {
                cmd.setBanana(new Coord(x + w.getX(), y + w.getY()));
                x++;
                if (x >= endx) {
                    y++;
                    if (y >= endy)
                        throw new NoSuchElementException();
                    x = dlx[(y - yst) * 2];
                    endx = dlx[(y - yst) * 2 + 1];
                }
                return cmd;
            }
        }

        private static class CmdSnowballIter extends CmdBananaIter {
            public CmdSnowballIter(Worm worm) {
                super(worm);
            }

            @Override
            public Command next() throws NoSuchElementException {
                Command ret = super.next();
                Coord c = ret.getTarget();
                ret.setSnowball(c);
                return ret;
            }
        }

        Worm w;
        Iterator<Command> subiter;

        enum IterType {
            MOVE, DIG, BANANA, SNOWBALL, SHOOT, DONE
        };

        IterType it;

        public SingleWormIterator(Worm w) {
            this.w = w;
            subiter = new CmdMoveIter(w.getPos());
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
                it = IterType.DIG;
                subiter = new CmdDigIter(w.getPos());
                return next();
            case DIG:
                it = IterType.BANANA;
                subiter = new CmdBananaIter(w);
                return next();
            case BANANA:
                it = IterType.SNOWBALL;
                subiter = new CmdSnowballIter(w);
                return next();
            case SNOWBALL:
                it = IterType.SHOOT;
                subiter = new CmdShootIter();
                return next();
            case SHOOT:
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
            return (ix != arr.length) || subiter.hasNext();
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
            BBox bbox = state.getMap().makeBBox();
            Command bestCmd = new Command();
            State best = (State) state.clone();
            State last = (State) best.clone();
            State cur = (State) best.clone();

            int myPid = state.getMyPlayerID();
            {
                cmd.put(myPid, bestCmd);
                int opponentPid = (myPid == 1) ? 2 : 1;
                Iterator<Command> ito = new PlayerIterator(
                        state.getPlayerByID(opponentPid));
                while (ito.hasNext()) {
                    Command cmdo = ito.next();
                    if ((cmdo.getTarget() != null)
                            && !cmdo.getTarget().isBounded(bbox))
                        continue;
                    cmd.put(opponentPid, cmdo);
                    cur.copyFrom(state);
                    try {
                        CommandExecutor.execute(cur, cmd);
                        if (selector.isStateBetter(best, cur)) {
                            State temp = cur;
                            cur = best;
                            best = temp;
                        }
                    } catch (CommandExecutor.InvalidCommandException e) {
                    }
                }
            }

            Iterator<Command> itp = new SingleWormIterator(
                    state.getPlayerByID(myPid)
                            .getWormByID(state.getCurrentWormID()));
            while (itp.hasNext()) {
                last.copyFrom(state);
                Command cmdp = itp.next();
                if ((cmdp.getTarget() != null)
                        && !cmdp.getTarget().isBounded(bbox))
                    continue;
                cmd.put(myPid, cmdp);
                boolean isMoveValid = false;
                int opponentPid = (myPid == 1) ? 2 : 1;
                Iterator<Command> ito = new PlayerIterator(
                        state.getPlayerByID(opponentPid));
                while (ito.hasNext()) {
                    Command cmdo = ito.next();
                    if ((cmdo.getTarget() != null)
                            && !cmdo.getTarget().isBounded(bbox))
                        continue;
                    cmd.put(opponentPid, cmdo);
                    cur.copyFrom(state);
                    try {
                        CommandExecutor.execute(cur, cmd);
                        isMoveValid = true;
                        if (selector.isStateBetter(last, cur)) {
                            State temp = cur;
                            cur = last;
                            last = temp;
                        }
                    } catch (CommandExecutor.InvalidCommandException e) {
                    }
                }
                if (!isMoveValid)
                    continue;
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
