package is.hi.tournamentmanager.ui.authentication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import is.hi.tournamentmanager.R;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("LoginFragment", "onCreateView");
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("login", "fragment");
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        final NavController navController = Navigation.findNavController(view);
        final View root = view;

        Button loginButton = root.findViewById(R.id.login_form_submit);
        EditText usernameInput = root.findViewById(R.id.login_form_username);
        EditText passwordInput = root.findViewById(R.id.login_form_password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("login button", "click");
                viewModel.authenticate(usernameInput.getText().toString(),
                        passwordInput.getText().toString());
            }
        });

        // If back button is pressed we refuse authentication and go back to home fragment
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("LoginFragment", "back");
                viewModel.refuseAuthentication();
                navController.popBackStack(R.id.nav_home, false);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        viewModel.authenticationState.observe(getViewLifecycleOwner(),
            authenticationState -> {
                switch (authenticationState) {
                    case AUTHENTICATED:
                        Log.d("LoginFragment", "AUTHENTICATED");
                        navController.popBackStack();
                        break;
                    case INVALID_AUTHENTICATION:
                        Log.d("LoginFragment", "INVALID_AUTHENTICATION");
                        Snackbar.make(root,
                            "asd",
                            Snackbar.LENGTH_SHORT
                        ).show();
                        break;
                }
            });
    }
}
