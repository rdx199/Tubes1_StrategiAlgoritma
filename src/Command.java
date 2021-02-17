
public class Command {

    public enum CommandType {
        NOTHING, MOVE, DIG, SHOOT, BANANA, SNOWBALL,
    };

    private int wormId;
    private CommandType cmd;
    private Coord target;
    private Coord.Direction dir;

    // TODO: Constructor
    // TODO: Getter/setter
    // TODO: Command execute
    // TODO: Command output
}
