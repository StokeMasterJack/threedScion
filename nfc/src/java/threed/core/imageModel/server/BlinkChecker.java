package threed.core.imageModel.server;

import threed.core.imageModel.shared.PngShortSha;

public interface BlinkChecker {
    boolean isBlinkPng(PngShortSha shortSha);
}
