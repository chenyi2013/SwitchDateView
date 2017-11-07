package ired.dateswitchview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kevin on 17/11/6.
 */

public class DateSwitchView extends View implements ViewPager.OnPageChangeListener {

    private int showDateCount;
    private int currentIndex;
    private int tempIndex;

    private int clickIndex;

    private float dateFontSize;
    private float weekFontSize;
    private float meanWidth;

    private int dateSelectColor;
    private int dateUnSelectColor;

    private int weekSelectColor;
    private int weekUnSelectColor;
    private int graphColor;

    private float space1 = 10;
    private float space2 = 10;
    private float space3 = 10;
    private float r;
    private float rectHeight;
    private float dateFontHeight;
    private float weekFontHeight;

    private float topPadding;


    private Paint datePaint;
    private Paint weekPaint;
    private Paint graphPaint;
    private RectF rect = new RectF();
    private GraphData item;
    private float positionOffset;

    private List<GraphData> data;

    private OnItemClickListener onItemClickListener;
    private ViewPager viewPager;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        tempIndex = position;
        this.positionOffset = positionOffset * meanWidth;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        currentIndex = position;
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    public static interface OnItemClickListener {
        void onClick(int position, GraphData data);
    }

    public static class GraphData implements Serializable {
        public String date;
        public String week;
        public boolean hasProduct;

        public GraphData(String date, String week, boolean hasProduct) {
            this.date = date;
            this.week = week;
            this.hasProduct = hasProduct;
        }
    }


    public DateSwitchView(Context context) {
        super(context);
        init(context);
    }


    public DateSwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public DateSwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public void setData(List<GraphData> data) {
        this.data = data;
        invalidate();
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        this.tempIndex = currentIndex;
        invalidate();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void init(Context context) {


        dateSelectColor = Color.parseColor("#F44444");
        dateUnSelectColor = Color.parseColor("#333333");
        weekSelectColor = dateSelectColor;
        weekUnSelectColor = dateUnSelectColor;
        graphColor = dateSelectColor;

        dateFontSize = spToPx(context, 18);
        weekFontSize = spToPx(context, 10);
        topPadding = dpToPx(context, 10);

        showDateCount = 7;
        currentIndex = -1;

        space1 = dpToPx(context, 5);
        space2 = dpToPx(context, 8);
        space3 = dpToPx(context, 10);

        datePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        datePaint.setTextSize(dateFontSize);
        datePaint.setTextAlign(Paint.Align.CENTER);

        weekPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        weekPaint.setTextSize(weekFontSize);
        weekPaint.setTextAlign(Paint.Align.CENTER);

        graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        graphPaint.setColor(graphColor);

        r = dpToPx(context, 3);
        rectHeight = dpToPx(context, 3);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                for (int i = 0; i < showDateCount; i++) {
                    rect.set((i + 1) * meanWidth - meanWidth / 2, 0, (i + 1) * meanWidth + meanWidth / 2, getHeight());
                    if (rect.contains(x, y)) {
                        clickIndex = i;
                        return true;
                    }
                }

                clickIndex = -1;

                return true;

            case MotionEvent.ACTION_UP:

                for (int i = 0; i < showDateCount; i++) {
                    rect.set((i + 1) * meanWidth - meanWidth / 2, 0, (i + 1) * meanWidth + meanWidth / 2, getHeight());
                    if (rect.contains(x, y) && clickIndex == i) {
                        currentIndex = i;
                        invalidate();

                        if (viewPager != null) {
                            viewPager.setCurrentItem(i);
                        }

                        if (onItemClickListener != null && data != null && data.size() > i) {
                            onItemClickListener.onClick(i, data.get(i));
                        }
                        return true;
                    }
                }


                return true;
        }


        return super.onTouchEvent(event);
    }


    public void setViewPager(ViewPager view) {

        if (viewPager == view) {
            return;
        }
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(null);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        viewPager = view;
        viewPager.setCurrentItem(currentIndex);
        viewPager.addOnPageChangeListener(this);
        invalidate();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        dateFontHeight = getFontHeight("明天周一二三四五", dateFontSize);
        weekFontHeight = getFontHeight("0123456789", weekFontSize);
        int h = (int) (dateFontHeight + weekFontHeight + topPadding + space1 + space2 + space3 + 2 * r + rectHeight);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        meanWidth = w / (showDateCount + 1);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data == null || data.size() <= 0) {
            return;
        }

        int len = data.size();

        float left = 0.0f;
        float top = 0.0f;


