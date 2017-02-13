package hu.android.dani.game2048;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.android.dani.game2048.fragment.HighScoreAdapter;
import hu.android.dani.game2048.fragment.HighScoreItem;
import hu.android.dani.game2048.view.SimpleDividerItemDecoration;

public class HighScoreActivity extends AppCompatActivity {
    @BindView(R.id.MainRecyclerView)
    RecyclerView recyclerView;

    private HighScoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        ButterKnife.bind(this);
        SPHelper.getInstance().init(this);
        adapter = new HighScoreAdapter();

        saveHighScoreItem();
        initRecyclerView();


    }

    @Override
    protected void onPause() {
        super.onPause();
        loadItemsInBackground();
    }

    private void saveHighScoreItem() {
        HighScoreItem hs = new HighScoreItem();

        hs.name = SPHelper.getInstance().getPlayerName();
        hs.score = SPHelper.getInstance().getPlayerScore();
        hs.fieldSize = SPHelper.getInstance().getGameSize();
        hs.date = SPHelper.getInstance().getGameDate();
        hs.isWin = SPHelper.getInstance().getPlayerWin();

        if (hs.score != 0)
            hs.save();

        SPHelper.getInstance().clear();
    }

    private void initRecyclerView() {

        loadItemsInBackground();
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

    }

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<HighScoreItem>>() {
            @Override
            protected List<HighScoreItem> doInBackground(Void... params) {
                return HighScoreItem.listAll(HighScoreItem.class);
            }

            @Override
            protected void onPostExecute(List<HighScoreItem> highScoreItems) {
                super.onPostExecute(highScoreItems);


                Collections.sort(highScoreItems, new Comparator<HighScoreItem>() {
                    @Override
                    public int compare(HighScoreItem item1, HighScoreItem item2) {
                        int c = (item1.score > item2.score) ? -1 :
                                (item1.score < item2.score) ? 1 : 0;

                        if (c < 0)
                            return c;
                        else {
                            return (item1.fieldSize > item2.fieldSize) ? -1 :
                                    (item1.fieldSize < item2.fieldSize) ? 1 : 0;
                        }

                    }
                });

                if (highScoreItems.size() > 0)
                    saveToSP(highScoreItems);

                adapter.update(highScoreItems);
            }
        }.execute();
    }

    private void saveToSP(List<HighScoreItem> list) {
        ArrayList<Integer> highScoresByDescendingFieldSize = new ArrayList(Collections.nCopies(4, 0));

        int size = 6;
        for (int i = 0; i < list.size(); i++) {

            for (int j = size; j >= 3; j--) {
                if (list.get(i).fieldSize == j) {
                    highScoresByDescendingFieldSize.set(6 - j, list.get(i).score);
                    size = j - 1;
                }
            }
        }


        SPHelper.getInstance().putInt(SPHelper.HIGH_SCORE_6, highScoresByDescendingFieldSize.get(0));
        SPHelper.getInstance().putInt(SPHelper.HIGH_SCORE_5, highScoresByDescendingFieldSize.get(1));
        SPHelper.getInstance().putInt(SPHelper.HIGH_SCORE_4, highScoresByDescendingFieldSize.get(2));
        SPHelper.getInstance().putInt(SPHelper.HIGH_SCORE_3, highScoresByDescendingFieldSize.get(3));

    }


}
