package cn.bingoogolapple.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/8/26 17:45
 * 描述:
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = GameView.class.getSimpleName();
    private RenderThread mRenderThread;
    private SurfaceHolder mSurfaceHolder;

    private static final int MAP_WIDTH_COUNT = 20;
    private static final int MAP_HEIGHT_COUNT = 30;
    private int mStartX;
    private int mStartY;
    private Paint mPaint;
    private int mSnakeWidth;
    private Point mFoodPoint;
    private boolean mIsRunning = false;
    private Random mRandom = new Random();
    private List<Point> mSnakePoints;
    private Direction mDirection = Direction.DOWN;
    private int mFoodColor = Color.parseColor("#F91852");
    private int mSnakeColor = Color.parseColor("#4620C6");
    private int mMapColor = Color.parseColor("#BBFA6C");
    private int mGameAreaColor = Color.parseColor("#B6753D");
    private GameViewDelegate mDelegate;

    private Handler mMoveHandler;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        mPaint = new Paint();
        mPaint.setStrokeWidth(4);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int whiteSpaceCount = 4;
        if (w / (MAP_WIDTH_COUNT + whiteSpaceCount) < h / (MAP_HEIGHT_COUNT + whiteSpaceCount)) {
            mSnakeWidth = w / (MAP_WIDTH_COUNT + whiteSpaceCount);
        } else {
            mSnakeWidth = h / (MAP_HEIGHT_COUNT + whiteSpaceCount);
        }
        mStartX = mSnakeWidth * whiteSpaceCount / 2;
        mStartY = mSnakeWidth * whiteSpaceCount / 2;
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

        lockCanvas.drawColor(mMapColor);
        drawGameArea(lockCanvas);
        if (mFoodPoint != null && mSnakePoints != null) {
            drawFood(lockCanvas);
            drawSnake(lockCanvas);
        }

        mSurfaceHolder.unlockCanvasAndPost(lockCanvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");

        restart();
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


    private void drawGameArea(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mGameAreaColor);
        canvas.drawRect(mStartX, mStartY, mStartX + mSnakeWidth * MAP_WIDTH_COUNT, mStartY + mSnakeWidth * MAP_HEIGHT_COUNT, mPaint);
    }

    private void drawFood(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mFoodColor);
        canvas.drawRect(mFoodPoint.x * mSnakeWidth + mStartX, mFoodPoint.y * mSnakeWidth + mStartY, mFoodPoint.x * mSnakeWidth + mStartX + mSnakeWidth, mFoodPoint.y * mSnakeWidth + mStartY + mSnakeWidth, mPaint);
    }

    private void drawSnake(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mSnakeColor);
        Point snake;
        for (int i = 0; i < mSnakePoints.size(); i++) {
            snake = mSnakePoints.get(i);
            int left = snake.x * mSnakeWidth + mStartX;
            int top = snake.y * mSnakeWidth + mStartY;
            int right = snake.x * mSnakeWidth + mStartX + mSnakeWidth;
            int bottom = snake.y * mSnakeWidth + mStartY + mSnakeWidth;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }

    private void eat() {
        if (mSnakePoints.get(0).x == mFoodPoint.x && mSnakePoints.get(0).y == mFoodPoint.y) {
            mSnakePoints.add(new Point(mSnakePoints.get(mSnakePoints.size() - 1).x, mSnakePoints.get(mSnakePoints.size() - 1).y));
            mFoodPoint = getRandomPoint();
        }
    }

    private void move(int x, int y) {
        if (isAlive(x, y)) {
            otherMove();
            mSnakePoints.get(0).x = mSnakePoints.get(0).x + x;
            mSnakePoints.get(0).y = mSnakePoints.get(0).y + y;
            eat();
        } else {
            if (mDelegate != null) {
                mDelegate.onGameOver();
            }
            mIsRunning = false;
        }
    }

    public boolean isAlive(int x, int y) {
        Point headPoint = mSnakePoints.get(0);
        if ((headPoint.x + x) < 0 || (headPoint.x + x) > MAP_WIDTH_COUNT - 1 || (headPoint.y + y) < 0 || (headPoint.y + y) > MAP_HEIGHT_COUNT - 1) {
            return false;
        }

        // 大于等于5个点时才会和自身相撞
        if (mSnakePoints.size() >= 5) {
            // 第5个点会移到第4个点，也就是i等于3开始判断是否会和自身相撞
            for (int i = 3; i < mSnakePoints.size() - 1; i++) {
                if (headPoint.x + x == mSnakePoints.get(i).x && headPoint.y + y == mSnakePoints.get(i).y) {
                    return false;
                }
            }
        }

        return true;
    }

    private void otherMove() {
        for (int i = 0; i < mSnakePoints.size(); i++) {
            if (i == 1) {
                mSnakePoints.get(i).x = mSnakePoints.get(0).x;
                mSnakePoints.get(i).y = mSnakePoints.get(0).y;
            } else if (i > 1) {
                Point body = mSnakePoints.get(i - 1);
                mSnakePoints.set(i - 1, mSnakePoints.get(i));
                mSnakePoints.set(i, body);
            }
        }
    }

    public void changeDirection(Direction direction) {
        if (mDirection != direction) {
            switch (direction) {
                case LEFT:
                    if (mDirection != Direction.RIGHT) {
                        mDirection = direction;
                        left();
                    }
                    break;
                case UP:
                    if (mDirection != Direction.DOWN) {
                        mDirection = direction;
                        up();
                    }
                    break;
                case RIGHT:
                    if (mDirection != Direction.LEFT) {
                        mDirection = direction;
                        right();
                    }
                    break;
                case DOWN:
                    if (mDirection != Direction.UP) {
                        mDirection = direction;
                        down();
                    }
                    break;
            }
            postInvalidate();
        }
    }

    private Point getRandomPoint() {
        return new Point(mRandom.nextInt(MAP_WIDTH_COUNT - 1), mRandom.nextInt(MAP_HEIGHT_COUNT - 1));
    }

    public void restart() {
        mIsRunning = true;

        mFoodPoint = getRandomPoint();
        if (mSnakePoints == null) {
            mSnakePoints = new ArrayList<>();
        } else {
            mSnakePoints.clear();
        }
        mSnakePoints.add(new Point(getRandomPoint()));

        if (mRenderThread != null) {
            mRenderThread.interrupt();
        }
        mRenderThread = new RenderThread();
        mRenderThread.start();

        if (mMoveHandler == null) {
            mMoveHandler = new Handler();
        } else {
            mMoveHandler.removeCallbacks(mMoveRunnable);
        }
        mMoveHandler.postDelayed(mMoveRunnable, 1000);
    }

    private Runnable mMoveRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                switch (mDirection) {
                    case LEFT:
                        left();
                        break;
                    case UP:
                        up();
                        break;
                    case RIGHT:
                        right();
                        break;
                    case DOWN:
                        down();
                        break;
                }
                if (mIsRunning) {
                    mMoveHandler.postDelayed(mMoveRunnable, 1000);
                }
            }
        }
    };

    private void left() {
        move(-1, 0);
    }

    private void up() {
        move(0, -1);
    }

    private void right() {
        move(1, 0);
    }

    private void down() {
        move(0, 1);
    }

    public enum Direction {
        LEFT, UP, RIGHT, DOWN
    }

    public void setDelegate(GameViewDelegate delegate) {
        mDelegate = delegate;
    }

    public interface GameViewDelegate {
        void onGameOver();
    }

}