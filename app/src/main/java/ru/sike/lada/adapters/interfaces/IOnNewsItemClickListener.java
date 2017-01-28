package ru.sike.lada.adapters.interfaces;

import android.view.View;

public interface IOnNewsItemClickListener {
    void onItemClick(long newsId, View view, String pTransitionImagePath);
}