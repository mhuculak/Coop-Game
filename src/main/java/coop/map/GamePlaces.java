package coop.map;

import java.util.*;

public class GamePlaces {
	private List<Place> places;	

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	public Place findPlace(Position position) {
		for (Place place : places) {
			if (place.contains(position)) {
				return place;
			}
		}
		return null;
	}

	public Place findPlace(int cellID) {
		for (Place place : places) {
			if (cellID == place.getCellID()) {
				return place;
			}
		}
		return null;
	}
	
}