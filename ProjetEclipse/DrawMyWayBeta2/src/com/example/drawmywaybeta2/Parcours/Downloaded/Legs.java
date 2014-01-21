package com.example.drawmywaybeta2.Parcours.Downloaded;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class Legs {

	private DistDur distance;
	private DistDur duration;
	private String end_address;
	private LatLng end_location;
	private String start_address;
	private LatLng start_location;
	private List<Step> steps;

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

	public String getEnd_address() {
		return end_address;
	}

	public void setEnd_address(String end_address) {
		this.end_address = end_address;
	}

	public LatLng getEnd_location() {
		return end_location;
	}

	public void setEnd_location(LatLng end_location) {
		this.end_location = end_location;
	}

	public String getStart_address() {
		return start_address;
	}

	public void setStart_address(String start_address) {
		this.start_address = start_address;
	}

	public LatLng getStart_location() {
		return start_location;
	}

	public void setStart_location(LatLng start_location) {
		this.start_location = start_location;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	@Override
	public String toString() {
		return "Legs [distance=" + distance + ", duration=" + duration
				+ ", end_address=" + end_address + ", end_location="
				+ end_location + ", start_address=" + start_address
				+ ", start_location=" + start_location + ", steps=" + steps
				+ "]";
	}

}
