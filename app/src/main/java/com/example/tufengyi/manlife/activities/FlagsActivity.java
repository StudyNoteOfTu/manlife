package com.example.tufengyi.manlife.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tufengyi.manlife.MyApplication;
import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.CommentOfFlag;
import com.example.tufengyi.manlife.bean.Flag;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.RedirectInterceptor;
import com.example.tufengyi.manlife.utils.tools.DateUtil;
import com.example.tufengyi.manlife.view.swipe.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class FlagsActivity extends AppCompatActivity {

    String lastTime;

    private int j;//计数

    private View darkback;

    private RecyclerView mRecyclerView;
    private List<Flag> mFlags = new ArrayList<>();
    private FlagsAdapter adapter;

    int jumpIndex;

    int curIndex = 0;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case 1:
                if(resultCode == 1){
                    //添加flag
                    String name = data.getStringExtra("name");
                    String wx_id = data.getStringExtra("wx_id");
                    String img_url = data.getStringExtra("img_url");
                    String flag_id = data.getStringExtra("flag_id");
                    String content = data.getStringExtra("content");
                    long time = data.getLongExtra("time",0) ;
//                    public Flag(String openid, String name, String img_url, String id, long time,String date, String content) {
                    Flag flag = new Flag(wx_id,name,img_url,flag_id,time,DateUtil.full_stampToDate(time),content);
                    mFlags.add(0,flag);
                    adapter.notifyItemInserted(0);
                    adapter.notifyItemRangeChanged(0,0);
                    mRecyclerView.scrollToPosition(0);
                }
                break;

            case 2:
                if(resultCode == 1){//改flag数据
                    Log.d("TestTu512","flag result");
//                    mFlags.get(jumpIndex).setLikes(MyApplication.getInstance().getLikes());
//                    mFlags.get(jumpIndex).setComments(MyApplication.getInstance().getComments());
                    mFlags.get(jumpIndex).likes.clear();
                    mFlags.get(jumpIndex).likes.addAll(MyApplication.getInstance().getLikes());
                    mFlags.get(jumpIndex).comments.clear();
                    mFlags.get(jumpIndex).comments.addAll(MyApplication.getInstance().getComments());
                    mFlags.get(jumpIndex).setHasChanged(true);

                    for(String user:mFlags.get(jumpIndex).likes){
                        if(user.equals(MyApplication.wx_id) && !user.isEmpty() && MyApplication.wx_id !=null){
                            mFlags.get(jumpIndex).setCanLike(false);
                        }else{
                            mFlags.get(jumpIndex).setCanLike(true);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    //adapter.listUpdated(mFlags);
                    adapter.notifyItemChanged(jumpIndex);
//                    mFlags.get(jumpIndex).setHasChanged(false);
                }
        }
    }

    private void initDatas(){
        GET(curIndex);
    }



    private void GET(final int num){
        new Thread(){
            @Override
            public void run() {


                String token = SPManager.setting_get("token",FlagsActivity.this);

                OkHttpClient okHttpClient = new OkHttpClient();

                Request build1 = new Request.Builder().url("https://slow.hustonline.net/api/v1/flag/flags?num="+num)
                        .addHeader("Auth","Bearer "+token)
                        .get()
                        .build();


                curIndex++;

                okHttpClient.newCall(build1).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        Log.d("TestFlag","flags get---"+string);
                        //获取其中的数组，可以进行封装， 开头结束通过 最近的[]进行判断
                        try{
                            String arrayString = string.substring(string.indexOf("flags")+7,string.length()-2);
                            Log.d("TestFlag",arrayString);
                            if(arrayString.equals("null")){
                                //没数据了
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FlagsActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                Log.d("TestLog", "arrayString" + arrayString);
                                JSONArray flags = new JSONArray(arrayString);

                                for (int i = 0; i < flags.length(); i++) {
                                    JSONObject object = flags.getJSONObject(i);
                                    String name = object.getString("name");
                                    String wx_id = object.getString("wx_id");
                                    String img_url = object.getString("img_url");
                                    JSONObject innerFlag = object.getJSONObject("flags");
                                    String id = innerFlag.getString("id");
                                    String content = innerFlag.getString("content");
                                    long time = innerFlag.getLong("time");
                                    JSONArray likes = innerFlag.getJSONArray("likes");//String
                                    JSONArray comments = innerFlag.getJSONArray("comments");//comments对象
                                    //String object_Json = object.toString();
                                    Flag flag = new Flag(wx_id,name,img_url,id,time,DateUtil.full_stampToDate(time),content);
                                    Log.d("TestFlag","get flag");
                                    for (int j = 0; j < likes.length(); j++) {
                                        flag.likes.add((String) likes.get(j));
                                        if(MyApplication.wx_id.equals((String)likes.get(j))){
                                            flag.setCanLike(false);
                                        }
                                    }
                                    for (int k = 0; k < comments.length(); k++) {
                                        Log.d("TestFlag","begin flag comment");
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
                                        Log.d("TestFlag", "name" + commentOfFlag.getName());
                                        Log.d("TestFlag", "img" + commentOfFlag.getImg());
                                        Log.d("TestFlag","img"+commentOfFlag.getImg());
                                        flag.comments.add(commentOfFlag);
                                    }
                                    mFlags.add(flag);

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


        LinearLayout btn_back = (LinearLayout) findViewById(R.id.ll_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout btn_addFlag  =(RelativeLayout) findViewById(R.id.rl_edit);
        btn_addFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlagsActivity.this,AddFlagActivity.class);
//                startActivity(intent);
                startActivityForResult(intent,1);
            }
        });


//        TextView view = new TextView(this);
//        view.setPadding(100,100,100,100);


        lastTime = DateUtil.full_stampToDate(System.currentTimeMillis());


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setEnabled(true);
//        swipeRefreshLayout.setHeadView(view);
        swipeRefreshLayout.hideColorProgressBar();

        swipeRefreshLayout.setRefreshSubText("date");
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshSubText("上次更新时间: "+lastTime);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshSubText("上次更新时间: "+lastTime);
                lastTime = DateUtil.full_stampToDate(System.currentTimeMillis());
                //...获取数据的操作

                //curIndex=0;
                //mFlags.clear();

                new Thread(){
                    @Override
                    public void run() {

                        String token = SPManager.setting_get("token",FlagsActivity.this);

                        OkHttpClient okHttpClient = new OkHttpClient();

                        //只查询最近的10个的变化，有多少变化插入多少个，curIndex不变
                        Request build1 = new Request.Builder().url("https://slow.hustonline.net/api/v1/flag/flags?num="+0)
                                .addHeader("Auth","Bearer "+token)
                                .get()
                                .build();

                        //curIndex++;

                        okHttpClient.newCall(build1).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
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
                                                Toast.makeText(FlagsActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                        });
                                    }else {
//                                        mFlags.clear();

                                        Log.d("TestLog", "arrayString" + arrayString);
                                        JSONArray flags = new JSONArray(arrayString);


                                        for (int i = 0; i < flags.length(); i++) {
                                            JSONObject object = flags.getJSONObject(i);
                                            String name = object.getString("name");
                                            String wx_id = object.getString("wx_id");
                                            String img_url = object.getString("img_url");
                                            JSONObject innerFlag = object.getJSONObject("flags");
                                            String id = innerFlag.getString("id");
                                            String content = innerFlag.getString("content");
                                            long time = innerFlag.getLong("time");
                                            JSONArray likes = innerFlag.getJSONArray("likes");//String
                                            JSONArray comments = innerFlag.getJSONArray("comments");//comments对象
                                            //String object_Json = object.toString();
                                            Flag flag = new Flag(wx_id,name,img_url,id,time,DateUtil.full_stampToDate(time),content);
                                            for (int j = 0; j < likes.length(); j++) {
                                                flag.likes.add((String) likes.get(j));
                                                if(MyApplication.wx_id.equals((String)likes.get(j))){
                                                    flag.setCanLike(false);
                                                }
                                            }
                                            for (int k = 0; k < comments.length(); k++) {
                                                Log.d("TestFlag","begin flag comment");
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
                                                Log.d("TestFlag","img"+commentOfFlag.getImg());
                                                flag.comments.add(commentOfFlag);
                                            }
                                            if(flag.equalTo(mFlags.get(i))){
                                                //判断内容，不一样就修改
                                                if(flag.likes.size() != mFlags.get(i).likes.size()){
                                                    mFlags.get(i).likes = flag.likes;
                                                }

                                                if(flag.comments.size()!=mFlags.get(i).comments.size()){
                                                    mFlags.get(i).comments = flag.comments;
                                                }


                                            }else{
                                                mFlags.add(i,flag);

                                                j = i;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemInserted(j);
                                                    }
                                                });
                                            }
//                                            mFlags.add(flag);

                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(j==0){
                                                    mRecyclerView.scrollToPosition(0);
                                                }
                                                adapter.notifyItemRangeChanged(0, j);
//                                                adapter.notifyItemRangeInserted(0,j);
                                                adapter.notifyDataSetChanged();
                                                j=0;
                                                swipeRefreshLayout.setRefreshing(false);
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


                //swipeRefreshLayout.setRefreshing(false);
            }
        });

    }




    private class FlagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
        //创建list集合，泛型为之前定义的实体类
        //private List<Flag> mFlags;

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
            //this.mFlags = mFlags;
        }




        //在onCreateViewHolder（）中完成布局的绑定，同时创建ViewHolder对象，返回ViewHolder对象
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if(viewType == normalType){
                return new FlagsAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.flags_item,parent,false));
            }else{

                return  new FlagsAdapter.FootHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_flag,parent,false));
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

        public class FootHolder extends RecyclerView.ViewHolder{
            private TextView footer;

            public FootHolder(View itemView){
                super(itemView);
                footer = (TextView) itemView.findViewById(R.id.tv_footer);
            }
        }


        //在onBindViewHolder（）中完成对数据的填充
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {
            //holder.setIsRecyclable(false);
            //这里待优化
            //每次adapter.notifyDataSetChanged();都会重新绑定一次
//            if(i==1){
//                Toast.makeText(FlagsActivity.this, "adding", Toast.LENGTH_SHORT).show();
//            }
            Log.d("TestTu512","onBindViewCalled index = "+ i);


            if(holder instanceof ViewHolder){
                final FlagsAdapter.ViewHolder viewHolder = (FlagsAdapter.ViewHolder) holder;

                final Flag flag = mFlags.get(i);

                Log.d("TestTu512","comments size"+mFlags.get(i).comments.size());
                Log.d("TestTu512","has changed?"+mFlags.get(i).isHasChanged());
                mFlags.get(i).setHasChanged(false);


                if(!flag.isCanLike()){
                    //如果有当前用户，就不让点赞
                    viewHolder.btn_likes.setBackgroundResource(R.drawable.afterlike);
                    viewHolder.btn_likes.setEnabled(false);
                }else{
                    viewHolder.btn_likes.setBackgroundResource(R.drawable.beforelike);
                    viewHolder.btn_likes.setEnabled(true);
                }

//                for(String user:flag.likes){
//                    Log.d("TestFlag","likes"+flag.likes.size());
//                    if(user.equals(MyApplication.wx_id) && !user.isEmpty() && MyApplication.wx_id !=null){
//                        //如果有当前用户，就不让点赞
//                        viewHolder.btn_likes.setBackgroundResource(R.drawable.afterlike);
//                        viewHolder.btn_likes.setEnabled(false);
//                        break;
//                    }else{
//                        viewHolder.btn_likes.setBackgroundResource(R.drawable.beforelike);
//                        viewHolder.btn_likes.setEnabled(true);
//                    }
//                }

                Glide.with(FlagsActivity.this).load(flag.getImg_url()).into(viewHolder.img_user);
                viewHolder.tv_name.setText(flag.getName());
                viewHolder.tv_date.setText(flag.getDate());
                viewHolder.tv_updates.setText(flag.getContent());
                viewHolder.tv_updates.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FlagsActivity.this,FlagDetailActivity.class);
                        jumpIndex = i;
                        MyApplication.getInstance().setComments(flag.comments);
                        MyApplication.getInstance().setLikes(flag.likes);
                        intent.putExtra("content",flag.getContent());
                        intent.putExtra("date",flag.getDate());
                        intent.putExtra("openid",flag.getOpenid());
                        intent.putExtra("flagid",flag.getId());
                        intent.putExtra("name",flag.getName());
                        intent.putExtra("img",flag.getImg_url());
                        //...

                        startActivityForResult(intent,2);
                    }
                });
                viewHolder.ll_top.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jumpIndex = i;
                        Intent intent = new Intent(FlagsActivity.this,FlagDetailActivity.class);
                        MyApplication.getInstance().setComments(flag.comments);
                        MyApplication.getInstance().setLikes(flag.likes);
                        intent.putExtra("content",flag.getContent());
                        intent.putExtra("date",flag.getDate());
                        intent.putExtra("openid",flag.getOpenid());
                        intent.putExtra("flagid",flag.getId());
                        intent.putExtra("name",flag.getName());
                        intent.putExtra("img",flag.getImg_url());
                        //...

                        startActivityForResult(intent,2);
                    }
                });

                viewHolder.num_comments.setText(""+flag.comments.size());
                viewHolder.num_likes.setText(""+flag.likes.size());



                viewHolder.btn_likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.btn_likes.isEnabled()) {
                            viewHolder.btn_likes.setBackgroundResource(R.drawable.afterlike);
                            viewHolder.btn_likes.setEnabled(false);
                            flag.setCanLike(false);
                            viewHolder.num_likes.setText(String.valueOf(Integer.parseInt(viewHolder.num_likes.getText().toString()) + 1));
                            flag.likes.add(MyApplication.wx_id);
                            postLike(flag);
                        }
                    }
                });
                viewHolder.btn_comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(viewHolder.num_comments,flag);
