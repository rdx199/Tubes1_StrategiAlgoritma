
import org.json.JSONArray;
import org.json.JSONObject;

public class Map {
    private int width, height;

    public enum CellType {
        DEEP_SPACE, AIR, DIRT, LAVA, AIR_HPACK, DIRT_HPACK, LAVA_HPACK,
    };

    private CellType[] data;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;

        int n = this.width * this.height;
        data = new CellType[n];

        for (int i = 0; i < n; i++) {
            data[i] = CellType.DEEP_SPACE;
        }
    }

    public Map(final Map src) {
        width = src.width;
        height = src.height;

        int n = width * height;
        data = new CellType[n];

        for (int i = 0; i < n; i++) {
            data[i] = src.data[i];
        }
    }

    public Map(JSONObject json) {
        int mapSize = json.getInt("mapSize");
        width = height = mapSize;

        int n = width * height;
        data = new CellType[n];

        for (int i = 0; i < n; i++) {
            data[i] = CellType.DEEP_SPACE;
        }

        parseMap(json.getJSONArray("map"));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public CellType getCell(int x, int y) {
        if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
            throw new IndexOutOfBoundsException("Cell index out of bound");
        }
        return data[x + y * width];
    }

    public CellType getCell(final Coord c) {
        return getCell(c.getX(), c.getY());
    }

    public void setCell(int x, int y, CellType cell) {
        if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
            throw new IndexOutOfBoundsException("Cell index out of bound");
        }
        data[x + y * width] = cell;
    }

    public void setCell(final Coord c, CellType cell) {
        setCell(c.getX(), c.getY(), cell);
    }

    public BBox makeBBox() {
        return new BBox(width, height);
    }

    public void parseMap(JSONArray mapData) {
        for (int i_ = 0; i_ < mapData.length(); i_++) {
            JSONArray rowData = mapData.getJSONArray(i_);
            for (int j_ = 0; j_ < rowData.length(); j_++) {
                JSONObject cellData = rowData.getJSONObject(j_);

                int x = cellData.getInt("x");
                int y = cellData.getInt("y");

                CellType type = null;
                switch (cellData.getString("type")) {
                case "DEEP_SPACE":
                    type = CellType.DEEP_SPACE;
                    break;
                case "AIR":
                    type = CellType.AIR;
                    break;
                case "DIRT":
                    type = CellType.DIRT;
                    break;
                case "LAVA":
                    type = CellType.LAVA;
                    break;
                default:
                    System.err.format("[ERROR] Unknown String %s",
                            cellData.getString("type"));
                }

                if (type == null) {
                    continue;
                }

                JSONObject powerup = cellData.getJSONObject("powerup");
                if (powerup != null) {
                    switch (powerup.getString("type")) {
                    case "HEALTH_PACK":
                        switch (type) {
                        case AIR:
                            type = CellType.AIR_HPACK;
                        case DIRT:
                            type = CellType.DIRT_HPACK;
                        case LAVA:
                            type = CellType.LAVA_HPACK;
                        default:
                            System.err.format(
                                    "[WARNING] HEALTH_PACK not supported in %s",
                                    type.toString());
                        }
                    default:
                        System.err.format("[ERROR] Unknown String %s",
                                powerup.getString("type"));
                    }
                }

                try {
                    setCell(x, y, type);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
    }
}
