package c3i.util.shared.futures;

public interface ExceptionHandlerFactory {

    OnException createExceptionHandler(Future future);

}
