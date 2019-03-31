package com.example.tufengyi.manlife.utils.tools;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.activities.FlagsActivity;
import com.example.tufengyi.manlife.activities.MainActivity;
import com.example.tufengyi.manlife.bean.Empty;
import com.example.tufengyi.manlife.bean.HeadTitle;
import com.example.tufengyi.manlife.bean.PunchedAss;
import com.example.tufengyi.manlife.bean.SubTitle;

import java.util.List;

public class ObjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private int ITEM_HEADTITLE  = 1;
    private int ITEM_SUBTITLE   = 2;
    private int ITEM_PUNCHEDASS = 3;
    private int ITEM_EMPTY = 4;
    private List<Object> objects;
    private Context context;
    private int[] icons = {
            R.drawable.apples,
            R.drawable.vocabulary,
            R.drawable.water,
            R.drawable.breakfirst,
            R.drawable.makeup,
            R.drawable.night,
            R.drawable.read,
            R.drawable.shower,
            R.drawable.doctor,
            R.drawable.dailyarticle
    };

    public ObjectsAdapter(List<Object> objects,Context context){
        this.objects=objects;
        this.context = context;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = null;
        RecyclerView.ViewHolder holder = null;
        if(viewType == ITEM_HEADTITLE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_toptitle,parent,false);
            holder = new ViewHolderTopTitle(view);
        }else if(viewType == ITEM_SUBTITLE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_title,parent,false);
            holder = new ViewHolderSubTitle(view);
        }else if(viewType == ITEM_PUNCHEDASS){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_test,parent,false);
            holder = new ViewHolderPunched(view);
        }else if(viewType == ITEM_EMPTY){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_empty,parent,false);
            holder = new ViewHolderEmpty(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if(holder instanceof ViewHolderTopTitle){
            HeadTitle ht= (HeadTitle) objects.get(position);
            ((ViewHolderTopTitle)holder).tv_topTitle.setText(ht.getDate());
        }else if(holder instanceof ViewHolderSubTitle){
            SubTitle st = (SubTitle) objects.get(position);
            //这里记得改
            ((ViewHolderSubTitle)holder).tv_subTitle.setText(st.getDate()+"th");
        }else if(holder instanceof ViewHolderPunched){
            final PunchedAss pAss = (PunchedAss) objects.get(position);
            ((ViewHolderPunched)holder).img_ass.setBackgroundResource(icons[pAss.getIcon()]);
            ((ViewHolderPunched)holder).tv_ass.setText(pAss.getTitle());
        }else if(holder instanceof ViewHolderEmpty){
            Empty ep = (Empty) objects.get(position);
        }
    }

    @Override
    public int getItemViewType(int position){
        if(objects.get(position) instanceof HeadTitle){
            return ITEM_HEADTITLE;
        }else if(objects.get(position) instanceof SubTitle){
            return ITEM_SUBTITLE;
        }else if(objects.get(position) instanceof PunchedAss){
            return ITEM_PUNCHEDASS;
        }else if(objects.get(position) instanceof Empty){
            return ITEM_EMPTY;
        }

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount(){
        return objects ==null? 0:objects.size();
    }

}
