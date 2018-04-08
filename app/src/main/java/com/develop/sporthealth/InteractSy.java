package com.develop.sporthealth;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Text;
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
import com.develop.tools.AdapterTools;
import com.develop.tools.SPTools;
import com.develop.tools.TimeTools;
import com.develop.tools.database.SQLOperator;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/16.
 */

public class InteractSy extends Fragment {

    private Context context;
    private View view;

    private SQLOperator op;
    private SPTools sp;

    private ImageView top;
    private ImageView userimg;
    private TextView username;
    private ListView list;
    private Button add;

    private AdapterTools<ShareMsg> adapter;
    private ShareMsg shareMsg;
    private ArrayList<ShareMsg> shareMsgs;

    //广播通知主线程更新
    private static boolean state=false;
    private MeSy.MyBroad broad;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x001:
                    if(sp.getIsLogin()) {
                        getDate();
                        list.setVisibility(View.VISIBLE);
                    }else {
                        userimg.setImageResource(R.mipmap.ic_launcher_round);
                        username.setText("");
                        list.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    public InteractSy(){}
    @SuppressLint("ValidFragment")
    public InteractSy(Context context){
        this.context=context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.interact, container, false);

            init();

            //广播更新
            broad=new MeSy.MyBroad();
            IntentFilter filter=new IntentFilter();
            filter.addAction("com.develop.sport.MYBROAD2");
            getActivity().registerReceiver(broad,filter);

            new Thread(){
                @Override
                public void run() {
                    while (true)
                    {
                        if(state) {
                            state=false;
                            handler.sendEmptyMessage(0x001);
                        }
                    }
                }
            }.start();


            if(sp.getIsLogin()) {
                getDate();
            }
        }
        return view;
    }

    private void init() {
        op=new SQLOperator(context);
        sp=new SPTools(context);

        top=view.findViewById(R.id.inte_topimg);
        userimg=view.findViewById(R.id.inte_userimg);
        username=view.findViewById(R.id.inte_username);
        list=view.findViewById(R.id.inte_list);
        add=view.findViewById(R.id.inte_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,InteractAdd.class);
                startActivity(intent);

            }
        });

    }

    private void getDate() {
        if(readImage(sp.getID())!=null) {
            userimg.setImageBitmap(readImage(sp.getID()));
        }else {
            userimg.setImageResource(R.mipmap.ic_launcher_round);
        }
        username.setText(sp.getUserName());
        /*List<Map<String, String>> data = new ArrayList<>();
        data = op.select("select a.id,a.Content,a.Count,a.UserID,b.UserName from Share a,UserInfo b where a.UserID=b.id order by a.id desc", new String[]{});
        if (data.size() != 0) {
            shareMsgs=new ArrayList<>();
            for (int i=0;i<data.size();i++){
                shareMsg=new ShareMsg();
                shareMsg.setId(data.get(i).get("id"));
                shareMsg.setName(data.get(i).get("UserName"));
                shareMsg.setContent(data.get(i).get("Content"));
                shareMsg.setUserID(data.get(i).get("UserID"));
                if(data.get(i).get("Count")!=null) {
                    shareMsg.setNum(data.get(i).get("Count"));
                }else {
                    shareMsg.setNum("0");
                }
                shareMsgs.add(shareMsg);
            }
        }*/
        //从服务器中获取数据
        AVQuery<AVObject> query = new AVQuery<>("Share");
        query.orderByDescending("Date");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size()>0) {
                    shareMsgs=new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        shareMsg = new ShareMsg();
                        shareMsg.setId(list.get(i).getObjectId());
                        shareMsg.setName(list.get(i).get("UserName").toString());
                        shareMsg.setContent(list.get(i).get("Content").toString());
                        shareMsg.setUserID(list.get(i).get("UserID").toString());
                        shareMsg.setImageUrl(list.get(i).get("ImageUrl").toString());
                        shareMsg.setRank(list.get(i).get("Rank").toString());
                        if (list.get(i).get("Count") != null) {
                            shareMsg.setNum(list.get(i).get("Count").toString());
                        } else {
                            shareMsg.setNum("0");
                        }
                        shareMsgs.add(shareMsg);
                    }
                    setAdapter();
                }

            }
        });



    }

    private void setAdapter() {
        adapter=new AdapterTools<ShareMsg>(shareMsgs, R.layout.interact_item) {
            @Override
            public void bindView(ViewHolder holder, ShareMsg obj) {
                holder.setText(R.id.inte_item_username,obj.getName());
                holder.setText(R.id.inte_item_content,obj.getContent());
                holder.setText(R.id.inte_item_num,obj.getNum());
                int count=new Integer(obj.getRank());
                if(count<10){
                    holder.setImageResource(R.id.inte_item_rank,R.mipmap.head1);
                }else if(count<50){
                    holder.setImageResource(R.id.inte_item_rank,R.mipmap.head2);
                }else if(count<100){
                    holder.setImageResource(R.id.inte_item_rank,R.mipmap.head3);
                }else if(count<200){
                    holder.setImageResource(R.id.inte_item_rank,R.mipmap.head4);
                }else if(count>=200){
                    holder.setImageResource(R.id.inte_item_rank,R.mipmap.head5);
                }
                if(readImage(obj.getUserID())!=null) {
                    holder.setImageBitmap(R.id.inte_item_userimg, readImage(obj.getUserID()));
                }else {
                    downLoad(obj.getUserID(),obj.getImageUrl());
                }
                holder.setOnClickListener(R.id.inte_item_good);

            }
        };

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        adapter.setOnItemClickListener(new AdapterTools.onItemClickListener() {
            @Override
            public void onItemClick(final int i) {
                AVQuery<AVObject> query1 = new AVQuery<>("Praise");
                query1.whereEqualTo("UserID",sp.getID());
                AVQuery<AVObject> query2 = new AVQuery<>("Praise");
                query2.whereEqualTo("ShareID",shareMsgs.get(i).getId());
                AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        int num=0;
                        if(shareMsgs.get(i).getNum()!=null) {
                            num = new Integer(shareMsgs.get(i).getNum());
                        }
                        if (list.size()==1){
                            shareMsgs.get(i).setNum((num-1)+"");
                            adapter.notifyDataSetChanged();
                            // 第一参数是 className,第二个参数是 objectId
                            AVObject testObject1 = AVObject.createWithoutData("Share", shareMsgs.get(i).getId());
                            testObject1.put("Count", shareMsgs.get(i).getNum());
                            // 保存到云端
                            testObject1.saveInBackground();
                            Toast.makeText(context,"取消点赞~",Toast.LENGTH_SHORT).show();

                            //这里删除一个数据
                            // 第一参数是 className,第二个参数是 objectId
                            AVObject testObject = AVObject.createWithoutData("Praise", list.get(0).getObjectId());
                            // 保存到云端
                            testObject.deleteInBackground();

                        }else if(list.size()==0){
                            AVObject testObject1 = new AVObject("Praise");
                            testObject1.put("UserID",sp.getID());
                            testObject1.put("ShareID",shareMsgs.get(i).getId());
                            final int nums=num+1;
                            testObject1.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if(e == null){
                                        shareMsgs.get(i).setNum((nums)+"");
                                        adapter.notifyDataSetChanged();
                                        // 第一参数是 className,第二个参数是 objectId
                                        AVObject testObject1 = AVObject.createWithoutData("Share", shareMsgs.get(i).getId());
                                        testObject1.put("Count", shareMsgs.get(i).getNum());
                                        // 保存到云端
                                        testObject1.saveInBackground();
                                        Toast.makeText(context,"点赞成功~",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(context, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }


                    }
                });


                //op.insert("update Share set Count=? where id=?",new String[]{shareMsgs.get(i).getNum(),shareMsgs.get(i).getId()});



                //Toast.makeText(context,"你点击了~"+i,Toast.LENGTH_SHORT).show();
            }
        });
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

    public static class MyBroad extends BroadcastReceiver {
        public final String board="com.develop.sport.MYBROAD2";
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(board)){
                state=true;

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(broad!=null){
            getActivity().unregisterReceiver(broad);
        }
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
