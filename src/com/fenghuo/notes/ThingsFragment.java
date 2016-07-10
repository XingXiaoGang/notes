package com.fenghuo.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
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

import com.fenghuo.notes.adapter.NoteAdapter;
import com.fenghuo.notes.bean.Note;
import com.fenghuo.notes.context.NoteEvent;
import com.fenghuo.notes.db.DBNoteHelper;
import com.mine.view.menu.icon.MaterialMenuDrawable;
import com.mine.view.menu.icon.MaterialMenuView;

import java.util.List;

import de.greenrobot.event.EventBus;

public class ThingsFragment extends FragmentExt implements OnClickListener,
        OnItemClickListener, OnItemLongClickListener {

    private Button btn_complete;// 结束编辑
    private GridView gv_list;// 列表
    private DBNoteHelper noteHelper;// 数据库
    private List<Note> list;// 数据集合
    private NoteAdapter adapter;// 适配器
    private PopupWindow pop_button;
    private View mPopBtnView;// 弹出
    // 按钮
    private Note note;// 当前的note
    private View view;
    private MaterialMenuView mMaterialMenuView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        view = inflater.inflate(R.layout.fragment_things, null, false);
        gv_list = (GridView) view.findViewById(R.id.gv_list);
        view.findViewById(R.id.material_add_button).setOnClickListener(this);
        mMaterialMenuView = (MaterialMenuView) view.findViewById(R.id.material_menu_button);
        mMaterialMenuView.setOnClickListener(this);
        noteHelper = new DBNoteHelper(getActivity());
        list = noteHelper.Getlist();
        adapter = new NoteAdapter(getActivity(), list);
        gv_list.setAdapter(adapter);

        gv_list.setOnItemClickListener(this);
        gv_list.setOnItemLongClickListener(this);

        return view;
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
            case R.id.material_add_button:
                Intent intent = new Intent(getActivity(), AddNoteActivity.class);
                startActivity(intent);
                break;
            // 结束
            case R.id.btn_compelete:
                btn_complete.setVisibility(View.GONE);
                adapter.quitEdit();
                pop_button.dismiss();
                break;
            case R.id.material_menu_button: {
                boolean isToOpen = mMaterialMenuView.getState() == MaterialMenuDrawable.IconState.BURGER;
                updateMenuState(isToOpen);
                NoteEvent noteEvent = new NoteEvent(NoteEvent.TPYE_MENU_CLICK);
                noteEvent.argBoolean = isToOpen;
                EventBus.getDefault().post(noteEvent);

                break;
            }
            default:
                break;
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
        adapter.startEdit();
        showPopButton();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        note = list.get(position);
        Intent intent_edit = new Intent(getActivity(), EditNoteActivity.class);
        intent_edit.putExtra("noteid", note.getId());
        startActivity(intent_edit);
    }

    /**
     * 与发布者在同一个线程
     *
     * @param msg 事件1
     */
    @Keep
    public void onEvent(NoteEvent msg) {
        if (msg != null) {
            switch (msg.what) {
                case NoteEvent.TPYE_UPDATE_MENU_STATE: {
                    boolean isOpen = msg.argBoolean;
                    updateMenuState(isOpen);
                    break;
                }
            }
        }
    }

    private void updateMenuState(boolean toOpen) {
        if (toOpen) {
            mMaterialMenuView.animateState(MaterialMenuDrawable.IconState.X);
        } else {
            mMaterialMenuView.animateState(MaterialMenuDrawable.IconState.BURGER);
        }
    }

    private void showPopButton() {

        mPopBtnView = getActivity().getLayoutInflater().inflate(
                R.layout.pop_button, null);
        btn_complete = (Button) mPopBtnView.findViewById(R.id.btn_compelete);
        btn_complete.setOnClickListener(this);
        pop_button = new PopupWindow(mPopBtnView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        pop_button.showAtLocation(getActivity().findViewById(R.id.vp_content), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        pop_button.update();
    }

}
