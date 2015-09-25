package com.example.bushelper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.bushelper.struct.Line;
import com.example.bushelper.utils.NetUtils;
import com.example.bushelper.utils.NetworkStateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineActivity extends Activity {

    private ExpandableListView elv;
    private String cityName;
    private String lineName;
    private ProgressDialog pd;
    private boolean NetworkStateTag;
    private ActionBar actionBar;
    private List<String> parent = new ArrayList<String>();
    private Map<String, List<String>> child = new HashMap<String, List<String>>();
    private final String AIBANG_APP_KEY = "d706b1f36e6adfdb862f7f54c132390f";
    private final String AIBANG_LINE_URL = "http://openapi.aibang.com/bus/lines";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);

        //actionBar上添加返回按钮
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        //实例化控件
        elv = (ExpandableListView) findViewById(R.id.activity_line_elv);

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

    private List<Line> jsonToLineList(String json) {

        //实例化ArrayList<BusData>对象
        List<Line> lineList = new ArrayList<Line>();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            //获得每个线路line的信息
            JSONObject linesObj = new JSONObject(jsonObject.getString("lines"));
            JSONArray lineArr = new JSONArray(linesObj.getString("line"));
            for(int i = 0; i < lineArr.length(); i++) {
                Line line = new Line();
                JSONObject jsonObjLine = (JSONObject) lineArr.get(i);

                line.name = jsonObjLine.getString("name").replaceFirst("\\(", "\n(");
                line.info = "[线路信息]\n" + jsonObjLine.getString("info").replace(";", "\n");

                //改变格式，使更美观
                int j = 2;
                String temp = "(1) " + jsonObjLine.getString("stats");
                while(temp.indexOf(";") > 0) {
                    temp = temp.replaceFirst(";", "\n(" + j +") ");
                    j++;
                }
                line.stats = "[沿途站点]\n" + temp;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    class LineAsyncTask extends AsyncTask<String, Void, List<Line>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //显示progressdialog
            pd = ProgressDialog.show(LineActivity.this, null, "正在搜索...", false);
        }

        @Override
        protected List<Line> doInBackground(String... params) {
            //封装请求参数，发送请求
            Map<String, String> params2 = new HashMap<String, String>();
            params2.put("city", cityName);
            params2.put("q", lineName);
            params2.put("app_key", AIBANG_APP_KEY);
            params2.put("alt", "json");
            String jsonString = NetUtils.getRequest(AIBANG_LINE_URL, params2);

            //返回数据解析
            return jsonString != null ? jsonToLineList(jsonString) : new ArrayList<Line>();
        }

        @Override
        protected void onPostExecute(List<Line> lines) {
            super.onPostExecute(lines);

            //没有找到结果，finish
            if(lines.size() == 0) {
                Toast.makeText(LineActivity.this, "没有找到线路", Toast.LENGTH_SHORT).show();
                LineActivity.this.finish();
            } else {
                //改变ActionBar的title
                actionBar.setTitle("找到" + lines.size() + "条线路");

                //初始化数据
                for (Line line : lines) {
                    List<String> listTemp = new ArrayList<String>();

                    parent.add(line.name);
                    listTemp.add(line.info);
                    listTemp.add(line.stats);
                    child.put(line.name, listTemp);
                }

                //加载数据到列表
                elv.setAdapter(new MyExpandableListAdapter(LineActivity.this, parent, child));
            }

            //dismiss pg
            pd.dismiss();
        }
    }

}
