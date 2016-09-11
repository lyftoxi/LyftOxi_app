package com.lyftoxi.lyftoxi.dao;

import java.util.Date;

public class Promo {
	private String id;
	private String rideId;
	private String userId;
	private Date time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRideId() {
		return rideId;
	}
	public void setRideId(String rideId) {
		this.rideId = rideId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "Promo [id=" + id + ", rideId=" + rideId + ", userId=" + userId + ", time=" + time + "]";
	}
	
	
}