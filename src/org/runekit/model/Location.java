package org.runekit.model;

public class Location {

	private int x, y, z;
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getLocalX() {
		return x - 8 * (getRegionX() - 6);
	}
	
	public int getLocalY() {
		return y - 8 * (getRegionY() - 6);
	}
	
	public int getLocalX(Location loc) {
		return x - 8 * (loc.getRegionX() - 6);
	}
	
	public int getLocalY(Location loc) {
		return y - 8 * (loc.getRegionY() - 6);
	}
	
	public int getRegionX() {
		return (x >> 3);
	}
	
	public int getRegionY() {
		return (y >> 3);
	}
	
	public Location(int x, int y, int z) {
		this.x = x; this.y = y; this.z = z;
	}
	
	public Location(int x, int y) {
		this(x, y, 0);
	}
	
	public static Location create(int x, int y, int z) {
		return new Location(x, y, z);
	}
	
	public static Location create(int x, int y) {
		return new Location(x, y);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Location) {
			Location l = (Location) o;
			return l.getX() == x & l.getY() == y & l.getZ() == z;
		}
		return false;
	}
	
}