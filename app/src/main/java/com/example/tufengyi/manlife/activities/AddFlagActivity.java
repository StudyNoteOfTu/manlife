package com.example.tufengyi.manlife.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tufengyi.manlife.MyApplication;
import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.bean.Flag;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.RedirectInterceptor;
import com.umeng.analytics.MobclickAgent;
//import com.example.tufengyi.manlife.utils.dao.FlagDao;

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

public class AddFlagActivity extends AppCompatActivity {

//    private PunchedAssDao punchedAssDao;
//    private LogDao logDao;
//    private FlagDao flagDao;

    EditText edt_flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addflag);
//        punchedAssDao = new PunchedAssDao(AddFlagActivity.this);
//        logDao = new LogDao(AddFlagActivity.this);
//        flagDao = new FlagDao(AddFlagActivity.this);
        initViews();

    }

    private void initViews(){

        edt_flag = (EditText) findViewById(R.id.edt_flag);
        Button btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_flag.getText().toString().isEmpty()){
                    Toast.makeText(AddFlagActivity.this, "flag不能为空哦", Toast.LENGTH_SHORT).show();
                }else{
                    //添加flag埋点
                    HashMap<String,String> map  = new HashMap<>();
                    map.put("user",MyApplication.userId);
                    MobclickAgent.onEvent(AddFlagActivity.this,"setFlagnumber",map);
                    POST(edt_flag.getText().toString());
                    Toast toast = new Toast(getApplicationContext());
                    LayoutInflater inflater = LayoutInflater.from(AddFlagActivity.this);
                    View view = inflater.inflate(R.layout.toast_ok,null);
                    toast.setView(view);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    showMyToast(toast,1000);
                }
            }
        });
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void POST(final String content){
        new Thread(){
            @Override
            public void run(){
                String token = SPManager.setting_get("token", AddFlagActivity.this);

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .followRedirects(false)
                        .addInterceptor(new RedirectInterceptor())
                        .build();

                String json = "{\"id\":\"\"," +
                        "\"time\":0," +
                        "\"content\":\""+content+"\"," +
                        "\"likes\":[]," +
                        "\"comments\":[],"+
                        "\"sign_in\":[]}";



                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


                Request request = new Request.Builder()
                        .addHeader("Auth","Bearer " + token )
                        .url("https://slow.hustonline.net/api/v1/flag")
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
                            Log.d("TestFlag", "postFlag success" + string);
                            String temp = string.substring(string.indexOf("\"id\"")+6);
                            final String id = temp.substring(0,temp.indexOf("\""));
                            temp = string.substring(string.indexOf("\"content\"")+11);
                            final String content = temp.substring(0,temp.indexOf("\""));
                            temp = string.substring(string.indexOf("\"time\"")+7);
                            final long time = Long.parseLong(temp.substring(0,temp.indexOf(",")));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.putExtra("flag_id",id);
                                    intent.putExtra("content",content);
                                    intent.putExtra("time",time);
                                    intent.putExtra("name", MyApplication.userName);
                                    intent.putExtra("wx_id",MyApplication.wx_id);
                                    intent.putExtra("img_url",MyApplication.userImg);
                                    setResult(1,intent);
                                    finish();
                                }
                            });
                        }else{
                            Log.d("TestRoutine", "postLog error"+response.code()+response);
                        }


                    }
                });
            }
        }.start();
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
        MobclickAgent.onPageStart("setFlag");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("setFlag");
        MobclickAgent.onPause(this);
    }
}
