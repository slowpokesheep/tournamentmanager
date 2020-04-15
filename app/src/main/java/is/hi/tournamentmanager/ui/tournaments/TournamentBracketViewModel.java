package is.hi.tournamentmanager.ui.tournaments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.tournament.TournamentBracketQuery;

import is.hi.tournamentmanager.service.ApiRepository;

public class TournamentBracketViewModel extends ViewModel {

    private final MutableLiveData<TournamentBracketQuery.Data> bracketDataObservable =
            new MutableLiveData<>();

    public MutableLiveData<TournamentBracketQuery.Data> getBracketDataObservable() {
        return bracketDataObservable;
    }

    public void fetchTournamentBracket(String code) {
        ApiRepository.getInstance().getTournamentBracket(bracketDataObservable, code);
    }
}
