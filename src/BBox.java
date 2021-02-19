
public class BBox {
    private int xmin, xmax, ymin, ymax;

    public BBox(int xmin, int xmax, int ymin, int ymax) {
        if (xmin > xmax) {
            throw new IllegalArgumentException("xmin > xmax");
        }
        if (ymin > ymax) {
            throw new IllegalArgumentException("ymin > ymax");
        }

        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }

    public BBox(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("width <= 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height <= 0");
        }

        xmin = ymin = 0;
        xmax = width - 1;
        ymax = height - 1;
    }

    public int getXmin() {
        return xmin;
    }

    public void setXmin(int xmin) {
        if (xmin > xmax) {
            throw new IllegalArgumentException("xmin > xmax");
        }
        this.xmin = xmin;
    }

    public int getXmax() {
        return xmax;
    }

    public void setXmax(int xmax) {
        if (xmin > xmax) {
            throw new IllegalArgumentException("xmin > xmax");
        }
        this.xmax = xmax;
    }

    public int getYmin() {
        return ymin;
    }

    public void setYmin(int ymin) {
        if (ymin > ymax) {
            throw new IllegalArgumentException("ymin > ymax");
        }
        this.ymin = ymin;
    }

    public int getYmax() {
        return ymax;
    }

    public void setYmax(int ymax) {
        if (ymin > ymax) {
            throw new IllegalArgumentException("ymin > ymax");
        }
        this.ymax = ymax;
    }

    public boolean isCoordInBound(final Coord c) {
        int x = c.getX();
        int y = c.getY();
        return (x >= xmin) && (x <= xmax) && (y >= ymin) && (y <= ymax);
    }
}
