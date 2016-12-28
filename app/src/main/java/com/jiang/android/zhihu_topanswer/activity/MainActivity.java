package com.jiang.android.zhihu_topanswer.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jiang.android.architecture.rxsupport.RxAppCompatActivity;
import com.jiang.android.zhihu_topanswer.R;
import com.jiang.android.zhihu_topanswer.fragment.BaseFragment;
import com.jiang.android.zhihu_topanswer.fragment.CollectionFragment;
import com.jiang.android.zhihu_topanswer.fragment.MainFragment;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationItem;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;
import com.luseen.luseenbottomnavigation.BottomNavigation.OnBottomNavigationItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends RxAppCompatActivity {



    private List<BaseFragment> mFragments = new ArrayList<>();
    private int  mCheckPosition = 0;
    private  int mLastCheckePosition = -1;
    private Toolbar mToolbar;

    private boolean isExit;
    private BottomNavigationView mBottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTitle();

        initFragment();
        initBottomNav();
        itemChecked();


    }

    private void initBottomNav(){
        mBottomView = (BottomNavigationView) findViewById(R.id.main_bottom_nav);

        BottomNavigationItem bottomNavigationItem = new BottomNavigationItem
                ("话题", ContextCompat.getColor(this, R.color.colorPrimaryDark), R.drawable.ic_topic);
        BottomNavigationItem bottomNavigationItem1 = new BottomNavigationItem
                ("收藏", ContextCompat.getColor(this, R.color.colorPrimaryDark), R.drawable.ic_collection);
        mBottomView.addTab(bottomNavigationItem);
        mBottomView.addTab(bottomNavigationItem1);
        mBottomView.setOnBottomNavigationItemClickListener(new OnBottomNavigationItemClickListener() {
            @Override
            public void onNavigationItemClick(int index) {
                if(index == mCheckPosition)
                    return;
                mLastCheckePosition = mCheckPosition;
                mCheckPosition = index;
                itemChecked();
            }
        });

    }

    private void itemChecked() {
        if(mCheckPosition>=0){
            switchFragment(mLastCheckePosition>=0?mFragments.get(mLastCheckePosition):null,mFragments.get(mCheckPosition));
        }
    }

    private void initFragment() {
        mFragments.add(MainFragment.newInstance());
        mFragments.add(CollectionFragment.newInstance());
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


    public void switchFragment(BaseFragment from, BaseFragment to) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (from == null) {
            if (to != null) {
                transaction.replace(R.id.id_content, to);
                transaction.commit();
            }
        } else {
            if (!to.isAdded()) {
                transaction.hide(from).add(R.id.id_content, to).commit();
            } else {
                transaction.hide(from).show(to).commit();
            }

        }

    }


}
