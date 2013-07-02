package edu.denishamann.guesstimate.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Spinner;
import edu.denishamann.guesstimate.R;
import edu.denishamann.guesstimate.model.Game;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	public void startGame(View view) {
		Spinner diffSpinner = (Spinner) findViewById(R.id.difficulty_spinner);

		Intent i = null;
		switch (diffSpinner.getSelectedItemPosition()) {
			case 0:
				Game.getUniqueInstance().startGame(0, "playername");
				i = new Intent(this, MapActivity.class);
				break;

			case 1:
				i = new Intent(this, GuessActivity.class);
				break;

			default:
				i = new Intent(this, MapActivity.class);
				break;
		}

		this.startActivity(i);
		overridePendingTransition(0, 0);
	}

	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}
}
