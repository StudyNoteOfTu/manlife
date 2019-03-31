package com.example.tufengyi.manlife.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tufengyi.manlife.R;
import com.example.tufengyi.manlife.activities.EditAssActivity;
import com.example.tufengyi.manlife.bean.DailyAssignment;
import com.example.tufengyi.manlife.bean.PunchedAss;
import com.example.tufengyi.manlife.bean.Settings;
import com.example.tufengyi.manlife.db.SPManager;
import com.example.tufengyi.manlife.utils.RedirectInterceptor;
import com.example.tufengyi.manlife.utils.dao.DailyAssDao;
//import com.example.tufengyi.manlife.utils.dao.PunchedAssDao;
import com.example.tufengyi.manlife.utils.dao.SettingsDao;
//import com.example.tufengyi.manlife.utils.helper.PunchedAssHelper;
import com.example.tufengyi.manlife.utils.tools.DateUtil;
import com.example.tufengyi.manlife.view.SwipeItemLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.view.View.GONE;

public class RecyclerViewFragment extends Fragment {
//    private PunchedAssDao punchedAssDao;
    private DailyAssDao dailyAssDao;
    private List<DailyAssignment> mDailyAss = new ArrayList<DailyAssignment>();
    private boolean up = true;
    private SettingsDao settingsDao;
    private SoundPool sp;//声明一个SoundPool
    private int music;//定义一个整型，用load()：来设置soundID


    private View root;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        music = sp.load(getContext(),R.raw.dingdingding,1);
        if(root==null){
            root = inflater.inflate(R.layout.fragment_recyclerview,container,false);
            RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);    //嵌套与SwipeRefreshLayout中的recyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));    //子项监听器
            recyclerView.setAdapter(new MyAdapter(getContext()));//获取Adapter
           //不加分割线
            // recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));

            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh);//SwipeRefreshLayout
            swipeRefreshLayout.setEnabled(false);
//            swipeRefreshLayout.setColorSchemeColors(Color.RED);
//            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {    //刷新监听器
//                @Override
//                public void onRefresh() {
//                    swipeRefreshLayout.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            swipeRefreshLayout.setRefreshing(false);        //不刷新
//                        }
//                    },0);
//                }
//            });
        }
        dailyAssDao = new DailyAssDao(getContext());
//        punchedAssDao = new PunchedAssDao(getContext());
        settingsDao = new SettingsDao(getContext());

        if(settingsDao.queryAll().get(0).getUp() == 1){//开启
            up = true;
        }else{
            up = false;
        }

        mDailyAss = dailyAssDao.queryAll();
        return root;
    }


    //通过Adapter填充数据

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder> {         //Adapter
        private Context mContext;


        private String finish = "no";
        private int progress = 0;
        private String dates = null;

        public MyAdapter(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(mContext).inflate(R.layout.item_recycler, parent, false);   //获得子项layout
            return new Holder(root);    //返回holder
        }

        @Override
        public void onBindViewHolder(final Holder holder, final int position) {     //绑定ViewHolder
            //mBmp是holder中的ImageView项
            final DailyAssignment mDailyAss_temp = mDailyAss.get(position);

            finish = mDailyAss_temp.getFinish();//yes/no
            progress = mDailyAss_temp.getProgress();
            dates = mDailyAss_temp.getDate();


            StringBuffer sb = new StringBuffer();
            Calendar cal = Calendar.getInstance();

            sb.append(cal.get(Calendar.YEAR));
            sb.append("-");
            if(cal.get(Calendar.MONTH)+1>=0 && cal.get(Calendar.MONTH)+1<10){
                sb.append("0");
            }
            sb.append(cal.get(Calendar.MONTH)+1);
            sb.append("-");
            if(cal.get(Calendar.DAY_OF_MONTH)>=0 && cal.get(Calendar.DAY_OF_MONTH)<10){
                sb.append("0");
            }
            sb.append(cal.get(Calendar.DAY_OF_MONTH));
            //这里可能会出问题，如何把StringBuffer转为String
            String dateToday = String.valueOf(sb);
            //这里我们不对数据库进行操作
            if(!dateToday.equals(mDailyAss_temp.getDate())){
                DailyAssignment ass =  new DailyAssignment(mDailyAss_temp.getId(),mDailyAss_temp.getObjId(),mDailyAss_temp.getDate(),
                        mDailyAss_temp.getTitle(),mDailyAss_temp.getIcon(),
                        "no",mDailyAss_temp.getProgress());
                dailyAssDao.update(ass);
                mDailyAss_temp.setFinish("no");
            }

            holder.img_ass.setBackgroundResource(icons[mDailyAss_temp.getIcon()]);
            holder.tv_ass.setText(mDailyAss_temp.getTitle());
            holder.tv_progress.setText("已坚持"+mDailyAss_temp.getProgress()+"天");
            if(mDailyAss_temp.getFinish().equals("yes")){
                holder.btn_finish.setVisibility(GONE);
                holder.ll_item.setBackgroundResource(R.drawable.finishback);
            }else{
                holder.btn_finish.setVisibility(View.VISIBLE);
                holder.ll_item.setBackgroundColor(Color.WHITE);
            }
            //holder.img_ass.setImageResource(icons[m]);        //将List的数据填入holder中
            holder.btn_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(up) {
                        sp.play(music, 1, 1, 0, 0, 1);
                    }
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

                    dailyAssDao = new DailyAssDao(mContext);
                    holder.ll_item.setBackgroundResource(R.drawable.finishback);
                    holder.btn_finish.setVisibility(GONE);
                    finish = "yes";
                    progress++;
                    dates = date;
                    DailyAssignment dailyAss_temp = new DailyAssignment(mDailyAss_temp.getId(),mDailyAss_temp.getObjId(),date,
                            mDailyAss_temp.getTitle(),mDailyAss_temp.getIcon(),
                            "yes",mDailyAss_temp.getProgress()+1);
                    dailyAssDao.update(dailyAss_temp);
                    holder.tv_progress.setText("已坚持"+String.valueOf(mDailyAss_temp.getProgress()+1)+"天");


                    //这里进行post更新

                    POST(mDailyAss_temp);

                    //打卡
//                    punchedAssDao = new PunchedAssDao(mContext);
//                    PunchedAss pAss = new PunchedAss(date,mDailyAss_temp.getTitle(),mDailyAss_temp.getIcon());
//                    punchedAssDao.insert(pAss);

                }
            });

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dailyAssDao = new DailyAssDao(mContext);
                    dailyAssDao.delete(mDailyAss_temp.getId());
                    mDailyAss.remove(position);
