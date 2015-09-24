package com.example.bushelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineActivity extends AppCompatActivity {

    private String cityName;
    private String lineName;
    private ProgressDialog pd;
    private boolean NetworkStateTag;
    private final String AIBANG_APP_KEY = "d706b1f36e6adfdb862f7f54c132390f";
    private final String AIBANG_LINE_URL = "http://openapi.aibang.com/bus/lines";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);

        //获得MainActivity传过来的参数
        Intent intent = getIntent();
        cityName = intent.getStringExtra("cityName");
        lineName = intent.getStringExtra("lineName");

        //检查网络状态
        NetworkStateTag = NetworkStateUtils.checkNetworkStateAndShowAlert(LineActivity.this);

        if(NetworkStateTag) {
            //run GetLocationAsyncTask
            new LineAsyncTask().execute(AIBANG_LINE_URL);
        }
    }

    private ArrayList<Line> jsonToBusData(String json) {

        //实例化ArrayList<BusData>对象
        ArrayList<Line> lineList = new ArrayList<Line>();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            //获得每个线路line的信息
            JSONObject linesObj = new JSONObject(jsonObject.getString("lines"));
            JSONArray lineArr = new JSONArray(linesObj.getString("line"));
            for(int i = 0; i < lineArr.length(); i++) {
                Line line = new Line();
                JSONObject jsonObjLine = (JSONObject) lineArr.get(i);

                line.name = jsonObjLine.getString("name");
                line.info = jsonObjLine.getString("info").replace(";", "<br/>");

                //改变格式，使更美观
                int j = 2;
                String temp = "(1)" + jsonObjLine.getString("stats");
                while(temp.indexOf(";") > 0) {
                    temp = temp.replaceFirst(";", "<br/>(" + j +")");
                    j++;
                }
                line.stats = temp;

                lineList.add(line);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //测试用，遍历输出
        for(Line line:lineList) {
            Log.i("mytag", line.name + "---" + line.info + "---" + line.stats);
        }

        return lineList;
    }

    class LineAsyncTask extends AsyncTask<String, Void, List<Line>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //显示progressdialog
            pd = ProgressDialog.show(LineActivity.this, "请稍等", "正在搜索...", false);
        }

        @Override
        protected List<Line> doInBackground(String... params) {
            //封装请求参数，发送请求
            Map<String, String> params2 = new HashMap<String, String>();
            params2.put("city", cityName);
            params2.put("q", lineName);
            params2.put("appKey", AIBANG_APP_KEY);
            params2.put("alt", "json");
            String jsonString = NetUtils.getRequest(AIBANG_LINE_URL, params2);

            //返回数据解析
            return jsonToBusData(jsonString);
        }

        @Override
        protected void onPostExecute(List<Line> lines) {
            super.onPostExecute(lines);

            Log.d("myTag", lines.size() + "");
        }
    }

}
