package com.fenghuo.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.fenghuo.LineEditText;
import com.fenghuo.db.DBNoteHelper;
import com.fenghuo.bean.Note;

import java.util.Timer;
import java.util.TimerTask;

public class EditNoteActivity extends Activity implements View.OnClickListener {

    private Button btn_back;
    private Button btn_save;
    private Button btn_alarm;
    private Button btn_delete;
    private Button btn_edit;
    private TextView tv_date;
    private ViewGroup mScrollView;
    private LineEditText et_content;
    private DBNoteHelper noteHelper;// 数据库
    private Note currentNote;
    private CustomToast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_thing);

        findviews();

        noteHelper = new DBNoteHelper(EditNoteActivity.this);
        toast = new CustomToast(EditNoteActivity.this);

        int noteid = getIntent().getIntExtra("noteid", -1);
        if (noteid != -1) {
            currentNote = noteHelper.GetSingle(noteid);
        }

        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_alarm.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_save.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 编辑、添加逻辑处理
        currentNote = noteHelper.GetSingle(currentNote.getId());
        et_content.setText(currentNote.getContent());
        et_content.setInputType(InputType.TYPE_NULL);
        et_content.setSingleLine(false);
        et_content.setLineDown(et_content.getPaddingTop() - 2);
        tv_date.setText(currentNote.getDate());
        setbackgroud();
    }

    private void findviews() {
        btn_back = (Button) findViewById(R.id.btn_back_editthi);
        btn_save = (Button) findViewById(R.id.btn_save_editthi);
        et_content = (LineEditText) findViewById(R.id.et_text_editthi);
        btn_alarm = (Button) findViewById(R.id.btn_alarm_editthi);
        btn_delete = (Button) findViewById(R.id.btn_delete_editthi);
        btn_edit = (Button) findViewById(R.id.btn_edit_editthi);
        tv_date = (TextView) findViewById(R.id.tv_date_editthi);
        mScrollView = (ViewGroup) findViewById(R.id.scroll_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteHelper.Desdroy();
    }

    private void showKeyboard() {
        // 自动弹出键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                InputMethodManager manager = (InputMethodManager) et_content
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                manager.showSoftInput(et_content, 0);
            }
        }, 500);
    }

    /**
     * 设置背景
     */
    private void setbackgroud() {

        switch (currentNote.getImg()) {
            case R.drawable.page_blue:
                et_content.setBackgroundResource(R.drawable.page_bg_blue);
                break;
            case R.drawable.page_green:
                et_content.setBackgroundResource(R.drawable.page_bg_green);
                break;
            case R.drawable.page_pink:
                et_content.setBackgroundResource(R.drawable.page_bg_pink);
                break;
            case R.drawable.page_white:
                et_content.setBackgroundResource(R.color.white);
                break;
            case R.drawable.page_yellow:
                et_content.setBackgroundResource(R.drawable.page_bg_yellow);
                break;
            default:
                et_content.setBackgroundResource(R.drawable.page_bg_blue);
                break;
        }
    }

    // 返回 /保存 按钮事件
    @Override
    public void onClick(View view) {

        String content = et_content.getText().toString().trim();
        switch (view.getId()) {
            case R.id.btn_alarm_editthi:
                Intent intent_alarm = new Intent(EditNoteActivity.this, AlarmActivity.class);
                intent_alarm.putExtra("noteid", currentNote.getId());
                startActivity(intent_alarm);
                break;

            case R.id.btn_delete_editthi:
                new AlertDialog.Builder(EditNoteActivity.this)
                        .setTitle("确认删除?")
                        .setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        noteHelper.Delete(currentNote.getId());
                                        toast.ShowMsg("删除成功!", CustomToast.Img_Ok);
                                        finish();
                                    }
                                }).setNegativeButton("取消", null).show();

                break;
            case R.id.btn_edit_editthi:
                et_content.setEnabled(true);
                et_content.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                et_content.setSingleLine(false);
                btn_save.setVisibility(View.VISIBLE);
                showKeyboard();
                break;
            case R.id.btn_back_editthi:
                finish();
                break;
            case R.id.btn_save_editthi:

                currentNote.setContent(content);
                noteHelper.Update(currentNote);
                noteHelper.Desdroy();
                finish();
                break;
            default:
                break;
        }
    }

}
