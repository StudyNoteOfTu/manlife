package com.example.tufengyi.manlife.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.Flag;
//import com.example.tufengyi.manlife.utils.dao.FlagDao;
import com.example.tufengyi.manlife.utils.tools.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFlagsActivity extends AppCompatActivity{


//    private FlagDao flagDao;
    private RecyclerView mRecyclerView;
    private List<Flag> mFlags = new ArrayList<>();
    private FlagsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myflags);
//        flagDao = new FlagDao(MyFlagsActivity.this);
        initDatas();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(MyFlagsActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FlagsAdapter(mFlags);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(16, 16, 16, 8);
            }

        });

        initViews();
    }

    private void initViews(){
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initDatas(){
//        mFlags = flagDao.queryAll();

//        Collections.sort(mFlags, new Comparator<Flag>() {
//            @Override
//            public int compare(Flag o1, Flag o2) {
//                long num1 = DateUtil.stringToDate(o1.getDate());
//                long num2 = DateUtil.stringToDate(o2.getDate());
//                if (num1 > num2) {
//                    return 1;
//                } else if (num1 < num2) {
//                    return -1;
//                } else {
//                    return 0;
//                }
//            }
//        });
    }
//
//    private void iniViews(){
//
//    }


    private class FlagsAdapter extends RecyclerView.Adapter<FlagsAdapter.ViewHolder> {
        //创建list集合，泛型为之前定义的实体类
        private List<Flag> mFlags;
        //添加构造方法
        public FlagsAdapter(List<Flag> mFlags) {
            this.mFlags = mFlags;
        }
        //在onCreateViewHolder（）中完成布局的绑定，同时创建ViewHolder对象，返回ViewHolder对象
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flags_item,parent,false);
            FlagsAdapter.ViewHolder holder = new FlagsAdapter.ViewHolder(view);
            return holder;
        }


        //在内部类中完成对控件的绑定
        public class ViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView img_user;
            private TextView tv_name,tv_date;
            private TextView tv_updates;
            private ImageView btn_likes,btn_comments;
            private TextView num_comments,num_likes;

            public ViewHolder(View itemView) {
                super(itemView);
                img_user = itemView.findViewById(R.id.img_circle_user);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_date = itemView.findViewById(R.id.tv_date);
                tv_updates = itemView.findViewById(R.id.tv_contentOfFlag);
                btn_likes = itemView.findViewById(R.id.btn_like);
                btn_comments = itemView.findViewById(R.id.btn_toComment);
                num_comments = itemView.findViewById(R.id.tv_comment_num);
                num_likes = itemView.findViewById(R.id.tv_like_num);
            }
        }


        //在onBindViewHolder（）中完成对数据的填充
        @Override
        public void onBindViewHolder(final FlagsAdapter.ViewHolder holder, int i) {
            //每次adapter.notifyDataSetChanged();都会重新绑定一次
//            if(i==1){
//                Toast.makeText(FlagsActivity.this, "adding", Toast.LENGTH_SHORT).show();
//            }
            final Flag flag = mFlags.get(i);
            holder.tv_name.setText(flag.getID());
            holder.tv_date.setText(flag.getDate());
            holder.tv_updates.setText(flag.getUpdates());
            holder.tv_updates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyFlagsActivity.this,FlagDetailActivity.class);
//                    intent.putExtra("id",flag.getId());
                    intent.putExtra("name",flag.getID());
                    intent.putExtra("date",flag.getDate());
                    intent.putExtra("updates",flag.getUpdates());
                    intent.putExtra("comments",flag.getComments());
                    intent.putExtra("likes",flag.getLikes());
                    startActivity(intent);
                }
            });
            holder.num_comments.setText(""+flag.getComments());
            holder.num_likes.setText(""+flag.getLikes());
            holder.btn_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.btn_likes.setBackgroundResource(R.drawable.afterlike);
                    holder.num_likes.setText(""+(flag.getLikes()+1));
                }
            });

            flag.setLikes(flag.getLikes()+1);
//            flagDao.update(flag);

//            holder.btn_comments.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holder.num_comments.setText(""+(flag.getComments()+1));
//                    holder.btn_comments.setBackgroundResource(R.drawable.commentlight);
//
//                }
//            });
        }

        //这个方法很简单了，返回playerList中的子项的个数
        @Override
        public int getItemCount() {
            return mFlags.size();
        }
    }


}
