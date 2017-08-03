package net.daergoth.dankbank.ui.share;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.daergoth.dankbank.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TagListItem {

    private ViewGroup parent;

    private String tagName;

    private View listItemView;

    @BindView(R.id.textViewTagItemName)
    TextView tagNameTextView;

    public TagListItem(ViewGroup parent, String tagName) {
        this.parent = parent;

        this.tagName = tagName;

        this.listItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.share_tag_list_item, parent, false);

        ButterKnife.bind(this, listItemView);

        tagNameTextView.setText(tagName);
    }

    @OnClick(R.id.buttonRemoveTag)
    void removeTagOnClick() {
        parent.removeView(listItemView);
    }

    public View getListItemView() {
        return listItemView;
    }

    public void setListItemView(View listItemView) {
        this.listItemView = listItemView;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
