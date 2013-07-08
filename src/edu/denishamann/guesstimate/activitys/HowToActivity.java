package edu.denishamann.guesstimate.activitys;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.denishamann.guesstimate.R;

public class HowToActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_how_to);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				startActivity(new Intent(HowToActivity.this, MainActivity.class));
				break;
			default:
				break;
		}

		return true;
	}

	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}
}
