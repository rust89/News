package ru.sike.lada.utils.caching;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.os.Process;
import android.util.Log;


public class CacheExecutorProvider {

    private static final String LOG_TAG = "CacheExecutorProvider";

    /*
    * Определяем количество доступных процессоров для создания
    */
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    /*
    * Центральный пул потоков
    */
    private final ThreadPoolExecutor mForBackgroundTasks;

    /*
    *
    * */
    private final ArrayList<CacheTask> mInWork;
    /*
    * синглтон объект
    */
    private static CacheExecutorProvider sInstance;

    /*
    * Возвращает синглтон
    */
    private static CacheExecutorProvider getInstance() {
        if (sInstance == null) {
            synchronized (CacheExecutorProvider.class) {
                sInstance = new CacheExecutorProvider();
            }
        }
        return sInstance;
    }

    /*
    * Очевидный дефолтный конструктор очевиден
    * */
    private CacheExecutorProvider() {
        mForBackgroundTasks = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND)
        );
        mInWork = new ArrayList<>();
    }

    public static void Execute(Context pContext, CacheTask pTask) {
        Execute(pContext, pTask, null);
    }

    public static void Execute(Context pContext, CacheTask pTask, Object pMonitor) {
        CacheExecutorProvider instance = getInstance();
        // добавляем новую задачу только если в очереди уже нет аналогичной
        if (instance.addInWork(pTask))
            instance.mForBackgroundTasks.execute(new ParameterizedRunnable(pContext, instance, pTask, pMonitor));
    }

    private boolean addInWork(final CacheTask pTask) {
        synchronized (mInWork) {
            for (int i = 0; i < mInWork.size(); i++)
                if (mInWork.get(i).equals(pTask))
                    return false;
            mInWork.add(pTask);
            return true;
        }
    }

    private void removeInWork(final CacheTask pTask) {
        synchronized (mInWork) {
            for (int i = mInWork.size() - 1; i >= 0; i--)
                if (mInWork.get(i).equals(pTask))
                    mInWork.remove(i);
        }
    }

    private static class ParameterizedRunnable implements Runnable {

        private final CacheTask mTask;
        private final CacheExecutorProvider mExecutor;
        private final Context mContext;
        private final Object mMonitor;

        public ParameterizedRunnable(Context pContext, CacheExecutorProvider pExecutor, CacheTask pTask, Object pMonitor) {
            mExecutor = pExecutor;
            mTask = pTask;
            mContext = pContext.getApplicationContext();
            mMonitor = pMonitor;
        }

        @Override
        public void run() {
            if (mMonitor != null) {
                synchronized (mMonitor) {
                    CacheExecutor.Execute(mContext, mTask);
                    mExecutor.removeInWork(mTask);
                    mMonitor.notifyAll();
                }
            } else {
                CacheExecutor.Execute(mContext, mTask);
                mExecutor.removeInWork(mTask);
            }
        }
    }

}
