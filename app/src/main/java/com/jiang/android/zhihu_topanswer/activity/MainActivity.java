package com.jiang.android.zhihu_topanswer.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends RxAppCompatActivity {


    private List<TopicModel> mLists = new ArrayList<>();
    private List<RecyclerViewFragment> mFragments = new ArrayList<>();
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mSelectPop;
    private SelectPopupWindow popupWindow;
    private TabAdapter adapter;
    private boolean isExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTitle();
        initTabLayout();
        initView();

    }

    private void initViewPager() {
        if (adapter == null) {
            adapter = new TabAdapter(getSupportFragmentManager(), mLists, mFragments);
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

    private void initTabLayout() {
        mTabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mSelectPop = (ImageView) findViewById(R.id.main_arrow);


    }

    private void initTitle() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setTitle(getString(R.string.app_name));
    }

    @Override
    public void onBackPressed() {
        exitBy2Click();

    }

    /**
     * 双击退出
     */
    private void exitBy2Click() {
        Timer tExit;
        if (isExit == false) {
            isExit = true; // 准备退出
            toast("再按一次退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 3000); // 如果3秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            this.finish();
        }
    }

    private void initView() {

        mLists.clear();
        mFragments.clear();
        mTabLayout.removeAllTabs();
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


    private void initPopWindow() {
        initPopWindowReal();

    }

    private void initPopWindowReal() {
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
        popupWindow.showAsDropDown(mToolbar);
    }

    private void choosePosition(int position) {
        mTabLayout.setScrollPosition(position, 0, true);
        mViewPager.setCurrentItem(position);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/jiang111?tab=repositories"));
                startActivity(intent);
                break;
            case R.id.share:
                shareText(item.getActionView());
                break;
            case R.id.update:
                Toast.makeText(this, "当前版本:" + getVersion(), Toast.LENGTH_SHORT).show();
                Intent updateIntent = new Intent(Intent.ACTION_VIEW);
                updateIntent.setData(Uri.parse("https://github.com/jiang111/ZhiHu-TopAnswer/releases"));
                startActivity(updateIntent);
        }


        return super.onOptionsItemSelected(item);
    }

    public void shareText(View view) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hi,我正在使用" + getString(R.string.app_name) + ",推荐你下载这个app一起玩吧 到应用商店或者https://github.com/jiang111/ZhiHu-TopAnswer/releases即可下载");
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }


}
