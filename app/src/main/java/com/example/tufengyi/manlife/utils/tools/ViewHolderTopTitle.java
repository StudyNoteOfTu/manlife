package com.example.tufengyi.manlife.utils.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.tufengyi.manlife.R;

public class ViewHolderTopTitle extends RecyclerView.ViewHolder{
    public TextView tv_topTitle;
    public ViewHolderTopTitle(View view){
        super(view);
        tv_topTitle = (TextView) view.findViewById(R.id.tv_date);
    }
}
