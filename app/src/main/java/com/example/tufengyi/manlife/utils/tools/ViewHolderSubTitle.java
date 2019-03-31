package com.example.tufengyi.manlife.utils.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.tufengyi.manlife.R;

public class ViewHolderSubTitle extends RecyclerView.ViewHolder{
    public TextView tv_subTitle ;
    public ViewHolderSubTitle(View view){
        super(view);
        tv_subTitle = (TextView) view.findViewById(R.id.tv_date);
    }
}
