package cn.bingoogolapple.agn.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/8/7 17:35
 * 描述:
 */
public class GameUI extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = GameUI.class.getSimpleName();
    private RenderThread mRenderThread;
    private boolean mIsRunning;
    private SurfaceHolder mSurfaceHolder;
    private Man mMan;
    private List<Face> mFaces;
    private MyButton mButton;

    public GameUI(Context context) {
        super(context);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    private class RenderThread extends Thread {
        @Override
        public void run() {
            while (mIsRunning) {
                try {
                    long startTime = System.currentTimeMillis();
                    drawUI();
                    long endTime = System.currentTimeMillis();
                    long dTime = endTime - startTime;
                    int fps = (int) (1000 / dTime);
//                    Log.i(TAG, "fps = " + fps);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void drawUI() {
        Canvas lockCanvas = mSurfaceHolder.lockCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        lockCanvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        mMan.drawSelf(lockCanvas);
        mButton.drawSelf(lockCanvas);
        for (Face face : mFaces) {
            face.drawSelf(lockCanvas);
            face.move();

            if (face.mPostion.x < 0 || face.mPostion.x > getWidth() || face.mPostion.y < 0 || face.mPostion.y > getHeight()) {
                mFaces.remove(face);
            }
        }
        mSurfaceHolder.unlockCanvasAndPost(lockCanvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        mMan = new Man(BitmapFactory.decodeResource(getResources(), R.mipmap.avatar_boy), new Point(0, 0));
        mFaces = new CopyOnWriteArrayList<>();
        Bitmap defautBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_default);
        Bitmap pressedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bottom_press);
        mButton = new MyButton(defautBitmap, new Point((getWidth() - defautBitmap.getWidth()) / 2, getHeight() - defautBitmap.getHeight() - 50), pressedBitmap);
        mButton.setOnClickListener(new MyButton.OnClickListener() {
            @Override
            public void click() {
                mMan.move(Man.DOWN);
            }
        });

        mRenderThread = new RenderThread();
        mIsRunning = true;
        mRenderThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        mIsRunning = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
    }

    public void handleTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                if (mButton.isClick(new Point(x, y))) {
                    mButton.click();
                } else {
                    Face face = mMan.createFace(getContext(), new Point(x, y));
                    mFaces.add(face);
                }
                break;
            case MotionEvent.ACTION_UP:
                mButton.setIsClick(false);
                break;
            default:
                break;
        }
    }
}

/*
1 SurfaceView 展示电影的屏幕

内部双缓冲机制   显示界面的效率非常快   对内存和cpu的开销非常大的(当不可见的时候,回收)

A   加载数据  显示界面
B   显示界面  加载数据
2 SurfaceHolder 展示电影的内容
3 Thread 工作人员
需要满足两个条件 在surfaceCreated之后创建, 在surfaceDestroyed 销毁

如何绘制界面
1.如何获取到Surface
2.SurfaceVIew里面 获取不到Surface 但是能够获取到SurfaceHolder ,看看SurfaceHolder能否获取到Surface
3.拿到Surface以后  需要了解如何去绘制界面
4.由于SurfaceHolder已经具备Surface的功能了 ,所以就可以直接通过SurfaceHolder 去实现了


Surface
lockCanvas  锁定画布 获取到一个画布 绘制界面了
绘制界面
unlockCanvasAndPost 解锁画布提交


SurfaceHolder
里面具备了和Surface一样的方法 也可以锁定画布 解锁画布
其实SurfaceHolder 里面的就是通过Surface实现的


把看到的事物 都封装成一个对象
 */