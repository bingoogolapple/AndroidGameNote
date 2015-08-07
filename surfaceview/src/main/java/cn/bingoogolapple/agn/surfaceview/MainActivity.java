package cn.bingoogolapple.agn.surfaceview;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/8/7 17:23
 * 描述:
 */
public class MainActivity extends Activity {
    private GameUI mGameUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameUI = new GameUI(getApplicationContext());
        setContentView(mGameUI);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGameUI.handleTouch(event);
        return super.onTouchEvent(event);
    }

}