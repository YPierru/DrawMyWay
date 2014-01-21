package com.example.drawmywaybeta2.Parcours.Downloaded;

import java.util.List;

public class DirectionsResponse {

	private List<Route> routes;

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	@Override
	public String toString() {
		return "DirectionsResponse [routes=" + routes + "]";
	}
	
	
}
