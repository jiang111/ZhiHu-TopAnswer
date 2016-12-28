package com.jiang.android.zhihu_topanswer.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiang.android.architecture.adapter.BaseAdapter;
import com.jiang.android.architecture.adapter.BaseViewHolder;
import com.jiang.android.architecture.view.MultiStateView;
import com.jiang.android.zhihu_topanswer.R;
import com.jiang.android.zhihu_topanswer.activity.AnswerDetailActivity;
import com.jiang.android.zhihu_topanswer.activity.AnswersActivity;
import com.jiang.android.zhihu_topanswer.db.Collection;
import com.jiang.android.zhihu_topanswer.utils.CollectionUtils;

/**
 * Created by jiang on 2016/12/28.
 */

public class CollectionFragment extends BaseFragment {

    private SwipeRefreshLayout mRefresh;
    private MultiStateView mStateView;
    private RecyclerView mRecyclerView;

    public static CollectionFragment newInstance() {

        CollectionFragment fragment = new CollectionFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_rv);
        mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.fragment_refresh);
        mStateView = (MultiStateView) view.findViewById(R.id.fragment_state);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }


    private void initView() {
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                initData();
            }
        });
    }

    private void initData( ) {
        mStateView.setViewState(MultiStateView.ViewState.CONTENT);
        mRefresh.setRefreshing(false);
       initRecyclerView();

    }

    private void initRecyclerView() {
        if (mRecyclerView.getAdapter() == null) {

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(new BaseAdapter() {
                @Override
                public void onBindView(BaseViewHolder holder, int position) {

                    if(position == 0){
                        TextView tv = holder.getView(R.id.collection_clear_all);
                        tv.setClickable(true);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clearAll();
                            }
                        });
                        return;
                    }
                    position = position-1;
                    Collection answers = CollectionUtils.getInstance().getCollection().get(position);
                    holder.setText(R.id.item_fr_top_title, answers.getTitle())
                    .setText(R.id.item_fr_top_body, Html.fromHtml(answers.getDetail()).toString())
                    .setVisibility(R.id.item_fr_top_body, TextUtils.isEmpty(answers.getDetail())?BaseViewHolder.GONE:BaseViewHolder.VISIBLE);

                }

                @Override
                public int getLayoutID(int position) {
                    if(position == 0)
                        return R.layout.item_top_collection_fragment;
                    return R.layout.item_collection_fragment;
                }

                @Override
                public boolean clickable() {
                    return true;
                }

                @Override
                public void onItemClick(View v, int position) {
                    super.onItemClick(v, position);
                    if(position == 0)
                        return;
                    position = position-1;
                    if(CollectionUtils.getInstance().getCollection().get(position).getType() == CollectionUtils.TYPE_ANSWERS){
                        Intent intent = new Intent(getActivity(), AnswersActivity.class);
                        intent.putExtra(AnswersActivity.QUESTION_URL, CollectionUtils.getInstance().getCollection().get(position).getUrl());
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getActivity(), AnswerDetailActivity.class);
                        intent.putExtra(AnswerDetailActivity.URL, CollectionUtils.getInstance().getCollection().get(position).getUrl());
                        intent.putExtra(AnswerDetailActivity.TITLE, CollectionUtils.getInstance().getCollection().get(position).getTitle());
                        intent.putExtra(AnswerDetailActivity.DETAIL, CollectionUtils.getInstance().getCollection().get(position).getDetail());

                        startActivity(intent);
                    }



                }

                @Override
                public int getItemCount() {
                    return CollectionUtils.getInstance().getCollection().size()+1;
                }
            });
        } else {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void clearAll() {
        new AlertDialog.Builder(getActivity())
                .setTitle("确定全部删除?")
                .setMessage("删除后将无法恢复")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CollectionUtils.getInstance().clear();
                        dialog.dismiss();
                        initData();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        }).show();


    }


}
