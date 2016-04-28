package com.fenghuo.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.fenghuo.adapter.NoteAdapter;
import com.fenghuo.bean.Note;
import com.fenghuo.db.DBNoteHelper;

import java.util.List;

public class ThingsFragment extends FragmentExt implements OnClickListener,
        OnItemClickListener, OnItemLongClickListener {

    private Button btn_add;// 新记事
    private Button btn_complete;// 结束编辑
    private GridView gv_list;// 列表
    private DBNoteHelper noteHelper;// 数据库
    private List<Note> list;// 数据集合
    private NoteAdapter adapter;// 适配器
    private PopupWindow pop_button;
    private View mPopBtnView;// 弹出按钮
    private Note note;// 当前的note
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_things, null, false);
        findviews(view);
        noteHelper = new DBNoteHelper(getActivity());

        list = noteHelper.Getlist();
        adapter = new NoteAdapter(getActivity(), list);
        gv_list.setAdapter(adapter);

        gv_list.setOnItemClickListener(this);
        gv_list.setOnItemLongClickListener(this);
        btn_add.setOnClickListener(this);

        return view;
    }

    private void findviews(View view) {
        btn_add = (Button) view.findViewById(R.id.btn_add_things);
        gv_list = (GridView) view.findViewById(R.id.gv_list);
    }

    @Override
    public void onDestroy() {

        noteHelper.Desdroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        list.clear();
        list = noteHelper.Getlist();
        adapter.Update(list);
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (adapter.isEdit()) {
            adapter.quitEdit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            // 跳转界面
            case R.id.btn_add_things:
                Intent intent = new Intent(getActivity(), Ac_addThing.class);
                startActivity(intent);
                break;
            // 结束
            case R.id.btn_compelete:
                btn_complete.setVisibility(View.GONE);
                adapter.quitEdit();
                btn_add.setVisibility(View.VISIBLE);
                pop_button.dismiss();
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
        adapter.startEdit();
        btn_add.setVisibility(View.INVISIBLE);
        showPopButton();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        note = list.get(position);
        Intent intent_edit = new Intent(getActivity(), Ac_EditThing.class);
        intent_edit.putExtra("noteid", note.getId());
        startActivity(intent_edit);
    }

    private void showPopButton() {

        mPopBtnView = getActivity().getLayoutInflater().inflate(
                R.layout.pop_button, null);
        btn_complete = (Button) mPopBtnView.findViewById(R.id.btn_compelete);
        btn_complete.setOnClickListener(this);
        pop_button = new PopupWindow(mPopBtnView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        pop_button.showAtLocation(getActivity().findViewById(R.id.vp_content),
                Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        pop_button.update();
    }

}
