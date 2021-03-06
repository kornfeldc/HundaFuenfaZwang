package hfz.svoeoggau.at.hundatfuenfazwanzg.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.Vector;

import hfz.svoeoggau.at.hundatfuenfazwanzg.R;
import hfz.svoeoggau.at.hundatfuenfazwanzg.activities.ArticleActivity;
import hfz.svoeoggau.at.hundatfuenfazwanzg.adapter.ArticlesAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseAdapter;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseFragment;
import hfz.svoeoggau.at.hundatfuenfazwanzg.base.BaseList;
import hfz.svoeoggau.at.hundatfuenfazwanzg.db.Article;
import hfz.svoeoggau.at.hundatfuenfazwanzg.helpers.Params;

/**
 * Created by Christian on 23.02.2018.
 */

public class ArticlesFragment extends BaseFragment {
    private ArticlesAdapter mAdapter;
    private BaseList mList;
    private Vector<Article> articles = new Vector<>();
    private Vector<Article> articlesFiltered = new Vector<>();
    //private FloatingActionButton fab;
    private Button buttonNewArticle;
    private String search = "";
    private ListenerRegistration listenerRegistration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_articles, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonNewArticle = getView().findViewById(R.id.buttonNewArticle);
        buttonNewArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openArticle(null);
            }
        });

        mAdapter = new ArticlesAdapter(getActivity(), R.layout.listitem_article, articlesFiltered, new BaseAdapter.IOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T item) {
                openArticle(articlesFiltered.get(position));
            }
        });


        setOnSearchListener(new OnSearch() {
            @Override
            public void search(String searchStr) {
                search = searchStr;
                createFiltered();
                mAdapter.notifyDataSetChanged();
            }
        });

        showProgress();
        listenerRegistration = Article.listen(articles, getContext(), new Article.OnListChanged() {
            @Override
            public void callback() {
                createFiltered();
                hideProgress();
                if(mList == null) {
                    mList = new BaseList(getActivity(), R.id.swipeRefreshLayout, mAdapter);
                    //mList.hideFabOnScroll(fab);
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void createFiltered() {
        articlesFiltered.clear();
        for(Article a : articles) {
            if(a.matchSearch(search))
                articlesFiltered.add(a);
        }
    }

    private void openArticle(Article article) {
        Intent intent = new Intent(getActivity(), ArticleActivity.class);
        if(article != null) {
            //intent.putExtra("articleId", article.getReference().getId());
            intent.putExtra("articleId", Params.setParams(article));
        }
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(listenerRegistration != null)
            listenerRegistration.remove();
    }
}