//                        showDialog(viewHolder.num_comments,flag);
                    }
                });


            }else{
                final FlagsAdapter.FootHolder footHolder = (FlagsAdapter.FootHolder) holder;

                //点击增加更多
                footHolder.footer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        footHolder.footer.setText("加载中..");

                        new Thread(){
                            @Override
                            public void run() {

                                String token = SPManager.setting_get("token",FlagsActivity.this);

                                OkHttpClient okHttpClient = new OkHttpClient();

                                Request build1 = new Request.Builder().url("https://slow.hustonline.net/api/v1/flag/flags?num="+curIndex)
                                        .addHeader("Auth","Bearer "+token)
                                        .get()
                                        .build();

                                Log.d("TestFlag","current Index --- "+ curIndex);

                                curIndex++;

                                okHttpClient.newCall(build1).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                footHolder.footer.setText("加载中....");
                                            }
                                        });

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
                                                        Toast.makeText(FlagsActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                                                        footHolder.footer.setText("没有更多数据了");
                                                    }
                                                });
                                            }else {
                                                JSONArray flags = new JSONArray(arrayString);

                                                for (int i = 0; i < flags.length(); i++) {
                                                    JSONObject object = flags.getJSONObject(i);
                                                    String name = object.getString("name");
                                                    String wx_id = object.getString("wx_id");
                                                    String img_url = object.getString("img_url");
                                                    JSONObject innerFlag = object.getJSONObject("flags");
                                                    String id = innerFlag.getString("id");
                                                    String content = innerFlag.getString("content");
                                                    long time = innerFlag.getLong("time");
                                                    JSONArray likes = innerFlag.getJSONArray("likes");//String
                                                    JSONArray comments = innerFlag.getJSONArray("comments");//comments对象
                                                    //String object_Json = object.toString();
                                                    Flag flag = new Flag(wx_id,name,img_url,id,time,DateUtil.full_stampToDate(time),content);
                                                    for (int j = 0; j < likes.length(); j++) {
                                                        flag.likes.add((String) likes.get(j));
                                                        if(MyApplication.wx_id.equals((String)likes.get(j))){
                                                            flag.setCanLike(false);
                                                        }
                                                    }
                                                    for (int k = 0; k < comments.length(); k++) {
                                                        Log.d("TestFlag","begin flag comment");
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
                                                        Log.d("TestFlag","img"+commentOfFlag.getImg());
                                                        flag.comments.add(commentOfFlag);
                                                    }
                                                    mFlags.add(flag);
//                                                    JSONObject object = flags.getJSONObject(i);
//                                                    JSONArray likes = object.getJSONArray("likes");//String
//                                                    JSONArray comments = object.getJSONArray("comments");//comments对象
//                                                    //String object_Json = object.toString();
//                                                    Flag flag = new Flag(object.getString("id"), object.getLong("time"), DateUtil.full_stampToDate(object.getLong("time")), object.getString("content"));
//                                                    for (int j = 0; j < likes.length(); j++) {
//                                                        flag.likes.add((String) likes.get(j));
//                                                    }
//                                                    for (int k = 0; k < comments.length(); k++) {
//                                                        CommentOfFlag commentOfFlag = new CommentOfFlag();
//                                                        JSONObject comment = comments.getJSONObject(k);
//                                                        commentOfFlag.setId(comment.getString("id"));
//                                                        commentOfFlag.setFrom_id(comment.getString("from_id"));
//                                                        commentOfFlag.setContent(comment.getString("content"));
//                                                        commentOfFlag.setTime(comment.getLong("time"));
//                                                        Log.d("TestFlag", "id" + commentOfFlag.getId());
//                                                        Log.d("TestFlag", "from_id" + commentOfFlag.getFrom_id());
//                                                        Log.d("TestFlag", "content" + commentOfFlag.getContent());
//                                                        Log.d("TestFlag", "time" + commentOfFlag.getTime());
//                                                        flag.comments.add(commentOfFlag);
//                                                    }
//                                                    mFlags.add(flag);

                                                }
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyDataSetChanged();
                                                        footHolder.footer.setText("点击加载更多");
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

                        //判断是否为null，弱为null则说明没有更多数据，则修改提示没有更多内容

                        //添加数据，完成后 提示更新，过程中设置其文字为 正在更新 结束后回到 点击加载更多 这里记得设置是否可点击
//                        Calendar cal = Calendar.getInstance();
//                        for(int i=0;i<20;i++){
////                            Flag flag = new Flag("NUM"+i,(cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH)+"日"+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE),"hehehaha"+i,20*i%13,18*i%7);
////                            mFlags.add(flag);
//                        }
//                        adapter.notifyDataSetChanged();
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

    private void postLike(final Flag flag){
        new Thread(){
            @Override
            public void run() {
                String token = SPManager.setting_get("token",FlagsActivity.this);
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
                commentOfFlag.setImg(MyApplication.userImg);
                flag.comments.add(commentOfFlag);
//                tv.setText(""+(flag.getComments()+1));
//                btn_comments.setBackgroundResource(R.drawable.commentlight);

//                postComment();
                new Thread(){
                    @Override
                    public void run() {
                        String token = SPManager.setting_get("token", FlagsActivity.this);

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
                                            LayoutInflater inflater = LayoutInflater.from(FlagsActivity.this);
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
