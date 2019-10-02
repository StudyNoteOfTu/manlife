package com.example.tufengyi.manlife.activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufengyi.manlife.MyApplication;
import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.base.BaseActivity;
import com.example.tufengyi.manlife.bean.DayLog;

import com.example.tufengyi.manlife.bean.Flag;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.RedirectInterceptor;
import com.example.tufengyi.manlife.utils.tools.DateUtil;
import com.example.tufengyi.manlife.view.ColorfulMonthView;
import com.example.tufengyi.manlife.view.ColorfulWeekView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LogDetActivity extends BaseActivity implements
    CalendarView.OnCalendarSelectListener,
    CalendarView.OnCalendarLongClickListener,
    CalendarView.OnMonthChangeListener,
    CalendarView.OnYearChangeListener,
    CalendarView.OnWeekChangeListener,
    CalendarView.OnViewChangeListener,
    CalendarView.OnCalendarInterceptListener{


//
        private TextView tv_year1;
        //private List<Player> playerList = new ArrayList<>();
        private List<DayLog> mDayLogs = new ArrayList<>();
//
//        private RecyclerView recyclerView;
        private List<DayLog> mSelectedDayLogs = new ArrayList<>();
//        private LogsAdapter adapter;

        TextView tv_log;


        TextView mTextMonthDay;

        TextView mTextYear;

        TextView mTextLunar;

        TextView mTextCurrentDay;

        CalendarView mCalendarView;

        RelativeLayout mRelativeTool;
        private int mYear;
        //CalendarLayout mCalendarLayout;

        String date;



        @Override
        protected void obtainIntent(){
            Intent intent = getIntent();
            date = intent.getStringExtra("date");
        }


        @Override
        protected int getLayoutId() {
            return R.layout.activity_article;
        }

        private void initList(){

            new Thread(){
                @Override
                public void run(){

                    String token = SPManager.setting_get("token",LogDetActivity.this);

                    OkHttpClient okHttpClient = new OkHttpClient();

                    Request build1 = new Request.Builder().url("https://slow.hustonline.net/api/v1/record")
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
                            Log.d("TestLog","userget---"+string);
                            //获取其中的数组，可以进行封装， 开头结束通过 最近的[]进行判断
                            try{
                                String arrayString = string.substring(string.indexOf("records")+9,string.length()-2);
                                Log.d("TestLog","arrayString"+arrayString);
                                JSONArray logs = new JSONArray(arrayString);
                                Log.d("TestLog","logs length"+logs.length());
                                for(int i = 0; i< logs.length(); i++){
                                    JSONObject jsonObject_inner = logs.getJSONObject(i);
                                    mDayLogs.add(new DayLog(jsonObject_inner.getString("id"),
                                            DateUtil.stampToDate(jsonObject_inner.getLong("time")),
                                            jsonObject_inner.getLong("time"),
                                            jsonObject_inner.getString("content")));
                                }

                                //第一次
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSelectedDayLogs.clear();
                                        //这个部分添加日志
                                        Log.d("TestLog","0-----------"+date);
                                        for (int j = 0; j< mDayLogs.size(); j++){
                                            if (mDayLogs.get(j).getDate().equals(date.substring(0,10))) {
                                                mSelectedDayLogs.add(mDayLogs.get(j));
                                                tv_log.setText(mDayLogs.get(j).getContent());
                                            }
                                        }
                                        if(mSelectedDayLogs.size()==0){
                                            DayLog dayLog = new DayLog();
                                            dayLog.setContent("这天没有日志哦");
                                            mSelectedDayLogs.add(dayLog);
                                            tv_log.setText("这天没有日志哦");
                                        }
//                                        adapter.notifyDataSetChanged();
                                    }
                                });


                                initData();

                            }catch(Exception e){
                                Log.d("TestLog",""+e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });



                }
            }.start();


