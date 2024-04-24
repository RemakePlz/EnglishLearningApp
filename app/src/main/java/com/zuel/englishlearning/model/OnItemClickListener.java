package com.zuel.englishlearning.model;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.zuel.englishlearning.entity.ItemWordMeanChoice;

public interface OnItemClickListener {

    void onItemClick(RecyclerView parent, View view, int position, ItemWordMeanChoice itemWordMeanChoice);

}
