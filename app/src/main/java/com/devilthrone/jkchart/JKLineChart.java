package com.devilthrone.jkchart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by JK on 16/1/18.
 * 这是一个点阵折线图
 */
public class JKLineChart extends View {

    private static final String TAG = "JKLineChart";
    private final double EP = 0.000000001;
    private Context mContext;
    private Paint mCoordPaint;//坐标线的Paint
    private Paint mIndicPaint;  //点图的Paint
    private Paint mHintTitlePaint; //提示块的Paint
    private int mWidth;
    private int mHeight;
    private int mCoordColor = Color.GRAY; //坐标线的color
    private int mPadding = 20;
    private int mGapHeight = 50;
    private float mTextX = 0f; //第一个方块的X坐标（和坐标上的字分离）
    private float[] mMinAndMaxPointY = new float[2]; //最低点和最高点的Y坐标（参考点 用来计算方块的坐标）
    private List<LineChartItem> mItemList; //数据集合
    private List<LineChartItem> mTypeSet;
    private float[] values;
    private IndicatorType mIndicatorType = IndicatorType.RECTANGLE;
    private int mGap = 5; //默认迭代间距
    private int mIndicWidth = 5; //小方块的宽度 或者小圆圈的直径
    private int mInitWidth = 0;
    private int mSpeed = 1;


    private boolean isShowHintBlock = true; //是否显示底下的提示模块
    private int mHintTitleHeight = 30; //提示模块高度

    public JKLineChart(Context context) {
        this(context, null);
    }

