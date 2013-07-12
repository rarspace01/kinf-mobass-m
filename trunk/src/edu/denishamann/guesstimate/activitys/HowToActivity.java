package edu.denishamann.guesstimate.activitys;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import edu.denishamann.guesstimate.R;

/**
 * @author PaulB
 */
public class HowToActivity extends Activity {

	private boolean expandedEasy   = false;
	private boolean expandedNormal = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_how_to1);

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

	public void expandEasy(View view) {
		TextView tv = (TextView) view;
		if (expandedEasy) {
			tv.setMaxLines(5);
			expandedEasy = false;
		} else {
			tv.setMaxLines(100);
			expandedEasy = true;
		}
	}

	public void expandNormal(View view) {
		TextView tv = (TextView) view;
		if (expandedNormal) {
			tv.setMaxLines(5);
			expandedNormal = false;
		} else {
			tv.setMaxLines(100);
			expandedNormal = true;
		}
	}
}
