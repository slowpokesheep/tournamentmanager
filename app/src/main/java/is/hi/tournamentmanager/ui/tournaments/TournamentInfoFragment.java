package is.hi.tournamentmanager.ui.tournaments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Base64;
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
                String idBase64 = infoData.tournament().id();
                String id = new String(Base64.getDecoder().decode(idBase64)).split(":")[1];
                boolean canEdit = infoData.tournament().canEdit();
                String name = infoData.tournament().name();
                String location = infoData.tournament().location();
                String timeGraph = infoData.tournament().time().toString();
                final String time = timeGraph.split(":")[0] + ":" +  timeGraph.split(":")[1];
                String dateGraph = infoData.tournament().date().toString();


                TextView codeLabel = root.findViewById(R.id.code_label);
                TextView hostLabel = root.findViewById(R.id.host_label);
                TextView statusLabel = root.findViewById(R.id.status_label);
                TextView locationLabel = root.findViewById(R.id.location_label);
                TextView categoryLabel = root.findViewById(R.id.category_label);
                TextView dateLabel = root.findViewById(R.id.date_label);
                TextView privateLabel = root.findViewById(R.id.private_label);
                TextView nameLabel = root.findViewById(R.id.name_label);
                TextView timeLabel = root.findViewById(R.id.tima_label);

                if (canEdit) {
                    LinearLayout nameLayout = root.findViewById(R.id.tournament_info_layout_name);
                    LinearLayout locationLayout = root.findViewById(R.id.tournament_info_layout_location);
                    LinearLayout dateLayout = root.findViewById(R.id.tournament_info_layout_date);
                    LinearLayout timeLayout = root.findViewById(R.id.tournament_info_layout_time);
                    // reset
                    try {
                        nameLayout.removeViews(1, 1);
                        locationLayout.removeViews(1, 1);
                        dateLayout.removeViews(1, 1);
                        timeLayout.removeViews(1, 1);
                    } catch (Exception e) {}

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            100,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    ImageView iv = new ImageView(getActivity());
                    iv.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    iv.setOnClickListener(v -> {
                        TournamentUpdateDialog d = TournamentUpdateDialog.newInstance(id, code, "name", name);
                        d.setViewModel(tournamentInfoViewModel);
                        d.show(getParentFragmentManager(), "Tournament Update - Name");
                    });
                    nameLayout.addView(iv, 1, params);

                    ImageView iv2 = new ImageView(getActivity());
                    iv2.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    iv2.setOnClickListener(v -> {
                        TournamentUpdateDialog d = TournamentUpdateDialog.newInstance(id, code, "location", location);
                        d.setViewModel(tournamentInfoViewModel);
                        d.show(getParentFragmentManager(), "Tournament Update - Location");
                    });
                    locationLayout.addView(iv2, 1, params);

                    ImageView iv3 = new ImageView(getActivity());
                    iv3.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    iv3.setOnClickListener(v -> {
                        TournamentUpdateDialog d = TournamentUpdateDialog.newInstance(id, code, "date", dateGraph);
                        d.setViewModel(tournamentInfoViewModel);
                        d.show(getParentFragmentManager(), "Tournament Update - Date");
                    });
                    dateLayout.addView(iv3, 1, params);

                    ImageView iv4 = new ImageView(getActivity());
                    iv4.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    iv4.setOnClickListener(v -> {
                        TournamentUpdateDialog d = TournamentUpdateDialog.newInstance(id, code, "time", time);
                        d.setViewModel(tournamentInfoViewModel);
                        d.show(getParentFragmentManager(), "Tournament Update - Time");
                    });
                    timeLayout.addView(iv4, 1, params);
                }


                codeLabel.setText(infoData.tournament().code());

                String statusDisplay = infoData.tournament().statusDisplay();
                switch (statusDisplay.toLowerCase()) {
                    case "open":
                        statusLabel.setTextColor(Color.GREEN);
                        break;
                    case "ongoing":
                        statusLabel.setTextColor(Color.BLUE);
                        break;
                    case "finished":
                        statusLabel.setTextColor(Color.RED);
                        break;
                    default:
                        break;
                }
                statusLabel.setText(statusDisplay);

                hostLabel.setText(infoData.tournament().creator().username());
                locationLabel.setText(location);
                categoryLabel.setText(infoData.tournament().category().name());

                String dateString = "";
                try {
                    Date d = new SimpleDateFormat("yyyy-MM-dd").parse(dateGraph);
                    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                    dateString = f.format(d);
                } catch (Exception e) {
                    Log.e("Date Exception", e.toString());
                }
                dateLabel.setText(dateString);

                boolean privateBool = infoData.tournament().private_();
                String privateString = "No";
                if (privateBool) privateString = "Yes";
                privateLabel.setText(privateString);

                nameLabel.setText(name);
                timeLabel.setText(time);
            }
        });

        tournamentInfoViewModel.fetchTournamentInfo(code);
    }

}