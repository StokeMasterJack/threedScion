package threed.core.imageModel.shared.slice;

import java.util.List;

public class JpgSpec {

    private final List<Png> pngs;

    public JpgSpec(List<Png> pngs) {
        this.pngs = pngs;
    }

    public static class JpgFingerprint {

        public static final String GUID_SEPARATOR = "-";

        private final String value;

        public JpgFingerprint(List<Png> pngs) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < pngs.size(); i++) {
                Png png = pngs.get(i);
                String shortSha = png.getShortSha();
                sb.append(shortSha);
                boolean last = (i == pngs.size() - 1);
                if (!last) sb.append(GUID_SEPARATOR);
            }
            this.value = sb.toString();
        }

        public String stringValue() {
            return value;
        }

        @Override
        public String toString() {
            return stringValue();
        }
    }

    public String getFingerprint(){
        return new JpgFingerprint(pngs).stringValue();
    }

}
