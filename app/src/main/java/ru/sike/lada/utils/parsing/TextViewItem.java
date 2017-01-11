package ru.sike.lada.utils.parsing;


import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.sike.lada.R;
import ru.sike.lada.utils.parsing.abstraction.ViewDecorator;
import ru.sike.lada.utils.parsing.abstraction.ViewItem;

public class TextViewItem extends ViewItem {

    private String mText;
    private ViewDecorator mDecorator;

    public TextViewItem(Context pContext, String pText, ViewDecorator pDecorator) {
        super(pContext);
        mText = pText;
        mDecorator = pDecorator;
    }

    @Override
    public View getView() {
        TextView result = new TextView(getContext());
        result.setText(Html.fromHtml(mText));
        result.setMovementMethod(LinkMovementMethod.getInstance());
        if (mDecorator != null)
            mDecorator.Decorate(result);
        return result;
    }
}
