package com.example.drawmywaybeta2.Parcours.Downloaded;

import com.google.android.gms.maps.model.LatLng;

public class Step {

	private DistDur distance;
	private DistDur duration;
	private LatLng end_location;
	private String html_instructions;
	private Poly polyline;
	private LatLng start_location;

	public DistDur getDistance() {
		return distance;
	}

	public void setDistance(DistDur distance) {
		this.distance = distance;
	}

	public DistDur getDuration() {
		return duration;
	}

	public void setDuration(DistDur duration) {
		this.duration = duration;
	}

	public LatLng getEnd_location() {
		return end_location;
	}

	public void setEnd_location(LatLng end_location) {
		this.end_location = end_location;
	}

	public String getHtml_instructions() {
		return html_instructions;
	}

	public void setHtml_instructions(String html_instructions) {
		this.html_instructions = html_instructions;
	}

	public Poly getPolyline() {
		return polyline;
	}

	public void setPolyline(Poly polyline) {
		this.polyline = polyline;
	}

	public LatLng getStart_location() {
		return start_location;
	}

	public void setStart_location(LatLng start_location) {
		this.start_location = start_location;
	}

	@Override
	public String toString() {
		return "Step [distance=" + distance + ", duration=" + duration
				+ ", end_location=" + end_location + ", html_instructions="
				+ html_instructions + ", polyline=" + polyline
				+ ", start_location=" + start_location + "]";
	}

}
