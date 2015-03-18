/**
 * Copyright (c) 2013, BigBeard Team, Inc. All rights reserved. 
 */
package com.androidkit.context;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.InetSocketAddress;

public class NetworkSensor {
    /**
     * The invalid APN
     */
	public static final String INVALID_ACCESS_POINT = "None";	
	public static final String Network_2G = "2G";	
	public static final String Network_3G = "3G";	
	public static final String Network_WIFI = "WIFI";	
	
	public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_HSUPA = 9;
    public static final int NETWORK_TYPE_HSPAP = 15;
    public static final int NETWORK_TYPE_HSPA = 10;
    public static final int NETWORK_TYPE_HSDPA = 8;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_EHRPD = 14;
    
    private ConnectivityManager mConnectivityManager;
    private TelephonyManager mTelephonyManager;
    
    public NetworkSensor(Context context) {
        this.mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public String getAccessPoint() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info == null || info.getState() != NetworkInfo.State.CONNECTED) {
            return INVALID_ACCESS_POINT;
        }
        String networkType = info.getTypeName();
        if (networkType != null && !networkType.equalsIgnoreCase("WIFI")) {
            String mobiName = info.getExtraInfo();
            if (mobiName != null && !mobiName.equals("")) {
                networkType = mobiName;
            }
        }
        return networkType;
    }
    
    public boolean isApnActive() {
    	String ap = getNetworkType();
		return Network_3G.equalsIgnoreCase(ap) || Network_2G.equalsIgnoreCase(ap);
    }
    
	public boolean isWifiActive() {
		String ap = getAccessPoint();
		return Network_WIFI.equalsIgnoreCase(ap);
	}

    public InetSocketAddress getProxy() {
        /*if ("WIFI".equalsIgnoreCase(getAccessPoint())) {
            return null;
        }
        
        InetSocketAddress localInetSocketAddress = null;
        String host = android.net.Proxy.getDefaultHost();
        int port = android.net.Proxy.getDefaultPort();
        if ((host != null) && (host.trim().length() > 0)) {
            localInetSocketAddress = new InetSocketAddress(host, port);
        }
        return localInetSocketAddress;*/
        return null;
    }

    public boolean hasAvailableNetwork() {
        String apn = getAccessPoint();
        if (TextUtils.isEmpty(apn) || apn.equals(INVALID_ACCESS_POINT))
            return false;
        
        return true;
    }

    public String getNetworkType() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info == null || info.getState() != NetworkInfo.State.CONNECTED) {
            return INVALID_ACCESS_POINT;
        }
        
        int type = info.getType();
        int subtype = mTelephonyManager.getNetworkType();
        
        return getMobileNetworkType(type, subtype);
    }
    
	private String getMobileNetworkType(int type, int subtype) {
        if(type== ConnectivityManager.TYPE_WIFI){
            return Network_WIFI;
        }
        else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subtype){
            
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_LTE:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPAP:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
                return Network_3G;
                
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            case NETWORK_TYPE_IDEN:
            default:
                return Network_2G;
            }
        }
        return Network_2G;
    }
}
