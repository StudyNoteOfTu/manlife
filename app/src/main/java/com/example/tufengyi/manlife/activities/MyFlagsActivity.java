package com.example.tufengyi.manlife.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tufengyi.manlife.MyApplication;
import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.CommentOfFlag;
import com.example.tufengyi.manlife.bean.Flag;
//import com.example.tufengyi.manlife.utils.dao.FlagDao;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.RedirectInterceptor;
import com.example.tufengyi.manlife.utils.tools.DateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        initDatas();
        initViews();
    }

    private void initViews(){
        LinearLayout back = (LinearLayout)findViewById(R.id.ll_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initDatas(){
        new Thread(){
            @Override
            public void run() {
                String token = SPManager.setting_get("token",MyFlagsActivity.this);

                OkHttpClient okHttpClient = new OkHttpClient();

                Request build1 = new Request.Builder().url("https://slow.hustonline.net/api/v1/flag")
                        .addHeader("Auth","Bearer "+token)
                        .get()
                        .build();

                okHttpClient.newCall(build1).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
//                        String string = response.body().string();
//                        Log.d("TestFlag","flags get---"+string);
//                        try{
//                            String arrayString = string.substring(string.indexOf("flags")+7,string.length()-2);
//                            Log.d("TestLog","arrayString"+arrayString);
//                            JSONArray flags = new JSONArray(arrayString);
//
//                            for(int i = 0; i<flags.length() ; i++){
//                                JSONObject object = flags.getJSONObject(i);
//                                JSONArray likes = object.getJSONArray("likes");//String
//                                JSONArray comments = object.getJSONArray("comments");//comments对象
//                                Flag flag = new Flag(object.getString("id"),object.getLong("time"),DateUtil.full_stampToDate(object.getLong("time")),object.getString("content"));
//                                for(int j = 0; j<likes.length() ;j++){
//                                    flag.likes.add((String)likes.get(j));
//                                }
//                                for(int k = 0; k<comments.length();k++){
//                                    CommentOfFlag commentOfFlag = new CommentOfFlag();
//                                    JSONObject comment = comments.getJSONObject(k);
//                                    commentOfFlag.setId(comment.getString("id"));
//                                    commentOfFlag.setFrom_id(comment.getString("from_id"));
//                                    commentOfFlag.setContent(comment.getString("content"));
//                                    commentOfFlag.setTime(comment.getLong("time"));
//                                    flag.comments.add(commentOfFlag);
//                                }
//                                mFlags.add(flag);
//
//                            }
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    adapter.notifyDataSetChanged();
//                                }
//                            });
//
//                        }catch(Exception e){
//                            Log.d("TestLog","get flag error "+e.getMessage());
//                            e.printStackTrace();
//                        }
                        String string = response.body().string();
                        Log.d("TestFlag","flags get---"+string);
                        //获取其中的数组，可以进行封装， 开头结束通过 最近的[]进行判断
                        try{

                            String arrayString = string.substring(string.indexOf("flags")+7,string.length()-2);
                            if(arrayString.equals("null")){
                                //没数据了
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MyFlagsActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                mFlags.clear();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                                Log.d("TestLog", "arrayString" + arrayString);
                                JSONArray flags = new JSONArray(arrayString);

                                for (int i = 0; i < flags.length(); i++) {
                                    JSONObject object = flags.getJSONObject(i);
                                    String wx_id = MyApplication.wx_id;
                                    String name = MyApplication.userName;
                                    String img_url = MyApplication.userImg;
                                    String id = object.getString("id");
                                    String content = object.getString("content");
                                    long time = object.getLong("time");
                                    JSONArray likes = object.getJSONArray("likes");//String
                                    JSONArray comments = object.getJSONArray("comments");//comments对象
                                    //String object_Json = object.toString();
                                    Flag flag = new Flag(wx_id,name,img_url,id,time,DateUtil.full_stampToDate(time),content);
                                    for (int j = 0; j < likes.length(); j++) {
                                        flag.likes.add((String) likes.get(j));
                                        if(MyApplication.wx_id.equals((String)likes.get(j))){
                                            flag.setCanLike(false);
                                        }
                                    }
                                    for (int k = 0; k < comments.length(); k++) {
                                        CommentOfFlag commentOfFlag = new CommentOfFlag();
                                        JSONObject comment = comments.getJSONObject(k);
                                        commentOfFlag.setId(comment.getString("id"));
                                        commentOfFlag.setFrom_id(comment.getString("from_id"));
                                        commentOfFlag.setContent(comment.getString("content"));
                                        commentOfFlag.setTime(comment.getLong("time"));
                                        commentOfFlag.setName(comment.getString("name"));
                                        commentOfFlag.setImg(comment.getString("img"));
                                        Log.d("TestFlag", "id" + commentOfFlag.getId());
                                        Log.d("TestFlag", "from_id" + commentOfFlag.getFrom_id());
                                        Log.d("TestFlag", "content" + commentOfFlag.getContent());
                                        Log.d("TestFlag", "time" + commentOfFlag.getTime());
                                        flag.comments.add(commentOfFlag);
                                    }
                                    mFlags.add(flag);
//                                            JSONObject object = flags.getJSONObject(i);
//                                            JSONArray likes = object.getJSONArray("likes");//String
//                                            JSONArray comments = object.getJSONArray("comments");//comments对象
//                                            //String object_Json = object.toString();
//                                            Flag flag = new Flag(object.getString("id"), object.getLong("time"), DateUtil.full_stampToDate(object.getLong("time")), object.getString("content"));
//                                            for (int j = 0; j < likes.length(); j++) {
//                                                flag.likes.add((String) likes.get(j));
//                                            }
//                                            for (int k = 0; k < comments.length(); k++) {
//                                                CommentOfFlag commentOfFlag = new CommentOfFlag();
//                                                JSONObject comment = comments.getJSONObject(k);
//                                                commentOfFlag.setId(comment.getString("id"));
//                                                commentOfFlag.setFrom_id(comment.getString("from_id"));
//                                                commentOfFlag.setContent(comment.getString("content"));
//                                                commentOfFlag.setTime(comment.getLong("time"));
//                                                Log.d("TestFlag", "id" + commentOfFlag.getId());
//                                                Log.d("TestFlag", "from_id" + commentOfFlag.getFrom_id());
//                                                Log.d("TestFlag", "content" + commentOfFlag.getContent());
//                                                Log.d("TestFlag", "time" + commentOfFlag.getTime());
//                                                flag.comments.add(commentOfFlag);
//                                            }
//                                            mFlags.add(flag);

                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }catch(Exception e){
                            Log.d("TestLog","get log error "+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
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
            private LinearLayout ll_top;

            public ViewHolder(View itemView) {
                super(itemView);
                ll_top = itemView.findViewById(R.id.ll_top);
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

            if(!flag.isCanLike()){
                    holder.btn_likes.setBackgroundResource(R.drawable.afterlike);
                    holder.btn_likes.setEnabled(false);
            }else{
                    holder.btn_likes.setBackgroundResource(R.drawable.beforelike);
                    holder.btn_likes.setEnabled(true);
            }
//            for(String user:flag.likes){
//                Log.d("TestFlag","likes"+flag.likes.size());
//                if(user.equals(MyApplication.wx_id) && !user.isEmpty() && MyApplication.wx_id !=null){
//                    //如果有当前用户，就不让点赞
//                    holder.btn_likes.setBackgroundResource(R.drawable.afterlike);
//                    holder.btn_likes.setEnabled(false);
//                    break;
//                }else{
//                    holder.btn_likes.setBackgroundResource(R.drawable.beforelike);
//                    holder.btn_likes.setEnabled(true);
//                }
//            }
//            holder.tv_name.setText(flag.getID());
//            holder.tv_date.setText(flag.getDate());
            Glide.with(MyFlagsActivity.this).load(flag.getImg_url()).into(holder.img_user);
            holder.tv_updates.setText(flag.getContent());
            holder.tv_name.setText(flag.getName());
            holder.tv_date.setText(flag.getDate());
            holder.num_likes.setText(""+flag.likes.size());
            holder.num_comments.setText(""+flag.comments.size());

            holder.btn_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.btn_likes.setBackgroundResource(R.drawable.afterlike);
                    holder.btn_likes.setEnabled(false);
                    holder.num_likes.setText(String.valueOf(Integer.parseInt(holder.num_likes.getText().toString())+1));
                    flag.likes.add(MyApplication.wx_id);
                    postLike(flag);
                }
            });

            holder.num_comments.setText(""+flag.comments.size());
            holder.num_likes.setText(""+flag.likes.size());

            holder.tv_updates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyFlagsActivity.this,FlagDetailActivity.class);
                    MyApplication.getInstance().setComments(flag.comments);
                    MyApplication.getInstance().setLikes(flag.likes);
                    intent.putExtra("content",flag.getContent());
                    intent.putExtra("date",flag.getDate());
                    intent.putExtra("openid",flag.getOpenid());
                    intent.putExtra("flagid",flag.getId());
                    intent.putExtra("name",flag.getName());
                    intent.putExtra("img",flag.getImg_url());
                    //...

                    startActivity(intent);
                }
            });
            holder.ll_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyFlagsActivity.this,FlagDetailActivity.class);
                    MyApplication.getInstance().setComments(flag.comments);
                    MyApplication.getInstance().setLikes(flag.likes);
                    intent.putExtra("content",flag.getContent());
                    intent.putExtra("date",flag.getDate());
                    intent.putExtra("openid",flag.getOpenid());
                    intent.putExtra("flagid",flag.getId());
                    intent.putExtra("name",flag.getName());
                    intent.putExtra("img",flag.getImg_url());
                    //...
                    startActivity(intent);
                }
            });

