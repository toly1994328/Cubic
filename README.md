#### 零、前言

>1.可以说贝塞尔曲线是一把 "石中剑"，能够拔出它，会让你的绘图如虎添翼。  
2.今天要与贝塞尔曲线大战三百回合，将它加入我的绘图大军麾下。  
3.自此Android绘图五虎将:`Canvas，Path，Paint，Color，贝塞尔`便集结完成。   
4.本项目源码见文尾`捷文规范`第一条,视图源码在`view包`，分析工具在`analyze包`

---

#### 一、贝塞尔三次曲线初体验

##### 1.`无网格，不曲线`,废话不多说，上网格+坐标系

```
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
        mPaint.setStrokeWidth(5);
        //初始化主路径
        mPath = new Path();
        
        //初始化辅助
        mHelpPint = HelpDraw.getHelpPint(Color.RED);
        mCooPicture = HelpDraw.getCoo(getContext(), mCoo);
        mGridPicture = HelpDraw.getGrid(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mCoo.x, mCoo.y);
        //TODO ----drawSomething
        canvas.restore();
        HelpDraw.draw(canvas, mGridPicture, mCooPicture);
    }
}
```

##### 2.分析一段三次贝塞尔
>一段三次贝塞尔曲线是由四个点控制的，四个点分别是干嘛的，且看分析：

```
//准备成员变量---四个点
Point p0 = new Point(0, 0);
Point p1 = new Point(200, 200);
Point p2 = new Point(300, -100);
Point p3 = new Point(500, 300);

//onDraw中：
mPath.moveTo(p0.x, p0.y);
mPath.cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
canvas.drawPath(mPath, mPaint);
```

