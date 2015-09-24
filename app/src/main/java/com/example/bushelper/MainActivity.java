package com.example.bushelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

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
    private ProgressDialog pd;
    private boolean NetworkStateTag;
    private final String BAIDU_APP_KEY = "08f6ef39dd1e939ab7438b5847842dbc";   //百度api的ak
    private final String BAIDU_URL = "http://api.map.baidu.com/location/ip";  //百度API URL

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

                //查询按钮点击事件
                btnSubmit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //传递城市名和线路名到LineActivity
                        Intent intent = new Intent(MainActivity.this, LineActivity.class);
                        intent.putExtra("cityName", etCityNameLine.getText().toString());
                        intent.putExtra("lineName", etLineName.getText().toString());
                        startActivity(intent);
                    }
                });
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

                //查询按钮点击事件
                btnSubmit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //传递城市名和线路名到LineActivity
                        Intent intent = new Intent(MainActivity.this, LineActivity.class);
                        intent.putExtra("cityName", etCityNameTransfer.getText().toString());
                        intent.putExtra("init", etInit.getText().toString());
                        intent.putExtra("dest", etDest.getText().toString());
                        startActivity(intent);
                    }
                });
            }
        });

        //检查网络状态
        NetworkStateTag = NetworkStateUtils.checkNetworkStateAndShowAlert(MainActivity.this);

        if(NetworkStateTag) {
            //run GetLocationAsyncTask
            new GetLocationAsyncTask().execute(BAIDU_URL);
        }
    }

    /**
     * 解析位置信息字符串
     * @param json
     * @return
     */
    private Location jsonToLocation(String json) {

        //实例化Location对象
        Location location = new Location();

        //获得status
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            int status = jsonObject.getInt("status");

            //status为0再解析
            if(status == 0) {
                //获得城市
                String address = jsonObject.getString("address");
                String addressArray[] = address.split("\\|");
                location.cityName = addressArray[2];

                //获得城市全称
                JSONObject content = new JSONObject(jsonObject.getString("content"));
                location.fullCityName = content.getString("address");

                //获得x，y坐标
                JSONObject point = new JSONObject(content.getString("point"));
                location.x = point.getString("x");
                location.y = point.getString("y");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Debug
        Log.i("location", location.cityName + "---" + location.fullCityName + "---" + location.x + "---" + location.y);

        return location;
    }

    class GetLocationAsyncTask extends AsyncTask<String, Void, Location> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //显示progressdialog
            pd = ProgressDialog.show(MainActivity.this, "请稍等", "正在获取您的位置...", false);
        }

        @Override
        protected Location doInBackground(String... params) {
            //封装请求参数，发送请求
            List<BasicNameValuePair> params2 = new LinkedList<BasicNameValuePair>();
            params2.add(new BasicNameValuePair("ak", BAIDU_APP_KEY));
            String jsonString = NetUtils.postRequest(params[0], params2);

            //返回数据解析
            return jsonToLocation(jsonString);
        }

        @Override
        protected void onPostExecute(Location location) {
            super.onPostExecute(location);

            //将数据更新到UI
            etCityNameLine.setText(location.cityName);
            etCityNameTransfer.setText(location.cityName);
            tvHint.setBackgroundColor(Color.parseColor("#5eb95e"));
            tvHint.setText("您所在的城市：" + location.fullCityName);

            //dismiss ProgressDialog
            pd.dismiss();
        }
    }


}
