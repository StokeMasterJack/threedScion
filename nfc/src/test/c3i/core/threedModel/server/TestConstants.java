package c3i.core.threedModel.server;

import c3i.core.common.shared.BrandKey;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.Map;

public interface TestConstants {

    File TOYOTA_REPO_BASE_DIR = new File("/configurator-content-toyota");
    File SCION_REPO_BASE_DIR = new File("/configurator-content-scion");

    Map<BrandKey, File> REPO_BASE_DIR_MAP = ImmutableMap.of(BrandKey.TOYOTA, TOYOTA_REPO_BASE_DIR, BrandKey.SCION, TOYOTA_REPO_BASE_DIR);


}
