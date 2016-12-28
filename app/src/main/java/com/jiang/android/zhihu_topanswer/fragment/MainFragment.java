package com.jiang.android.zhihu_topanswer.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiang.android.architecture.adapter.BaseAdapter;
import com.jiang.android.architecture.adapter.BaseViewHolder;
import com.jiang.android.zhihu_topanswer.R;
import com.jiang.android.zhihu_topanswer.model.TopicModel;
import com.jiang.android.zhihu_topanswer.utils.AllTopic;
import com.jiang.android.zhihu_topanswer.view.SelectPopupWindow;
import com.trello.rxlifecycle.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jiang on 2016/12/28.
 */

public class MainFragment extends BaseFragment {

    private List<TopicModel> mLists = new ArrayList<>();
    private List<RecyclerViewFragment> mFragments = new ArrayList<>();
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mSelectPop;
    private TabAdapter adapter;
    private SelectPopupWindow popupWindow;
    private View mLine;


    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initTabLayout(view);
        return view;
    }
    private void initTabLayout(View view) {
        mTabLayout = (TabLayout) view.findViewById(R.id.main_tablayout);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mSelectPop = (ImageView) view.findViewById(R.id.main_arrow);
        mLine = view.findViewById(R.id.main_pop_line);


    }
    private void initViewPager() {
        if (adapter == null) {
            adapter = new TabAdapter(getChildFragmentManager(), mLists, mFragments);
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
        } else {
            adapter.notifyDataSetChanged();
        }
    }



    private static class TabAdapter extends FragmentPagerAdapter {

        private List<TopicModel> title;
        private List<RecyclerViewFragment> views;

        public TabAdapter(FragmentManager fm, List<TopicModel> title, List<RecyclerViewFragment> views) {
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

    private void initView() {

        mLists.clear();
        mFragments.clear();
        mTabLayout.removeAllTabs();
        mLists.addAll(AllTopic.getInstance().getAllTopics(getActivity()));


        Observable.from(mLists)
                .map(new Func1<TopicModel, TopicModel>() {
                    @Override
                    public TopicModel call(TopicModel topicModel) {
                        mFragments.add(RecyclerViewFragment.newInstance(topicModel.getTopic()));
                        return topicModel;
                    }
                }).compose(this.<TopicModel>bindUntilEvent(FragmentEvent.DESTROY))
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

    private void initPopWindow() {
        initPopWindowReal();

    }

    private void initPopWindowReal() {
        if (popupWindow == null) {
            popupWindow = new SelectPopupWindow(getActivity());
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
                    return true;
                }

                @Override
                public void onItemClick(View v, int position) {
                    super.onItemClick(v, position);
                    choosePosition(position);
                    popupWindow.dismiss();
                }

                @Override
                public int getItemCount() {
                    return mLists.size();
                }
            });

        }
        popupWindow.showAsDropDown(mLine);
    }

    private void choosePosition(int position) {
        mTabLayout.setScrollPosition(position, 0, true);
        mViewPager.setCurrentItem(position);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }
}
