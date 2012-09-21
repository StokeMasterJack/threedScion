package c3i.core.imageModel.server;

import c3i.core.imageModel.shared.PngShortSha;

public interface BlinkChecker {
    boolean isBlinkPng(PngShortSha shortSha);
}
