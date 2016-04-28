package com.fenghuo.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fenghuo.LineEditText;
import com.fenghuo.adapter.KindAdapter;
import com.fenghuo.db.DBAccountHelper;
import com.fenghuo.db.DbKindHelper;
import com.fenghuo.bean.Account;
import com.fenghuo.bean.Kind;

import java.util.List;

public class AddAccountActivity extends Activity implements OnClickListener {

	private Button btn_back;
	private Button btn_save;
	private LineEditText et_money;
	private TextView tv_kinds;
	private RelativeLayout ln_kinds;
	private TextView tv_time;
	private TextView tv_date;
	private RadioGroup group;
	private DBAccountHelper accountHelper;
	private DbKindHelper kindHelper;
	private AlertDialog dialog_date;
	private AlertDialog dialog_time;
	private AlertDialog dialog_kinds;
	private AlertDialog dialog_addkinds;
	private List<Kind> kinds;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private CustomToast toast;
	private ListView listView;// 类型列表
	private KindAdapter adapter;// 类型适配器

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_acc);

		findviews();
		kindHelper = new DbKindHelper(AddAccountActivity.this);
		toast = new CustomToast(AddAccountActivity.this);

		tv_date.setText(kindHelper.GetDateTime().substring(0, 10));
		tv_time.setText(kindHelper.GetDateTime().substring(10));
		btn_back.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		tv_kinds.setOnClickListener(this);
		ln_kinds.setOnClickListener(this);
		tv_time.setOnClickListener(this);
		tv_date.setOnClickListener(this);
	}

	private void findviews() {
		btn_back = (Button) findViewById(R.id.btn_back_accadd);
		btn_save = (Button) findViewById(R.id.btn_save_addacc);
		et_money = (LineEditText) findViewById(R.id.et_money);
		tv_kinds = (TextView) findViewById(R.id.tv_kinds_addacc);
		ln_kinds = (RelativeLayout) findViewById(R.id.ln_kinds_addacc);
		tv_time = (TextView) findViewById(R.id.tv_picktime_acc);
		tv_date = (TextView) findViewById(R.id.tv_pickdate_acc);
		group = (RadioGroup) findViewById(R.id.rg_kind);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.btn_back_accadd:
				finish();
				break;
			case R.id.btn_save_addacc:
				float money = Float.valueOf(et_money.getText().toString());
				int kind = group.getCheckedRadioButtonId() == R.id.rab_outcome ? 1
						: 2;
				String kinds = tv_kinds.getText().toString();
				String time = tv_date.getText().toString() + " "
						+ tv_time.getText().toString();
				if (money != 0 && !kinds.equals("点击选择 >")) {
					accountHelper = new DBAccountHelper(AddAccountActivity.this);
					Account account = new Account(-1, kind, kinds, money, time);
					accountHelper.insert(account);
					accountHelper.Destroy();
					toast.ShowMsg("成功！", CustomToast.Img_Ok);
					finish();
				} else {
					toast.ShowMsg("请将金额/分类填写正确！！", CustomToast.Img_Erro);
				}
				break;
			case R.id.tv_kinds_addacc:
			case R.id.ln_kinds_addacc:
				showselectKinds();
				break;
			case R.id.tv_picktime_acc:
				showselecttime();
				break;
			case R.id.tv_pickdate_acc:
				showselectDate();
				break;
			default:
				break;
		}
	}

	/**
	 * 显示选择日期对话框
	 */
	private void showselectDate() {
		if (dialog_date == null) {
			View view = getLayoutInflater().inflate(R.layout.dialog_selectdate,
					null);
			datePicker = (DatePicker) view.findViewById(R.id.datepicker);
			dialog_date = new AlertDialog.Builder(AddAccountActivity.this)
					.setView(view)
					.setTitle("请选择日期")
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
													int arg1) {
									String year = datePicker.getYear() + "";
									String moth = datePicker.getMonth() + 1
											+ "";
									String day = datePicker.getDayOfMonth()
											+ "";
									tv_date.setText(year
											+ "-"
											+ (moth.length() == 1 ? "0" + moth
											: moth)
											+ "-"
											+ (day.length() == 1 ? "0" + day
											: day));
								}
							}).show();
		} else {
			dialog_date.show();
		}
	}

	/**
	 * 显示选择时间对话框
	 */
	private void showselecttime() {
		if (dialog_time == null) {
			View view = getLayoutInflater().inflate(R.layout.dialog_selecttime,
					null);
			timePicker = (TimePicker) view.findViewById(R.id.tiempicker);
			dialog_time = new AlertDialog.Builder(AddAccountActivity.this)
					.setView(view)
					.setTitle("请选择时间")
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
													int arg1) {
									String hour = timePicker.getCurrentHour()
											+ "";
									String minite = timePicker
											.getCurrentMinute() + "";
									hour = hour.length() == 1 ? "0" + hour
											: hour;
									minite = minite.length() == 1 ? "0"
											+ minite : minite;
									tv_time.setText(hour + ":" + minite);
								}
							}).show();
		} else {
			dialog_time.show();
		}
	}

	/**
	 * 显示类型
	 */
	private void showselectKinds() {
		dialog_kinds=null;
		listView=null;
		View view = getLayoutInflater().inflate(R.layout.kind_list, null);
		listView = (ListView) view.findViewById(R.id.lv_kinds);
		kinds=kindHelper.Getlist();
		adapter = new KindAdapter(AddAccountActivity.this, kinds);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				tv_kinds.setText(kinds.get(position).getKind());
				handler.sendEmptyMessage(1);
			}
		});
		dialog_kinds = new AlertDialog.Builder(AddAccountActivity.this)
				.setTitle("请选择类型").setView(view)
				.setPositiveButton("添加", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						showAddKinds();
					}
				}).show();

	}

	/**
	 * 显示添加类型对话框
	 */
	private void showAddKinds() {
		if (dialog_addkinds == null) {
			View view = getLayoutInflater().inflate(R.layout.dialog_addkinds,
					null);

			final EditText editText = (EditText) view
					.findViewById(R.id.et_addkinds);
			dialog_addkinds = new AlertDialog.Builder(AddAccountActivity.this)
					.setTitle("请输入名称")
					.setView(view)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
													int arg1) {
									String text = editText.getText() + "";
									if (text.length() > 0) {
										kindHelper.Add(text);
										tv_kinds.setText(text);
										dialog_kinds = null;
									} else {
										toast.ShowMsg("未添加",
												CustomToast.Img_Erro);
									}
								}
							}).show();
		} else {
			dialog_addkinds.show();
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case 1:
					dialog_kinds.dismiss();
					break;

				default:
					break;
			}

			super.handleMessage(msg);
		}

	};

	@Override
	protected void onDestroy() {

		kindHelper.destroy();
		if (accountHelper != null)
			accountHelper.Destroy();
		super.onDestroy();
	}

}
