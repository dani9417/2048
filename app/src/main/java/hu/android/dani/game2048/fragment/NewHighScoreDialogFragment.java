package hu.android.dani.game2048.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import hu.android.dani.game2048.R;
import hu.android.dani.game2048.SPHelper;
import hu.android.dani.game2048.model.GameTableModel;


public class NewHighScoreDialogFragment extends AppCompatDialogFragment {
    public static final String TAG = "NewHighScoreDialogFragment";

    EditText newHsNameEt;

    public interface INewHighScoreItemDialogListener {
        void onHighScoreItemCreated(HighScoreItem item);
    }

    private INewHighScoreItemDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView = getContentView();
        newHsNameEt = (EditText) contentView.findViewById(R.id.new_hs_name_et);
        String title;

        if(GameTableModel.getInstance().isWin())
            title = getString(R.string.you_won_title);
        else
            title = getString(R.string.you_lost_title);

        return new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(title)
                .setView(contentView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isValidInput()) {
                            savePlayer();
                            Toast.makeText(getContext(), "Player saved", Toast.LENGTH_SHORT).show();

                        }
                        GameTableModel.getInstance().resetModel();
                        Log.d("tag",newHsNameEt.getText().toString());

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    private boolean isValidInput() {
        return newHsNameEt.getText().length() > 0;
    }

    private void savePlayer() {
        SPHelper.getInstance().init(getContext());
        SPHelper.getInstance().putString(SPHelper.PLAYER_NAME,newHsNameEt.getText().toString());
        SPHelper.getInstance().putInt(SPHelper.PLAYER_SCORE,GameTableModel.getInstance().getGameScore());
        SPHelper.getInstance().putInt(SPHelper.GAME_SIZE,GameTableModel.getInstance().getSize());
        SPHelper.getInstance().putBoolean(SPHelper.GAME_WIN,GameTableModel.getInstance().isWin());
        SPHelper.getInstance().putLong(SPHelper.GAME_DATE, System.currentTimeMillis());

    }

    private View getContentView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_high_score_item, null);
    }




}
