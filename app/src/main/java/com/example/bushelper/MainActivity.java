package com.example.bushelper;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button btnLine; //线路查询
    private Button btnTransfer; //换乘查询
    private Button btnSubmit;   //提交
    private EditText etCityNameLine;    //线路查询的城市名
    private EditText etLineName;    //公交线路
    private EditText etCityNameTransfer;    //换乘查询的城市名
    private EditText etInit;    //起始地
    private EditText etDest;    //目的地
    private TextView tvHint;    //底部提示信息
    private LinearLayout layoutLine;
    private LinearLayout layoutTransfer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化控件
        btnLine = (Button) findViewById(R.id.activity_main_btn_line);
        btnTransfer = (Button) findViewById(R.id.activity_main_btn_transfer);
        btnSubmit = (Button) findViewById(R.id.activity_main_btn_submit);
        etCityNameLine = (EditText) findViewById(R.id.activity_main_et_city_name_line);
        etLineName = (EditText) findViewById(R.id.activity_main_et_line_name);
        etCityNameTransfer = (EditText) findViewById(R.id.activity_main_et_city_name_transfer);
        etInit = (EditText) findViewById(R.id.activity_main_et_init);
        etDest = (EditText) findViewById(R.id.activity_main_et_dest);
        tvHint = (TextView) findViewById(R.id.activity_main_tv_hint);
        layoutLine = (LinearLayout) findViewById(R.id.activity_main_layout_line);
        layoutTransfer = (LinearLayout) findViewById(R.id.activity_main_layout_transfer);

        //线路查询点击事件
        btnLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTransfer.setBackgroundColor(Color.parseColor("#EEEEEE"));
                btnTransfer.setTextColor(Color.parseColor("#000000"));
                btnLine.setBackgroundColor(Color.parseColor("#3390D2"));
                btnLine.setTextColor(Color.parseColor("#FFFFFF"));

                layoutTransfer.setVisibility(View.GONE);
                layoutLine.setVisibility(View.VISIBLE);
            }
        });

        //换乘查询点击事件
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLine.setBackgroundColor(Color.parseColor("#EEEEEE"));
                btnLine.setTextColor(Color.parseColor("#000000"));
                btnTransfer.setBackgroundColor(Color.parseColor("#3390D2"));
                btnTransfer.setTextColor(Color.parseColor("#FFFFFF"));

                layoutLine.setVisibility(View.GONE);
                layoutTransfer.setVisibility(View.VISIBLE);
            }
        });
    }




}
