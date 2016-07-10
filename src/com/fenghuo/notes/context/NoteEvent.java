package com.fenghuo.notes.context;

/**
 * Created by gang on 16-7-10.
 */
public class NoteEvent {

    public static final int TPYE_MENU_CLICK = 0;
    public static final int TPYE_UPDATE_MENU_STATE = 1;

    public int what;
    public int argInt1;
    public int argInt2;
    public boolean argBoolean;
    public Object object;

    public NoteEvent(int what) {
        this.what = what;
    }
}