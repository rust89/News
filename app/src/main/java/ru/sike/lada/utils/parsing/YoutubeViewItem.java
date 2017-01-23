package ru.sike.lada.utils.parsing;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import ru.sike.lada.R;
import ru.sike.lada.utils.parsing.abstraction.ViewItem;

public class YoutubeViewItem extends ViewItem {

    private String mVideoId;

    public YoutubeViewItem(String pVideoId) {
        super();
        mVideoId = pVideoId;
    }

    @Override
    public View getView(Context pContext) {
        // Определяем контейнер для фрагмента с видео
        FrameLayout youtubeContainer = new FrameLayout(pContext);
        if (pContext instanceof Activity) {
            FragmentManager fragmentManager = ((Activity)pContext).getFragmentManager();
            youtubeContainer.setId(generateViewId());
            YouTubePlayerFragment youtubePlayerFragment = new YouTubePlayerFragment();
            youtubePlayerFragment.initialize(pContext.getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider pProvider, YouTubePlayer pYouTubePlayer, boolean pWasRestored) {
                    if (!pWasRestored)
                        pYouTubePlayer.cueVideo(mVideoId);
                    else
                        pYouTubePlayer.loadVideo(mVideoId);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider pProvider, YouTubeInitializationResult pYouTubeInitializationResult) {
                    if (pYouTubeInitializationResult.isUserRecoverableError()) {
                        //pYouTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
                    } else {
                        //String errorMessage = String.format(
                        //        "There was an error initializing the YouTubePlayer (%1$s)",
                        //        pYouTubeInitializationResult.toString());
                        //Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
            fragmentManager.beginTransaction().replace(youtubeContainer.getId(), youtubePlayerFragment).commit();
        } else {
            TextView result = new TextView(pContext);
            result.setText(mVideoId);
            youtubeContainer.addView(result);
        }

        return youtubeContainer;
    }
}
