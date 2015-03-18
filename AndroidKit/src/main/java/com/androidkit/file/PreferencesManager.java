/**
 * Copyright (c) 2013, BigBeard Team, Inc. All rights reserved. 
 */
package com.androidkit.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import java.util.Map;

public class PreferencesManager {
	private static final String PREFERENCES_FILE_NAME = "preferences.cfg";
	private static SharedPreferences sSharedPreferences;
	
	/**
	 * Make the constructor to private
	 */
	private PreferencesManager() {
	}
	
	/**
	 * Initial the PreferencesManager
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		if(context != null)
			sSharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
	}
	
	/**
     * Retrieve all values from the preferences.
     *
     * @return Returns a map containing a list of pairs key/value representing
     * the preferences.
     *
     * @throws NullPointerException
     */
	public static Map<String, ?> getAll() {
		if (sSharedPreferences == null)
			return null;
		return sSharedPreferences.getAll();
	}
	
	
	/**
     * Checks whether the preferences contains a preference.
     * 
     * @param key The name of the preference to check.
     * @return Returns true if the preference exists in the preferences,
     *         otherwise false.
     */
	public static boolean contains(String key) {
		if (sSharedPreferences == null)
			return false;
		return sSharedPreferences.contains(key);
	}

	/**
     * Retrieve a String value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a String.
     * 
     * @throws ClassCastException
     */
	public static String getString(String key, String defValue) {
		if (sSharedPreferences == null)
			return null;
		return sSharedPreferences.getString(key, defValue);
	}

	/**
     * Retrieve an int value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * 
     * @throws ClassCastException
     */
	public static int getInt(String key, int defValue) {
		if (sSharedPreferences == null)
			return 0;
		return sSharedPreferences.getInt(key, defValue);
	}

	 /**
     * Retrieve a long value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a long.
     * 
     * @throws ClassCastException
     */
	public static long getLong(String key, long defValue) {
		if (sSharedPreferences == null)
			return 0;
		return sSharedPreferences.getLong(key, defValue);
	}

	/**
     * Retrieve a float value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a float.
     * 
     * @throws ClassCastException
     */
	public static float getFloat(String key, float defValue) {
		if (sSharedPreferences == null)
			return 0;
		return sSharedPreferences.getFloat(key, defValue);
	}

	/**
     * Retrieve a boolean value from the preferences.
     * 
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * 
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a boolean.
     * 
     * @throws ClassCastException
     */
	public static boolean getBoolean(String key, boolean defValue) {
		if (sSharedPreferences == null)
			return false;
		return sSharedPreferences.getBoolean(key, defValue);
	}

	/**
	 *
     * Set a boolean value in the preferences editor and commit.
     * 
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
	 * @return Returns true if the new values were successfully written
     * to persistent storage.
	 */
	public static boolean put(String key, boolean value) {
		return sSharedPreferences.edit().putBoolean(key, value).commit();
	}
	
	/**
	 *
    * Set a boolean value in the preferences editor and commit.
    * 
    * @param key The name of the preference to modify.
    * @param value The new value for the preference.
	 * @return Returns true if the new values were successfully written
    * to persistent storage.
	 */
	public static boolean put(String key, float value) {
		return sSharedPreferences.edit().putFloat(key, value).commit();
	}
	
	/**
	 *
    * Set a boolean value in the preferences editor and commit.
    * 
    * @param key The name of the preference to modify.
    * @param value The new value for the preference.
	 * @return Returns true if the new values were successfully written
    * to persistent storage.
	 */
	public static boolean put(String key, int value) {
		return sSharedPreferences.edit().putInt(key, value).commit();
	}
	
	/**
	 *
    * Set a boolean value in the preferences editor and commit.
    * 
    * @param key The name of the preference to modify.
    * @param value The new value for the preference.
	 * @return Returns true if the new values were successfully written
    * to persistent storage.
	 */
	public static boolean put(String key, long value) {
		return sSharedPreferences.edit().putLong(key, value).commit();
	}

	/**
	 *
    * Set a boolean value in the preferences editor and commit.
    * 
    * @param key The name of the preference to modify.
    * @param value The new value for the preference.
	 * @return Returns true if the new values were successfully written
    * to persistent storage.
	 */
	public static boolean put(String key, String value) {
		return sSharedPreferences.edit().putString(key, value).commit();
	}

	/**
     * Registers a callback to be invoked when a change happens to a preference.
     * 
     * @param listener The callback that will run.
     * @see #unregisterOnSharedPreferenceChangeListener
     */
	public static void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		sSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	/**
     * Unregisters a previous callback.
     * 
     * @param listener The callback that should be unregistered.
     * @see #registerOnSharedPreferenceChangeListener
     */
	public static void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		sSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);		
	}
}
