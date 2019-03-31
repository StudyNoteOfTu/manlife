package com.example.tufengyi.manlife.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.Comments;
import com.example.tufengyi.manlife.bean.Flag;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FlagDetailActivity  extends AppCompatActivity {

    private ImageView iv_comments;
    private ImageView iv_like;
    private LinearLayout ll_comment;
    private LinearLayout ll_like;

    private ListView listView;
    private List<Comments> mComments = new ArrayList<>();
    long id = 0;
    String name = "Tuu";
    String date = "yyyy-MM-dd";
    String content = "goood!";
    int num_comments =0;
    int num_likes = 0;
    MyAdapter adapter;
    private Flag flag = new Flag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_detail);

        Intent intent = getIntent();
        //可以获取id，然后这里遍历查询展现
        id = intent.getLongExtra("id",0);
        name = intent.getStringExtra("name");
        date = intent.getStringExtra("date");
        content = intent.getStringExtra("updates");
        num_comments = intent.getIntExtra("comments",0);
        num_likes = intent.getIntExtra("likes",0);

//        flag = new Flag(id,name,date,content,num_comments,num_likes);

        initData();
        initViews();
    }

    private void initViews(){

        //注意是否点过赞也要有一个记录，需要后台
        listView  = (ListView) findViewById(R.id.listView);
        TextView tv_name = findViewById(R.id.tv_name);
        TextView tv_date = findViewById(R.id.tv_date);
        TextView tv_contentOfFlag = findViewById(R.id.tv_contentOfFlag);
        ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
        ll_like = (LinearLayout) findViewById(R.id.ll_like);
        final TextView tv_comments = findViewById(R.id.tv_comment_num);
        final TextView tv_likes = findViewById(R.id.tv_like_num);


        iv_comments = (ImageView)findViewById(R.id.btn_toComment);

        iv_like =(ImageView) findViewById(R.id.btn_like);

        tv_comments.setText(""+flag.getComments());
        tv_likes.setText(""+flag.getLikes());

        //这里点击范围小，考虑吧两个控件放在一个Layout中

//        iv_comments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog(tv_comments,iv_comments,flag);
//            }
//        });
//
//        iv_like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                iv_like.setBackgroundResource(R.drawable.afterlike);
//                tv_likes.setText(String.valueOf(flag.getLikes()+1));
//            }
//        });
        ll_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(tv_comments,iv_comments,flag);
            }
        });

        ll_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_like.setBackgroundResource(R.drawable.afterlike);
                tv_likes.setText(String.valueOf(flag.getLikes()+1));
            }
        });

        tv_name.setText(name);
        tv_date.setText(date);
        tv_contentOfFlag.setText(content);
      //  tv_comments.setText(String.valueOf(num_comments));
       // tv_likes.setText(""+num_likes);


        adapter = new MyAdapter(mComments);
        listView.setAdapter(adapter);
    }

    private void initData(){
        Comments comments =new Comments("goood!!!","Tuuu");
        for(int j=0;j<5;j++) {
            mComments.add(comments);
        }
    }


    private class MyAdapter extends BaseAdapter{
        private List<Comments> mComments;
        private LayoutInflater layoutInflater;

        public MyAdapter(List<Comments> mComments){
            this.layoutInflater = LayoutInflater.from(FlagDetailActivity.this);
            this.mComments = mComments;
        }

        @Override
        public int getCount(){
            return mComments.size();
        }

        @Override
        public Object getItem(int position){
            return null;
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.comments_item,parent,false);
                viewHolder.tv_comments = convertView.findViewById(R.id.tv_comment);
                viewHolder.tv_name = convertView.findViewById(R.id.tv_name);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Comments comments = mComments.get(position);
            viewHolder.tv_name.setText(comments.getID());
            viewHolder.tv_comments.setText(comments.getComment());


         return convertView ;
        }

        public class ViewHolder {
            private TextView tv_name;
            private TextView tv_comments;

            public ViewHolder() {
            }
        }
    }

    private void showDialog(final TextView tv, final ImageView btn_comments, final Flag flag){
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
                LayoutInflater inflater = LayoutInflater.from(FlagDetailActivity.this);
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
