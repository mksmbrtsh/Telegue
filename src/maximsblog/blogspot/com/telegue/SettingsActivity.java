package maximsblog.blogspot.com.telegue;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.fragmented_preferences);
	    setResult(RESULT_OK, new Intent());
	}
	
	@Override
	public void onBackPressed() {
		setResult(1, new Intent());
		finish();
		super.onBackPressed();
	}
}
