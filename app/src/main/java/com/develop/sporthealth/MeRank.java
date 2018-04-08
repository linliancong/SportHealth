package com.develop.sporthealth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/26.
 */

public class MeRank extends AppCompatActivity {

    private Context context;
    private SQLOperator op;
    private SPTools sp;

    private ImageView img;
    private ImageView img_dj;

    private TextView txt_dj;
    private TextView txt_count;
    private TextView txt_rank;

    private TextView txt_1;
    private TextView txt_2;
    private TextView txt_3;
    private TextView txt_4;
    private TextView txt_5;

    private MyLayout back;
    private LinearLayout visible;

    private int count=0;

    private List<Map<String, String>> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_rank);

        init();

        readImage();
        getRank();


    }

    private void init() {
        context=getApplicationContext();
        op=new SQLOperator(context);
        sp=new SPTools(context);

        img=findViewById(R.id.merank_img);
        img_dj=findViewById(R.id.merank_img_dj);
        txt_count=findViewById(R.id.merank_count);
        txt_rank=findViewById(R.id.merank_rank);
        txt_dj=findViewById(R.id.merank_dj);
        back=findViewById(R.id.merank_back);
        visible=findViewById(R.id.merank_vi);

        txt_1=findViewById(R.id.merank_txt1);
        txt_2=findViewById(R.id.merank_txt2);
        txt_3=findViewById(R.id.merank_txt3);
        txt_4=findViewById(R.id.merank_txt4);
        txt_5=findViewById(R.id.merank_txt5);

        txt_1.setText("累计达标天数 < 10天");
        txt_2.setText("10天 ≤ 累计达标天数 < 50天");
        txt_3.setText("50天 ≤ 累计达标天数 < 100天");
        txt_4.setText("100天 ≤ 累计达标天数 < 200天");
        txt_5.setText("累计达标天数 ≥ 200天");

        back.setOnClickListener(new MyLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getRank() {
        //计算等级
        //从服务器中获取数据
        AVQuery<AVObject> query = new AVQuery<>("SportFinish");
        query.whereEqualTo("UserID", sp.getID());
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    count=i;
                    if(count<10){
                        img_dj.setImageResource(R.mipmap.head1);
                        txt_dj.setText("菜鸟");
                        txt_rank.setText("新秀");
                        txt_count.setText((10-count)+"");
                    }else if(count<50){
                        img_dj.setImageResource(R.mipmap.head2);
                        txt_dj.setText("新秀");
                        txt_rank.setText("达人");
                        txt_count.setText((50-count)+"");
                    }else if(count<100){
                        img_dj.setImageResource(R.mipmap.head3);
                        txt_dj.setText("达人");
                        txt_rank.setText("健将");
                        txt_count.setText((100-count)+"");
                    }else if(count<200){
                        img_dj.setImageResource(R.mipmap.head4);
                        txt_dj.setText("健将");
                        txt_rank.setText("王者");
                        txt_count.setText((200-count)+"");
                    }else if(count>=200){
                        img_dj.setImageResource(R.mipmap.head5);
                        txt_dj.setText("王者");
                        visible.setVisibility(View.GONE);
                    }
                }
            }
        });
        /*data=op.select("select count(*) num from SportFinish where UserID=?", new String[]{sp.getID()});
        if (data.size() != 0) {
            count=new Integer(data.get(0).get("num"));
            if(count<10){
                img_dj.setImageResource(R.mipmap.head1);
                txt_dj.setText("菜鸟");
                txt_rank.setText("新秀");
                txt_count.setText((10-count)+"");
            }else if(count<50){
                img_dj.setImageResource(R.mipmap.head2);
                txt_dj.setText("新秀");
                txt_rank.setText("达人");
                txt_count.setText((50-count)+"");
            }else if(count<100){
                img_dj.setImageResource(R.mipmap.head3);
                txt_dj.setText("达人");
                txt_rank.setText("健将");
                txt_count.setText((100-count)+"");
            }else if(count<200){
                img_dj.setImageResource(R.mipmap.head4);
                txt_dj.setText("健将");
                txt_rank.setText("王者");
                txt_count.setText((200-count)+"");
            }else if(count>=200){
                img_dj.setImageResource(R.mipmap.head5);
                txt_dj.setText("王者");
                visible.setVisibility(View.GONE);
            }

        }*/
    }

    /**
     * 读取图片
     * */
    private boolean readImage() {
        File filesDir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断sd卡是否挂载
            //路径1：storage/sdcard/Android/data/包名/files
            filesDir = context.getExternalFilesDir("");
        }else{//手机内部存储
            //路径2：data/data/包名/files
            filesDir = context.getFilesDir();
        }
        File file = new File(filesDir,sp.getID()+".png");
        if(file.exists()){
            //存储--->内存
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            img.setImageBitmap(bitmap);
            return true;
        }
        return false;
    }
}
