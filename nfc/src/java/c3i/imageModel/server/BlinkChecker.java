package c3i.imageModel.server;

import c3i.imageModel.shared.PngShortSha;

public interface BlinkChecker {
    boolean isBlinkPng(PngShortSha shortSha);
}
