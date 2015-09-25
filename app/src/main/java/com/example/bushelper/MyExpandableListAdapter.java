package com.example.bushelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by chan on 15/9/24.
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> parent;
    private Map<String, List<String>> child;

    public MyExpandableListAdapter(Context context, List<String> parent, Map<String, List<String>> child) {
        this.context = context;
        this.parent = parent;
        this.child = child;
    }

    @Override
    public int getGroupCount() {
        return parent.size();
    }

    //获取当前父item下的子item的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        String key = parent.get(groupPosition);
        int size = child.get(key).size();
        return size;
    }

    //获取当前父item的数据
    @Override
    public Object getGroup(int groupPosition) {
        return parent.get(groupPosition);
    }

    //得到子item需要关联的数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String key = parent.get(groupPosition);
        return (child.get(key).get(childPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //得到子item的ID
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //设置父item组件
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_parent, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.layout_parent_tv);
        tv.setText(this.parent.get(groupPosition));
        return tv;
    }

    //设置子item的组件
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String key = this.parent.get(groupPosition);
        String info = child.get(key).get(childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_children, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.layout_children_tv);
        tv.setText(info);
        return tv;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
