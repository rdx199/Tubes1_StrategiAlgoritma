import java.util.HashMap;
import java.util.Iterator;

public class CommandExecutor {

    public static class InvalidCommandException extends RuntimeException {
        private static final long serialVersionUID = 1_00_00L;

        public final int playerId;
        public final Command command;

        private static String print(int pid, Command cmd) {
            return String.format("Invalid command for player %d: %s", pid,
                    cmd.toString());
        }

        public InvalidCommandException(int pid, Command cmd) {
            super(InvalidCommandException.print(pid, cmd));
            playerId = pid;
            command = cmd;
        }

        public InvalidCommandException(int pid, Command cmd, Throwable cause) {
            super(InvalidCommandException.print(pid, cmd), cause);
            playerId = pid;
            command = cmd;
        }
    }

    private static class TempKey {
        public final int playerId, wormId;

        public TempKey(int pid, int wid) {
            playerId = pid;
            wormId = wid;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + playerId;
            result = prime * result + wormId;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TempKey other = (TempKey) obj;
            if (playerId != other.playerId)
                return false;
            if (wormId != other.wormId)
                return false;
            return true;
        }
    }

    public static void execute(State state,
            java.util.Map<Integer, Command> cmdMap)
            throws InvalidCommandException {
        Iterable<Integer> entries = cmdMap.keySet();

        // Move
        for (Integer i : entries) {
            Command cmd = cmdMap.get(i);
            if (cmd.getCmd() != Command.CommandType.MOVE) {
                continue;
            }
            Coord target = cmd.getTarget();
            Worm worm = state.getPlayerByID(i).getWormByID(cmd.getWormId());
            if (worm.getHealth() <= 0) {
                throw new InvalidCommandException(i, cmd);
            }
            Map map = state.getMap();
            Map.Cell cell = map.getCell(target);
            switch (cell.type) {
            case AIR:
            case LAVA:
                break;
            default:
                throw new InvalidCommandException(i, cmd);
            }
            cell.goingToOccupy = true;
            map.setCell(target, cell);
        }

        for (Integer i : entries) {
            Command cmd = cmdMap.get(i);
            if (cmd.getCmd() != Command.CommandType.MOVE) {
                continue;
            }
            Coord target = cmd.getTarget();
            Worm worm = state.getPlayerByID(i).getWormByID(cmd.getWormId());
            Map map = state.getMap();
            Map.Cell cell = map.getCell(target);
            if (cell.goingToOccupy) {
                cell.goingToOccupy = false;
                map.setCell(target, cell);
                worm.setHealth(worm.getHealth() - state.getPushDamage());
                if (worm.getHealth() <= 0) {
                    cell = map.getCell(worm.getPos());
                    cell.occupied = false;
                    map.setCell(worm.getPos(), cell);
                }
            } else {
                worm.setX(target.getX());
                worm.setY(target.getY());
                if (state.getMap().getCell(target).hasHpack) {
                    worm.setHealth(worm.getHealth() + 10); // XXX: Health
                }
                cell.occupied = true;
                map.setCell(target, cell);
                cell = map.getCell(worm.getPos());
                cell.occupied = false;
                map.setCell(worm.getPos(), cell);
            }
        }

        // Dig
        for (Integer i : entries) {
            Command cmd = cmdMap.get(i);
            if (cmd.getCmd() != Command.CommandType.DIG) {
                continue;
            }
            Coord target = cmd.getTarget();
            Worm worm = state.getPlayerByID(i).getWormByID(cmd.getWormId());
            if (worm.getHealth() <= 0) {
                throw new InvalidCommandException(i, cmd);
            }
            Map map = state.getMap();
            Map.Cell cell = map.getCell(target);
            switch (cell.type) {
            case DIRT:
                cell.type = Map.CellType.AIR;
                map.setCell(target, cell);
                break;
            case DEEP_SPACE:
                throw new InvalidCommandException(i, cmd);
            default:
            }
        }

        // Alive map
        HashMap<TempKey, Worm> alive = new HashMap<TempKey, Worm>();

        for (Player p : state.getPlayers())
            for (Worm w : p.getWorms())
                if (w.getHealth() > 0)
                    alive.put(new TempKey(w.getPlayerID(), w.getID()), w);

        // Banana
        for (Integer i : entries) {
            Command cmd = cmdMap.get(i);
            if (cmd.getCmd() != Command.CommandType.BANANA) {
                continue;
            }
            Coord target = cmd.getTarget();
            WormExt worm;
            try {
                worm = (WormExt) state.getPlayerByID(i)
                        .getWormByID(cmd.getWormId());
            } catch (ClassCastException e) {
                throw new InvalidCommandException(i, cmd);
            }
            if (!alive.containsKey(
                    new TempKey(worm.getPlayerID(), worm.getID()))) {
                throw new InvalidCommandException(i, cmd);
            }
            worm.setBananaCount(worm.getBananaCount() - 1);
            Map map = state.getMap();
            int range = worm.getBananaRadius();
            int damage = worm.getBananaDamage();
            for (Worm w : alive.values()) {
                Coord pos = w.getPos();
                int dx = pos.getX() - target.getX();
                if (dx < 0)
                    dx = -dx;
                int dy = pos.getY() - target.getY();
                if (dy < 0)
                    dy = -dy;
                if (dx + dy > range)
                    continue;
                w.setHealth(w.getHealth()
                        - (int) (damage / (pos.distance(target) + 1)));
            }
            for (int x = -range; x <= range; x++) {
                int end = (x < 0) ? -x : x;
                for (int y = -end; y <= end; y++) {
                    Coord pos = new Coord(target.getX() + x, target.getY() + y);
                    Map.Cell cell = map.getCell(pos);
                    switch (cell.type) {
                    case AIR:
                        if (cell.occupied) {
                            Worm occupier = state.occupier(pos);
                            if ((occupier != null)
                                    && (occupier.getHealth() > 0)) {
                                occupier.setHealth(
                                        occupier.getHealth() - (int) (damage
                                                / (pos.distance(target) + 1)));
                                if (occupier.getHealth() <= 0) {
                                    cell.occupied = false;
                                    map.setCell(pos, cell);
                                }
                            }
                        }
                        break;
                    case DIRT:
                        cell.type = Map.CellType.AIR;
                        map.setCell(pos, cell);
                        break;
                    default:
                    }
                }
            }
        }

        {
            Map map = state.getMap();
            Iterator<Worm> it = alive.values().iterator();
            while (it.hasNext()) {
                Worm worm = it.next();
                if (worm.getHealth() > 0)
                    continue;
                Coord pos = worm.getPos();
                Map.Cell cell = map.getCell(pos);
                cell.occupied = false;
                map.setCell(pos, cell);
                it.remove();
            }
        }
    }

}
