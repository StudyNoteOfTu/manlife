package com.example.tufengyi.manlife.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.Flag;
import com.example.tufengyi.manlife.utils.tools.ScreenUtils;
import com.example.tufengyi.manlife.view.CustomSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Flags;

import de.hdodenhof.circleimageview.CircleImageView;

public class FlagsActivity extends AppCompatActivity {


    private Handler handler ;
    private View darkback;

    private RecyclerView mRecyclerView;
    private List<Flag> mFlags = new ArrayList<>();
    private FlagsAdapter adapter;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flags);
        darkback = (View) findViewById(R.id.darkback);
        Calendar cal = Calendar.getInstance();

        //判断时间是否是1-3号
//        if(cal.get(Calendar.DAY_OF_MONTH)<0||cal.get(Calendar.DAY_OF_MONTH)>3) {
//            darkback.setVisibility(View.VISIBLE);
//            darkback.setAlpha(0.5f);
//            handler = new Handler(){
//                @Override
//                public void handleMessage(Message msg){
//                    if(msg.what == 1){
//                        darkback.setVisibility(View.GONE);
//                        Intent intent = new Intent(FlagsActivity.this,MainActivity.class);
//                        startActivity(intent);
//                    }
//                }
//            };
//
//            new Thread(new Runnable(){
//                @Override
//                public void run(){
//                    try{
//                        Thread.sleep(1500);
//                    }catch(InterruptedException e){
//                        e.printStackTrace();
//                    }
//                    handler.sendEmptyMessage(1);
//                }
//            }).start();
//            Toast toast = new Toast(getApplicationContext());
//            //创建一个填充物，用于填充Toast
//            LayoutInflater inflater = LayoutInflater.from(FlagsActivity.this);
//            //填充物来自的xml文件，在这里改成一个view
//            //实现xml到view的转变
//            View view = inflater.inflate(R.layout.warning, null);
//            //不一定需要，找到xml里面的组件，摄制组建里面的具体内容
////                ImageView imageView1 = view.findViewById(R.id.img_toast);
////                TextView textView1 = view.findViewById(R.id.tv_toast);
////                imageView1.setImageResource(R.drawable.smile);
////                textView1.setText("哈哈哈哈哈");
//            toast.setView(view);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.setDuration(Toast.LENGTH_SHORT);
//            showMyToast(toast, 2000);
//
//
////            Intent intent = new Intent(FlagsActivity.this,MainActivity.class);
////            startActivity(intent);
//        }else {
            initDatas();

            initViews();
//        }





    }

    private void initDatas(){
        Calendar cal = Calendar.getInstance();
        for(int i=0;i<20;i++){
            Flag flag = new Flag("NUM"+i,(cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH)+"日"+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE),"hehehaha"+i,20*i%13,18*i%7);
            mFlags.add(flag);
        }
    }

    private void initViews(){

        mRecyclerView = (RecyclerView) findViewById(R.id.flags_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FlagsActivity.this);
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
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
//                super.onScrollStateChanged(recyclerView,newState);
//                //在newState为滑动到底部时候
//                if(newState == RecyclerView.SCROLL_STATE_IDLE){
//                    Log.d("TestFlag","the bottom");
//                    //获取数据，添加数据
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
//                super.onScrolled(recyclerView,dx,dy);
//                //在滑动完成后，拿到最后一个课件的item的位置
//                //... =  linearLayoutManager.findLastVisibeItemPosition();
//            }
//        });


        ImageView btn_back = (ImageView) findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView btn_addFlag  =(ImageView) findViewById(R.id.btn_addFlag);
        btn_addFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlagsActivity.this,AddFlagActivity.class);
                startActivity(intent);
            }
        });


        final CustomSwipeRefreshLayout swipeRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //...获取数据的操作

                //swipeRefreshLayout.setRefreshing(false);
            }
        });

    }




    private class FlagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        //创建list集合，泛型为之前定义的实体类
        private List<Flag> mFlags;

        private boolean hasMore = true;



        private int normalType = 0; //第一种ViewType 正常的item
        private int footType  = 1;  //第二种ViewType，底部的提示View

        private Handler mHandler = new Handler(Looper.getMainLooper());//获取主线程的Handler


        public boolean isHasMore() {
            return hasMore;
        }

        public void setHasMore(boolean hasMore) {
            this.hasMore = hasMore;
        }


        //添加构造方法
        public FlagsAdapter(List<Flag> mFlags) {
            this.mFlags = mFlags;
        }
        //在onCreateViewHolder（）中完成布局的绑定，同时创建ViewHolder对象，返回ViewHolder对象
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if(viewType == normalType){
                return new FlagsAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.flags_item,parent,false));
            }else{
                return new FlagsAdapter.FootHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_flag,parent,false));
            }

