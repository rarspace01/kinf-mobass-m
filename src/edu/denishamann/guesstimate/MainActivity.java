package edu.denishamann.guesstimate;

import edu.denishamann.guesstimate.BackgroundService.BackgroundServiceBinder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * 
 * @author denis
 * 
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

	public void startGame(View view) {
		Intent i = new Intent(this, StartActivity.class);
		this.startActivity(i);
		overridePendingTransition(0, 0);
	}

	public void startHowTo(View view) {
		Intent i = new Intent(this, HowToActivity.class);
		this.startActivity(i);
		overridePendingTransition(0, 0);
	}

	public void startAbout(View view) {
		Intent i = new Intent(this, AboutActivity.class);
		this.startActivity(i);
		overridePendingTransition(0, 0);
	}
}
