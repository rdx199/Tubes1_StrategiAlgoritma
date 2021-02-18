
public class Command {

    public static enum CommandType {
        NOTHING, MOVE, DIG, SHOOT, BANANA, SNOWBALL,
    };

    private int wormId;
    private CommandType cmd;
    private Coord target;
    private Coord.Direction direction;

    public Command() {
        wormId = 0;
        setNothing();
    }

    public Command(int id) {
        wormId = id;
        setNothing();
    }

    public int getWormId() {
        return wormId;
    }

    public void setWormId(int wormId) {
        this.wormId = wormId;
    }

    public CommandType getCmd() {
        return cmd;
    }

    public Coord getTarget() {
        return target;
    }

    public Coord.Direction getDirection() {
        return direction;
    }

    public void setNothing() {
        cmd = CommandType.NOTHING;
        target = null;
        direction = null;
    }

    public void setMove(Coord to) {
        cmd = CommandType.MOVE;
        target = to;
        direction = null;
    }

    public void setDig(Coord block) {
        cmd = CommandType.DIG;
        target = block;
        direction = null;
    }

    public void setShoot(Coord.Direction dir) {
        cmd = CommandType.SHOOT;
        target = null;
        direction = dir;
    }

    public void setBanana(Coord target) {
        cmd = CommandType.BANANA;
        this.target = target;
        direction = null;
    }

    public void setSnowball(Coord target) {
        cmd = CommandType.SNOWBALL;
        this.target = target;
        direction = null;
    }

    @Override
    public String toString() {
        return switch (cmd) {
        case NOTHING -> "nothing";
        case MOVE -> String.format("select %d;move %d %d", wormId,
                target.getX(), target.getY());
        case DIG -> String.format("select %d;dig %d %d", wormId, target.getX(),
                target.getY());
        case SHOOT -> String.format("select %d;shoot %s", wormId,
                direction.toString());
        case BANANA -> String.format("select %d;banana %d %d", wormId,
                target.getX(), target.getY());
        case SNOWBALL -> String.format("select %d;snowball %d %d", wormId,
                target.getX(), target.getY());
        };
    }

    public String cmdString(int roundNo) {
        return String.format("C;%d;%s", roundNo, toString());
    }
}
