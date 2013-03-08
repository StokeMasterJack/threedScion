package c3i.repo.server.vnode;

import c3i.repo.server.SeriesRepo;
import com.google.common.io.Closeables;
import com.google.common.io.InputSupplier;
import org.eclipse.jgit.lib.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class VNodeRepo extends VNode {

    private SeriesRepo seriesRepo;

    public VNodeRepo(String name, List<VNode> childNodes, int depth, SeriesRepo seriesRepo) {
        super(name, childNodes, depth);
        this.seriesRepo = seriesRepo;
    }

    public VNodeRepo(String name, ObjectId fullSha, int depth, SeriesRepo seriesRepo) {
        super(name, fullSha, depth);
        this.seriesRepo = seriesRepo;
    }


    @Override
    public Properties readFileAsProperties() {
        InputSupplier<? extends InputStream> inputSupplier = seriesRepo.getObject(fullSha);
        InputStream input = null;
        try {
            input = inputSupplier.getInput();
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (input != null) {
                Closeables.closeQuietly(input);
            }
        }
    }
}
