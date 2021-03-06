package com.yoavst.quickapps.toggles;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Check device's network connectivity and speed
 *
 * @author emil http://stackoverflow.com/users/220710/emil
 */
public class Connectivity {

	/**
	 * Get the network info
	 *
	 * @param context
	 * @return the netowrk info
	 */
	public static NetworkInfo getNetworkInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/**
	 * Check if there is any connectivity
	 *
	 * @param context
	 * @return true if connected
	 */
	public static boolean isConnected(Context context) {
		NetworkInfo info = Connectivity.getNetworkInfo(context);
		return (info != null && info.isConnected());
	}

	/**
	 * Check if there is any connectivity to a Wifi network
	 *
	 * @param context
	 * @return true if connected to wifi
	 */
	public static boolean isConnectedWifi(Context context) {
		NetworkInfo info = Connectivity.getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * Check if there is any connectivity to a mobile network
	 *
	 * @param context
	 * @return
	 */
	public static boolean isConnectedMobile(Context context) {
		NetworkInfo info = Connectivity.getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
	}

	/**
	 * Check if there is fast connectivity
	 *
	 * @param context
	 * @return
	 */
	public static boolean isConnectedFast(Context context) {
		NetworkInfo info = Connectivity.getNetworkInfo(context);
		return (info != null && info.isConnected() && Connectivity.isConnectionFast(info.getType(), info.getSubtype()));
	}

	/**
	 * Check if the connection is fast
	 *
	 * @param type
	 * @param subType
	 * @return
	 */
	public static boolean isConnectionFast(int type, int subType) {
		if (type == ConnectivityManager.TYPE_WIFI) {
			return true;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
				case TelephonyManager.NETWORK_TYPE_1xRTT:
					return false; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_CDMA:
					return false; // ~ 14-64 kbps
				case TelephonyManager.NETWORK_TYPE_EDGE:
					return false; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
					return true; // ~ 400-1000 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
					return true; // ~ 600-1400 kbps
				case TelephonyManager.NETWORK_TYPE_GPRS:
					return false; // ~ 100 kbps
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					return true; // ~ 2-14 Mbps
				case TelephonyManager.NETWORK_TYPE_HSPA:
					return true; // ~ 700-1700 kbps
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					return true; // ~ 1-23 Mbps
				case TelephonyManager.NETWORK_TYPE_UMTS:
					return true; // ~ 400-7000 kbps
			/*
			 * Above API level 7, make sure to set android:targetSdkVersion 
			 * to appropriate level to use these
			 */
				case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
					return true; // ~ 1-2 Mbps
				case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
					return true; // ~ 5 Mbps
				case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
					return true; // ~ 10-20 Mbps
				case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
					return false; // ~25 kbps
				case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
					return true; // ~ 10+ Mbps
				// Unknown
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				default:
					return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isAirplaneMode(Context context) {
		return Integer.parseInt(Settings.Global.getString(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON)) != 0;
	}

	public static boolean isApOn(Context context) {
		WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		try {
			Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
			method.setAccessible(true);
			return (Boolean) method.invoke(wifimanager);
		} catch (Throwable ignored) {
		}
		return false;
	}

	//turn on/off wifi hotspot as toggle
	public static boolean configApState(Context context) {
		WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiConfiguration wificonfiguration = null;
		try {
			if (isApOn(context)) {
				//turn off whether wifi is on
				wifimanager.setWifiEnabled(false);
			}
			Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
			method.invoke(wifimanager, wificonfiguration, !isApOn(context));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
