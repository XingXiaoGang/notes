package com.fenghuo.notes;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

/**
 * @author Administrator 添加事件接口的FragMent
 */
public class FragmentExt extends Fragment implements KeyEvent.Callback {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }
}
