package com.example.tufengyi.manlife.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.utils.dao.DailyAssDao;
import com.example.tufengyi.manlife.utils.tools.ScreenShot;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {


    String[] PERMISSIONS = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private ImageView back,gou;
    private TextView tv_date;
    private ListView listView;
    private List<DailyAssignment> list = new ArrayList<DailyAssignment>();
    private List<DailyAssignment> temp = new ArrayList<DailyAssignment>();
    private MyListAdapter adapter;

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
    private DailyAssDao dao;

//    Bitmap cachebmp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
//
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
        String date = String.valueOf(sb);


        //这里改成从后台获取，加载中的图就提示用户正在加载图片

        dao = new DailyAssDao(this);
        temp = dao.queryAll();
        for(int i=0;i<temp.size();i++){
            if(temp.get(i).getDate().equals(date)){
                list.add(temp.get(i));
            }
        }

        initViews();

        int permission = ContextCompat.checkSelfPermission(PreviewActivity.this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(PreviewActivity.this, PERMISSIONS,1);
        }

//        DisplayMetrics metric = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int width = metric.widthPixels;     // 屏幕宽度（像素）
//        int height = metric.heightPixels;   // 屏幕高度（像素）
//        View view = LayoutInflater.from(this).inflate(R.layout.activity_preview, null, false);
//
//        layoutView(view,width,height);
    }

    private void initViews(){
        tv_date = (TextView) findViewById(R.id.tv_date);
        Calendar cal = Calendar.getInstance();
        String date = "我的"+String.valueOf(cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH)+"日";
        tv_date.setText(date);

        listView = findViewById(R.id.listView);
        adapter = new MyListAdapter(list);
        listView.setAdapter(adapter);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gou = findViewById(R.id.gou);
        gou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics metric = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metric);
                int width = metric.widthPixels;     // 屏幕宽度（像素）
                int height = metric.heightPixels;   // 屏幕高度（像素）
                View view_subTitle = LayoutInflater.from(PreviewActivity.this).inflate(R.layout.layout_subtitle, null, false);

                layoutView(view_subTitle,width,height);

                ScrollView sv = view_subTitle.findViewById(R.id.scrollView);

                ImageView iv = view_subTitle.findViewById(R.id.img_back);

                Bitmap img_back = loadBitmapFromView(iv);

                Bitmap subTitle = loadBitmapFromView(sv);

                View tv_pre = LayoutInflater.from(PreviewActivity.this).inflate(R.layout.activity_preview,null,false);
                layoutView(tv_pre,width,height);

                TextView tv = tv_pre.findViewById(R.id.tv_date);
                Calendar cal = Calendar.getInstance();
                String date = "我的"+String.valueOf(cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH)+"日";
                tv.setText(date);

                Bitmap tv_bit = loadBitmapFromView(tv);

                ScreenShot.saveImageToGallery(PreviewActivity.this,ScreenShot.createBitmap(subTitle,img_back,tv_bit,listView,PreviewActivity.this),"慢慢");

                Toast.makeText(PreviewActivity.this, "已保存至相册", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    private void layoutView(View v, int width, int height) {
        // 整个View的大小 参数是左上角 和右下角的坐标
        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST);
        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
         */
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }


    class MyListAdapter extends BaseAdapter {
        private List<DailyAssignment> mList;
        MyListAdapter(List<DailyAssignment> list){
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) PreviewActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_list, null);
                holder = new ViewHolder();
                holder.img = convertView.findViewById(R.id.img);
                holder.name = (TextView) convertView.findViewById(R.id.tv_dailyAss);
                holder.progress = convertView.findViewById(R.id.tv_progress);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.img.setBackgroundResource(icons[mList.get(position).getIcon()]);
            holder.name.setText(mList.get(position).getTitle());
            holder.progress.setText("已经坚持"+mList.get(position).getProgress()+"天");

            return convertView;
        }

        class ViewHolder {
            ImageView img;
            TextView name;
            TextView progress;
        }
    }

}
