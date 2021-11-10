package com.wk.chart.enumeration;

/**
 * <p>PositionType</p>
 * 位置
 */

public interface PositionType {
    //自动
    int AUTO = 0;
    //开始
    int START = 2;
    //上
    int TOP = 4;
    //结束
    int END = 8;
    //下
    int BOTTOM = 16;
    //全部
    int ALL = 32;
    //上和下
    int TOP_AND_BOTTOM = 64;
    //开始和结束
    int START_AND_END = 128;
    //垂直居中
    int CENTER_VERTICAL = 512;
    //水平居中
    int CENTER_HORIZONTAL = 1024;
    //垂直居外
    int OUTSIDE_VERTICAL = 2048;
    //水平居外
    int OUTSIDE_HORIZONTAL = 4096;
}
