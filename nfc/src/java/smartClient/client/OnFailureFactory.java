package smartClient.client;

public interface OnFailureFactory {

    OnFailure createOnFailure(Future future,Throwable exception);
}
