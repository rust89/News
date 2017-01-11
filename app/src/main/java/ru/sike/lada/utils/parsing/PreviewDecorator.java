package ru.sike.lada.utils.parsing;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.sike.lada.R;
import ru.sike.lada.utils.parsing.abstraction.ViewDecorator;

public class PreviewDecorator extends ViewDecorator {

    public PreviewDecorator(Context pContext) {
        super(pContext);
    }

    @Override
    public void Decorate(View pView) {
        TextView textView = (TextView) pView;
        if (textView != null) {
            int verticalPadding = getContext().getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
            int horizontalSmallPadding = getContext().getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_small_margin);
            int verticalSmallPadding = getContext().getResources().getDimensionPixelOffset(R.dimen.activity_vertical_small_margin);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0, verticalPadding);
            textView.setLayoutParams(layoutParams);
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.news_preview_background));
            textView.setPadding(horizontalSmallPadding, verticalSmallPadding, horizontalSmallPadding, verticalSmallPadding);
            textView.setTextColor(getContext().getResources().getColor(R.color.fullNewsPreviewTextColor));
        }
    }
}
