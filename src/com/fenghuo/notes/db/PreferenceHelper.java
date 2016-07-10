package com.fenghuo.notes.db;

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
	public String getLast() {
		String str = "";
		str = preferences.getString("last", "");
		return str;
	}

	/**保存草稿
	 * @param str
	 */
	public void saveLast(String str) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("last", str);
		editor.commit();
	}
}
