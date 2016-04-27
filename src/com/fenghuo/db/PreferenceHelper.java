package com.fenghuo.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

	SharedPreferences preferences;

	public PreferenceHelper(Context context) {
		preferences = context.getSharedPreferences("temp",
				Activity.MODE_PRIVATE);
	}

	/**读取上一次的草稿
	 * @return
	 */
	public String getlast() {
		String str = "";
		str = preferences.getString("last", "");
		return str;
	}

	/**保存草稿
	 * @param str
	 */
	public void savelase(String str) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("last", str);
		editor.commit();
	}
}
