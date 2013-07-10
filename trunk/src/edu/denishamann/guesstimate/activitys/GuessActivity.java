package edu.denishamann.guesstimate.activitys;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.denishamann.guesstimate.R;
import edu.denishamann.guesstimate.model.Game;
import edu.denishamann.guesstimate.model.GuessPoint;

public class GuessActivity extends Activity {

	private List<GuessPoint> guessPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guess);

		guessPoints = Game.getInstance().getLocationsToBeGuessed();

		TextView lblGuess1 = (TextView) findViewById(R.id.lblGuess1);
		TextView lblGuess2 = (TextView) findViewById(R.id.lblGuess2);
		TextView lblGuess3 = (TextView) findViewById(R.id.lblGuess3);
		TextView lblGuess4 = (TextView) findViewById(R.id.lblGuess4);

		lblGuess1.setText(guessPoints.get(0).getDescription_());
		lblGuess2.setText(guessPoints.get(1).getDescription_());
		lblGuess3.setText(guessPoints.get(2).getDescription_());
		lblGuess4.setText(guessPoints.get(3).getDescription_());
	}

	public void startGame(View view) {
		EditText guess1 = (EditText) findViewById(R.id.guess1);
		EditText guess2 = (EditText) findViewById(R.id.guess2);
		EditText guess3 = (EditText) findViewById(R.id.guess3);
		EditText guess4 = (EditText) findViewById(R.id.guess4);

		if (!guess1.getText().toString().isEmpty()
				&& !guess2.getText().toString().isEmpty()
				&& !guess3.getText().toString().isEmpty()
				&& !guess4.getText().toString().isEmpty()) {
			guessPoints.get(0).setGuessDistance_(
					Integer.valueOf(guess1.getText().toString()));
			guessPoints.get(1).setGuessDistance_(
					Integer.valueOf(guess2.getText().toString()));
			guessPoints.get(2).setGuessDistance_(
					Integer.valueOf(guess3.getText().toString()));
			guessPoints.get(3).setGuessDistance_(
					Integer.valueOf(guess4.getText().toString()));
			if (Game.getInstance().evaluateGuesses()) {
				Intent i = new Intent(this, MapActivity.class);
				startActivity(i);
				overridePendingTransition(0, 0);
			}
		}
	}

	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}
}
