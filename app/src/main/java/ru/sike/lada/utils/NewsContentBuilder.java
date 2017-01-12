package ru.sike.lada.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.List;

import ru.sike.lada.R;
import ru.sike.lada.utils.parsing.ImageViewItem;
import ru.sike.lada.utils.parsing.PreviewDecorator;
import ru.sike.lada.utils.parsing.SimpleTextDecorator;
import ru.sike.lada.utils.parsing.TextViewItem;
import ru.sike.lada.utils.parsing.abstraction.ViewItem;
import ru.sike.lada.utils.parsing.abstraction.ViewItemList;

public class NewsContentBuilder {

    private static final String LOG_TAG = "NewsContentBuilder";

    private static final String rootClass = "mbwrap";
    private static final String previewClass = "mblead";
    private static final String mainClass = "mbcontent";

    private static final String divTagName = "div";
    private static final String linkTagName = "a";
    private static final String imgTagName = "img";
    private static final String brTagName = "br";
    private static final String frameTagName = "iframe";

    private static final String linkHRefAttribute = "href";
    private static final String imgSrcAttribute = "src";

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
        ViewItemList viewList = new ViewItemList();

        // контейнер с содержимым новости
        Element contentRoot = elements.size() > 0 ? elements.get(0) : null;
        if (contentRoot == null)
            return rootLayout;

        // получаем превью для новости
        elements = contentRoot.getElementsByClass(previewClass);
        if (elements.size() > 0) {
            Element previewElement = elements.get(0);
            viewList.add(new TextViewItem(pContext, previewElement.html(), new PreviewDecorator(pContext)));
        }
        // получаем основной контент для новости
        elements = contentRoot.getElementsByClass(mainClass);
        viewList.addAll(parseNewsContent(pContext, elements.size() > 0 ? elements.get(0) : null));

        for (ViewItem item : viewList)
            rootLayout.addView(item.getView());

        View newsMainContent = prepareNewsMainContent(pContext, elements.size() > 0 ? elements.get(0) : null);
        if (newsMainContent != null)
            rootLayout.addView(newsMainContent);

        return rootLayout;
    }

    private View prepareNewsMainContent(Context pContext, Element newsPreview) {
        TextView previewLayout = null;
        if (newsPreview != null) {
            String content = newsPreview.html();
            if (content != null && !content.isEmpty()) {
                previewLayout = new TextView(pContext);
                previewLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                previewLayout.setBackgroundColor(Color.parseColor("#00ff00"));
                previewLayout.setText(content);
            }
        }
        return previewLayout;
    }

    private ViewItemList parseNewsContent(Context pContext, Element pContent) {
        ViewItemList result = new ViewItemList();
        if (pContent == null)
            return result;

        List<Node> contentChilds = pContent.childNodes();
        // перебираем элементы контента
        String plainTextBuffer = "";
        for (Node child : contentChilds) {
            if (child instanceof TextNode) {
                String plainText = ((TextNode) child).text();
                if (!plainText.isEmpty())
                    plainTextBuffer += plainText;
            } else if (child instanceof Element) {
                Element element = (Element) child;
                if (divTagName.equals(element.nodeName())) {
                    Element divFirstChild = element.children().first();
                    if (divFirstChild != null) {
                        if (linkTagName.equals(divFirstChild.tagName())) {
                            // получаем ссылку
                            String linkHRef = divFirstChild.hasAttr(linkHRefAttribute) ? divFirstChild.attr(linkHRefAttribute) : "";
                            // проверяем на картинку
                            Element linkFirstChild = divFirstChild.children().first();
                            if (imgTagName.equals(linkFirstChild.tagName())) {
                                // получаем ссылку на картинку
                                String imgSrc = linkFirstChild.hasAttr(imgSrcAttribute) ? linkFirstChild.attr(imgSrcAttribute) : "";
                                result.add(new ImageViewItem(pContext, imgSrc, linkHRef));
                            }
                        }
                    } else {
                        String divContent = element.html().trim();
                        if (!divContent.isEmpty())
                            result.add(new TextViewItem(pContext, divContent, new SimpleTextDecorator(pContext)));
                    }
                } else if (brTagName.equals(element.nodeName())) {
                    if (!plainTextBuffer.isEmpty()) {
                        result.add(new TextViewItem(pContext, plainTextBuffer, new SimpleTextDecorator(pContext)));
                        plainTextBuffer = "";
                    }
                } else if (frameTagName.equals(element.nodeName())) {
                    result.add(new TextViewItem(pContext, "Здесь iframe", new SimpleTextDecorator(pContext)));
                } else {
                    plainTextBuffer += element.outerHtml();
                }
            }
        }

        if (!plainTextBuffer.isEmpty()) {
            result.add(new TextViewItem(pContext, plainTextBuffer, new SimpleTextDecorator(pContext)));
        }

        return result;
    }
}
