package com.develop.sporthealth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.develop.bean.ShareMsg;
import com.develop.bean.StepEntity;
import com.develop.tools.AdapterTools;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/4/8.
 */

public class PlanRanking extends AppCompatActivity {

    private Context context;
    private SQLOperator op;
    private SPTools sp;

    private MyLayout back;
    private ListView list;

    private AdapterTools<StepEntity> adapter;
    private StepEntity stepEntity;
    private static StepEntity stepEntity2;
    private ArrayList<StepEntity> stepEntitys;

    private int sque=1;
    private int j=0;
    private int n=0;
    private boolean isget=false;

    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    AVQuery<AVObject> query = new AVQuery<>("UserInfo");
                    query.getInBackground(stepEntitys.get(j).getUserID(), new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {
                            if (avObject != null) {
                                stepEntitys.get(j).setUserName(avObject.get("UserName").toString());
                                stepEntitys.get(j).setImageURL(avObject.get("ImageUrl").toString());
                                j++;
                                handler.sendEmptyMessage(0x002);

                            }
                        }
                    });
                    break;
                case 0x002:
                    if (j == stepEntitys.size()) {
                        for (int i = 0; i < stepEntitys.size(); i++) {
                            for (int j = 0; i < stepEntitys.size()-1-i; i++) {
                                int n=new Integer(stepEntitys.get(j).getSteps());
                                int m = new Integer(stepEntitys.get(j+1).getSteps());
                                if (n < m) {
                                    stepEntity2=new StepEntity();
                                    stepEntity2.setUserID(stepEntitys.get(j+1).getUserID());
                                    stepEntity2.setSteps(stepEntitys.get(j+1).getSteps());
                                    stepEntity2.setUserName(stepEntitys.get(j+1).getUserName());
                                    stepEntity2.setImageURL(stepEntitys.get(j+1).getImageURL());

                                    stepEntitys.get(j+1).setUserID(stepEntitys.get(j).getUserID());
                                    stepEntitys.get(j+1).setSteps(stepEntitys.get(j).getSteps());
                                    stepEntitys.get(j+1).setUserName(stepEntitys.get(j).getUserName());
                                    stepEntitys.get(j+1).setImageURL(stepEntitys.get(j).getImageURL());

                                    stepEntitys.get(j).setUserID(stepEntity2.getUserID());
                                    stepEntitys.get(j).setSteps(stepEntity2.getSteps());
                                    stepEntitys.get(j).setUserName(stepEntity2.getUserName());
                                    stepEntitys.get(j).setImageURL(stepEntity2.getImageURL());
                                }
                            }

                        }

                        setAdapter();
                    }else {
                        handler.sendEmptyMessage(0x001);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_ranking);

        context=getApplicationContext();
        op=new SQLOperator(context);
        sp=new SPTools(context);

        list=findViewById(R.id.ranking_list);

        back=findViewById(R.id.ranking_back);
        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getDate();
    }


    private void getDate() {
        //从服务器中获取数据
        AVQuery<AVObject> query1 = new AVQuery<>("Step");
        query1.whereEqualTo("Date", TimeTools.getCurrentDate());
        AVQuery<AVObject> query2 = new AVQuery<>("Step");
        query2.whereNotEqualTo("UserID", "");
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.orderByDescending("TotalSteps");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size()>0) {
                    stepEntitys=new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        stepEntity = new StepEntity();
                        stepEntity.setUserID(list.get(i).get("UserID").toString());
                        stepEntity.setSteps(list.get(i).get("TotalSteps").toString());
                        stepEntitys.add(stepEntity);
                    }
                    handler.sendEmptyMessage(0x001);
                }

            }
        });



    }

    private void setAdapter() {
        adapter=new AdapterTools<StepEntity>(stepEntitys, R.layout.planranking_item) {
            @Override
            public void bindView(final ViewHolder holder, final StepEntity obj) {
                holder.setText(R.id.ranking_item_sequen,(sque++)+"");
                holder.setText(R.id.ranking_item_step,obj.getSteps());

                holder.setText(R.id.ranking_item_username,obj.getUserName());
                if(readImage(obj.getUserID())!=null) {
                    holder.setImageBitmap(R.id.ranking_item_userimg, readImage(obj.getUserID()));
                }else {
                    downLoad(obj.getUserID(),obj.getImageURL());
                }
            }

        };

        list.setAdapter(adapter);
    }

    /**
     * 读取图片
     * */
    private Bitmap readImage(String id) {
        File filesDir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断sd卡是否挂载
            //路径1：storage/sdcard/Android/data/包名/files
            filesDir = context.getExternalFilesDir("");
        }else{//手机内部存储
            //路径2：data/data/包名/files
            filesDir = context.getFilesDir();
        }
        File file = new File(filesDir,id+".png");
        if(file.exists()){
            //存储--->内存
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());


            return bitmap;
        }
        return null;
    }

    /**
     * 下载图片
     * */
    public void downLoad(final String name, String url){
        final Bitmap[] bitmap = new Bitmap[1];
        final AVFile file=new AVFile(name+".png",url,new HashMap<String, Object>());
        file.getThumbnailUrl(true, 100, 100);
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                // bytes 就是文件的数据流
                if(bytes!=null) {
                    bitmap[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    saveImage(bitmap[0], name);
                }
                //img.setImageBitmap(bitmap[0]);
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                // 下载进度数据，integer 介于 0 和 100。
            }
        });
    }

    /**
     * 保存图片
     */
    private void saveImage(Bitmap bitmap,String name) {
        File filesDir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断sd卡是否挂载
            //路径1：storage/sdcard/Android/data/包名/files
            filesDir = context.getExternalFilesDir("");
        }else{//手机内部存储
            //路径2：data/data/包名/files
            filesDir = context.getFilesDir();
        }
        FileOutputStream fos = null;
        try {
            File file = new File(filesDir,name+".png");
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,fos);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(fos != null){
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
