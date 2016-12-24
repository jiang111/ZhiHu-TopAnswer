package com.jiang.android.zhihu_topanswer.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.jiang.android.architecture.adapter.BaseAdapter;
import com.jiang.android.architecture.adapter.BaseViewHolder;
import com.jiang.android.architecture.rxsupport.RxAppCompatActivity;
import com.jiang.android.zhihu_topanswer.R;
import com.jiang.android.zhihu_topanswer.fragment.RecyclerViewFragment;
import com.jiang.android.zhihu_topanswer.model.TopicModel;
import com.jiang.android.zhihu_topanswer.utils.AllTopic;
import com.jiang.android.zhihu_topanswer.view.SelectPopupWindow;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends RxAppCompatActivity {


    private List<TopicModel> mLists = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mSelectPop;
    private SelectPopupWindow popupWindow;
    private CoordinatorLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTitle();
        initTabLayout();
        initView();

    }

    private void initViewPager() {
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), mLists, mFragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(mLists.size());
        //为TabLayout设置ViewPager
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setupWithViewPager(mViewPager);
        mSelectPop.setClickable(true);
        mSelectPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopWindow();
            }
        });
    }

    private void initTabLayout() {
        mTabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mSelectPop = (ImageView) findViewById(R.id.main_arrow);
        mRootLayout = (CoordinatorLayout) findViewById(R.id.activity_main);


    }

    private void initTitle() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("首页");
    }

    private void initView() {

        mLists.addAll(AllTopic.getInstance().getAllTopics(this));

        Observable.from(mLists)
                .map(new Func1<TopicModel, TopicModel>() {
                    @Override
                    public TopicModel call(TopicModel topicModel) {
                        mFragments.add(RecyclerViewFragment.newInstance(topicModel.getTopic()));
                        return topicModel;
                    }
                }).compose(this.<TopicModel>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TopicModel>() {
                    @Override
                    public void onCompleted() {

                        initViewPager();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TopicModel topicModel) {
                        mTabLayout.addTab(mTabLayout.newTab().setText(topicModel.getName()));

                    }
                });


    }

    private static class TabAdapter extends FragmentPagerAdapter {

        private List<TopicModel> title;
        private List<Fragment> views;

        public TabAdapter(FragmentManager fm, List<TopicModel> title, List<Fragment> views) {
            super(fm);
            this.title = title;
            this.views = views;
        }

        @Override
        public Fragment getItem(int position) {
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }


        //配置标题的方法
        @Override
        public CharSequence getPageTitle(int position) {
            return title.get(position).getName();
        }
    }


    private void initPopWindow() {
        if (popupWindow == null) {
            popupWindow = new SelectPopupWindow(this);
            popupWindow.setAdapter(new BaseAdapter() {
                @Override
                public void onBindView(BaseViewHolder holder, int position) {

                    holder.setText(R.id.item_pop_text, mLists.get(position).getName());

                }

                @Override
                public int getLayoutID(int position) {
                    return R.layout.item_pop_t;
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
            popupWindow.setDeleteAdapter(new BaseAdapter() {
                @Override
                public void onBindView(BaseViewHolder holder, int position) {

                    holder.setText(R.id.item_pop_text, mLists.get(position).getName());

                }

                @Override
                public int getLayoutID(int position) {
                    return R.layout.item_pop_t;
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

        }
        popupWindow.showAsDropDown(mToolbar);
    }

}
