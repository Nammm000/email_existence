import java.util.concurrent.*;

public class AsyncEmailValidator {

    private static final ExecutorService executor =
            Executors.newFixedThreadPool(5);

    public static CompletableFuture<EmailValidationResult> validateAsync(String email) {

        return CompletableFuture.supplyAsync(() ->
                EmailValidatorService.validate(email), executor);
    }
}