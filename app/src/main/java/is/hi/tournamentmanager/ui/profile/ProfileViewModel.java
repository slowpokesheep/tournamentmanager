package is.hi.tournamentmanager.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.tournament.MeQuery;

import is.hi.tournamentmanager.service.ApiRepository;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<MeQuery.Data> meDataObservable =
            new MutableLiveData<>();

    public ProfileViewModel() {
        ApiRepository.getInstance().getMe(meDataObservable);
    }


    public MutableLiveData<MeQuery.Data> getMeDataObservable() {
        return meDataObservable;
    }
}