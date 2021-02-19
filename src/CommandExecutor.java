import java.util.HashMap;

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

        // Alive map
        HashMap<TempKey, Worm> alive = new HashMap<TempKey, Worm>();

        for (Player p : state.getPlayers())
            for (Worm w : p.getWorms())
                if (w.getHealth() > 0)
                    alive.put(new TempKey(w.getPlayerID(), w.getID()), w);

        // Move
        for (Integer i : entries) {
            Command cmd = cmdMap.get(i);
            if (cmd.getCmd() == Command.CommandType.NOTHING) {
                continue;
            }
            Worm worm = state.getPlayerByID(i).getWormByID(cmd.getWormId());
            if ((worm.getRoundsUntilUnfrozen() > 0) || !alive.containsKey(
                    new TempKey(worm.getPlayerID(), worm.getID()))) {
                throw new InvalidCommandException(i, cmd);
            }
            if (cmd.getCmd() != Command.CommandType.MOVE) {
                continue;
            }
            Coord target = cmd.getTarget();
            Map map = state.getMap();
            Map.Cell cell = map.getCell(target);
            if (cell.occupied) {
                throw new InvalidCommandException(i, cmd);
            }
            switch (cell.type) {
            case AIR:
            case LAVA:
                break;
            default:
                throw new InvalidCommandException(i, cmd);
            }
            cell.goingToOccupy += 1;
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
            if (cell.goingToOccupy > 1) {
                worm.setHealth(worm.getHealth() - state.getPushDamage());
            } else {
                cell.occupied = false;
                map.setCell(worm.getPos(), cell);
                worm.setX(target.getX());
                worm.setY(target.getY());
                if (state.getMap().getCell(target).hasHpack) {
                    worm.setHealth(worm.getHealth() + 10); // XXX: Health
                }
                cell.goingToOccupy = 0;
                cell.occupied = true;
                map.setCell(target, cell);
                cell = map.getCell(worm.getPos());
            }
        }

        // Dig
        for (Integer i : entries) {
            Command cmd = cmdMap.get(i);
            if (cmd.getCmd() != Command.CommandType.DIG) {
                continue;
            }
            Coord target = cmd.getTarget();
            Map map = state.getMap();
            Map.Cell cell = map.getCell(target);
            switch (cell.type) {
            case DIRT:
                cell.type = Map.CellType.AIR;
                map.setCell(target, cell);
                break;
            default:
                throw new InvalidCommandException(i, cmd);
            }
        }

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
            worm.setBananaCount(worm.getBananaCount() - 1);
            Map map = state.getMap();
            if (map.getCell(target).type == Map.CellType.DEEP_SPACE) {
                throw new InvalidCommandException(i, cmd);
            }
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
                if (w.getHealth() <= 0) {
                    Map.Cell cell = map.getCell(pos);
                    cell.occupied = false;
                    map.setCell(pos, cell);
                }
            }
            for (int x = -range; x <= range; x++) {
                int end = (x < 0) ? -x : x;
                for (int y = -end; y <= end; y++) {
                    Coord pos = new Coord(target.getX() + x, target.getY() + y);
                    Map.Cell cell;
                    try {
                        cell = map.getCell(pos);
                    } catch (IndexOutOfBoundsException e) {
                        continue;
                    }
                    switch (cell.type) {
                    case DIRT:
                        cell.type = Map.CellType.AIR;
                        break;
                    default:
                    }
                    cell.hasHpack = false;
                    map.setCell(pos, cell);
                }
            }
        }

        // Snowball
        for (Integer i : entries) {
            Command cmd = cmdMap.get(i);
            if (cmd.getCmd() != Command.CommandType.SNOWBALL) {
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
            worm.setSnowballCount(worm.getSnowballCount() - 1);
            Map map = state.getMap();
            if (map.getCell(target).type == Map.CellType.DEEP_SPACE) {
                throw new InvalidCommandException(i, cmd);
            }
            int range = worm.getSnowballRadius();
            int freeze = worm.getSnowballDuration();
            for (Worm w : alive.values()) {
                Coord pos = w.getPos();
                int dx = pos.getX() - target.getX();
                if (dx < 0)
                    dx = -dx;
                int dy = pos.getY() - target.getY();
                if (dy < 0)
                    dy = -dy;
                if (dy > dx)
                    dx = dy;
                if (dx > range)
                    continue;
                w.setRoundsUntilUnfrozen(w.getRoundsUntilUnfrozen() + freeze);
            }
        }

        // Shoot
        for (Integer i : entries) {
            Command cmd = cmdMap.get(i);
            if (cmd.getCmd() != Command.CommandType.SHOOT) {
                continue;
            }
            WormExt worm;
            try {
                worm = (WormExt) state.getPlayerByID(i)
                        .getWormByID(cmd.getWormId());
            } catch (ClassCastException e) {
                throw new InvalidCommandException(i, cmd);
            }
            Coord.Direction dir = cmd.getDirection();
            Map map = state.getMap();
            Coord from = worm.getPos();
            Coord pos = new Coord(from);
            int damage = worm.getWeaponDamage();
            float maxDist = (float) (worm.getWeaponRange() + 1);
            pos.moveToDirection(dir);
            while (from.distance(pos) < maxDist) {
                Map.Cell cell;
                try {
                    cell = map.getCell(pos);
                } catch (IndexOutOfBoundsException e) {
                    continue;
                }
                if (cell.type == Map.CellType.DIRT) {
                    break;
                }
                if (cell.occupied) {
                    boolean found = false;
                    for (Worm w : alive.values()) {
                        if (!w.getPos().equals(pos))
                            continue;
                        w.setHealth(w.getHealth() - damage);
                        if (w.getHealth() <= 0) {
                            cell.occupied = false;
                            map.setCell(pos, cell);
                        }
                        found = true;
                        break;
                    }
                    assert found;
                    break;
                }
                pos.moveToDirection(dir);
            }
        }

        // Remove the dead
        {
            Map map = state.getMap();
            for (Worm worm : alive.values()) {
                if (worm.getHealth() > 0)
                    continue;
                Coord pos = worm.getPos();
                Map.Cell cell = map.getCell(pos);
                cell.occupied = false;
                map.setCell(pos, cell);
            }
        }
    }

}
