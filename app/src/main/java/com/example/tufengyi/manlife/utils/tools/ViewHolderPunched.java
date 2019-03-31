package com.example.tufengyi.manlife.utils.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tufengyi.manlife.R;

public class ViewHolderPunched extends RecyclerView.ViewHolder {

    public LinearLayout ll_seeArt;
    public ImageView img_ass;
    public TextView tv_ass;
    public ViewHolderPunched(View view){
        super(view);
        ll_seeArt = (LinearLayout) view.findViewById(R.id.btn_seeArt);
        img_ass = (ImageView) view.findViewById(R.id.img_ass);
        tv_ass = (TextView) view.findViewById(R.id.tv_ass);
    }
}
