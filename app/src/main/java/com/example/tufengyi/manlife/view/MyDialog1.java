package com.example.tufengyi.manlife.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.tufengyi.manlife.R;

public class MyDialog1 extends Dialog implements View.OnClickListener {

    //构造方法里提前加载了样式
    private Context context;//上下文
    private int layoutResId;//布局文件id
   // private int[] listenedItem;//监听的控件id
    public MyDialog1(Context context,int layoutResId){
        super(context,R.style.MyDialog);//加载dialog样式
        this.context = context;
        this.layoutResId = layoutResId;
        //this.listenedItem = listenedItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //提前设置Dialog的一些样式
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//居中
        //dialogWindow.setWindowAnimations();//设置动画效果
        setContentView(layoutResId);

        WindowManager windowManager = ((Activity)context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5;  //设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失

        //遍历控件id添加点击注册，这里可因需求更改
//        for(int id:listenedItem){
//            findViewById(id).setOnClickListener(this);
//        }
    }

//    private OnCenterItemClickListener listener;
//    public interface OnCenterItemClickListener{
//        void OnCenterItemClick(MyDialog1 dialog,View view);
//    }

    //我们需要在这里写个接口，然后添加一个方法
//    public void setOnCenterItemClickListener(OnCenterItemClickListener listener){
//        this.listener = listener;
//    }

    @Override
    public void onClick(View v){
        dismiss();//关闭，即只要按了弹窗就会消失
        //listener.OnCenterItemClick(this,v);
    }



}
