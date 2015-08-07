package cn.bingoogolapple.agn.surfaceview;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/8/7 17:30
 * 描述:
 */
public class Face extends Sprite {
    private int mSpeed = 6;
    private int mStepX;
    private int mSetpY;

    public Face(Bitmap defautBitmap, Point postion, Point touchPoint) {
        super(defautBitmap, postion);
        int distanceX = touchPoint.x - postion.x;
        int distanceY = touchPoint.y - postion.y;

        int distance = (int) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        mStepX = mSpeed * distanceX / distance;
        mSetpY = mSpeed * distanceY / distance;
    }

    public void move() {
        mPostion.x += mStepX;
        mPostion.y += mSetpY;
    }

}