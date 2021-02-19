
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

    public Object clone() throws CloneNotSupportedException {
        Command c = new Command(wormId);
        c.cmd = cmd;
        if (target != null)
            c.target = (Coord) target.clone();
        c.direction = direction;
        return c;
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
        switch (cmd) {
        case NOTHING:
            return "nothing";
        case MOVE:
            return String.format("move %d %d", target.getX(), target.getY());
        case DIG:
            return String.format("dig %d %d", target.getX(), target.getY());
        case SHOOT:
            return String.format("shoot %s", direction.toString());
        case BANANA:
            return String.format("banana %d %d", target.getX(), target.getY());
        case SNOWBALL:
            return String.format("snowball %d %d", target.getX(),
                    target.getY());
        }
        return null;
    }

    public String cmdString(int roundNo) {
        return String.format("C;%d;%s", roundNo, toString());
    }
}
