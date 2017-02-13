package hu.android.dani.game2048.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.android.dani.game2048.R;
import hu.android.dani.game2048.SPHelper;

public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder> {

    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final List<HighScoreItem> items;
    private Context context;

    public HighScoreAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public HighScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_high_score, parent, false);
        return new HighScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HighScoreViewHolder holder, final int position) {
        final HighScoreItem item = items.get(position);
        final String title = context.getString(R.string.delete_high_score,item.name,item.score);

        DateFormat df = new SimpleDateFormat(DATE_FORMAT);

        holder.hsDate.setText(df.format(item.date));
        holder.hsScoreTv.setText(context.getString(R.string.item_highscore_score,item.score));
        holder.hsGameSizeTv.setText(context.getString(R.string.item_highscore_size,item.fieldSize,item.fieldSize));
        holder.hsNameTv.setText(context.getString(R.string.item_highscore_name, item.name));
        holder.hsIsWin.setChecked(item.isWin);
        holder.shDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle(Html.fromHtml(title))
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HighScoreItem removed = items.remove(position);
                                removed.delete();
                                deleteFromSP(removed);
                                notifyItemRemoved(position);
                                if(position < items.size())
                                    notifyItemRangeChanged(position,items.size() - position);
                                Toast.makeText(context, "High Score: "+item.name+" deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();

                return true;
            }
        });

    }

    private void deleteFromSP(HighScoreItem removed) {
        switch (removed.fieldSize) {
            case 3:
                SPHelper.getInstance().delete(SPHelper.HIGH_SCORE_3); break;
            case 4:
                SPHelper.getInstance().delete(SPHelper.HIGH_SCORE_4); break;
            case 5:
                SPHelper.getInstance().delete(SPHelper.HIGH_SCORE_5); break;
            case 6:
                SPHelper.getInstance().delete(SPHelper.HIGH_SCORE_6); break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(List<HighScoreItem> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    static class HighScoreViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.hs_name_tv)
        TextView hsNameTv;
        @BindView(R.id.hs_score_tv)
        TextView hsScoreTv;
        @BindView(R.id.hs_game_size_tv)
        TextView hsGameSizeTv;
        @BindView(R.id.hs_date)
        TextView hsDate;
        @BindView(R.id.sh_is_win)
        CheckBox hsIsWin;
        @BindView(R.id.sh_delete)
        ImageButton shDelete;

        HighScoreViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
