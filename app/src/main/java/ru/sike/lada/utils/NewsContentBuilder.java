package ru.sike.lada.utils;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.sike.lada.R;

public class NewsContentBuilder {

    private static final String rootClass = "mbwrap";
    private static final String previewClass = "mblead";
    private static final String mainClass = "mbcontent";

    /**
     * Выполняет построение представление с содержимым новости
     * @param pContext
     * @param pHtml
     * @return
     */
    public View Build(Context pContext, String pHtml) {
        LinearLayout rootLayout = new LinearLayout(pContext);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // временная переменная
        Elements elements;

        Document doc = Jsoup.parse(pHtml != null ? pHtml : "");
        elements = doc.getElementsByClass(rootClass);

        // контейнер с содержимым новости
        Element contentRoot = elements.size() > 0 ? elements.get(0) : null;
        if (contentRoot == null)
            return rootLayout;

        // получаем превью для новости
        elements = contentRoot.getElementsByClass(previewClass);
        View newsPreview = prepareNewsPreview(pContext, elements.size() > 0 ? elements.get(0) : null);
        if (newsPreview != null)
            rootLayout.addView(newsPreview);

        // получаем основной контент для новости
        elements = contentRoot.getElementsByClass(mainClass);
        View newsMainContent = prepareNewsMainContent(pContext, elements.size() > 0 ? elements.get(0) : null);
        if (newsMainContent != null)
            rootLayout.addView(newsMainContent);

        return rootLayout;
    }

    private View prepareNewsPreview(Context pContext, Element newsPreview) {
        TextView previewLayout = null;
        if (newsPreview != null) {
            String content = newsPreview.html();
            if (content != null && !content.isEmpty()) {
                previewLayout = new TextView(pContext);
                int verticalPadding = pContext.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
                int horizontalSmallPadding = pContext.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_small_margin);
                int verticalSmallPadding = pContext.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_small_margin);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,0,0, verticalPadding);
                previewLayout.setLayoutParams(layoutParams);
                previewLayout.setBackground(pContext.getResources().getDrawable(R.drawable.news_preview_background));
                previewLayout.setPadding(horizontalSmallPadding, verticalSmallPadding, horizontalSmallPadding, verticalSmallPadding);
                previewLayout.setTextColor(pContext.getResources().getColor(R.color.fullNewsPreviewTextColor));
                previewLayout.setText(Html.fromHtml(content));
            }
        }
        return previewLayout;
    }

    private View prepareNewsMainContent(Context pContext, Element newsPreview) {
        TextView previewLayout = null;
        if (newsPreview != null) {
            String content = newsPreview.html();
            if (content != null && !content.isEmpty()) {
                previewLayout = new TextView(pContext);
                previewLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                previewLayout.setText(Html.fromHtml(content));
            }
        }
        return previewLayout;
    }
}
