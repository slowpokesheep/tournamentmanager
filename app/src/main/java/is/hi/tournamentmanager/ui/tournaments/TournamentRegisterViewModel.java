package is.hi.tournamentmanager.ui.tournaments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.tournament.TournamentUsersQuery;

import is.hi.tournamentmanager.service.ApiRepository;

public class TournamentRegisterViewModel extends ViewModel {


    private final MutableLiveData<TournamentUsersQuery.Data> usersObservable =
            new MutableLiveData<>();

    public MutableLiveData<TournamentUsersQuery.Data> getUsersObservable() {
        return usersObservable;
    }

    public void fetchTournamentUsers(String code) {
        ApiRepository.getInstance().getTournamentUsers(usersObservable, code);
    }
}
