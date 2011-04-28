package com.tms.threed.threedAdmin.main.client.services;

import com.tms.threed.threedFramework.jpgGen.shared.ExecutorStatus;

import java.util.List;

public interface FetchQueueDetailsCallback {
    void onSuccess(List<ExecutorStatus> queueDetails);

    void onError(String text);

    void badJobId();
}
