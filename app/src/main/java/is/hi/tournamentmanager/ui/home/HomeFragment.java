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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.service.ApiRepository;
import is.hi.tournamentmanager.ui.collections.CollectionTournamentFragment;
import is.hi.tournamentmanager.ui.tournaments.TournamentBracket;
import is.hi.tournamentmanager.utils.ApolloConnector;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Button findTournamentButton = root.findViewById(R.id.find_tournament_button);
        findTournamentButton.setOnClickListener(v -> {
            EditText text = root.findViewById(R.id.find_tournament_text);
            final NavController navController = Navigation.findNavController(root);
            // navigation happens on the callback if the code is valid
            ApiRepository.getInstance().tournamentSearch(text.getText().toString().toUpperCase(), navController);
        });

        return root;
    }

}
