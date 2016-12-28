package com.jiang.android.zhihu_topanswer.utils;


import android.text.TextUtils;

import com.jiang.android.architecture.utils.L;
import com.jiang.android.zhihu_topanswer.model.TopicAnswers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jiang on 2016/12/28.
 */

public class JSoupUtils {

    private static final String TAG = "JSoupUtils";



    /**
     * 首页获取列表信息
     * @param document
     * @return
     */
    public static List<TopicAnswers> getTopicList(Document document) {
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
                L.i(TAG, answers.toString());
                list.add(answers);
            }
        }
        return list;
    }


}
