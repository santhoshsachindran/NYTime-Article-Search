package com.santhosh.codepath.nytimes.custom;


import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.BASE_THUMBNAIL_URL;

public class Article {
    private String mWebUrl;
    private String mHeadline;
    private String mThumbnail;

    public Article(String webUrl, String headline, String thumbnail) {

        mWebUrl = webUrl;
        mHeadline = headline;
        mThumbnail = thumbnail;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getThumbnail() {
        return BASE_THUMBNAIL_URL + mThumbnail;
    }

    @Override
    public String toString() {
        return "Article{" +
                "mWebUrl='" + mWebUrl + '\'' +
                ", mHeadline='" + mHeadline + '\'' +
                ", mThumbnail='" + mThumbnail + '\'' +
                '}';
    }
}
