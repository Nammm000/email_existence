import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class SmtpVerifier {

    private static String hear(BufferedReader reader, BufferedWriter writer, String text) throws IOException {
        writer.write(text);
        writer.flush();
        return reader.readLine();
    }

    public static boolean verify(String email, String domain, ArrayList mxList) {

        boolean valid = false;
        for (Object o : mxList) {
            try {
                Socket socket = new Socket((String) o, 25);
//                Socket socket = new Socket();
//                socket.connect(new InetSocketAddress((String) o, 25), 9000);
//                socket.setSoTimeout(9000);

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));

                BufferedWriter writer =
                        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                reader.readLine();

                hear(reader, writer, "HELO example.com\r\n");

                hear(reader, writer, "MAIL FROM:<nam.nlh@gmail.com>\r\n");

                String response = hear(reader, writer, "RCPT TO:<" + email + ">\r\n");
                System.out.println("Response:" + response);

                reader.close();
                writer.close();
                socket.close();

                valid = response.startsWith("250");
                if (valid) break;

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return valid;
    }
}