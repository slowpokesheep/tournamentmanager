package is.hi.tournamentmanager.utils;

import android.app.Application;
import android.content.SharedPreferences;

// singleton utility class for storing and getting shared preferences
public class SharedPref {
    public static SharedPref sharedPref;

    private static final String prefName = "MyPref";
    private final SharedPreferences pref;

    private SharedPref(SharedPreferences pref) {
        this.pref = pref;
    }

    public static void init(Application app) {
        if (sharedPref != null) {
            throw new AssertionError("You already initialized me");
        }
        sharedPref = new SharedPref(app.getSharedPreferences(prefName, 0));
    }

    public static SharedPref getInstance() {
        if (sharedPref == null) {
            throw new AssertionError("You have to call init first");
        }
        return sharedPref;
    }

    public String getToken() {
        return pref.getString("token", null);
    }

    public int getUserId() {
        return pref.getInt("user_id", -1);
    }

    public void setToken(String token, int userId) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.putInt("user_id", userId);
        editor.commit();
    }

    public void clearToken() {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("token");
        editor.remove("user_id");
        editor.commit();
    }

}
