package com.lyftoxi.lyftoxi.util;

import android.util.Log;

import com.lyftoxi.lyftoxi.CarInfo;
import com.lyftoxi.lyftoxi.UserInfo;
import com.lyftoxi.lyftoxi.dao.Car;
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Util {

    public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    public static Car convertCarInfoToCar(CarInfo carInfo){
        Car car = new Car();
        car.setAirbagAvailable(carInfo.isAirbagAvailable());
        car.setMusicAvailable(carInfo.isMusicAvailable());
        car.setAcAvailable(carInfo.isAcAvailable());
        car.setLuggageAllowed(carInfo.isLuggageAllowed());
        car.setSmokingAllowed(carInfo.isSmokingAllowed());
        car.setCarBrand(carInfo.getCarBrand());
        car.setCarModel(carInfo.getCarModel());
        car.setCarNo(carInfo.getCarNo());
        car.setNoOfSeat(carInfo.getNoOfSeat());
        car.setCarColor(carInfo.getCarColor());
        return car;

    }


    public static CarInfo convertCarToCarInfo(Car car){
        CarInfo carInfo = new CarInfo();
        carInfo.setAirbagAvailable(car.isAirbagAvailable());
        carInfo.setMusicAvailable(car.isMusicAvailable());
        carInfo.setAcAvailable(car.isAcAvailable());
        carInfo.setLuggageAllowed(car.isLuggageAllowed());
        carInfo.setSmokingAllowed(car.isSmokingAllowed());
        carInfo.setCarBrand(car.getCarBrand());
        carInfo.setCarModel(car.getCarModel());
        carInfo.setCarNo(car.getCarNo());
        carInfo.setNoOfSeat(car.getNoOfSeat());
        carInfo.setCarColor(car.getCarColor());
        return carInfo;

    }

    public static User convertUserInfoToUser(CurrentUserInfo userInfo)
    {
        User user = new User();
        user.setId(userInfo.getId());
        user.setName(userInfo.getName());
        user.setPhNo(userInfo.getPhNo());
        user.setEmail(userInfo.getEmail());
        user.setGender(userInfo.getGender());

        return user;
    }

    public static UserInfo convertUserToUserInfo(User user)
    {
        UserInfo userInfo = new UserInfo();
        userInfo.setUID(user.getId());
        userInfo.setName(user.getName());
        userInfo.setPhNo(user.getPhNo());
        userInfo.setEmail(user.getEmail());
        userInfo.setSex(user.getGender());

        return userInfo;
    }

    public static void addParamToUrl(StringBuffer url,String paramName, String paramValue)
    {
        if(url.toString().contains("?"))
        {
            url.append("&") ;
        }
        else
        {
            url.append("?") ;
        }
        url.append(paramName);
        url.append("=");
        url.append(paramValue);
    }

    public static String getResourceNameFromDisplayName(String displayName)
    {
        String resourceName = null;
        resourceName = displayName.toLowerCase();
        resourceName = resourceName.replace(" ","_");
        return resourceName;
    }
}
