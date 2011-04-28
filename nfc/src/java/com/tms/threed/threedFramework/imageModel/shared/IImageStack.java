package com.tms.threed.threedFramework.imageModel.shared;

import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.List;

public interface IImageStack {

    Path getImageBase();

    Path getJpgUrl();

    List<Path> getUrlsJpgMode(boolean includeZPngs);
    List<Path> getUrlsJpgMode();

    List<Path> getUrlsPngMode();


    List<IPng> getAllPngs();
}
