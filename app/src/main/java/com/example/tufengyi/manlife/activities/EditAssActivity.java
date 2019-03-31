package com.example.tufengyi.manlife.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.RedirectInterceptor;
import com.example.tufengyi.manlife.utils.dao.DailyAssDao;
import com.example.tufengyi.manlife.utils.tools.ScreenUtils;

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

public class EditAssActivity extends AppCompatActivity {


   // private boolean hasOpen = false;
    private DailyAssignment tempDailyAss = null;
    private DailyAssDao dailyAssDao;
    private RelativeLayout icon1,icon2,icon3,icon4,icon5,icon6,icon7,icon8,icon9;

    private RelativeLayout lastll = null;

    private int chosedIcon = 0;
    private ImageView img_ass;
    private TextView tv_dailyAss;
    private Button btn_confirm;
    private int[] icons = {
            R.drawable.apples,
            R.drawable.vocabulary,
            R.drawable.water,
            R.drawable.breakfirst,
            R.drawable.makeup,
            R.drawable.night,
            R.drawable.read,
            R.drawable.sport,
            R.drawable.doctor
    };

    private int yes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfass);


//            public DailyAssignment(long id,String date,String title,int icon,String finish,int progress){

        Intent intent = getIntent();
        tempDailyAss = new DailyAssignment(intent.getLongExtra("id",0),
                intent.getStringExtra("objId"),
                intent.getStringExtra("date"),
                intent.getStringExtra("title"),
                intent.getIntExtra("icon",0),
                intent.getStringExtra("finish"),
                intent.getIntExtra("progress",0));
        yes = intent.getIntExtra("yes",0);



        dailyAssDao = new DailyAssDao(EditAssActivity.this);
        initViews();



    }

    @Override
    protected void onPause(){
        super.onPause();
        yes = 0;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        yes = 0;
    }

    private void initViews(){

        ImageView btn_back = (ImageView) findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yes = 0;
                finish();
            }
        });

        tv_dailyAss = (TextView) findViewById(R.id.tv_dailyAss);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_self);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(tv_dailyAss.getText().toString());
            }
        });


