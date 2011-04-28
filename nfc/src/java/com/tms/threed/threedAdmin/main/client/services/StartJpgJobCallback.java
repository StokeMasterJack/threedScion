package com.tms.threed.threedAdmin.main.client.services;

import com.tms.threed.threedFramework.repo.shared.JpgWidth;

public interface StartJpgJobCallback {
    void onSuccess(String text,String commitId,JpgWidth jpgWidth);
}
