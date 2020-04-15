package is.hi.tournamentmanager.ui.newtournament;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewTournamentViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NewTournamentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

