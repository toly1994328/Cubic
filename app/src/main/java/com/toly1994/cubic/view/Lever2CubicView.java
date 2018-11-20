package com.toly1994.cubic.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.toly1994.cubic.analyze.HelpDraw;

/**
 * 作者：张风捷特烈<br/>
 * 时间：2018/11/16 0016:9:04<br/>
 * 邮箱：1981462002@qq.com<br/>
 * 说明：贝塞尔三次曲线初体验
 */
public class Lever2CubicView extends View {
    private Point mCoo = new Point(500, 500);//坐标系
    private Picture mCooPicture;//坐标系canvas元件
    private Picture mGridPicture;//网格canvas元件
    private Paint mHelpPint;//辅助画笔

    private float r = 300f;
    private static float rate = 0.551915024494f;
//    private float ctrlLen = r * 0.151915024494f;

    private float runNum = 1;

    /**
     * 贝塞尔曲线控制点
     */
    private static final float[][] CIRCLE_ARRAY = {
            //0---第一段线
            {0, rate},//控制点1
            {1 - rate, 1},//控制点2
            {1, 1},//终点

            //1---第二段线
            {1 + rate, 1},//控制点1
            {2, rate},//控制点2
            {2, 0},//终点

            //2---第二段线
            {2, -rate},//控制点1
            {1 + rate, -1},//控制点2
            {1, -1},//终点

            //3---第四段线
            {1 - rate, -1},//控制点1
            {0, -rate},//控制点2
            {0, 0}//终点
    };

    PointF src = new PointF(0, 0);

    PointF ctrlPos = new PointF(400, 0);


    private Paint mPaint;//主画笔
    private Path mPath;//主路径
    private ValueAnimator mAnimator;


    public Lever2CubicView(Context context) {
        this(context, null);
    }

    public Lever2CubicView(Context context, @Nullable AttributeSet attrs) {
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
            runNum = (float) a.getAnimatedValue();
            mPath.reset();
            invalidate();
        });

    }



    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        HelpDraw.draw(canvas, mGridPicture, mCooPicture);
        canvas.save();
        canvas.translate(mCoo.x, mCoo.y);


        mPaint.setStyle(Paint.Style.FILL);
        mPath.lineTo(0, 0);
