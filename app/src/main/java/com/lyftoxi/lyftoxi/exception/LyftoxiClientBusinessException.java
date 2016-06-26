package com.lyftoxi.lyftoxi.exception;


public class LyftoxiClientBusinessException extends LyftoxiClientException{

    public LyftoxiClientBusinessException(String code, String message)
    {
        super(code,message);
    }
}
