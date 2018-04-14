package com.viclee.barrageviewdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


public class BarrageActivity extends Activity {

    private Button btn_show;
    private Context mContext;
    private View message_view ;
    private View rope_view ;
    private TextView letter_view ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrage);
        mContext = BarrageActivity.this;
        btn_show = (Button) findViewById(R.id.button);
        message_view = findViewById(R.id.vm_message);
        rope_view = findViewById(R.id.view);
        letter_view = (TextView) findViewById(R.id.textView);
        letter_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopWindow(view , R.layout.activity_pop_letter, -100 , 500);
            }
        });
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopWindow(v,R.layout.activity_pop_window,-100,0);
            }
        });
        message_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopWindow(view,R.layout.activity_pop_message,50,100);
            }
        });

        rope_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopWindow(view,R.layout.activity_pop_rope,-100,300);
            }
        });

    }



    private void initPopWindow(View v , int sour , int x ,int y ) {
        View view = LayoutInflater.from(mContext).inflate(sour, null, false);

        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(view, x, y);
    }
}
