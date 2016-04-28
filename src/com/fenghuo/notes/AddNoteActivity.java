package com.fenghuo.notes;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.fenghuo.LineEditText;
import com.fenghuo.notes.bean.Note;
import com.fenghuo.notes.db.DBNoteHelper;
import com.fenghuo.notes.db.PreferenceHelper;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AddNoteActivity extends Activity implements View.OnClickListener {

    private Button btn_back;
    private Button btn_save;
    private ViewGroup mScrollView;
    private LineEditText et_content;
    private DBNoteHelper noteHelper;// 数据库
    private PreferenceHelper helper;// 用于存储草稿
    private Random random;// 用于分配随机图片
    private boolean isSaved = false;
    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thing);

        findviews();

        random = new Random();
        helper = new PreferenceHelper(AddNoteActivity.this);
        noteHelper = new DBNoteHelper(AddNoteActivity.this);

        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        // 上次没有保存的草稿
        et_content.setText(helper.getlast());
        et_content.setLineDown(et_content.getPaddingTop() - 2);
        // 自动弹出键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                InputMethodManager manager = (InputMethodManager) et_content
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                manager.showSoftInput(et_content, 0);
            }
        }, 500);

    }

    private void findviews() {
        btn_back = (Button) findViewById(R.id.btn_back_addthi);
        btn_save = (Button) findViewById(R.id.btn_save_addthi);
        mScrollView = (ViewGroup) findViewById(R.id.scroll_view);
        et_content = (LineEditText) findViewById(R.id.et_text_addthi);
    }

    @Override
    protected void onDestroy() {

        if (!isSaved)
            helper.savelase(et_content.getText() + "");
        super.onDestroy();
    }

    // 返回 /保存 按钮事件
    @Override
    public void onClick(View arg0) {

        String content = et_content.getText().toString().trim();

        switch (arg0.getId()) {
            case R.id.btn_back_addthi:
                if (content.length() > 0)
                    helper.savelase(content);
                finish();
                break;
            case R.id.btn_save_addthi:
                currentNote = new Note(-1, random.nextInt(5), content, noteHelper.GetDate(), 0);
                noteHelper.Add(currentNote);
                helper.savelase("");// 如果是插入一条新记事 则需要清空last
                noteHelper.Desdroy();
                isSaved = true;
                finish();
                break;
            default:
                break;
        }
    }

}
