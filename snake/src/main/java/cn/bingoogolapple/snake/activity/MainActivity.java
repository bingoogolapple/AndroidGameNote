package cn.bingoogolapple.snake.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import cn.bingoogolapple.snake.widget.GameView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends Activity implements GameView.GameViewDelegate {
    private GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameView = new GameView(this);
        mGameView.setDelegate(this);
        setContentView(mGameView);
    }

    @Override
    public void onGameOver() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText("提示").setContentText("游戏结束").show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGameView.handleTouch(event);
        return super.onTouchEvent(event);
    }
}