        for (int i = 0; i < showDateCount; i++) {


            if (i >= len) {
                break;
            }

            item = data.get(i);

            left = (float) ((i + 1) * meanWidth);
            top = topPadding - datePaint.ascent();


            if (item.hasProduct) {
                canvas.drawCircle(left, topPadding + dateFontHeight + weekFontHeight + space1 + space2 + r, r, graphPaint);
            }

            if (currentIndex == i) {
                datePaint.setColor(dateSelectColor);
                weekPaint.setColor(weekSelectColor);
                canvas.drawText(item.date, left, top, datePaint);

                canvas.drawText(item.week, left, topPadding - weekPaint.ascent() + dateFontHeight + space1, weekPaint);

            } else {
                datePaint.setColor(dateUnSelectColor);
                canvas.drawText(item.date, left, top, datePaint);
                weekPaint.setColor(weekUnSelectColor);
                canvas.drawText(item.week, left, topPadding - weekPaint.ascent() + dateFontHeight + space1, weekPaint);

            }

            if (tempIndex == i) {
                canvas.drawRect(
                        (float) ((i + 1) * meanWidth) - meanWidth / 2 + positionOffset
                        , topPadding + dateFontHeight + weekFontHeight + space1 + space2 + space3 + 2 * r
                        , (float) ((i + 1) * meanWidth) + meanWidth / 2 + positionOffset
                        , topPadding + dateFontHeight + weekFontHeight + space1 + space2 + space3 + rectHeight + 2 * r, graphPaint);
            }

        }
    }


    private float getFontHeight(String text, float fontSize) {
        Rect rect = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }


    /**
     * 将dp换算成px
     *
     * @param context
     * @param dp
     * @return
     */
    public float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * 将sp换算成px
     *
     * @param context
     * @param sp
     * @return
     */
    public float spToPx(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }


    public int getShowDateCount() {
        return showDateCount;
    }

    public void setShowDateCount(int showDateCount) {
        this.showDateCount = showDateCount;
        invalidate();
    }

    public float getDateFontSize() {
        return dateFontSize;
    }

    public void setDateFontSize(float dateFontSize) {
        this.dateFontSize = dateFontSize;
        datePaint.setTextSize(dateFontSize);
        requestLayout();
        invalidate();
    }

    public float getWeekFontSize() {
        return weekFontSize;
    }

    public void setWeekFontSize(float weekFontSize) {
        this.weekFontSize = weekFontSize;
        weekPaint.setTextSize(weekFontSize);
        requestLayout();
        invalidate();
    }

    public int getDateSelectColor() {
        return dateSelectColor;
    }

    public void setDateSelectColor(int dateSelectColor) {
        this.dateSelectColor = dateSelectColor;
        invalidate();
    }

    public int getDateUnSelectColor() {
        return dateUnSelectColor;
    }

    public void setDateUnSelectColor(int dateUnSelectColor) {
        this.dateUnSelectColor = dateUnSelectColor;
        invalidate();
    }

    public int getWeekSelectColor() {
        return weekSelectColor;
    }

    public void setWeekSelectColor(int weekSelectColor) {
        this.weekSelectColor = weekSelectColor;
        invalidate();
    }

    public int getWeekUnSelectColor() {
        return weekUnSelectColor;
    }

    public void setWeekUnSelectColor(int weekUnSelectColor) {
        this.weekUnSelectColor = weekUnSelectColor;
        invalidate();
    }

    public int getGraphColor() {
        return graphColor;
    }

    public void setGraphColor(int graphColor) {
        this.graphColor = graphColor;
        invalidate();
    }

    public float getSpace1() {
        return space1;
    }

    public void setSpace1(float space1) {
        this.space1 = space1;
        requestLayout();
        invalidate();
    }

    public float getSpace2() {
        return space2;
    }

    public void setSpace2(float space2) {
        this.space2 = space2;
        requestLayout();
        invalidate();
    }

    public float getSpace3() {
        return space3;
    }

    public void setSpace3(float space3) {
        this.space3 = space3;
        requestLayout();
        invalidate();
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
        requestLayout();
        invalidate();
    }

    public float getRectHeight() {
        return rectHeight;
    }

    public void setRectHeight(float rectHeight) {
        this.rectHeight = rectHeight;
        requestLayout();
        invalidate();
    }

    public float getTopPadding() {
        return topPadding;
    }

    public void setTopPadding(float topPadding) {
        this.topPadding = topPadding;
        requestLayout();
        invalidate();
    }


}
