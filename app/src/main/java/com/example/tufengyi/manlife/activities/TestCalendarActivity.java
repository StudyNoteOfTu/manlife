package com.example.tufengyi.manlife.activities;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.base.BaseActivity;
import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.bean.DayLog;
import com.example.tufengyi.manlife.bean.Empty;
import com.example.tufengyi.manlife.bean.HeadTitle;
import com.example.tufengyi.manlife.bean.PunchedAss;
import com.example.tufengyi.manlife.bean.SubTitle;
//import com.example.tufengyi.manlife.utils.dao.PunchedAssDao;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.tools.DateUtil;
import com.example.tufengyi.manlife.utils.tools.ObjectsAdapter;
import com.example.tufengyi.manlife.utils.tools.ViewHolderEmpty;
import com.example.tufengyi.manlife.utils.tools.ViewHolderPunched;
import com.example.tufengyi.manlife.utils.tools.ViewHolderSubTitle;
import com.example.tufengyi.manlife.utils.tools.ViewHolderTopTitle;
import com.example.tufengyi.manlife.view.ColorfulMonthView;
import com.example.tufengyi.manlife.view.ColorfulWeekView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
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
import java.util.logging.LogRecord;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TestCalendarActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnCalendarLongClickListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener,
        CalendarView.OnWeekChangeListener,
        CalendarView.OnViewChangeListener,
        CalendarView.OnCalendarInterceptListener{

    private TextView tv_year1;

    private RecyclerView recyclerView;
    ObjectsAdapter adapter;
    //private List<Player> playerList = new ArrayList<>();
    private List<PunchedAss> mPunchedAss = new ArrayList<>();
    private List<Object> mObjects =new ArrayList<>();
//    private PunchedAssDao punchedAssDao;
    private LinearLayoutManager linearLayoutManager;

    private boolean move = false;

    private int mIndex=0;

    private int requestCount = 0;

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;

    Map<String, Calendar> map = new HashMap<>();


    private boolean isMenuOpen = false;
    private LinearLayout ll_article,ll_print,ll_menu;
    private View darkView;
    private ImageView btn_more;

    @SuppressLint("HandlerLeak")
    private android.os.Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    if(requestCount<1){
                        Log.d("TestLog","count"+requestCount);
                        requestCount++;
                    }else{
                        Log.d("TestLog","begin init list");
                        initList();
                        requestCount=0;
                    }
                    break;
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.calendar_demo;
    }

    @Override
    protected void obtainIntent(){

    }

    private void initList(){
//        punchedAssDao =new PunchedAssDao(TestCalendarActivity.this);
//        mPunchedAss  = punchedAssDao.queryAll();
        Log.d("TestLog",mPunchedAss.size()+"size");

        //时间yyyy-MM-DD
        //排序
        Collections.sort(mPunchedAss, new Comparator<PunchedAss>() {
            @Override
            public int compare(PunchedAss o1, PunchedAss o2) {
                Log.d("TestLog",o1.getDate());
                long num1 = DateUtil.full_stringToDate(o1.getDate());
                long num2 = DateUtil.full_stringToDate(o2.getDate());
                if (num1 > num2) {
                    return 1;
                } else if (num1 < num2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        int length = mPunchedAss.size();
        String date_temp = "1000-00-00";



        //根据yyyy-MM-dd
        for(int i=0;i<length;i++){
            //如果年月不一样，插入HeadTitle,再判断日是否一样
            if(!mPunchedAss.get(i).getDate().substring(0,7).equals(date_temp.substring(0,7))){
                HeadTitle ht = new HeadTitle(mPunchedAss.get(i).getDate().substring(0,7));
                mObjects.add(ht);
                if(!mPunchedAss.get(i).getDate().substring(0,10).equals(date_temp)){//如果日也不同，那么插入SubTitle
                    SubTitle st = new SubTitle(mPunchedAss.get(i).getDate().substring(8,10));
                    mObjects.add(st);
                    date_temp = mPunchedAss.get(i).getDate().substring(0,10);
                }
                mObjects.add(mPunchedAss.get(i));

            }else if(!mPunchedAss.get(i).getDate().substring(0,10).equals(date_temp)){    //年月一样，判断日是否一样
                    SubTitle subTitle = new SubTitle(mPunchedAss.get(i).getDate().substring(8,10));
                    mObjects.add(subTitle);
                mObjects.add(mPunchedAss.get(i));
                date_temp = mPunchedAss.get(i).getDate().substring(0,10);
            }else{  //如果年月日一样，那么直接添加该日程
                mObjects.add(mPunchedAss.get(i));
            }
        }
        mObjects.add(new Empty());

        adapter.notifyDataSetChanged();



    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {

        ll_article = findViewById(R.id.ll_article);
        ll_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer sb = new StringBuffer();
                java.util.Calendar cal_buff = java.util.Calendar.getInstance();

                sb.append(cal_buff.get(java.util.Calendar.YEAR));
                sb.append("-");
                if (cal_buff.get(java.util.Calendar.MONTH) + 1 >= 0 && cal_buff.get(java.util.Calendar.MONTH) + 1 < 10) {
                    sb.append("0");
                }
                sb.append(cal_buff.get(java.util.Calendar.MONTH) + 1);
                sb.append("-");
                if (cal_buff.get(java.util.Calendar.DAY_OF_MONTH) >= 0 && cal_buff.get(java.util.Calendar.DAY_OF_MONTH) < 10) {
                    sb.append("0");
                }
                sb.append(cal_buff.get(java.util.Calendar.DAY_OF_MONTH));
                //格式 yyyy-MM-dd
                //这里可能会出问题，如何把StringBuffer转为String
                String date = String.valueOf(sb);

                ll_menu.setVisibility(View.GONE);
                darkView.setVisibility(View.GONE);
                isMenuOpen = false;

                Intent intent = new Intent(TestCalendarActivity.this,LogDetActivity.class);
                intent.putExtra("date",date);
                startActivity(intent);

            }
        });


        ll_print = findViewById(R.id.ll_print);
        ll_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_menu.setVisibility(View.GONE);
                darkView.setVisibility(View.GONE);
                isMenuOpen = false;
                Intent intent = new Intent(TestCalendarActivity.this,PreviewActivity.class);
                startActivity(intent);
            }
        });


        ll_menu = findViewById(R.id.ll_menu);
        darkView = findViewById(R.id.darkback);
        darkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMenuOpen){
                    showOpenAnim(80);
                }else{
                    showCloseAnim(80);
                }
            }
        });

        btn_more = findViewById(R.id.more);
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMenuOpen){
                    showOpenAnim(80);
                }else{
                    showCloseAnim(80);
                }
            }
        });







        tv_year1 = findViewById(R.id.tv_year1);

        setStatusBarDarkMode();

        ImageView btn_back = (ImageView) findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//        initList();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager =new LinearLayoutManager(TestCalendarActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ObjectsAdapter(mObjects,this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerViewListener());

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

