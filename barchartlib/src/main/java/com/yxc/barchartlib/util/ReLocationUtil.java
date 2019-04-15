package com.yxc.barchartlib.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.yxc.barchartlib.component.DistanceCompare;
import com.yxc.barchartlib.entrys.BarEntry;
import com.yxc.barchartlib.view.BarChartAdapter;

import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.List;

/**
 * @author yxc
 * @date 2019/4/12
 */
public class ReLocationUtil {

    public static final int VIEW_DAY = 0;
    public static final int VIEW_WEEK = 1;
    public static final int VIEW_MONTH = 2;
    public static final int VIEW_YEAR = 3;

    //位置进行微调
    public static HashMap<Float, List<BarEntry>> microRelation(RecyclerView recyclerView) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        BarChartAdapter adapter = (BarChartAdapter) recyclerView.getAdapter();
        List<BarEntry> entries = adapter.getEntries();
        //获取最后一个完全显示的ItemPosition
        int lastVisibleItemPosition = manager.findLastCompletelyVisibleItemPosition();

        lastVisibleItemPosition = manager.findLastCompletelyVisibleItemPosition();
        int firstVisibleItemPosition = manager.findFirstCompletelyVisibleItemPosition();

        Log.d("VisiblePosition", "begin:" + entries.get(firstVisibleItemPosition).localDate +
                ": end" + entries.get(lastVisibleItemPosition).localDate);

        List<BarEntry> visibleEntries = entries.subList(firstVisibleItemPosition, lastVisibleItemPosition + 1);
        float yAxisMaximum = DecimalUtil.getTheMaxNumber(visibleEntries);
        HashMap<Float, List<BarEntry>> map = new HashMap<>();
        map.put(yAxisMaximum, visibleEntries);

