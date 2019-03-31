package com.example.tufengyi.manlife.view;


import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.view.View;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekView;


public class ColorfulWeekView extends WeekView {

    private int mRadius;

    public ColorfulWeekView(Context context) {
        super(context);
        //兼容硬件加速无效的代码
        setLayerType(View.LAYER_TYPE_SOFTWARE, mSelectedPaint);
        //4.0以上硬件加速会导致无效
        //滤镜
        //mSelectedPaint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.SOLID));

        setLayerType(View.LAYER_TYPE_SOFTWARE, mSchemePaint);
        //mSchemePaint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.SOLID));
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
    }

    /**
     * 如果需要点击Scheme没有效果，则return true
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return false 则不绘制onDrawScheme，因为这里背景色是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        mSelectedPaint.setColor(0xffffc107);
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return true;
    }


    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;

        mSchemePaint.setColor(0xffffc107);//更改有scheme的背景
        canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
        mSchemePaint.setColor(0xffffffff);//上面盖一层有scheme的背景
        canvas.drawCircle(cx, cy , mRadius * 8f/9f, mSchemePaint);


//        if(calendar.isCurrentDay()){
//            mSchemePaint.setColor(0xffffccbb);
//            canvas.drawCircle(cx,cy,mRadius,mSchemePaint);
//        }
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int top = -mItemHeight / 8;
        int cy = mItemHeight / 2;
        int y=0;
        if (isSelected) {
            mSelectedPaint.setColor(0xffffc107);
            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
            mSelectTextPaint.setColor(0xffffffff);
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    mSelectTextPaint);
            mSelectedLunarTextPaint.setColor(0xffffffff);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine  + mItemHeight / 10, mSelectedLunarTextPaint);

        } else if (hasScheme) {
            //只要有Scheme统一字都是ffc107,而且外圈为ffc107
            mSchemeTextPaint.setColor(0xffffc107);
            mSchemeTextPaint.setColor(0xffffc107);
            mCurDayTextPaint.setColor(0xffffc107);
            mSchemeLunarTextPaint.setColor(0xffffc107);
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mSchemeTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + mItemHeight / 10, mSchemeLunarTextPaint);
        } else {

            mSchemePaint.setColor(calendar.isCurrentDay()?0xffffecb3:0xffffffff);
            canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
            mCurDayTextPaint.setColor(0xffffc107);
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
            mCurDayLunarTextPaint.setColor(0xffffc107);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10, mCurMonthLunarTextPaint);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10,
                    calendar.isCurrentDay() ? mCurDayLunarTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthLunarTextPaint : mOtherMonthLunarTextPaint);

        }
    }
}