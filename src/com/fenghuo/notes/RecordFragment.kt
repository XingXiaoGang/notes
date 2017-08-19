package com.fenghuo.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by xingxiaogang on 2017/8/19.
 */
class RecordFragment : FragmentExt() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = View.inflate(context, R.layout.fragment_record, null);
        
        return rootView;
    }

}