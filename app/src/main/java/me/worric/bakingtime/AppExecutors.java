package me.worric.bakingtime;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppExecutors {

    private final Executor mDiskIo;

    private final MainThreadExecutor mMainThreadExecutor;

    @Inject
    public AppExecutors() {
        mDiskIo = Executors.newSingleThreadExecutor();
        mMainThreadExecutor = new MainThreadExecutor();
    }

    public Executor diskIO() {
        return mDiskIo;
    }

    public MainThreadExecutor mainThread() {
        return mMainThreadExecutor;
    }

    public static class MainThreadExecutor {

        private final Handler mHandler;

        public MainThreadExecutor() {
            mHandler = new Handler(Looper.getMainLooper());
        }

        public void execute(Runnable runnable) {
            mHandler.post(runnable);
        }
    }

}
