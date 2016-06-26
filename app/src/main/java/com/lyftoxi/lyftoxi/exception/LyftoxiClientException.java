package com.lyftoxi.lyftoxi.exception;


public class LyftoxiClientException extends Exception{

    public LyftoxiClientException(String code, String message)
    {
        super(message);
    }
}
