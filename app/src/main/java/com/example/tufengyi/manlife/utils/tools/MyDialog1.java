package com.example.tufengyi.manlife.utils.tools;

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

public class MyDialog1 extends Dialog implements View.OnClickListener{

    private Context context;
    private int layoutResID;
    private int[] listenedItem;
    public MyDialog1(Context context,int layoutResID,int[] listenedItem){
        super(context, R.style.MyDialog);
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItem = listenedItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //提前设置Dialog的一些样式
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        //dialogWindow.setWindowAnimations;
        setContentView(layoutResID);

        WindowManager windowManager = ((Activity)context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5;//设置dialog的宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);//点击外部dialog消失
        //遍历控件id添加点击注册
        for(int id:listenedItem){
            findViewById(id).setOnClickListener(this);
        }
    }

    private OnCenterItemClickListener  listener;
    public interface OnCenterItemClickListener {
        void OnCenterItemClick(MyDialog1 dialog,View view);
    }
    //我们在这里写一个接口，然后添加一个方法
    public void setOnCenterItemClickListener(OnCenterItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v){
        dismiss();//只要按任何一个控件的id，弹窗都会消失，不管是确定还是取消
        listener.OnCenterItemClick(this,v);
    }
}
