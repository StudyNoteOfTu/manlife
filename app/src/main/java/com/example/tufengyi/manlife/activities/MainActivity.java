package com.example.tufengyi.manlife.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufengyi.manlife.MyApplication;
import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.bean.Sentence;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.DonwloadSaveImg;
import com.example.tufengyi.manlife.utils.dao.DailyAssDao;
import com.example.tufengyi.manlife.utils.dao.SentenceDao;
import com.example.tufengyi.manlife.fragments.RecyclerViewFragment;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static String[] months = {
      "Jan","Feb" ,"Mat","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"
    };
   // private EditText edt_sentence;
   // private ListView lv_dailyAss;
    private ImageView btn_punch,btn_article;
    private View view_back;
    private ImageView btn_addAss;
    private TextView tv_dates;
    private ImageView btn_flag,btn_pen;
    private LinearLayout btn_home,btn_mine;
    private SentenceDao sentenceDao;
    private List<Sentence> mSentence = new ArrayList<Sentence>();
    private DailyAssDao dailyAssDao;
    private List<DailyAssignment> mDailyAss = new ArrayList<DailyAssignment>();
    private String FLAG = "EDIT";
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
    //MyAdapter adapter;


    private boolean isMenuOpen = false;

    private List<ImageView> imageViews = new ArrayList<>();

    String token;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0xfff2b600);

        }
        setContentView(R.layout.activity_main);
        initViews();


        Log.d("TestUser","MainActivityOncreate");
        token = null;//先让他为空，进入一次循环
        new Thread(){
            @Override
            public void run(){
                while(token == null){
                    token = SPManager.setting_get("token",MainActivity.this);
                    if(token!=null) getUser();
                }
            }


        }.start();


//        getUser();

    }



    @Override
    protected  void onResume(){
        super.onResume();
        MobclickAgent.onPageStart("home");
        MobclickAgent.onResume(this);

        RecyclerViewFragment fragment = new RecyclerViewFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(fragmentManager.findFragmentById(R.id.viewpager)==null) {
            transaction.add(R.id.viewpager, fragment);
        }else{
            transaction.replace(R.id.viewpager, fragment);
        }
        transaction.commit();



    }

    private void refreshToken(){
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
                                SPManager.setting_add("token",token,MainActivity.this);
                            }
                        });

                    }
                });
            }
        }.start();
    }


    //获得用户信息
    private void getUser(){
        OkHttpClient okHttpClient = new OkHttpClient();

        //String format = String.format("https://slow.hustonline.net/api/v1/user");


        Log.d("TestUser","token----"+token);

        Request build1 = new Request.Builder().url("https://slow.hustonline.net/api/v1/user")
                .addHeader("Auth","Bearer "+token)
                .get()
                .build();

        okHttpClient.newCall(build1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d("TestUser","userget---"+string);

                if(string.contains("expired")||string.contains("fail")){//过期?
                    //重新登录
                    Toast.makeText(MainActivity.this, "登录过期", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }

                //开始解析JSONObject
                try{
                    String jsontarget = string.substring(string.indexOf("\"data\"")+7,string.length()-1);//左闭右开
                    Log.d("TestUser","getjsontarget"+jsontarget);
                    JSONObject jsonObject = new JSONObject(jsontarget);

                    final String userName = jsonObject.getString("name");
                    String userImg =jsonObject.getString("img_url");
                    String userId = jsonObject.getString("id");
                    String wx_id = jsonObject.getString("wx_id");


                    MyApplication.userId = userId;
                    MyApplication.userImg = userImg;
                    MyApplication.userName = userName;
                    MyApplication.wx_id = wx_id;

                    //当用户使用自有账号登录时，可以这样统计：
                    MobclickAgent.onProfileSignIn(userId);

                    if(SPManager.setting_get("img_url",MainActivity.this).equals(userImg)){

                        if(BitmapFactory.decodeFile(SPManager.setting_get("img_path",MainActivity.this))==null){
                            //如果被用户从本地删除了，重新下载
                            SPManager.setting_add("img_url",userImg,MainActivity.this);
                            DonwloadSaveImg.donwloadImg(MainActivity.this,userImg);
                        }

                    }else{
                        //如果不一致，下载
                        SPManager.setting_add("img_url",userImg,MainActivity.this);
                        DonwloadSaveImg.donwloadImg(MainActivity.this,userImg);
                    }

                    Log.d("TestMyApplication","userid"+MyApplication.userId);

//                    JSONArray jsonArray = new JSONArray(string);
//                    for(int i = 0; i<jsonArray.length();i++){
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        JSONObject data = jsonObject.getJSONObject("data");
//                        String userName = data.getString("name");
//                        String userImg = data.getString("img_url");
//                        String userId = data.getString("id");
//                        MyApplication.userId = userId;
//                        MyApplication.userImg = userImg;
//                        MyApplication.userName = userName;
//                        DayLog.d("TestMyApplication","userid"+MyApplication.userId);
//                    }
                }catch(Exception e){
                    Log.d("TestUser","getJSON Error");
                    e.printStackTrace();
                }




            }
        });


    }



    @Override
    protected void onPause(){
        super.onPause();
        MobclickAgent.onPageEnd("home");
        MobclickAgent.onPause(this);
        overridePendingTransition(0,0);
    }


    private void initViews(){

        ImageView img_toTimeLine = findViewById(R.id.btn_toTimeLine);
        img_toTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TestCalendarActivity.class);
                startActivity(intent);
            }
        });


        view_back = findViewById(R.id.darkback);
        view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMenuOpen){
                    showOpenAnim(80);
                }else{
                    showCloseAnim(80);
                }
            }
        });
        btn_addAss =(ImageView) findViewById(R.id.btn_addAss);
        btn_addAss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMenuOpen){
                    showOpenAnim(80);
                }else{
                    showCloseAnim(80);
                }
            }
        });

