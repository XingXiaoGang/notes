package com.fenghuo.notes.db;

import android.content.Context;
import android.os.Environment;

import com.fenghuo.notes.bean.Alarm;
import com.fenghuo.notes.bean.Kind;
import com.fenghuo.notes.bean.MonthAccount;
import com.fenghuo.notes.bean.Note;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

public class BackupRestoreUtils {

	private Context context;
	private DBAlarmHelper alarmHelper;
	private DBAccountHelper accountHelper;
	private DbKindHelper kindHelper;
	private DBNoteHelper noteHelper;

	private List<Alarm> listalarm;
	private List<Kind> listKind;
	private List<Note> listnote;
	private List<MonthAccount> listaccount;
	// 数据库在手机内存中的目录
	private String DB_phone_path = "/data"
			+ Environment.getDataDirectory().getAbsolutePath()
			+ "/com.fenghuo.notes/databases";
	// 外部存储的备份目录
	private String DB_sd_path = Environment.getExternalStorageDirectory()
			.getPath() + "/Android/backups/com.fenghuo.notes";
	private String DB_name = "data.db";

	public BackupRestoreUtils(Context context) {
		this.context = context;
	}

	// 第一种思路 将所有数据转换成json字符串 存储到内存中

	public boolean backup() {
		alarmHelper = new DBAlarmHelper(context);
		kindHelper = new DbKindHelper(context);
		noteHelper = new DBNoteHelper(context);
		accountHelper = new DBAccountHelper(context);
		listalarm = alarmHelper.Getall();
		listKind = kindHelper.Getlist();
		listnote = noteHelper.Getlist();
		listaccount = accountHelper.GetListAll();
		// 转换json
		String Jsonalarms = "";
		if (listalarm != null && listalarm.size() > 0) {
			HashMap<String, String> map;
			for (Alarm alarm : listalarm) {
				map = new HashMap<String, String>();
				map.put("id", alarm.getId() + "");
				map.put("date", alarm.getId() + "");
				map.put("time", alarm.getId() + "");
				map.put("repeat", alarm.getId() + "");
				map.put("week", alarm.getId() + "");
				map.put("vibration", alarm.getId() + "");
				map.put("ringuri", alarm.getId() + "");
				map.put("content", alarm.getId() + "");
				JSONObject object = new JSONObject(map);
				Jsonalarms = Jsonalarms + object.toString() + "\n";
			}
		}

		return false;
	}

	public boolean restore() {

		return false;
	}

	public boolean checkexist() {

		File dri = new File(DB_sd_path);
		if (!dri.exists()) {
			dri.mkdirs();
			return false;
		}
		File file = new File(DB_sd_path + "/" + DB_name);
		if (file.exists()) {
			// 如果存在 ，读取数据信息
			// SQLiteDatabase.CursorFactory cursorFactory = new
			// SQLiteDatabase.CursorFactory() {
			// @Override
			// public Cursor newCursor(SQLiteDatabase db,
			// SQLiteCursorDriver masterQuery, String editTable,
			// SQLiteQuery query) {
			// return null;
			// }
			// };
			// SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_sd_path,
			// cursorFactory, 0);

			return true;
		}
		return false;
	}

	// 第二种思路 直接将数据库导出来 -1 失败 0 未找到数据 1 成功

	public int restore2() {
		String pathto = DB_phone_path + "/" + DB_name;
		String pathfrom = DB_sd_path + "/" + DB_name;
		try {
			File to = new File(pathto);
			File from = new File(pathfrom);
			// 先决断sd卡文件夹是否存在
			File pderectroy = new File(DB_sd_path);
			if (!pderectroy.exists()) {
				pderectroy.mkdirs();
				return 0;
			}
			// sd卡数据库是否存在
			if (!from.exists()) {
				return 0;
			}
			FileInputStream streami = new FileInputStream(from);
			FileOutputStream streamo = new FileOutputStream(to);
			byte[] bs = new byte[512];
			int count = 0;
			while ((count = streami.read(bs)) > 0) {
				streamo.write(bs, 0, count);
			}
			streami.close();
			streamo.close();
			System.out.println("恢复成功===========");
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public boolean backup2() {
		String pathto = DB_phone_path + "/" + DB_name;
		String pathfrom = DB_sd_path + "/" + DB_name;
		try {
			File from = new File(pathfrom);
			File to = new File(pathto);
			FileInputStream streami = new FileInputStream(to);
			FileOutputStream streamo = new FileOutputStream(from, false);
			byte[] bs = new byte[512];
			int count = 0;
			while ((count = streami.read(bs)) > 0) {
				streamo.write(bs, 0, count);
			}
			streami.close();
			streamo.close();
			System.out.println("备份成功！------------");
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
