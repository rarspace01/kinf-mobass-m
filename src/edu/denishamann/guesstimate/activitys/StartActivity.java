package edu.denishamann.guesstimate.activitys;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;

import edu.denishamann.guesstimate.R;
import edu.denishamann.guesstimate.model.Game;

public class StartActivity extends Activity {

	private boolean useRealLateration = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		EditText playername = (EditText) findViewById(R.id.playername);
		if (Game.getInstance().getPlayerName_().isEmpty() || Game.getInstance().getPlayerName_().equals("Player 1")) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		} else {
			playername.setText(Game.getInstance().getPlayerName_());
		}
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.start, menu);

		menu.findItem(R.id.real_lateration).setChecked(true);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				startActivity(new Intent(StartActivity.this, MainActivity.class));
				break;
			case R.id.real_lateration:
				menuItem.setChecked(!menuItem.isChecked());
				useRealLateration = menuItem.isChecked();
				break;

			default:
				break;
		}
		return true;
	}

	public void startGame(View view) {
		Spinner diffSpinner = (Spinner) findViewById(R.id.difficulty_spinner);
		EditText player = (EditText) findViewById(R.id.playername);
		String playername = player.getText().toString();
		if (playername.isEmpty()) {
			playername = "Player 1";
		}

		Intent i = null;
		switch (diffSpinner.getSelectedItemPosition()) {
			case 0:
				Game.getInstance().startGame(0, playername, useRealLateration);
				i = new Intent(this, MapActivity.class);
				break;

			case 1:
				Game.getInstance().startGame(1, playername, useRealLateration);
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
