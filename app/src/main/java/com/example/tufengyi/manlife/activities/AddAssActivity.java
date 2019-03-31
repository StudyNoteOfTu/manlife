package com.example.tufengyi.manlife.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.RedirectInterceptor;
import com.example.tufengyi.manlife.utils.dao.DailyAssDao;
import com.example.tufengyi.manlife.view.MyDialog1;

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

public class AddAssActivity extends AppCompatActivity {
    private DailyAssDao dailyAssDao;
    private LinearLayout icon1,icon2,icon3,icon4,icon5,icon6,icon7,icon8,icon9;
    private Button btn_newAss;
    private LinearLayout lastll = null;
    private String title = null;
    private int num = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addass);
        dailyAssDao = new DailyAssDao(AddAssActivity.this);
        initViews();

    }

    private void initViews(){

        btn_newAss = (Button) findViewById(R.id.btn_newAss);
        btn_newAss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAssActivity.this,EditAssActivity.class);
                startActivity(intent);
            }
        });

        ImageView btn_back = (ImageView) findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //就9个直接写了， 如果更多改用GridView
        icon1 = (LinearLayout) findViewById(R.id.icon_1);
        updateClick(icon1,0,"吃水果");
        icon2 = (LinearLayout) findViewById(R.id.icon_2);
        updateClick(icon2,1,"记单词");
        icon3 = (LinearLayout) findViewById(R.id.icon_3);
        updateClick(icon3,2,"喝水");
        icon4 = (LinearLayout) findViewById(R.id.icon_4);
        updateClick(icon4,3,"吃早餐");
        icon5 = (LinearLayout) findViewById(R.id.icon_5);
        updateClick(icon5,4,"化妆");
        icon6 = (LinearLayout) findViewById(R.id.icon_6);
        updateClick(icon6,5,"早睡");
        icon7 = (LinearLayout) findViewById(R.id.icon_7);
        updateClick(icon7,6,"读书");
        icon8 = (LinearLayout) findViewById(R.id.icon_8);
        updateClick(icon8,7,"锻炼");
        icon9 = (LinearLayout) findViewById(R.id.icon_9);
        updateClick(icon9,8,"吃药");


        ImageView gou = findViewById(R.id.gou);
        gou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastll !=null) {

                    //这里要本地储存， 为了能够做到断开打卡（本地+云端）





                    new Thread(){
                        @Override
                        public void run(){
                            String token = SPManager.setting_get("token",AddAssActivity.this);

                            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                    .followRedirects(false)
                                    .addInterceptor(new RedirectInterceptor())
                                    .build();

                            String json = "{\"id\":\"\"," +
                                    "\"time\":0," +
                                    "\"title\":\""+title+"\"," +
                                    "\"icon_id\":\""+num+"\"," +
                                    "\"sign_in\":[]}";



                            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


                            Request request = new Request.Builder()
                                    .addHeader("Auth","Bearer " + token )
                                    .url("https://slow.hustonline.net/api/v1/routine")
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
                                        Log.d("TestRoutine", "postLog success" + string);
                                        String tempString = string.substring(string.indexOf("id")+5);
                                        String objId = tempString.substring(0,tempString.indexOf("\",\""));
                                        Log.d("TestRoutine","substring objId"+objId);


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
                                        String date = String.valueOf(sb);
                                        DailyAssignment ass = new DailyAssignment(objId,date, title, num, "no", 0);//把id加进来,而且本地n天就n天
                                        dailyAssDao.insert(ass);


                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast toast = new Toast(getApplicationContext());
                                                //创建一个填充物，用于填充Toast
                                                LayoutInflater inflater = LayoutInflater.from(AddAssActivity.this);
                                                //填充物来自的xml文件，在这里改成一个view
                                                //实现xml到view的转变
                                                View view = inflater.inflate(R.layout.toast_ok, null);
                                                toast.setView(view);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.setDuration(Toast.LENGTH_SHORT);
                                                showMyToast(toast, 1000);
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

                    //Toast.makeText(AddAssActivity.this, "succeeded", Toast.LENGTH_SHORT).show();



                }else{
                    Toast.makeText(AddAssActivity.this, "请先选择", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void updateClick(final LinearLayout linearLayout, final int n, final String tit){
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lastll !=null){
                    lastll.setBackgroundResource(R.drawable.edit_back);
                }
                linearLayout.setBackgroundResource(R.drawable.select_back);
                lastll = linearLayout;
                title = tit;
                num = n;

                    }
//                });
//                myDialog1.show();

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
