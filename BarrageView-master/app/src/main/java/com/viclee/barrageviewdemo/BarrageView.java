package com.viclee.barrageviewdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class BarrageView extends RelativeLayout {
    private Context mContext;
    private BarrageHandler mHandler = new BarrageHandler();
    private Random random = new Random(System.currentTimeMillis());
    private static final long BARRAGE_GAP_MIN_DURATION = 10000;//两个弹幕的最小间隔时间
    private static final long BARRAGE_GAP_MAX_DURATION = 15000;//两个弹幕的最大间隔时间
    private int maxSpeed = 30000;//速度，ms
    private int minSpeed = 10000;//速度，ms
    private int maxSize = 30;//文字大小，dp
    private int minSize = 15;//文字大小，dp

    private int totalHeight = 0;
    private int lineHeight = 0;//每一行弹幕的高度
    private int totalLine = 0;//弹幕的行数
    private String[] itemText = {"这是数据库里的数据", "心情。。。。", "哈哈哈哈哈哈哈", "开心。。。。。。", "************", "数据。。。。",
            "我不会轻易的狗带", "作死。。。。。。。", "这是我见过的最长长长长长长长长长长长的评论"};
    private int textCount;
//    private List<BarrageItem> itemList = new ArrayList<BarrageItem>();

    public BarrageView(Context context) {
        this(context, null);
    }

    public BarrageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        textCount = itemText.length;

        int duration = (int) ((BARRAGE_GAP_MAX_DURATION - BARRAGE_GAP_MIN_DURATION) * Math.random());
        mHandler.sendEmptyMessageDelayed(0, duration);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        totalHeight = getMeasuredHeight();
        lineHeight = getLineHeight();
        totalLine = totalHeight / lineHeight;
    }

    private void generateItem() {
        BarrageItem item = new BarrageItem();
        String tx = itemText[(int) (Math.random() * textCount)];
        int sz = (int) (minSize + (maxSize - minSize) * Math.random());
        item.textView = new TextView(mContext);
        item.textView.setOnClickListener(new OnClickListener() {
            //点击出现窗口
            @Override
            public void onClick(View v) {
                initPopWindow(v);
//                Toast.makeText(getContext(), "这里是一个框。。。",Toast.LENGTH_SHORT).show();
            }
        });
        item.textView.setText(tx);
        item.textView.setTextSize(sz);
        item.textView.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        item.textMeasuredWidth = (int) getTextWidth(item, tx, sz);
        item.moveSpeed = (int) (minSpeed + (maxSpeed - minSpeed) * Math.random());
        if (totalLine == 0) {
            totalHeight = getMeasuredHeight();
            lineHeight = getLineHeight();
            totalLine = totalHeight / lineHeight;
        }
        item.verticalPos = random.nextInt(totalLine) * lineHeight;
//        itemList.add(item);
        showBarrageItem(item);
    }

    private void showBarrageItem(final BarrageItem item) {

        int leftMargin = this.getRight() - this.getLeft() - this.getPaddingLeft();

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = item.verticalPos;
        this.addView(item.textView, params);
        ObjectAnimator anim = generateTranslateAnim(item, leftMargin);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
          public void onAnimationStart(Animator animation) {
          }

          @Override
          public void onAnimationRepeat(Animator animation) {
          }

          @Override
          public void onAnimationEnd(Animator animation) {
              item.textView.clearAnimation();
              BarrageView.this.removeView(item.textView);
          }

          @Override
          public void onAnimationCancel(Animator animation) {
          }
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                item.textView.clearAnimation();
//                BarrageView.this.removeView(item.textView);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
        });
//        item.textView.startAnimation(anim);
        anim.start();
    }
    private void initPopWindow(View v) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_pop_textview, null, false);

        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v, 000, -800);
    }
    private ObjectAnimator generateTranslateAnim(BarrageItem item, int leftMargin) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(item.textView ,"translationX",-item.textMeasuredWidth,leftMargin);//;leftMargin, -item.textMeasuredWidth, 0, 0);
//        TranslateAnimation anim = new  TranslateAnimation(leftMargin, -item.textMeasuredWidth, 0, 0);
        //从（leftMargin，0）到（-item.textMeasuredWidth,0）
        anim.setDuration(item.moveSpeed);//持续时间
        anim.setInterpolator(new AccelerateDecelerateInterpolator());//设置加速曲线
//        anim.setFillAfter(true);//执行完保持不变
        return anim;
    }

    /**
     * 计算TextView中字符串的长度
     *
     * @param text 要计算的字符串
     * @param Size 字体大小
     * @return TextView中字符串的长度
     */
    public float getTextWidth(BarrageItem item, String text, float Size) {
        Rect bounds = new Rect();
        TextPaint paint;
        paint = item.textView.getPaint();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    /**
     * 获得每一行弹幕的最大高度
     *
     * @return
     */
    private int getLineHeight() {
        BarrageItem item = new BarrageItem();
        String tx = itemText[0];
        item.textView = new TextView(mContext);
        item.textView.setText(tx);
        item.textView.setTextSize(maxSize);

        Rect bounds = new Rect();
        TextPaint paint;
        paint = item.textView.getPaint();
        paint.getTextBounds(tx, 0, tx.length(), bounds);
        return bounds.height();
    }

    class BarrageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            generateItem();
            //每个弹幕产生的间隔时间随机
            int duration = (int) ((BARRAGE_GAP_MAX_DURATION - BARRAGE_GAP_MIN_DURATION) * Math.random());
            this.sendEmptyMessageDelayed(0, duration);
        }
    }

}
