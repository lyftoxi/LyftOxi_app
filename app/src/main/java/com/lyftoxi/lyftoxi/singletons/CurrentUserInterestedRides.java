package com.lyftoxi.lyftoxi.singletons;

import com.lyftoxi.lyftoxi.dao.Ride;
import com.lyftoxi.lyftoxi.dao.TakeRide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DhimanZ on 6/4/2016.
 */
public class CurrentUserInterestedRides {
    private static CurrentUserInterestedRides ourInstance = new CurrentUserInterestedRides();

    private List<TakeRide> rides = new ArrayList<TakeRide>();

    public static CurrentUserInterestedRides getInstance() {
        return ourInstance;
    }

    private CurrentUserInterestedRides() {
    }

    public List<TakeRide> getRides() {
        return rides;
    }

    public void setRides(List<TakeRide> rides) {
        this.rides = rides;
    }

    @Override
    public String toString() {
        return "CurrentUserInterestedRides{" +
                "rides=" + rides +
                '}';
    }

    public static void reset()
    {
        ourInstance=new CurrentUserInterestedRides();
    }
}
