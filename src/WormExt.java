import org.json.JSONException;
import org.json.JSONObject;

public class WormExt extends Worm {

    private int weaponDamage, weaponRange;
    private int bananaDamage, bananaRange, bananaRadius, bananaCount;
    private int snowballDuration, snowballRange, snowballRadius, snowballCount;

    public WormExt(int id, int playerId) {
        super(id, playerId);
        weaponDamage = weaponRange = 0;
        bananaDamage = bananaRange = bananaRadius = bananaCount = 0;
        snowballDuration = snowballRange = snowballRadius = snowballCount = 0;
    }

    public WormExt(final WormExt src) {
        super(src);
        weaponDamage = src.weaponDamage;
        weaponRange = src.weaponRange;
        bananaDamage = src.bananaDamage;
        bananaRange = src.bananaRange;
        bananaRadius = src.bananaRadius;
        bananaCount = src.bananaCount;
        snowballDuration = src.snowballDuration;
        snowballRange = src.snowballRange;
        snowballRadius = src.snowballRadius;
        snowballCount = src.snowballCount;
    }

    public int getWeaponDamage() {
        return weaponDamage;
    }

    public int getWeaponRange() {
        return weaponRange;
    }

    public int getBananaDamage() {
        return bananaDamage;
    }

    public int getBananaRange() {
        return bananaRange;
    }

    public int getBananaRadius() {
        return bananaRadius;
    }

    public int getBananaCount() {
        return bananaCount;
    }

    public void setBananaCount(int bananaCount) {
        this.bananaCount = bananaCount;
    }

    public int getSnowballDuration() {
        return snowballDuration;
    }

    public int getSnowballRange() {
        return snowballRange;
    }

    public int getSnowballRadius() {
        return snowballRadius;
    }

    public int getSnowballCount() {
        return snowballCount;
    }

    public void setSnowballCount(int snowballCount) {
        this.snowballCount = snowballCount;
    }

    @Override
    public void parseJSON(JSONObject json)
            throws JSONException, IDMismatchException {
        super.parseJSON(json);

        JSONObject weapon;
        try {
            weapon = json.getJSONObject("weapon");
        } catch (JSONException e) {
            weapon = null;
        }
        if (weapon != null) {
            weaponDamage = weapon.getInt("damage");
            weaponRange = weapon.getInt("range");
        }

        JSONObject banana;
        try {
            banana = json.getJSONObject("bananaBombs");
        } catch (JSONException e) {
            banana = null;
        }
        if (banana != null) {
            bananaDamage = banana.getInt("damage");
            bananaRange = banana.getInt("range");
            bananaRadius = banana.getInt("damageRadius");
            bananaCount = banana.getInt("count");
        }

        JSONObject snowball;
        try {
            snowball = json.getJSONObject("snowballs");
        } catch (JSONException e) {
            snowball = null;
        }
        if (snowball != null) {
            snowballDuration = snowball.getInt("freezeDuration");
            snowballRange = snowball.getInt("range");
            snowballRadius = snowball.getInt("freezeRadius");
            snowballCount = snowball.getInt("count");
        }
    }
}
