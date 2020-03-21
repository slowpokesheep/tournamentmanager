package is.hi.tournamentmanager.ui.home;

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
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.tournament.TournamentsQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.api.ApolloConnector;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private TournamentListAdapter adapter;
    Handler uiHandler = new Handler(Looper.getMainLooper());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);

        recyclerView = root.findViewById(R.id.tournament_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TournamentListAdapter();
        getTournaments();
        recyclerView.setAdapter(adapter);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

    public void getTournaments() {
        ApolloConnector.getApolloClient().query(
            TournamentsQuery
                .builder()
                .build())
            .enqueue(new ApolloCall.Callback<TournamentsQuery.Data>() {
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
            });
    }



}
