package cats.space.laser.toot_android;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class ApiException extends Exception {

    private ApiError apiError;

    public ApiException(String message, ApiError apiError) {
        super(message);
        this.apiError = apiError;
    }

    public ApiError getApiError() {
        return apiError;
    }

    @Override
    public String getMessage() {
        if (apiError != null && apiError.getErrorMessage() != null) {
            return apiError.getErrorMessage();
        } else {
            return super.getMessage();
        }
    }
}
