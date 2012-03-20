package com.tms.threed.threedCore.imageModel.server;

import com.tms.threed.threedCore.imageModel.shared.PngShortSha;

public interface BlinkChecker {
    boolean isBlinkPng(PngShortSha shortSha);
}
