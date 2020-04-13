package is.hi.tournamentmanager.ui.tournaments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.tournament.TournamentDetailsQuery;
import com.apollographql.apollo.tournament.TournamentsQuery;

import is.hi.tournamentmanager.service.ApiRepository;

public class TournamentDetailsViewModel extends ViewModel {

    private final MutableLiveData<TournamentDetailsQuery.Data> tournamentDetailsDataObservable =
            new MutableLiveData<>();

    public MutableLiveData<TournamentDetailsQuery.Data> getTournamentDetailsDataObservable() {
        return tournamentDetailsDataObservable;
    }

    public void fetchTournamentDetails(String code) {
        ApiRepository.getInstance().getTournamentDetails(tournamentDetailsDataObservable, code);
    }
}
