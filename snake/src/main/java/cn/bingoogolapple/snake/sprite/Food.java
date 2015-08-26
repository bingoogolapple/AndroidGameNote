package cn.bingoogolapple.snake.sprite;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import cn.bingoogolapple.snake.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/8/26 下午11:42
 * 描述:
 */
public class Food extends Sprite {
    private Rect mGameRect;

    public Food(Context context, Point postion, Rect gameRect) {
        super(BitmapFactory.decodeResource(context.getResources(), R.mipmap.food), postion);
        mGameRect = gameRect;
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawBitmap(mDefautBitmap, mGameRect.left + mPostion.x * mDefautBitmap.getWidth(), mGameRect.top + mPostion.y * mDefautBitmap.getHeight(), null);
    }

}