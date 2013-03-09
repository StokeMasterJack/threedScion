package c3i.imgGen.server;

import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.RawBaseImage;
import c3i.imageModel.shared.Slice;
import c3i.imageModel.shared.Slice2;
import c3i.imgGen.external.ImgGenContext;
import c3i.imgGen.external.ImgGenContextFactory;
import c3i.repo.server.BrandRepos;

import java.util.HashSet;

public class JpgSetFactory2 extends JpgSetFactory {

    public JpgSetFactory2(BrandRepos brandRepos, ImgGenContextFactory imgGenContextFactory) {
        super(brandRepos, imgGenContextFactory);
    }

    @Override
    protected JpgSet createJpgSet(JpgSetKey key) {
        ImgGenContext imgGenContext = getImgGenContext(key);

        ImageModel im = imgGenContext.getImageModel();
        Slice slice = key.getSlice();
        Slice2 slice2 = im.getSlice2(slice);

        JpgSetTask t = new JpgSetTask(imgGenContext, slice2);
        t.start();
        HashSet<RawBaseImage> jpgSet = t.getJpgSet();
        return new JpgSet(jpgSet);
    }

}
