package is.hi.tournamentmanager.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import is.hi.tournamentmanager.R;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        observeViewModel();

        return root;
    }

    private void observeViewModel() {
        profileViewModel.getMeDataObservable().observe(getViewLifecycleOwner(), meData -> {
            if (meData != null) {
                Log.d("Me Data", meData.toString());
                final TextView textView = root.findViewById(R.id.text_profile);
                String username = meData.me().username();
                String email = meData.me().email();
                textView.setText(username + " - " + email);
            }
        });
    }
}
