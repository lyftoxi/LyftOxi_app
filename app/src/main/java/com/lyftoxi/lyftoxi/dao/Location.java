package com.lyftoxi.lyftoxi.dao;

public class Location {
	private String name;
	private double longitude;
	private double latitude;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	@Override
	public String toString() {
		return "Location [name=" + name + ", longitude=" + longitude
				+ ", latitude=" + latitude + "]";
	}
		
}
