package com.jiang.android.zhihu_topanswer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiang.android.architecture.rxsupport.RxAppCompatActivity;
import com.jiang.android.architecture.view.MultiStateView;
import com.jiang.android.zhihu_topanswer.R;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jiang on 2016/12/24.
 */

public class AnswerDetailActivity extends RxAppCompatActivity {

    private static final String TAG = "AnswerDetailActivity";
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String DETAIL = "detail";
    private String mUrl;
    private WebView mWebView;
    private TextView mTitle;
    private ImageView mBack;
    private MultiStateView mStateView;
    private String title;
    private String bodyHtml;
    private TextView mDetail;
    private String detail;
    private View mPadding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_detail);
        mUrl = "https://www.zhihu.com" + getIntent().getExtras().getString(URL);
        title = getIntent().getExtras().getString(TITLE);
        detail = getIntent().getExtras().getString(DETAIL);
        initView();
    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.answers_detail_title);
        mBack = (ImageView) findViewById(R.id.answers_detail_back);
        mWebView = (WebView) findViewById(R.id.activity_answers_detail_webview);
        mStateView = (MultiStateView) findViewById(R.id.activity_answers_detail_state);
        mDetail = (TextView) findViewById(R.id.activity_answers_detail_detail);
        mPadding = (View) findViewById(R.id.activity_answers_detail_padding);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setTextSize(WebSettings.TextSize.LARGER);
//        mWebView.getSettings().setBlockNetworkImage(false);
        mTitle.setText(title);
        if (TextUtils.isEmpty(detail)) {
            mPadding.setVisibility(View.GONE);
            mDetail.setVisibility(View.GONE);
        } else {
            mPadding.setVisibility(View.VISIBLE);
            mDetail.setVisibility(View.VISIBLE);
            mDetail.setText(Html.fromHtml(detail).toString());
        }
        mBack.setClickable(true);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerDetailActivity.this.finish();
            }
        });


        initData(true);
    }

    private void initData(final boolean needState) {

        if (needState) {
            mStateView.setViewState(MultiStateView.ViewState.LOADING);
        }
        final String url = mUrl;
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
        }).map(new Func1<Document, String>() {
            @Override
            public String call(Document document) {


                Element bodyAnswer = document.getElementById("zh-question-answer-wrap");
                Elements bodys = bodyAnswer.select("div.zm-item-answer");
                Elements headElements = document.getElementsByTag("head");
                headElements.iterator().next();
                String head = headElements.iterator().next().outerHtml();

                String html = "";
                if (bodys.iterator().hasNext()) {
                    Iterator iterator = bodys.iterator();
                    if (iterator.hasNext()) {
                        Element element = (Element) iterator.next();
                        String body = "<body>" + element.select("div.zm-item-rich-text.expandable.js-collapse-body").iterator().next().outerHtml() + "</body>";
                        html = "<html lang=\"en\" xmlns:o=\"http://www.w3.org/1999/xhtml\">" + head + body + "</html>";

                        Document docu = Jsoup.parse(html);
                        Elements elements = docu.getElementsByTag("img");
                        Iterator iter = elements.iterator();
                        while (iter.hasNext()) {
                            Element imgElement = (Element) iter.next();
                            String result = imgElement.attr("data-actualsrc");
                            if (TextUtils.isEmpty(result)) {
                                result = imgElement.attr("data-original");
                            }
                            imgElement.attr("src", result);
                        }
                        html = docu.outerHtml();
//                        html = html.replace("<noscript>", "<!--<noscript> ");
//                        html = html.replace("</noscript>", "</noscript>--> ");
//                        html = html.replace("src=\"//", "src=\"https://");
                        return html;

                    }
                }
                return "";

            }
        }).compose(this.<String>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {


                    @Override
                    public void onCompleted() {

                        if (mStateView.getViewState() != MultiStateView.ViewState.CONTENT) {
                            mStateView.setViewState(MultiStateView.ViewState.CONTENT);
                        }


                        mWebView.loadDataWithBaseURL("http://www.zhihu.com", bodyHtml, "text/html", "utf-8", null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (needState) {
                            mStateView.setViewState(MultiStateView.ViewState.ERROR);
                        }


                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onNext(String s) {

                        bodyHtml = s;

                    }
                });
    }
}
