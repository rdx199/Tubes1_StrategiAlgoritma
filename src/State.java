import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class State implements Cloneable {

    private Map map;
    private Player[] players;
    private int myPlayerID;
    private int pushDamage;
    private int lavaDamage;

    public State() {
        map = null;
        players = null;
        myPlayerID = pushDamage = lavaDamage = 0;
    }

    public State(JSONObject json) throws JSONException, IDMismatchException {
        parseJSON(json);
    }

    public Object clone() throws CloneNotSupportedException {
        State s = (State) super.clone();
        s.map = (Map) s.map.clone();
        s.players = new Player[players.length];
        for (int i = 0; i < players.length; i++) {
            s.players[i] = (Player) players[i].clone();
        }
        return s;
    }

    public Map getMap() {
        return map;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getPlayerByID(int id) {
        for (Player p : players) {
            if (p.getID() == id) {
                return p;
            }
        }
        return null;
    }

    public int getMyPlayerID() {
        return myPlayerID;
    }

    public int getPushDamage() {
        return pushDamage;
    }

    public int getLavaDamage() {
        return lavaDamage;
    }

    public Worm occupier(final Coord pos) {
        for (Player p : players) {
            for (Worm w : p.getWorms()) {
                if (w.getPos() == pos) {
                    return w;
                }
            }
        }
        return null;
    }

    public void copyFrom(final State src) {
        myPlayerID = src.myPlayerID;
        pushDamage = src.pushDamage;
        lavaDamage = src.lavaDamage;
        map.copyFrom(src.map);
        for (Player p : players) {
            p.copyFrom(src.getPlayerByID(p.getID()));
        }
    }

    public void parseJSON(JSONObject json)
            throws JSONException, IDMismatchException {
        pushDamage = json.getInt("pushbackDamage");
        lavaDamage = json.getInt("lavaDamage");

        map = new Map(json);

        JSONObject myPlayer = json.getJSONObject("myPlayer");
        JSONArray opponents = json.getJSONArray("opponents");

        players = new Player[opponents.length() + 1];

        {
            myPlayerID = myPlayer.getInt("id");
            Player player = new Player(myPlayerID);
            player.parseJSONExt(myPlayer);
            players[players.length - 1] = player;
        }

        for (int i = 0; i < opponents.length(); i++) {
            JSONObject playerJSON = opponents.getJSONObject(i);
            Player player = new Player(playerJSON.getInt("id"));
            player.parseJSON(playerJSON);
            Worm[] worms = player.getWorms();
            for (int j = 0; j < worms.length; j++) {
                WormExt worm = WormExt.tryUpgrade(worms[j]);
                if (worm != null)
                    worms[j] = worm;
            }
            players[i] = player;
        }
    }
}
