package hfz.svoeoggau.at.hundatfuenfazwanzg.base;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;

/**
 * Created by Christian on 25.02.2018.
 */

public class BaseList {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    public BaseList(Context context, int resourceId, BaseAdapter adapter) {
        View v = ((Activity)context).findViewById(resourceId);
        if(v instanceof RecyclerView) {
            mSwipeRefreshLayout = null;
            mRecyclerView = (RecyclerView) v;
        }
        else if(v instanceof SwipeRefreshLayout) {
            mSwipeRefreshLayout = (SwipeRefreshLayout)v;
            mRecyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
        }
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        initSwipeRefresh();
    }

    public BaseList(Context context, View baseView, BaseAdapter adapter) {
        mSwipeRefreshLayout = (SwipeRefreshLayout)baseView;
        mRecyclerView = (RecyclerView)baseView.findViewById (R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        initSwipeRefresh();
    }

    public void destroy(){
        mRecyclerView.setAdapter(null);
    }

    private void initSwipeRefresh() {
        if(mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorScheme(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent, R.color.colorAccentDark);

            //has to be enabled by user
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    public void showLoading(Context context) {
        if(mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);
    }

    public void hideLoading() {
        if(mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public SwipeRefreshLayout getPullToRefresh() {
        return mSwipeRefreshLayout;
    }

    public void hideFabOnScroll(final FloatingActionButton fab) {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }

        });
    }
}
