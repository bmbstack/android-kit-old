package com.github.bmbstack.androidkit.util;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

public class DefaultAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

	private static final String TAG = "DefaultAsyncHttpResponseHandler";
	private PageDataHandler mPageDataHandler;
	
	public DefaultAsyncHttpResponseHandler(PageDataHandler handler) {
		mPageDataHandler = handler;
	}


	@Override
	public void onStart() {
		mPageDataHandler.onStart();
	}
	
	@Override
	public void onFinish() {
		mPageDataHandler.onFinish();
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
		mPageDataHandler.onFailure();
	}
}
