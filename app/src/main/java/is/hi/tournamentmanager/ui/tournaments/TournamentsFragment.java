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

import java.util.Collections;
import java.util.List;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.utils.ApolloConnector;
import is.hi.tournamentmanager.utils.SharedPref;

public class TournamentsFragment extends Fragment {

    private TournamentsViewModel tournamentsViewModel;
    private RecyclerView recyclerView;

    private TournamentListAdapter adapter = new TournamentListAdapter();
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        tournamentsViewModel = new ViewModelProvider(this).get(TournamentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tournaments, container, false);

        recyclerView = root.findViewById(R.id.tournament_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        getTournaments(20);
        recyclerView.setAdapter(adapter);

        return root;
    }

    public void getTournaments(int first) {
        TournamentsQuery query = TournamentsQuery
            .builder()
            .first(first)
            .build();
        ApolloConnector.getInstance().getApolloClient().query(query)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentsQuery.Data>() {

                @Override
                public void onResponse(@NotNull Response<TournamentsQuery.Data> response) {
                    Log.d("Tournament", "Response: " + response.data());
                    TournamentsQuery.Data data = response.data();
                    // if data is null it is most likely because our token is expired/invalid
                    if (data == null || data.tournaments() == null) {
                        SharedPref.getInstance().clearToken();
                        // TODO: display error message
                        adapter.setData(Collections.emptyList());
                    } else {
                        List<TournamentsQuery.Edge> edges = response.data().tournaments().edges();
                        adapter.setData(edges);
                    }
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    Log.d("Tournament", "Exception " + e.getMessage(), e);
                }

            }, uiHandler));
    }
}
