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
import com.develop.bean.ShareMsg;
import com.develop.tools.AdapterTools;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.io.File;
import java.util.ArrayList;
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
                    setAdapter();
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


            setAdapter();
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

    private void setAdapter() {
        if(readImage(sp.getID())!=null) {
            userimg.setImageBitmap(readImage(sp.getID()));
        }
        username.setText(sp.getUserName());

        List<Map<String, String>> data = new ArrayList<>();
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
        }

        adapter=new AdapterTools<ShareMsg>(shareMsgs, R.layout.interact_item) {
            @Override
            public void bindView(ViewHolder holder, ShareMsg obj) {
                holder.setText(R.id.inte_item_username,obj.getName());
                holder.setText(R.id.inte_item_content,obj.getContent());
                holder.setText(R.id.inte_item_num,obj.getNum());
                if(readImage(obj.getUserID())!=null) {
                    holder.setImageBitmap(R.id.inte_item_userimg, readImage(obj.getUserID()));
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
            public void onItemClick(int i) {
                int num=0;
                if(shareMsgs.get(i).getNum()!=null) {
                    num = new Integer(shareMsgs.get(i).getNum());
                }
                shareMsgs.get(i).setNum((num+1)+"");
                adapter.notifyDataSetChanged();
                op.insert("update Share set Count=? where id=?",new String[]{shareMsgs.get(i).getNum(),shareMsgs.get(i).getId()});
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
}
