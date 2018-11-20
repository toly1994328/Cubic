package com.toly1994.cubic.view;

import android.animation.ValueAnimator;
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
import com.toly1994.cubic.analyze.L;

/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/16 0016:9:04<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：贝塞尔三次曲线初体验
 */
public class Lever1CubicView extends View {
    private Point mCoo = new Point(500, 500);//坐标系
    private Picture mCooPicture;//坐标系canvas元件
    private Picture mGridPicture;//网格canvas元件
    private Paint mHelpPint;//辅助画笔
    Point c1p0 = new Point(0, 0);
    Point c1p1 = new Point(300, 0);
    Point c1p2 = new Point(150, -200);
    Point c1p3 = new Point(300, -200);

    Point src = new Point(0, 0);

    Point ctrlPos = new Point(400, 0);


    private Paint mPaint;//主画笔
    private Path mPath;//主路径
    private ValueAnimator mAnimator;


    public Lever1CubicView(Context context) {
        this(context, null);
    }

    public Lever1CubicView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();//初始化
    }

    private void init() {
        //初始化主画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
//        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        //初始化主路径
        mPath = new Path();

        //初始化辅助
        mHelpPint = HelpDraw.getHelpPint(0xffF83517);
        mCooPicture = HelpDraw.getCoo(getContext(), mCoo);
        mGridPicture = HelpDraw.getGrid(getContext());
        //数字时间流
        mAnimator = ValueAnimator.ofFloat(1, 0);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.setRepeatCount(-1);
        mAnimator.addUpdateListener(a -> {
            float rate = (float) a.getAnimatedValue();
            L.d(rate + L.l());
            c1p2.y = -(int) (rate * 200);
            c1p3.y = -(int) (rate * 200);
            mPath.reset();
            invalidate();
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {

        float rate = (float) ((Math.sqrt(1.8f)));
        super.onDraw(canvas);
        HelpDraw.draw(canvas, mGridPicture, mCooPicture);
        canvas.save();
        canvas.translate(mCoo.x, mCoo.y);

        mPaint.setStyle(Paint.Style.STROKE);
        mPath.lineTo(c1p0.x, c1p0.y);
        mPath.cubicTo(c1p1.x, c1p1.y, c1p2.x, c1p2.y, c1p3.x, c1p3.y);

        reflectY(c1p0, c1p1, c1p2, c1p3, mPath);


        canvas.drawPath(mPath, mPaint);

        helpView(canvas);
        canvas.restore();
    }

    private void reflectY(Point p0, Point p1, Point p2, Point p3, Path path) {
        path.cubicTo(p3.x * 2 - p2.x, p2.y, p3.x * 2 - p1.x, p1.y, p3.x * 2 - p0.x, p0.y);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAnimator.start();
                src.x = (int) event.getX() - mCoo.x;
                src.y = (int) event.getY() - mCoo.y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (JudgeMan.judgeCircleArea(src, c1p0, 30)) {
                    setPos(event, c1p0);
                }
                if (JudgeMan.judgeCircleArea(src, c1p1, 30)) {
                    setPos(event, c1p1);
                }

                if (JudgeMan.judgeCircleArea(src, c1p2, 30)) {
                    setPos(event, c1p2);
                }

                if (JudgeMan.judgeCircleArea(src, c1p3, 30)) {
                    setPos(event, c1p3);
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
     *
     * @param event 事件
     * @param p     点位
     */
    private void setPos(MotionEvent event, Point p) {
        p.x = (int) event.getX() - mCoo.x;
        p.y = (int) event.getY() - mCoo.y;
    }

    private void helpView(Canvas canvas) {
        mHelpPint.setStrokeWidth(20);
        HelpDraw.drawPos(canvas, mHelpPint, c1p0, c1p1, c1p2, c1p3);
//        HelpDraw.drawPos(canvas, mHelpPint, ctrlPos);
        mHelpPint.setStrokeWidth(2);
        HelpDraw.drawLines(canvas, mHelpPint, c1p0, c1p1, c1p2, c1p3);
//        mHelpPint.setPathEffect(null);
//        mHelpPint.setStyle(Paint.Style.FILL);
//        canvas.drawText("起始点p0:" + p0.toString(), 700, -300, mHelpPint);
//        canvas.drawText("控制点p1:" + p1.toString(), 700, -240, mHelpPint);
//        canvas.drawText("控制点p2:" + p2.toString(), 700, -180, mHelpPint);
//        canvas.drawText("终止点p3:" + p3.toString(), 700, -120, mHelpPint);
    }


}
