package com.yxc.barchartlib.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.yxc.barchartlib.util.BarChartAttrs;

/**
 * @author yxc
 * @date 2019/4/11
 */
public class SpeedRatioLinearLayoutManager extends LinearLayoutManager {

    private BarChartAttrs mAttrs;

    public SpeedRatioLinearLayoutManager(Context context, BarChartAttrs attrs) {
        super(context);
        this.mAttrs = attrs;
        setOrientation(mAttrs.layoutManagerOrientation);
    }

    public SpeedRatioLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SpeedRatioLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //屏蔽之后无滑动效果，证明滑动的效果就是由这个函数实现
        int a = super.scrollHorizontallyBy((int) (mAttrs.ratioSpeed * dx), recycler, state);
        if (a == (int) (mAttrs.ratioSpeed * dx)) {
            return dx;
        }
        return a;
    }

}