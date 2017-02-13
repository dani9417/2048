package hu.android.dani.game2048;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.android.dani.game2048.model.GameTableModel;

public class MainMenuActivity extends Activity {

    @BindView(R.id.bt_start_game) Button btStartGame;
    @BindView(R.id.bt_high_scores) Button btHighScores;
    @BindView(R.id.bt_exit) Button btExit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ButterKnife.bind(this);

        Animation playAnim = AnimationUtils.loadAnimation(this,R.anim.main_menu_play_anim);
        Animation scoresAnim = AnimationUtils.loadAnimation(this,R.anim.main_menu_scores_anim);
        Animation exitAnim = AnimationUtils.loadAnimation(this,R.anim.main_menu_exit_anim);
        btStartGame.startAnimation(playAnim);
        btHighScores.startAnimation(scoresAnim);
        btExit.startAnimation(exitAnim);





    }

    @OnClick(R.id.bt_start_game)
    void startGame(View view) {
        Intent intent = new Intent(MainMenuActivity.this,GameActivity.class);
        GameTableModel.getInstance().resetModel();
        startActivity(intent);
    }

    @OnClick(R.id.bt_high_scores)
    void showHighScores() {
        Intent intent = new Intent(MainMenuActivity.this, HighScoreActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.bt_exit)
    void exitGame() {
        this.finish();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
