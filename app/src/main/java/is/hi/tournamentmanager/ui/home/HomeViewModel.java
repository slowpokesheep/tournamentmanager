package is.hi.tournamentmanager.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.tournament.TournamentInfoQuery;
import is.hi.tournamentmanager.service.ApiRepository;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<TournamentInfoQuery.Data> infoObservable =
            new MutableLiveData<>();

    public MutableLiveData<TournamentInfoQuery.Data> getInfoObservable() {
        return infoObservable;
    }

    public void fetchTournaments(String code) {
        ApiRepository.getInstance().getTournamentInfo(infoObservable, code);
    }
}