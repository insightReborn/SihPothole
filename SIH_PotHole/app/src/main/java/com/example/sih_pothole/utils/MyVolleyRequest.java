package com.example.sih_pothole.utils;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyVolleyRequest {

    private static MyVolleyRequest myInstance;
    private RequestQueue requestQueue;
    private static Context myContext;

    private MyVolleyRequest(Context context){
        myContext = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized MyVolleyRequest getInstance(Context context){
        if(myInstance==null){
            myInstance = new MyVolleyRequest(context);
        }
        return myInstance;
    }

    public <T>void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }

    private RequestQueue getRequestQueue() {
        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(myContext.getApplicationContext());
        }
        return requestQueue;
    }
}