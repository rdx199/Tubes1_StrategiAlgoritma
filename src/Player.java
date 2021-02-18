import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Player implements Cloneable {

    private final int id;
    private Worm[] worms;

    public Player(int id) {
        this.id = id;
    }

    public Player(final Player src) {
        try {
            id = src.id;
            worms = new Worm[src.worms.length];
            for (int i = 0; i < worms.length; i++) {
                worms[i] = (Worm) src.worms[i].clone();
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        Player p = (Player) super.clone();
        p.worms = new Worm[worms.length];
        for (int i = 0; i < worms.length; i++) {
            p.worms[i] = (Worm) worms[i].clone();
        }
        return p;
    }

    public int getID() {
        return id;
    }

    public Worm[] getWorms() {
        return worms;
    }

    public Worm getWormByID(int id) {
        for (Worm w : worms) {
            if (w.getID() == id) {
                return w;
            }
        }
        return null;
    }

    public void copyFrom(final Player src) {
        if (id != src.id) {
            throw new IllegalArgumentException("Player ID mismatch");
        }
        for (Worm worm : worms) {
            worm.copyFrom(src.getWormByID(worm.getID()));
        }
    }

    public void parseJSON(JSONObject json)
            throws JSONException, IDMismatchException {
        if (id != json.getInt("id")) {
            throw new IDMismatchException(
                    String.format("Worm id mismatch! (expected %d, got %d)", id,
                            json.getInt("id")));
        }

        JSONArray worms = json.getJSONArray("worms");
        this.worms = new Worm[worms.length()];
        for (int i = 0; i < worms.length(); i++) {
            JSONObject worm = worms.getJSONObject(i);
            this.worms[i] = new Worm(worm.getInt("id"), id);
            this.worms[i].parseJSON(worm);
        }
    }

    public void parseJSONExt(JSONObject json)
            throws JSONException, IDMismatchException {
        if (id != json.getInt("id")) {
            throw new IDMismatchException(
                    String.format("Worm id mismatch! (expected %d, got %d)", id,
                            json.getInt("id")));
        }

        JSONArray worms = json.getJSONArray("worms");
        this.worms = new Worm[worms.length()];
        for (int i = 0; i < worms.length(); i++) {
            JSONObject worm = worms.getJSONObject(i);
            this.worms[i] = new WormExt(worm.getInt("id"), id);
            this.worms[i].parseJSON(worm);
        }
    }
}