//        btn_addAss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,AddAssActivity.class);
//                startActivity(intent);
//            }
//        });

        tv_dates = (TextView) findViewById(R.id.tv_dates);
        setTodayText();

        btn_punch = (ImageView) findViewById(R.id.btn_punch);
        btn_punch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(MainActivity.this,R.anim.scale_back);
                btn_addAss.startAnimation(scaleAnimation);
                btn_article.setVisibility(View.GONE);
                btn_punch.setVisibility(View.GONE);
                view_back.setVisibility(View.GONE);
                //菜单状态置关闭
                isMenuOpen = false;
                Intent intent = new Intent(MainActivity.this,AddAssActivity.class);
                startActivity(intent);
            }
        });
        btn_article = (ImageView) findViewById(R.id.btn_article);
        btn_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(MainActivity.this,R.anim.scale_back);
                btn_addAss.startAnimation(scaleAnimation);
                btn_article.setVisibility(View.GONE);
                btn_punch.setVisibility(View.GONE);
                view_back.setVisibility(View.GONE);
                //菜单状态置关闭
                isMenuOpen = false;
                Intent intent = new Intent(MainActivity.this, EditlogActivity.class);
                startActivity(intent);
            }
        });

        imageViews.add(btn_punch);//2
        imageViews.add(btn_article);//1


        btn_home = (LinearLayout) findViewById(R.id.btn_home);
        btn_mine = (LinearLayout) findViewById(R.id.btn_mine);
        btn_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(MainActivity.this,)
                //Intent intent = new Intent(MainActivity.this,MyActivity.class);
                Intent intent = new Intent(MainActivity.this,MyActivity.class);
                startActivity(intent);
            }
        });
        TextView btn_addFlag = (TextView) findViewById(R.id.btn_addFlag) ;
        btn_addFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FlagsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showOpenAnim(int dp){
        btn_article.setVisibility(View.VISIBLE);
        btn_punch.setVisibility(View.VISIBLE);
        view_back.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(view_back,"alpha",0,0.5f).setDuration(500).start();

        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(MainActivity.this,R.anim.scale);
        btn_addAss.startAnimation(scaleAnimation);

        //for循环来开始小图标的出现动画
        for (int i = 0; i < imageViews.size(); i++) {
            AnimatorSet set = new AnimatorSet();
            //标题1与x轴负方向角度为20°，标题2为100°，转换为弧度
            double a = -Math.cos(60 * Math.PI / 180 * (i +1));
            double b = -Math.sin(60 * Math.PI / 180 * (i +1));
            double x = a * dip2px(dp);
            double y = b * dip2px(dp);

            set.playTogether(
                    ObjectAnimator.ofFloat(imageViews.get(i), "translationX", (float) (x * 0.25), (float) x),
                    ObjectAnimator.ofFloat(imageViews.get(i), "translationY", (float) (y * 0.25), (float) y)
                    , ObjectAnimator.ofFloat(imageViews.get(i), "alpha", 0, 1).setDuration(250)
            );
            //set.setInterpolator(new HesitateInterpolator());
            set.setDuration(300);
            set.start();

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    //菜单状态置打开
                    isMenuOpen = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

        //转动加号大图标本身45°
        ObjectAnimator rotate = ObjectAnimator.ofFloat(btn_addAss, "rotation", 0, 90).setDuration(300);
        //rotate.setInterpolator(new BounceInterpolator());
        rotate.start();
        }

    private void showCloseAnim(int dp){
        ObjectAnimator.ofFloat(view_back,"alpha",0.5f,0).setDuration(300).start();

        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(MainActivity.this,R.anim.scale_back);
        btn_addAss.startAnimation(scaleAnimation);

        //for循环来开始小图标的出现动画
        for (int i = 0; i < imageViews.size(); i++) {
            AnimatorSet set = new AnimatorSet();
            double a = -Math.cos(60 * Math.PI / 180 * (i + 1));
            double b = -Math.sin(60 * Math.PI / 180 * (i + 1));
            double x = a * dip2px(dp);
            double y = b * dip2px(dp);

            set.playTogether(
                    ObjectAnimator.ofFloat(imageViews.get(i), "translationX", (float) x, (float) (x * 0.25)),
                    ObjectAnimator.ofFloat(imageViews.get(i), "translationY", (float) y, (float) (y * 0.25)),
                    ObjectAnimator.ofFloat(imageViews.get(i), "alpha", 1, 0).setDuration(250)
            );
//      set.setInterpolator(new AccelerateInterpolator());
            set.setDuration(300);
            set.start();

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    btn_article.setVisibility(View.GONE);
                    btn_punch.setVisibility(View.GONE);
                    view_back.setVisibility(View.GONE);

                    //菜单状态置关闭
                    isMenuOpen = false;




                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }

        //转动加号大图标本身45°
        ObjectAnimator rotate = ObjectAnimator.ofFloat(btn_addAss, "rotation", 0, 90).setDuration(300);
        //rotate.setInterpolator(new BounceInterpolator());
        rotate.start();
    }

    private int dip2px(int value) {
        float density = getResources()
                .getDisplayMetrics().density;
        return (int) (density * value + 0.5f);
    }



    private void setTodayText(){
        String today_Month="Jan",today_last="th";
        Calendar cal = Calendar.getInstance();
        for(int i =0;i<12;i++){
            if(cal.get(Calendar.MONTH)==i){
                today_Month = months[i];
                break;
            }
        }
        switch(cal.get(Calendar.DAY_OF_MONTH)%10){
            case 1:
                today_last="st";
                break;
            case 2:
                today_last = "nd";
                break;
            case 3:
                today_last = "rd";
                break;
        }
        String today = today_Month+"."+cal.get(Calendar.DAY_OF_MONTH)+today_last;
        tv_dates.setText(today);
    }

//    private class MyAdapter extends BaseAdapter {
//
//        //对应要展现的是mData数据，所以这里面使用mData而不是mList
//        public int getCount(){
//            return mDailyAss.size();
//            // return 1000;
//        }
//        public Object getItem(int position){
//            return mDailyAss.get(position);
//            //return null;
//        }
//        public long getItemId(int position){
//            return position;
//            //return 0;
//        }
//        @SuppressLint("SetTextI18n")
//        public View getView(final int position, View convertView, final ViewGroup parent){
//            final ViewHolder viewHolder;
//            if (convertView == null) {
//                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, null);
//                viewHolder = new ViewHolder();
//                final DailyAssignment mDailyAss_temp = mDailyAss.get(position);
//                viewHolder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
//                //viewHolder.ll_item.setBackgroundColor();
//                viewHolder.img_ass = (ImageView) convertView.findViewById(R.id.img_ass);
//                viewHolder.img_ass.setBackgroundResource(icons[mDailyAss_temp.getIcon()]);
//
//                viewHolder.tv_ass = (TextView) convertView.findViewById(R.id.tv_dailyAss);
//                viewHolder.tv_ass.setText(mDailyAss_temp.getTitle());
//
//                viewHolder.tv_progress = (TextView) convertView.findViewById(R.id.tv_progress);
//                viewHolder.tv_progress.setText("已坚持"+String.valueOf(mDailyAss_temp.getProgress())+"天");
//
//                viewHolder.btn_finish = (ImageView) convertView.findViewById(R.id.btn_finish);
//                if (mDailyAss_temp.getFinish().equals("yes"))
//                {
//                    viewHolder.btn_finish.setVisibility(GONE);
//                    viewHolder.ll_item.setBackgroundColor(Color.GREEN);
//                }
//                viewHolder.btn_finish.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dailyAssDao = new DailyAssDao(MainActivity.this);
//                        viewHolder.ll_item.setBackgroundColor(Color.GREEN);
//                        viewHolder.btn_finish.setVisibility(GONE);
//                        DailyAssignment dailyAss_temp = new DailyAssignment(mDailyAss_temp.getId(),
//                                mDailyAss_temp.getTitle(),mDailyAss_temp.getIcon(),
//                                "yes",mDailyAss_temp.getProgress()+1);
//                        dailyAssDao.update(dailyAss_temp);
//                        viewHolder.tv_progress.setText("已坚持"+String.valueOf(mDailyAss_temp.getProgress()+1)+"天");
//                    }
//                });
//
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//
//
//            return convertView;
//        }
//    }
//
//    class ViewHolder{
//        ImageView img_ass;
//        TextView tv_ass;
//        TextView tv_progress;
//        ImageView btn_finish;
//        LinearLayout ll_item;
//    }
//
//    private void DiyDialog2() {
//        AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(MainActivity.this,R.style.MyDialog);
//        alterDiaglog.setView(R.layout.dialog_succeeded);//加载进去
//        AlertDialog dialog = alterDiaglog.create();
//        //显示
//        dialog.show();
//        //自定义的东西
//    }
    @Override
    public void onBackPressed(){
        Intent backHome = new Intent(Intent.ACTION_MAIN);
        backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        backHome.addCategory(Intent.CATEGORY_HOME);
        startActivity(backHome);
    }

}
