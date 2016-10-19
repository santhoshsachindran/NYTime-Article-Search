package com.santhosh.codepath.nytimes.fragment;


import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.API_KEY;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.ARTS;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.BASE_NYT_URL;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.DEFAULT_SEARCH;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.DOCS;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.FASHION;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.FL_KEY;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.FQ_KEY;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.HEADLINE;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.MAIN;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.MULTIMEDIA;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.NEWS_DESK;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.NYT_API_KEY;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.PAGE;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.QUERY_KEY;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.RESPONSE;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.SHARED_PREFS_NAME;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.SORT;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.SPORTS;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.START_DATE;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.SUBTYPE;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.URL;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.WEB_URL;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.WIDE;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.XLARGE;
import static com.santhosh.codepath.nytimes.utils.UtilsAndConstants.networkAvailable;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.santhosh.codepath.nytimes.R;
import com.santhosh.codepath.nytimes.custom.Article;
import com.santhosh.codepath.nytimes.custom.EndlessRecyclerViewScrollListener;
import com.santhosh.codepath.nytimes.views.ArticlesAdapter;
import com.santhosh.codepath.nytimes.views.RecyclerViewItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Article>>,
        NewsPrefEditFragment.EditPrefDialogListener {
    private static List<Article> mArticlesList = new ArrayList<>();
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout mSwipeContainer;
    @BindView(R.id.empty_view)
    TextView mEmptyView;
    @BindView(R.id.swipeContainerEmpty)
    SwipeRefreshLayout mSwipeContainerEmpty;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.root_layout)
    FrameLayout mRootLayout;


    private ArticlesAdapter mArticlesAdapter;
    private static int mPageNumber = 0;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                        SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(QUERY_KEY, query);
                editor.commit();
                mArticlesList.clear();
                mPageNumber = 0;
                getLoaderManager().restartLoader(0, null, MainFragment.this);
                searchView.clearFocus();
                menu.findItem(R.id.action_search).collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                NewsPrefEditFragment alertDialog = NewsPrefEditFragment.newInstance(
                        getString(R.string.filter_items));
                alertDialog.setTargetFragment(MainFragment.this, 300);
                alertDialog.show(fragmentManager, "edit_pref");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager layoutManager;
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            //layoutManager = new GridLayoutManager(getContext(), 3);
        } else {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            //layoutManager = new GridLayoutManager(getContext(), 2);
        }

        if (!networkAvailable(getContext())) {
            mSwipeContainerEmpty.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mSwipeContainer.setVisibility(View.GONE);

            mEmptyView.setText(R.string.no_network);

            mSwipeContainerEmpty.setOnRefreshListener(() -> {
                if (networkAvailable(getContext())) {
                    mArticlesList.clear();
                    mPageNumber = 0;
                    getLoaderManager().restartLoader(0, null, MainFragment.this);
                } else {
                    mSwipeContainerEmpty.setRefreshing(false);
                    Snackbar.make(mRootLayout, R.string.no_network, Snackbar.LENGTH_LONG).show();
                }
            });

            mSwipeContainerEmpty.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);
        } else {
            mSwipeContainerEmpty.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mSwipeContainer.setVisibility(View.VISIBLE);

            mSwipeContainer.setOnRefreshListener(() -> {
                if (networkAvailable(getContext())) {
                    mArticlesList.clear();
                    mPageNumber = 0;
                    getLoaderManager().restartLoader(0, null, MainFragment.this);
                } else {
                    mSwipeContainer.setRefreshing(false);
                    Snackbar.make(mRootLayout, R.string.no_network, Snackbar.LENGTH_LONG).show();
                }
            });
            mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mArticlesAdapter = new ArticlesAdapter(mArticlesList);
        mRecyclerView.setAdapter(mArticlesAdapter);

        mRecyclerView.addOnScrollListener(
                new EndlessRecyclerViewScrollListener((StaggeredGridLayoutManager) layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        if (mPageNumber < page) {
                            mPageNumber = page;
                            getLoaderManager().restartLoader(0, null, MainFragment.this);
                        }
                    }
                });
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(),
                (v, position) -> {
                    Article article = mArticlesList.get(position);
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_action_share);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, article.getWebUrl());
                    int requestCode = 100;

                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),
                            requestCode,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setActionButton(bitmap, getString(R.string.share_link),
                            pendingIntent, true);
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(getActivity(),
                            Uri.parse(article.getWebUrl()));
                }));

        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        return new FetchArticles(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        if (data != null && data.size() > 0) {
            mArticlesList.addAll(data);

            if (networkAvailable(getContext())) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mSwipeContainer.setVisibility(View.VISIBLE);
                mSwipeContainerEmpty.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
            }

            mArticlesAdapter.notifyDataSetChanged();

            mSwipeContainer.setRefreshing(false);
            mSwipeContainerEmpty.setRefreshing(false);
        } else {
            Snackbar.make(mRootLayout, R.string.no_data, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
    }

    @Override
    public void onFinishEditDialog() {
        mArticlesList.clear();
        mPageNumber = 0;
        getLoaderManager().restartLoader(0, null, this);
    }

    private static class FetchArticles extends AsyncTaskLoader<List<Article>> {
        public FetchArticles(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Article> loadInBackground() {
            OkHttpClient client = new OkHttpClient();
            Response response;
            String queryUrl = getQueryUrl();

            Request request = new Request.Builder()
                    .url(queryUrl)
                    .build();

            try {
                response = client.newCall(request).execute();
                return getArticlesList(response.body().string());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private List<Article> getArticlesList(String body) throws JSONException {
            List<Article> articles = new ArrayList<>();
            String url;
            String thumbnail;
            String xlarge;
            String wide;
            String headline;

            JSONObject json = new JSONObject(body);
            JSONObject jsonObject = json.optJSONObject(RESPONSE);
            JSONArray jsonArray;
            if (jsonObject != null) {
                jsonArray = jsonObject.optJSONArray(DOCS);
            } else {
                return articles;
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                thumbnail = "";
                xlarge = "";
                wide = "";

                JSONObject eachArticle = (JSONObject) jsonArray.get(i);
                url = eachArticle.optString(WEB_URL);
                JSONArray multimedia = eachArticle.optJSONArray(MULTIMEDIA);
                for (int j = 0; j < multimedia.length(); j++) {
                    JSONObject item = (JSONObject) multimedia.get(j);
                    String subtype = item.optString(SUBTYPE);
                    if (subtype.equals(XLARGE)) {
                        xlarge = item.optString(URL);
                        break;
                    } else if (subtype.equals(WIDE)) {
                        wide = item.optString(URL);
                    } else {
                        thumbnail = item.optString(URL);
                    }
                }
                JSONObject headlineObj = eachArticle.optJSONObject(HEADLINE);
                headline = headlineObj.optString(MAIN);

                thumbnail = !xlarge.isEmpty() ? xlarge : !wide.isEmpty() ? wide : thumbnail;

                Article article = new Article(url, headline, thumbnail);
                articles.add(article);
            }

            return articles;
        }

        public String getQueryUrl() {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                    SHARED_PREFS_NAME, Context.MODE_PRIVATE);

            String toSearch = sharedPreferences.getString(QUERY_KEY, "");
            Set<String> newsDesk = sharedPreferences.getStringSet(NEWS_DESK, null);
            boolean isItemChecked = sharedPreferences.getInt(SPORTS, 0) == 1
                    || sharedPreferences.getInt(FASHION, 0) == 1
                    || sharedPreferences.getInt(ARTS, 0) == 1;

            StringBuilder queryUrl = new StringBuilder(BASE_NYT_URL);
            queryUrl.append(QUERY_KEY);
            queryUrl.append(toSearch.isEmpty() ? DEFAULT_SEARCH : toSearch + "&");
            queryUrl.append(SORT + "=");
            queryUrl.append(sharedPreferences.getString(SORT, "newest") + "&");
            queryUrl.append(START_DATE + "=");
            queryUrl.append(sharedPreferences.getString(START_DATE, "20161001") + "&");
            if (mArticlesList != null && mArticlesList.size() > 0) {
                queryUrl.append(PAGE);
                queryUrl.append((mArticlesList.size() / 10) + "&");
            }
            if (isItemChecked && newsDesk != null && newsDesk.size() > 0) {
                queryUrl.append(FQ_KEY);
                queryUrl.append(NEWS_DESK + ":(");

                for (String s : newsDesk) {
                    queryUrl.append("\"" + s + "\" ");
                }
                queryUrl.append(")&");
            }
            queryUrl.append(FL_KEY);
            queryUrl.append(API_KEY + NYT_API_KEY);

            return queryUrl.toString();
        }
    }
}
