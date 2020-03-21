package is.hi.tournamentmanager.ui.tournaments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TournamentsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TournamentsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tournaments fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}