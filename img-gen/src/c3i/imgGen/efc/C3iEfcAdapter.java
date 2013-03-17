package c3i.imgGen.efc;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.Slice2;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public class C3iEfcAdapter {

    private EfcService efcService;
    private DaveService daveService

    public C3iEfcAdapter(EfcService efcService) {
        this.efcService = efcService;
    }

    public void processImageModel(HttpServletRequest request) {
        Object fmKey = efcService.parseFmKeyFromHttpRequest(request);

        String imageModelJsonText = efcService.getImageModelJsonText(fmKey);

        ImageModel imageModel = daveService.parseImageModel(imageModelJsonText);


        List<Slice2> slices = imageModel.getSlices();
        for (Slice2 slice : slices) {
            Set<Var> pngVars = slice.getPngVars();
//            Set<String> pngVarCodes =

        }

    }
}
