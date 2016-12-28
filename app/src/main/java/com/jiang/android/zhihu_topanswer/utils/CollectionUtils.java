package com.jiang.android.zhihu_topanswer.utils;

import com.jiang.android.zhihu_topanswer.db.Collection;
import com.jiang.android.zhihu_topanswer.db.DbUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.jiang.android.zhihu_topanswer.db.DbUtil.getOperatorsService;

/**
 * Created by jiang on 2016/12/28.
 */

public class CollectionUtils {

    public static final int TYPE_ANSWERS = 1;
    public static final int TYPE_ANSWER = 2;
    private static CollectionUtils mCollection;
    private List<Collection> mLists;

    public static CollectionUtils getInstance() {
        synchronized (CollectionUtils.class) {
            if (mCollection == null) {
                synchronized (CollectionUtils.class) {
                    mCollection = new CollectionUtils();
                }
            }
        }
        return mCollection;
    }


    private CollectionUtils() {
        mLists = new ArrayList<>();
        mLists.addAll(getOperatorsService().queryAll());
    }


    public void saveItem(Collection model){
        getOperatorsService().save(model);
        mLists.add(model);


    }


    public Observable<Collection> contailItem(final String url){
      return   Observable.from(mLists)
                .filter(new Func1<Collection, Boolean>() {
                    @Override
                    public Boolean call(Collection model) {
                        return model.getUrl().equals(url);
                    }
                });
    }


    public void removeItem(String mQuestionUrl) {
        for (int i = 0; i < mLists.size(); i++) {
            if(mLists.get(i).getUrl().equals(mQuestionUrl)){
              List<Collection> list =   DbUtil.getOperatorsService().query(" where url=?",mLists.get(i).getUrl());
                if(list!= null && list.size()>0){
                    DbUtil.getOperatorsService().delete(list);
                }
                mLists.remove(i);
                return;
            }
        }
    }

    public List<Collection> getCollection(){
        return mLists;
    }

    public void clear() {
        getOperatorsService().deleteAll();
        mLists.clear();



    }
}
