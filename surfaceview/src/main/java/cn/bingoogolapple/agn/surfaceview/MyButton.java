package cn.bingoogolapple.agn.surfaceview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/8/7 17:34
 * 描述:
 */
public class MyButton extends Sprite {
    private boolean mIsClick;
    private Bitmap mPressedBitmap;
    private OnClickListener mOnClickListener;

    public interface OnClickListener{
        void click();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void click(){
        if(mOnClickListener !=null){
            mOnClickListener.click();
        }
    }

    public MyButton(Bitmap defautBitmap, Point postion, Bitmap pressedBitmap) {
        super(defautBitmap, postion);
        mPressedBitmap = pressedBitmap;
    }


    public void setIsClick(boolean isClick) {
        this.mIsClick = isClick;
    }

    public boolean isClick(Point touchPoint) {
        Rect rect = new Rect(mPostion.x, mPostion.y, mPostion.x + mPressedBitmap.getWidth(), mPostion.y + mPressedBitmap.getHeight());
        mIsClick =rect.contains(touchPoint.x, touchPoint.y);
        return mIsClick;

    }

    @Override
    public void drawSelf(Canvas canvas) {
        if (mIsClick) {
            canvas.drawBitmap(mPressedBitmap, mPostion.x, mPostion.y, null);
        } else {
            super.drawSelf(canvas);
        }
    }
}