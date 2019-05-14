package com.example.tufengyi.manlife.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufengyi.manlife.MyApplication;
import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.Settings;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.DonwloadSaveImg;
import com.example.tufengyi.manlife.utils.HttpCallBackListener;
import com.example.tufengyi.manlife.utils.HttpUtil;
import com.example.tufengyi.manlife.utils.PrefParams;
import com.example.tufengyi.manlife.utils.tools.DateUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public IWXAPI mWxApi;
    private ReceiveBroadCast receiveBroadCast;

    private static String url = "https://slow.hustonline.net/api/v1/login";


    private static final String APP_ID = "wx7ef876fe1742f5df";
    private static final String secret = "7842d96f93d4116b247a6d38c8824c29";


    String[] PERMISSIONS = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @SuppressLint("HandlerLeak")
    private Handler handler  = new Handler(){
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 1){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Window window = getWindow();
                        window.setStatusBarColor(0xfff2b600);
                    }
                });

//                Window window = getWindow();
//                window.setStatusBarColor(0xfff2b600);
//                rl_greet.setVisibility(View.GONE);
//                rl_login.setVisibility(View.VISIBLE);
//                ll_loading.setVisibility(View.GONE);



                if(SPManager.setting_get("first_download",LoginActivity.this).equals("yes")){
                    SPManager.setting_add("token","",LoginActivity.this);
                    SPManager.setting_add("first_download","no",LoginActivity.this);
                }
                String token = SPManager.setting_get("token",LoginActivity.this);
                Log.d("Test427","token is "+ token);
                if(token!=null && !token.isEmpty()){//token不为null且不为空
                    Log.d("Test427","begin intent");
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);

                }else{
                    Window window = getWindow();
                    window.setStatusBarColor(0xfff2b600);
                    rl_greet.setVisibility(View.GONE);
                    rl_login.setVisibility(View.VISIBLE);
                    ll_loading.setVisibility(View.GONE);
                }
            }
        }
    };

    int send = 0;


    RelativeLayout rl_greet;
    LinearLayout ll_loading;
    TextView tv_date;
    RelativeLayout rl;
    RelativeLayout rl_login;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        SharedPreferences mSharedPreferences = getSharedPreferences("code",0);
//        String code = mSharedPreferences.getString("code","null");
//        Toast.makeText(this, "之前保存下来的code--"+code, Toast.LENGTH_SHORT).show();



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0xfff2b600);

        }

//        setContentView(R.layout.activity_greeting);

        setContentView(R.layout.activity_login);
        setTheme(R.style.AppTheme_Laucher1);

        registerToWX();


        int permission = ContextCompat.checkSelfPermission(LoginActivity.this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS,1);
        }
        int permission2 =  ContextCompat.checkSelfPermission(LoginActivity.this,
                "android.permission.INTERNET");
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{"android.permission.INTERNET"},1);
        }
        initGreetingViews();
        initSPData();
