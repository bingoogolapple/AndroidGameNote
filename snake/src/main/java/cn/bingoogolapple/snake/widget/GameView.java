package cn.bingoogolapple.snake.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bingoogolapple.snake.R;
import cn.bingoogolapple.snake.sprite.Button;
import cn.bingoogolapple.snake.sprite.Food;
import cn.bingoogolapple.snake.sprite.Snake;
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
    private boolean mIsRunning = false;
    private Random mRandom = new Random();
    private List<Snake> mSnakes = new ArrayList<>();
    private Direction mDirection = Direction.DOWN;
    private int mBgColor = Color.WHITE;
    private int mEdgeLineColor = Color.parseColor("#1D971D");
    private int mGameAreaColor = Color.parseColor("#BBFA6C");
    private GameViewDelegate mDelegate;

    private Handler mMoveHandler;
    private int mEdgeLineSize;
    private int mSnakeSize;

    private Rect mGameRect;
    private Rect mGameEdgeLineRect;

    private Button mLeftBtn;
    private Button mUpBtn;
    private Button mRightBtn;
    private Button mDownBtn;
    private Button mRestartBtn;

    private Food mFood;

    public GameView(Context context) {
        super(context);

        mEdgeLineSize = UIUtil.dp2px(getContext(), 5);
        mSnakeSize = BitmapFactory.decodeResource(getResources(), R.mipmap.snake).getWidth();

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
        mPaint.setTextSize(UIUtil.sp2px(getContext(), 24));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");

        initRect();

        initLeftBtn();
        initUpBtn();
        initRightBtn();
        initDownBtn();
        initRestartBtn();

        restart();
    }

    private void initRect() {
        int startX = (getWidth() - mSnakeSize * COL_COUNT) / 2;
        int startY = startX;
        mGameRect = new Rect(startX, startY, startX + mSnakeSize * COL_COUNT, startY + mSnakeSize * ROW_COUNT);

        mGameEdgeLineRect = new Rect(mGameRect.left - mEdgeLineSize / 2, mGameRect.top - mEdgeLineSize / 2, mGameRect.right + mEdgeLineSize / 2, mGameRect.bottom + mEdgeLineSize / 2);
    }

    private void initLeftBtn() {
        Bitmap defautBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.left_normal);
        Bitmap pressedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.left_pressed);
        mLeftBtn = new Button(defautBitmap, new Point((getWidth() - defautBitmap.getWidth()) / 2 - defautBitmap.getWidth() * 2, getHeight() - defautBitmap.getHeight() * 4), pressedBitmap);
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
        mUpBtn = new Button(defautBitmap, new Point((getWidth() - defautBitmap.getWidth()) / 2, getHeight() - defautBitmap.getHeight() * 6), pressedBitmap);
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
        mRightBtn = new Button(defautBitmap, new Point((getWidth() - defautBitmap.getWidth()) / 2 + defautBitmap.getWidth() * 2, getHeight() - defautBitmap.getHeight() * 4), pressedBitmap);
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
        mDownBtn = new Button(defautBitmap, new Point((getWidth() - defautBitmap.getWidth()) / 2, getHeight() - defautBitmap.getHeight() * 2), pressedBitmap);
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
        mRestartBtn = new Button(defautBitmap, new Point((getWidth() - defautBitmap.getWidth()) / 2, getHeight() - defautBitmap.getHeight() * 4), pressedBitmap);
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

    private void move(int x, int y) {
        if (isAlive(x, y)) {
            Point lastPoint;
            Snake firstSnake = mSnakes.get(0);
            if (mSnakes.size() == 1) {
                lastPoint = new Point(firstSnake.getX(), firstSnake.getY());
                firstSnake.moveBy(x, y);
            } else {
                Snake lastSnake = mSnakes.remove(mSnakes.size() - 1);
                lastPoint = new Point(lastSnake.getX(), lastSnake.getY());
                lastSnake.setPosition(new Point(firstSnake.getX() + x, firstSnake.getY() + y));
                mSnakes.add(0, lastSnake);
            }
            eatFood(lastPoint);
        } else {
            if (mDelegate != null) {
                mDelegate.onGameOver();
            }
            mIsRunning = false;
        }
    }

    public boolean isAlive(int x, int y) {
        Snake snake = mSnakes.get(0);
        int firstX = snake.getX() + x;
        int firstY = snake.getY() + y;

        // 判断是否与边缘相撞
        if (firstX < 0 || firstX > COL_COUNT - 1 || firstY < 0 || firstY > ROW_COUNT - 1) {
            return false;
        }

        // 判断是否与蛇身相撞。大于等于5个点时才会和蛇身相撞
        if (mSnakes.size() >= 5) {
            // 第5个点会移到第4个点，也就是i等于3开始判断是否会和蛇身相撞
            for (int i = 3; i < mSnakes.size() - 1; i++) {
                if (firstX == mSnakes.get(i).getX() && firstY == mSnakes.get(i).getY()) {
                    return false;
                }
            }
        }

        return true;
    }

    private void eatFood(Point lastPoint) {
        if (mSnakes.get(0).getX() == mFood.getX() && mSnakes.get(0).getY() == mFood.getY()) {
            mSnakes.add(new Snake(getContext(), lastPoint, mGameRect));
            generateNewFood();
        }
    }

    /**
     * 获取随机的点。初始化舌头或者食物的位置
     *
     * @return
     */
    private Point getRandomPoint() {
        // ROW_COUNT - 2 避免刚出来就在底部最后一格
        return new Point(mRandom.nextInt(COL_COUNT - 1), mRandom.nextInt(ROW_COUNT - 2));
    }

    private void generateNewFood() {
        Point point = getRandomPoint();
        // 避免食物与舍身重叠
        while (isOverlap(point)) {
            point = getRandomPoint();
        }
        mFood = new Food(getContext(), point, mGameRect);
    }

    private boolean isOverlap(Point point) {
        for (Snake snake : mSnakes) {
            if (snake.getX() == point.x && snake.getY() == point.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * 开始游戏，如果已经开始了游戏则重新开始游戏
     */
    public void restart() {
        mIsRunning = true;
        mDirection = Direction.DOWN;

        mSnakes.clear();
        mSnakes.add(new Snake(getContext(), getRandomPoint(), mGameRect));

        generateNewFood();

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
        // 绘制控制按钮
        drawControlBtn(lockCanvas);

        if (mFood != null && mSnakes != null) {
            drawFood(lockCanvas);
            drawSnake(lockCanvas);
        }

        drawScore(lockCanvas);

        mSurfaceHolder.unlockCanvasAndPost(lockCanvas);
    }

    private void drawScore(Canvas canvas) {
        mPaint.setColor(mEdgeLineColor);
        mPaint.setFakeBoldText(true);
        String score = "分数：" + (mSnakes.size() - 1) * 10;
        Rect scoreRect = new Rect();
        mPaint.getTextBounds(score, 0, score.length(), scoreRect);
        canvas.drawText(score, (getWidth() - scoreRect.width()) / 2, mGameRect.top - (mGameRect.top - mPaint.getTextSize()) / 2, mPaint);
    }

    private void drawEdgeLine(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mEdgeLineColor);
        mPaint.setStrokeWidth(mEdgeLineSize);
        canvas.drawRect(mGameEdgeLineRect, mPaint);
    }

    private void drawGameArea(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mGameAreaColor);
        canvas.drawRect(mGameRect, mPaint);
    }

    private void drawControlBtn(Canvas canvas) {
        mLeftBtn.drawSelf(canvas);
        mUpBtn.drawSelf(canvas);
        mRightBtn.drawSelf(canvas);
        mDownBtn.drawSelf(canvas);
        mRestartBtn.drawSelf(canvas);
    }

    private void drawFood(Canvas canvas) {
        mFood.drawSelf(canvas);
    }

    private void drawSnake(Canvas canvas) {
        for (int i = 0; i < mSnakes.size(); i++) {
            mSnakes.get(i).drawSelf(canvas);
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