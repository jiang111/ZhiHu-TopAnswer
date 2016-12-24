package com.jiang.android.zhihu_topanswer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiang.android.architecture.adapter.BaseAdapter;
import com.jiang.android.architecture.adapter.BaseViewHolder;
import com.jiang.android.architecture.rxsupport.RxAppCompatActivity;
import com.jiang.android.architecture.view.MultiStateView;
import com.jiang.android.zhihu_topanswer.R;
import com.jiang.android.zhihu_topanswer.model.AnswersModel;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jiang on 2016/12/24.
 */

public class AnswersActivity extends RxAppCompatActivity {


    public static final String QUESTION_URL = "question_url";
    private String mQuestionUrl;
    private RecyclerView mRecyclerView;
    private List<AnswersModel> mLists = new ArrayList<>();
    private SwipeRefreshLayout mRefresh;
    private MultiStateView mStateView;
    private String title;
    private String detail;
    private TextView mTitle;
    private ImageView mBack;
    private LinearLayoutManager linearLayoutManager;
    private ImageView mWeb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);
        mTitle = (TextView) findViewById(R.id.answers_title);
        mBack = (ImageView) findViewById(R.id.answers_back);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_answers_rv);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.activity_answers_refresh);
        mStateView = (MultiStateView) findViewById(R.id.activity_answers_state);
        mWeb = (ImageView) findViewById(R.id.answers_right);
        mQuestionUrl = getIntent().getStringExtra(QUESTION_URL);
        initView();
    }


    private void initView() {
        setTitle(false);
        mBack.setClickable(true);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswersActivity.this.finish();
            }
        });
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                initData(false);
            }
        });

        mWeb.setClickable(true);
        mWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mQuestionUrl));
                startActivity(intent);
            }
        });
        initData(true);
    }

    private void setTitle(boolean show) {
        if (show) {
            mTitle.setText(title);
        } else {
            mTitle.setText("");
        }
    }

    public void initData(final boolean needState) {
        if (needState) {
            mStateView.setViewState(MultiStateView.ViewState.LOADING);
        }
        final String url = mQuestionUrl;
        Observable.create(new Observable.OnSubscribe<Document>() {
            @Override
            public void call(Subscriber<? super Document> subscriber) {

                try {
                    subscriber.onNext(Jsoup.connect(url + "#zh-question-collapsed-wrap").timeout(5000).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).map(new Func1<Document, List<AnswersModel>>() {
            @Override
            public List<AnswersModel> call(Document document) {

                List<AnswersModel> list = new ArrayList<>();


                Element titleLink = document.getElementById("zh-question-title");
                Element detailLink = document.getElementById("zh-question-detail");
                String title = titleLink.select("span.zm-editable-content").text();
                String detailHtml = detailLink.select("div.zm-editable-content").html();
                AnswersActivity.this.title = title;
                detail = detailHtml;


                Element bodyAnswer = document.getElementById("zh-question-answer-wrap");
                Elements bodys = bodyAnswer.select("div.zm-item-answer");
                Element bodyWrapAnswer = document.getElementById("zh-question-collapsed-wrap");
                bodys.addAll(bodyWrapAnswer.select("div.zm-item-answer.zm-item-expanded"));

                if (bodys.iterator().hasNext()) {
                    Iterator iterator = bodys.iterator();
                    while (iterator.hasNext()) {
                        AnswersModel answersModel = new AnswersModel();
                        Element element = (Element) iterator.next();
                        String url = element.getElementsByTag("link").attr("href");
                        String vote = element.select("span.count").text();
                        String content = element.select("div.zh-summary.summary.clearfix").text();
                        if (content.length() > 4) {
                            content = content.substring(0, content.length() - 4);
                        }
                        String user = element.select("a.author-link").text();
                        answersModel.setAuthor(user);
                        answersModel.setContent(content);
                        answersModel.setUrl(url);
                        answersModel.setVote(vote);
                        list.add(answersModel);

                    }
                }

                return list;
            }
        }).compose(this.<List<AnswersModel>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<AnswersModel>>() {


                    @Override
                    public void onCompleted() {

                        if (mStateView.getViewState() != MultiStateView.ViewState.CONTENT) {
                            mStateView.setViewState(MultiStateView.ViewState.CONTENT);
                        }

                        mRefresh.setRefreshing(false);

                        initRecyclerView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (needState) {
                            mStateView.setViewState(MultiStateView.ViewState.ERROR);
                        }
                        mRefresh.setRefreshing(false);


                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onNext(List<AnswersModel> s) {
                        mLists.clear();
                        mLists.addAll(s);

                    }
                });


    }

    private void initRecyclerView() {
        if (mRecyclerView.getAdapter() == null) {
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setAdapter(new BaseAdapter() {
                @Override
                public void onBindView(BaseViewHolder holder, int position) {

                    if (position == 0) {
                        holder.setText(R.id.item_fr_answers_top_title, title)
                                .setVisibility(R.id.item_fr_answers_top_body, TextUtils.isEmpty(detail) ? BaseViewHolder.GONE : BaseViewHolder.VISIBLE)
                                .setText(R.id.item_fr_answers_top_body, Html.fromHtml(detail).toString())
                                .setText(R.id.item_fr_answers_top_count, mLists.size() + "个回答(只抽取前" + mLists.size() + "个)");

                    } else {
                        AnswersModel answers = mLists.get(position - 1);
                        holder.setText(R.id.item_fr_answers_author, TextUtils.isEmpty(answers.getAuthor()) ? "匿名" : answers.getAuthor())
                                .setText(R.id.item_fr_answers_body, answers.getContent())
                                .setText(R.id.item_fr_answers_vote, answers.getVote() + " 赞同");

                    }
                }

                @Override
                public int getLayoutID(int position) {
                    if (position == 0)
                        return R.layout.item_answers_top_activity;
                    return R.layout.item_answers_activity;
                }

                @Override
                public boolean clickable() {
                    return true;
                }

                @Override
                public void onItemClick(View v, int position) {
                    super.onItemClick(v, position);
                    if (position == 0)
                        return;
                    Intent intent = new Intent(AnswersActivity.this, AnswerDetailActivity.class);
                    intent.putExtra(AnswerDetailActivity.URL, mLists.get(position - 1).getUrl());
                    intent.putExtra(AnswerDetailActivity.TITLE, title);
                    intent.putExtra(AnswerDetailActivity.DETAIL, detail);

                    startActivity(intent);

                }

                @Override
                public int getItemCount() {
                    return mLists.size() + 1;
                }
            });

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (linearLayoutManager.findFirstVisibleItemPosition() != 0) {
                        setTitle(true);
                    } else {
                        setTitle(false);
                    }
                }
            });
        } else {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

}
