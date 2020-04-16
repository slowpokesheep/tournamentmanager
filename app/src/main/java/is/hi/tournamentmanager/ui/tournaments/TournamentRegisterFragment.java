
package is.hi.tournamentmanager.ui.tournaments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.apollographql.apollo.tournament.TournamentUsersQuery;
import com.google.android.material.tabs.TabLayout;

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

    public void newLine(int layout, String label, String input) {
        TableLayout firstlayout = root.findViewById(layout);

        LinearLayout secondlayoout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.template_linear, null);
        View thirdayoout = this.getLayoutInflater().inflate(R.layout.template_view, null);

        TextView l = secondlayoout.findViewById(R.id.template_label);
        TextView i = secondlayoout.findViewById(R.id.template_input);

        l.setText("");
        i.setText(input);

        firstlayout.addView(secondlayoout);
        firstlayout.addView(thirdayoout);
    }

    private void observeViewModel(String code) {
        tournamentRegisterViewModel.getUsersObservable().observe(getViewLifecycleOwner(), usersData -> {
            if (usersData != null) {
                Log.d("Tournament participants", usersData.toString());
                TournamentUsersQuery.RegisteredUsers registeredUsers = usersData.tournament().registeredUsers();
                TournamentUsersQuery.Admins admins = usersData.tournament().admins();
                TournamentUsersQuery.Creator host = usersData.tournament().creator();

                registeredUsers.edges().forEach((edge -> {
                    TournamentUsersQuery.Node1 node = edge.node();
                    String username = node.username();
                    System.out.println(username);
                    newLine(R.id.tournament_register_users_layout, "Username", username);
                }));

                admins.edges().forEach((edge -> {
                    TournamentUsersQuery.Node node = edge.node();
                    String username = node.username();
                    System.out.println(username);
                    newLine(R.id.tournament_register_admin_layout, "Username", username);
                }));

                String hostUsername = host.username();

                newLine(R.id.tournament_register_host_layout, "Host", hostUsername);

                System.out.println(hostUsername);

                //newLine("Hallo");
                //newLine("Ballo");
                //newLine("thusdhuadasd");

                //TextView dynamicTextView = new TextView(root.getContext());
                //dynamicTextView.setText(" Hello World ");
                //main_layout.addView(dynamicTextView);
            }
        });

        tournamentRegisterViewModel.fetchTournamentUsers(code);
    }
}
