package com.tms.threed.threedFramework.repo.server;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.repo.server.rt.RtRepo;
import com.tms.threed.threedFramework.repo.shared.CommitId;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.repo.shared.SeriesNamesWithYears;
import com.tms.threed.threedFramework.threedCore.shared.SeriesId;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.lang.shared.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectId;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class Repos {

    private final File repoBaseDir;

    private final ConcurrentMap<SeriesKey, SeriesRepo> seriesRepoCache;

    private final RtConfigHelper rtConfigHelper;

    public Repos(final File repoBaseDir) {
        Preconditions.checkNotNull(repoBaseDir);
        this.repoBaseDir = repoBaseDir;

        if (!repoBaseDir.exists()) {
            throw new IllegalStateException("repoBaseDir [" + repoBaseDir + "] does not exist");
        }

        seriesRepoCache = new MapMaker()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .makeComputingMap(
                        new Function<SeriesKey, SeriesRepo>() {
                            public SeriesRepo apply(SeriesKey seriesKey) {
                                return new SeriesRepo(repoBaseDir, seriesKey);
                            }
                        });

        rtConfigHelper = new RtConfigHelper(repoBaseDir);


    }

    public SeriesRepo getSeriesRepo(String seriesName, int seriesYear) {
        return getSeriesRepo(new SeriesKey(seriesYear, seriesName));
    }

    public SeriesRepo getSeriesRepo(SeriesKey seriesKey) {
        Preconditions.checkNotNull(seriesKey);


        SeriesRepo seriesRepo = seriesRepoCache.get(seriesKey);
//        if (seriesRepo == null) {
//            seriesRepo = new SeriesRepo(repoBaseDir, seriesKey);
//            seriesRepoCache.put(seriesKey, seriesRepo);
//        }
        return seriesRepo;
    }

    public RtConfigHelper getRtConfigHelper() {
        return rtConfigHelper;
    }

    public RtConfig getRtConfig(){
        return rtConfigHelper.read();
    }

    public ThreedModel getThreedModel(SeriesKey seriesKey, RootTreeId rootTreeId) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        return seriesRepo.getThreedModel(rootTreeId);
    }

    public ThreedModel getThreedModel(String seriesName, int seriesYear, RootTreeId rootTreeId) {
        SeriesKey seriesKey = new SeriesKey(seriesYear, seriesName);
        return getThreedModel(seriesKey, rootTreeId);
    }

    public ThreedModel getThreedModelForHead(SeriesKey seriesKey) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        return seriesRepo.getThreedModelHead();
    }

    public ThreedModel getThreedModel(String seriesName, int seriesYear) {
        SeriesKey seriesKey = new SeriesKey(seriesYear, seriesName);
        return getThreedModelForHead(seriesKey);
    }

    public FeatureModel getFeatureModel(SeriesKey seriesKey) {
        return getThreedModelForHead(seriesKey).getFeatureModel();
    }

    public void resetCache() {
        seriesRepoCache.clear();
    }

    public File getRepoBaseDir() {
        return repoBaseDir;
    }

    public List<String> getRepoNames() {
        Set<String> seriesNames = new HashSet<String>();
        List<SeriesKey> seriesKeys = getSeriesKeys();

        for (SeriesKey seriesKey : seriesKeys) {
            seriesNames.add(seriesKey.getName());
        }


        ArrayList<String> a = new ArrayList<String>();

        a.addAll(seriesNames);

        Collections.sort(a);

        return a;
    }

    public List<Integer> getYearsForSeries(String seriesName) {
        ArrayList<Integer> a = new ArrayList<Integer>();


        List<SeriesKey> seriesKeys = getSeriesKeys();
        for (SeriesKey seriesKey : seriesKeys) {
            if (seriesKey.getName().equalsIgnoreCase(seriesName)) {
                a.add(seriesKey.getYear());
            }
        }

        Collections.sort(a);

        return a;
    }

    public ArrayList<SeriesNamesWithYears> getSeriesNamesWithYears() {
        File repoBaseDir = getRepoBaseDir();
        if (repoBaseDir == null) throw new IllegalStateException();
        File[] seriesNameDirs = repoBaseDir.listFiles(seriesDirFilter);

        if (seriesNameDirs == null) {
            throw new IllegalStateException("repoBaseDir[" + repoBaseDir + "] which is defined in web.xml contains no child directories. ");
        }


        ArrayList<SeriesNamesWithYears> seriesNamesWithYears = new ArrayList<SeriesNamesWithYears>();
        for (File seriesNameDir : seriesNameDirs) {
            String seriesName = seriesNameDir.getName();
            File[] yearDirs = seriesNameDir.listFiles(seriesDirFilter);

            SeriesNamesWithYears seriesNameWithYears = new SeriesNamesWithYears();
            seriesNameWithYears.setSeriesName(seriesName);

            for (File yearDir : yearDirs) {


                String sSeriesYear = yearDir.getName();
                try {
                    Integer seriesYear = new Integer(sSeriesYear);
                    seriesNameWithYears.addYear(seriesYear);
                } catch (NumberFormatException e) {
                    log.warn("Found a non-int directory name [" + sSeriesYear + "] for a seriesYear in folder [" + seriesNameDir + "]");
                }

            }

            seriesNamesWithYears.add(seriesNameWithYears);


        }
        return seriesNamesWithYears;
    }

    private static Log log = LogFactory.getLog(Repos.class);

    public List<SeriesKey> getSeriesKeys() {
        File repoBaseDir = getRepoBaseDir();
        if (repoBaseDir == null) throw new IllegalStateException();
        File[] yearDirs = repoBaseDir.listFiles(seriesDirFilter);

        if (yearDirs == null) {
            throw new IllegalStateException("repoBaseDir[" + repoBaseDir + "] which is defined in web.xml contains no child directories. ");
        }


        List<SeriesKey> seriesKeys = new ArrayList<SeriesKey>();
        for (File yearDir : yearDirs) {

            File[] seriesDirs = yearDir.listFiles(seriesDirFilter);

            for (File seriesDir : seriesDirs) {
                String seriesRepoName = seriesDir.getName();


                String seriesName = seriesDir.getName();
                String seriesYear = yearDir.getName();

                SeriesKey seriesKey = new SeriesKey(seriesYear, seriesName);

                seriesKeys.add(seriesKey);
            }


        }
        return seriesKeys;
    }

    private static FileFilter seriesDirFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isHidden()) return false;
            if (f.getName().startsWith(".")) return false;
            if (!f.isDirectory()) return false;
            String name = f.getName();
            if (Strings.containsWhitespace(name)) return false;
            if (Strings.containsNonWordChar(name)) return false;
            return true;
        }
    };

    public File getFileForJpg(JpgId jpgId) {
        SeriesRepo seriesRepo = getSeriesRepo(jpgId.getSeriesKey());
        RtRepo genRepo = seriesRepo.getRtRepo();
        return genRepo.getJpgFileName(jpgId);
    }


