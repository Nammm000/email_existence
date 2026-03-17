public class Main {
    public static void main(String[] args) {

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
                    EmailValidator.isValidEmail(testData[i]) + "\n");
        }
    }
}