//        ImageView today = (ImageView) findViewById(R.id.today);
//        today.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCalendarView.scrollToCurrent();
//            }
//        });

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
                if (!mCalendarLayout.isExpand()) {
                    mCalendarView.showYearSelectLayout(mYear);
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
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


        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
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


        //这里get
        GETArticel();
        GETAssignment();


    }

    private void GETArticel(){

        new Thread(){
            @Override
            public void run(){

                String token = SPManager.setting_get("token",TestCalendarActivity.this);

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
                        Log.d("TestLog","records get---"+string);
                        //获取其中的数组，可以进行封装， 开头结束通过 最近的[]进行判断
                        try{
                            String arrayString = string.substring(string.indexOf("records")+9,string.length()-2);
                            Log.d("TestLog","arrayString"+arrayString);
                            JSONArray logs = new JSONArray(arrayString);
                            Log.d("TestLog","logs length"+logs.length());
                            for(int i = 0; i< logs.length(); i++){
                                JSONObject jsonObject_inner = logs.getJSONObject(i);

                                mPunchedAss.add(new DayLog(jsonObject_inner.getString("id"),
                                        DateUtil.full_stampToDate(jsonObject_inner.getLong("time")),
                                        jsonObject_inner.getLong("time"),
                                        jsonObject_inner.getString("content")));



                            }


                            int length = mPunchedAss.size();
                            Log.d("TestLog","adding mPunch size"+length);
                            for(int i=0;i<length;i++){
                                //yyyy-mm-dd
                                //0123456789
                                PunchedAss pAss = mPunchedAss.get(i);
                                map.put(getSchemeCalendar(Integer.parseInt(pAss.getDate().substring(0,4)),Integer.parseInt(pAss.getDate().substring(5,7)),Integer.parseInt(pAss.getDate().substring(8,10)),0xfffc107,"").toString(),
                                        getSchemeCalendar(Integer.parseInt(pAss.getDate().substring(0,4)),Integer.parseInt(pAss.getDate().substring(5,7)),Integer.parseInt(pAss.getDate().substring(8,10)),0xffffc107,""));
                            }

                            mCalendarView.setSchemeDate(map);

                            Message msg = new Message();
                            msg.what=1;
                            handler.sendMessage(msg);



                        }catch(Exception e){
                            Log.d("TestLog","get log error "+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });



            }
        }.start();

    }

    private void GETAssignment(){

        new Thread(){
            @Override
            public void run(){

                String token = SPManager.setting_get("token",TestCalendarActivity.this);

                OkHttpClient okHttpClient = new OkHttpClient();

                Request build1 = new Request.Builder().url("https://slow.hustonline.net/api/v1/routine")
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
                        Log.d("TestLog","routine get---"+string);
                        //获取其中的数组，可以进行封装， 开头结束通过 最近的[]进行判断
                        try{
                            //"routine":[{}]
                            String arrayString = string.substring(string.indexOf("routines")+10,string.length()-2);
                            Log.d("TestLog","arrayString"+arrayString);
                            JSONArray logs = new JSONArray(arrayString);
                            Log.d("TestLog","logs length"+logs.length());
                            for(int i = 0; i< logs.length(); i++){
                                JSONObject jsonObject_inner = logs.getJSONObject(i);

                                JSONArray sign_inArray = jsonObject_inner.getJSONArray("sign_in");
                                Log.d("TestLog",sign_inArray.length()+"signArray length  -- id" + jsonObject_inner.getString("id"));
                                for(int j = 0; j < sign_inArray.length() ; j++){
                                    mPunchedAss.add(new DailyAssignment(jsonObject_inner.getString("id"),
                                            DateUtil.full_stampToDate((long)sign_inArray.get(j)),
                                            jsonObject_inner.getString("title"),
                                            Integer.parseInt(jsonObject_inner.getString("icon_id")),
                                            "no",0));
                                }
//                                    public DailyAssignment(String objId,String date,String title,int icon,String finish,int progress){

                            }



                            int length = mPunchedAss.size();
                            for(int i=0;i<length;i++){
                                //yyyy-mm-dd
                                //0123456789
                                PunchedAss pAss = mPunchedAss.get(i);
                                map.put(getSchemeCalendar(Integer.parseInt(pAss.getDate().substring(0,4)),Integer.parseInt(pAss.getDate().substring(5,7)),Integer.parseInt(pAss.getDate().substring(8,10)),0xfffc107,"").toString(),
                                        getSchemeCalendar(Integer.parseInt(pAss.getDate().substring(0,4)),Integer.parseInt(pAss.getDate().substring(5,7)),Integer.parseInt(pAss.getDate().substring(8,10)),0xffffc107,""));
                            }

                            mCalendarView.setSchemeDate(map);

                            Message msg = new Message();
                            msg.what=1;
                            handler.sendMessage(msg);



                        }catch(Exception e){
                            Log.d("TestLog","get assignment error "+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });



            }
        }.start();

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
        Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {

        //DayLog.e("onDateSelected", "  -- " + calendar.getYear() + "  --  " + calendar.getMonth() + "  -- " + calendar.getDay());
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        tv_year1.setText(String.valueOf(calendar.getYear()));
        mYear = calendar.getYear();
        if (isClick) {  //点击事件在此设置
            Log.d("TestLog","click");
            for(int i=0;i<mObjects.size();i++){
                String year =String.valueOf(calendar.getYear());
                int tempMonth = calendar.getMonth();
                String month = tempMonth<10 ? "0"+String.valueOf(tempMonth) : String.valueOf(tempMonth);
                int tempDay = calendar.getDay();
                String day = tempDay < 10 ? "0"+String.valueOf(tempDay) : String.valueOf(tempDay);
                Log.d("TestLog",year+"-"+month+"-"+day);

                tv_year1.setText(String.valueOf(year));

                if(mObjects.get(i)!=null){
                    if(mObjects.get(i) instanceof PunchedAss && ((PunchedAss) mObjects.get(i)).getDate().substring(0,10).equals(year+"-"+month+"-"+day)){
                      //  Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                        moveToPosition(i-1);
                        break;
                    }
                }
            }

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
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
    }

    @Override
    public void onViewChange(boolean isMonthView) {
        //DayLog.e("onViewChange", "  ---  " + (isMonthView ? "月视图" : "周视图"));
    }


    @Override
    public void onWeekChange(List<Calendar> weekCalendars) {
        for (Calendar calendar : weekCalendars) {
            Log.e("onWeekChange", calendar.toString());
        }
    }

    /**
     * 屏蔽某些不可点击的日期，可根据自己的业务自行修改
     *
     * @param calendar calendar
     * @return 是否屏蔽某些不可点击的日期，MonthView和WeekView有类似的API可调用
     */
    @Override
    public boolean onCalendarIntercept(Calendar calendar) {
        Log.e("onCalendarIntercept", calendar.toString());
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


    private void moveToPosition(int n){
        Log.d("TestLog","move to position");
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = linearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem ){
            //当要置顶的项在当前显示的第一个项的前面时
            recyclerView.scrollToPosition(n);
        }else if ( n <= lastItem ){
            //当要置顶的项已经在屏幕上显示时
            int top = recyclerView.getChildAt(n - firstItem).getTop();
            recyclerView.smoothScrollBy(0, top);
        }else {
            //当要置顶的项在当前显示的最后一项的后面时
            recyclerView.smoothScrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }
    }

    class RecyclerViewListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

                move = false;
                int n = mIndex - linearLayoutManager.findFirstVisibleItemPosition();
                if ( 0 <= n && n < recyclerView.getChildCount()){
                    int top = recyclerView.getChildAt(n).getTop();
                    recyclerView.smoothScrollBy(0, top);
                }

        }

//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            if (move && mRadioGroup.getCheckedRadioButtonId() == R.id.scroll){
//                move = false;
//                int n = mIndex - mLinearLayoutManager.findFirstVisibleItemPosition();
//                if ( 0 <= n && n < mRecyclerView.getChildCount()){
//                    int top = mRecyclerView.getChildAt(n).getTop();
//                    mRecyclerView.scrollBy(0, top);
//                }
//            }
//        }
    }



    public class ObjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private int ITEM_HEADTITLE  = 1;
        private int ITEM_SUBTITLE   = 2;
        private int ITEM_PUNCHEDASS = 3;
        private int ITEM_EMPTY = 4;
        private List<Object> objects;
        private Context context;
        private int[] icons = {
                R.drawable.apples,
                R.drawable.vocabulary,
                R.drawable.water,
                R.drawable.breakfirst,
                R.drawable.makeup,
                R.drawable.night,
                R.drawable.read,
                R.drawable.sport,
                R.drawable.doctor,
                R.drawable.dailyarticle
        };

        public ObjectsAdapter(List<Object> objects,Context context){
            this.objects=objects;
            this.context = context;
        }



        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view = null;
            RecyclerView.ViewHolder holder = null;
            if(viewType == ITEM_HEADTITLE){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_toptitle,parent,false);
                holder = new ViewHolderTopTitle(view);
            }else if(viewType == ITEM_SUBTITLE){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_title,parent,false);
                holder = new ViewHolderSubTitle(view);
            }else if(viewType == ITEM_PUNCHEDASS){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_test,parent,false);
                holder = new ViewHolderPunched(view);
            }else if(viewType == ITEM_EMPTY){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_empty,parent,false);
                holder = new ViewHolderEmpty(view);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
            if(holder instanceof ViewHolderTopTitle){
                HeadTitle ht= (HeadTitle) objects.get(position);
                ((ViewHolderTopTitle)holder).tv_topTitle.setText(ht.getDate());
            }else if(holder instanceof ViewHolderSubTitle){
                SubTitle st = (SubTitle) objects.get(position);
                //这里记得改
                ((ViewHolderSubTitle)holder).tv_subTitle.setText(st.getDate()+"th");
            }else if(holder instanceof ViewHolderPunched){
                final PunchedAss pAss = (PunchedAss) objects.get(position);
                ((ViewHolderPunched)holder).img_ass.setBackgroundResource(icons[pAss.getIcon()]);
                ((ViewHolderPunched)holder).ll_seeArt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pAss.getTitle().equals("日志")){
                            Intent intent = new Intent(TestCalendarActivity.this, LogDetActivity.class);
                            intent.putExtra("date", pAss.getDate());
                            startActivity(intent);
                        }
                    }
                });
                ((ViewHolderPunched)holder).tv_ass.setText(pAss.getTitle());
            }else if(holder instanceof ViewHolderEmpty){
                Empty ep = (Empty) objects.get(position);
            }
        }

        @Override
        public int getItemViewType(int position){
            if(objects.get(position) instanceof HeadTitle){
                return ITEM_HEADTITLE;
            }else if(objects.get(position) instanceof SubTitle){
                return ITEM_SUBTITLE;
            }else if(objects.get(position) instanceof PunchedAss){
                return ITEM_PUNCHEDASS;
            }else if(objects.get(position) instanceof Empty){
                return ITEM_EMPTY;
            }

            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount(){
            return objects ==null? 0:objects.size();
        }

    }


    private void showOpenAnim(int dp){
       darkView.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(darkView,"alpha",0,0.5f).setDuration(300).start();


        //for循环来开始小图标的出现动画

            AnimatorSet set = new AnimatorSet();
            //标题1与x轴负方向角度为20°，标题2为100°，转换为弧度

        ll_menu.setVisibility(View.VISIBLE);

            set.playTogether(
                   ObjectAnimator.ofFloat(ll_menu, "alpha", 0, 1).setDuration(300)
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

    private void showCloseAnim(int dp){
        ObjectAnimator.ofFloat(darkView,"alpha",0.5f,0).setDuration(300).start();

            AnimatorSet set = new AnimatorSet();

            set.playTogether(
                    ObjectAnimator.ofFloat(ll_menu, "alpha", 1, 0).setDuration(300)
            );
            set.setDuration(300);
            set.start();

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    ll_menu.setVisibility(View.GONE);
                    darkView.setVisibility(View.GONE);
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


}