//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flags_item,parent,false);
//            FlagsAdapter.ViewHolder holder = new FlagsAdapter.ViewHolder(view);
//            return holder;
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

        public class FootHolder extends RecyclerView.ViewHolder{
            private TextView footer;

            public FootHolder(View itemView){
                super(itemView);
                footer = (TextView) itemView.findViewById(R.id.tv_footer);
            }
        }


        //在onBindViewHolder（）中完成对数据的填充
        @Override
        public void onBindViewHolder( RecyclerView.ViewHolder holder, int i) {
            //每次adapter.notifyDataSetChanged();都会重新绑定一次
//            if(i==1){
//                Toast.makeText(FlagsActivity.this, "adding", Toast.LENGTH_SHORT).show();
//            }
            if(holder instanceof ViewHolder){
                final FlagsAdapter.ViewHolder viewHolder = (FlagsAdapter.ViewHolder) holder;

                final Flag flag = mFlags.get(i);
                viewHolder.tv_name.setText(flag.getID());
                viewHolder.tv_date.setText(flag.getDate());
                viewHolder.tv_updates.setText(flag.getUpdates());
                viewHolder.tv_updates.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FlagsActivity.this,FlagDetailActivity.class);
//                    intent.putExtra("id",flag.getId());
                        intent.putExtra("name",flag.getID());
                        intent.putExtra("date",flag.getDate());
                        intent.putExtra("updates",flag.getUpdates());
                        intent.putExtra("comments",flag.getComments());
                        intent.putExtra("likes",flag.getLikes());
                        startActivity(intent);
                    }
                });

                viewHolder.num_comments.setText(""+flag.getComments());
                viewHolder.num_likes.setText(""+flag.getLikes());
                viewHolder.btn_likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //添加后台事件
                        viewHolder.btn_likes.setBackgroundResource(R.drawable.afterlike);
                        viewHolder.num_likes.setText(""+(flag.getLikes()+1));
                    }
                });
                viewHolder.btn_comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(viewHolder.num_comments,viewHolder.btn_comments,flag);
                    }
                });
            }else{
                final FlagsAdapter.FootHolder footHolder = (FlagsAdapter.FootHolder) holder;

                //点击增加更多
                footHolder.footer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //添加数据，完成后 提示更新，过程中设置其文字为 正在更新 结束后回到 点击加载更多 这里记得设置是否可点击
                        Calendar cal = Calendar.getInstance();
                        for(int i=0;i<20;i++){
                            Flag flag = new Flag("NUM"+i,(cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH)+"日"+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE),"hehehaha"+i,20*i%13,18*i%7);
                            mFlags.add(flag);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

                //这里补逻辑， 通过判断数据是否增加，如果增加了就显示正在加载更多，如果没有增加就显示没有更多数据了？

//                if(hasMore){
//                    footHolder.footer.setText("正在加载更多...");
//                }else{
//                    footHolder.footer.setText("没有更多数据了");
//
//                }

            }

        }

        //这个方法很简单了，返回playerList中的子项的个数
        //这类增加了一个footer，+1
        @Override
        public int getItemCount() {
            return mFlags.size() + 1;
        }

        public int getRealLastPosition(){
            return mFlags.size();
        }

        @Override
        public int getItemViewType(int position){
            if(position == getItemCount() - 1){
                return footType;
            } else {
                return normalType;
            }
        }
    }

    private void showDialog(final TextView tv,final ImageView btn_comments,final Flag flag){
        View view = LayoutInflater.from(this).inflate(R.layout.flag_dialog,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        final EditText edt_comment = (EditText) view.findViewById(R.id.edt_comment);
        final ImageView btn_send = (ImageView) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                tv.setText(""+(flag.getComments()+1));
                btn_comments.setBackgroundResource(R.drawable.commentlight);

                String comment = edt_comment.getText().toString();
                dialog.dismiss();

                Toast toast = new Toast(getApplicationContext());
                //创建一个填充物，用于填充Toast
                LayoutInflater inflater = LayoutInflater.from(FlagsActivity.this);
                //填充物来自的xml文件，在这里改成一个view
                //实现xml到view的转变
                View view = inflater.inflate(R.layout.toast_succeed, null);
                //不一定需要，找到xml里面的组件，摄制组建里面的具体内容
//                ImageView imageView1 = view.findViewById(R.id.img_toast);
//                TextView textView1 = view.findViewById(R.id.tv_toast);
//                imageView1.setImageResource(R.drawable.smile);
//                textView1.setText("哈哈哈哈哈");
                toast.setView(view);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                showMyToast(toast, 1000);

            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.BOTTOM);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕
        dialog.getWindow().setAttributes(p); //设置生效

    }
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 1000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );

    }
}
