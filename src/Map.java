
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Map implements Cloneable {

    private int width, height;

    public static enum CellType {
        DEEP_SPACE, AIR, DIRT, LAVA,
    };

    public static class Cell implements Cloneable {
        public CellType type;
        public boolean hasHpack;
        public boolean occupied;

        // Temporary stuff
        public boolean goingToOccupy;

        public Cell(CellType type, boolean hasHpack, boolean occupied) {
            this.type = type;
            this.hasHpack = hasHpack;
            this.occupied = occupied;

            goingToOccupy = false;
        }

        public Cell() {
            type = CellType.DEEP_SPACE;
            hasHpack = false;
            occupied = false;

            goingToOccupy = false;
        }

        private static final int hpackFlag = 0x8000_0000;
        private static final int occupiedFlag = 0x4000_0000;

        private static final int goingToOccupyFlag = 0x8000;

        Cell(int data) {
            type = switch (data & 3) {
            case 0 -> CellType.DEEP_SPACE;
            case 1 -> CellType.AIR;
            case 2 -> CellType.DIRT;
            case 3 -> CellType.LAVA;
            default -> throw new IllegalArgumentException(
                    String.format("Invalid data %x", data));
            };
            hasHpack = (data & hpackFlag) != 0;
            occupied = (data & occupiedFlag) != 0;
            goingToOccupy = (data & goingToOccupyFlag) != 0;
        }

        static int toInt(Cell cell) {
            return switch (cell.type) {
            case DEEP_SPACE -> 0;
            case AIR -> 1;
            case DIRT -> 2;
            case LAVA -> 3;
            } | (cell.hasHpack ? hpackFlag : 0)
                    | (cell.occupied ? occupiedFlag : 0)
                    | (cell.goingToOccupy ? goingToOccupyFlag : 0);
        }
    }

    private int[] data;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;

        int n = this.width * this.height;
        data = new int[n];

        for (int i = 0; i < n; i++) {
            data[i] = 0;
        }
    }

    public Map(final Map src) {
        width = src.width;
        height = src.height;

        data = src.data.clone();
    }

    public Map(JSONObject json) throws JSONException {
        int mapSize = json.getInt("mapSize");
        width = height = mapSize;

        int n = width * height;
        data = new int[n];

        for (int i = 0; i < n; i++) {
            data[i] = 0;
        }

        parseJSON(json.getJSONArray("map"));
    }

    public Object clone() throws CloneNotSupportedException {
        Map m = (Map) super.clone();
        m.data = m.data.clone();
        return m;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell getCell(int x, int y) {
        if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
            throw new IndexOutOfBoundsException("Cell index out of bound");
        }
        return new Cell(data[x + y * width]);
    }

    public Cell getCell(final Coord c) {
        return getCell(c.getX(), c.getY());
    }

    public void setCell(int x, int y, final Cell cell) {
        if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
            throw new IndexOutOfBoundsException("Cell index out of bound");
        }
        data[x + y * width] = Cell.toInt(cell);
    }

    public void setCell(final Coord c, final Cell cell) {
        setCell(c.getX(), c.getY(), cell);
    }

    public BBox makeBBox() {
        return new BBox(width, height);
    }

    public void copyFrom(final Map src) {
        if ((width != src.width) && (height != src.height)) {
            throw new IllegalArgumentException("Source map size mismatch");
        }
        for (int i = 0; i < data.length; i++) {
            data[i] = src.data[i];
        }
    }

    public void parseJSON(JSONArray mapData) throws JSONException {
        for (int i_ = 0; i_ < mapData.length(); i_++) {
            JSONArray rowData = mapData.getJSONArray(i_);
            for (int j_ = 0; j_ < rowData.length(); j_++) {
                JSONObject cellData = rowData.getJSONObject(j_);

                int x = cellData.getInt("x");
                int y = cellData.getInt("y");

                Cell cell = new Cell(0);
                cell.type = switch (cellData.getString("type")) {
                case "DEEP_SPACE" -> CellType.DEEP_SPACE;
                case "AIR" -> CellType.AIR;
                case "DIRT" -> CellType.DIRT;
                case "LAVA" -> CellType.LAVA;
                default -> {
                    System.err.format("[ERROR] Unknown String %s\n",
                            cellData.getString("type"));
                    continue;
                }
                };

                JSONObject powerup;
                try {
                    powerup = cellData.getJSONObject("powerup");
                } catch (JSONException e) {
                    powerup = null;
                }
                if (powerup != null) {
                    switch (powerup.getString("type")) {
                    case "HEALTH_PACK":
                        cell.hasHpack = true;
                        break;
                    default:
                        System.err.format("[ERROR] Unknown String %s\n",
                                powerup.getString("type"));
                    }
                }

                try {
                    cellData.getJSONObject("occupier");
                    cell.occupied = true;
                } catch (JSONException e) {
                    cell.occupied = false;
                }

                try {
                    setCell(x, y, cell);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
    }
}
