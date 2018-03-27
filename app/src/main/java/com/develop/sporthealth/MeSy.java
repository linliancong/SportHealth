package com.develop.sporthealth;

import android.annotation.SuppressLint;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.develop.tools.MyLayout;
import com.develop.tools.SPTools;
import com.develop.tools.database.SQLOperator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018/3/16.
 */

public class MeSy extends Fragment implements View.OnClickListener{

    private Context context;
    private View view;
    private SQLOperator op;
    private SPTools sp;

    private List<Map<String, String>> data = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();

    private RelativeLayout tx;
    private RelativeLayout dj;
    private RelativeLayout xm;
    private RelativeLayout xb;
    private RelativeLayout tz;
    private RelativeLayout yx;
    private RelativeLayout sj;
    private RelativeLayout qq;
    private RelativeLayout sz;

    private ImageView img;
    private ImageView img_dj;
    private TextView tx_txt;
	private TextView tx2_txt;
    private MyLayout zh_txt;
    private MyLayout xm_txt;
    private MyLayout xb_txt;
    private MyLayout tz_txt;
    private MyLayout yx_txt;
    private MyLayout sj_txt;
    private MyLayout qq_txt;


    //广播通知主线程更新
    private static boolean state=false;
    private MyBroad broad;

    private static final int REQUEST_CODE_PICK_IMAGE=1;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA=2;
    private static final int CODE_RESULT_REQUEST=3;