        return map;
    }

    public static HashMap<Float, List<BarEntry>> getVisibleEntries(RecyclerView recyclerView) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        BarChartAdapter adapter = (BarChartAdapter) recyclerView.getAdapter();
        List<BarEntry> mEntries = adapter.getEntries();
        //获取最后一个完全显示的ItemPosition
        int lastVisibleItemPosition = manager.findLastCompletelyVisibleItemPosition();
        int firstVisibleItemPosition = manager.findFirstCompletelyVisibleItemPosition();
        List<BarEntry> visibleEntries = mEntries.subList(firstVisibleItemPosition, lastVisibleItemPosition + 1);
        float yAxisMaximum = DecimalUtil.getTheMaxNumber(visibleEntries);
        HashMap<Float, List<BarEntry>> map = new HashMap<>();
        map.put(yAxisMaximum, visibleEntries);
        return map;
    }



    //compute the scrollByDx, the left is large position, right is small position.
    public static int computeScrollByXOffset(RecyclerView recyclerView, int displayNumbers) {
        DistanceCompare distanceCompare = findDisplayFirstTypePosition(recyclerView, displayNumbers);
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        BarChartAdapter adapter = (BarChartAdapter) recyclerView.getAdapter();
        List<BarEntry> entries = adapter.getEntries();
        int positionCompare = distanceCompare.position;

        View compareView = manager.findViewByPosition(positionCompare);
        int compareViewRight = compareView.getRight();
        int compareViewLeft = compareView.getLeft();

        int childWidth = compareView.getWidth();
        int parentLeft = recyclerView.getPaddingLeft();
        int parentRight = recyclerView.getWidth() - recyclerView.getPaddingRight();

        //todo int 会不会 越界
        int firstViewRight = compareViewRight + positionCompare * childWidth;

        //这个值会为 负的。
        int lastViewLeft = compareViewLeft - (entries.size() - 1 - positionCompare) * childWidth;

        int scrollByXOffset;

        if (distanceCompare.isNearLeft()) {//靠近左边，content左移，recyclerView右移，取正。
            //情况 1.
            int distance = compareViewRight - parentLeft;//原始调整距离
            int distanceRightBoundary = Math.abs(firstViewRight - parentRight);//右边界

            if (distanceRightBoundary < distance) { //content左移不够，顶到头，用 distanceRightBoundary
                distance = distanceRightBoundary;
            } else {//distance 不用修改

            }
            scrollByXOffset = distance;
        } else {//靠近右边，content右移，recyclerView左移，取负。
            int distance = parentRight - compareViewRight;//原始调整距离
            int distanceLeftBoundary = Math.abs(parentLeft - lastViewLeft);//右边 - 左边，因为 lastViewLeft是负值，实际上是两值相加。

            if (distanceLeftBoundary < distance) {//content右移不够，顶到头，distanceLeftBoundary
                distance = distanceLeftBoundary;
            }
            //记得取负， scrollBy的话
            scrollByXOffset = distance - 2 * distance;
        }
        return scrollByXOffset;
    }

    //find the largest divider position ( ItemDecoration ).
    public static DistanceCompare findDisplayFirstTypePosition(RecyclerView recyclerView, int displayNumbers) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        BarChartAdapter adapter = (BarChartAdapter) recyclerView.getAdapter();
        List<BarEntry> entries = adapter.getEntries();
        int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
        int position = firstVisibleItemPosition; //从右边的第一个View开始找
        int parentRight = recyclerView.getWidth() - recyclerView.getPaddingRight();
        int parentLeft = recyclerView.getPaddingLeft();
        DistanceCompare distanceCompare = new DistanceCompare(0, 0);
        for (int i = 0; i < displayNumbers; i++) {
            if (i > 0) {
                position++;
            }
            if (position >= 0 && position < entries.size()) {
                BarEntry barEntry = entries.get(position);
                if (barEntry.type == BarEntry.TYPE_XAXIS_FIRST || barEntry.type == BarEntry.TYPE_XAXIS_SPECIAL) {
                    distanceCompare.position = position;
                    View positionView = manager.findViewByPosition(position);
                    int viewLeft = positionView.getLeft();
                    distanceCompare.distanceRight = parentRight - viewLeft;
                    distanceCompare.distanceLeft = viewLeft - parentLeft;
                    distanceCompare.setBarEntry(barEntry);
                    break;
                }
            }
        }
        return distanceCompare;
    }


    /**
     * @param recyclerView
     * @param displayNumbers
     * @param type
     * @return
     *
     * the scrollToPosition Just let this position item display,
     * but don't consume the location in the edge of screen. so Deprecated and
     * use computeScrollByXOffset replace
     */
    @Deprecated
    public static DistanceCompare findScrollToPostion(RecyclerView recyclerView, int displayNumbers, int type) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        BarChartAdapter adapter = (BarChartAdapter) recyclerView.getAdapter();
        List<BarEntry> entries = adapter.getEntries();
        int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
        int position = firstVisibleItemPosition; //从右边的第一个View开始找
        int parentRight = recyclerView.getWidth() - recyclerView.getPaddingRight();
        int parentLeft = recyclerView.getPaddingLeft();
        DistanceCompare distanceCompare = new DistanceCompare(0, 0);
        for (int i = 0; i < displayNumbers; i++) {
            if (i > 0) {
                position++;
            }
            if (position >= 0 && position < entries.size()) {
                BarEntry barEntry = entries.get(position);
                if (barEntry.type == BarEntry.TYPE_XAXIS_FIRST || barEntry.type == BarEntry.TYPE_XAXIS_SPECIAL) {
                    distanceCompare.position = position;

                    //这里最好不要去找这个view 容易 crash
                    View positionView = manager.findViewByPosition(position);
                    int viewLeft = positionView.getLeft();
                    distanceCompare.distanceRight = parentRight - viewLeft;
                    distanceCompare.distanceLeft = viewLeft - parentLeft;
                    distanceCompare.setBarEntry(barEntry);

                    if (distanceCompare.isNearLeft()) {//靠近左边回到上一个月。
                        int lastPosition = distanceCompare.position - getNumbersUnitType(distanceCompare.barEntry, type);
                        Log.d("ReLocation", "lastPosition:" + lastPosition + " entries' size" + entries.size());
                        if (lastPosition > 0) {
                            distanceCompare.position = lastPosition;
                            distanceCompare.barEntry = entries.get(lastPosition);
                        } else {
                            distanceCompare.position = 0;
                            distanceCompare.barEntry = entries.get(0);
                        }
                        distanceCompare.position = lastPosition;
                    } else {//靠近右边，直接返回当月的。

                    }
                    break;
                }
            }
        }
        return distanceCompare;
    }


    @Deprecated
    private static int getNumbersUnitType(BarEntry currentBarEntry, int type) {
        if (type == VIEW_DAY) {
            return TimeUtil.NUM_HOUR_OF_DAY;
        } else if (type == VIEW_WEEK) {
            return TimeUtil.NUM_DAY_OF_WEEK;
        } else if (type == VIEW_MONTH) {
            LocalDate localDate = currentBarEntry.localDate;
            LocalDate lastMonthEndLocalDate = TimeUtil.getFirstDayOfMonth(localDate).minusDays(1);//上个月末的最后一天
            int distance = TimeUtil.getIntervalDay(lastMonthEndLocalDate, localDate);
            Log.d("Tag", "localDate:" + localDate + " lastMonthDay:" + lastMonthEndLocalDate + " distance:" + distance);
            return distance;
        } else if (type == VIEW_YEAR) {
            return TimeUtil.NUM_MONTH_OF_YEAR;
        }
        return TimeUtil.NUM_HOUR_OF_DAY;
    }



}
