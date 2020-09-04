package bupt.mxly.healthcare.ui.smartdev;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SmartdevViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SmartdevViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is smartdev fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}