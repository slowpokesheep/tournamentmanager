package is.hi.tournamentmanager.utils;

import android.app.Application;
import android.content.SharedPreferences;

// singleton utility class for storing and getting shared preferences
public class SharedPref {
    public static SharedPref sharedPref;

    //private static final String prefName = "MyPref";
    private final String prefName;
    private final SharedPreferences pref;

    public SharedPref(String name, SharedPreferences pref) {
        this.prefName = name;
        this.pref = pref;
        sharedPref = this;
    }

    public static void init(Application app) {
        /*
        if (sharedPref != null) {
            throw new AssertionError("You already initialized me");
        }
        */
        //sharedPref = new SharedPref(app.getSharedPreferences(prefName, 0));
    }

    public static SharedPref getInstance() {
        if (sharedPref == null) {
            throw new AssertionError("You have to call init first");
        }
        return sharedPref;
    }

    public SharedPreferences getPref() {
        return pref;
    }

    public String getUsername() {
        return pref.getString("username", null);
    }

    public String getToken() {
        return pref.getString("token", null);
    }

    public int getUserId() {
        return pref.getInt("user_id", -1);
    }

    public void setUserInfo(String name, String token, int userId) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", name);
        editor.putString("token", token);
        editor.putInt("user_id", userId);
        editor.commit();
    }

    public void clearUserInfo() {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("username");
        editor.remove("token");
        editor.remove("user_id");
        editor.commit();
    }

}
