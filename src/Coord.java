import org.json.JSONObject;

public class Coord implements Cloneable {
    private int x, y;

    public enum Direction {
        N, NE, E, SE, S, SW, W, NW;

        @Override
        public String toString() {
            return switch (this) {
            case N -> "n";
            case NE -> "ne";
            case E -> "e";
            case SE -> "se";
            case S -> "s";
            case SW -> "sw";
            case W -> "w";
            case NW -> "nw";
            };
        }
    };

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coord(final Coord src) {
        x = src.x;
        y = src.y;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void moveToDirection(Direction dir, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount < 0");
        } else if (amount == 0) {
            return;
        }
        switch (dir) {
        case N:
            y -= amount;
            break;
        case NE:
            y -= amount;
        case E:
            x += amount;
            break;
        case SE:
            x += amount;
        case S:
            y += amount;
            break;
        case SW:
            y += amount;
        case W:
            x -= amount;
            break;
        case NW:
            x -= amount;
            y -= amount;
            break;
        }
    }

    public void moveToDirection(Direction dir) {
        moveToDirection(dir, 1);
    }

    public double distance(final Coord to) {
        int dx = x - to.x;
        int dy = y - to.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean isBounded(final BBox bbox) {
        return bbox.isCoordInBound(this);
    }

    public static Coord fromJSON(JSONObject json) {
        return new Coord(json.getInt("x"), json.getInt("y"));
    }
}