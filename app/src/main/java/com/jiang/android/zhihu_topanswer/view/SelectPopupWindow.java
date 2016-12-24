package com.jiang.android.zhihu_topanswer.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.jiang.android.architecture.adapter.BaseAdapter;
import com.jiang.android.zhihu_topanswer.R;

/**
 * Created by jiang on 2016/12/24.
 */

public class SelectPopupWindow extends PopupWindow {

    private final View mMenuView;
    private final RecyclerView mRecyclerView;
    private final RecyclerView mRecyclerViewDelete;

    public SelectPopupWindow(Activity context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_layout, null);
        mRecyclerView = (RecyclerView) mMenuView.findViewById(R.id.pop_layout1);
        mRecyclerViewDelete = (RecyclerView) mMenuView.findViewById(R.id.pop_layout2);

        this.setContentView(mMenuView);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);

    }

    public void setAdapter(BaseAdapter adapter) {
        GridLayoutManager layoutManager = new GridLayoutManager(mRecyclerView.getContext(), 4);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

    }

    public void setDeleteAdapter(BaseAdapter adapter) {
        GridLayoutManager layoutManager = new GridLayoutManager(mRecyclerView.getContext(), 4);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerViewDelete.setLayoutManager(layoutManager);
        mRecyclerViewDelete.setAdapter(adapter);
    }



}
