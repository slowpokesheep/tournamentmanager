package is.hi.tournamentmanager.ui.profile;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.apollographql.apollo.tournament.MeQuery;

import is.hi.tournamentmanager.service.ApiRepository;

public class ProfileViewModel extends AndroidViewModel {

    private final MutableLiveData<MeQuery.Data> meDataObservable =
            new MutableLiveData<>();

    public ProfileViewModel(Application app) {
        super(app);
        ApiRepository.getInstance().getMe(meDataObservable);
    }


    public MutableLiveData<MeQuery.Data> getMeDataObservable() {
        return meDataObservable;
    }
}