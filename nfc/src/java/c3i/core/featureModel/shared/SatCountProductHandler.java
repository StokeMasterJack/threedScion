package c3i.core.featureModel.shared;

import c3i.core.featureModel.shared.search.ProductHandler;

public class SatCountProductHandler implements ProductHandler {

    private final boolean writeProductToConsole;

    private long count;

    public SatCountProductHandler(boolean writeProductToConsole) {
        this.writeProductToConsole = writeProductToConsole;
    }

    public SatCountProductHandler() {
        this(false);
    }

    @Override
    public void onProduct(AssignmentsForTreeSearch product) {
        count++;

        if (writeProductToConsole) {
            System.out.println(product);
        }
    }

    public long getCount() {
        return count;
    }


}
