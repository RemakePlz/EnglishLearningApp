package com.zuel.englishlearning.activity.review;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;

import com.zuel.englishlearning.R;
import com.zuel.englishlearning.activity.BaseActivity;
import com.zuel.englishlearning.adapter.MatchAdapter;
import com.zuel.englishlearning.database.Word;
import com.zuel.englishlearning.entity.ItemMatch;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends BaseActivity {

    public static List<Word> wordList = new ArrayList<>();

    public static ArrayList<ItemMatch> matchList = new ArrayList<>();

    public static ArrayList<ItemMatch> allMatches = new ArrayList<>();

    private RecyclerView recyclerView;

    private static final String TAG = "MatchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        init();

        windowExplode();

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        MatchAdapter matchAdapter = new MatchAdapter(matchList);
        recyclerView.setAdapter(matchAdapter);

    }

    private void init() {
        recyclerView = findViewById(R.id.recycler_mt);
    }

}
