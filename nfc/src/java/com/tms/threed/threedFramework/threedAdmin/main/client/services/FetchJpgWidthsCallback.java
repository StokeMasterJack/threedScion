package com.tms.threed.threedFramework.threedAdmin.main.client.services;

import com.tms.threed.threedFramework.repo.shared.JpgWidth;

import java.util.List;

public interface FetchJpgWidthsCallback {

    void onSuccess(List<JpgWidth> jpgWidths);
}
