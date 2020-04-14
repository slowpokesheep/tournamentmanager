package is.hi.tournamentmanager.ui.tournaments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TournamentInfoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TournamentInfoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public int getData(){
        return 0;
    }
}
