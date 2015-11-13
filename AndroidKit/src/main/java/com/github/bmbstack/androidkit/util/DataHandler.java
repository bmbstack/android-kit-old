package com.github.bmbstack.androidkit.util;


import com.loopj.android.http.AsyncHttpResponseHandler;

public abstract class DataHandler<T> extends AsyncHttpResponseHandler {
	
	public abstract void onSuccess(T data);
		
	public void onFailure(){
		
	}

}
