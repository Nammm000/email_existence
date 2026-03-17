public class EmailValidationResult {

    private boolean validFormat;
    private boolean hasMxRecord;
    private boolean disposable;
    private boolean smtpValid;

    public EmailValidationResult(boolean validFormat, boolean hasMxRecord,
                                 boolean disposable, boolean smtpValid) {
        this.validFormat = validFormat;
        this.hasMxRecord = hasMxRecord;
        this.disposable = disposable;
        this.smtpValid = smtpValid;
    }

    public boolean isValid() {
        return validFormat && hasMxRecord && !disposable && smtpValid;
    }

    @Override
    public String toString() {
        return "EmailValidationResult{" +
                "validFormat=" + validFormat +
                ", hasMxRecord=" + hasMxRecord +
                ", disposable=" + disposable +
                ", smtpValid=" + smtpValid +
                '}';
    }
}