//            holder.btn_likes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holder.btn_likes.setBackgroundResource(R.drawable.afterlike);
//                    holder.num_likes.setText(""+(flag.getLikes()+1));
//                }
//            });
//
//            flag.setLikes(flag.getLikes()+1);
//            flagDao.update(flag);

            holder.btn_comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(holder.num_comments,flag);
                }
            });
        }

        //这个方法很简单了，返回playerList中的子项的个数
        @Override
        public int getItemCount() {
            return mFlags.size();
        }
    }

    private void postLike(final Flag flag){
        new Thread(){
            @Override
            public void run() {
                String token = SPManager.setting_get("token",MyFlagsActivity.this);
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .followRedirects(false)
                        .addInterceptor(new RedirectInterceptor())
                        .build();

                String json = "{\"openid\":\""+flag.getOpenid()+"\"," +
                        "\"flag_id\":\""+ flag.getId() +"\"}";


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
                flag.comments.add(commentOfFlag);
//                tv.setText(""+(flag.getComments()+1));
//                btn_comments.setBackgroundResource(R.drawable.commentlight);

//                postComment();
                new Thread(){
                    @Override
                    public void run() {
                        String token = SPManager.setting_get("token", MyFlagsActivity.this);

                        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                .followRedirects(false)
                                .addInterceptor(new RedirectInterceptor())
                                .build();

                        String json = "{\"openid\":\""+flag.getOpenid()+"\"," +
                                "\"flag_id\":\""+ flag.getId() +"\"," +
                                "\"comment\":{" +
                                "\"id\":\"\"," +
                                "\"from_id\":\"\"," +
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
                                            LayoutInflater inflater = LayoutInflater.from(MyFlagsActivity.this);
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

}
