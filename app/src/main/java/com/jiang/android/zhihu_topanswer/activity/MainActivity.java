package com.jiang.android.zhihu_topanswer.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.jiang.android.architecture.rxsupport.RxAppCompatActivity;
import com.jiang.android.zhihu_topanswer.R;
import com.jiang.android.zhihu_topanswer.fragment.RecyclerViewFragment;
import com.jiang.android.zhihu_topanswer.model.TopicModel;
import com.jiang.android.zhihu_topanswer.utils.AllTopic;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends RxAppCompatActivity {


    private List<TopicModel> mLists = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTitle();
        initTabLayout();
        initView();

    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), mLists, mFragments);
        mViewPager.setAdapter(adapter);
//        mViewPager.setOffscreenPageLimit(mLists.size());
        //为TabLayout设置ViewPager
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initTabLayout() {
        mTabLayout = (TabLayout) findViewById(R.id.main_tablayout);

    }

    private void initTitle() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("首页");
    }

    private void initView() {

        mLists.addAll(AllTopic.getInstance().getTopics(0, 2));
        for (TopicModel item : mLists) {
            mFragments.add(RecyclerViewFragment.newInstance(item.getTopic()));
            mTabLayout.addTab(mTabLayout.newTab().setText(item.getName()));
        }

        initViewPager();
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


}
