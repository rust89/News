package ru.sike.lada.adapters.listenters;

import android.view.View;

import ru.sike.lada.adapters.interfaces.IOnNewsItemClickListener;

public class NewsItemClickHandler implements View.OnClickListener {

    private long mNewsId;
    private String mImagePath;
    private IOnNewsItemClickListener mExternalClickListener;

    public NewsItemClickHandler(long pNewsId, String pImagePath, IOnNewsItemClickListener pExternalClickListener) {
        mNewsId = pNewsId;
        mImagePath = pImagePath;
        mExternalClickListener = pExternalClickListener;
    }

    @Override
    public void onClick(View view) {
        mExternalClickListener.onItemClick(mNewsId, view, mImagePath);
    }
}
