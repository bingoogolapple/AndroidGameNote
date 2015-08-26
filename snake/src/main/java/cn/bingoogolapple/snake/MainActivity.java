package cn.bingoogolapple.snake;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements GameView.GameViewDelegate {
    private GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGameView = (GameView) findViewById(R.id.gameView);
        mGameView.setDelegate(this);
    }

    public void left(View v) {
        mGameView.changeDirection(GameView.Direction.LEFT);
    }

    public void up(View v) {
        mGameView.changeDirection(GameView.Direction.UP);
    }

    public void right(View v) {
        mGameView.changeDirection(GameView.Direction.RIGHT);
    }

    public void down(View v) {
        mGameView.changeDirection(GameView.Direction.DOWN);
    }

    public void restart(View v) {
        mGameView.restart();
    }

    @Override
    public void onGameOver() {
        Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
    }
}