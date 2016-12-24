package com.jiang.android.architecture.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by jiang on 15/9/16.
 */
public class LoadMoreRecyclerView extends RecyclerView {

    /**
     * 监听到底部的接口
     */
    private OnLoadMoreListener onLoadMoreListener;

    /**
     * 正在加载更多
     */
    public static final int STATE_START_LOADMORE = 100;

    /**
     * 加载完成
     */
    public static final int STATE_FINISH_LOADMORE = 90;
    /**
     * 加载更多的状态开关
     */
    public boolean canLOADMORE = true;

    /**
     * 加载更多时候的状态
     */
    private int loadmore_state;


    /**
     * layoutManager的类型（枚举）
     */
    protected LAYOUT_MANAGER_TYPE layoutManagerType;

    /**
     * 最后一个的位置
     */
    private int[] lastPositions;

    /**
     * 最后一个可见的item的位置
     */
    private int lastVisibleItemPosition;


    /**
     * 当前滑动的状态
     */
    private int currentScrollState = 0;

    public static enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }


    public LoadMoreRecyclerView(Context context) {
        super(context);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        super.setOnScrollListener(listener);

    }

    public boolean isCanLOADMORE() {
        return canLOADMORE;
    }

    public void setCanLOADMORE(boolean canLOADMORE) {
        this.canLOADMORE = canLOADMORE;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        LayoutManager layoutManager = getLayoutManager(); //拿到layoutmanager用来判断类型，拿到最后一个可见的view
        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
            } else {
                throw new RuntimeException(
                        "不支持的LayoutManager ,目前只支持 LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }
        /**
         * 拿到最后一个可见的view
         */
        switch (layoutManagerType) {
            case LINEAR:
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GRID:
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                break;
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (!canLOADMORE)
            return;
        if (loadmore_state == STATE_START_LOADMORE)  //如果正在加载更多， 则直接return
            return;
        currentScrollState = state;
        LayoutManager layoutManager = getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
            loadmore_state = STATE_START_LOADMORE;
            onLoadMoreListener.onLoadMore();
        }
    }


    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoadmore_state(int loadmore_state) {
        this.loadmore_state = loadmore_state;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
