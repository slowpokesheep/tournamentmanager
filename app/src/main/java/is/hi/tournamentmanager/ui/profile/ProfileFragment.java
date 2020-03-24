package is.hi.tournamentmanager.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.ui.authentication.LoginViewModel;
import is.hi.tournamentmanager.utils.SharedPref;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private LoginViewModel loginViewModel;
    private View root;

    public ProfileFragment(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        Button signOut = root.findViewById(R.id.sign_out_button);

        signOut.setOnClickListener(v -> {
            Log.d("sign out button", "click");
            loginViewModel.refuseAuthentication();
            SharedPref.getInstance().clearToken();
            navController.popBackStack(R.id.nav_home, false);
        });

        observeViewModel();
    }

    private void observeViewModel() {
        profileViewModel.getMeDataObservable().observe(getViewLifecycleOwner(), meData -> {
            if (meData != null) {
                Log.d("Me Data", meData.toString());
                final TextView textView = root.findViewById(R.id.text_profile);
                String username = meData.me().username();
                textView.setText(username);
            }
        });
    }
}
