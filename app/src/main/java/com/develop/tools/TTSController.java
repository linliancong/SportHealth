package com.develop.tools;

/**
 * Created by Administrator on 2018/1/15.
 */

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import java.util.LinkedList;

/**
 * 当前DEMO的播报方式是单条。
 */
public class TTSController {

    /**
     * 请替换您自己申请的ID。
     */
    private final String appId = "5abd9b42";

    public static TTSController ttsManager;
    private Context mContext;
    private SpeechSynthesizer mTts;
    private boolean isPlaying = false;
    private LinkedList<String> wordList = new LinkedList();

    private TTSController(Context context) {
        mContext = context.getApplicationContext();
        SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "=" + appId);
        if (mTts == null) {
            createSynthesizer();
        }
    }

    private void createSynthesizer() {
        mTts = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
            @Override
            public void onInit(int errorcode) {
                if (ErrorCode.SUCCESS == errorcode) {
                } else {
                    Toast.makeText(mContext, "语音合成初始化失败!", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void init() {
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置语速,值范围：[0, 100],默认值：50
        mTts.setParameter(SpeechConstant.SPEED, "55");
        //设置音量
        mTts.setParameter(SpeechConstant.VOLUME, "tts_volume");
        //设置语调
        mTts.setParameter(SpeechConstant.PITCH, "tts_pitch");
    }

    public static TTSController getInstance(Context context) {
        if (ttsManager == null) {
            ttsManager = new TTSController(context);
        }
        return ttsManager;
    }

    public void stopSpeaking() {
        if (wordList != null) {
            wordList.clear();
        }
        if (mTts != null) {
            mTts.stopSpeaking();
        }
        isPlaying = false;
    }

    public void destroy() {
        if (wordList != null) {
            wordList.clear();
        }
        if (mTts != null) {
            mTts.destroy();
        }
    }

    /**
     *合成回调监听。
     */
    private SynthesizerListener mTtsListener=new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            // show Tip("开始播放");
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {
            // showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            // showTip("继续播放");
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        public void onCompleted(SpeechError error) {
            if(error ==null) {
                // showTip("播放完成");
            }else if(error !=null) {
                // showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }

    };

    //这里输入要播报的文字
    public void Speark(String str){
        mTts.startSpeaking(str,mTtsListener);
    }

}

