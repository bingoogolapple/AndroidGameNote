package cn.bingoogolapple.agn.surfaceview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/8/7 17:29
 * 描述:
 */
public class Sprite {
    protected Bitmap mDefautBitmap;
    protected Point mPostion;

    public Sprite(Bitmap defautBitmap, Point postion) {
        mDefautBitmap = defautBitmap;
        mPostion = postion;
    }

    public void drawSelf(Canvas canvas) {
        canvas.drawBitmap(mDefautBitmap, mPostion.x, mPostion.y, null);
    }
}
