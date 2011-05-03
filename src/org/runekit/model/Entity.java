package org.runekit.model;

public abstract class Entity implements Model {
	
	private Location location = Location.create(3222, 3222);
	
	public Location getLocation() {
		return location;
	}

}