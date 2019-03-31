package com.example.tufengyi.manlife.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.utils.mail.SendMailUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedBackActivity extends AppCompatActivity {
    private String toEmail = "1412540290@qq.com";
    private String ccEmail = "tu17371442015@163.com";

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initViews();

    }

    private void initViews(){
        final EditText edt_problem = findViewById(R.id.edt_problem);
        final EditText edt_connect = findViewById(R.id.edt_connect);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String problem = edt_problem.getText().toString();
                String connect = edt_connect.getText().toString();

                if(problem.isEmpty()&&connect.isEmpty()){
                    Toast.makeText(FeedBackActivity.this, "请输入遇见的问题和您的联系方式噢！", Toast.LENGTH_SHORT).show();
                }else {
                    sendTextMail(problem, connect);
                }
            }
        });

//                Intent data = new Intent(Intent.ACTION_SENDTO);
//                data.setData(Uri.parse("mailto:820218696@qq.com"));
//                data.putExtra(Intent.EXTRA_SUBJECT,"问题反馈");
//                data.putExtra(Intent.EXTRA_TEXT,edt_problem.getText().toString());
//                startActivity(data);

//                Toast toast = new Toast(getApplicationContext());
//                //创建一个填充物，用于填充Toast
//                LayoutInflater inflater = LayoutInflater.from(FeedBackActivity.this);
//                //填充物来自的xml文件，在这里改成一个view
//                //实现xml到view的转变
//                View view = inflater.inflate(R.layout.toast_ok,null);
//                //不一定需要，找到xml里面的组件，摄制组建里面的具体内容
////                ImageView imageView1 = view.findViewById(R.id.img_toast);
////                TextView textView1 = view.findViewById(R.id.tv_toast);
////                imageView1.setImageResource(R.drawable.smile);
////                textView1.setText("哈哈哈哈哈");
//                toast.setView(view);
//                toast.setGravity(Gravity.CENTER,0,0);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                showMyToast(toast,1000);
//                Intent intent = new Intent(FeedBackActivity.this,MyActivity.class);
//                startActivity(intent);
//            }
//        });
    }


    private void sendTextMail(String problem,String connect){
        SendMailUtil.send("2847701186@qq.com","------问题反馈："+problem+"\n"+"------联系方式："+connect);
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