//        rl_greet.setVisibility(View.GONE);



        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    Thread.sleep(2200);
                    //这里可以进行网络操作
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            }
        }).start();


    }


    private void initSPData() {
        if(SPManager.setting_get("img_url", LoginActivity.this)==null){
            SPManager.setting_add("img_url","",LoginActivity.this);
        }

        if(SPManager.setting_get("img_path",LoginActivity.this)==null){
            SPManager.setting_add("img_path","",LoginActivity.this);
        }

        if(SPManager.setting_get("sound",LoginActivity.this)==null){
            SPManager.setting_add("sound","true",LoginActivity.this);
        }

        if(SPManager.setting_get("token",LoginActivity.this)==null){
            SPManager.setting_add("token","",LoginActivity.this);
        }

        if(SPManager.setting_get("log",LoginActivity.this)==null){
            SPManager.setting_add("log","",LoginActivity.this);
        }

        if(SPManager.setting_get("log_time",LoginActivity.this)==null){
            SPManager.setting_add("log_time","",LoginActivity.this);
        }

        if(SPManager.setting_get("log_id",LoginActivity.this)==null){
            SPManager.setting_add("log_id","",LoginActivity.this);
        }

        if(SPManager.setting_get("first_download",LoginActivity.this)==null){
            SPManager.setting_add("first_download","yes",LoginActivity.this);
        }
    }

    @SuppressLint("NewApi")
    private void initGreetingViews(){

        rl_greet = findViewById(R.id.rl_greet);
        rl_login = findViewById(R.id.rl_login);
        rl_login.setVisibility(View.GONE);
        ll_loading = findViewById(R.id.ll_loading);
        ll_loading.setVisibility(View.GONE);

        StringBuffer sb = new StringBuffer();
        Calendar cal_buff = Calendar.getInstance();


        sb.append(cal_buff.get(Calendar.MONTH) + 1);
        sb.append("月");
        sb.append(cal_buff.get(Calendar.DAY_OF_MONTH));
        sb.append("日");

        //格式 yyyy-MM-dd
        //这里可能会出问题，如何把StringBuffer转为String
        String date = String.valueOf(sb);
        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText("");
        ImageView img = findViewById(R.id.img);
        TextView tv_era = findViewById(R.id.tv_era);


        if(cal_buff.get(Calendar.HOUR_OF_DAY)<=17 && cal_buff.get(Calendar.HOUR_OF_DAY)>=6){
            img.setBackgroundResource(R.drawable.morning);
            tv_era.setText("日");
        }else{
            img.setBackgroundResource(R.drawable.evening);
            rl_greet.setBackgroundColor(0xFF7E7F9B);
            Window window = getWindow();
            window.setStatusBarColor(0xFF7E7F9B);
            tv_era.setText("晚");
        }

        tv_date = findViewById(R.id.tv_date);
        tv_date.setText(date);

        rl = (RelativeLayout) findViewById(R.id.btn_log);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换到微信登录
                Log.d("TestWX","clicked intent");
                weChatAuth();
                //Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                //startActivity(intent);
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("authlogin");
        this.registerReceiver(receiveBroadCast,filter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(receiveBroadCast);
    }

    private void registerToWX(){
        mWxApi = WXAPIFactory.createWXAPI(this,APP_ID,true);
       // mWxApi.handleIntent(getIntent(),this);
        mWxApi.registerApp(APP_ID);
    }

    private void weChatAuth(){
        if(mWxApi == null){
            mWxApi = WXAPIFactory.createWXAPI(LoginActivity.this,APP_ID,true);
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope="snsapi_userinfo";
        req.state = "ex_login_test";
        mWxApi.sendReq(req);
    }

    public void getAccessToken() {
        Log.d("login","getAccessToken");
        SharedPreferences WxSp = getApplicationContext()
                .getSharedPreferences(PrefParams.spName, Context.MODE_PRIVATE);
        String code = WxSp.getString(PrefParams.CODE, "");
        final SharedPreferences.Editor WxSpEditor = WxSp.edit();

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +
                APP_ID
                + "&secret="
                + "7842d96f93d4116b247a6d38c8824c29"
                + "@code="
                + code
                + "&grant_type=authorization_code";

        HttpUtil.sendHttpRequest(url, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Log.d("login","开始解析以及储存获取到的信息----");
                //解析以及储存获取到的信息
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Log.d("login","respoense"+response);

                    String access_token = jsonObject.getString("access_token");
                    String openid = jsonObject.getString("openid");
                    String refresh_token = jsonObject.getString("refresh_token");

                    Log.d("login","-----access_token"+access_token+"\n"+"-----openid"+openid+"\n-----refresh_token"+refresh_token);


                    if (!access_token.equals("")) {
                        WxSpEditor.putString(PrefParams.ACCESS_TOKEN, access_token);
                        WxSpEditor.apply();
                    }
                    if (!refresh_token.equals("")) {
                        WxSpEditor.putString(PrefParams.REFRESH_TOKEN, refresh_token);
                        WxSpEditor.apply();
                    }
                    if (!openid.equals("")) {
                        WxSpEditor.putString(PrefParams.WXOPENID,openid);
                        WxSpEditor.apply();
                        getPersonMessage(access_token, openid);
                    }
                } catch (JSONException e) {
                    Log.d("login","exception"+e);
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    Log.d("login","throwable"+throwable);
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("login","通过code获取数据没有成功");
            }
        });
    }

    private void getPersonMessage(String access_token,String openid){
        Log.d("login","getPersonMessage");
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
        +access_token
        +"&openid="
        +openid;
        HttpUtil.sendHttpRequest(url,new HttpCallBackListener(){
            @Override
            public void onFinish(String response){
                try{
                    JSONObject jsonObject  = new JSONObject(response);
                    Log.d("login","------获取到的个人信息-------"+jsonObject.toString());
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Exception e){
                Log.d("login","通过openid获取数据没有成功");
//                Toast.makeText(LoginActivity.this,"通过openid获取数据没有成功",Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ReceiveBroadCast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            getAccessToken();
            ll_loading.setVisibility(View.VISIBLE);

            //user debug

            SharedPreferences mSharedPreferences = getSharedPreferences("code",0);
            String code = mSharedPreferences.getString("code","null");
            try {

                if( (send & 1) == 0 ){//应该没什么用...
                    run(code);
                    send++;
                }

            } catch (Exception e) {
                Log.d("TestWX","new throws");
                e.printStackTrace();
            }



        }
    }


    private final OkHttpClient client = new OkHttpClient();

//    private void run(String code){
//        try {
//            GETPOST.run(url,code,client);
//        } catch (Exception e) {
//            DayLog.d("TestWX","error");
//            e.printStackTrace();
//        }
//
//    }


    public void run(final String code) throws Exception{

        new Thread(){
            @Override
            public void run() {
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("code",code)
                        .build();
                Request request=new Request.Builder()
                        .url("https://slow.hustonline.net/api/v1/user/login")
                        .post(requestBody)
                        .build();
                Call call=client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if(response.isSuccessful()){
                            final  String content=response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //加一个登录中暗影
                                    //user debug
//                                    Toast.makeText(LoginActivity.this, "response", Toast.LENGTH_SHORT).show();
                                    Log.d("TestWX","response"+content);
                                    String token = content.substring(content.indexOf("token")+8,content.length()-2);
                                    SPManager.setting_add("token",token,LoginActivity.this);

                                    //这里再开个线程去refresh token
                                    refreshToken(token);



                                }
                            });
                        }else{
                            Log.d("TestWX","error"+response.body().string());
                        }
                    }
                });
            }
        }.start();

    }


    private void refreshToken(final String token){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();

                //String format = String.format("https://slow.hustonline.net/api/v1/user");

                Request build1 = new Request.Builder().url("https://slow.hustonline.net/api/v1/auth/refresh_token")
                        .addHeader("Auth","Bearer "+token)
                        .get()
                        .build();

                okHttpClient.newCall(build1).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String string = response.body().string();
                        Log.d("Tu513","refresh get token ："+string);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String token = string.substring(string.indexOf("token")+8,string.length()-2);
                                SPManager.setting_add("token",token,LoginActivity.this);
                                //这里应该用回调接口，写的真垃圾
                                Intent intent1 = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent1);
                                ll_loading.setVisibility(View.GONE);
                            }
                        });

                    }
                });
            }
        }.start();
    }


    @Override
    public void onBackPressed() {
        //退回桌面
        Intent backHome = new Intent(Intent.ACTION_MAIN);
        backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        backHome.addCategory(Intent.CATEGORY_HOME);
        startActivity(backHome);
    }
}

