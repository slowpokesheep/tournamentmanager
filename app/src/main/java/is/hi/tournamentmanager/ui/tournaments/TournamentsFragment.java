package is.hi.tournamentmanager.ui.tournaments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCallback;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.tournament.TournamentsQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.utils.ApolloConnector;

public class TournamentsFragment extends Fragment {

    private TournamentsViewModel tournamentsViewModel;
    private RecyclerView recyclerView;

    private TournamentListAdapter adapter = new TournamentListAdapter();
    Handler uiHandler = new Handler(Looper.getMainLooper());

    private final String STATE_LIST = "Tournament List Adapter Data";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        tournamentsViewModel = new ViewModelProvider(this).get(TournamentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tournaments, container, false);
        final TextView textView = root.findViewById(R.id.text_tournaments);

        recyclerView = root.findViewById(R.id.tournament_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        getTournaments(20);
        recyclerView.setAdapter(adapter);

        /*
        tournamentsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("Tournament", "saving state...");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIST, (ArrayList) adapter.getData());
    }

    public void getTournaments(int first) {
        ApolloConnector.getApolloClient().query(
            TournamentsQuery
                    .builder()
                    .first(first)
                    .build())
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentsQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<TournamentsQuery.Data> response) {
                    // Log.d("Tournament", "Response: " + response.data());
                    List<TournamentsQuery.Edge> edges = response.data().tournaments().edges();
                    adapter.setData(edges);
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    Log.d("Tournament", "Exception " + e.getMessage(), e);
                }
            }, uiHandler));
    }
}
