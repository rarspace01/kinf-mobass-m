package edu.denishamann.guesstimate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class HowToActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_how_to);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.how_to, menu);
		return true;
	}

	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}
}
