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

import com.apollographql.apollo.tournament.MeQuery;

import java.util.Date;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.time.OffsetDateTime;

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

        observeViewModel();
    }

    private void observeViewModel() {
        profileViewModel.getMeDataObservable().observe(getViewLifecycleOwner(), meData -> {
            if (meData != null) {
                final TextView usernameTextView = root.findViewById(R.id.text_profile_username);
                final TextView emailTextView = root.findViewById(R.id.text_profile_email);
                final TextView nameTextView = root.findViewById(R.id.text_profile_name);
                final TextView dateJoinedTextView = root.findViewById(R.id.text_profile_date_joined);

                MeQuery.Me me = meData.me();
                usernameTextView.setText(me.username());
                emailTextView.setText(me.email());
                nameTextView.setText(me.name());
                String dateJoined = "";
                try {
                    // Fix to support api level 25
                    String joined = me.dateJoined().toString();
                    System.out.println(joined);
                    String parts[] = joined.split("T");
                    String p[] = parts[0].split("-");
                    dateJoined = p[0] + "/" + p[1] + "/" + p[2];
                } catch (Exception e) {
                    Log.e("Date Joined Exception", e.toString());
                }
                dateJoinedTextView.setText(dateJoined);
            }
        });
    }
}
