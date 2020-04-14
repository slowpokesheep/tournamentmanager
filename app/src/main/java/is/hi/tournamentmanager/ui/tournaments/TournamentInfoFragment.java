package is.hi.tournamentmanager.ui.tournaments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.ui.dashboard.DashboardViewModel;
import is.hi.tournamentmanager.ui.tournaments.filters.CategoryFilterDialogFragment;


public class TournamentInfoFragment extends Fragment {

    private TournamentInfoViewModel tournamentInfoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d("TournamentInfoFragment","onCreateView");
        tournamentInfoViewModel = new ViewModelProvider(this).get(TournamentInfoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tournament_info, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstaceState){
        super.onViewCreated(view, savedInstaceState);
        Log.d("info","fragment");
        tournamentInfoViewModel = new ViewModelProvider(requireActivity()).get(TournamentInfoViewModel.class);
        final View root = view;

        TextView codeLabel = root.findViewById(R.id.code_label);
        TextView hostLabel = root.findViewById(R.id.host_label);
        TextView statusLabel = root.findViewById(R.id.status_label);
        TextView locationLabel = root.findViewById(R.id.location_label);
        TextView categoryLabel = root.findViewById(R.id.category_label);
        TextView dateLabel = root.findViewById(R.id.date_label);
        TextView privateLabel = root.findViewById(R.id.private_label);
        TextView nameLabel = root.findViewById(R.id.name_label);
        TextView timeLabel = root.findViewById(R.id.tima_label);
        codeLabel.setText("EBAXT");
        hostLabel.setText("Flóki");
        statusLabel.setText("Ongoing");
        locationLabel.setText("Reykjavík");
        categoryLabel.setText("LoL");
        dateLabel.setText("14.04.2020");
        privateLabel.setText("no");
        nameLabel.setText("Keppni fyrir lúða");
        timeLabel.setText("19:30:00");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    // type: 0 for all public tournaments, 1 for "my" tournaments, 2 for tournaments "i am" registered in
    public static TournamentInfoFragment newInstance() {
        TournamentInfoFragment newFragment = new TournamentInfoFragment();
        Bundle args = new Bundle();
        newFragment.setArguments(args);

        return newFragment;
    }

}