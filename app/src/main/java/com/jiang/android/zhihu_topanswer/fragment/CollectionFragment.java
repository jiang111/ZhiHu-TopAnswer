package com.jiang.android.zhihu_topanswer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiang.android.zhihu_topanswer.R;

/**
 * Created by jiang on 2016/12/28.
 */

public class CollectionFragment extends BaseFragment {


    public static CollectionFragment newInstance() {

        CollectionFragment fragment = new CollectionFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        return view;
    }

}
