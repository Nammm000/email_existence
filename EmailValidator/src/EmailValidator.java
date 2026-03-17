import javax.naming.directory.*;
import javax.naming.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

public class EmailValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final int TIMEOUT = 5000;

    public static boolean isValidEmail(String email) {

        // 1. Regex check
        if (!EMAIL_PATTERN.matcher(email).matches())
            return false;

        String domain = email.substring(email.indexOf("@") + 1);

        // 2. MX lookup
        List<String> mxList = getMXRecords(domain);
        if (mxList.isEmpty())
            return false;

        // 3. Try SMTP validation
        for (String mx : mxList) {

            if (verifySMTP(email, mx)) {
                return true;
            }
        }

        return false;
    }

    private static List<String> getMXRecords(String domain) {

        List<String> result = new ArrayList<>();

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial",
                    "com.sun.jndi.dns.DnsContextFactory");

            DirContext ctx = new InitialDirContext(env);

            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");

            if (attr == null) return result;

            for (int i = 0; i < attr.size(); i++) {

                String record = (String) attr.get(i);
                String[] parts = record.split(" ");

                if (parts.length >= 2) {
                    String host = parts[1].endsWith(".")
                            ? parts[1].substring(0, parts[1].length() - 1)
                            : parts[1];

                    result.add(host);
                }
            }

        } catch (Exception ignored) {}

        return result;
    }

    private static boolean verifySMTP(String email, String host) {

        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress(host, 25), TIMEOUT);
            socket.setSoTimeout(TIMEOUT);

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedWriter writer =
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            if (readResponse(reader) != 220) return false;

            send(writer, "EHLO yourdomain.com");
            if (readResponse(reader) != 250) return false;

            send(writer, "MAIL FROM:<nam.nlh@gmail.com>");
            if (readResponse(reader) != 250) return false;

            send(writer, "RCPT TO:<" + email + ">");
            int rcpt = readResponse(reader);

            send(writer, "QUIT");

            return rcpt == 250 || rcpt == 251;

        } catch (Exception e) {
            return false;
        }
    }

    private static void send(BufferedWriter writer, String msg)
            throws IOException {
        writer.write(msg + "\r\n");
        writer.flush();
    }

    private static int readResponse(BufferedReader reader)
            throws IOException {

        String line = reader.readLine();
        if (line == null || line.length() < 3)
            return -1;

        return Integer.parseInt(line.substring(0, 3));
    }
}