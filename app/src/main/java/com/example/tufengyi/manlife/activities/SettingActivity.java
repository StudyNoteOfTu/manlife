package com.example.tufengyi.manlife.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.db.SPManager;

public class SettingActivity  extends AppCompatActivity {

//    private List<Settings> mSettings = new ArrayList<>();
//    private SettingsDao settingsDao;

    private boolean b_daka = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView back = findViewById(R.id.back);
        final ImageView daka = findViewById(R.id.daka);
;

//        settingsDao = new SettingsDao(SettingActivity.this);
//        mSettings = settingsDao.queryAll();

//        final Settings set1 = mSettings.get(0);
        String sound = SPManager.setting_get("sound",SettingActivity.this);

        if(sound.equals("true")){
            b_daka = true;
            daka.setBackgroundResource(R.drawable.switchup);
        }else{
            b_daka = false;
            daka.setBackgroundResource(R.drawable.switchdown);
        }



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        daka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(b_daka){
                    daka.setBackgroundResource(R.drawable.switchdown);
                    b_daka = false;
                    SPManager.setting_add("sound","false",SettingActivity.this);

                }else {
                    daka.setBackgroundResource(R.drawable.switchup);
                    b_daka = true;
                    SPManager.setting_add("sound","true",SettingActivity.this);

                }
            }
        });


    }
}
