package com.example.tufengyi.manlife.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tufengyi.manlife.MyApplication;
import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.DayLog;
import com.example.tufengyi.manlife.bean.PunchedAss;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.RedirectInterceptor;
//import com.example.tufengyi.manlife.utils.dao.PunchedAssDao;

import java.io.IOException;
import java.util.Calendar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editlog);

        Thread.setDefaultUncaughtExceptionHandler(MyApplication.sUncaughtExceptionHandler);

//        punchedAssDao = new PunchedAssDao(EditlogActivity.this);

        initViews();
    }

    private void initViews(){
        final EditText editText = findViewById(R.id.edt_log);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView ok = findViewById(R.id.btn_ok);
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


                    new Thread(){
                        @Override
                        public void run(){

                            String token = SPManager.setting_get("token",EditlogActivity.this);

                            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                    .followRedirects(false)
                                    .addInterceptor(new RedirectInterceptor())
                                    .build();

                            String content = editText.getText().toString();

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
                                    }else{
                                        android.util.Log.d("TestLog", "postLog error"+response.code()+response);
                                    }


                                }
                            });

                        }
                    }.start();

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


                    finish();
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


}
