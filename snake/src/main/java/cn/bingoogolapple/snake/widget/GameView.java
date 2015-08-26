package cn.bingoogolapple.snake.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bingoogolapple.snake.sprite.Button;
import cn.bingoogolapple.snake.R;
import cn.bingoogolapple.snake.util.UIUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/8/26 17:45
 * 描述:
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = GameView.class.getSimpleName();
    /**
     * 行数
     */
    private static final int ROW_COUNT = 10;
    /**
     * 列数
     */
    private static final int COL_COUNT = 10;
    /**
     * 渲染线程
     */
    private RenderThread mRenderThread;
    private SurfaceHolder mSurfaceHolder;

    private Paint mPaint;
    private Point mFoodPoint;
    private boolean mIsRunning = false;
    private Random mRandom = new Random();
    private List<Point> mSnakePoints;
    private Direction mDirection = Direction.DOWN;
    private int mBgColor = Color.WHITE;
    private int mFoodColor = Color.parseColor("#F91852");
    private int mSnakeColor = Color.parseColor("#4620C6");
    private int mEdgeLineColor = Color.parseColor("#1D971D");
    private int mGameAreaColor = Color.parseColor("#BBFA6C");
    private GameViewDelegate mDelegate;

    private Handler mMoveHandler;
    private int mEdgeSize;
    private int mSnakeSize;

    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;

    private Button mLeftBtn;
    private Button mUpBtn;
    private Button mRightBtn;
    private Button mDownBtn;
    private Button mRestartBtn;

    private int mBtnMargin;

    public GameView(Context context) {
        super(context);

        mEdgeSize = UIUtil.dp2px(getContext(), 5);
        mSnakeSize = UIUtil.dp2px(getContext(), 20);
        mBtnMargin = UIUtil.dp2px(getContext(), 30);

        initSurfaceHolder();
        initPaint();
    }

    private void initSurfaceHolder() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mStartX = (w - mSnakeSize * COL_COUNT) / 2;
        mStartY = mStartX;
        mEndX = mStartX + mSnakeSize * COL_COUNT;
        mEndY = mStartY + mSnakeSize * ROW_COUNT;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");

        initLeftBtn();
        initUpBtn();
        initRightBtn();
        initDownBtn();
        initRestartBtn();

        restart();
    }

    private void initLeftBtn() {
        Bitmap defautBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.left_normal);
        Bitmap pressedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.left_pressed);
        mLeftBtn = new Button(defautBitmap, new Point(mBtnMargin, getHeight() - defautBitmap.getHeight() * 3 - mBtnMargin), pressedBitmap);
        mLeftBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void click() {
                changeDirection(Direction.LEFT);
            }
        });
    }

    private void initUpBtn() {
        Bitmap defautBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.up_normal);
        Bitmap pressedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.up_pressed);
        mUpBtn = new Button(defautBitmap, new Point((getWidth() - defautBitmap.getWidth()) / 2, getHeight() - defautBitmap.getHeight() * 5 - mBtnMargin), pressedBitmap);
        mUpBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void click() {
                changeDirection(Direction.UP);
            }
        });
    }

    private void initRightBtn() {
        Bitmap defautBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.right_normal);
        Bitmap pressedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.right_pressed);
        mRightBtn = new Button(defautBitmap, new Point(getWidth() - defautBitmap.getWidth() - mBtnMargin, getHeight() - defautBitmap.getHeight() * 3 - mBtnMargin), pressedBitmap);
        mRightBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void click() {
                changeDirection(Direction.RIGHT);
            }
        });
    }

    private void initDownBtn() {
        Bitmap defautBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.down_normal);
        Bitmap pressedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.down_pressed);
        mDownBtn = new Button(defautBitmap, new Point((getWidth() - defautBitmap.getWidth()) / 2, getHeight() - defautBitmap.getHeight() - mBtnMargin), pressedBitmap);
        mDownBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void click() {
                changeDirection(Direction.DOWN);
            }
        });
    }

    private void initRestartBtn() {
        Bitmap defautBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.restart_normal);
        Bitmap pressedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.restart_pressed);
        mRestartBtn = new Button(defautBitmap, new Point((getWidth() - defautBitmap.getWidth()) / 2, getHeight() - mBtnMargin - defautBitmap.getHeight() * 3), pressedBitmap);
        mRestartBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void click() {
                restart();
            }
        });
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


    private void eatFood() {
        if (mSnakePoints.get(0).x == mFoodPoint.x && mSnakePoints.get(0).y == mFoodPoint.y) {
            mSnakePoints.add(new Point(mSnakePoints.get(mSnakePoints.size() - 1).x, mSnakePoints.get(mSnakePoints.size() - 1).y));
            mFoodPoint = getRandomPoint();
        }
    }

    private void move(int x, int y) {
        if (isAlive(x, y)) {
            // 移动蛇身
            moveSnakeBody();
            // 移动舌头
            mSnakePoints.get(0).x = mSnakePoints.get(0).x + x;
            mSnakePoints.get(0).y = mSnakePoints.get(0).y + y;
            eatFood();
        } else {
            if (mDelegate != null) {
                mDelegate.onGameOver();
            }
            mIsRunning = false;
        }
    }

    public boolean isAlive(int x, int y) {
        Point headPoint = mSnakePoints.get(0);

        // 判断是否与边缘相撞
        if ((headPoint.x + x) < 0 || (headPoint.x + x) > COL_COUNT - 1 || (headPoint.y + y) < 0 || (headPoint.y + y) > ROW_COUNT - 1) {
            return false;
        }

        // 判断是否与蛇身相撞。大于等于5个点时才会和蛇身相撞
        if (mSnakePoints.size() >= 5) {
            // 第5个点会移到第4个点，也就是i等于3开始判断是否会和蛇身相撞
            for (int i = 3; i < mSnakePoints.size() - 1; i++) {
                if (headPoint.x + x == mSnakePoints.get(i).x && headPoint.y + y == mSnakePoints.get(i).y) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 移动蛇的尾巴
     */
    private void moveSnakeBody() {
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

    /**
     * 获取随机的点。初始化舌头或者食物的位置
     *
     * @return
     */
    private Point getRandomPoint() {
        return new Point(mRandom.nextInt(COL_COUNT - 1), mRandom.nextInt(ROW_COUNT - 1));
    }

    /**
     * 开始游戏，如果已经开始了游戏则重新开始游戏
     */
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

    public void handleTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Point clickPoint = new Point((int) event.getRawX(), (int) event.getRawY());
                if (mLeftBtn.isClick(clickPoint)) {
                    mLeftBtn.click();
                } else if (mUpBtn.isClick(clickPoint)) {
                    mUpBtn.click();
                } else if (mRightBtn.isClick(clickPoint)) {
                    mRightBtn.click();
                } else if (mDownBtn.isClick(clickPoint)) {
                    mDownBtn.click();
                } else if (mRestartBtn.isClick(clickPoint)) {
                    mRestartBtn.click();
                }
                break;
            case MotionEvent.ACTION_UP:
                mLeftBtn.setIsClick(false);
                mUpBtn.setIsClick(false);
                mRightBtn.setIsClick(false);
                mDownBtn.setIsClick(false);
                mRestartBtn.setIsClick(false);
                break;
            default:
                break;
        }
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
        // 绘制整个背景
        lockCanvas.drawColor(mBgColor);
        // 绘制游戏区域边框线
        drawEdgeLine(lockCanvas);
        // 绘制游戏区域背景色
        drawGameArea(lockCanvas);

        mLeftBtn.drawSelf(lockCanvas);
        mUpBtn.drawSelf(lockCanvas);
        mRightBtn.drawSelf(lockCanvas);
        mDownBtn.drawSelf(lockCanvas);
        mRestartBtn.drawSelf(lockCanvas);

        if (mFoodPoint != null && mSnakePoints != null) {
            drawFood(lockCanvas);
            drawSnake(lockCanvas);
        }

        mSurfaceHolder.unlockCanvasAndPost(lockCanvas);
    }

    private void drawEdgeLine(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mEdgeLineColor);
        mPaint.setStrokeWidth(mEdgeSize);
        canvas.drawRect(mStartX - mEdgeSize / 2, mStartY - mEdgeSize / 2, mStartX + mSnakeSize * COL_COUNT + mEdgeSize / 2, mStartY + mSnakeSize * ROW_COUNT + mEdgeSize / 2, mPaint);
    }

    private void drawGameArea(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mGameAreaColor);
        canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
    }

    private void drawFood(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mFoodColor);
        canvas.drawRect(mFoodPoint.x * mSnakeSize + mStartX, mFoodPoint.y * mSnakeSize + mStartY, mFoodPoint.x * mSnakeSize + mStartX + mSnakeSize, mFoodPoint.y * mSnakeSize + mStartY + mSnakeSize, mPaint);
    }

    private void drawSnake(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mSnakeColor);
        Point snake;
        for (int i = 0; i < mSnakePoints.size(); i++) {
            snake = mSnakePoints.get(i);
            int left = snake.x * mSnakeSize + mStartX;
            int top = snake.y * mSnakeSize + mStartY;
            int right = snake.x * mSnakeSize + mStartX + mSnakeSize;
            int bottom = snake.y * mSnakeSize + mStartY + mSnakeSize;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
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
                    mMoveHandler.postDelayed(mMoveRunnable, 700);
                }
            }
        }
    };

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