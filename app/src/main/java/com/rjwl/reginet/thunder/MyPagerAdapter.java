package com.rjwl.reginet.thunder;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13.
 */

public class MyPagerAdapter extends PagerAdapter {
    private List<View> views;

    public MyPagerAdapter(List<View> views) {
        this.views = views;
    }

    /**
     * 获得当前界面数
     *
     * @return
     */
    @Override
    public int getCount() {
        return views.size();
    }

    /**
     * 创建
     * 初始化position界面
     *
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        ((ViewPager) container).addView(views.get(position));

        return views.get(position);
    }

    /**
     * 判断是否由对象生成界面
     *
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 销毁
     * 删除界面
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(views.get(position));
    }

}
