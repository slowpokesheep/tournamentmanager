
package is.hi.tournamentmanager.ui.tournaments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.apollographql.apollo.tournament.TournamentUsersQuery;

import is.hi.tournamentmanager.R;

public class TournamentRegisterFragment extends Fragment {
    private TournamentRegisterViewModel tournamentRegisterViewModel;
    private View root;

    public static TournamentRegisterFragment newInstance(String code) {
        TournamentRegisterFragment newFragment = new TournamentRegisterFragment();
        Bundle args = new Bundle();
        args.putString("code", code);
        newFragment.setArguments(args);

        return newFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        String code = args.getString("code");

        tournamentRegisterViewModel = new ViewModelProvider(this).get(TournamentRegisterViewModel.class);
        root = inflater.inflate(R.layout.fragment_tournament_register, container, false);

        observeViewModel(code);

        return root;
    }

    private void observeViewModel(String code) {
        tournamentRegisterViewModel.getUsersObservable().observe(getViewLifecycleOwner(), usersData -> {
            if (usersData != null) {
                Log.d("Tournament participants", usersData.toString());
                TournamentUsersQuery.RegisteredUsers registeredUsers = usersData.tournament().registeredUsers();
                TournamentUsersQuery.Admins admins = usersData.tournament().admins();
                TournamentUsersQuery.Creator host = usersData.tournament().creator();
            }
        });

        tournamentRegisterViewModel.fetchTournamentUsers(code);
    }
}
