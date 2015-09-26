package com.example.bushelper.struct;

import java.util.ArrayList;

/**
 * Created by chan on 15/9/26.
 */
public class Transfer {
    public int dist;	//总距离
    public int time;	//估计耗费时间
    public int foot_dist;	//总步行距离
    public int last_foot_dist;	//从终点站走到终点的距离
    public int transTimes; //换乘次数
    public ArrayList<Segment> segmentList = new ArrayList<Segment>(); //换乘的公交信息
}
