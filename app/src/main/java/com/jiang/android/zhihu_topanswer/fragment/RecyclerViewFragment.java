package com.jiang.android.zhihu_topanswer.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jiang.android.architecture.adapter.BaseAdapter;
import com.jiang.android.architecture.adapter.BaseViewHolder;
import com.jiang.android.architecture.rxsupport.RxFragment;
import com.jiang.android.architecture.utils.L;
import com.jiang.android.zhihu_topanswer.R;
import com.jiang.android.zhihu_topanswer.model.TopicAnswers;
import com.trello.rxlifecycle.android.FragmentEvent;

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
 * Created by jiang on 2016/12/23.
 */

public class RecyclerViewFragment extends RxFragment {

    private static final String TAG = "RecyclerViewFragment";

    private int page = 1;
    private static final java.lang.String BUNDLE_ID = "bundle_id";
    private int mTopic;
    private RecyclerView mRecyclerView;
    private List<TopicAnswers> mLists = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTopic = arguments.getInt(BUNDLE_ID);
        }
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_rv);
        return view;
    }

    public static RecyclerViewFragment newInstance(int topic) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID, topic);
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(page);
        initRecyclerView();
    }

    private void initData(int page) {
        final String url = "https://www.zhihu.com/topic/" + mTopic + "/top-answers?page=" + page;
        Observable.create(new Observable.OnSubscribe<Document>() {
            @Override
            public void call(Subscriber<? super Document> subscriber) {

                try {
                    subscriber.onNext(Jsoup.connect(url).timeout(5000).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).map(new Func1<Document, List<TopicAnswers>>() {
            @Override
            public List<TopicAnswers> call(Document document) {
                Elements contentLinks = document.select("div.content");
                List<TopicAnswers> list = new ArrayList<>();
                Iterator iterator = contentLinks.iterator();
                while (iterator.hasNext()) {
                    TopicAnswers answers = new TopicAnswers();
                    Element body = (Element) iterator.next();
                    Elements questionLinks = body.select("a.question_link");
                    if (questionLinks.iterator().hasNext()) {
                        Element questionLink = questionLinks.iterator().next();
                        answers.setTitle(questionLink.text());
                        answers.setUrl("https://www.zhihu.com" + questionLink.attr("href"));
                    }


                    Elements votes = body.select("a.zm-item-vote-count.js-expand.js-vote-count");
                    if (votes.size() > 0) {
                        if (votes.iterator().hasNext()) {
                            Element aVotes = votes.iterator().next();
                            answers.setVote(aVotes.text());
                        }
                    }

                    Elements divs = body.select("div.zh-summary.summary.clearfix");

                    String descBody = divs.text();
                    if (descBody.length() > 4) {
                        descBody = descBody.substring(0, descBody.length() - 4);
                    }
                    answers.setBody(descBody);
                    if (divs.size() > 0) {
                        if (divs.iterator().hasNext()) {
                            Element aDiv = divs.iterator().next();

                            Element img = aDiv.children().first();
                            if (img.tagName().equals("img")) {
                                String imgUrl = img.attr("src");
                                answers.setImg(imgUrl);
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(answers.getTitle()) && !TextUtils.isEmpty(answers.getUrl())) {
                        list.add(answers);
                    }
                }
                return list;
            }
        }).compose(this.<List<TopicAnswers>>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<TopicAnswers>>() {
                    @Override
                    public void onCompleted() {

                        initRecyclerView();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<TopicAnswers> s) {
                        L.i(TAG, "onNext: " + s);
                        mLists.addAll(s);
                    }
                });


    }

    private void initRecyclerView() {
        if (mRecyclerView.getAdapter() == null) {

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(new BaseAdapter() {
                @Override
                public void onBindView(BaseViewHolder holder, int position) {

                    TopicAnswers answers = mLists.get(position);
                    holder.setText(R.id.item_fr_top_title, answers.getTitle())
                            .setText(R.id.item_fr_top_body, answers.getBody())
                            .setText(R.id.item_fr_top_vote, answers.getVote() + " 赞同");
                    SimpleDraweeView simpleDraweeView = holder.getView(R.id.item_fr_top_desc);
                    if (TextUtils.isEmpty(answers.getImg())) {
                        simpleDraweeView.setVisibility(View.GONE);
                    } else {
                        simpleDraweeView.setVisibility(View.VISIBLE);
                        simpleDraweeView.setImageURI(Uri.parse(answers.getImg()));
                    }
                }

                @Override
                public int getLayoutID(int position) {
                    return R.layout.item_fragment;
                }

                @Override
                public boolean clickable() {
                    return false;
                }

                @Override
                public int getItemCount() {
                    return mLists.size();
                }
            });
        } else {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }


}
