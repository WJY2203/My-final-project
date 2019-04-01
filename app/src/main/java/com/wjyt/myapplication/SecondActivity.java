package com.wjyt.myapplication;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import android.app.Activity;
import android.media.MediaRecorder;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;


public class SecondActivity extends Activity{
    private Button btn_RecordStart, btn_RecordStop, btn_translate;
    private MediaRecorder mediaRecorder;
    private boolean isRecording;
    private File file= new File("/sdcard/mediarecorder.amr");      /* 存储路径 */

    private static String mytext = null;
    private static String lag_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent=getIntent();
        String NewsID=intent.getStringExtra("newsid");
        lag_type = NewsID;
        TextView  textView2 =(TextView)findViewById(R.id.record_voice);
        textView2.setText(NewsID);

        btn_RecordStart = (Button) findViewById(R.id.btn_RecordStart);
        btn_RecordStop = (Button) findViewById(R.id.btn_RecordStop);
        btn_translate = (Button) findViewById(R.id.btn_translate);

        btn_RecordStop.setEnabled(false);
        btn_translate.setEnabled(false);

        btn_RecordStart.setOnClickListener(click);
        btn_RecordStop.setOnClickListener(click);
        btn_translate.setOnClickListener(click);
    }

    private View.OnClickListener click = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_RecordStart:
                    startRecord();
                    break;
                case R.id.btn_RecordStop:
                    stopRecord();
                    break;
                case R.id.btn_translate:
                    translate();
                    break;
                default:
                    break;
            }
        }
    };

    protected void translate(){

        if(mytext == null){
            Toast.makeText(SecondActivity.this, "No content can be translated", Toast.LENGTH_SHORT).show();

        }else{
            Intent intent=new Intent();
            intent.putExtra("newsid", mytext);//设置参数,
            //intent.putExtra("lagtype", lag_type);//设置参数,
            intent.setClass(SecondActivity.this, ThirdActivity.class);//从哪里跳到哪里
            SecondActivity.this.startActivity(intent);
        }

    }

    /**
     * 开始录音
     */
    protected void startRecord() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            if (file.exists()) {                                             /* 检测文件是否存在 */
                file.delete();
            }
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);        /* 设置麦克风 */
            mediaRecorder.setAudioSamplingRate(8000);
            mediaRecorder.setAudioChannels(1);                                  /* 单声道采样 */
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   /* 设置输出格式 */
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   /* 设置采样波形 */
            mediaRecorder.setAudioEncodingBitRate(16000);
            mediaRecorder.setOutputFile(file.getAbsolutePath());             /* 存储路径 */
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();     /* 开始录音 */
                isRecording = true;
                btn_RecordStart.setEnabled(false);
                btn_RecordStop.setEnabled(true);
                btn_translate.setEnabled(false);
                Toast.makeText(SecondActivity.this, "Begin to input voice", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void stopRecord() {
        if (file != null && file.exists()) {
            mediaRecorder.stop();         /* 停止录音 */
            mediaRecorder.release();      /* 释放资源 */
            mediaRecorder = null;
            isRecording = false;
            btn_RecordStart.setEnabled(true);
            btn_RecordStop.setEnabled(false);
            btn_translate.setEnabled(true);
            Toast.makeText(SecondActivity.this, "Stop Inputting voice", Toast.LENGTH_SHORT).show();
            if(mytext == null){
                //btn_translate.setEnabled(false);
                Toast.makeText(SecondActivity.this, "Speech recognition in progress", Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(SecondActivity.this, "Stop Inputting voice", Toast.LENGTH_SHORT).show();
            /* 开始识别 */
            try {
                getToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final String serverURL = "http://vop.baidu.com/server_api";   //语音识别网关
    private static String token = null;
    private static final String apiKey = "98C36KDSvikCvnW43M9Eqtbj";             // API Key 
    private static final String secretKey = "yLMHBfjK2M41XmS8qKN6r9F3iTtO5wUA";  // Secret Key
    private static final String cuid = "358240051111110";            //唯一表示码
    private static int dev_pid = 1537 ;
    private void getToken(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" +"&client_id=" + apiKey + "&client_secret=" + secretKey;
                try {
                    connection = (HttpURLConnection) new URL(getTokenURL).openConnection();
                    token = new JSONObject(printResponse(connection)).getString("access_token");
                    SpeechRecognition();    //开始语音识别
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(connection!= null) connection.disconnect();
                }
            }
        }).start();
    }

    private void SpeechRecognition(){

        int result1 = lag_type.indexOf("E");
        if(result1 != -1){
            dev_pid = 1737;
        }else{
            dev_pid = 1537;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                String strc;
                try {
                    File pcmFile = new File(file.getAbsolutePath());
                    HttpURLConnection conn = (HttpURLConnection) new URL(serverURL+ "?cuid=" + cuid +
                            "&token=" + token+ "&dev_pid=" + dev_pid).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "audio/amr; rate=16000");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.write(loadFile(pcmFile));
                    wr.flush();
                    wr.close();
                    strc=printResponse(conn);
                    Message message = new Message();
                    message.what = 0x02;
                    message.obj =strc;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //字符处理
    private  String printResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
            Toast.makeText(SecondActivity.this, "buzidao", Toast.LENGTH_LONG).show();
            return "";
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        Message message = new Message();
        message.what = 0x01;
        message.obj = new JSONObject(response.toString()).toString(4);
        handler.sendMessage(message);
        return response.toString();
    }
    //文件加载
    private byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            String response=(String)msg.obj;
            String strc=null;
            switch (msg.what){
                case 0x01:
                    Log.e("return:",response);            //得到返回的所有结果
                    break;
                case 0x02:
                    strc=getRectstr(response,"[","]");    //得到返回语音内容
                    Log.d("return:",strc);
                    TextView text_curtime = (TextView) findViewById(R.id.record_voice);
                    text_curtime.setText(strc);
                    mytext = strc;

                    break;
                default:
                    break;
            }
        }
    };

    private String getRectstr(String str,String strStart, String strEnd){
        if (str.indexOf(strStart) < 0 || str.indexOf(strEnd) < 0) return "";
        return str.substring(str.indexOf(strStart) + strStart.length()+1, str.indexOf(strEnd)-1);
    }
}

