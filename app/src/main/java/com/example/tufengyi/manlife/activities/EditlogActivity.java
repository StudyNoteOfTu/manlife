package com.example.tufengyi.manlife.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tufengyi.manlife.MyApplication;
import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.DayLog;
import com.example.tufengyi.manlife.bean.PunchedAss;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.RedirectInterceptor;
import com.example.tufengyi.manlife.utils.tools.DateUtil;
import com.umeng.analytics.MobclickAgent;
//import com.example.tufengyi.manlife.utils.dao.PunchedAssDao;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditlogActivity extends AppCompatActivity {

//    private PunchedAssDao punchedAssDao;

    int status = 0;//0添加 1 更新今日 2 更新往日
    //来源
    //1.首页打开编辑 可能已发送，注意 如果有字段说明已经更新过，那么接下来就是修改，如果没字段就说明这天还没更新，更新需要id，需要保存在SP
    //2.已发送日志打开修改，传入id和content，进行修改

    String log_id;
    long log_time;
    String log_content;

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editlog);



        Thread.setDefaultUncaughtExceptionHandler(MyApplication.sUncaughtExceptionHandler);

//        punchedAssDao = new PunchedAssDao(EditlogActivity.this);

        initViews();
        initDatas();
    }

    private void initDatas(){
        //从LogDetActivity传来
        Intent intent = getIntent();
        log_id = intent.getStringExtra("log_id");
        log_time = intent.getLongExtra("log_time",0);
        log_content = intent.getStringExtra("log");
        status = 1;//外面传来的一定更新,判断是否传入的是今日
        if(log_id==null || log_id.isEmpty()){
            //从首页打开编辑
            if(DateUtil.stampToDate(System.currentTimeMillis()).equals(SPManager.setting_get("log_time",EditlogActivity.this))){
                //如果是同一天 更新
                status = 1;
                Log.d("Test54","log content"+SPManager.setting_get("log",EditlogActivity.this));
                Log.d("Test54","log id"+SPManager.setting_get("log_id",EditlogActivity.this));
                editText.setText(SPManager.setting_get("log",EditlogActivity.this));
                log_id = SPManager.setting_get("log_id",EditlogActivity.this);
            }else{//不是同一天，那么就不添加字段
                //添加
                status = 0;

            }
        }else if(DateUtil.stampToDate(log_time).equals(System.currentTimeMillis())){//如果是今日
            status = 1;
            editText.setText(log_content);
        }else{
            //如果是过去的flag
            status = 2;
            editText.setText(log_content);
        }

    }

    private void initViews(){
        editText = findViewById(R.id.edt_log);
        LinearLayout back = findViewById(R.id.ll_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout ok = findViewById(R.id.rl_gou);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if(content.trim().isEmpty()){
                    Toast.makeText(EditlogActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    StringBuffer sb = new StringBuffer();
                    Calendar cal_buff = Calendar.getInstance();

                    sb.append(cal_buff.get(Calendar.YEAR));
                    sb.append("-");
                    if (cal_buff.get(Calendar.MONTH) + 1 >= 0 && cal_buff.get(Calendar.MONTH) + 1 < 10) {
                        sb.append("0");
                    }
                    sb.append(cal_buff.get(Calendar.MONTH) + 1);
                    sb.append("-");
                    if (cal_buff.get(Calendar.DAY_OF_MONTH) >= 0 && cal_buff.get(Calendar.DAY_OF_MONTH) < 10) {
                        sb.append("0");
                    }
                    sb.append(cal_buff.get(Calendar.DAY_OF_MONTH));
                    //格式 yyyy-MM-dd
                    //这里可能会出问题，如何把StringBuffer转为String
                    final String date = String.valueOf(sb);
                    DayLog dayLog = new DayLog(date, editText.getText().toString());

//                    PunchedAss pAss = new PunchedAss(date, "日志", 9);
//                    punchedAssDao.insert(pAss);

                    if(status == 0){//添加
                        HashMap<String,String> map  = new HashMap<>();
                        map.put("user",MyApplication.userId);
                        MobclickAgent.onEvent(EditlogActivity.this,"diarynumber",map);

                        //添加
                        new Thread(){
                            @Override
                            public void run(){

                                String token = SPManager.setting_get("token",EditlogActivity.this);

                                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .followRedirects(false)
                                        .addInterceptor(new RedirectInterceptor())
                                        .build();

                                final String content = editText.getText().toString();

                                String json = "{\"id\":\"\"," +
                                        "\"time\":"+System.currentTimeMillis()+"," +
                                        "\"content\":\""+content+"\"}";



                                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


                                Request request = new Request.Builder()
                                        .addHeader("Auth","Bearer " + token )
                                        .url("https://slow.hustonline.net/api/v1/record")
                                        .post(requestBody)
                                        .build();


                                okHttpClient.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        android.util.Log.d("TestLog",e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if(response.isSuccessful()) {
                                            String string = response.body().string();
                                            android.util.Log.d("TestLog", "postLog success" + string);
                                            android.util.Log.d("Test54", "postLog success" + string);
                                            String temp = string.substring(string.indexOf("\"id\"")+6);
                                            final String log_id = temp.substring(0,temp.indexOf("\""));
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //添加到spmanager
                                                    SPManager.setting_add("log",content,EditlogActivity.this);
                                                    SPManager.setting_add("log_time", DateUtil.stampToDate(System.currentTimeMillis()),EditlogActivity.this);
                                                    SPManager.setting_add("log_id",log_id,EditlogActivity.this);
                                                    Log.d("Test54","log content"+SPManager.setting_get("log",EditlogActivity.this));
                                                    Log.d("Test54","log id"+SPManager.setting_get("log_id",EditlogActivity.this));
                                                    finish();
                                                }
                                            });
                                        }else{
                                            android.util.Log.d("TestLog", "postLog error"+response.code()+response);
                                        }


                                    }
                                });

                            }
                        }.start();
                    }else if(status == 1){
                        //更新今日
                        new Thread(){
                            @Override
                            public void run(){

                                String token = SPManager.setting_get("token",EditlogActivity.this);

                                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .followRedirects(false)
                                        .addInterceptor(new RedirectInterceptor())
                                        .build();

                                final String content = editText.getText().toString();

                                String json = "{\"id\":\""+log_id+"\"," +
                                        "\"time\":"+System.currentTimeMillis()+"," +
                                        "\"content\":\""+content+"\"}";



                                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


                                Request request = new Request.Builder()
                                        .addHeader("Auth","Bearer " + token )
                                        .url("https://slow.hustonline.net/api/v1/record")
                                        .put(requestBody)
                                        .build();


                                okHttpClient.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        android.util.Log.d("TestLog",e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if(response.isSuccessful()) {
                                            String string = response.body().string();
                                            android.util.Log.d("TestLog", "putLog success" + string);
                                            android.util.Log.d("Test54", "putLog success" + string);
                                            String temp = string.substring(string.indexOf("\"id\"")+5);
                                            final String log_id = temp.substring(0,temp.indexOf("\""));
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //添加到spmanager
                                                    SPManager.setting_add("log",content,EditlogActivity.this);
                                                    SPManager.setting_add("log_time", DateUtil.stampToDate(System.currentTimeMillis()),EditlogActivity.this);
                                                    SPManager.setting_add("log_id",log_id,EditlogActivity.this);
                                                    Log.d("Test54","log content"+SPManager.setting_get("log",EditlogActivity.this));
                                                    Log.d("Test54","log id"+SPManager.setting_get("log_id",EditlogActivity.this));

                                                    Intent intent = new Intent();
                                                    intent.putExtra("log",content);
                                                    intent.putExtra("log_time",DateUtil.stampToDate(System.currentTimeMillis()));
                                                    intent.putExtra("log_id",log_id);
                                                    setResult(1,intent);
                                                    finish();

                                                    //这里要把数据回传回去
                                                }
                                            });
                                        }else{
                                            android.util.Log.d("TestLog", "postLog error"+response.code()+response);
                                        }


                                    }
                                });

                            }
                        }.start();
                    }else{
                        //更新往日
                        new Thread(){
                            @Override
                            public void run(){

                                String token = SPManager.setting_get("token",EditlogActivity.this);

                                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .followRedirects(false)
                                        .addInterceptor(new RedirectInterceptor())
                                        .build();

                                final String content = editText.getText().toString();

                                String json = "{\"id\":\""+log_id+"\"," +
                                        "\"time\":"+System.currentTimeMillis()+"," +
                                        "\"content\":\""+content+"\"}";



                                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


                                Request request = new Request.Builder()
                                        .addHeader("Auth","Bearer " + token )
                                        .url("https://slow.hustonline.net/api/v1/record")
                                        .put(requestBody)
                                        .build();


                                okHttpClient.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        android.util.Log.d("TestLog",e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if(response.isSuccessful()) {
                                            String string = response.body().string();
                                            android.util.Log.d("TestLog", "putLog success" + string);
                                            android.util.Log.d("Test54", "putLog success" + string);
                                            String temp = string.substring(string.indexOf("\"id\"")+5);
                                            final String log_id = temp.substring(0,temp.indexOf("\""));
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //不用添加到spmanager
                                                    Intent intent = new Intent();
                                                    intent.putExtra("log",content);
                                                    setResult(2,intent);
                                                    finish();
                                                }
                                            });
                                        }else{
                                            android.util.Log.d("TestLog", "postLog error"+response.code()+response);
                                        }


                                    }
                                });

                            }
                        }.start();
                    }



                    //以下步骤只能在主线程中进行

                    //...
                    Toast toast = new Toast(getApplicationContext());
                    //创建一个填充物，用于填充Toast
                    LayoutInflater inflater = LayoutInflater.from(EditlogActivity.this);
                    //填充物来自的xml文件，在这里改成一个view
                    //实现xml到view的转变
                    View view = inflater.inflate(R.layout.toast_ok, null);
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
            }
        });

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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("diary");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("diary");
        MobclickAgent.onPause(this);
    }
}
