package c3i.repo.server;

import c3i.core.threedModel.shared.CommitId;
import c3i.featureModel.shared.common.FullSha;
import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.repo.shared.CommitHistory;
import c3i.repo.shared.RepoHasNoHeadException;
import c3i.repo.shared.RevisionParameter;
import c3i.repo.shared.TagCommit;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import org.eclipse.jgit.JGitText;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.errors.UnmergedPathException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.storage.file.FileRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static smartsoft.util.shared.Strings.isEmpty;

public class SrcRepo {

    private final File vtcBaseDir;
    private final File srcRepoDir;
    private final File srcWorkDir;
    private final SeriesKey seriesKey;

    private final FileRepository repo;

    public SrcRepo(File vtcBaseDir, File srcRepoDir, File srcWorkDir, SeriesKey seriesKey) {

        assert vtcBaseDir != null;
        assert srcRepoDir != null;
        assert srcWorkDir != null;
        assert seriesKey != null;

        this.vtcBaseDir = vtcBaseDir;
        this.srcRepoDir = srcRepoDir;
        this.srcWorkDir = srcWorkDir;
        this.seriesKey = seriesKey;


        try {
            repo = new FileRepository(this.srcRepoDir);

        } catch (Exception e) {
            throw new RepoException("Problems reading srcRepoDir [" + srcRepoDir + "]", e);
        }

        createGitRepoIfNeeded();
        fixupGitConfigIfNeeded();
        fixupGitAttributesIfNeeded();
        fixupGitIgnoreIfNeeded();


    }

    protected void createGitRepoIfNeeded() {
        if (!srcRepoDir.exists()) {
            try {
                repo.create();
            } catch (IOException e) {
                throw new RepoException("Problems initializing new srcRepoDir [" + srcRepoDir + "]", e);
            }
        }
    }