![结果1.png](https://upload-images.jianshu.io/upload_images/9414344-fd30e5f73fa75df6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>也许这样看不出什么关系：现在把四个控制点也画出来(红色)：

```
mHelpPint.setStrokeWidth(10);
HelpDraw.drawPos(canvas, mHelpPint, p0, p1, p2, p3);
```

![结果2.png](https://upload-images.jianshu.io/upload_images/9414344-14d93d59a4ee37fc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>是不是有点意思了--在加两条线：

```
mHelpPint.setStrokeWidth(2);
HelpDraw.drawLines(canvas, mHelpPint, p0, p1, p2, p3);
```

![结果3.png](https://upload-images.jianshu.io/upload_images/9414344-6b2b97de76a96cc5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>小结：`p0:第一点`，`p3：最终点`，`p1:控制点1`，`p2:控制点2`


---
####  二、动态效果：任意一段三次贝塞尔曲线的最优雅实现形式
>以前看过别人的任意一段三次贝塞尔曲线，感觉体验太差,切换个点还要点按钮，  
下面我实现四个点任意拖动的三次贝塞尔曲线，可谓是非常优雅的，让你明白点域的判断  

![三次贝塞尔测试.gif](https://upload-images.jianshu.io/upload_images/9414344-7fa3475d79b2a1ef.gif?imageMogr2/auto-orient/strip)

##### 1.判断一个点是否在一个圆形区域

```
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
/**
 * 两点间距离函数
 */
public static float disPos2d(float x1, float y1, float x2, float y2) {
    return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
}
```

##### 2.触摸事件动态改变点位：

```
//添加成员变量
Point src = new Point(0, 0);

@Override
public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            src.x = (int) event.getX() - mCoo.x;
            src.y = (int) event.getY() - mCoo.y;
            break;
        case MotionEvent.ACTION_MOVE:
            if (judgeCircleArea(src, p0, 30)) {
                setPos(event, p0);
            }
            if (judgeCircleArea(src, p1, 30)) {
                setPos(event, p1);
            }
            if (judgeCircleArea(src, p2, 30)) {
                setPos(event, p2);
            }
            if (judgeCircleArea(src, p3, 30)) {
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
```

>好了，这样就行了，是不是一种还没开始就结束的感觉。


---
#### 三、贝塞尔曲线实战1：(初级：运动)

##### 1.镜像：
>先选取感觉满意的半边，记录四个点位：

![左半.png](https://upload-images.jianshu.io/upload_images/9414344-2d614186c02f3d8b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


```
Point c1p0 = new Point(0, 0);
Point c1p1 = new Point(300, 0);
Point c1p2 = new Point(150, -200);
Point c1p3 = new Point(300, -200);
```

##### 2.如何实现下面的效果呢?


![贝塞尔单段镜像.gif](https://upload-images.jianshu.io/upload_images/9414344-9828ca02e82c8375.gif?imageMogr2/auto-orient/strip)

>在原来的基础上在画一段贝塞尔曲线，要求：新控制点1(记为：c2p1)和c1p2关于c1p3.x对称  
点关于竖线对称的原理：`(c2p1.x+c1p2.x)/2 = c1p3.x` `c2p1.y = c1p2.y`,转换一下：`c2p1.x=c1p3.x*2-c1p2.x`  
新控制点2(记为：c2p2)和c1p1关于对称c1p3.x以及新结尾点(记为：c2p3)和c1p0关于c1p3.x对称即可

```
private void reflectY( Point p0, Point p1, Point p2, Point p3, Path path) {
    path.cubicTo(p3.x * 2 - p2.x, p2.y, p3.x * 2 - p1.x, p1.y, p3.x * 2 - p0.x, p0.y);
}
```

##### 3.凸出来的一块慢慢变平的动画
>想象一下，只需要才c1p2和c1p3一起向下移动就行了，要运动，二话不说，ValueAnimator走起  
好吧，有点像做俯卧撑,实现起来也挺简单的：

![动态修改.gif](https://upload-images.jianshu.io/upload_images/9414344-5c01484c7ab91678.gif?imageMogr2/auto-orient/strip)

```
//数字时间流
mAnimator = ValueAnimator.ofFloat(1, 0);
mAnimator.setDuration(2000);
mAnimator.setRepeatMode(ValueAnimator.REVERSE);
mAnimator.setRepeatCount(-1);
mAnimator.addUpdateListener(a -> {
    float rate = (float) a.getAnimatedValue();
    c1p2.y = -(int) (rate * 200);
    c1p3.y = -(int) (rate * 200);
    mPath.reset();
    invalidate();
});
```


#### 4.随便玩玩
>源码在文尾,文件是`Lever1CubicView.java`,大家可以下载，运行自己玩玩，加深一下对贝塞尔三次曲线的感觉  

![随便玩玩.gif](https://upload-images.jianshu.io/upload_images/9414344-40af10a47d3a05a3.gif?imageMogr2/auto-orient/strip)
>好了，开胃菜结束了，下面进入正餐，你没看错，好戏才刚刚开始。
---

#### 四、高阶：三阶贝塞尔的优雅使用：
>`注意：`前方高能，非战斗人员请尽快准备瓜子，饮料，花生米...

##### 1.三阶贝塞尔画圆：
>看下图，你可能会满脸不屑地说："切,我用canvas分分秒描画你信不信?"  
老大，我信...且往下看

![圆.png](https://upload-images.jianshu.io/upload_images/9414344-5cf4d2f6a80e30f9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


##### 2.如何优雅地绘制多条贝塞尔曲线
>下面是四条贝塞尔曲线绘制的圆，看图就知道优势在于任意改变形状  
但如果把点位都放在mPath.cubicTo()里，多几条线就乱成一锅粥了,最好统一管理一下  
第一个想到的是每条线的三个点都抽成三个成员变量,不过还是很难维护，这个问题一直困扰我  
今天突然想到二维数组不是挺好吗?二维每个里面两个点。

![圆分析.png](https://upload-images.jianshu.io/upload_images/9414344-46940af50d0fcd7f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```
//单位圆(即半径为1)控制线长
private static float rate = 0.551915024494f;
/**
 * 单位圆(即半径为1)的贝塞尔曲线点位
 */
private static final float[][] CIRCLE_ARRAY = {
        //0---第一段线
        {-1, rate},//控制点1
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
```

##### 2.绘制循环一下就行了
>看网上一些绘制方法，点都很乱，看着费劲也晦涩。  

```
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.save();
    canvas.translate(mCoo.x, mCoo.y);

    mPaint.setStyle(Paint.Style.STROKE);
    mPath.lineTo(0, 0);
    for (int i = 0; i < CIRCLE_ARRAY.length / 3; i++) {
        mPath.cubicTo(
                r * CIRCLE_ARRAY[3*i][0], r * CIRCLE_ARRAY[3*i][1],
                r * CIRCLE_ARRAY[3*i + 1][0], r * CIRCLE_ARRAY[3*i + 1][1],
                r * CIRCLE_ARRAY[3*i + 2][0], r * CIRCLE_ARRAY[3*i + 2][1]);
    }
    canvas.drawPath(mPath, mPaint);
    canvas.restore();
}
```

##### 3.既然能控制，那来玩玩呗
>让它变形倒不是什么难事，关键是为了明显些添加辅助点线真是要命,总算是完美展现给大家了

![圆的形变.gif](https://upload-images.jianshu.io/upload_images/9414344-cc8236deabb8f00c.gif?imageMogr2/auto-orient/strip)


```
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

//绘制时动态改变

for (int i = 0; i < CIRCLE_ARRAY.length / 3; i++) {
    mPath.cubicTo(
            r * runNum * CIRCLE_ARRAY[3 * i][0], r * runNum * CIRCLE_ARRAY[3 * i][1],
            r * runNum * CIRCLE_ARRAY[3 * i + 1][0], r * runNum * CIRCLE_ARRAY[3 * i + 1][1],
            r * CIRCLE_ARRAY[3 * i + 2][0], r * CIRCLE_ARRAY[3 * i + 2][1]);
}
```

##### 4.爱心---刚才是瞎玩的，现在要认真了：
>只要控制第三段线的尾部，向下移的话，你应该能想到什么吧

![心形.gif](https://upload-images.jianshu.io/upload_images/9414344-d98e02a018a8d3d1.gif?imageMogr2/auto-orient/strip)


```
mPath.cubicTo(//第一段
        r * CIRCLE_ARRAY[0][0], r * CIRCLE_ARRAY[0][1],
        r * CIRCLE_ARRAY[1][0], r * CIRCLE_ARRAY[1][1],
        r * CIRCLE_ARRAY[2][0], r * CIRCLE_ARRAY[2][1]);
mPath.cubicTo(//第二段
        r * CIRCLE_ARRAY[3][0], r * CIRCLE_ARRAY[3][1],
        r * CIRCLE_ARRAY[4][0], r * CIRCLE_ARRAY[4][1],
        r * CIRCLE_ARRAY[5][0], r * CIRCLE_ARRAY[5][1]);
mPath.cubicTo(//第三段
        r * CIRCLE_ARRAY[6][0], r * CIRCLE_ARRAY[6][1],
        r * CIRCLE_ARRAY[7][0], r * CIRCLE_ARRAY[7][1],
        r * CIRCLE_ARRAY[8][0], r * (runNum) * CIRCLE_ARRAY[8][1]);//<----动我试试
mPath.cubicTo(//第四段
        r * CIRCLE_ARRAY[9][0], r * CIRCLE_ARRAY[9][1],
        r * CIRCLE_ARRAY[10][0], r * CIRCLE_ARRAY[10][1],
        r * CIRCLE_ARRAY[11][0], r * CIRCLE_ARRAY[11][1]);
```

>你也许会说好胖啊，瘦一点可以不  
将第一段的控制点2和第二段的控制点1往上移动一点  
一共就这么九个主要点位，任你摆弄,你get到了吗?

![心形优化.gif](https://upload-images.jianshu.io/upload_images/9414344-015c13b2dbe4e83c.gif?imageMogr2/auto-orient/strip)


```
mPath.cubicTo(//第一段
        r * CIRCLE_ARRAY[0][0], r * CIRCLE_ARRAY[0][1],
        r * CIRCLE_ARRAY[1][0], r * CIRCLE_ARRAY[1][1] - ((1 - runNum) * 0.3f) * r,
        r * CIRCLE_ARRAY[2][0], r * CIRCLE_ARRAY[2][1]);
mPath.cubicTo(//第二段
        r * CIRCLE_ARRAY[3][0], r * CIRCLE_ARRAY[3][1] - ((1 - runNum) * 0.3f) * r,
        r * CIRCLE_ARRAY[4][0], r * CIRCLE_ARRAY[4][1],
        r * CIRCLE_ARRAY[5][0], r * CIRCLE_ARRAY[5][1]);
mPath.cubicTo(//第三段
        r * CIRCLE_ARRAY[6][0], r * CIRCLE_ARRAY[6][1],
        r * CIRCLE_ARRAY[7][0], r * CIRCLE_ARRAY[7][1],
        r * CIRCLE_ARRAY[8][0], r * CIRCLE_ARRAY[8][1] + ((1 - runNum) * 0.6f) * r);
mPath.cubicTo(//第四段
        r * CIRCLE_ARRAY[9][0], r * CIRCLE_ARRAY[9][1],
        r * CIRCLE_ARRAY[10][0], r * CIRCLE_ARRAY[10][1],
        r * CIRCLE_ARRAY[11][0], r * CIRCLE_ARRAY[11][1]);
```


---
##### 4.想变扁/宽怎么办?
>下侧三个点一起平移

![三点下移.gif](https://upload-images.jianshu.io/upload_images/9414344-b8fa7f49885cf5bf.gif?imageMogr2/auto-orient/strip)

```
mPath.cubicTo(//第一段
        r * CIRCLE_ARRAY[0][0], r * CIRCLE_ARRAY[0][1],
        r * CIRCLE_ARRAY[1][0], r * CIRCLE_ARRAY[1][1]+ (1 - runNum) * 0.6f * r,
        r * CIRCLE_ARRAY[2][0], r * CIRCLE_ARRAY[2][1]+ (1 - runNum) * 0.6f * r);
mPath.cubicTo(//第二段
        r * CIRCLE_ARRAY[3][0], r * CIRCLE_ARRAY[3][1]+ (1 - runNum) * 0.6f * r,
        r * CIRCLE_ARRAY[4][0], r * CIRCLE_ARRAY[4][1],
        r * CIRCLE_ARRAY[5][0], r * CIRCLE_ARRAY[5][1]);
mPath.cubicTo(//第三段
        r * CIRCLE_ARRAY[6][0], r * CIRCLE_ARRAY[6][1] ,
        r * CIRCLE_ARRAY[7][0], r * CIRCLE_ARRAY[7][1] ,
        r * CIRCLE_ARRAY[8][0], r * CIRCLE_ARRAY[8][1]) ;
mPath.cubicTo(//第四段
        r * CIRCLE_ARRAY[9][0], r * CIRCLE_ARRAY[9][1],
        r * CIRCLE_ARRAY[10][0], r * CIRCLE_ARRAY[10][1],
        r * CIRCLE_ARRAY[11][0], r * CIRCLE_ARRAY[11][1]);
```

>再让下面变尖一点呢

![三点下移尖底.gif](https://upload-images.jianshu.io/upload_images/9414344-eb70568e35152afd.gif?imageMogr2/auto-orient/strip)


```
mPath.cubicTo(//第一段
        r * CIRCLE_ARRAY[0][0], r * CIRCLE_ARRAY[0][1],
        r * CIRCLE_ARRAY[1][0], r * CIRCLE_ARRAY[1][1]+ (1 - runNum) * 0.6f * r
                - ((1 - runNum) * 0.3f) * r,
        r * CIRCLE_ARRAY[2][0], r * CIRCLE_ARRAY[2][1]+ (1 - runNum) * 0.6f * r);
mPath.cubicTo(//第二段
        r * CIRCLE_ARRAY[3][0], r * CIRCLE_ARRAY[3][1]+ (1 - runNum) * 0.6f * r
                - ((1 - runNum) * 0.3f) * r,
        r * CIRCLE_ARRAY[4][0], r * CIRCLE_ARRAY[4][1],
        r * CIRCLE_ARRAY[5][0], r * CIRCLE_ARRAY[5][1]);
mPath.cubicTo(//第三段
        r * CIRCLE_ARRAY[6][0], r * CIRCLE_ARRAY[6][1] ,
        r * CIRCLE_ARRAY[7][0], r * CIRCLE_ARRAY[7][1] ,
        r * CIRCLE_ARRAY[8][0], r * CIRCLE_ARRAY[8][1]) ;
mPath.cubicTo(//第四段
        r * CIRCLE_ARRAY[9][0], r * CIRCLE_ARRAY[9][1],
        r * CIRCLE_ARRAY[10][0], r * CIRCLE_ARRAY[10][1],
        r * CIRCLE_ARRAY[11][0], r * CIRCLE_ARRAY[11][1]);
```

##### 5.控制点长度变化:UFO的由来...
>改变坐标，将线1控制点2和线2的控制点1加长

![加长控制点.gif](https://upload-images.jianshu.io/upload_images/9414344-81d1a7f8c7cec2db.gif?imageMogr2/auto-orient/strip)


```
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
```

###### 6.触摸事件小试
>当然你也可以不用ValueAnimate，用触摸事件来控制这些点也是相同的道理。

![触摸事件.gif](https://upload-images.jianshu.io/upload_images/9414344-a5ba9129d282d7b2.gif?imageMogr2/auto-orient/strip)

```
mPath.cubicTo(//第一段
        r * CIRCLE_ARRAY[0][0], r * CIRCLE_ARRAY[0][1],
        r * CIRCLE_ARRAY[1][0], r * CIRCLE_ARRAY[1][1],
        r * CIRCLE_ARRAY[2][0], r * CIRCLE_ARRAY[2][1]);
mPath.cubicTo(//第二段
        r * CIRCLE_ARRAY[3][0], r * CIRCLE_ARRAY[3][1],
        r * CIRCLE_ARRAY[4][0], r * CIRCLE_ARRAY[4][1],
        r * CIRCLE_ARRAY[5][0] + src.x - 2*r, r * CIRCLE_ARRAY[5][1]+ src.y);
mPath.cubicTo(//第三段
        r * CIRCLE_ARRAY[6][0], r * CIRCLE_ARRAY[6][1],
        r * CIRCLE_ARRAY[7][0], r * CIRCLE_ARRAY[7][1],
        r * CIRCLE_ARRAY[8][0], r * CIRCLE_ARRAY[8][1]);
mPath.cubicTo(//第四段
        r * CIRCLE_ARRAY[9][0], r * CIRCLE_ARRAY[9][1],
        r * CIRCLE_ARRAY[10][0], r * CIRCLE_ARRAY[10][1],
        r * CIRCLE_ARRAY[11][0], r * CIRCLE_ARRAY[11][1]);
```

---



>好了，就演示这么多，你可以把源码拷过去自己玩玩，源码文件`Lever2CubicView.java`      
总结一下，一条贝塞尔曲线关键就是那三个点,能控制住，贝塞尔曲线可就在你股掌之间。  
贝塞尔三次曲线还有很多逆天级别的操作，能力有限，日后有需求再研究吧  
把圆形贝塞尔玩转之后，基本上就能对付了。贝塞尔曲线水很深，只有你想不到，没有它做不到。  


---

#### 后记：捷文规范


##### 2.更多关于我

笔名 | QQ|微信|爱好
---|---|---|---|
张风捷特烈 | 1981462002|zdl1994328|语言
 [我的github](https://github.com/toly1994328)|[我的简书](https://www.jianshu.com/u/e4e52c116681)|[我的掘金](https://juejin.im/user/5b42c0656fb9a04fe727eb37)|[个人网站](http://www.toly1994.com)

##### 3.声明
>1----本文由张风捷特烈原创,转载请注明  
2----欢迎广大编程爱好者共同交流  
3----个人能力有限，如有不正之处欢迎大家批评指证，必定虚心改正   
4----看到这里，我在此感谢你的喜欢与支持

---

![icon_wx_200.png](https://upload-images.jianshu.io/upload_images/9414344-8a0c95a090041a0d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)