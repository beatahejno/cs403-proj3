package com.example.cs403_proj3;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestManager {
    /*
    Helper class for Volley requests that is set up as a Singleton, which is recommended
    in the Android Documentation.
    This allows the requestQueue to be accessed from any activity without having it be static
    or have an instance of it be passed around. The RequestManager object is actually stored
    inside of the class itself and can be accessed by a getter.
     */
    private static RequestManager instance;
    private final Context ctx;
    private RequestQueue requestQueue;

    final static String TAG = "requests";

    //internal constructor
    private RequestManager(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }
    //getter for internal RequestManager instance
    //if one has not been created, it will do so first
    public static synchronized RequestManager getInstance(Context context) {
        if (instance == null) instance = new RequestManager(context);
        return instance;
    }
    //returns the Volley request queue if it has been created, if not it will create one first
    public RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }
    //adds a request to the Volley request queue
    //<T> syntax denotes a generic type, since it does not know the "type" of request it will
    // receive.
    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

}