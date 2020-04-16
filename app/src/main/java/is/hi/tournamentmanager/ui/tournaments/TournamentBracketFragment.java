package is.hi.tournamentmanager.ui.tournaments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.service.ApiRepository;

public class TournamentBracketFragment extends Fragment {
    private String code;
    private View root;
    private TournamentBracketViewModel viewModel;
    private RelativeLayout tournamentBracketLayout;
    private GridLayout grid;
    private boolean isOngoing;
    private boolean canEdit;

    public static TournamentBracketFragment newInstance(String code) {
        TournamentBracketFragment newFragment = new TournamentBracketFragment();
        Bundle args = new Bundle();
        args.putString("code", code);
        newFragment.setArguments(args);

        return newFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        code = args.getString("code");

        viewModel = new ViewModelProvider(this).get(TournamentBracketViewModel.class);
        root = inflater.inflate(R.layout.fragment_tournament_bracket, container, false);
        tournamentBracketLayout = root.findViewById(R.id.tournament_bracket);

        observeViewModel(code);

        return root;
    }

    private void observeViewModel(String code) {
        viewModel.getBracketDataObservable().observe(getViewLifecycleOwner(), bracketData -> {
            if (bracketData != null) {
                // clear
                tournamentBracketLayout.removeAllViews();

                LinearLayout layout = root.findViewById(R.id.tournament_bracket_linear_layout);
                layout.setPadding(10, 10, 10, 10);
                LinearLayout seedLayout = root.findViewById(R.id.tournament_bracket_seed_layout);

                canEdit = bracketData.tournament().canEdit();
                String status = bracketData.tournament().statusDisplay();
                isOngoing = status.toLowerCase().equals("ongoing");
                // if the status is open we don't display the bracket since it's empty
                if (status.toLowerCase().equals("open")) {
                    layout.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    p.bottomMargin = 25;
                    TextView infoView = new TextView(getActivity());
                    infoView.setText("This tournament has not been seeded yet.");
                    infoView.setTextColor(Color.BLACK);
                    infoView.setTextSize(16);
                    seedLayout.addView(infoView, p);
                    String idBase64 = bracketData.tournament().id();
                    String id = new String(Base64.getDecoder().decode(idBase64)).split(":")[1];


                    boolean canEdit = bracketData.tournament().canEdit();
                    if (canEdit) {
                        TextView tv = new TextView(getActivity());
                        tv.setTextColor(Color.BLACK);
                        tv.setTextSize(16);
                        tv.setText("You are authorized to start the tournament.");
                        Button b = new Button(getActivity());
                        b.setPadding(25, 0, 25, 0);
                        b.setBackgroundResource(R.drawable.border_button);
                        b.setText("Seed the bracket");

                        b.setOnClickListener(v -> {
                            ApiRepository.getInstance().seedTournamentBracket(viewModel, id, code);
                            seedLayout.removeView(infoView);
                            seedLayout.removeView(tv);
                            seedLayout.removeView(b);
                        });

                        seedLayout.addView(tv, p);
                        seedLayout.addView(b, p);
                    }
                }
                // tournament is ongoing or finished
                else {
                    int nRounds = bracketData.tournament().nRounds();
                    int firstRoundMatches = (int) Math.pow(2, nRounds);
                    grid = new GridLayout(getActivity());
                    grid.setRowCount(firstRoundMatches * 2); // space between matches
                    grid.setColumnCount(nRounds + 1);
                    try {
                        JSONObject root = new JSONObject(bracketData.tournament().matchBracket());
                        tournamentBracket(root, firstRoundMatches, nRounds, nRounds - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    tournamentBracketLayout.addView(grid);

                    // lets display the winner
                    TextView winnerView = root.findViewById(R.id.tournament_winner);
                    if (bracketData.tournament().winner() != null) {
                        winnerView.setText("Congratulations to the winner: " + bracketData.tournament().winner().username() + "!");
                    } else if (isOngoing && canEdit) {
                        winnerView.setTextSize(14);
                        winnerView.setText("You can submit match results by selecting the matches.");
                    }

                }
            }
        });

        viewModel.fetchTournamentBracket(code);
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
        int visitorScore = visitor.getJSONObject("score").getInt("score");

        matchLayout(id, homeUser, visitorUser, homeScore, visitorScore, row, col);

        if (home.has("seed")) {
            JSONObject nextHomeRoot = home.getJSONObject("seed").getJSONObject("sourceGame");
            JSONObject nextVisitorRoot = visitor.getJSONObject("seed").getJSONObject("sourceGame");
            int nextHomeRow = row - (int) Math.pow(2, counter);
            int nextVisitorRow = row + (int) Math.pow(2, counter);
            int nextCol = col - 1;
            lines(row, nextHomeRow, nextVisitorRow, col, nextCol);
            tournamentBracket(nextHomeRoot, nextHomeRow, nextCol, counter - 1);
            tournamentBracket(nextVisitorRoot, nextVisitorRow, nextCol, counter - 1);
        }
    }

    private void matchLayout(int id, String homeUser, String visitorUser, int homeScore, int visitorScore, int row, int col) {
        TableLayout matchLayout = new TableLayout(getActivity());

        int result = 0; // 0 - draw, 1 - home is winner, 2 - visitor is winner
        if (homeScore > visitorScore) result = 1;
        else if (visitorScore > homeScore) result = 2;

        // sides
        TableRow homeRow = new TableRow(getActivity());
        homeRow.setBackgroundColor(Color.argb(100, 255, 255, 0));
        TableRow visitorRow = new TableRow(getActivity());
        visitorRow.setBackgroundColor(Color.argb(80, 0, 0, 255));
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
        if (homeUser.length() > 0) {
            homeScoreView.setText(String.valueOf(homeScore));
            if (result == 1) {
                homeScoreView.setTextColor(Color.GREEN);
            }
        }

        // visitor
        TextView visitorUserView = new TextView(getActivity());
        visitorUserView.setText(visitorUser);
        TextView visitorScoreView = new TextView(getActivity());
        if (visitorUser.length() > 0) {
            visitorScoreView.setText(String.valueOf(visitorScore));
            if (result == 2) {
                visitorScoreView.setTextColor(Color.GREEN);
            }
        }

        // results (green text color for the match winner)
        if (result == 0) {
            if (homeUser.length() > 0 && visitorUser.length() == 0) {
                result = 3;
                homeScoreView.setTextColor(Color.CYAN);
                homeUserView.setTextColor(Color.CYAN);
            }
        }
        else if (result == 1) { // home
            homeScoreView.setTextColor(Color.CYAN);
            homeUserView.setTextColor(Color.CYAN);
        } else if (result == 2) { // visitor
            visitorScoreView.setTextColor(Color.CYAN);
            visitorUserView.setTextColor(Color.CYAN);
        }

        homeRow.addView(homeUserView, userParams);
        homeRow.addView(homeScoreView, scoreParams);
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

        if (isOngoing && canEdit && result == 0) {
            matchLayout.setOnClickListener(v -> {
                MatchResultsDialog newFragment = MatchResultsDialog.newInstance(id, code, homeUser, visitorUser);
                newFragment.setViewModel(viewModel);
                newFragment.show(getParentFragmentManager(), "Match Results Dialog");
            });
        }

        grid.addView(matchLayout, params);
    }

    // draws lines between matches utilizing borders
    private void lines(int currRow, int nextHomeRow, int nextVisitorRow, int currCol, int nextCol) {
        if (currCol < 2) return;
        ArrayList<int[]> listi = new ArrayList<>();
        for (int i=nextHomeRow; i<=nextVisitorRow; ++i) {
            if (i == currRow) continue;
            int[] x = {i, currCol};
            listi.add(x);
        }

        /*
        int[] x = {nextHomeRow, nextCol};
        listi.add(x);
        int[] y = {nextVisitorRow, nextCol};
        listi.add(y);
         */

        for (int[] arr: listi) {
            int i = arr[0];
            int j = arr[1];
            TableLayout view = new TableLayout(getActivity());
            // sides
            TableRow homeRow = new TableRow(getActivity());
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
            TextView homeScoreView = new TextView(getActivity());
            // visitor
            TextView visitorUserView = new TextView(getActivity());
            TextView visitorScoreView = new TextView(getActivity());

            homeRow.addView(homeUserView, userParams);
            homeRow.addView(homeScoreView, scoreParams);
            visitorRow.addView(visitorUserView, userParams);
            visitorRow.addView(visitorScoreView, scoreParams);

            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            rowParams.gravity = Gravity.CENTER_VERTICAL;
            view.addView(homeRow, rowParams);
            view.addView(visitorRow, rowParams);

            if (j == currCol) {
                view.setBackgroundResource(R.drawable.border_left);
            } else {
                view.setBackgroundResource(R.drawable.border_bottom);
            }
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j));
            grid.addView(view, params);
        }
    }
}
