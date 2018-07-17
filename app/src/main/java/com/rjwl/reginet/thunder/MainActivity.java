package com.rjwl.reginet.thunder;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.PlatformConfig;
import cn.jiguang.share.android.api.ShareParams;
import cn.jiguang.share.wechat.Wechat;
import cn.jiguang.share.wechat.WechatMoments;
import me.kaelaela.verticalviewpager.VerticalViewPager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private VerticalViewPager viewPager;
    private List<View> views;
    private RelativeLayout rl_share;
    private ImageView img_share;
    private ImageView imgWechat;
    private ImageView imgWxfriends;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    private ShareParams shareParams;

    private int[] images = {R.drawable.first, R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4,
            R.drawable.t5, R.drawable.t6, R.drawable.t7, R.drawable.t8, R.drawable.t9, R.drawable.t10};
    private MyPagerAdapter viewPagerAdapter;

    private long fistPressedTime;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String toastMsg = (String) msg.obj;
            Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
            Log.d("TAG1", "分享结果:" + toastMsg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置满屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.dialog);

        viewPager = findViewById(R.id.viewpager);
        rl_share = findViewById(R.id.rl_share);
        img_share = findViewById(R.id.img_share);

        views = new ArrayList<>();
        viewPagerAdapter = new MyPagerAdapter(views);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG1", "分享");
                alertDialog = alertDialogBuilder.create();
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.share_dialog, null);
                initDialogView(view1);
                alertDialog.setView(view1);
                alertDialog.show();
                settingWifiDialog(alertDialog);
            }
        });

        PlatformConfig platformConfig = new PlatformConfig();
        platformConfig.setWechat("wx24ddb563862ef90", "3d2bc690f6769530b84272d5aa1b9ac");
        JShareInterface.init(getApplicationContext(), platformConfig);

        shareParams = new ShareParams();
        shareParams.setTitle("避险指南-中小学生版");
        shareParams.setText("中小学生雷电避险知识");
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        shareParams.setUrl("http://47.93.24.30/zhinan/X-admin/");//必须
        shareParams.setImageData(BitmapFactory.decodeResource(getResources(), R.drawable.share_tit));
        List<String> list = JShareInterface.getPlatformList();
        Log.d("TAG1", "配置的平台：" + list);
        JShareInterface.authorize(WechatMoments.Name, null);
    }

    private void initDialogView(View view) {
        imgWechat = view.findViewById(R.id.img_share_wechat);
        imgWxfriends = view.findViewById(R.id.img_share_wxcircle);

        imgWechat.setOnClickListener(this);
        imgWxfriends.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        //Friend 分享微信好友,Zone 分享微信朋友圈,Favorites 分享微信收藏
        String str = "";
        switch (v.getId()) {
            case R.id.img_share_wechat:
                Log.d("TAG1", "微信分享");
                //Friend 分享微信好友,Zone 分享微信朋友圈,Favorites 分享微信收藏
                str = Wechat.Name;
                break;
            case R.id.img_share_wxcircle:
                Log.d("TAG1", "朋友圈分享");
                str = WechatMoments.Name;
                //Friend 分享微信好友,Zone 分享微信朋友圈,Favorites 分享微信收藏
                break;
        }
        JShareInterface.share(str, shareParams, mPlatActionListener);
    }

    private PlatActionListener mPlatActionListener = new PlatActionListener() {
        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> data) {
            if (handler != null) {
                Message message = handler.obtainMessage();
                message.obj = "分享成功";
                Log.d("TAG1", "分享成功");
                handler.sendMessage(message);
            }
            dissDialog();
        }

        @Override
        public void onError(Platform platform, int action, int errorCode, Throwable error) {
            if (handler != null) {
                Message message = handler.obtainMessage();
                message.obj = "分享失败:" + (error != null ? error.getMessage() : "") + "---" + errorCode;
                Log.d("TAG1", "分享失败");
                handler.sendMessage(message);
            }
            dissDialog();
        }

        @Override
        public void onCancel(Platform platform, int action) {
            if (handler != null) {
                Message message = handler.obtainMessage();
                message.obj = "分享取消";
                Log.d("TAG1", "分享取消");
                handler.sendMessage(message);
            }
            dissDialog();
        }
    };

    public void dissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    private void settingWifiDialog(AlertDialog alertDialog) {

        /*Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);*/

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        Window window = alertDialog.getWindow();
        android.view.WindowManager.LayoutParams p = window.getAttributes();  //获取对话框当前的参数值
        window.setGravity(Gravity.BOTTOM);
        p.height = (int) (d.getHeight() * 0.2);  //高度设置为屏幕的0.3
        p.width = (int) (d.getWidth() * 1);    //宽度设置为屏幕的0.5
        window.setAttributes(p);    //设置生效
    }


    /**
     * 初始化数据
     */
    int startX = 0;
    int startY = 0;
    int endX = 0;
    int endY = 0;

    private void initData() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                //设置宽高
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i : images) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(i);
            //设置图片属性
            imageView.setLayoutParams(layoutParams);
            views.add(imageView);
        }
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        //Log.d("TAG1", startX + ";" + startY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = (int) event.getX();
                        endY = (int) event.getY();
                        if (Math.abs(endY - startY) < 50) {
                            Log.d("TAG1", "点击事件");
                            if (rl_share.getVisibility() == View.VISIBLE) {
                                rl_share.setVisibility(View.GONE);
                            } else {
                                rl_share.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d("TAG1", "onPageScrolled:" + positionOffset + ";" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                //Log.d("TAG1", "onPageSelected:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.d("TAG1", "onPageScrollStateChanged:" + state);
                if (rl_share.getVisibility() == View.VISIBLE) {
                    rl_share.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - fistPressedTime < 2000) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        }
        fistPressedTime = System.currentTimeMillis();
    }
}
