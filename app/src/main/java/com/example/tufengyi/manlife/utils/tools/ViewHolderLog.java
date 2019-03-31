package com.example.tufengyi.manlife.utils.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tufengyi.manlife.R;

public class ViewHolderLog  extends RecyclerView.ViewHolder {

    public TextView tv_log;
    public ViewHolderLog(View view){
        super(view);
        tv_log = view.findViewById(R.id.tv_log);
    }
}
