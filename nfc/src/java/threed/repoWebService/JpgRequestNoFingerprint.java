package threed.repoWebService;


import com.google.common.collect.ImmutableSet;
import threed.core.threedModel.shared.BrandKey;
import threed.core.threedModel.shared.JpgWidth;
import threed.core.threedModel.shared.SeriesKey;
import threed.core.threedModel.shared.Slice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static smartsoft.util.lang.shared.Strings.isEmpty;


/**
 *
 * http://localhost:8080/configurator-content/avalon/2011/exterior-2/wStd/3544/070/LH02/nofp.jpg
 *
 *
 * 3544, 070, LH02
 * http://localhost:8080/configurator-content/<series-name>/<series-year>/<view>-<angle>/<jpg-width>/f1/f2/f2/f4/nofp.jpg
 */
public class JpgRequestNoFingerprint extends SeriesBasedRepoRequest {

    private final SeriesKey seriesKey;
    private final Slice slice;
    private final JpgWidth jpgWidth;
    private final ImmutableSet<String> varCodes;

    public JpgRequestNoFingerprint(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        String uri = getUri();


        String msg = "Bad jpg uri [" + uri + "]";
        if (isEmpty(uri)) throw new NotFoundException(msg);

        String[] a = uri.split("/");

        String seriesYear = a[3];
        String seriesName = a[2];


        this.seriesKey = new SeriesKey(BrandKey.TOYOTA, seriesYear, seriesName);

        slice = new Slice(a[4]);
        jpgWidth = new JpgWidth(a[5]);

        ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        for (int i = 6; i < a.length - 1; i++) {
            String varCode = a[i];
            builder.add(varCode);
        }

        this.varCodes = builder.build();

    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public Slice getSlice() {
        return slice;
    }

    public JpgWidth getJpgWidth() {
        return jpgWidth;
    }

    public ImmutableSet<String> getVarCodes() {
        return varCodes;
    }
}
