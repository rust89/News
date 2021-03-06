package ru.sike.lada.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = space;
            outRect.bottom = 0;
        } else {
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }
}