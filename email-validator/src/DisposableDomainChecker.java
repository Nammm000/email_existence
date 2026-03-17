import java.util.Set;

public class DisposableDomainChecker {

    private static final Set<String> DISPOSABLE_DOMAINS = Set.of(
            "mailinator.com",
            "10minutemail.com",
            "tempmail.com",
            "guerrillamail.com"
    );

    public static boolean isDisposable(String email) {

        String domain = email.substring(email.indexOf("@") + 1);

        return DISPOSABLE_DOMAINS.contains(domain.toLowerCase());
    }
}