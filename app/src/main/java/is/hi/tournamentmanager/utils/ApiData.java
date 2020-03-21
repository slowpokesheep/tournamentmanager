package is.hi.tournamentmanager.utils;

import com.apollographql.apollo.tournament.TournamentsQuery;

import java.util.Collections;
import java.util.List;

// singleton class responsible for 'caching' data from the API.
public class ApiData {
    public static ApiData apiData = null;

    private List<TournamentsQuery.Edge> tournamentsData = Collections.emptyList();

    public static ApiData getInstance() {
        if (apiData == null) {
            apiData = new ApiData();
        }
        return apiData;
    }

    public List<TournamentsQuery.Edge> getTournamentsData() {
        return tournamentsData;
    }

    public void setTournamentsData(List<TournamentsQuery.Edge> data) {
        tournamentsData = data;
    }
}
