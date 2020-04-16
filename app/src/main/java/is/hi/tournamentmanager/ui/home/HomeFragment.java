package is.hi.tournamentmanager.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.apollographql.apollo.tournament.TournamentsQuery;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.ui.tournaments.TournamentBracket;
import is.hi.tournamentmanager.ui.tournaments.TournamentsViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        Button findTournamentButton = root.findViewById(R.id.find_tournament_button);

        // Search button listener
        findTournamentButton.setOnClickListener(v -> {
            EditText text = root.findViewById(R.id.find_tournament_text);
            String code = text.getText().toString();
            homeViewModel.fetchTournaments(code);
        });

        observeViewModel();

        return root;
    }

    private void observeViewModel() {

        // On change, when the search button is pressed, navigate to the tournament info
        homeViewModel.getInfoObservable().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                final NavController navController = Navigation.findNavController(this.getView());

                String code = data.tournament().code();

                Bundle args = new Bundle();
                args.putString("node", code);
                navController.navigate(R.id.nav_tournament, args);
            }
        });
    }

}
