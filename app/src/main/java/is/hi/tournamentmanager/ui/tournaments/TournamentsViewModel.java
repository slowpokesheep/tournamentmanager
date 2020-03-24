package is.hi.tournamentmanager.ui.tournaments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.tournament.TournamentsQuery;

import is.hi.tournamentmanager.service.ApiRepository;

public class TournamentsViewModel extends ViewModel {

    private final MutableLiveData<TournamentsQuery.Data> tournamentsDataObservable =
            new MutableLiveData<>();

    public TournamentsViewModel() {
        fetchTournaments("");
    }

    public MutableLiveData<TournamentsQuery.Data> getTournamentsDataObservable() {
        return tournamentsDataObservable;
    }

    public void fetchTournaments(String endCursor) {
        ApiRepository.getInstance().getTournaments(tournamentsDataObservable, 20, endCursor);
    }
}