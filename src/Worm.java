import org.json.JSONException;
import org.json.JSONObject;

public class Worm implements Cloneable {

    private final int id, playerId;
    private int health;
    private Coord pos;

    private int diggingRange;
    private int movementRange;
    private int roundsUntilUnfrozen;

    private String profession;

    // TODO: Extended stuff (in new class)

    public Worm(int id, int playerId) {
        this.id = id;
        this.playerId = playerId;
        health = 0;
        pos = new Coord(0, 0);
        diggingRange = 0;
        movementRange = 0;
        roundsUntilUnfrozen = 0;
        profession = null;
    }

    public Worm(final Worm src) {
        id = src.id;
        playerId = src.playerId;
        health = src.health;
        pos = new Coord(src.pos);
        diggingRange = src.diggingRange;
        movementRange = src.movementRange;
        roundsUntilUnfrozen = src.roundsUntilUnfrozen;
        profession = src.profession;
    }

    public Object clone() throws CloneNotSupportedException {
        Worm w = (Worm) this.clone();
        w.pos = (Coord) w.pos.clone();
        return w;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Coord getPos() {
        return pos;
    }

    public int getRoundsUntilUnfrozen() {
        return roundsUntilUnfrozen;
    }

    public void setRoundsUntilUnfrozen(int roundsUntilUnfrozen) {
        this.roundsUntilUnfrozen = roundsUntilUnfrozen;
    }

    public int getID() {
        return id;
    }

    public int getPlayerID() {
        return playerId;
    }

    public int getDiggingRange() {
        return diggingRange;
    }

    public int getMovementRange() {
        return movementRange;
    }

    public String getProfession() {
        return profession;
    }

    public int getX() {
        return pos.getX();
    }

    public void setX(int x) {
        pos.setX(x);
    }

    public int getY() {
        return pos.getY();
    }

    public void setY(int y) {
        pos.setY(y);
    }

    public void moveToDirection(Coord.Direction dir) {
        pos.moveToDirection(dir);
    }

    public void moveToDirection(Coord.Direction dir, int amount) {
        pos.moveToDirection(dir, amount);
    }

    public boolean isBounded(final BBox bbox) {
        return pos.isBounded(bbox);
    }

    public void parseJSON(JSONObject json)
            throws JSONException, IDMismatchException {
        if (id != json.getInt("id")) {
            throw new IDMismatchException(
                    String.format("Worm id mismatch! (expected %d, got %d)", id,
                            json.getInt("id")));
        }

        health = json.getInt("health");

        pos = Coord.fromJSON(json.getJSONObject("position"));

        diggingRange = json.getInt("diggingRange");
        movementRange = json.getInt("movementRange");
        roundsUntilUnfrozen = json.getInt("roundsUntilUnfrozen");
        profession = json.getString("profession");
    }
}
