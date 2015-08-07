package cn.bingoogolapple.agn.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/8/7 17:31
 * 描述:
 */
public class Man extends Sprite {
    public static final int DOWN = 0;
    private int mSpeed = 6;

    public Man(Bitmap defautBitmap, Point postion) {
        super(defautBitmap, postion);
    }

    public Face createFace(Context context, Point touchPoint) {
        Bitmap faceBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.rating_small);
        Face face = new Face(faceBitmap, new Point(mPostion.x + 150, mPostion.y + 150), touchPoint);
        return face;
    }

    public void move(int d) {
        if (d == DOWN) {
            mPostion.y += mSpeed;
        }
    }
}