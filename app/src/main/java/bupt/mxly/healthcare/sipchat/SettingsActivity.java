package bupt.mxly.healthcare.sipchat;
import android.os.Bundle;
import android.preference.PreferenceActivity;

//import bupt.jsip_demo.R;
import bupt.mxly.healthcare.R;

public class SettingsActivity extends PreferenceActivity {
	 @SuppressWarnings("deprecation")

	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	       
	       addPreferencesFromResource(R.xml.preference);
	 
	    }
}
