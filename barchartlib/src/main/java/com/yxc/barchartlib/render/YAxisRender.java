package com.yxc.barchartlib.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.yxc.barchartlib.component.YAxis;
import com.yxc.barchartlib.util.BarChartAttrs;
import com.yxc.barchartlib.util.DisplayUtil;

import java.util.HashMap;
import java.util.Map;

public class YAxisRender {

    protected YAxis mYAxis;

    protected Paint mZeroLinePaint;

    protected Paint mLinePaint;

    protected Paint mTextPaint;

    protected BarChartAttrs mBarChartAttrs;


    public YAxisRender(BarChartAttrs barChartAttrs, YAxis yAxis) {
        this.mBarChartAttrs = barChartAttrs;
        this.mYAxis = yAxis;
        initPaint();
        initTextPaint();
    }

    private void initTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.reset();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize(mYAxis.getTextSize());
    }

    private void initPaint() {
        mLinePaint = new Paint();
        mLinePaint.reset();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setColor(Color.GRAY);
    }


    //绘制 Y轴刻度线 横的网格线
    public void drawHorizontalLine(Canvas canvas, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        mLinePaint.setColor(mYAxis.getGridColor());
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        float distance = bottom - mBarChartAttrs.contentPaddingBottom - mBarChartAttrs.maxYAxisPaddingTop;
        int lineNums = mYAxis.getLabelCount();
        float lineDistance = distance / lineNums;
        float gridLine = top + mBarChartAttrs.maxYAxisPaddingTop;
        for (int i = 0; i <= lineNums; i++) {
            if (i > 0) {
                gridLine = gridLine + lineDistance;
            }
            Path path = new Path();
            path.moveTo(left, gridLine);
            path.lineTo(right, gridLine);

            boolean enable = false;
            if (i == lineNums && mBarChartAttrs.enableYAxisZero) {
                enable = true;
            } else {
                enable = mBarChartAttrs.enableXAxisGridLine;//允许画 Y轴刻度
            }
            if (enable) {
                canvas.drawPath(path, mLinePaint);
            }
        }
    }

    //绘制左边的刻度
    public void drawLeftYAxisLabel(Canvas canvas, RecyclerView parent) {
        if (mBarChartAttrs.enableLeftYAxisLabel) {
            int top = parent.getPaddingTop();
            int bottom = parent.getHeight() - parent.getPaddingBottom();

            mTextPaint.setTextSize(mYAxis.getTextSize());
            String longestStr = mYAxis.getLongestLabel();

            float yAxisWidth = mTextPaint.measureText(longestStr) + mBarChartAttrs.recyclerPaddingLeft;
            mYAxis.leftTxtWidth = mTextPaint.measureText(longestStr);

            int paddingLeft = computeYAxisWidth(parent.getPaddingLeft(), yAxisWidth);
            //设置 recyclerView的 BarChart 内容区域
            parent.setPadding(paddingLeft, parent.getPaddingTop(), parent.getPaddingRight(), parent.getPaddingBottom());

            float topLocation = top + mBarChartAttrs.maxYAxisPaddingTop;
            float containerHeight = bottom - mBarChartAttrs.contentPaddingBottom - topLocation;
            float itemHeight = containerHeight / mYAxis.getLabelCount();
            HashMap<Float, Float> yAxisScaleMap = mYAxis.getYAxisScaleMap(topLocation, itemHeight, mYAxis.getLabelCount());

            for (Map.Entry<Float, Float> entry : yAxisScaleMap.entrySet()) {
                float yAxisScaleLocation = entry.getKey();
                float yAxisScaleValue = entry.getValue();
                String labelStr = mYAxis.getValueFormatter().getFormattedValue(yAxisScaleValue);

                float txtY = yAxisScaleLocation + mYAxis.labelVerticalPadding;
                float txtX = yAxisWidth - mTextPaint.measureText(labelStr) - mYAxis.labelHorizontalPadding;
                canvas.drawText(labelStr, txtX, txtY, mTextPaint);
            }
        }
    }

    //绘制右边的刻度
    public void drawRightYAxisLabel(Canvas canvas, RecyclerView parent) {
        if (mBarChartAttrs.enableRightYAxisLabel) {
            int right = parent.getWidth();
            int top = parent.getPaddingTop();
            int bottom = parent.getHeight() - parent.getPaddingBottom();

            mTextPaint.setTextSize(mYAxis.getTextSize());
            String longestStr = mYAxis.getLongestLabel();
            float yAxisWidth = mTextPaint.measureText(longestStr) + mBarChartAttrs.recyclerPaddingRight;
            mYAxis.rightTxtWidth = mTextPaint.measureText(longestStr);

            int paddingRight = computeYAxisWidth(parent.getPaddingRight(), yAxisWidth);
            //设置 recyclerView的 BarChart 内容区域
            parent.setPadding(parent.getPaddingLeft(), parent.getPaddingTop(), paddingRight, parent.getPaddingBottom());

            float topLocation = top + mBarChartAttrs.maxYAxisPaddingTop;
            float containerHeight = bottom - mBarChartAttrs.contentPaddingBottom - topLocation;
            float itemHeight = containerHeight / mYAxis.getLabelCount();
            HashMap<Float, Float> yAxisScaleMap = mYAxis.getYAxisScaleMap(topLocation, itemHeight, mYAxis.getLabelCount());

            float txtX = right - parent.getPaddingRight() + mYAxis.labelHorizontalPadding;

            for (Map.Entry<Float, Float> entry : yAxisScaleMap.entrySet()) {
                float yAxisScaleLocation = entry.getKey();
                float yAxisScaleValue = entry.getValue();
                String labelStr = mYAxis.getValueFormatter().getFormattedValue(yAxisScaleValue);
                float txtY = yAxisScaleLocation + mYAxis.labelVerticalPadding;
                canvas.drawText(labelStr, txtX, txtY, mTextPaint);
            }
        }
    }

    private int computeYAxisWidth(int originPadding, float yAxisWidth) {
        float resultPadding;
        Log.d("YAxis1", "originPadding:" + originPadding + " yAxisWidth:" + yAxisWidth);
        if (originPadding > yAxisWidth) {
            float distance = originPadding - yAxisWidth;
            if (distance > DisplayUtil.dip2px(8)) {
                Log.d("YAxis", "if control originPadding:" + originPadding + " yAxisWidth:" + yAxisWidth);
                resultPadding = yAxisWidth;//实际需要的跟原来差8dp了就用，实际测量的，否则就用原来的
            } else {
                Log.d("YAxis", "else control originPadding:" + originPadding + " yAxisWidth:" + yAxisWidth);
                resultPadding = originPadding;
            }
        } else {//原来设定的 padding 不够用
            Log.d("YAxis", "control originPadding:" + originPadding + " yAxisWidth:" + yAxisWidth);
            resultPadding = yAxisWidth;
        }
        return (int) resultPadding;
    }
}