    public String getLocalResource(String localName) {
        URL configFileUrl = Resources.getResource(this.getClass(), localName);
        try {
            return Resources.toString(configFileUrl, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getExpectedGitConfigFile() {
        return getLocalResource("expectedGitConfigFile.txt");
    }

    public String getExpectedGitAttributesFile() {
        return getLocalResource("expectedGitAttributesFile.txt");
    }

    public String getExpectedGitIgnoreFile() {
        return getLocalResource("expectedGitIgnoreFile.txt");
    }

    private void fixupGitConfigIfNeeded() {

        String expected = getExpectedGitConfigFile();

        FileBasedConfig config = repo.getConfig();
        String actual = config.toText();

        boolean needsFixup = !expected.equals(actual);

        if (needsFixup) {
            try {
                config.fromText(expected);
            } catch (ConfigInvalidException e) {
                throw new RuntimeException("Problem with ExpectedGitConfigFile");
            }
            try {
                config.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void fixupGitAttributesIfNeeded() {

        String expected = getExpectedGitAttributesFile();
        String actual;

        File file = new File(srcWorkDir, ".gitattributes");
        if (file.exists()) {
            try {
                actual = Files.toString(file, Charsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            actual = null;
        }

        boolean needsFixup = !expected.equals(actual);

        if (needsFixup) {
            try {
                Files.write(expected, file, Charsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fixupGitIgnoreIfNeeded() {

        String expected = getExpectedGitIgnoreFile();
        String actual;

        File file = new File(srcWorkDir, ".gitignore");
        if (file.exists()) {
            try {
                actual = Files.toString(file, Charsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            actual = null;
        }

        boolean needsFixup = !expected.equals(actual);

        if (needsFixup) {
            try {
                Files.write(expected, file, Charsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public String getTagFromCommitId(CommitId commitId) {
        ObjectId objectId = ObjectId.fromString(commitId.getName());
        return getTagFromCommitId(objectId);
    }

    public String getTagFromCommitId(ObjectId commitId) {
        Map<String, Ref> tags = getTags();
        for (Map.Entry<String, Ref> e : tags.entrySet()) {
            String shortTagName = e.getKey();
            Ref ref = e.getValue();
            ObjectId objectId = ref.getObjectId();

            if (objectId.equals(commitId)) {
                return shortTagName;
            }

        }

        return null;
    }

    private ObjectId resolveGitObjectId(RevisionParameter revisionParameter) throws UnableToResolveRevisionParameterException {
        ObjectId oid;
        try {
            oid = repo.resolve(revisionParameter.stringValue());
        } catch (Exception e) {
            throw new UnableToResolveRevisionParameterException("Error resolving revisionParameter to an ObjectId", e);
        }

        if (oid == null)
            throw new UnableToResolveRevisionParameterException("Unable to resolve revisionParameter, [" + revisionParameter + "], to an ObjectId", null);

        return oid;
    }

    public RootTreeId resolveRootTreeId(@Nullable String revisionParameter) throws UnableToResolveRevisionParameterException {
        ObjectId commitId;
        if (revisionParameter == null) {
            commitId = resolveCommitHead();
        } else {
            commitId = resolve(new RevisionParameter(revisionParameter));
        }
        RevCommit revCommit = getRevCommitEager(commitId);
        RevTree tree = revCommit.getTree();
        return new RootTreeId(tree.getName());
    }

    public ObjectId resolve(String revisionParameter) throws UnableToResolveRevisionParameterException {
        return resolve(new RevisionParameter(revisionParameter));
    }

    public ObjectId resolve(RevisionParameter revisionParameter) throws UnableToResolveRevisionParameterException {
        return resolveGitObjectId(revisionParameter);
    }

    public ObjectId resolveCommitHead() throws UnableToResolveRevisionParameterException {
        return resolve(RevisionParameter.HEAD_REVISION_PARAMETER);
    }


    public RootTreeId resolveHeadRootTreeId() {
        ObjectId commitId = resolveCommitHead();
        RevCommit revCommit = getRevCommitEager(commitId);
        RevTree tree = revCommit.getTree();
        return new RootTreeId(tree.getName());
    }

    @Nonnull
    public ObjectLoader getRepoObject(ObjectId objectId) throws UnableToResolveRevisionParameterException {

        try {
            ObjectLoader loader = repo.open(objectId);
            if (loader == null) throw new UnableToResolveRevisionParameterException("ObjectLoader was null", null);
            return loader;
        } catch (Exception e) {
            throw new UnableToResolveRevisionParameterException("Error opening ObjectLoader", e);
        }
    }

    public byte[] getRepoObjectAsBytes(ObjectId objectId) throws UnableToResolveRevisionParameterException {
        ObjectLoader loader = getRepoObject(objectId);
        byte[] bytes = loader.getBytes();
        return bytes;
    }

    public void close() {
        repo.close();
    }

    /**
     * @return null nothing if nothing has been committed
     */
    public List<TagCommit> getTagCommits() {


        ArrayList<TagCommit> a = new ArrayList<TagCommit>();

        Map<String, Ref> tags = getTags();

        boolean isHeadTagged = false;

        ObjectId headId;
        try {
            headId = repo.resolve(Constants.HEAD);
            if (headId == null) return null;
        } catch (IOException e) {
            log.severe("Problem resolving HEAD commit for repo[" + seriesKey + "]");
            throw new RuntimeException(e);
        }

        for (String shortTagName : tags.keySet()) {
            Ref ref = tags.get(shortTagName);
            Ref leaf = ref.getLeaf();
            ObjectId commitId = leaf.getObjectId();
            boolean head = headId.equals(commitId);
            if (head) {
                isHeadTagged = true;
            }

            try {
                int type = repo.open(commitId).getType();
                if (type != Constants.OBJ_COMMIT) continue;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            RevCommit revCommit = getRevCommitEager(commitId);
            RevTree revTree = revCommit.getTree();

            String treeId = revTree.getName();

            TagCommit tagCommit = new TagCommit(shortTagName, commitId.name(), treeId, head);
            a.add(tagCommit);
        }

        if (!isHeadTagged) {
            RevCommit revCommit = getRevCommitEager(headId);
            RevTree revTree = revCommit.getTree();
            TagCommit h = new TagCommit("HEAD", headId.name(), revTree.getName(), true);
            a.add(h);
        }


        Collections.sort(a, new Comparator<TagCommit>() {
            @Override
            public int compare(TagCommit t1, TagCommit t2) {

                String n1 = t1.getTagShortName();
                String n2 = t2.getTagShortName();

                if (n1.equalsIgnoreCase("HEAD")) n1 = "__HEAD";
                if (n2.equalsIgnoreCase("HEAD")) n2 = "__HEAD";

                return n1.compareTo(n2);
            }
        });


        return a;

    }

    @Nonnull
    public CommitHistory getHeadCommitHistory() {
        ObjectId headId;
        try {
            headId = resolveCommitHead();
        } catch (UnableToResolveRevisionParameterException e) {
            throw new RepoHasNoHeadException();
        }
        return getCommitHistory(headId, true);
    }

    @Nonnull
    public CommitHistory getCommitHistory(ObjectId startCommitId) {
        return getCommitHistory(startCommitId, null);
    }

    @Nonnull
    public CommitHistory getCommitHistory(ObjectId startCommitId, @Nullable Boolean isHead) {

        boolean h;
        if (isHead == null) {
            h = this.isHead(startCommitId);
        } else {
            h = isHead;
        }

        RevCommit startRevCommit = getRevCommitEager(startCommitId);
        if (startRevCommit == null) {
            log.severe("Problem retrieving RevCommit for repo[" + seriesKey + "]");
            throw new RuntimeException("Problem retrieving RevCommit for repo[" + seriesKey + "]  getRevCommitEager returned null");
        }

        TagMap tagMap = this.getTagMap();
        RevCommit revCommit = startRevCommit;

        final RootTreeId vtcRootTreeId = getVtcRootTreeId();
        CommitHistory commitDetail = this.toCommitDetail(revCommit, tagMap, h, vtcRootTreeId);

        return commitDetail;

    }

    public RootTreeId getVtcRootTreeId() {
        File vtcFile = getVtcFile();

        if (vtcFile.exists()) {
            String sha;
            try {
                sha = Files.toString(vtcFile, Charset.defaultCharset());
                if (isEmpty(sha))
                    throw new RuntimeException("vtcVersion file [" + vtcFile.getAbsolutePath() + "] contains and empty sha");
                if (!ObjectId.isId(sha))
                    throw new RuntimeException("vtcVersion file [" + vtcFile.getAbsolutePath() + "] contains an invalid sha [" + sha + "].");
                return new RootTreeId(sha);
            } catch (IOException e) {
                throw new RuntimeException("Problem reading vtcVersion from file [" + vtcFile.getAbsolutePath() + "]", e);
            }
        } else {
            log.warning("No vtc file for [" + this.seriesKey + "]");

            RootTreeId rootTreeId = resolveHeadRootTreeId();
            setVtcRootTreeId(rootTreeId);
            return rootTreeId;
        }

    }

    public File getVtcFile() {
        String fileName = seriesKey.getSeriesName() + "-" + seriesKey.getYear() + ".txt";
        return new File(vtcBaseDir, fileName);
    }

    public void setVtcRootTreeId(RootTreeId rootTreeId) {
        File vtcFile = getVtcFile();
        try {
            Files.write(rootTreeId.getName(), vtcFile, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static class TagMapBuilder {

        private Map<ObjectId, Set<String>> map = new HashMap<ObjectId, Set<String>>();

        public void add(ObjectId commitId, String tag) {
            Set<String> tags = map.get(commitId);
            if (tags == null) {
                tags = new HashSet<String>();
                map.put(commitId, tags);
            }
            tags.add(tag);
        }

        public void add(Map<String, Ref> tags, FileRepository repo) {
            for (String tagName : tags.keySet()) {
                Ref ref = tags.get(tagName);
                Ref leaf = ref.getLeaf();
                ObjectId commitId = leaf.getObjectId();

                try {
                    int type = repo.open(commitId).getType();
                    if (type != Constants.OBJ_COMMIT) continue;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                add(commitId, tagName);
            }
        }

        private ImmutableMap<ObjectId, Set<String>> buildInternal() {
            final ImmutableMap.Builder<ObjectId, Set<String>> builder = ImmutableMap.builder();
            return builder.putAll(map).build();
        }

        public TagMap build() {
            final ImmutableMap<ObjectId, Set<String>> m = buildInternal();
            return new TagMap(m);
        }

    }

    public static class TagMap {

        private final ImmutableMap<ObjectId, Set<String>> map;

        public TagMap(ImmutableMap<ObjectId, Set<String>> map) {
            this.map = map;
        }

        public Set<String> getTags(ObjectId commitId) {
            return map.get(commitId);
        }
    }

    public TagMap getTagMap() {
        final Map<String, Ref> tags = getTags();
        TagMapBuilder tagMapBuilder = new TagMapBuilder();
        tagMapBuilder.add(tags, repo);
        return tagMapBuilder.build();
    }

    private CommitHistory toCommitDetail(RevCommit revCommit, TagMap tagMap, boolean isHead, RootTreeId vtcRootTreeId) {


        final RevTree revTree = revCommit.getTree();
        CommitHistory commitDetail = new CommitHistory(isHead);
        commitDetail.setCommitId(new CommitId(revCommit.getName()));


        commitDetail.setRootTreeId(revTree == null ? null : (new RootTreeId(revTree.getName())));
        commitDetail.setTags(tagMap.getTags(revCommit));
        commitDetail.setCommitTime(revCommit.getCommitTime());
        commitDetail.setShortMessage(revCommit.getShortMessage());
        commitDetail.setHead(isHead);
        commitDetail.setCommitter(revCommit.getCommitterIdent().getName());

        if (commitDetail.getRootTreeId().equals(vtcRootTreeId)) {
            commitDetail.setVtc(true);
        } else {
            commitDetail.setVtc(false);
        }

        final RevCommit[] parentRevCommits = revCommit.getParents();
        final CommitHistory[] parentCommitDetails = new CommitHistory[parentRevCommits.length];

        for (int i = 0; i < parentRevCommits.length; i++) {
            RevCommit parentRevCommit = parentRevCommits[i];
            CommitHistory parentCommitDetail = toCommitDetail(parentRevCommit, tagMap, false, vtcRootTreeId);
            parentCommitDetails[i] = parentCommitDetail;
        }

        commitDetail.setParents(parentCommitDetails);

        return commitDetail;
    }


    public Map<String, Ref> getTags() {
        return repo.getTags();
    }

    public File getDir() {
        return srcRepoDir;
    }

    public FileRepository getGitRepo() {
        return repo;
    }

//    private RevCommit getRevCommit(CommitId commitId) {
//        ObjectLoader loader = this.getRepoObject(commitId);
//        return RevCommit.parse(loader.getBytes());
//    }

//    public RevCommit getRevCommitFromCommitId(CommitId commitId) {
//        ObjectId oid = RevCommit.fromString(commitId.stringValue());
//        ObjectLoader loader;
//        try {
//            loader = repo.open(oid);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return RevCommit.parse(loader.getBytes());
//    }

//    public RevCommit getRevCommit(ObjectId commitId) {
//        RevWalk revWalk = new RevWalk(repo);
//        return revWalk.lookupCommit(commitId);
//    }

    public RevCommit getRevCommit(AnyObjectId commitId) {
        RevWalk revWalk = new RevWalk(repo);
        RevCommit revCommit = revWalk.lookupCommit(commitId);
        revWalk.release();
        return revCommit;
    }

    public RevCommit getRevCommitEager(ObjectId startCommitId) {
        RevWalk walk = new RevWalk(repo);
        final RevCommit startRevCommit = walk.lookupCommit(startCommitId);

        try {
            walk.markStart(startRevCommit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        RevCommit revCommit;
        while (true) {
            try {
                revCommit = walk.next();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (revCommit == null) {
                break;
            }
        }

        walk.release();

        return startRevCommit;
    }


//    public RevCommit getRevCommitFromCommitId(ObjectId commitId) {
//        ObjectLoader loader;
//        try {
//            loader = repo.open(commitId);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        System.out.println("AA commitId = " + commitId);
//        byte[] bytes = loader.getBytes();
//        return RevCommit.parse(bytes);
//    }


    public Ref getRef(String name) {
        try {
            return repo.getRef(Constants.MASTER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void validateFullSha(String fullSha) {
        Preconditions.checkNotNull(fullSha, "Invalid fullSha[" + fullSha + "]. fullSha must be non-null");
        Preconditions.checkArgument(fullSha.length() == 40, "Invalid fullSha[" + fullSha + "]. fullSha must be 40 characters long");
        Preconditions.checkArgument(RevCommit.isId(fullSha));
    }

    public ObjectId tagCommit(String tag, CommitId commitId) {
        ObjectId objectId = toGitObjectId(commitId);
        tagCommit(tag, objectId);
        return objectId;
    }

    public void tagCommit(String tag, ObjectId commitId) {
        RevWalk revWalk = null;
        try {

            //grab commitId of HEAD
//            ObjectId objectId = repo.resolve(Constants.HEAD + "^{commit}");
//            if (objectId == null) {
//                throw new NoHeadException(JGitText.get().tagOnRepoWithoutHEADCurrentlyNotSupported);
//            }


            revWalk = new RevWalk(repo);

            String refName = Constants.R_TAGS + tag;
            RefUpdate tagRef = repo.updateRef(refName);
            tagRef.setNewObjectId(commitId);
            tagRef.setForceUpdate(false);
            tagRef.setRefLogMessage("tagged " + tag, false);

            RefUpdate.Result updateResult = tagRef.update(revWalk);


            switch (updateResult) {
                case NEW:
                case FORCED:
                    return;
                case LOCK_FAILURE:
                    throw new ConcurrentRefUpdateException(
                            JGitText.get().couldNotLockHEAD,
                            tagRef.getRef(), updateResult);
                default:
                    throw new JGitInternalException(MessageFormat.format(
                            JGitText.get().updatingRefFailed, refName,
                            tag, updateResult));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (revWalk != null) {
                revWalk.release();
            }
        }


    }


//    class SeriesCommitBuilder {
//
//        private final RevCommit revCommit;
//        private final Map<String, Ref> tags;
//
//        public SeriesCommitBuilder(RevCommit revCommit) {
//            this.revCommit = revCommit;
//            tags = getTags();
//        }
//
//        public SeriesCommit buildSeriesCommit() {
//            SeriesId seriesId = new SeriesId(seriesKey, revCommit.getId().getName());
//            String shortMessage = getShortMessage();
//            String fullMessage = getFullMessage();
//            SeriesCommit.Committer author = getAuthor();
//
//            String commitId = seriesId.getCommitId();
//
//            Ref ref = tags.get(commitId);
//
//
//            SeriesCommit seriesCommit = new SeriesCommit(seriesId, shortMessage, fullMessage, author, revCommit.getCommitTime(), getParentCommits());
//            return seriesCommit;
//        }
//
//        private String getShortMessage() {
//            try {
//                return revCommit.getShortMessage();
//            } catch (NullPointerException e) {
//                return null;
//            }
//        }
//
//
//        private String getFullMessage() {
//            try {
//                return revCommit.getFullMessage();
//            } catch (NullPointerException e) {
//                return null;
//            }
//        }
//
//        private SeriesCommit.Committer getAuthor() {
//            try {
//                PersonIdent authorIdent = revCommit.getAuthorIdent();
//                return new SeriesCommit.Committer(authorIdent.getEmailAddress());
//            } catch (NullPointerException e) {
//                return null;
//            }
//        }
//
//        private CommitId[] getParentCommits() {
//            RevCommit[] rParents = revCommit.getParents();
//            CommitId[] parentCommits;
//            if (rParents == null || rParents.length == 0) {
//                parentCommits = new CommitId[0];
//            } else {
//                parentCommits = new CommitId[rParents.length];
//                for (int i = 0; i < rParents.length; i++) {
//                    RevCommit rParent = revCommit.getParent(i);
//                    assert rParent != null;
//                    parentCommits[i] = new CommitId(revCommit);
//                }
//            }
//            return parentCommits;
//        }
//
//    }

//    public SeriesCommit getHeadSeriesCommit() {
//        CommitId commitId = resolveCommitHead();
//        return getSeriesCommit(commitId);
//    }
//
//    public SeriesCommit getSeriesCommit(ObjectId commitId) {
//        RevCommit revCommit = getRevCommit(commitId);
//        return getCommitFromRevCommit(revCommit);
//    }
//
//    public SeriesCommit getSeriesCommit(CommitId commitId) {
//        RevCommit revCommit = getRevCommit(commitId);
//        return getCommitFromRevCommit(revCommit);
//    }

//    public SeriesCommit getCommitFromRevCommit(RevCommit revCommit) {
//        Preconditions.checkNotNull(revCommit);
//        SeriesCommitBuilder builder = new SeriesCommitBuilder(revCommit);
//        return builder.buildSeriesCommit();
//    }


//    public TagCommit getTagCommit(ObjectId commitId) {
//        String tag = getTagFromCommitId(commitId);
//        if (tag == null) throw new IllegalArgumentException("This commitId is not tagged[" + commitId + "]");
//        boolean h = isHead(commitId);
//        return new TagCommit(tag, commitId.name(), h);
//    }
//
//    public void test1() throws Exception {
//
//    }

    public boolean isHead(CommitId commitId) {
        ObjectId objectId = toGitObjectId(commitId);
        return isHead(objectId);
    }

    public boolean isHead(ObjectId commitId) {
        if (commitId == null) return false;
        ObjectId objectId = resolveCommitHead();
        if (objectId == null) return false;
        return commitId.equals(objectId);
    }

    public void addAll() throws NoFilepatternException {
        log.info("Adding files to the repository[" + seriesKey + "]");
        Git git = new Git(repo);
        AddCommand addCommand = git.add().addFilepattern(".");
        addCommand.call();
        log.info("Add complete for [" + seriesKey + "]");
    }

    public void test1() throws Exception {
        final DirCache dirCache = repo.lockDirCache();

        dirCache.unlock();

    }

    public RevCommit commitAll(String commitMessage) throws UnmergedPathException, NoHeadException, NoMessageException, ConcurrentRefUpdateException, WrongRepositoryStateException {
        log.info("Committing to the repository[" + seriesKey + "]");
        Git git = new Git(repo);

        CommitCommand commit = git.commit()
                .setMessage(commitMessage)
                .setAll(true);

        RevCommit revCommit = commit.call();
        log.info("Commit complete for [" + seriesKey + "] - new commitId[" + revCommit.getName() + "]");
        return revCommit;
    }

    public RevCommit addAllAndCommit(String commitMessage) throws NoFilepatternException, NoHeadException, UnmergedPathException, NoMessageException, ConcurrentRefUpdateException, WrongRepositoryStateException {
        log.fine("addAllAndCommit   seriesKey[" + seriesKey + "]  commitMessage[" + commitMessage + "]");

        log.fine("\t addAll....");
        addAll();
        log.fine("\t addAll complete!");

        log.fine("\t commitAll....");
        RevCommit revCommit = commitAll(commitMessage);
        log.fine("\t commitAll complete!");


        return revCommit;
    }

//    public RevCommit addAllCommitAndTag(String commitMessage, String newTagName) throws Exception {
//        Preconditions.checkNotNull(newTagName);
//        RevCommit revCommit = addAllAndCommit(commitMessage);
//        tag(newTagName, revCommit);
//        return revCommit;
//    }


    private static Logger log = Logger.getLogger("c3i");

    public ObjectId toGitObjectId(FullSha objectId) {
        return ObjectId.fromString(objectId.getName());
    }

    public ObjectId toGitObjectId(String fullSha) {
        return ObjectId.fromString(fullSha);
    }


    public InputSupplier<? extends InputStream> getInputSupplier(final ObjectId objectId) {
        return new InputSupplier<InputStream>() {
            @Override
            public InputStream getInput() throws IOException {
                ObjectLoader loader = getRepoObject(objectId);
                return loader.openStream();
            }
        };


    }
}
