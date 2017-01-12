package ru.sike.lada.utils.parsing;


import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import ru.sike.lada.utils.parsing.abstraction.ViewDecorator;
import ru.sike.lada.utils.parsing.abstraction.ViewItem;

public class TextViewItem extends ViewItem {

    private String mText;
    private ViewDecorator mDecorator;

    public TextViewItem(String pText, ViewDecorator pDecorator) {
        super();
        mText = pText;
        mDecorator = pDecorator;
    }

    @Override
    public View getView(Context pContext) {
        TextView result = new TextView(pContext);
        result.setText(Html.fromHtml(mText));
        result.setMovementMethod(LinkMovementMethod.getInstance());
        if (mDecorator != null)
            mDecorator.Decorate(result);
        return result;
    }
}
