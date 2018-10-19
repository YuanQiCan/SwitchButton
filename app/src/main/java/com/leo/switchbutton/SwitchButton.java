package com.leo.switchbutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by leo on 2018/10/18.
 */
public class SwitchButton extends View implements View.OnTouchListener {



    private int leftDis = 0;
    //标记最大滑动
    private int slidingMax;
    //标记按钮开关状态
    private boolean mCurrent = false;
    //标记是否点击事件
    private boolean isClickable;
    //标记是否移动
    private boolean isMove;
    //"开"事件监听器
    private SoftFloorListener softFloorListener;
    //"关"事件监听器
    private HydropowerListener hydropowerListener;

    //标记开关文本的宽度
    float width1, width2;
    //记录文本中心点 cx1:绘制文本1的x坐标  cx2:绘制文本2的x坐标
    //cy记录绘制文本的高度
    float cx1, cy, cx2;
    /*参数属性值*/

    String leftText;//左边文字

    String rightText;//右边文字

    float textSize;//定义文本大小

    int checkedTextColor;//选中文字颜色

    int unCheckedTextColor;//未选中文字颜色

    int backColor; //背景颜色

    int btnColor;//按钮颜色

    int backStrokeColor;//背景边框颜色

    int btnStrokeColor;//按钮边框颜色

    float strokeWidth;//边框宽度

    int roundRadius;//边框弧度


    Paint rectBackPaint;//背景画笔

    Paint rectBtnPaint; //按钮画笔

    private Paint textPaint;//文字画笔



