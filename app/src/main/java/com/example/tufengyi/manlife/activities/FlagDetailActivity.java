package com.example.tufengyi.manlife.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tufengyi.manlife.MyApplication;
import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.CommentOfFlag;
import com.example.tufengyi.manlife.bean.Comments;
import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.bean.Flag;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.RedirectInterceptor;
import com.example.tufengyi.manlife.utils.tools.DateUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FlagDetailActivity  extends AppCompatActivity {

    private ImageView iv_comments;
    private ImageView iv_like;
    private LinearLayout ll_comment;
    private LinearLayout ll_like;
    private CircleImageView iv_head;
    private TextView tv_name;

    private ListView listView;
    private List<CommentOfFlag> mComments = new ArrayList<>();
    private List<String> likes = new ArrayList<>();
    long id = 0;
    String name ;
    String date;
    String content;
    String openid;
    String flag_id;
    String img;

    MyAdapter adapter;
    ImageView btn_back;

    boolean canLike = true;

    Flag flag;



//    private Flag flag = new Flag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_detail);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        content = intent.getStringExtra("content");
        img = intent.getStringExtra("img");
        name = intent.getStringExtra("name");
        openid = intent.getStringExtra("openid");
        flag_id = intent.getStringExtra("flagid");
        likes.clear();
        likes.addAll(MyApplication.getInstance().getLikes());
        mComments.clear();
        mComments.addAll(MyApplication.getInstance().getComments());
//        public Flag(String openid, String name, String img_url, String id, long time,String date, String content) {
        flag = new Flag(openid,name,img,flag_id, DateUtil.full_stringToDate(date),date,content);
        flag.likes.addAll(likes);
        flag.comments.addAll(mComments);

        initData();
        initViews();
    }

    private void initViews(){


        btn_back = (ImageView) findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().setLikes(likes);
                MyApplication.getInstance().setComments(mComments);
                Log.d("TestFlag","myapplication comments zise"+MyApplication.getInstance().getComments().size());
                Intent intent = new Intent();
                setResult(1,intent);
                finish();
            }
        });

        iv_head = (CircleImageView) findViewById(R.id.img_circle_user);
        tv_name = (TextView) findViewById(R.id.tv_name);

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

//        if(!canLike){
//            iv_like.setBackgroundResource(R.drawable.afterlike);
//            iv_like.setClickable(false);//不能再点赞
//        }

        tv_comments.setText(""+mComments.size());
        tv_likes.setText(""+likes.size());
        tv_name.setText(name+"");

        for(String user:likes){
            if(user.equals(MyApplication.wx_id) && !user.isEmpty() && MyApplication.wx_id !=null){
                Log.d("MyTest421","has user");
                //如果有当前用户，就不让点赞
                iv_like.setBackgroundResource(R.drawable.afterlike);
                ll_like.setEnabled(false);
                ll_like.setClickable(false);
                break;
            }else{
                iv_like.setBackgroundResource(R.drawable.beforelike);
                ll_like.setEnabled(true);
                ll_like.setClickable(true);
            }
        }

        Glide.with(FlagDetailActivity.this).load(img).into(iv_head);

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
                showDialog(tv_comments,flag);
            }
        });

        ll_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //点赞埋点

                MobclickAgent.onEvent(FlagDetailActivity.this,"likenumber");

                iv_like.setBackgroundResource(R.drawable.afterlike);
                ll_like.setEnabled(false);
                ll_like.setClickable(false);
                tv_likes.setText(String.valueOf(Integer.parseInt(tv_likes.getText().toString())+1));
                likes.add(MyApplication.wx_id);
                postLike();
