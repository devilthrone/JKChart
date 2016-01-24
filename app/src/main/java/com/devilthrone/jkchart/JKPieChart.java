package com.devilthrone.jkchart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by JK on 16/1/16.
 * 这是一个饼状图
 */
public class JKPieChart extends View {

    public static final float ANIMATION_SPEED_DEFAULT = 6.5f; //默认速度
    private int maxValue; //最大值
    private boolean mIsShowTitle; //是否显示标题
    private boolean mIsRound; //实心圆 还是 甜甜圈
    private boolean mIsShowShadow = true;
    private int shadowBackgroundColor;
    private int mBackgroundColor; //背景颜色

    private int mHintTitleColor; //右边提示块 文字颜色
    private float mHintTitleSize; //右边提示块 文字大小
    private Context mContext;
    private Typeface mTypeface = null;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private float mAnimationSpeed = 6.5f;

    private float mCurrentAngle = 0.0f;

    private float mPadding = 15; //饼图和右边提示部分的间距

    private float radio = 0.25f;
    private List<PieChartItem> mItemList;


    private Paint mHintTextPaint;
    private Paint mInsideShadowPaint;
    private Paint mInsideChartPaint;

    public JKPieChart(Context context) {
        this(context, null);
    }

    public JKPieChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JKPieChart(Context context, List<PieChartItem> list, int maxValue, boolean isRound) {
        super(context);
        init(context, list, maxValue, isRound, true, Color.parseColor("#DDDDDD"), Color.parseColor("#FFFFFF"));
    }

