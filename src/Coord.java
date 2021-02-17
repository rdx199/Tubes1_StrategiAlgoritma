
public class Coord {
    private int x, y;

    public enum Direction {
        N, NE, E, SE, S, SW, W, NW,
    };

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coord(final Coord src) {
        x = src.x;
        y = src.y;
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

    public void moveToDirection(Direction dir) {
        switch (dir) {
        case N:
            y -= 1;
            break;
        case NE:
            y -= 1;
        case E:
            x += 1;
            break;
        case SE:
            x += 1;
        case S:
            y += 1;
            break;
        case SW:
            y += 1;
        case W:
            x -= 1;
            break;
        case NW:
            x -= 1;
            y -= 1;
            break;
        }
    }

    public double distance(final Coord to) {
        int dx = x - to.x;
        int dy = y - to.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean isBounded(final BBox bbox) {
        return bbox.isCoordInBound(this);
    }
}
