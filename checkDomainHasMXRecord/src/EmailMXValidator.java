import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.Scanner;

public class EmailMXValidator {

    public static boolean hasMXRecord(String domain) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});

            Attribute attr = attrs.get("MX");

            return attr != null && attr.size() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidEmail(String email) {

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!email.matches(regex)) {
            System.out.println("Invalid email format");
            return false;
        }

        return isEmailDomainValid(email);
    }

    public static boolean isEmailDomainValid(String email) {
        String domain = email.substring(email.indexOf("@") + 1);
        return hasMXRecord(domain);
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String email = scanner.nextLine();

        if (isValidEmail(email)) {
            System.out.println(email + "'s domain has MX records (can receive emails)");
        } else {
            System.out.println("Invalid email domain");
        }
    }
}