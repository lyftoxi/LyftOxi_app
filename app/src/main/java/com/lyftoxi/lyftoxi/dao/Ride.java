package com.lyftoxi.lyftoxi.dao;
import java.util.Date;
import java.util.List;




public class Ride {

	private String id;
	private User rideOwner;
	private Car car;
	private Location source;
	private Location destination;
	private Date startTime;
	private int fare;
	private String comment;
	private String rideStatus;
	private int interestedUserCount;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public User getRideOwner() {
		return rideOwner;
	}
	public void setRideOwner(User rideOwner) {
		this.rideOwner = rideOwner;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public Location getSource() {
		return source;
	}
	public void setSource(Location source) {
		this.source = source;
	}
	public Location getDestination() {
		return destination;
	}
	public void setDestination(Location destination) {
		this.destination = destination;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public int getFare() {
		return fare;
	}
	
	public void setFare(int fare) {
		this.fare = fare;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getRideStatus() {
		return rideStatus;
	}

	public void setRideStatus(String rideStatus) {
		this.rideStatus = rideStatus;
	}

    public int getInterestedUserCount() {
        return interestedUserCount;
    }

    public void setInterestedUserCount(int interestedUserCount) {
        this.interestedUserCount = interestedUserCount;
    }

    @Override
	public String toString() {
		return "Ride{" +
				"id='" + id + '\'' +
				", rideOwner=" + rideOwner +
				", car=" + car +
				", source=" + source +
				", destination=" + destination +
				", startTime=" + startTime +
				", fare=" + fare +
				", comment='" + comment + '\'' +
				", rideStatus='" + rideStatus + '\'' +
				", interestedUserCount=" + interestedUserCount +
				'}';
	}
}
