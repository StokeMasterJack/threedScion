package threed.smartClient.client.util.futures;

public interface OnFailureFactory {

    OnFailure createOnFailure(Future future,Throwable exception);
}