    public JKPieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JKPieChart, 0, 0);
        try {
            boolean isRound = typedArray.getBoolean(R.styleable.JKPieChart_isRound, false);
            boolean showTitle = typedArray.getBoolean(R.styleable.JKPieChart_showTitle, false);
            mHintTitleColor = typedArray.getColor(R.styleable.JKPieChart_hintTitleColor, Color.BLACK);
            mHintTitleSize = typedArray.getDimensionPixelSize(R.styleable.JKPieChart_hintTitleSize, 18);
            int backgroundColor;
            int[] indices = new int[]{android.R.attr.background};
            typedArray = context.obtainStyledAttributes(attrs, indices);
            backgroundColor = typedArray.getInt(typedArray.getIndex(0), Color.parseColor("#FFFFFF"));
            init(context, null, 100, isRound, showTitle, Color.parseColor("#DDDDDD"), backgroundColor);
        } finally {
            typedArray.recycle();
        }


    }

    private void init(Context context, List<PieChartItem> itemsList, int maxValue, boolean isRound, boolean showTitle, int shadowColor, int backgroundColor) {
        this.mContext = context;
        this.mItemList = itemsList;
        this.mIsRound = isRound;
        this.shadowBackgroundColor = shadowColor;
        this.mBackgroundColor = backgroundColor;
        this.maxValue = maxValue;
        this.mIsShowTitle = showTitle;
        initPaint();
    }

    private void initPaint() {
        mInsideShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInsideShadowPaint.setColor(shadowBackgroundColor);
        mInsideChartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInsideChartPaint.setColor(mBackgroundColor);

        mHintTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHintTextPaint.setColor(mHintTitleColor);
        mHintTextPaint.setTextSize(mHintTitleSize);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = screenWidth;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        this.mWidth = result;
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = screenHeight;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        this.mHeight = result;
        return result;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if (mWidth != mHeight) {
            mWidth = mHeight = Math.min(mHeight, mWidth);
        }
        if (mIsShowTitle) {
            mRadius = (int) (mWidth * (1 - radio));
        }
        animatedDraw(canvas);
        canvas.restore();
    }


    public void setChartItemsList(List<PieChartItem> itemsList) {
        this.mItemList = itemsList;
        invalidate();
    }

    public void setShadowBackgroundColor(int color) {
        this.shadowBackgroundColor = color;
        invalidate();
    }

    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate();
    }

    private float getPercent(int value, int maxValue) {
        float result = (float) value / maxValue;
        return result;
    }

    private void animatedDraw(Canvas canvas) {
        RectF rect = new RectF();
        if (mIsShowTitle) {
            rect.set(10, 10 + mWidth * radio / 2, mRadius - 10, mHeight - 10 - mWidth * radio / 2);
        } else {
            rect.set(10, 10, mWidth - 10, mHeight - 10);
        }
        //画外圈圆环
        drawOutSideCircle(canvas, mInsideShadowPaint, mInsideChartPaint, rect);
        //canvas.rotate(-90f, rect.centerX(), rect.centerY());

        if (mItemList != null && maxValue > 0) {
            //动画式画弧度
            drawItems(canvas, rect);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(mBackgroundColor);
            RectF oval = new RectF();
            if (mIsShowTitle) {
                oval.set(8, 8 + mWidth * radio / 2, mRadius - 8, mHeight - 8 - mWidth * radio / 2);
            } else {
                oval.set(8, 8, mWidth - 8, mHeight - 8);
            }

            Path path = new Path();
            path.moveTo(oval.centerX(), oval.centerY());
            path.addArc(oval, mCurrentAngle, 360.0f - mCurrentAngle);
            path.lineTo(oval.centerX(), oval.centerY());
            canvas.drawPath(path, paint);
            mCurrentAngle += mAnimationSpeed;
            drawInsideCircle(canvas, mInsideShadowPaint, mInsideChartPaint);
            if (mIsShowTitle) {
                //canvas.rotate(90f, rect.centerX(), rect.centerY());
                drawHintText(canvas);
            }
            if (mCurrentAngle >= 360) {
                mCurrentAngle = 0.0f;
                //canvas.rotate(-90f, rect.centerX(), rect.centerY());
                drawItems(canvas, rect);
                //canvas.rotate(90f, rect.centerX(), rect.centerY());
                drawInsideCircle(canvas, mInsideShadowPaint, mInsideChartPaint);
                return;
            }
            invalidate();
        }
    }

    /**
     * 画右边的提示块块和文字
     *
     * @param canvas
     */
    private void drawHintText(Canvas canvas) {
        float x = mWidth * (1 - radio) + 3 * mPadding;
//        Paint OutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        OutPaint.setColor(Color.RED);
        RectF outRect = new RectF(mWidth * (1 - radio) + mPadding, mHeight * radio, mWidth, mHeight * (1 - radio));
        //canvas.drawRect(outRect, OutPaint);


        int size = mItemList.size();
        for (int i = 0; i < size; ++i) {
            PieChartItem currentItem = mItemList.get(i);
            String title = currentItem.title;
            int color = currentItem.color;
            Rect textRect = new Rect();
            mHintTextPaint.getTextBounds(title, 0, title.length(), textRect);
            float rectWidth = textRect.height();  //小方块的宽高等于文字的高度
            float rectHeight = textRect.height();
            float baseline = outRect.top + (i + 1) * (outRect.height() / size);
            mHintTextPaint.setColor(color);
            canvas.drawRect(x + mPadding, baseline - textRect.height() - (outRect.height() / size - textRect.height()) / 2, x + mPadding + textRect.height(), baseline - (outRect.height() / size - textRect.height()) / 2, mHintTextPaint);
            canvas.drawText(title, x + mPadding * 1.5f + rectWidth, baseline - textRect.height() - (outRect.height() / size - textRect.height()) / 2 + rectHeight, mHintTextPaint);

        }
    }

    private void drawInsideCircle(Canvas canvas, Paint insideShadowPaint, Paint insideChartPaint) {
        if (!mIsRound) {
            //canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 4 - 10, insideShadowPaint);
            if (mIsShowTitle) {
                canvas.drawCircle(mWidth * (1 - radio) / 2, mHeight / 2, mWidth * (1 - radio) / 4 - 20, insideChartPaint);
            } else {
                canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 4 - 20, insideChartPaint);
            }

        }
    }

    private void drawOutSideCircle(Canvas canvas, Paint insideShadowPaint, Paint insideChartPaint, RectF mainRectangle) {
        //
        // canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, insideShadowPaint);
        canvas.drawArc(mainRectangle, 0f, 360f, true, insideChartPaint);
    }

    private void drawItems(Canvas canvas, RectF mainRectangle) {
        float startAngle = 0f;
        float anglesSum = 0f;
        Paint currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < mItemList.size(); ++i) {
            PieChartItem currentItem = mItemList.get(i);
            int color = currentItem.color;
            String title = currentItem.title;
            int value = currentItem.value;
            float currentPercentValue = getPercent(value, maxValue);
            float currentAngle = currentPercentValue * 360;
            anglesSum += currentAngle;
            currentPaint.setColor(color);
            canvas.drawArc(mainRectangle, startAngle, currentAngle, true, currentPaint);
            startAngle += currentAngle;
        }
    }


}
