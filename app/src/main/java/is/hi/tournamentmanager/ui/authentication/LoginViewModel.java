package is.hi.tournamentmanager.ui.authentication;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.tournament.LoginMutation;

import org.jetbrains.annotations.NotNull;

import is.hi.tournamentmanager.utils.ApolloConnector;
import is.hi.tournamentmanager.utils.SharedPref;

public class LoginViewModel extends AndroidViewModel {

    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    private enum LoginResult {
        WAITING, // Waiting for API to respond
        SUCCESS, // Successful login
        ERROR // Unsuccessful login
    }

    public final MutableLiveData<AuthenticationState> authenticationState =
            new MutableLiveData<>();
    private LoginResult loginResult = LoginResult.WAITING;
    String username;

    public LoginViewModel(Application application) {
        super(application);
        // In this example, the user is always unauthenticated when MainActivity is launched
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        username = "";
    }

    public void authenticate(String username, String password) {
        if (passwordIsValidForUsername(username, password)) {
            this.username = username;
            authenticationState.setValue(AuthenticationState.AUTHENTICATED);
        } else {
            authenticationState.setValue(AuthenticationState.INVALID_AUTHENTICATION);
        }
    }

    public void refuseAuthentication() {
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }

    private boolean passwordIsValidForUsername(String username, String password) {
        login(username, password);
        while (loginResult == LoginResult.WAITING) {
            try {
                Thread.sleep(100);
            } catch (Exception e) { }
        }
        if (loginResult == LoginResult.SUCCESS) {
            loginResult = LoginResult.WAITING;
            return true;
        }
        loginResult = LoginResult.WAITING;
        return false;
    }

    public void login(String username, String password) {
        LoginMutation mutation = LoginMutation
                .builder()
                .username(username)
                .password(password)
                .build();
        ApolloConnector.getInstance().getApolloClient().mutate(mutation)
            .enqueue(new ApolloCall.Callback<LoginMutation.Data>() {

                @Override
                public void onResponse(@NotNull Response<LoginMutation.Data> response) {
                    Log.d("Login", "Response: " + response.data());
                    LoginMutation.TokenCreate token = response.data().tokenCreate();
                    if (token != null) {
                        // storing token in shared preferences
                        SharedPref.getInstance().setToken(token.token());
                        loginResult = LoginResult.SUCCESS;
                    } else {
                        loginResult = LoginResult.ERROR;
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    Log.d("Login", "Exception " + e.getMessage(), e);
                    loginResult = LoginResult.ERROR;
                }

            });
    }
}
