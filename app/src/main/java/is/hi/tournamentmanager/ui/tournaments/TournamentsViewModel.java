package is.hi.tournamentmanager.ui.tournaments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.tournament.TournamentsQuery;

import is.hi.tournamentmanager.service.ApiRepository;

public class TournamentsViewModel extends ViewModel {

    private final MutableLiveData<TournamentsQuery.Data> tournamentsDataObservable =
            new MutableLiveData<>();

    public MutableLiveData<TournamentsQuery.Data> getTournamentsDataObservable() {
        return tournamentsDataObservable;
    }

    public void fetchTournaments(int type, int superCategory, String search, String endCursor) {
        ApiRepository.getInstance().getTournaments(tournamentsDataObservable, type, superCategory, search, 20, endCursor);
    }
}