package is.hi.tournamentmanager.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.ui.tournaments.TournamentDetails;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Button findTournamentButton = root.findViewById(R.id.find_tournament_button);
        findTournamentButton.setOnClickListener(v -> {
            EditText text = root.findViewById(R.id.find_tournament_text);
            DialogFragment newFragment = TournamentDetails.newInstance(text.getText().toString().toUpperCase());
            newFragment.show(
                    getParentFragmentManager(),
                    "Tournament Dialog"
            );
        });

        return root;
    }

}
