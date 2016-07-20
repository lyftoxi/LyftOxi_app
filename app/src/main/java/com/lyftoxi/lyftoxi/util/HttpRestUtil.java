package com.lyftoxi.lyftoxi.util;

import android.content.Context;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Base64;
import com.lyftoxi.lyftoxi.R;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;


import java.io.IOException;

public class HttpRestUtil {

    private HttpHeaders header;
    private HttpTransport transport;

    private static String API_AUTH_USER_NAME;
    private static String API_AUTH_PASSWORD;
    private static String API_BASE_URL;



    public HttpRestUtil(Context context)
    {
        API_BASE_URL = context.getString(R.string.lyftoxi_api_end_point);
        API_AUTH_USER_NAME = context.getString(R.string.lyftoxi_api_auth_header_user_name);
        API_AUTH_PASSWORD = context.getString(R.string.lyftoxi_api_auth_header_user_password);

        String usernameAndPassword = API_AUTH_USER_NAME+":"+API_AUTH_PASSWORD;
        String basicAuthHeader = "Basic "+new String(Base64.encodeBase64(usernameAndPassword.getBytes()));
        Log.d("lyftoxi.debug"," basicAuthHeader "+basicAuthHeader);
        header = new HttpHeaders();
        header.setAuthorization(basicAuthHeader);
        transport = AndroidHttp.newCompatibleTransport();
    }


    public String httpGet(String url) throws IOException {
        String response = null;
        url=url.replace("+","%2b");
        GenericUrl fullUrl = new GenericUrl(API_BASE_URL+url);
        HttpRequest getRequest  = transport.createRequestFactory().buildGetRequest(fullUrl);
        getRequest.setHeaders(header);
        Log.d("lyftoxi.debug", "input " + fullUrl);
        HttpResponse httpResponse  = getRequest.execute();

        response  =  Util.getStringFromInputStream(httpResponse.getContent());
        Log.d("lyftoxi.debug", "output " + response);
        return response;
    }

    public String httpPost(String url, Object payload) throws IOException,Exception {
        String response = null;

        if(null == payload)
        {
            throw new Exception("payload cannot be null");
        }
        GenericUrl fullUrl = new GenericUrl(API_BASE_URL+url);
        Log.d("lyftoxi.debug", "input " + fullUrl);
        Log.d("lyftoxi.debug","info json "+payload.toString());
        HttpRequest postRequest  = transport.createRequestFactory().buildPostRequest(fullUrl,
                ByteArrayContent.fromString("application/json", payload.toString()));
        postRequest.setHeaders(header);

        HttpResponse httpResponse  = postRequest.execute();

        response  =  Util.getStringFromInputStream(httpResponse.getContent());
        Log.d("lyftoxi.debug", "output " + response);
        return response;
    }

    public String httpPostSimple(String url) throws IOException,Exception {
        String response = null;

        GenericUrl fullUrl = new GenericUrl(API_BASE_URL+url);
        HttpRequest postRequest  = transport.createRequestFactory().buildPostRequest(fullUrl,
                ByteArrayContent.fromString("text/html",""));
        postRequest.setHeaders(header);
        Log.d("lyftoxi.debug", "input " + fullUrl);
        HttpResponse   httpResponse = postRequest.execute();

        response  =  Util.getStringFromInputStream(httpResponse.getContent());
        Log.d("lyftoxi.debug", "output " + response);
        return response;
    }

    public String httpPut(String url, Object payload) throws IOException,Exception {
        String response = null;

        if(null == payload)
        {
            throw new Exception("payload cannot be null");
        }
        GenericUrl fullUrl = new GenericUrl(API_BASE_URL+url);
        Log.d("lyftoxi.debug", "input " + fullUrl);
        Log.d("lyftoxi.debug","signup info json "+payload.toString());
        HttpRequest putRequest  = transport.createRequestFactory().buildPutRequest(fullUrl,
                ByteArrayContent.fromString("application/json", payload.toString()));
        putRequest.setHeaders(header);

        HttpResponse httpResponse  = putRequest.execute();

        response  =  Util.getStringFromInputStream(httpResponse.getContent());
        Log.d("lyftoxi.debug", "output " + response);
        return response;
    }

    public String httpDelete(String url) throws IOException {
        String response = null;
        GenericUrl fullUrl = new GenericUrl(API_BASE_URL+url);
        HttpRequest deleteRequest  = transport.createRequestFactory().buildDeleteRequest(fullUrl);
        deleteRequest.setHeaders(header);
        Log.d("lyftoxi.debug", "input " + fullUrl);
        HttpResponse httpResponse  = deleteRequest.execute();

        response  =  Util.getStringFromInputStream(httpResponse.getContent());
        Log.d("lyftoxi.debug", "output " + response);
        return response;
    }



}
