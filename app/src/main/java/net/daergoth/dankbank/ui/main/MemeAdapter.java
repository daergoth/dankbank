package net.daergoth.dankbank.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.daergoth.dankbank.R;
import net.daergoth.dankbank.meme.Meme;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.ViewHolder> {

    private List<Meme> memeList;

    public MemeAdapter(List<Meme> memeList) {
        this.memeList = memeList;
    }

    @Override
    public MemeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_meme_grid_item, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Meme m = memeList.get(position);

        holder.memeImageView.setImageURI(m.getUri());
    }

    @Override
    public int getItemCount() {
        return memeList.size();
    }

    public List<Meme> getMemeList() {
        return Collections.unmodifiableList(memeList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.meme_item_image)
        ImageView memeImageView;

        public ViewHolder(View holder) {
            super(holder);

            ButterKnife.bind(this, holder);
        }

    }

}
