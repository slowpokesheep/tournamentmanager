package is.hi.tournamentmanager.ui.tournaments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.tournament.TournamentInfoQuery;

import is.hi.tournamentmanager.service.ApiRepository;

public class TournamentInfoViewModel extends ViewModel {

    private final MutableLiveData<TournamentInfoQuery.Data> infoObservable =
            new MutableLiveData<>();

    public MutableLiveData<TournamentInfoQuery.Data> getInfoObservable() {
        return infoObservable;
    }

    public void fetchTournamentInfo(String code) {
        ApiRepository.getInstance().getTournamentInfo(infoObservable, code);
    }
}
