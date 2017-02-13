package hu.android.dani.game2048;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import hu.android.dani.game2048.fragment.NewHighScoreDialogFragment;
import hu.android.dani.game2048.model.GameTableModel;
import hu.android.dani.game2048.view.GameView;
import hu.android.dani.game2048.view.GestureListener;
import hu.android.dani.game2048.view.TileAnimation;


public class GameActivity extends AppCompatActivity {


    private final GestureDetector myGestureDetector = new GestureDetector(this.getApplication(), new GestureListener());

    @BindView(R.id.restart_button)
    Button restartButton;
    @BindView(R.id.score_textview)
    TextView scoreTextview;
    @BindView(R.id.best_score_textview)
    TextView bestScoreTextview;
    @BindView(R.id.game_area_title)
    TextView gameAreaTitle;

    GameView gameView;

    private boolean backPressed;
    private boolean isMoving;

    MediaPlayer mPlayer;

    TileAnimation tileAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        SPHelper.getInstance().init(getApplicationContext());

        initMediaPlayer();

        gameView = (GameView) findViewById(R.id.game_view);

        gameView.post(new Runnable() {
            @Override
            public void run() {
                Log.d("inspect", "post " + String.valueOf(gameView.getWidth()));
                gameView.initTilePositions();
                gameView.initTextSize();
            }
        });

        GameTableModel.getInstance().setSize(4);

        tileAnimation = new TileAnimation(gameView);
        tileAnimation.setDuration(200);

        isMoving = false;
        backPressed = false;

        setGameScoreTextView();
        setMaxScoreTextView();
        setGameAreaTitle();

        addAnimationListener();

        addGameViewOnTouchListener();
    }

    private void addGameViewOnTouchListener() {
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Log.d("animation", String.valueOf(isMoving));
                boolean result = myGestureDetector.onTouchEvent(event);
                if (!result) {


                    if (GameTableModel.getInstance().getMovingDirection() != GameTableModel.DIRECTION.NOTMOVING && !isMoving) {
                        isMoving = true;
                        tileAnimation.setDirection(GameTableModel.getInstance().getMovingDirection().ordinal());

                        tileAnimation.initParameters();

                        Log.d("invalidate", "startAnimation");
                        gameView.startAnimation(tileAnimation);

                    }
                    //gameView.postInvalidate();
                    setGameScoreTextView();
                    setMaxScoreTextView();


                }


                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mPlayer = MediaPlayer.create(this, R.raw.slide_short);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (GameTableModel.getInstance().isWin() ||
                        GameTableModel.getInstance().isLose()) {
                    mPlayer.reset();
                    mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.slide_short);
                }
            }
        });

    }


    private void setGameAreaTitle() {
        gameAreaTitle.setText(String.valueOf(GameTableModel.getInstance().getWinNumber()));
    }

    private void addAnimationListener() {
        tileAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("table", "before:\n" + GameTableModel.getInstance().toString());
                if (GameTableModel.getInstance().canMoveOrMerge()) {
                    mPlayer.start();
                }


            }

            @Override
            public void onAnimationEnd(Animation animation) {

                switch (GameTableModel.getInstance().getMovingDirection()) {
                    case LEFT:
                        GameTableModel.getInstance().moveLeft();
                        break;
                    case UP:
                        GameTableModel.getInstance().moveUp();
                        break;
                    case RIGHT:
                        GameTableModel.getInstance().moveRight();
                        break;
                    case DOWN:
                        GameTableModel.getInstance().moveDown();
                        break;
                    case NOTMOVING:
                    default:
                        break;

                }
                isMoving = false;
                tileAnimation.resetPositions();
                Log.d("table", "after:\n" + GameTableModel.getInstance().toString());
                setGameScoreTextView();
                setMaxScoreTextView();


                if (GameTableModel.getInstance().isWin() ||
                        GameTableModel.getInstance().isLose()) {

                    mPlayer.reset();
                    if (GameTableModel.getInstance().isWin()) {
                        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.success);
                        mPlayer.start();
                    } else {
                        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.nooooo);
                        mPlayer.start();

                    }

                    initMediaPlayer();


                    NewHighScoreDialogFragment dialogFragment = new NewHighScoreDialogFragment();

                    dialogFragment.setCancelable(false);
                    dialogFragment.show(getSupportFragmentManager(), NewHighScoreDialogFragment.TAG);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //do nothing
            }
        });
    }


    private int getMaxScoreFromSP() {
        return SPHelper.getInstance().getHighScore();
    }

    private void setGameScoreTextView() {
        scoreTextview.setText(getString(R.string.game_score, GameTableModel.getInstance().getGameScore()));
    }

    private void setMaxScoreTextView() {
        bestScoreTextview.setText(getString(R.string.best_score, getMaxScoreFromSP()));
    }

    @OnClick(R.id.restart_button)
    public void onClick(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        GameTableModel.getInstance().resetModel();
                        setGameScoreTextView();
                        gameView.resetTiles();
                        gameView.invalidate();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    default:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Do you want to restart the game?\nAll progress will be lost!")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if (!backPressed) {
            Toast.makeText(this, "Press back to leave game", Toast.LENGTH_SHORT).show();
            backPressed = !backPressed;
            new CountDownTimer(3000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    backPressed = !backPressed;
                }
            }.start();
        } else {

            GameTableModel.getInstance().resetModel();
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.size_three:
                GameTableModel.getInstance().setSize(3);
                gameView.resetTiles();
                setGameScoreTextView();
                setMaxScoreTextView();
                setGameAreaTitle();
                return true;
            case R.id.size_four:
                GameTableModel.getInstance().setSize(4);
                gameView.resetTiles();
                setGameScoreTextView();
                setMaxScoreTextView();
                setGameAreaTitle();
                return true;
            case R.id.size_five:
                GameTableModel.getInstance().setSize(5);
                gameView.resetTiles();
                setGameScoreTextView();
                setMaxScoreTextView();
                setGameAreaTitle();
                return true;
            case R.id.size_six:
                GameTableModel.getInstance().setSize(6);
                gameView.resetTiles();
                setGameScoreTextView();
                setMaxScoreTextView();
                setGameAreaTitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_size_menu, menu);
        return true;
    }


    @OnLongClick(R.id.score_textview)
    boolean onLongClick() {
        Bitmap b = screenShot(gameView);
        saveImage(b);

        return true;
    }

    private void saveImage(Bitmap b) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        int id = new Random().nextInt(100);
        String fname = "Image-" + id + ".jpg";
        File file = new File(myDir, fname);
        while (file.exists()) {
            id = new Random().nextInt(100);
            fname = "Image-" + id + ".jpg";
            file = new File(myDir, fname);
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


}
