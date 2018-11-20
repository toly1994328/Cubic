package com.toly1994.cubic.analyze.gold12;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/16 0016:10:25<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：审断之神----捷特麾下十二战神之一,负责审判任何罪恶
 */
public class JudgeMan {
    /**
     * 判断出是否在某点的半径为r圆范围内
     *
     * @param src 目标点
     * @param dst 主动点
     * @param r   半径
     */
    public static boolean judgeCircleArea(Point src, Point dst, float r) {
        return disPos2d(src.x, src.y, dst.x, dst.y) <= r;
    }

    public static boolean judgeCircleArea(PointF src, PointF dst, float r) {
        return disPos2d(src.x, src.y, dst.x, dst.y) <= r;
    }

    /**
     * 两点间距离函数
     */
    public static float disPos2d(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

}
