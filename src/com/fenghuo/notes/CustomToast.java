package com.fenghuo.notes;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

	private Context context;
	private Toast toast;
	private LayoutInflater inflater;
	private View view;
	private ImageView toast_img;
	private TextView toast_msg;

	public static final int Img_Ok = 1;
	public static final int Img_Erro = 3;
	public static final int Img_Info=2;

	public CustomToast(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void ShowMsg(String msg, int kind) {
		if (toast == null) {
			toast = new Toast(context);
			view = inflater.inflate(R.layout.toast, null);
			toast_img = (ImageView) view.findViewById(R.id.toast_img);
			toast_msg = (TextView) view.findViewById(R.id.toast_tv);
			toast.setView(view);
		}

		switch (kind) {
		case Img_Ok:
			toast_img.setImageResource(R.drawable.toast_ok);
			break;
		case Img_Erro:
			toast_img.setImageResource(R.drawable.toast_erro);
			break;
		case Img_Info:
			toast_img.setImageResource(R.drawable.toast_info);
			break;
		default:
			break;
		}

		toast_msg.setText(msg);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setView(view);
		toast.show();
	}
}
