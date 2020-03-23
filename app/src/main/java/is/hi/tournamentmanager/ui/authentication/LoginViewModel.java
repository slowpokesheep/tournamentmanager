package is.hi.tournamentmanager.ui.authentication;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import is.hi.tournamentmanager.service.ApiRepository;

public class LoginViewModel extends AndroidViewModel {

    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    MutableLiveData<AuthenticationState> authenticationState =
            new MutableLiveData<>();
    String username;

    public LoginViewModel(Application application) {
        super(application);
        // In this example, the user is always unauthenticated when MainActivity is launched
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        username = "";
    }

    public MutableLiveData<AuthenticationState> getAuthenticationState() {
        return authenticationState;
    }

    public void authenticate(String username, String password) {
        ApiRepository.getInstance().login(authenticationState, username, password);
    }

    public void acceptAuthentication() {
        authenticationState.setValue(AuthenticationState.AUTHENTICATED);
    }

    public void refuseAuthentication() {
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }
}