//        tv_dailyAss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog();
//            }
//        });

        TextView tv_progress  = findViewById(R.id.tv_progress);

        img_ass = (ImageView) findViewById(R.id.img_ass);

        icon1 = (RelativeLayout) findViewById(R.id.icon_1);
        chooseIcon(icon1,0);

        icon2 = (RelativeLayout) findViewById(R.id.icon_2);
        chooseIcon(icon2,1);

        icon3 = (RelativeLayout) findViewById(R.id.icon_3);
        chooseIcon(icon3,2);

        icon4 = (RelativeLayout) findViewById(R.id.icon_4);
        chooseIcon(icon4,3);

        icon5 = (RelativeLayout) findViewById(R.id.icon_5);
        chooseIcon(icon5,4);

        icon6 = (RelativeLayout) findViewById(R.id.icon_6);
        chooseIcon(icon6,5);

        icon7 = (RelativeLayout) findViewById(R.id.icon_7);
        chooseIcon(icon7,6);

        icon8 = (RelativeLayout) findViewById(R.id.icon_8);
        chooseIcon(icon8,7);

        icon9 = (RelativeLayout) findViewById(R.id.icon_9);
        chooseIcon(icon9,8);

        if(yes ==1){
            img_ass.setBackgroundResource(icons[tempDailyAss.getIcon()]);
            tv_dailyAss.setText(tempDailyAss.getTitle());
            tv_progress.setText("已坚持"+tempDailyAss.getProgress()+"天");
        }


        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer sb = new StringBuffer();
                Calendar cal_buff = Calendar.getInstance();

                sb.append(cal_buff.get(Calendar.YEAR));
                sb.append("-");
                if(cal_buff.get(Calendar.MONTH)+1>=0 && cal_buff.get(Calendar.MONTH)+1<10){
                    sb.append("0");
                }
                sb.append(cal_buff.get(Calendar.MONTH)+1);
                sb.append("-");
                if(cal_buff.get(Calendar.DAY_OF_MONTH)>=0 && cal_buff.get(Calendar.DAY_OF_MONTH)<10){
                    sb.append("0");
                }
                sb.append(cal_buff.get(Calendar.DAY_OF_MONTH));
                //这里可能会出问题，如何把StringBuffer转为String
                String date = String.valueOf(sb);
                String title = tv_dailyAss.getText().toString();
                int iconNow = chosedIcon;
                if(yes ==1 ){//有传来

                    //这里进行put更新
                    PUT(title);


                }else {
                    //如果没传来，那么就是自定义
                    //这里进行post添加

                    POST(title);

                    //DailyAssignment ass = new DailyAssignment(date, title, iconNow, "no", 0);
                    //dailyAssDao.insert(ass);
                }

                Toast toast = new Toast(getApplicationContext());
                //创建一个填充物，用于填充Toast
                LayoutInflater inflater = LayoutInflater.from(EditAssActivity.this);
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


                //Toast.makeText(EditAssActivity.this, "succeeded", Toast.LENGTH_SHORT).show();

                yes = 0;//初始化

                Intent intent = new Intent(EditAssActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void PUT(final String title){

        String token = SPManager.setting_get("token",EditAssActivity.this);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .addInterceptor(new RedirectInterceptor())
                .build();

        //这里注意第二行的 0 是否会修改后台数据
        String json = "{\"id\":\""+tempDailyAss.getObjId()+"\"," +
                "\"time\":0," +
                "\"title\":\""+title+"\"," +
                "\"icon_id\":\""+chosedIcon+"\"," +
                "\"sign_in\":[]}";

        Log.d("TestRoutine",json);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


        Request request = new Request.Builder()
                .addHeader("Auth","Bearer " + token )
                .url("https://slow.hustonline.net/api/v1/routine/title")
                .put(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TestRoutine",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    String string = response.body().string();
                    Log.d("TestRoutine", "postLog success" + string);

                    tempDailyAss.setTitle(title);
                    tempDailyAss.setIcon(chosedIcon);
                    dailyAssDao.update(tempDailyAss);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = new Toast(getApplicationContext());
                            //创建一个填充物，用于填充Toast
                            LayoutInflater inflater = LayoutInflater.from(EditAssActivity.this);
                            //填充物来自的xml文件，在这里改成一个view
                            //实现xml到view的转变
                            View view = inflater.inflate(R.layout.toast_ok, null);
                            toast.setView(view);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            showMyToast(toast, 1000);


                            yes = 0;//初始化

                            Intent intent = new Intent(EditAssActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });


                }else{
                    Log.d("TestRoutine", "put error"+response.code()+response);
                }


            }
        });
    }

    private void POST(final String title){
        new Thread(){
            @Override
            public void run(){
                String token = SPManager.setting_get("token", EditAssActivity.this);

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .followRedirects(false)
                        .addInterceptor(new RedirectInterceptor())
                        .build();

                String json = "{\"id\":\"\"," +
                        "\"time\":0," +
                        "\"title\":\""+title+"\"," +
                        "\"icon_id\":\""+chosedIcon+"\"," +
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
                        Log.d("TestLog",e.getMessage());
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
                            DailyAssignment ass = new DailyAssignment(objId,date, title, chosedIcon, "no", 0);//把id加进来,而且本地n天就n天
                            dailyAssDao.insert(ass);


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = new Toast(getApplicationContext());
                                    //创建一个填充物，用于填充Toast
                                    LayoutInflater inflater = LayoutInflater.from(EditAssActivity.this);
                                    //填充物来自的xml文件，在这里改成一个view
                                    //实现xml到view的转变
                                    View view = inflater.inflate(R.layout.toast_ok, null);
                                    toast.setView(view);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    showMyToast(toast, 1000);


                                    yes = 0;//初始化

                                    Intent intent = new Intent(EditAssActivity.this,MainActivity.class);
                                    startActivity(intent);
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


    private void chooseIcon(final RelativeLayout relativeLayout, final int icon){
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_ass.setBackgroundResource(icons[icon]);

                if(lastll !=null){
                    lastll.setBackgroundResource(R.drawable.edit_back);
                }
                relativeLayout.setBackgroundResource(R.drawable.select_back);
                lastll = relativeLayout;


                chosedIcon = icon;
//                if(!hasOpen){
//                    showDialog();
//                    hasOpen=true;
//                }
                //showDialog();
            }
        });

    }

    private void showDialog(String lastTitle){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_normal,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        final EditText edt_name_dialog = view.findViewById(R.id.edt_name);
        edt_name_dialog.setText(lastTitle);
        TextView btn_confirm = view.findViewById(R.id.btn_dialog_ok);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_dailyAss.setText(edt_name_dialog.getText().toString().trim());
              //  Toast.makeText(EditAssActivity.this, "", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)*5/7),LinearLayout.LayoutParams.WRAP_CONTENT);
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
