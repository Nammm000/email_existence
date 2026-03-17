package com.example.mx_record;

//import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;


public class MxRecordApplication {

	public static void main(String args[]) {
		String testData[] = {
				"nam.nlh2001@gmail.com", // true
				"jinujawad6s@gmail.com", // false
				"19020372@vnu.edu.vn", //true
				"57586796@vnu.edu.vn", // false
				"drp@drp.cz", // false
				"tvf@tvf.cz", //true ?
				"info@ermaelan.com", // false
				"drp@drp.cz", // false
				"begeddov@jfinity.com", // false ?
				"vdv@dyomedea.com", //true
				"me@aaronsw.com", // false
				"aaron@theinfo.org", // false
				"rss-dev@yahoogroups.com", // false
				"tvf@tvf.cz", //true ?
		};
		for (int i = 0; i < testData.length; i++) {
//			System.out.println("Check email: " + testData[i]); // this

			System.out.println(testData[i] + " is valid: " +
					isAddressValid(testData[i]) + "\n\n");
		}
//		return;
	}
	private static int hear(BufferedReader in) throws IOException {
		String line = null;
		int res = 0;
		while ((line = in.readLine()) != null) {
			String pfx = line.substring(0, 3);
//			System.out.println("line = " + line);  // this
			try {
				res = Integer.parseInt(pfx);
			}
			catch (Exception ex) {
				res = -1;
			}
			if (line.charAt(3) != '-') break;
		}
//		System.out.println("res = " + res);  // this
		return res;
	}
	private static void say(BufferedWriter wr, String text)
			throws IOException {
		wr.write(text + "\r\n");
//		System.out.println("Mess = " + text);	 // this
		wr.flush();
//		return;
	}

	private static ArrayList getMX(String hostName)
			throws NamingException {
		// Perform a DNS lookup for MX records in the domain
		Hashtable env = new Hashtable();
		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
		Attributes attrs = ictx.getAttributes
				(hostName, new String[] {"MX"});
		Attribute attr = attrs.get("MX");
		// if we don't have an MX record, try the machine itself
		if ((attr == null) || (attr.size() == 0)) {
			attrs = ictx.getAttributes(hostName, new String[] {"A"});
			attr = attrs.get("A");
			if(attr == null)
				throw new NamingException
						("No match for name '" + hostName + "'");
		}
		// Huzzah! we have machines to try. Return them as an array list
		// NOTE: We SHOULD take the preference into account to be absolutely
		// correct. This is left as an exercise for anyone who cares.
		ArrayList res = new ArrayList();
		NamingEnumeration en = attr.getAll();
		while (en.hasMore()) {
			String mailhost;
			String x = (String) en.next();
//			System.out.println("x = " + x);
			String f[] = x.split(" ");
			//THE fix *************
			if (f.length == 1) mailhost = f[0];
			else if (f[1].endsWith(".")) mailhost = f[1].substring(0, (f[1].length() - 1));
			else mailhost = f[1];
			// THE fix *************
			res.add(mailhost);

		}
		return res;
	}

	public static boolean isAddressValid(String address) {
		// Find the separator for the domain name
		int pos = address.indexOf('@');
		// If the address does not contain an '@', it's not valid
		if (pos == -1) return false;
		// Isolate the domain/machine name and get a list of mail exchangers
		String domain = address.substring(++pos);
		ArrayList mxList = null;
		try {
			mxList = getMX(domain);
//			System.out.println(mxList);	 // this
		}
		catch (NamingException ex) {
			return false;
		}
		// Just because we can send mail to the domain, doesn't mean that the
		// address is valid, but if we can't, it's a sure sign that it isn't
		if (mxList.size() == 0) return false;
		// Now, do the SMTP validation, try each mail exchanger until we get
		// a positive acceptance. It *MAY* be possible for one MX to allow
		// a message [store and forwarder for example] and another [like
		// the actual mail server] to reject it. This is why we REALLY ought
		// to take the preference into account.
        for (Object o : mxList) {
            boolean valid = false;
            try {
                int res;
//				System.out.println((String) mxList.get(mx)); // this
//				Socket skt = new Socket((String) mxList.get(mx), 25);
                Socket skt = new Socket();
                skt.connect(new InetSocketAddress((String) o, 25), 9000);
                skt.setSoTimeout(9000);
                BufferedReader rdr = new BufferedReader
                        (new InputStreamReader(skt.getInputStream()));
                BufferedWriter wtr = new BufferedWriter
                        (new OutputStreamWriter(skt.getOutputStream()));
                res = hear(rdr);
                if (res != 220) throw new Exception("Invalid header");
                say(wtr, "HELO example.com"); // EHLO
                res = hear(rdr);
                if (res != 250) throw new Exception("Not ESMTP");
                // validate the sender address
                say(wtr, "MAIL FROM: <nam.nlh2000@gmail.com");
                res = hear(rdr);
                if (res != 250) throw new Exception("Sender rejected");
                say(wtr, "RCPT TO: <" + address + ">");
                res = hear(rdr);
                // be polite
                say(wtr, "RSET");
                hear(rdr);
                say(wtr, "QUIT");
                hear(rdr);
                if (res != 250)
                    throw new Exception("Address is not valid!");
                valid = true;
//				System.out.println("valid is true: " + (String) mxList.get(mx)); // this
                rdr.close();
                wtr.close();
                skt.close();
            } catch (Exception ex) {
                // Do nothing but try next host
//				log.info("[mail validation] remote mail validation error.
//				Accepting email anyway: email=" + address + " - "
//				+ e.getMessage());
                System.out.println(ex.getMessage());
            } finally {
                if (valid) return true;
            }
        }
		return false;
	}
}