    public JKLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JKLineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JKLineChart, 0, 0);
        try {
            isShowHintBlock = typedArray.getBoolean(R.styleable.JKLineChart_showHintBlock, true);
            mGap = typedArray.getInt(R.styleable.JKLineChart_coordGap, mGap);
            int isRound = typedArray.getInt(R.styleable.JKLineChart_indicatorType, 1);
            Log.d(TAG, "ROUND :" + isRound);
            if (isRound == 0) {
                mIndicatorType = IndicatorType.CIRCLE;
            } else {
                mIndicatorType = IndicatorType.RECTANGLE;
            }

        } finally {
            typedArray.recycle();
        }

        init(context);
    }

    /*
* 根据手机的分辨率从 dp 的单位 转成为 px(像素)
*/
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public void setItemList(List<LineChartItem> list) {
        if (list.size() == 0 || list == null)
            return;
        mItemList.clear();
        mItemList.addAll(list);
        int size = mItemList.size();
        values = new float[mItemList.size()];
        for (int i = 0; i < size; i++) {
            values[i] = mItemList.get(i).value;
        }
        computerHeight();
        invalidate();
    }

    //计算布局的高度
    private void computerHeight() {
        float[] array = getMinAndMaxCoord();
        //坐标轴总数量
        float totalCount = (array[1] - array[0]) / mGap;
        //上面折线图的最大Y坐标
        float y = mPadding * 3 + mGapHeight * totalCount;
        //下面提示部分的起始Y坐标
        float startY = y + mPadding * 3;
        int height = (int) (startY + mIndicWidth * 2 + mPadding * 2);
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = height;
        this.setLayoutParams(lp);
        Log.d(TAG, "HEIGHT:" + height);

    }

    private void init(Context context) {
        this.mContext = context;
        mItemList = new ArrayList<LineChartItem>();
        mIndicWidth = dip2px(context, mIndicWidth);
        mHintTitleHeight = dip2px(context, 40);
        mGapHeight = dip2px(context, mGapHeight);
        initPaint();
    }

    private void initPaint() {
        mIndicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCoordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHintTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCoordPaint.setColor(mCoordColor);
        mCoordPaint.setTextSize(40);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.mWidth = width;
        this.mHeight = height;
        if (isShowHintBlock) {
            mHeight = mHeight - mHintTitleHeight;
        }
        Log.d(TAG, "onSizeChanged");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        animatedDraw(canvas);
        canvas.restore();
    }

    private void animatedDraw(Canvas canvas) {
        if (mItemList == null || mItemList.size() == 0)
            return;

        drawCoordText(canvas);
        drawPoint(canvas);
        if (isShowHintBlock) {
            drawHintTitle(canvas);
        }

    }

    public void getHintTitleList() {
        mTypeSet = new ArrayList<LineChartItem>(mItemList);
        for (int i = 0; i < mTypeSet.size() - 1; i++) {
            for (int j = mTypeSet.size() - 1; j > i; j--) {
                if (mTypeSet.get(j).title.equals(mTypeSet.get(i).title)) {
                    mTypeSet.remove(j);
                }
            }
        }
        Collections.sort(mTypeSet, new Comparator<LineChartItem>() {
            @Override
            public int compare(LineChartItem lhs, LineChartItem rhs) {
                return -(Float.compare(lhs.value, rhs.value));
            }
        });
    }

    private void drawHintTitle(Canvas canvas) {
        getHintTitleList();
        int totalWidth = mWidth - mPadding * 2;
        int width = totalWidth / mTypeSet.size();
        float startY = mMinAndMaxPointY[0] + mPadding * 3;
        for (int i = 0; i < mTypeSet.size(); i++) {
            //draw
            LineChartItem type = mTypeSet.get(i);
            String text = type.title;
            int color = type.color;
            mHintTitlePaint.setColor(color);
            mHintTitlePaint.setTextSize(40);
            Rect textRect = new Rect();
            mHintTitlePaint.getTextBounds(text, 0, text.length(), textRect);
            float x = (width - (mIndicWidth * 2 + mPadding + textRect.width())) / 2 + i * width;
            RectF indicRect = new RectF(x + mPadding, startY, x + mIndicWidth * 2 + mPadding, startY + mIndicWidth * 2);
            canvas.drawRect(indicRect, mHintTitlePaint);
            mHintTitlePaint.setColor(mCoordColor);
            canvas.drawText(text, mPadding * 3 + x, startY + mIndicWidth * 2, mHintTitlePaint);
        }

    }

    /**
     * 画小方块
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {
        float totalWidth = mWidth - 2 * mPadding - mTextX;  //所有方块所能占据的总面积
        int size = values.length;
        for (int i = 0; i < size; i++) {
            float value = values[i];
            int color = mItemList.get(i).color;
            mIndicPaint.setColor(color);
            mIndicPaint.setTextSize(40);
            String text = value + "";
            Rect textRect = new Rect();
            mIndicPaint.getTextBounds(text, 0, text.length(), textRect);
            if (mIndicatorType == IndicatorType.RECTANGLE) {
                float top = getPointYByValue(value);
                float left = mPadding + mTextX + totalWidth / size * i + (totalWidth / size - mInitWidth) / 2;
                float right = left + mInitWidth;
                float bottom = top + mInitWidth;
                RectF indicRect = new RectF(left, top, right, bottom);
                canvas.drawText(text, left, top - textRect.height(), mIndicPaint);
                canvas.drawRect(indicRect, mIndicPaint);
            }
        }
        if (mInitWidth <= mIndicWidth) {
            mInitWidth += mSpeed;
            invalidate();
        }
    }

    /**
     * 根据值来计算方块的y坐标
     *
     * @param value
     * @return
     */
    private float getPointYByValue(float value) {
        float[] array = getMinAndMaxCoord();
        float diffY = mMinAndMaxPointY[0] - mMinAndMaxPointY[1]; //坐标值之间的差值
        float diffValue = array[1] - array[0];
        float y = mMinAndMaxPointY[1] + diffY / diffValue * (array[1] - value) - mIndicWidth * 3 / 2;
        return y;
    }

    /**
     * 画坐标线
     *
     * @param canvas
     */
    private void drawCoordText(Canvas canvas) {
        //获取坐标上下限的值
        float[] array = getMinAndMaxCoord();
        //坐标轴总数量
        float totalCount = (array[1] - array[0]) / mGap + 1;
        for (float i = array[1], count = 0; i >= array[0]; i = i - mGap, count++) {
            String text = (int) i + "";
            Rect textRect = new Rect();
            mCoordPaint.getTextBounds(text, 0, text.length(), textRect);
            //float y = (mHeight - 2 * mPadding) / totalCount * (count + 0.5f) + mPadding;
            float y = mPadding * 3 + mGapHeight * count;
            if (count == 0) {
                mMinAndMaxPointY[1] = y; //最高点的坐标
            } else if (count == totalCount - 1) {
                mMinAndMaxPointY[0] = y; //最低点的坐标
            }
            canvas.drawText(text, mPadding, y, mCoordPaint);
            //canvas.drawLine(mPadding,y,mWidth,y,mCoordPaint);
            //画虚线坐标线
            PathEffect effects = new DashPathEffect(new float[]{5, 10}, 1);
            Path path = new Path();
            path.moveTo(mPadding * 2f + textRect.width(), y - textRect.height() / 2);
            if (mTextX >= -EP && mTextX <= EP) {
                mTextX = mPadding * 2f + textRect.width();
            }
            path.lineTo(mWidth - mPadding, y - textRect.height() / 2);
            mCoordPaint.setStyle(Paint.Style.STROKE);
            mCoordPaint.setPathEffect(effects);
            canvas.drawPath(path, mCoordPaint);

        }
    }

    /**
     * 动态计算最小坐标值和最大坐标值x
     * ps:{4.9f,6f,7f,8f,9.1f}  坐标最小值为  = 0; 坐标最大值 = 10;
     *
     * @return
     */
    private float[] getMinAndMaxCoord() {
        float[] array = getMaxAndMin(values);
        float[] result = new float[2];
        float min = array[0];
        float max = array[1];
        result[0] = (Math.round((min - 0.5f)) / mGap * mGap); //坐标值最小值
        result[1] = ((int) (floor((max + mGap - 0.5f)) / mGap) * mGap); //坐标值最大值
        return result;
    }

    /**
     * 重写floor除法
     *
     * @param value
     * @return
     */
    private float floor(float value) {
        float result = 0f;
        float tail = value % 1;
        int integer = (int) (value - tail);
        if (Float.compare(tail, 0.5f) > 0) {
            return integer + 1;
        } else if (Float.compare(tail, 0.5f) <= 0) {
            return integer;
        }
        return result;
    }

    private float[] getMaxAndMin(float[] array) {
        float min, max;
        min = max = array[0];
        float[] result = new float[2];
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
            if (Float.compare(array[i], max) > 0)   // 判断最大值
                max = array[i];
            if (Float.compare(array[i], min) < 0)   // 判断最小值
                min = array[i];
        }
        result[0] = min;
        result[1] = max;
        return result;
    }

    private String formatText(String text) {
        BigDecimal bd = new BigDecimal(text);
        bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        return String.valueOf(bd.floatValue());
    }

    enum IndicatorType {  //点阵图 图形类型
        CIRCLE, //圆
        RECTANGLE //方块
    }
}