package is.hi.tournamentmanager.ui.tournaments;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import is.hi.tournamentmanager.R;


public class TournamentInfoFragment extends Fragment {

    private View root;
    private TournamentInfoViewModel tournamentInfoViewModel;

    public static TournamentInfoFragment newInstance(String code) {
        TournamentInfoFragment newFragment = new TournamentInfoFragment();
        Bundle args = new Bundle();
        args.putString("code", code);
        newFragment.setArguments(args);

        return newFragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Bundle args = getArguments();
        String code = args.getString("code");

        tournamentInfoViewModel = new ViewModelProvider(this).get(TournamentInfoViewModel.class);
        root = inflater.inflate(R.layout.fragment_tournament_info, container, false);

        observeViewModel(code);

        return root;
    }

    private void observeViewModel(String code) {
        tournamentInfoViewModel.getInfoObservable().observe(getViewLifecycleOwner(), infoData -> {
            if (infoData != null) {
                TextView codeLabel = root.findViewById(R.id.code_label);
                TextView hostLabel = root.findViewById(R.id.host_label);
                TextView statusLabel = root.findViewById(R.id.status_label);
                TextView locationLabel = root.findViewById(R.id.location_label);
                TextView categoryLabel = root.findViewById(R.id.category_label);
                TextView dateLabel = root.findViewById(R.id.date_label);
                TextView privateLabel = root.findViewById(R.id.private_label);
                TextView nameLabel = root.findViewById(R.id.name_label);
                TextView timeLabel = root.findViewById(R.id.tima_label);
                Button register = root.findViewById(R.id.register_button);
                codeLabel.setText(infoData.tournament().code());
                hostLabel.setText(infoData.tournament().creator().username());
                statusLabel.setText(infoData.tournament().statusDisplay());
                locationLabel.setText(infoData.tournament().location());
                categoryLabel.setText(infoData.tournament().category().name());

                String dateString = "";
                try {
                    Date d = new SimpleDateFormat("yyyy-MM-dd").parse(infoData.tournament().date().toString());
                    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                    dateString = f.format(d);
                } catch (Exception e) {
                    Log.e("Date Exception", e.toString());
                }
                dateLabel.setText(dateString);

                boolean priv = infoData.tournament().private_();
                String privateString = "No";
                if (priv) privateString = "Yes";
                privateLabel.setText(privateString);

                nameLabel.setText(infoData.tournament().name());
                timeLabel.setText(infoData.tournament().time().toString());
            }
        });

        tournamentInfoViewModel.fetchTournamentInfo(code);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstaceState){
        super.onViewCreated(view, savedInstaceState);
        Log.d("info","fragment");
        Bundle args = getArguments();
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
        Button register = root.findViewById(R.id.register_button);
        codeLabel.setText(args.getString("code"));
        hostLabel.setText(args.getString("host"));
        statusLabel.setText(args.getString("status"));
        locationLabel.setText(args.getString("location"));
        categoryLabel.setText(args.getString("category"));
        dateLabel.setText(args.getString("date"));
        privateLabel.setText(args.getString("private"));
        nameLabel.setText(args.getString("name"));
        timeLabel.setText(args.getString("time"));
    }

}