    float backRectWidht;
    float backRectHeight;
    float padingwidth = 5;
    float btnrectWidth;
    float btnrectHight;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context,attrs);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    private void initData(Context context,AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.SwitchButton);

        leftText = a.getString(R.styleable.SwitchButton_leftText);
        rightText = a.getString(R.styleable.SwitchButton_rightText);
        textSize = a.getDimension(R.styleable.SwitchButton_btnTextSize,35);


        btnColor = a.getColor(R.styleable.SwitchButton_btnColor,Color.GREEN);
        backColor = a.getColor(R.styleable.SwitchButton_backColor,Color.WHITE);
        checkedTextColor = a.getColor(R.styleable.SwitchButton_checkedTextColor,Color.WHITE);
        unCheckedTextColor = a.getColor(R.styleable.SwitchButton_unCheckedTextColor,Color.BLACK);

        backStrokeColor = a.getColor(R.styleable.SwitchButton_backStrokeColor,Color.BLACK);
        btnStrokeColor = a.getColor(R.styleable.SwitchButton_btnStrokeColor,Color.BLACK);

        strokeWidth = a.getDimension(R.styleable.SwitchButton_strokeWidth,1);
        roundRadius = a.getInteger(R.styleable.SwitchButton_roundRadius,50);
        a.recycle();
    }


    private void initView()
    {
        rectBackPaint = new Paint();
        rectBackPaint.setStrokeWidth(strokeWidth);
        rectBackPaint.setAntiAlias(true);

        rectBtnPaint = new Paint();
        rectBtnPaint.setStrokeWidth(strokeWidth);
        rectBtnPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        width1 = textPaint.measureText(leftText);
        width2 = textPaint.measureText(rightText);

        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        backRectWidht = measureWidth(widthMeasureSpec); //获取组件宽度
        backRectHeight = measureHeight(heightMeasureSpec);//获取组件高度

        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        btnrectWidth = (backRectWidht- padingwidth*2)/2;
        btnrectHight = backRectHeight - padingwidth*2;

        slidingMax = (int) btnrectWidth;
        float yiban = strokeWidth/2;
        rectBackPaint.setStyle(Paint.Style.FILL);
        rectBackPaint.setColor(backColor);
        canvas.drawRoundRect(yiban ,yiban,backRectWidht-yiban,backRectHeight-yiban,roundRadius,roundRadius,rectBackPaint);
        rectBackPaint.setStyle(Paint.Style.STROKE);
        rectBackPaint.setColor(backStrokeColor);
        canvas.drawRoundRect(yiban,yiban,backRectWidht-yiban,backRectHeight-yiban,roundRadius,roundRadius,rectBackPaint);

        rectBtnPaint.setStyle(Paint.Style.FILL);
        rectBtnPaint.setColor(btnColor);
        canvas.drawRoundRect(padingwidth+leftDis,padingwidth,btnrectWidth+leftDis+padingwidth,btnrectHight+padingwidth,roundRadius,roundRadius,rectBtnPaint);

        rectBtnPaint.setStyle(Paint.Style.STROKE);
        rectBtnPaint.setColor(btnStrokeColor);
        canvas.drawRoundRect(padingwidth+leftDis,padingwidth,btnrectWidth+leftDis+padingwidth,btnrectHight+padingwidth,roundRadius,roundRadius,rectBtnPaint);

        cx1 = btnrectWidth/2 - width1/2;

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        textPaint.setTextAlign(Paint.Align.LEFT);
        cy = backRectHeight/2 -( fontMetrics.top+fontMetrics.bottom)/2;

        cx2 = backRectWidht/2 + (btnrectWidth-width2)/2;
        if (mCurrent)
        {
            textPaint.setColor(checkedTextColor);
            canvas.drawText(rightText,cx2,cy,textPaint);
            textPaint.setColor(unCheckedTextColor);
            canvas.drawText(leftText,cx1,cy,textPaint);
        }else
        {
            textPaint.setColor(checkedTextColor);
            canvas.drawText(leftText,cx1,cy,textPaint);
            textPaint.setColor(unCheckedTextColor);
            canvas.drawText(rightText,cx2,cy,textPaint);
        }
    }

    private void flushView()
    {
        mCurrent = !mCurrent;
        if (mCurrent)
        {
            leftDis = slidingMax;
            if (hydropowerListener != null)
            {
                hydropowerListener.hydropower();
            }
        }else
        {
            leftDis = 0 ;
            if (softFloorListener != null)
            {
                softFloorListener.softFloor();
            }
        }
        invalidate();
    }

    //startX 标记按下的X坐标,  lastX标记移动后的X坐标 ,disX移动的距离
    float startX, lastX, disX;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                isClickable = true;
                startX = event.getX();
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                lastX = event.getX();
                disX = lastX - startX;
                if (Math.abs(disX) < 5) break;
                isMove = true;
                isClickable = false;
                moveBtn();
                startX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (isClickable) {
                    flushView();
                }
                if (isMove) {
                    if (leftDis > slidingMax / 2) {
                        mCurrent = false;
                    } else {
                        mCurrent = true;
                    }
                    flushView();
                }
                break;



        }
        return true;
    }

    //移动后判断位置
    private void moveBtn() {
        leftDis += disX;
        if (leftDis > slidingMax) {
            leftDis = slidingMax;
        } else if (leftDis < 0) {
            leftDis = 0;
        }
        invalidate();
    }


    public void setSoftFloorListener(SoftFloorListener softFloorListener)
    {
        this.softFloorListener = softFloorListener;
    }

    public void setHydropowerListener(HydropowerListener hydropowerListener)
    {
        this.hydropowerListener = hydropowerListener;
    }

    public interface SoftFloorListener{
        void softFloor();
    }

    public interface HydropowerListener{
        void hydropower();
    }


    private  int measureWidth(int measureSpec)
    {
        int result = 0 ;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY)
        {
            result = specSize;

        }else{
            result = 200;
            if (specMode == MeasureSpec.AT_MOST)
            {
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    private  int measureHeight(int measureSpec)
    {
        int result = 0 ;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY)
        {
            result = specSize;

        }else{
            result = 60;
            if (specMode == MeasureSpec.AT_MOST)
            {
                result = Math.min(result,specSize);
            }
        }
        return result;
    }
}
