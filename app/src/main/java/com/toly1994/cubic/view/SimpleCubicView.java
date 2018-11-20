package com.toly1994.cubic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.toly1994.cubic.analyze.HelpDraw;
import com.toly1994.cubic.analyze.gold12.JudgeMan;

/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/16 0016:9:04<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：贝塞尔三次曲线初体验
 */
public class SimpleCubicView extends View {
    private Point mCoo = new Point(500, 500);//坐标系
    private Picture mCooPicture;//坐标系canvas元件
    private Picture mGridPicture;//网格canvas元件
    private Paint mHelpPint;//辅助画笔
    Point p0 = new Point(0, 0);
    Point p1 = new Point(200, 200);
    Point p2 = new Point(300, -100);
    Point p3 = new Point(500, 300);

    Point src = new Point(0, 0);



    private Paint mPaint;//主画笔
    private Path mPath;//主路径


    public SimpleCubicView(Context context) {
        this(context, null);
    }

    public SimpleCubicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();//初始化
    }

    private void init() {
        //初始化主画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        //初始化主路径
        mPath = new Path();

        //初始化辅助
        mHelpPint = HelpDraw.getHelpPint(0xffF83517);
        mCooPicture = HelpDraw.getCoo(getContext(), mCoo);
        mGridPicture = HelpDraw.getGrid(getContext());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        HelpDraw.draw(canvas, mGridPicture, mCooPicture);
        canvas.save();
        canvas.translate(mCoo.x, mCoo.y);
        mPath.moveTo(p0.x, p0.y);
        mPath.cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
        canvas.drawPath(mPath, mPaint);

        helpView(canvas);
        canvas.restore();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                src.x = (int) event.getX() - mCoo.x;
                src.y = (int) event.getY() - mCoo.y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (JudgeMan.judgeCircleArea(src, p0, 30)) {
                    setPos(event, p0);
                }
                if (JudgeMan.judgeCircleArea(src, p1, 30)) {
                    setPos(event, p1);
                }

                if (JudgeMan.judgeCircleArea(src, p2, 30)) {
                    setPos(event, p2);
                }

                if (JudgeMan.judgeCircleArea(src, p3, 30)) {
                    setPos(event, p3);
                }
                mPath.reset();
                src.x = (int) event.getX() - mCoo.x;
                src.y = (int) event.getY() - mCoo.y;
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 设置点位
     * @param event 事件
     * @param p 点位
     */
    private void setPos(MotionEvent event, Point p) {
        p.x = (int) event.getX() - mCoo.x;
        p.y = (int) event.getY() - mCoo.y;
    }

    private void helpView(Canvas canvas) {
        mHelpPint.setStrokeWidth(20);
        HelpDraw.drawPos(canvas, mHelpPint, p0, p1, p2, p3);
        mHelpPint.setStrokeWidth(2);
        HelpDraw.drawLines(canvas, mHelpPint, p0, p1, p2, p3);
        mHelpPint.setPathEffect(null);
        mHelpPint.setStyle(Paint.Style.FILL);
        canvas.drawText("起始点p0:"+p0.toString(),700,-300,mHelpPint);
        canvas.drawText("控制点p1:"+p1.toString(),700,-240,mHelpPint);
        canvas.drawText("控制点p2:"+p2.toString(),700,-180,mHelpPint);
        canvas.drawText("终止点p3:"+p3.toString(),700,-120,mHelpPint);
    }


}
