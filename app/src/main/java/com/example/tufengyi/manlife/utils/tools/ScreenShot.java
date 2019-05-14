package com.example.tufengyi.manlife.utils.tools;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScreenShot {
    private static String TAG = "ScreenShot";


    /**
     * 获取指定Activity的截屏，保存到png文件
     * @param activity
     * @return
     */
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        savePic(b, Environment.getExternalStorageDirectory().getPath()+"screen_test.png");

        return b;
    }


    /**
     * 保存到sdcard
     * @param b
     * @param strFileName
     */
    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *   把View对象转换成bitmap
     *
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }


    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }


    /**
     * 截取scrollview的屏幕
     *
     *
     */
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(
                    Color.parseColor("#ffffff"));
        }
        Log.d(TAG, "实际高度:" + h);
        Log.d(TAG, " 高度:" + scrollView.getHeight());
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * ListView 截屏
     * @param listView
     * @param context
     * @return
     */
    public  static Bitmap createBitmap(Bitmap sub,Bitmap back,Bitmap first,ListView listView, Context context){
        //底部view
        //View view_subTitle = View.inflate(context,R.layout.layout_subtitle,null);
        //Bitmap subtitle = viewToBitmap(view_subTitle,view_subTitle.getWidth(),view_subTitle.getHeight());
        //int sub_height = view_subTitle.getHeight();
        //宽 高
        //int sub_height = subTitle.getHeight();

        //我们设定，最小高度为5个item高度

        //背景高度
        int back_height = back.getHeight();
        //宽度
        int Twidth = sub.getWidth();

        int width, height, rootHeight=0;

        //距离顶部的高度
        int yPos = first.getHeight()+40;
        //元素个数
        int listItemNum;

        //增长后的个数
        //int realNum;




        // width = getDisplayMetrics(context)[0];//宽度等于屏幕宽
        width = listView.getWidth() ;

        int left = (Twidth-width)/2;

        ListAdapter listAdapter = listView.getAdapter();

        //如果小于5个就增长到5个
        listItemNum = listAdapter.getCount() ;


        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int windowHeight = display.getHeight();

        //

        //一般 6-9 个之间(具体大小根据手机屏幕高度来适配)
        //realNum = listItemNum<8? 8:listItemNum;

        Log.d("MyTest421","metrics"+windowHeight+"\n height"+back_height);


        List<View> childViews = new ArrayList<View>(listItemNum);
        //判断最小数量
        View itemView;
        //计算整体高度:
        //1.把有内容的填上
        for(int pos=0; pos < listItemNum; ++pos){
            itemView = listAdapter.getView(pos, null, listView);
            //measure过程
            itemView.measure(View.MeasureSpec.makeMeasureSpec(width, View. MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childViews.add(itemView);
            rootHeight += itemView.getMeasuredHeight();
        }

        int itemHeight;
        itemView = listAdapter.getView(0, null, listView);
        //measure过程
        itemView.measure(View.MeasureSpec.makeMeasureSpec(width, View. MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        itemHeight = itemView.getMeasuredHeight();

        //2.把空白部分补全 real 5 list 4 结果 1 补
//        for(int plus = 0; plus < realNum-listItemNum; plus++){
//            rootHeight += itemHeight;//获得第一项的高度（之前已经确保了没有任务不让添加）
//        }


        //40是first距离顶部的距离
        //现在height为整个高度
        height = sub.getHeight()+rootHeight+first.getHeight()+40;

        Log.d("MyTest421","height:"+height);

        //1.不够高 那么就增加，增加一次判断一次，看看什么时候在height超过屏幕高度（在这里之前截断进程）
        //判断是否够高
        boolean highEnough = false;
        int addheight = 0;
        if(height < windowHeight){//如果不够高
            //那么计算差多少，根据差的多少进行绘制白色图片
            addheight = windowHeight - height;//获得中间差的高度（需要填充白色的部分）
            Log.d("MyTest421","delta:"+addheight);
        }//如果足够高那么就不添加0 了
        else highEnough = true;//足够高了


        //2.如果超过了，那么就不管了

        //int back_counts =height/back_height+1;
        int back_counts;
        //如果mode不为0，说明不完整，那么这时候就加一个！
        boolean hasAdd = false;
        if((height+addheight)%back_height==0){
            hasAdd = false;
            back_counts = (height+addheight)/back_height;
        }else{
            hasAdd = true;
            back_counts = (height+addheight)/back_height+1;
        }

        Log.d("MyTest421","back counts:"+back_counts);

        //看看背景要多少个，把height设置为以背景为单元
        //高度是否足够
        if(highEnough){
            height = back_counts*back_height;
//            height就是原来的height

            //设置最小
            Bitmap result = Bitmap.createBitmap(Twidth,height,Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            //canvas.drawColor(Color.GREEN);

            int yTop = 0;


            //绘制背景
            for(int j = 0;j<back_counts;j++){
                canvas.drawBitmap(back,0,yTop,null);
                yTop+=back_height;
            }


            //绘制头部
            canvas.drawBitmap(first,left,40,null);

            Bitmap itemBitmap;
            int childHeight;
            //把每个ItemView生成图片，并画到背景画布上
            for(int pos=0; pos < childViews.size(); ++pos){
                itemView = childViews.get(pos);
                childHeight = itemView.getMeasuredHeight();
                itemBitmap = viewToBitmap(itemView,width,childHeight);
                if(itemBitmap!=null){
                    canvas.drawBitmap(itemBitmap, left, yPos, null);
                }
                yPos = childHeight +yPos;
            }

            canvas.drawBitmap(sub,0,height-sub.getHeight(),null);

            canvas.save();
            canvas.restore();
            return result;
        }else{//如果不够高
            height = windowHeight;
            //设置最小
            Bitmap result = Bitmap.createBitmap(Twidth,height,Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            int yTop = 0;
            //绘制背景
            if(hasAdd){
                for (int j = 0; j < back_counts-1; j++) {
                    canvas.drawBitmap(back, 0, yTop, null);
                    yTop += back_height;
                }
                canvas.drawBitmap(Bitmap.createBitmap(back,0,0,back.getWidth(),back_height*back_counts-height,null,false),0,yTop,null);
            }else {
                for (int j = 0; j < back_counts; j++) {
                    canvas.drawBitmap(back, 0, yTop, null);
                    yTop += back_height;
                }
            }

            //绘制头部
            canvas.drawBitmap(first,left,40,null);

            Bitmap itemBitmap;
            int childHeight;
            //把每个ItemView生成图片，并画到背景画布上
            for(int pos=0; pos < childViews.size(); ++pos){
                itemView = childViews.get(pos);
                childHeight = itemView.getMeasuredHeight();
                itemBitmap = viewToBitmap(itemView,width,childHeight);
                if(itemBitmap!=null){
                    canvas.drawBitmap(itemBitmap, left, yPos, null);
                }
                yPos = childHeight +yPos;
            }

            //填充白色方块
            itemBitmap = Bitmap.createBitmap(width,addheight, Bitmap.Config.ARGB_8888);
            itemBitmap.eraseColor(Color.parseColor("#FFFFFF"));//填充白色
            canvas.drawBitmap(itemBitmap,left,yPos,null);

            canvas.drawBitmap(sub,0,height-sub.getHeight(),null);


            canvas.save();
            canvas.restore();
            return result;
        }

    }

    private static Bitmap viewToBitmap(View view, int viewWidth, int viewHeight){
        view.layout(0, 0, viewWidth, viewHeight);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 压缩图片
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            // 重置baos
            baos.reset();
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            // 每次都减少10
            options -= 10;
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context
     * @return
     */
    public static final Integer[] getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new Integer[]{screenWidth, screenHeight};
    }

    /**
     * 保存图片并插入图库
     * @param context
     * @param bmp
     * @param dir 文件夹
     */
    public static void saveImageToGallery(Context context, Bitmap bmp,String dir) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), dir);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            file.canRead();
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        String path = appDir + fileName;
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }



    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param  （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return  (spValue * fontScale + 0.5f);
    }

}
