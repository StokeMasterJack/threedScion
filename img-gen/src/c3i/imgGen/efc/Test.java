package c3i.imgGen.efc;

public class Test {

    public static void main(String[] args) {

        String fmKeyString = "fs";

        EfcFactory efcFactory = null;

        EfcService efcService = efcFactory.getEfcService();

        Object fmKey = efcService.parseFmKeyFromString(fmKeyString);

        String imageModelJsonText = efcService.getImageModelJsonText(fmKey);


    }
}
