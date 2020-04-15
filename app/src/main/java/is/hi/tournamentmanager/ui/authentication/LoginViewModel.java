package is.hi.tournamentmanager.ui.authentication;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import is.hi.tournamentmanager.service.ApiRepository;
import is.hi.tournamentmanager.utils.SharedPref;

public class LoginViewModel extends AndroidViewModel {

    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    MutableLiveData<AuthenticationState> authenticationState =
            new MutableLiveData<>();

    public LoginViewModel(Application application) {
        super(application);
    }

    public void authenticate(String username, String password) {
        ApiRepository.getInstance().login(authenticationState, username, password);
    }
}
