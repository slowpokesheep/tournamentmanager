package is.hi.tournamentmanager.ui.tournaments;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.PrintWriter;

import is.hi.tournamentmanager.R;

public class TournamentDetails extends DialogFragment {
    private View root;
    private TournamentDetailsViewModel viewModel;
    private  RelativeLayout tournamentBracketLayout;
    private GridLayout grid;

    public static TournamentDetails newInstance(String code) {
        TournamentDetails newFragment = new TournamentDetails();
        Bundle args = new Bundle();
        args.putString("code", code);
        newFragment.setArguments(args);

        return newFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        String code = args.getString("code");

        viewModel = new ViewModelProvider(this).get(TournamentDetailsViewModel.class);
        root = inflater.inflate(R.layout.fragment_tournament, container, false);
        tournamentBracketLayout = root.findViewById(R.id.tournament_details_bracket);

        observeViewModel(code);

        return root;
    }

    private void observeViewModel(String code) {
        viewModel.getTournamentDetailsDataObservable().observe(getViewLifecycleOwner(), tournamentDetailsData -> {
            if (tournamentDetailsData != null) {
                TextView nameViewLabel = new TextView(getActivity());
                TextView nameView = new TextView(getActivity());
                nameViewLabel.setText("Name: ");
                nameView.setText(tournamentDetailsData.tournament().name());
                TextView codeViewLabel = new TextView(getActivity());
                TextView codeView = new TextView(getActivity());
                codeViewLabel.setText("code: ");
                codeView.setText(code);

                GridLayout infoGrid = root.findViewById(R.id.tournament_details_info_grid);
                infoGrid.addView(nameViewLabel);
                infoGrid.addView(nameView);
                infoGrid.addView(codeViewLabel);
                infoGrid.addView(codeView);

                // match bracket
                int nRounds = tournamentDetailsData.tournament().nRounds();
                int firstRoundMatches = (int) Math.pow(2, nRounds);
                grid = new GridLayout(getActivity());
                grid.setRowCount(firstRoundMatches * 2); // space between matches
                grid.setColumnCount(nRounds + 1);
                try {
                    JSONObject root = new JSONObject(tournamentDetailsData.tournament().matchBracket());
                    tournamentBracket(root, firstRoundMatches, nRounds, nRounds - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tournamentBracketLayout.addView(grid);
            }
        });

        viewModel.fetchTournamentDetails(code);
    }

    private void tournamentBracket(JSONObject root, int row, int col, int counter) throws JSONException {
        int id = root.getInt("id");
        JSONObject sides = root.getJSONObject("sides");
        JSONObject home = sides.getJSONObject("home");
        JSONObject visitor = sides.getJSONObject("visitor");
        JSONObject homeTeam = home.getJSONObject("team");
        JSONObject visitorTeam = visitor.getJSONObject("team");
        String homeUser = "";
        String visitorUser = "";
        if (homeTeam.length() > 0) {
            homeUser = homeTeam.getString("name");
        }
        if (visitorTeam.length() > 0) {
            visitorUser = visitorTeam.getString("name");
        }
        int homeScore = home.getJSONObject("score").getInt("score");
        int visitorScore = home.getJSONObject("score").getInt("score");

        matchLayout(homeUser, visitorUser, homeScore, visitorScore, row, col);

        if (home.has("seed")) {
            JSONObject nextHomeRoot = home.getJSONObject("seed").getJSONObject("sourceGame");
            JSONObject nextVisitorRoot = visitor.getJSONObject("seed").getJSONObject("sourceGame");
            tournamentBracket(nextHomeRoot, row - (int) Math.pow(2, counter), col - 1, counter - 1);
            tournamentBracket(nextVisitorRoot, row + (int) Math.pow(2, counter), col - 1, counter - 1);
        }
    }

    private void matchLayout(String homeUser, String visitorUser, int homeScore, int visitorScore, int row, int col) {
        TableLayout matchLayout = new TableLayout(getActivity());

        // sides
        TableRow homeRow = new TableRow(getActivity());
        homeRow.setBackgroundResource(R.drawable.border);
        TableRow visitorRow = new TableRow(getActivity());
        TableRow.LayoutParams userParams = new TableRow.LayoutParams(
                300,
                TableRow.LayoutParams.WRAP_CONTENT);
        userParams.setMargins(10, 10, 10, 10);
        TableRow.LayoutParams scoreParams = new TableRow.LayoutParams(
                60,
                TableRow.LayoutParams.WRAP_CONTENT);
        scoreParams.setMargins(10, 10 , 10 ,10);
        // home
        TextView homeUserView = new TextView(getActivity());
        homeUserView.setText(homeUser);
        TextView homeScoreView = new TextView(getActivity());
        if (homeUser.length() > 0) homeScoreView.setText(String.valueOf(homeScore));
        // border
        homeRow.addView(homeUserView, userParams);
        homeRow.addView(homeScoreView, scoreParams);
        // visitor
        TextView visitorUserView = new TextView(getActivity());
        visitorUserView.setText(visitorUser);
        TextView visitorScoreView = new TextView(getActivity());
        if (homeUser.length() > 0) visitorScoreView.setText(String.valueOf(visitorScore));
        // border
        visitorRow.addView(visitorUserView, userParams);
        visitorRow.addView(visitorScoreView, scoreParams);

        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        rowParams.gravity = Gravity.CENTER_VERTICAL;
        matchLayout.addView(homeRow, rowParams);
        matchLayout.addView(visitorRow, rowParams);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col));
        matchLayout.setBackgroundResource(R.drawable.border);
        grid.addView(matchLayout, params);
    }
}
