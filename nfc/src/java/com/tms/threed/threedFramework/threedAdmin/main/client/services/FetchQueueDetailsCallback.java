package com.tms.threed.threedFramework.threedAdmin.main.client.services;

import com.tms.threed.threedFramework.jpgGen.shared.ExecutorStatus;

import java.util.List;

public interface FetchQueueDetailsCallback {
    void onSuccess(List<ExecutorStatus> queueDetails);

    void onError(String text);

    void badJobId();
}