//            //时间yyyy-MM-DD
//            //排序
//            Collections.sort(mDayLogs, new Comparator<DayLog>() {
//                @Override
//                public int compare(DayLog o1, DayLog o2) {
//                    long num1 = DateUtil.stringToDate(o1.getDate());
//                    long num2 = DateUtil.stringToDate(o2.getDate());
//                    if (num1 > num2) {
//                        return 1;
//                    } else if (num1 < num2) {
//                        return -1;
//                    } else {
//                        return 0;
//                    }
//                }
//            });
//
//            int length = mDayLogs.size();
//
//            StringBuffer sb = new StringBuffer();
//            java.util.Calendar cal_buff = java.util.Calendar.getInstance();
//
//            sb.append(cal_buff.get(java.util.Calendar.YEAR));
//            sb.append("-");
//            if(cal_buff.get(java.util.Calendar.MONTH)+1>=0 && cal_buff.get(java.util.Calendar.MONTH)+1<10){
//                sb.append("0");
//            }
//            sb.append(cal_buff.get(java.util.Calendar.MONTH)+1);
//            sb.append("-");
//            if(cal_buff.get(java.util.Calendar.DAY_OF_MONTH)>=0 && cal_buff.get(java.util.Calendar.DAY_OF_MONTH)<10){
//                sb.append("0");
//            }
//            sb.append(cal_buff.get(java.util.Calendar.DAY_OF_MONTH));
//            //格式 yyyy-MM-dd
//            //这里可能会出问题，如何把StringBuffer转为String
//            String date = String.valueOf(sb);

        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void initView() {

//            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//            LinearLayoutManager linearLayoutManager =new LinearLayoutManager(LogDetActivity.this);
//            recyclerView.setLayoutManager(linearLayoutManager);
//            adapter = new LogsAdapter();
//            recyclerView.setAdapter(adapter);
//            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//                @Override
//                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                    super.getItemOffsets(outRect, view, parent, state);
//                    outRect.set(16, 16, 16, 8);
//                }
//
//            });


            Intent intent = getIntent();
            final String catchDate = intent.getStringExtra("date")==null? "":intent.getStringExtra("date");


            tv_log = findViewById(R.id.tv_log);
            tv_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSelectedDayLogs.get(0).getContent().equals("这天没有日志哦")){
                        Toast.makeText(LogDetActivity.this, "这天没有日志哦", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(LogDetActivity.this,EditlogActivity.class);
                        intent.putExtra("log",mSelectedDayLogs.get(mSelectedDayLogs.size()-1).getContent());
                        intent.putExtra("log_id",mSelectedDayLogs.get(mSelectedDayLogs.size()-1).getId());
                        intent.putExtra("log_time",mSelectedDayLogs.get(mSelectedDayLogs.size()-1).getStamp());
                        startActivityForResult(intent,1);
                        //后面改成forresult
                    }
                }
            });

//            tv_log.setMovementMethod(ScrollingMovementMethod.getInstance());
            tv_year1 = findViewById(R.id.tv_year1);
            initList();

//            for (int k = 0; k< mDayLogs.size(); k++){
//                if(!catchDate.isEmpty() && mDayLogs.get(k).getDate().equals(catchDate)){
//                    tv_log.setText(mDayLogs.get(k).getContent());
//                }
//            }



            setStatusBarDarkMode();

            ImageView btn_back = (ImageView) findViewById(R.id.back);
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });





            ImageView pre = (ImageView) findViewById(R.id.pre);
            pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCalendarView.scrollToPre();
                }
            });
            ImageView next = (ImageView) findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCalendarView.scrollToNext();
                }
            });

            ImageView today = (ImageView) findViewById(R.id.today);
            today.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCalendarView.scrollToCurrent();
                }
            });

            mTextMonthDay = (TextView) findViewById(R.id.tv_month_day);
            mTextYear = (TextView) findViewById(R.id.tv_year);
            mTextLunar = (TextView) findViewById(R.id.tv_lunar);
            mRelativeTool = (RelativeLayout) findViewById(R.id.rl_tool);
            mCalendarView = (CalendarView) findViewById(R.id.calendarView);
            mCalendarView.setWeekView(ColorfulWeekView.class);
            mCalendarView.setMonthView(ColorfulMonthView.class);



            mTextCurrentDay = (TextView) findViewById(R.id.tv_current_day);
            mTextMonthDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (!mCalendarLayout.isExpand()) {
//                        mCalendarView.showYearSelectLayout(mYear);
//                        return;
//                    }
//                    mCalendarView.showYearSelectLayout(mYear);
//                    mTextLunar.setVisibility(View.GONE);
//                    mTextYear.setVisibility(View.GONE);
//                    mTextMonthDay.setText(String.valueOf(mYear));
                }
            });
