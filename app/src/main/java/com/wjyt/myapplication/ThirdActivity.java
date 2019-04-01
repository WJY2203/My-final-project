package com.wjyt.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.wjyt.myapplication.TranslateAPI;

import com.google.gson.Gson;

import java.util.List;

public class ThirdActivity extends Activity {

    private Button button1;
    private Button button2;
    private TextView textView;
    private TextView textView2;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        Intent intent=getIntent();
        String NewsID=intent.getStringExtra("newsid");
        Log.d("return:",NewsID);
        textView2 =(TextView)findViewById(R.id.textView2);
        textView2.setText(NewsID);
        initView();
    }

    private void initView() {

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        textView = (TextView) findViewById(R.id.textView);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String query =textView2.getText().toString();
                //获取待翻译的内容
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String resultJson = new TranslateAPI().getTransResult(query, "zh", "en"); //设置翻译原语种及翻译结果语种
                        //拿到结果，对结果进行解析。
                        Gson gson = new Gson();
                        TranslateResult translateResult = gson.fromJson(resultJson, TranslateResult.class);
                        final List<TranslateResult.TransResultBean> trans_result = translateResult.getTrans_result();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                String dst = "";
                                for (TranslateResult.TransResultBean s : trans_result
                                ) {
                                    dst = dst + "\n" + s.getDst();
                                }
                                textView.setText(dst);
                            }
                        });
                    }
                }).start();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String query =textView2.getText().toString();
                //获取待翻译的内容
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String resultJson = new TranslateAPI().getTransResult(query, "en", "zh"); //设置翻译原语种及翻译结果语种
                        //拿到结果，对结果进行解析。
                        Gson gson = new Gson();
                        TranslateResult translateResult = gson.fromJson(resultJson, TranslateResult.class);
                        final List<TranslateResult.TransResultBean> trans_result = translateResult.getTrans_result();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                String dst = "";
                                for (TranslateResult.TransResultBean s : trans_result
                                ) {
                                    dst = dst + "\n" + s.getDst();
                                }
                                textView.setText(dst);
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
