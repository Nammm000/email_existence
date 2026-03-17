import java.util.ArrayList;

public class EmailValidatorService {

    public static EmailValidationResult validate(String email) {

        boolean formatValid =
                EmailFormatValidator.isValid(email);

        if (!formatValid)
            return new EmailValidationResult(false,false,false,false);

        String domain = email.substring(email.indexOf("@") + 1);

        boolean disposable =
                DisposableDomainChecker.isDisposable(email);

        ArrayList mxList = DnsUtils.getMXRecord(domain);
        boolean mx = mxList != null && !mxList.isEmpty();

        boolean smtp =
                mx && SmtpVerifier.verify(email, domain, mxList);

        return new EmailValidationResult(
                formatValid,
                mx,
                disposable,
                smtp
        );
    }
}