//                    notifyItemRemoved(position);
                    notifyDataSetChanged();//不可再子线程中调用，否则无效，
                    // 可以子线程中添加一个handler发送消息在主线程中更新
                    //往里看，notifyDataSetChanged中调用了 requireLayout()

                    //这里之前出现过一个bug，连续使用notifyItemRemoved后会崩溃
                    //这里改用notifyDataSetChanged()没有问题，但是没有notifyItemRemoved的渐退效果
                    //使用正常，猜测是性能问题？
                }
            });

            holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,EditAssActivity.class);
                    intent.putExtra("objId",mDailyAss_temp.getObjId());
                    intent.putExtra("icon",mDailyAss_temp.getIcon());
                    intent.putExtra("title",mDailyAss_temp.getTitle());
                    intent.putExtra("id",mDailyAss_temp.getId());
                    intent.putExtra("date",dates);
                    intent.putExtra("progress",progress);
                    intent.putExtra("finish",finish);
                    intent.putExtra("yes",1);
                    startActivity(intent);
                }
            });
        }

        private void POST(DailyAssignment dailyAssignment){
            String token = SPManager.setting_get("token",getActivity());

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .followRedirects(false)
                    .addInterceptor(new RedirectInterceptor())
                    .build();

            String json = "{\"id\":\""+dailyAssignment.getObjId()+"\"," +
                    "\"time\":"+ DateUtil.stringToDate(dailyAssignment.getDate()) +"," +
                    "\"title\":\""+ dailyAssignment.getTitle() +"\"," +
                    "\"icon_id\":\""+ dailyAssignment.getIcon() +"\"," +
                    "\"sign_in\":[]}";

            // {"id":"xxx","time",0,"title":"","icon_id":"","sign_in":[]}

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

            Request request = new Request.Builder()
                    .addHeader("Auth","Bearer " + token )
                    .url("https://slow.hustonline.net/api/v1/routine/sign")
                    .post(requestBody)
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

                    }else{
                        Log.d("TestRoutine", "put error"+response.code()+response);
                    }


                }
            });
        }

        @Override
        public int getItemCount() {
            return mDailyAss.size();
        }       //获得一共多少个item

        class Holder extends RecyclerView.ViewHolder  {
           // private ImageView mBmp;     //Holder的属性就是子项中的component
            private ImageView img_ass;
            private TextView tv_ass;
            private TextView tv_progress;
            private ImageView btn_finish;
            private LinearLayout ll_item;
            private Button btn_edit;
            private Button btn_delete;

            Holder(View itemView) {
                super(itemView);

                img_ass = (ImageView) itemView.findViewById(R.id.img_ass);
                tv_ass = (TextView) itemView.findViewById(R.id.tv_dailyAss);
                tv_progress = (TextView) itemView.findViewById(R.id.tv_progress) ;
                btn_finish = (ImageView) itemView.findViewById(R.id.btn_finish) ;

                ll_item = (LinearLayout) itemView.findViewById(R.id.main);
                btn_edit = (Button) itemView.findViewById(R.id.btn_edit);
                btn_delete = (Button) itemView.findViewById(R.id.btn_delete);


//                View main = itemView.findViewById(R.id.main);       //main为未划开的左边这部分，划开的不在R.id.main里
//                main.setOnClickListener(this);
//                main.setOnLongClickListener(this);

            }




        }
    }

}