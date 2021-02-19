
public class Command {

    public static enum CommandType {
        NOTHING, MOVE, DIG, SHOOT, BANANA, SNOWBALL,
    };

    private int wormId;
    private CommandType cmd;
    private Coord target;
    private Coord.Direction direction;

    // ctor
    public Command() {
        wormId = 0;
        setNothing();
    }

    public Command(int id) {
        wormId = id;
        setNothing();
    }

    // clone
    public Object clone() throws CloneNotSupportedException {
        Command c = new Command(wormId);
        c.cmd = cmd;
        if (target != null)
            c.target = (Coord) target.clone();
        c.direction = direction;
        return c;
    }
    // getter wormid
    public int getWormId() {
        return wormId;
    }
    
    // setter wormid
    public void setWormId(int wormId) {
        this.wormId = wormId;
    }

    // getter cmd
    public CommandType getCmd() {
        return cmd;
    }
    
    // getter target
    public Coord getTarget() {
        return target;
    }

    // getter direction
    public Coord.Direction getDirection() {
        return direction;
    }

    // Melakukan Nothing
    public void setNothing() {
        cmd = CommandType.NOTHING;
        target = null;
        direction = null;
    }
    
    // Melakukan Move
    public void setMove(Coord to) {
        cmd = CommandType.MOVE;
        target = to;
        direction = null;
    }

    // Melakukan Dig
    public void setDig(Coord block) {
        cmd = CommandType.DIG;
        target = block;
        direction = null;
    }

    // Melakukan Shoot
    public void setShoot(Coord.Direction dir) {
        cmd = CommandType.SHOOT;
        target = null;
        direction = dir;
    }

    // Menggunakan Banana
    public void setBanana(Coord target) {
        cmd = CommandType.BANANA;
        this.target = target;
        direction = null;
    }

    // Menggunakan SnowBall
    public void setSnowball(Coord target) {
        cmd = CommandType.SNOWBALL;
        this.target = target;
        direction = null;
    }

    // String
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
    
    // format untuk string cmd
    public String cmdString(int roundNo) {
        return String.format("C;%d;%s", roundNo, toString());
    }
}
