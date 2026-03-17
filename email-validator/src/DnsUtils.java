import javax.naming.directory.*;
import javax.naming.*;
import java.util.*;

public class DnsUtils {

    public static ArrayList getMXRecord(String domain) {

        try {

            // Perform a DNS lookup for MX records in the domain
            Hashtable<String, String> env = new Hashtable<>();

            env.put("java.naming.factory.initial",
                    "com.sun.jndi.dns.DnsContextFactory");

            DirContext ctx = new InitialDirContext(env);

            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");

            // if we don't have an MX record, try the machine itself
            if (attr == null || attr.size() == 0) {
                attrs = ctx.getAttributes(domain, new String[] {"A"});
                attr = attrs.get("A");
                if(attr == null)
                    return null;
//                    throw new NamingException
//                            ("No match for name '" + domain + "'");
            }

            ArrayList res = new ArrayList();
            NamingEnumeration en = attr.getAll();
            while (en.hasMore()) {
                String mailhost;
                String x = (String) en.next();
                String f[] = x.split(" ");
                //THE fix *************
                if (f.length == 1) mailhost = f[0];
                else if (f[1].endsWith(".")) mailhost = f[1].substring(0, (f[1].length() - 1));
                else mailhost = f[1];
                // THE fix *************
                res.add(mailhost);
            }
            return res;

//            return attr != null && attr.size() > 0;

        } catch (Exception e) {
            return null;
        }
    }

//    public static boolean hasMXRecord(String domain) {
//
//        try {
//            ArrayList mxList = getMXRecord(domain);
//
//            return mxList != null && !mxList.isEmpty();
//
//        } catch (Exception e) {
//            return false;
//        }
//    }
}