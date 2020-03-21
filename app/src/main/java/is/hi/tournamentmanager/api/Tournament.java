package is.hi.tournamentmanager.api;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.tournament.TournamentsQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Tournament {

    public static void getTournaments() {
        ApolloConnector.getApolloClient().query(
                TournamentsQuery
                .builder()
                .build())
                .enqueue(new ApolloCall.Callback<TournamentsQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<TournamentsQuery.Data> response) {
                        // Log.d("Tournament", "Response: " + response.data());
                        TournamentsQuery.Data data = response.data();
                        TournamentsQuery.Tournaments tournaments = data.tournaments();
                        List<TournamentsQuery.Edge> edges = tournaments.edges();
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("Tournament", "Exception " + e.getMessage(), e);
                    }
                });
    }

}
