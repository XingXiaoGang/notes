package com.fenghuo.notes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fenghuo.notes.db.DbKindHelper;
import com.fenghuo.notes.bean.Kind;
import com.fenghuo.notes.R;

import java.util.List;

public class KindAdapter extends BaseAdapter implements OnClickListener{

	private List<Kind> data;
	private LayoutInflater inflater;
	private Context context;
	private DbKindHelper kindHelper;

	public KindAdapter(Context context, List<Kind> list) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.data = list;
	}

	@Override
	public int getCount() {

		return data.size();
	}

	@Override
	public Object getItem(int position) {

		return data.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {

		ViewHolder h = null;
		if (view != null) {
			h = (ViewHolder) view.getTag();
		} else {
			h = new ViewHolder();
			view = inflater.inflate(R.layout.item_kind, null);
			h.item_text = (TextView) view.findViewById(R.id.kind_item_tv);
			h.item_btn_delete = (Button) view.findViewById(R.id.kind_item_delete);
			h.item_btn_delete.setTag(position);
			h.item_btn_delete.setOnClickListener(this);
			view.setTag(h);
		}
		h.item_text.setText(data.get(position).getKind());
		return view;
	}

	private class ViewHolder {
		public TextView item_text;
		public Button item_btn_delete;
	}

	@Override
	public void onClick(View view) {
		int position = Integer.valueOf(view.getTag().toString());
		// 从列表中移除
		kindHelper=new DbKindHelper(context);
		kindHelper.Delete(data.get(position).getId());
		// 从集合中移除
		data.remove(position);
		notifyDataSetChanged();
	}
}