//    public ObjectLoader getLoaderForPng(SeriesKey seriesKey, String shortSha) {
//        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
//        RevisionParameter revisionParameter = new RevisionParameter(shortSha);
//
//
//
//        return seriesRepo.getSrcRepo().getRepoObject(revisionParameter);
//    }

//    public ObjectLoader getLoaderForPng(String seriesName, int seriesYear, String shortSha) {
//        SeriesKey seriesKey = new SeriesKey(seriesName, seriesName);
//        return getLoaderForPng(seriesKey, shortSha);
//    }

//    public ObjectLoader getLoaderForPng(ImPng png) {
//        ImView imView = png.getView();
//        ImSeries series = imView.getSeries();
//        SeriesKey seriesKey = series.getSeriesInfo().getSeriesKey();
//        String shortSha = png.getShortSha();
//        return getLoaderForPng(seriesKey, shortSha);
//    }

    public SeriesId getHead(SeriesKey seriesKey) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        RootTreeId rootTreeId = seriesRepo.getSrcRepo().resolveHeadRootTreeId();
        return new SeriesId(seriesKey, rootTreeId);
    }

    public ThreedModel getThreedModel(SeriesId seriesId) {
        SeriesKey seriesKey = seriesId.getSeriesKey();
        return getThreedModel(seriesKey, seriesId.getRootTreeId());
    }

    public void createGenVersionDirs(SeriesId seriesId, JpgWidth jpgWidth) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesId.getSeriesKey());
        seriesRepo.createGenVersionDir(seriesId, jpgWidth);
    }


    public boolean isValidJpgWidth(JpgWidth width) {
        return width.isStandard() || getRtConfig().getJpgWidths().contains(width);
    }
}
