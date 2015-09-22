package com.example.bushelper;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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
    }


}
