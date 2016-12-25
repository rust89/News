package ru.sike.lada.utils;

public class GlobalValues {

    /**
     * Флажок показывающий был ли произведен запрос на обновление категорий новостей
     */
    private boolean mNewsCategoriesWasRequested;

    public GlobalValues() {
        mNewsCategoriesWasRequested = false;
    }

    public boolean getNewsCategoriesWasRequested() {
        return mNewsCategoriesWasRequested;
    }
    public void setNewsCategoriesWasRequested(boolean pValue) {
        mNewsCategoriesWasRequested = pValue;
    }
}
