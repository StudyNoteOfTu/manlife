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
//import com.example.tufengyi.manlife.utils.dao.FlagDao;

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

                POST(edt_flag.getText().toString());

//                StringBuffer sb = new StringBuffer();
//                Calendar cal_buff = Calendar.getInstance();
//
//                sb.append(cal_buff.get(Calendar.YEAR));
//                sb.append("-");
//                if(cal_buff.get(Calendar.MONTH)+1>=0 && cal_buff.get(Calendar.MONTH)+1<10){
//                    sb.append("0");
//                }
//                sb.append(cal_buff.get(Calendar.MONTH)+1);
//                sb.append("-");
//                if(cal_buff.get(Calendar.DAY_OF_MONTH)>=0 && cal_buff.get(Calendar.DAY_OF_MONTH)<10){
//                    sb.append("0");
//                }
//                sb.append(cal_buff.get(Calendar.DAY_OF_MONTH));
//                //格式 yyyy-MM-dd
//                //这里可能会出问题，如何把StringBuffer转为String
//                String date = String.valueOf(sb);
//
////                PunchedAss punchedAss = new PunchedAss(date,"日志",9);
////                punchedAssDao.insert(punchedAss);
////                DayLog log = new DayLog(date,edt_flag.getText().toString());
////                logDao.insert(log);
//                Flag flag = new Flag("tuuu",date,edt_flag.getText().toString(),0,0);
//                flagDao.insert(flag);


                Toast toast = new Toast(getApplicationContext());
                //创建一个填充物，用于填充Toast
                LayoutInflater inflater = LayoutInflater.from(AddFlagActivity.this);
                //填充物来自的xml文件，在这里改成一个view
                //实现xml到view的转变
                View view = inflater.inflate(R.layout.toast_ok,null);
                //不一定需要，找到xml里面的组件，摄制组建里面的具体内容
//                ImageView imageView1 = view.findViewById(R.id.img_toast);
//                TextView textView1 = view.findViewById(R.id.tv_toast);
//                imageView1.setImageResource(R.drawable.smile);
//                textView1.setText("哈哈哈哈哈");
                toast.setView(view);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.setDuration(Toast.LENGTH_SHORT);
                showMyToast(toast,1000);

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
}
