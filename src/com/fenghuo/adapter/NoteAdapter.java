package com.fenghuo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.fenghuo.LineTextView;
import com.fenghuo.db.DBNoteHelper;
import com.fenghuo.bean.Note;
import com.fenghuo.notes.R;

import java.util.List;

public class NoteAdapter extends BaseAdapter {

	private List<Note> data;
	private boolean isEdite;// 是否在编辑
	private LayoutInflater inflater;
	private Context context;

	public NoteAdapter(Context context, List<Note> list) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = list;
	}

	public void Update(List<Note> list) {
		data.clear();
		data = list;
		notifyDataSetChanged();
	}

	/**
	 * 进入编辑状态
	 */
	public void startEdit() {
		isEdite = true;
		notifyDataSetChanged();
	}

	/**
	 * 退出编辑状态
	 */
	public void quitEdit() {
		isEdite = false;
		notifyDataSetChanged();
	}

	public boolean isEdit() {
		return isEdite;
	}

	@Override
	public int getCount() {

		return data.size();
	}

	@Override
	public Object getItem(int arg0) {

		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {

		ViewHolder h = null;
		if (view != null) {
			h = (ViewHolder) view.getTag();
		} else {
			h = new ViewHolder();
			view = inflater.inflate(R.layout.item_note, null);
			h.item_img = (ImageView) view.findViewById(R.id.item_img);
			h.item_tv_content = (LineTextView) view
					.findViewById(R.id.item_tv_content);
			h.item_btn_delete = (Button) view.findViewById(R.id.item_btndelete);
			h.item_alarm = (ImageView) view.findViewById(R.id.item_alarm);
			view.setTag(h);
		}
		h.item_img.setBackgroundResource(data.get(position).getImg());
		h.item_tv_content.setText(data.get(position).getContent());
		h.item_btn_delete.setOnClickListener(new onclicklistener());
		h.item_btn_delete.setTag(position);
		// 此方法对横线的位置进行微调(原始:getPaddingtop+lineHeigt+x). 这里setLineDown(X)
		h.item_tv_content.setLineDown(-h.item_tv_content.getPaddingTop() - 1);
		if (data.get(position).getAlarm() == 1)
			h.item_alarm.setVisibility(View.VISIBLE);
		else
			h.item_alarm.setVisibility(View.INVISIBLE);
		if (isEdite)
			h.item_btn_delete.setVisibility(View.VISIBLE);
		else
			h.item_btn_delete.setVisibility(View.GONE);
		return view;
	}

	class onclicklistener implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {

			int position = Integer.valueOf(arg0.getTag().toString());
			// 从列表中移除
			DBNoteHelper dbNoteHelper = new DBNoteHelper(context);
			dbNoteHelper.Delete(data.get(position).getId());
			dbNoteHelper.Desdroy();
			// 从集合中移除
			data.remove(position);
			notifyDataSetChanged();
		}

	}

	private class ViewHolder {
		public ImageView item_img;
		// public LineEditText item_tv_content;
		public LineTextView item_tv_content;
		public Button item_btn_delete;
		public ImageView item_alarm;
	}
}
