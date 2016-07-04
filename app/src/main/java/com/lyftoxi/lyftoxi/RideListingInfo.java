package com.lyftoxi.lyftoxi;

import android.media.Image;

import com.google.android.gms.maps.model.LatLng;
import com.lyftoxi.lyftoxi.singletons.RideInfo;

import java.util.Calendar;
import java.util.Date;

public class RideListingInfo {

    private UserInfo rideOf;
    private String id, name, sourceName, viaName, DestinationName, userMessage;
    private CarInfo car;
    private LatLng source, via, destination;
    private Date starTime;
    private int fare;
    private Boolean interested;
    private String status;
    private boolean showProgress;

    public UserInfo getRideOf() {
        return rideOf;
    }

    public void setRideOf(UserInfo rideOf) {
        this.rideOf = rideOf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getViaName() {
        return viaName;
    }

    public void setViaName(String viaName) {
        this.viaName = viaName;
    }

    public String getDestinationName() {
        return DestinationName;
    }

    public void setDestinationName(String destinationName) {
        DestinationName = destinationName;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public CarInfo getCar() {
        return car;
    }

    public void setCar(CarInfo car) {
        this.car = car;
    }

    public LatLng getSource() {
        return source;
    }

    public void setSource(LatLng source) {
        this.source = source;
    }

    public LatLng getVia() {
        return via;
    }

    public void setVia(LatLng via) {
        this.via = via;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public Date getStarTime() {
        return starTime;
    }

    public void setStarTime(Date starTime) {
        this.starTime = starTime;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public Boolean isInterested() {
        return interested;
    }

    public void setInterested(Boolean interested) {
        this.interested = interested;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public Boolean getInterested() {
        return interested;
    }

    @Override
    public String toString() {
        return "RideListingInfo{" +
                "rideOf=" + rideOf +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", viaName='" + viaName + '\'' +
                ", DestinationName='" + DestinationName + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", car=" + car +
                ", source=" + source +
                ", via=" + via +
                ", destination=" + destination +
                ", starTime=" + starTime +
                ", fare=" + fare +
                ", interested=" + interested +
                ", status='" + status + '\'' +
                ", showProgress=" + showProgress +
                '}';
    }
}