//        findViewById(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerView.smoothScrollToPosition(7);
//                //View childView = recyclerView.getLayoutManager().findViewByPosition(35);
//                //childView.scrollTo(0,0);
//                Toast.makeText(TestCalendarActivity.this, "7", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        findViewById(R.id.iv_func).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerView.smoothScrollToPosition(30);
//                Toast.makeText(TestCalendarActivity.this, "30", Toast.LENGTH_SHORT).show();
//
//            }
//        });


            //mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
            mCalendarView.setOnYearChangeListener(this);
            mCalendarView.setOnCalendarSelectListener(this);
            mCalendarView.setOnMonthChangeListener(this);
            mCalendarView.setOnCalendarLongClickListener(this, true);
            mCalendarView.setOnWeekChangeListener(this);

            //设置日期拦截事件，仅适用单选模式，当前无效
            mCalendarView.setOnCalendarInterceptListener(this);

            mCalendarView.setOnViewChangeListener(this);
            mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
            mYear = mCalendarView.getCurYear();
            mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
            mTextLunar.setText("今日");

            tv_year1.setText(String.valueOf(mCalendarView.getCurYear()));

            mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        }

        @SuppressWarnings("unused")
        @Override
        protected void initData() {


            Map<String, Calendar> map = new HashMap<>();

            int length = mDayLogs.size();

            for(int i=0;i<length;i++) {
                //yyyy-mm-dd
                //0123456789
                DayLog pAss = mDayLogs.get(i);
                    map.put(getSchemeCalendar(Integer.parseInt(pAss.getDate().substring(0, 4)), Integer.parseInt(pAss.getDate().substring(5, 7)), Integer.parseInt(pAss.getDate().substring(8)), 0xfffc107, "").toString(),
                            getSchemeCalendar(Integer.parseInt(pAss.getDate().substring(0, 4)), Integer.parseInt(pAss.getDate().substring(5, 7)), Integer.parseInt(pAss.getDate().substring(8)), 0xffffc107, ""));

            }

            Log.d("TestLog","length"+map.size());

//            mSelectedDayLogs.clear();

//            for (int j = 0; j< mDayLogs.size(); j++){
//                if (mDayLogs.get(j).getDate().equals(date)) {
////                    mSelectedDayLogs.add(mDayLogs.get(j));
////                    tv_log.setText(mDayLogs.get(j).getContent());
//                    //这里未来会修改，break只是为了获取第一个日志
//                    break;
//                }else{
////                    tv_log.setText("这天没有日志噢！");
//                }
//            }
//            adapter.notifyDataSetChanged();




            mCalendarView.setSchemeDate(map);

        }


        //对日历进行操作
        private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
            Calendar calendar = new Calendar();
            calendar.setYear(year);
            calendar.setMonth(month);
            calendar.setDay(day);
            calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
            calendar.setScheme(text);
            return calendar;
        }


        @Override
        public void onCalendarOutOfRange(Calendar calendar) {
       //     Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onCalendarSelect(Calendar calendar, boolean isClick) {

            //DayLog.e("onDateSelected", "  -- " + calendar.getYear() + "  --  " + calendar.getMonth() + "  -- " + calendar.getDay());
            mTextLunar.setVisibility(View.VISIBLE);
            mTextYear.setVisibility(View.VISIBLE);
            mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
            mTextYear.setText(String.valueOf(calendar.getYear()));
            tv_year1.setText(String.valueOf(calendar.getYear()));
            mTextLunar.setText(calendar.getLunar());
            mYear = calendar.getYear();
            if (isClick) {  //点击事件在此设置
                //for(int i = 0; i< mDayLogs.size(); i++){
                    String year =String.valueOf(calendar.getYear());
                    int tempMonth = calendar.getMonth();
                    String month = tempMonth<10 ? "0"+String.valueOf(tempMonth) : String.valueOf(tempMonth);
                    int tempDay = calendar.getDay();
                    String day = tempDay < 10 ? "0"+String.valueOf(tempDay) : String.valueOf(tempDay);
                    Log.d("TestLog",year+"-"+month+"-"+day);

                    tv_year1.setText(String.valueOf(year));


                    mSelectedDayLogs.clear();

                    //这个部分添加日志
                    for (int j = 0; j< mDayLogs.size(); j++){

                        if (mDayLogs.get(j).getDate().equals(year+"-"+month+"-"+day)) {
                            mSelectedDayLogs.add(mDayLogs.get(j));
                            Log.d("TestLog","increased"+mSelectedDayLogs.size());
                            tv_log.setText(mDayLogs.get(j).getContent());
                            //这里未来会修改，break只是为了获取最后一个日志
                        }
                    }

                    if(mSelectedDayLogs.size()==0){
                        DayLog dayLog = new DayLog();
                        dayLog.setContent("这天没有日志哦");
                        mSelectedDayLogs.add(dayLog);
                        tv_log.setText("这天没有日志哦");
                    }

//                    adapter.notifyDataSetChanged();
                    //如果没有内容，就加一个空的Daylog



                   // int length = mDayLogs.size();

//                    if(mObjects.get(i)!=null){
//                        if(mObjects.get(i) instanceof PunchedAss && ((PunchedAss) mObjects.get(i)).getDate().equals(year+"-"+month+"-"+day)){
//                            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
//                            moveToPosition(i);
//                            break;
//                        }
//                    }



                //}

            }

        }

        @Override
        public void onCalendarLongClickOutOfRange(Calendar calendar) {
            //Toast.makeText(this, String.format("%s : LongClickOutOfRange", calendar), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCalendarLongClick(Calendar calendar) {
            // Toast.makeText(this, "长按", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "长按不选择日期\n" + getCalendarText(calendar), Toast.LENGTH_SHORT).show();
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onMonthChange(int year, int month) {
            //DayLog.e("onMonthChange", "  -- " + year + "  --  " + month);
            Calendar calendar = mCalendarView.getSelectedCalendar();
            mTextLunar.setVisibility(View.VISIBLE);
            mTextYear.setVisibility(View.VISIBLE);
            mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
            mTextYear.setText(String.valueOf(calendar.getYear()));
            tv_year1.setText(String.valueOf(calendar.getYear()));
            Log.d("TestCalendar",calendar.getYear()+" year");
//            tv_year1.setText(String.valueOf(mCalendarView.getCurYear()));
//            Log.d("TestCalendar",mCalendarView.getCurYear()+" --year");
            mTextLunar.setText(calendar.getLunar());
            mYear = calendar.getYear();
        }

        @Override
        public void onViewChange(boolean isMonthView) {
            //DayLog.e("onViewChange", "  ---  " + (isMonthView ? "月视图" : "周视图"));
        }


        @Override
        public void onWeekChange(List<Calendar> weekCalendars) {
        }

        /**
         * 屏蔽某些不可点击的日期，可根据自己的业务自行修改
         *
         * @param calendar calendar
         * @return 是否屏蔽某些不可点击的日期，MonthView和WeekView有类似的API可调用
         */
        @Override
        public boolean onCalendarIntercept(Calendar calendar) {
            int day = calendar.getDay();
            return day == 1 || day == 3 || day == 6 || day == 11 || day == 12 || day == 15 || day == 20 || day == 26;
        }

        @Override
        public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {
            // Toast.makeText(this, calendar.toString() + "拦截不可点击", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onYearChange(int year) {
            // mTextMonthDay.setText(String.valueOf(year));
            tv_year1.setText(String.valueOf(year));
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case 1:
                if(resultCode == 1){
                    //添加flag
//                    intent.putExtra("log",mSelectedDayLogs.get(mSelectedDayLogs.size()-1).getContent());
//                    intent.putExtra("log_id",mSelectedDayLogs.get(mSelectedDayLogs.size()-1).getId());
//                    intent.putExtra("log_time",mSelectedDayLogs.get(mSelectedDayLogs.size()-1).getStamp());
                    if(data !=null){
                        String log = data.getStringExtra("log");
                        String log_id = data.getStringExtra("log_id");
                        String log_time = data.getStringExtra("log_time");
                        mSelectedDayLogs.get(mSelectedDayLogs.size()-1).setContent(log);
                        mSelectedDayLogs.get(mSelectedDayLogs.size()-1).setId(log_id);

                    }
                }else if(resultCode == 2){
                    if(data != null) {
                        String log = data.getStringExtra("");
                        mSelectedDayLogs.get(mSelectedDayLogs.size()-1).setContent(log);
                    }
                }
                break;
        }
    }
}
