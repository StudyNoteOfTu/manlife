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
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    }




    private class FlagsAdapter extends RecyclerView.Adapter<FlagsAdapter.ViewHolder> {
        //创建list集合，泛型为之前定义的实体类
        private List<Flag> mFlags;
        //添加构造方法
        public FlagsAdapter(List<Flag> mFlags) {
            this.mFlags = mFlags;
        }
        //在onCreateViewHolder（）中完成布局的绑定，同时创建ViewHolder对象，返回ViewHolder对象
        @Override
        public FlagsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

            holder.num_comments.setText(""+flag.getComments());
            holder.num_likes.setText(""+flag.getLikes());
            holder.btn_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //添加后台事件
                    holder.btn_likes.setBackgroundResource(R.drawable.afterlike);
                    holder.num_likes.setText(""+(flag.getLikes()+1));
                }
            });
            holder.btn_comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(holder.num_comments,holder.btn_comments,flag);
                }
            });
        }

        //这个方法很简单了，返回playerList中的子项的个数
        @Override
        public int getItemCount() {
            return mFlags.size();
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
