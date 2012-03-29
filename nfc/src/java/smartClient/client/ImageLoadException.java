package smartClient.client;

import smartsoft.util.lang.shared.Path;

public class ImageLoadException extends RuntimeException {

    private final Path url;

    public ImageLoadException(Path url) {
        super(url.toString());
        this.url = url;
    }

    public Path getUrl() {
        return url;
    }


}
