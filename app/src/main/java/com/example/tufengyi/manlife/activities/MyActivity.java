package com.example.tufengyi.manlife.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tufengyi.manlife.MyApplication;
import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.DonwloadSaveImg;
import com.example.tufengyi.manlife.utils.tools.ScreenUtils;
import com.example.tufengyi.manlife.view.MyDialog1;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyActivity  extends AppCompatActivity {
    private LinearLayout btn_home,btn_toTimeLine,btn_toProblem,btn_toFlag,btn_toSetting;
    private ImageView btn_addAss;
    private ImageView btn_pen;
    private CircleImageView img_head;
    private EditText edt_name;
    private String FLAG = "TOEDIT";
    private MyDialog1 myDialog1;
    private boolean isMenuOpen = false;

    private List<ImageView> imageViews = new ArrayList<>();


    private ImageView btn_punch,btn_article;
    private View view_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        }
        setContentView(R.layout.activity_mine);
        initViews();
        initDatas();
    }

    private void initDatas(){
        new Thread(){
            @Override
            public void run(){
                int i = 0;
                while(true) {
                    if (MyApplication.userId != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                edt_name.setText(MyApplication.userName);
                            }
                        });
                        if (SPManager.setting_get("img_url", MyActivity.this).equals(MyApplication.userImg)) {

                            if (BitmapFactory.decodeFile(SPManager.setting_get("img_path", MyActivity.this)) == null) {
                                //如果被用户从本地删除了，重新加载下载
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(MyActivity.this).load(MyApplication.userImg).into(img_head);
                                    }
                                });

                                SPManager.setting_add("img_url", MyApplication.userImg, MyActivity.this);
                                DonwloadSaveImg.donwloadImg(MyActivity.this, MyApplication.userImg);
                            }else{
                                //没问题就加载本地
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(MyActivity.this).load(SPManager.setting_get("img_path",MyActivity.this)).into(img_head);
                                    }
                                });

                            }

                        } else {
                            //如果不一致，下载
                            SPManager.setting_add("img_url", MyApplication.userImg, MyActivity.this);
                            DonwloadSaveImg.donwloadImg(MyActivity.this, MyApplication.userImg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(MyActivity.this).load(MyApplication.userImg).into(img_head);
                                }
                            });

                        }
                        break;
                    }
                    try {
                        sleep(500);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(i>=120){//如果一分钟还加载不出来
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyActivity.this, "网络错误，头像无法加载", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
            }
        }.start();

    }

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }

    private void initViews(){

        img_head = (CircleImageView) findViewById(R.id.head_img);


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
        btn_punch = (ImageView) findViewById(R.id.btn_punch);
        btn_punch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(MyActivity.this,R.anim.scale_back);
                btn_addAss.startAnimation(scaleAnimation);
                btn_article.setVisibility(View.GONE);
                btn_punch.setVisibility(View.GONE);
                view_back.setVisibility(View.GONE);
                //菜单状态置关闭
                isMenuOpen = false;
                Intent intent = new Intent(MyActivity.this,AddAssActivity.class);
                startActivity(intent);
            }
        });
        btn_article = (ImageView) findViewById(R.id.btn_article);
        btn_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(MyActivity.this,R.anim.scale_back);
                btn_addAss.startAnimation(scaleAnimation);
                btn_article.setVisibility(View.GONE);
                btn_punch.setVisibility(View.GONE);
                view_back.setVisibility(View.GONE);
                //菜单状态置关闭
                isMenuOpen = false;
                Intent intent = new Intent(MyActivity.this, EditlogActivity.class);
                startActivity(intent);
            }
        });

        imageViews.add(btn_punch);//2
        imageViews.add(btn_article);//1





        btn_toSetting = (LinearLayout) findViewById(R.id.btn_toSettings);
        btn_toSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MyActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });

        btn_toProblem = (LinearLayout) findViewById(R.id.btn_toProblem);
        btn_toProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MyActivity.this,FeedBackActivity.class);
                startActivity(intent);
            }
        });

        btn_toFlag = (LinearLayout) findViewById(R.id.btn_toFlag);
        btn_toFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this,MyFlagsActivity.class);
                startActivity(intent);
            }
        });


        btn_toTimeLine = (LinearLayout) findViewById(R.id.btn_toTimeLine);
        btn_toTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this,TestCalendarActivity.class);
                startActivity(intent);
            }
        });

        btn_home = (LinearLayout) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

//        btn_addAss = (ImageView) findViewById(R.id.btn_addAss);
//        btn_addAss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MyActivity.this,AddAssActivity.class);
//                startActivity(intent);
//            }
//        });
        edt_name = (EditText) findViewById(R.id.edt_name);
         edt_name.setFocusable(false);
        edt_name.setFocusableInTouchMode(false);
        btn_pen = (ImageView) findViewById(R.id.img_pen);
        btn_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
               // DiyDialog1();
//                if(FLAG.equals("TOEDIT")) {
//                    btn_pen.setBackgroundResource(R.drawable.editdark);
//                    FLAG = "TOSAVE";
//                    edt_name.setFocusable(true);
//                    edt_name.setFocusableInTouchMode(true);
//                    //这里添加点击确定的逻辑，或者在dialog中添加对应的逻辑
//
//                }else{
//                    btn_pen.setBackgroundResource(R.drawable.editlight);
//                    edt_name.setFocusable(false);
//                    edt_name.setFocusableInTouchMode(false);
//                    FLAG="TOEDIT";
//                    InputMethodManager imm = (InputMethodManager) MyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    // 隐藏软键盘
//                    imm.hideSoftInputFromWindow(MyActivity.this.getWindow().getDecorView().getWindowToken(), 0);
//                }
            }
        });
        //DiyDialog1();

    }




    private void showDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_normal,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        final EditText edt_name_dialog = view.findViewById(R.id.edt_name);
        LinearLayout btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_name.setText(edt_name_dialog.getText().toString().trim());
                Toast.makeText(MyActivity.this, "changed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)*5/7),LinearLayout.LayoutParams.WRAP_CONTENT);
    }



    private void showOpenAnim(int dp){
        btn_article.setVisibility(View.VISIBLE);
        btn_punch.setVisibility(View.VISIBLE);
        view_back.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(view_back,"alpha",0,0.5f).setDuration(500).start();

        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(MyActivity.this,R.anim.scale);
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
        ObjectAnimator.ofFloat(view_back,"alpha",0.5f,0).setDuration(500).start();

        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(MyActivity.this,R.anim.scale_back);
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

}
