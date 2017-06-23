package net.daergoth.dankbank.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import net.daergoth.dankbank.R;
import net.daergoth.dankbank.meme.Meme;
import net.daergoth.dankbank.tag.Tag;

import java.util.List;

public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.ViewHolder> {

    private List<Meme> memeList;

    public MemeAdapter(List<Meme> memeList) {
        this.memeList = memeList;
    }

    @Override
    public MemeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meme_list_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Meme m = memeList.get(position);

        holder.memeImageView.setImageURI(m.getUri());

        holder.tagLinearLayout.removeAllViews();
        for (Tag t : m.getTags()) {
            TextView tagView = new TextView(holder.tagLinearLayout.getContext());
            tagView.setText(t.getTagName());
            holder.tagLinearLayout.addView(tagView);
        }
    }

    @Override
    public int getItemCount() {
        return memeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView memeImageView;

        private ScrollView tagLinearLayout;

        public ViewHolder(View holder) {
            super(holder);

            this.memeImageView = holder.findViewById(R.id.meme_row_image);
            this.tagLinearLayout = holder.findViewById(R.id.meme_row_tagLayout);
        }

    }

}