//        for (int i = 0; i < CIRCLE_ARRAY.length / 3; i++) {
//
//
//            mPath.cubicTo(
//                    r * (i == 0 ? runNum : 1) * CIRCLE_ARRAY[3 * i][0],
//                    r * (i == 0 ? runNum : 1) * CIRCLE_ARRAY[3 * i][1],
//
//
//                    r * (i == 0 ? runNum : 1) * CIRCLE_ARRAY[3 * i + 1][0],
//                    r * (i == 0 ? runNum : 1) * CIRCLE_ARRAY[3 * i + 1][1],
//
//                    r * CIRCLE_ARRAY[3 * i + 2][0], r * CIRCLE_ARRAY[3 * i + 2][1]);
//        }


        mPath.cubicTo(//第一段
                r * CIRCLE_ARRAY[0][0], r * CIRCLE_ARRAY[0][1],
                r * CIRCLE_ARRAY[1][0] - (1 - runNum) * 4f * r, r * CIRCLE_ARRAY[1][1],
                r * CIRCLE_ARRAY[2][0], r * CIRCLE_ARRAY[2][1]);

        mPath.cubicTo(//第二段
                r * CIRCLE_ARRAY[3][0]+ (1 - runNum) * 4f * r, r * CIRCLE_ARRAY[3][1],
                r * CIRCLE_ARRAY[4][0], r * CIRCLE_ARRAY[4][1],
                r * CIRCLE_ARRAY[5][0], r * CIRCLE_ARRAY[5][1]);

        mPath.cubicTo(//第三段
                r * CIRCLE_ARRAY[6][0], r * CIRCLE_ARRAY[6][1],
                r * CIRCLE_ARRAY[7][0], r * CIRCLE_ARRAY[7][1],
                r * CIRCLE_ARRAY[8][0], r * CIRCLE_ARRAY[8][1]);

        mPath.cubicTo(//第四段
                r * CIRCLE_ARRAY[9][0], r * CIRCLE_ARRAY[9][1],
                r * CIRCLE_ARRAY[10][0], r * CIRCLE_ARRAY[10][1],
                r * CIRCLE_ARRAY[11][0], r * CIRCLE_ARRAY[11][1]);

        canvas.drawPath(mPath, mPaint);
        helpView(canvas);
        canvas.restore();
    }

    private void reflectY(PointF p0, PointF p1, PointF p2, PointF p3, Path path) {
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
//                if (JudgeMan.judgeCircleArea(src, c1p0, 30)) {
//                    setPos(event, c1p0);
//                }
//                if (JudgeMan.judgeCircleArea(src, c1p1, 30)) {
//                    setPos(event, c1p1);
//                }
//
//                if (JudgeMan.judgeCircleArea(src, c1p2, 30)) {
//                    setPos(event, c1p2);
//                }
//
//                if (JudgeMan.judgeCircleArea(src, c1p3, 30)) {
//                    setPos(event, c1p3);
//                }
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
    private void setPos(MotionEvent event, PointF p) {
        p.x = (int) event.getX() - mCoo.x;
        p.y = (int) event.getY() - mCoo.y;
    }

    private void helpView(Canvas canvas) {
        mHelpPint.setStrokeWidth(20);


        PointF pointF0 = new PointF(0, 0);
        PointF pointF1 = new PointF(r * CIRCLE_ARRAY[0][0], r * CIRCLE_ARRAY[0][1]);
        PointF pointF2 = new PointF(r * CIRCLE_ARRAY[1][0]- (1 - runNum) * 4f * r, r * CIRCLE_ARRAY[1][1]);
        PointF pointF3 = new PointF(r * CIRCLE_ARRAY[2][0], r * CIRCLE_ARRAY[2][1] );

        HelpDraw.drawPos(canvas, mHelpPint, pointF0, pointF1, pointF2, pointF3);

        PointF pointF4 = new PointF(r * CIRCLE_ARRAY[3][0]+ (1 - runNum) * 4f * r, r * CIRCLE_ARRAY[3][1]);
        PointF pointF5 = new PointF(r * CIRCLE_ARRAY[4][0], r * CIRCLE_ARRAY[4][1]);
        PointF pointF6 = new PointF(r * CIRCLE_ARRAY[5][0], r * CIRCLE_ARRAY[5][1]);
        HelpDraw.drawPos(canvas, mHelpPint, pointF4, pointF5, pointF6);


        PointF pointF7 = new PointF(r * CIRCLE_ARRAY[6][0], r * CIRCLE_ARRAY[6][1]);
        PointF pointF8 = new PointF(r * CIRCLE_ARRAY[7][0], r * CIRCLE_ARRAY[7][1]);
        PointF pointF9 = new PointF(r * CIRCLE_ARRAY[8][0], r * CIRCLE_ARRAY[8][1]);
        HelpDraw.drawPos(canvas, mHelpPint, pointF7, pointF8, pointF9);

        PointF pointF10 = new PointF(r * CIRCLE_ARRAY[9][0], r * CIRCLE_ARRAY[9][1]);
        PointF pointF11 = new PointF(r * CIRCLE_ARRAY[10][0], r * CIRCLE_ARRAY[10][1]);
        PointF pointF12 = new PointF(r * CIRCLE_ARRAY[11][0], r * CIRCLE_ARRAY[11][1]);
        HelpDraw.drawPos(canvas, mHelpPint, pointF10, pointF11, pointF12);


        mHelpPint.setStrokeWidth(2);
        HelpDraw.drawLines(canvas, mHelpPint,
                pointF0, pointF1, pointF2, pointF3,
                pointF3, pointF4, pointF5, pointF6,
                pointF6, pointF7, pointF8, pointF9,
                pointF9, pointF10, pointF11, pointF12
        );

//        mPath.cubicTo(
//                r * CIRCLE_ARRAY[3][0], r * CIRCLE_ARRAY[3][1],
//                r * CIRCLE_ARRAY[4][0], r * CIRCLE_ARRAY[4][1],
//                r * CIRCLE_ARRAY[5][0], r * CIRCLE_ARRAY[5][1]);
//
//        mPath.cubicTo(
//                r * CIRCLE_ARRAY[6][0], r * CIRCLE_ARRAY[6][1],
//                r * CIRCLE_ARRAY[7][0], r * CIRCLE_ARRAY[7][1],
//                r * CIRCLE_ARRAY[8][0], r * CIRCLE_ARRAY[8][1]);
//
//        mPath.cubicTo(
//                r * CIRCLE_ARRAY[9][0], r * CIRCLE_ARRAY[9][1],
//                r * CIRCLE_ARRAY[10][0], r * CIRCLE_ARRAY[10][1],
//                r * CIRCLE_ARRAY[11][0], r * CIRCLE_ARRAY[11][1]);
//
//
//        for (int i = 0; i < CIRCLE_ARRAY.length - 1; i++) {
//
//            PointF pointF = new PointF(r * CIRCLE_ARRAY[i][0], r * CIRCLE_ARRAY[i][1]);
//            PointF pointF2 = new PointF(r * CIRCLE_ARRAY[i + 1][0], r * CIRCLE_ARRAY[i + 1][1]);


//        }


//        mHelpPint.setStrokeWidth(20);
//        HelpDraw.drawPos(canvas, mHelpPint, c1p0, c1p1, c1p2, c1p3);
//        HelpDraw.drawPos(canvas, mHelpPint, c1p3, c2p1, c2p2, c2p3);
//        HelpDraw.drawPos(canvas, mHelpPint, c2p3, c3p1, c3p2, c3p3);
//        HelpDraw.drawPos(canvas, mHelpPint, c3p3, c4p1, c4p2, c4p3);
////        HelpDraw.drawPos(canvas, mHelpPint, ctrlPos);
//        mHelpPint.setStrokeWidth(2);
//        HelpDraw.drawLines(canvas, mHelpPint, c1p0, c1p1, c1p2, c1p3);
//        HelpDraw.drawLines(canvas, mHelpPint, c1p3, c2p1, c2p2, c2p3);
//        HelpDraw.drawLines(canvas, mHelpPint, c2p3, c3p1, c3p2, c3p3);
//        HelpDraw.drawLines(canvas, mHelpPint, c3p3, c4p1, c4p2, c4p3);
//        mHelpPint.setPathEffect(null);
//        mHelpPint.setStyle(Paint.Style.FILL);
//        canvas.drawText("起始点p0:" + p0.toString(), 700, -300, mHelpPint);
//        canvas.drawText("控制点p1:" + p1.toString(), 700, -240, mHelpPint);
//        canvas.drawText("控制点p2:" + p2.toString(), 700, -180, mHelpPint);
//        canvas.drawText("终止点p3:" + p3.toString(), 700, -120, mHelpPint);
    }


}
