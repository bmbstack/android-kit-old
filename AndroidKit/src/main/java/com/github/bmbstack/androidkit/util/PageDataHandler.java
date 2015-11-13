package com.github.bmbstack.androidkit.util;

import com.loopj.android.http.AsyncHttpResponseHandler;

public abstract class PageDataHandler<T> extends AsyncHttpResponseHandler {
	
	public abstract void onSuccess(PageData<T> data);
		
	
	public void onFailure( ){
		
	}
}
