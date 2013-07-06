package edu.denishamann.guesstimate.model;

public class GuessPoint {

	private GeoLocation location_;
	private String      description_;
	private double      guessDistance_;
	private double      guessCalcedDistance_;
	private boolean hasBeenGuessed = false;

	public GuessPoint(GuessPoint gp) {
		location_ = gp.getLocation_();
		description_ = gp.getDescription_();
	}

	public GuessPoint(GeoLocation location, String description) {
		this.location_ = location;
		this.description_ = description;
	}

	public GeoLocation getLocation_() {
		return location_;
	}

	public void setLocation_(GeoLocation location_) {
		this.location_ = location_;
	}

	public String getDescription_() {
		return description_;
	}

	public void setDescription_(String description_) {
		this.description_ = description_;
	}

	public double getGuessDistance_() {
		return guessDistance_;
	}

	public void setGuessDistance_(double guessDistance_) {
		this.guessDistance_ = guessDistance_;
		hasBeenGuessed = true;
	}

	public double getGuessCalcedDistance_() {
		return guessCalcedDistance_;
	}

	public void setGuessCalcedDistance_(double guessCalcDistance_) {
		this.guessCalcedDistance_ = guessCalcDistance_;
	}


	public boolean hasBeenGuessed() {
		return hasBeenGuessed;
	}
}
