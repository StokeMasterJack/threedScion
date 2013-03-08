package c3i.repo.server.rt;

public class FullSha {

    protected final String shaString;

    public FullSha(String shaString) {
        if (shaString == null) throw new NullPointerException();
        if (shaString.length() != 40)
            throw new IllegalArgumentException("BadShaString[" + shaString + "]. Must be 40 digits");
        this.shaString = shaString;
    }

    @Override
    public String toString() {
        return shaString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullSha shaId = (FullSha) o;

        if (!shaString.equals(shaId.shaString)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return shaString.hashCode();
    }
}
