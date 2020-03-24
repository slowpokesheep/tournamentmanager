package is.hi.tournamentmanager.ui.profile;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.apollographql.apollo.tournament.MeQuery;

import is.hi.tournamentmanager.service.ApiRepository;
import is.hi.tournamentmanager.ui.authentication.LoginViewModel;

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