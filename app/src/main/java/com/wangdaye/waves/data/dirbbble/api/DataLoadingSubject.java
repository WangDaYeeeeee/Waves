package com.wangdaye.waves.data.dirbbble.api;

/**
 * An interface for classes offering data loading state to be observed.
 */
public interface DataLoadingSubject {
    boolean isDataLoading();
    void registerCallback(DataLoadingCallbacks callbacks);
    void unregisterCallback(DataLoadingCallbacks callbacks);

    interface DataLoadingCallbacks {
        void dataStartedLoading();
        void dataFinishedLoading();
    }
}
