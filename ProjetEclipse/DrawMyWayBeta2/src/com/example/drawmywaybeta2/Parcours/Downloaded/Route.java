package com.example.drawmywaybeta2.Parcours.Downloaded;

import java.util.List;

public class Route {
	
	private List<Legs> legs;
	private Poly overview_polyline;
	public List<Legs> getLegs() {
		return legs;
	}
	public void setLegs(List<Legs> legs) {
		this.legs = legs;
	}
	public Poly getOverview_polyline() {
		return overview_polyline;
	}
	public void setOverview_polyline(Poly overview_polyline) {
		this.overview_polyline = overview_polyline;
	}
	@Override
	public String toString() {
		return "Route [legs=" + legs + ", overview_polyline="
				+ overview_polyline + "]";
	}
	
	

}
