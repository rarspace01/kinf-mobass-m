package edu.denishamann.guesstimate.activitys;

import edu.denishamann.guesstimate.R;
import edu.denishamann.guesstimate.R.layout;
import edu.denishamann.guesstimate.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GuessActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guess);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guess, menu);
		return true;
	}

}