    private int count=0;



    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x001:
                    getUserInfo();
                    sp.setIsUpdate(true);
                    break;
                case 0x002:
                    sp.setIsUpdate(true);
                    context.sendBroadcast(new Intent("com.develop.sport.MYBROAD2").setComponent(new ComponentName("com.develop.sporthealth","com.develop.sporthealth.InteractSy$MyBroad")));
                    break;
            }
        }
    };

    public MeSy(){}
    @SuppressLint("ValidFragment")
    public MeSy(Context context){
        this.context=context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.me, container, false);

            init();

            readImage();
            //广播更新
            broad=new MyBroad();
            IntentFilter filter=new IntentFilter();
            filter.addAction("com.develop.sport.MYBROAD");
            getActivity().registerReceiver(broad,filter);

            if(!sp.getIsLogin()) {
                data = op.select("select * from UserInfo where id=?", new String[]{sp.getID()});
                if (data.size() != 0) {
                    map = data.get(0);
                }

                //计算等级
                data=op.select("select count(*) num from SportFinish where UserID=?", new String[]{sp.getID()});
                if (data.size() != 0) {
                    count=new Integer(data.get(0).get("num"));
                    if(count<10){
                        img_dj.setImageResource(R.mipmap.rank_level1);
                    }else if(count<50){
                        img_dj.setImageResource(R.mipmap.rank_level2);
                    }else if(count<100){
                        img_dj.setImageResource(R.mipmap.rank_level3);
                    }else if(count<200){
                        img_dj.setImageResource(R.mipmap.rank_level4);
                    }else if(count>=200){
                        img_dj.setImageResource(R.mipmap.rank_level5);
                    }

                }
            }



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
        }

        getUserInfo();
        return view;
    }

    private void init() {

        op=new SQLOperator(context);
        sp=new SPTools(context);
        tx=view.findViewById(R.id.me_ly_tx);
        dj=view.findViewById(R.id.me_ly_dj);
        xm=view.findViewById(R.id.me_ly_xm);
        xb=view.findViewById(R.id.me_ly_xb);
        tz=view.findViewById(R.id.me_ly_tz);
        yx=view.findViewById(R.id.me_ly_yx);
        sj=view.findViewById(R.id.me_ly_sj);
        qq=view.findViewById(R.id.me_ly_qq);
        sz=view.findViewById(R.id.me_ly_sz);

        img=view.findViewById(R.id.me_img_tx);
        img_dj=view.findViewById(R.id.me_img_dj);
        tx_txt=view.findViewById(R.id.me_txt_tx);
        tx2_txt=view.findViewById(R.id.me_txt_tx2);
        zh_txt=view.findViewById(R.id.me_myly_zh);
        xm_txt=view.findViewById(R.id.me_myly_xm);
        xb_txt=view.findViewById(R.id.me_myly_xb);
        tz_txt=view.findViewById(R.id.me_myly_tz);
        yx_txt=view.findViewById(R.id.me_myly_yx);
        sj_txt=view.findViewById(R.id.me_myly_sj);
        qq_txt=view.findViewById(R.id.me_myly_qq);

        tx.setOnClickListener(this);
        dj.setOnClickListener(this);
        xm.setOnClickListener(this);
        xb.setOnClickListener(this);
        tz.setOnClickListener(this);
        yx.setOnClickListener(this);
        sj.setOnClickListener(this);
        qq.setOnClickListener(this);
        sz.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.me_ly_tx:
                if(sp.getIsLogin()) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                }else {
                    Intent it_tx=new Intent(context,Login.class);
                    startActivity(it_tx);
                }
                break;
            case R.id.me_ly_dj:
                if(sp.getIsLogin()) {
                    Intent intent = new Intent(context,MeRank.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.me_ly_zh:
                if(sp.getIsLogin()) {
                    Intent it_zh = new Intent();
                    it_zh.setClass(context, MeUpdate.class);
                    Bundle bd_zh = new Bundle();
                    bd_zh.putString("STR", map.get("UserName"));
                    bd_zh.putString("VALUE", "UerName");
                    it_zh.putExtras(bd_zh);
                    startActivity(it_zh);
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.me_ly_xm:
                if(sp.getIsLogin()) {
                    Intent it_xm = new Intent();
                    it_xm.setClass(context, MeUpdate.class);
                    Bundle bd_xm = new Bundle();
                    bd_xm.putString("STR", map.get("Name"));
                    bd_xm.putString("VALUE", "Name");
                    it_xm.putExtras(bd_xm);
                    startActivity(it_xm);
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.me_ly_xb:
                if(sp.getIsLogin()) {
                    Intent it_xb = new Intent();
                    it_xb.setClass(context, MeUpdate.class);
                    Bundle bd_xb = new Bundle();
                    bd_xb.putString("STR", map.get("Sex"));
                    bd_xb.putString("VALUE", "Sex");
                    it_xb.putExtras(bd_xb);
                    startActivity(it_xb);
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.me_ly_tz:
                if(sp.getIsLogin()) {
                    Intent it_tz = new Intent();
                    it_tz.setClass(context, MeUpdate.class);
                    Bundle bd_tz = new Bundle();
                    bd_tz.putString("STR", map.get("Weight"));
                    bd_tz.putString("VALUE", "Weight");
                    it_tz.putExtras(bd_tz);
                    startActivity(it_tz);
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.me_ly_yx:
                if(sp.getIsLogin()) {
                    Intent it_yx = new Intent();
                    it_yx.setClass(context, MeUpdate.class);
                    Bundle bd_yx = new Bundle();
                    bd_yx.putString("STR", map.get("Email"));
                    bd_yx.putString("VALUE", "Email");
                    it_yx.putExtras(bd_yx);
                    startActivity(it_yx);
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.me_ly_sj:
                if(sp.getIsLogin()) {
                    Intent it_sj = new Intent();
                    it_sj.setClass(context, MeUpdate.class);
                    Bundle bd_sj = new Bundle();
                    bd_sj.putString("STR", map.get("Phone"));
                    bd_sj.putString("VALUE", "Phone");
                    it_sj.putExtras(bd_sj);
                    startActivity(it_sj);
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.me_ly_qq:
                if(sp.getIsLogin()) {
                    Intent it_qq = new Intent();
                    it_qq.setClass(context, MeUpdate.class);
                    Bundle bd_qq = new Bundle();
                    bd_qq.putString("STR", map.get("QQ"));
                    bd_qq.putString("VALUE", "QQ");
                    it_qq.putExtras(bd_qq);
                    startActivity(it_qq);
                }else {
                    Toast.makeText(context,"请先登录~",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.me_ly_sz:
                Intent it_sz=new Intent(context,MeSetting.class);
                startActivity(it_sz);
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode==RESULT_OK &&data!=null) {
            if (data != null) {
                cropPhoto(data.getData());
            }
        }
        else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            Uri uri = data.getData();
            //to do find the path of pic
        }else if(requestCode==CODE_RESULT_REQUEST)
        {
            if (data!=null) {
                setImageToHeadView(data);
                handler.sendEmptyMessage(0x002);
            }
        }
    }

    /**
     *获取用户信息
     * */
    public void getUserInfo(){
        if(!sp.getIsLogin()){
            tx_txt.setText("点我登录~");
            tx2_txt.setText("");
            zh_txt.setText("");
            xm_txt.setText("");
            xb_txt.setText("");
            tz_txt.setText("");
            yx_txt.setText("");
            sj_txt.setText("");
            qq_txt.setText("");
            map=new HashMap<>();
        }else {
            data = op.select("select * from UserInfo where id=?", new String[]{sp.getID()});
            if (data.size()!=0) {
                map = data.get(0);
                tx_txt.setText(map.get("Name"));
                tx2_txt.setText(map.get("UserName"));
                zh_txt.setText(map.get("UserName"));
                xm_txt.setText(map.get("Name"));
                xb_txt.setText(map.get("Sex"));
                tz_txt.setText(map.get("Weight"));
                if(map.get("Weight")!=null) {
                    sp.setWeight(new Float(map.get("Weight")));
                }
                yx_txt.setText(map.get("Email"));
                sj_txt.setText(map.get("Phone"));
                qq_txt.setText(map.get("QQ"));

            }
        }

    }

    /**
     *裁剪图片
     * */
    public void cropPhoto(Uri uri){
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");

        //设置裁剪
        intent.putExtra("crop","true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", img.getWidth());
        intent.putExtra("outputY", img.getHeight());
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    public void setImageToHeadView(Intent intent){
        Bundle bd=intent.getExtras();
        if(bd!=null){
            Bitmap photo=bd.getParcelable("data");
            img.setImageBitmap(toOvalBitmap(photo,200));
            saveImage(toOvalBitmap(photo,200));

        }
    }

    /**
     * 保存图片
     */
    private void saveImage(Bitmap bitmap) {
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
            File file = new File(filesDir,sp.getID()+".png");
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,fos);
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

    /**
     * 设置图片圆形显示
     * */
    public static Bitmap toOvalBitmap(Bitmap bitmap, float pix) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getHeight(),
                    bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
            RectF rectF = new RectF(rect);
            float roundPx = pix;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            int color = 0xff424242;
            paint.setColor(color);
            canvas.drawOval(rectF, paint);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }


    public static class MyBroad extends BroadcastReceiver {
        public final String board="com.develop.sport.MYBROAD";
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
