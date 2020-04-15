package is.hi.tournamentmanager.utils;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import is.hi.tournamentmanager.ui.authentication.LoginViewModel;

// singleton utility class for storing and getting shared preferences
public class SharedPref {
    public static SharedPref sharedPref;
    private final SharedPreferences pref;

    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    MutableLiveData<AuthenticationState> authenticationState =
            new MutableLiveData<>();

    public SharedPref(SharedPreferences pref) {
        this.pref = pref;
        sharedPref = this;
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

    public MutableLiveData<AuthenticationState> getAuthenticationState() {
        return authenticationState;
    }

    // Used to initalize loginStatus
    public void setLoginStatus(boolean isLoggedIn) {
        if (isLoggedIn) {
            authenticationState.setValue(AuthenticationState.AUTHENTICATED);
        } else {
            authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        }
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

        authenticationState.setValue(AuthenticationState.AUTHENTICATED);
    }

    public void clearUserInfo() {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("username");
        editor.remove("token");
        editor.remove("user_id");
        editor.commit();

        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }

}
