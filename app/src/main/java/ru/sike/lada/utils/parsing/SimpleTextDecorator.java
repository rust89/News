package ru.sike.lada.utils.parsing;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.sike.lada.R;
import ru.sike.lada.utils.parsing.abstraction.ViewDecorator;


public class SimpleTextDecorator extends ViewDecorator {

    public SimpleTextDecorator() {
        super();
    }

    @Override
    public void Decorate(View pView) {
        TextView textView = (TextView) pView;
        if (textView != null) {
            Context context = textView.getContext();
            int verticalSmallPadding = context.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_small_margin);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0, verticalSmallPadding);
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(context.getResources().getColor(R.color.fullNewsTextColor));
        }
    }
}
