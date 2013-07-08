package edu.denishamann.guesstimate.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import edu.denishamann.guesstimate.R;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		getActionBar().setHomeButtonEnabled(true);
	}

	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}
}
