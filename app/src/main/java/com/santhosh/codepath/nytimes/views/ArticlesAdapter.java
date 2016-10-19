package com.santhosh.codepath.nytimes.views;


import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.BASE_THUMBNAIL_URL;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.ONLY_TEXT;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.TEXT_WITH_THUMBNAIL;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.santhosh.codepath.nytimes.R;
import com.santhosh.codepath.nytimes.custom.Article;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static List<Article> mArticles;

    public ArticlesAdapter(List<Article> articles) {
        mArticles = articles;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        View view;

        switch (viewType) {
            case ONLY_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.single_item_no_image_layout,
                        parent, false);
                viewHolder = new ArticaleWithTextOnlyViewHolder(view);
                break;
            case TEXT_WITH_THUMBNAIL:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.single_item_layout,
                        parent, false);
                viewHolder = new ArticleViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Article article = mArticles.get(position);

        switch (holder.getItemViewType()) {
            case ONLY_TEXT:
                ((ArticaleWithTextOnlyViewHolder) holder).mHeadlineViewNoImage.setText(
                        article.getHeadline());
                break;
            case TEXT_WITH_THUMBNAIL:
                Glide.with(((ArticleViewHolder) holder).mHeadlineView.getContext())
                        .load(article.getThumbnail())
                        .centerCrop()
                        .placeholder(R.drawable.nyt)
                        .into(((ArticleViewHolder) holder).mThumbnailView);
                ((ArticleViewHolder) holder).mHeadlineView.setText(article.getHeadline());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mArticles.get(position).getThumbnail() == null || mArticles.get(
                position).getThumbnail().isEmpty() || mArticles.get(position).getThumbnail().equals(
                BASE_THUMBNAIL_URL)) {
            return ONLY_TEXT;
        } else {
            return TEXT_WITH_THUMBNAIL;
        }
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail_view)
        ImageView mThumbnailView;
        @BindView(R.id.headline_view)
        TextView mHeadlineView;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ArticaleWithTextOnlyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.headline_view_no_image)
        TextView mHeadlineViewNoImage;

        public ArticaleWithTextOnlyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
