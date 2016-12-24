package com.jiang.android.zhihu_topanswer.utils;

import android.content.Context;

import com.jiang.android.architecture.utils.SharePrefUtil;
import com.jiang.android.zhihu_topanswer.model.TopicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiang on 2016/12/23.
 */

public class AllTopic {
    private static String cacheString = "19550429=电影&19551147=生活&19550453=音乐&19550517=互联网&19550937=健康&19550560=创业&19551557=设计&19556664=科技&19552266=文化&19551556=旅行&19553298=自然科学&19551404=投资&19555457=商业&19560170=经济学&19551077=历史&19552192=健身&19550780=电子商务&19550994=游戏&19550874=法律&19553528=英语&19553128=减肥&19552204=性生活&19551388=摄影&19551915=汽车&19569420=冷知识&19556423=文学&19553176=教育&19550434=艺术&19560641=职业规划&19561709=用户体验设计&19641262=服饰搭配&19591985=动漫&19550228=知乎&19552706=运动&19575422=影视评论&19643259=美容护肤&19551325=产品经理&19550547=移动互联网&19550422=风险投资&19552414=淘宝网&19565870=谷歌&19555542=养生&19552497=睡眠&19552439=NBA&19554470=微信&19551075=中国历史&19555939=理财&19558435=英语学习&19555634=Android 开发&19550638=社交网络&19551805=烹饪&19550234=创业公司&19609455=金融&19551762=苹果公司&19603145=Android&19556950=物理学&19575492=生物学&19551137=美食&19562832=篮球&19550564=阅读&19554827=体育&19562906=化学&19559052=足球&19554825=职业发展&19562033=人际交往&19555513=生活方式&19556784=电影推荐&19579266=豆瓣&19564412=恋爱&19569848=生活常识&19805970=成人内容&19550581=学习&19556421=两性关系&19564408=爱情&19551469=旅游&19551003=金融学&19604128=医学&19602470=历史人物&19550573=Photoshop&19554051=装修&19556231=交互设计&19558321=人体&19556433=生理学&19568143=心理健康&19566266=学习方法&19561827=心理咨询&19556382=平面设计&19563625=天文学&19552079=面试&19559840=股票&19550340=Facebook&19645023=法律常识&19555480=大学生&19559209=用户界面设计&19557876=职场&19556353=大学&19556758=知识管理&19592882=美国电影&19551864=古典音乐&19552330=程序员&19551174=美剧&19563107=考研&19564504=人生规划&19551625=产品设计&19586269=中国古代历史&19550588=移动应用&19552249=饮食&19550714=摇滚乐&19624277=室内设计&19557815=赚钱&19588006=工作&19553732=单机游戏&19585936=神经学&19559937=留学&19550757=腾讯&19551424=政治&19582176=建筑设计&19577774=欧洲历史&19599479=世界历史&19561180=流行音乐&19551771=求职&19550895=投资银行&19562435=食品安全&19574423=中国近代史&19570354=证券&19551577=阿里巴巴集团&19560559=宇宙学&19554091=数学&19552417=支付宝&19587288=宏观经济学&19559915=科幻电影&19551167=北京美食&19554945=心理&19605346=英雄联盟&19563451=电子竞技&19552739=跑步&19556939=吉他&19569883=摄影技术&19561223=三国&19552212=设计师&19559947=家居&19559424=数据分析&19563977=医生&19592119=天体物理学&19551296=网络游戏&19580750=健身教练&19564862=抑郁症&19550685=用户体验&19559469=律师&19580586=高效学习&19578758=招聘&19551861=钢琴&19571583=犯罪&19555678=进化论&19585985=汽车行业&19561734=环境保护";

    private static AllTopic mAllTopic;
    public static final String MODEL_KEY = "all_topics";

    private static List<TopicModel> mAllTopics;

    public static AllTopic getInstance() {
        synchronized (AllTopic.class) {
            if (mAllTopic == null) {
                mAllTopic = new AllTopic();

            }
        }
        return mAllTopic;
    }

    private AllTopic() {
        mAllTopics = new ArrayList<>();
    }


    public List<TopicModel> getAllTopics(Context context) {

        if (mAllTopics == null || mAllTopics.size() == 0) {
            List<TopicModel> tempModels = (List<TopicModel>) SharePrefUtil.getObj(context, MODEL_KEY);
            if (tempModels != null) {
                mAllTopics.addAll(tempModels);
            } else {
                String[] cacheStr = cacheString.split("&");
                for (int i = 0; i < cacheStr.length; i++) {
                    TopicModel model = new TopicModel(Integer.valueOf(cacheStr[i].split("=")[0]), cacheStr[i].split("=")[1]);
                    mAllTopics.add(model);
                    SharePrefUtil.saveObj(context, MODEL_KEY, mAllTopics);

                }

            }
        }
        return mAllTopics;
    }

    public List<TopicModel> getTopics(Context context, int fromIndex, int toIndex) {
        return getAllTopics(context).subList(fromIndex, toIndex);
    }

    public void clearCache(Context context) {
        SharePrefUtil.removeItem(context, MODEL_KEY);
        mAllTopics.clear();

    }


    public void saveTopics(Context context, List<TopicModel> list) {
        SharePrefUtil.saveObj(context, MODEL_KEY, list);
        mAllTopics.clear();
        mAllTopics.addAll(list);

    }
}