//                tv_likes.setText(String.valueOf(flag.getLikes()+1));
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

    private void postLike(){
        new Thread(){
            @Override
            public void run() {
                String token = SPManager.setting_get("token",FlagDetailActivity.this);
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .followRedirects(false)
                        .addInterceptor(new RedirectInterceptor())
                        .build();

                String json = "{\"openid\":\""+openid+"\"," +
                        "\"flag_id\":\""+ flag_id +"\"}";

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


                Request request = new Request.Builder()
                        .addHeader("Auth","Bearer " + token )
                        .url("https://slow.hustonline.net/api/v1/flag/like")
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("TestLog",e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()) {
                            String string = response.body().string();
                            Log.d("TestFlag", "post flag like success" + string);

                        }else{
                            Log.d("TestFlag", "post flag like error"+response.code()+response);
                        }

                    }
                });

            }
        }.start();

    }

    public void postComment(final String comment, final String openid, final String flag_id){
        new Thread(){
            @Override
            public void run() {
                String token = SPManager.setting_get("token", FlagDetailActivity.this);

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .followRedirects(false)
                        .addInterceptor(new RedirectInterceptor())
                        .build();

                String json = "{\"openid\":\""+openid+"\"," +
                        "\"flag_id\":\""+ flag_id +"\"," +
                        "\"comment\":{" +
                        "\"id\":\"\"," +
                        "\"from_id\":\"\"," +
                        "\"content\":\""+ comment +"\"," +
                        "\"time\":0,}}";


                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


                Request request = new Request.Builder()
                        .addHeader("Auth","Bearer " + token )
                        .url("https://slow.hustonline.net/api/v1/flag/comment")
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("TestFlag",e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()) {
                            String string = response.body().string();
                            Log.d("TestFlag", "post flag comment success" + string);
                        }else{
                            Log.d("TestFlag", "post flag comment error"+response.code()+response);
                        }


                    }
                });
            }
        }.start();
    }

    private void initData(){
        mComments.clear();
        mComments.addAll(MyApplication.getInstance().getComments());

        likes.clear();
        likes.addAll(MyApplication.getInstance().getLikes());

//        for(String user:likes){
//            if(user.equals(MyApplication.wx_id)){
//                canLike = false;
//            }
//        }

    }


    private class MyAdapter extends BaseAdapter{
        private List<CommentOfFlag> mComments;
        private LayoutInflater layoutInflater;

        public MyAdapter(List<CommentOfFlag> mComments){
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
                viewHolder.img_head = convertView.findViewById(R.id.img_circle_user);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CommentOfFlag comment = mComments.get(position);
            viewHolder.tv_comments.setText(comment.getContent());
            viewHolder.tv_name.setText(comment.getName());
            Glide.with(FlagDetailActivity.this).load(comment.getImg()).into(viewHolder.img_head);


         return convertView ;
        }

        public class ViewHolder {
            private TextView tv_name;
            private TextView tv_comments;
            private ImageView img_head;
            public ViewHolder() {
            }
        }
    }

    private void showDialog(final TextView tv,final Flag flag){
        View view = LayoutInflater.from(this).inflate(R.layout.flag_dialog,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        final EditText edt_comment = (EditText) view.findViewById(R.id.edt_comment);
        final ImageView btn_send = (ImageView) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                CommentOfFlag commentOfFlag = new CommentOfFlag();
                commentOfFlag.setName(MyApplication.userName);
                commentOfFlag.setContent(edt_comment.getText().toString());
                commentOfFlag.setImg(MyApplication.userImg);
                mComments.add(commentOfFlag);
                adapter.notifyDataSetChanged();

                //评论埋点

                MobclickAgent.onEvent(FlagDetailActivity.this,"commentnumber");


//                tv.setText(""+(flag.getComments()+1));
//                btn_comments.setBackgroundResource(R.drawable.commentlight);

//                postComment();
                new Thread(){
                    @Override
                    public void run() {
                        String token = SPManager.setting_get("token", FlagDetailActivity.this);

                        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                .followRedirects(false)
                                .addInterceptor(new RedirectInterceptor())
                                .build();

                        String json = "{\"openid\":\""+flag.getOpenid()+"\"," +
                                "\"flag_id\":\""+ flag.getId() +"\"," +
                                "\"comment\":{" +
                                "\"id\":\"\"," +
                                "\"from_id\":\"\"," +
                                "\"img\":\""+MyApplication.userImg+"\","+
                                "\"name\":\""+MyApplication.userName+"\","+
                                "\"content\":\""+edt_comment.getText()+"\"," +
                                "\"time\":0}}";
//                        String json = "{\"openid\":\""+flag.getOpenid()+"\"," +
//                                "\"flag_id\":\""+ flag.getId() +"\"," +
//                                "\"comment\":{" +
//                                "\"id\":\"\"," +
//                                "\"from_id\":\"\"," +
//                                "\"img\":\""+MyApplication.userImg+"\","+
//                                "\"name\":\""+MyApplication.userName+"\","+
//                                "\"content\":\""+edt_comment.getText()+"\"," +
//                                "\"time\":0}}";


                        Log.d("TestFlag","comment json"+ json);


                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


                        Request request = new Request.Builder()
                                .addHeader("Auth","Bearer " + token )
                                .url("https://slow.hustonline.net/api/v1/flag/comment")
                                .post(requestBody)
                                .build();

                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d("TestLog",e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if(response.isSuccessful()) {
                                    String string = response.body().string();
                                    Log.d("TestFlag", "post flag comment success" + string);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {



                                            tv.setText(String.valueOf(Integer.parseInt(tv.getText().toString().trim())+1));
                                            dialog.dismiss();
                                            Toast toast = new Toast(getApplicationContext());
                                            //创建一个填充物，用于填充Toast
                                            LayoutInflater inflater = LayoutInflater.from(FlagDetailActivity.this);
                                            //填充物来自的xml文件，在这里改成一个view
                                            // 实现xml到view的转变
                                            View view = inflater.inflate(R.layout.toast_succeed, null);
                                            toast.setView(view);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.setDuration(Toast.LENGTH_SHORT);
                                            showMyToast(toast, 1000);
                                        }
                                    });
                                }else{
                                    Log.d("TestFlag", "post flag comment error"+response.code()+response);
                                }


                            }
                        });
                    }
                }.start();


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

    @Override
    public void onBackPressed() {
        MyApplication.getInstance().setLikes(likes);
        MyApplication.getInstance().setComments(mComments);
        Intent intent = new Intent();
        setResult(1,intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
