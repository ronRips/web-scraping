package scraping;

public class Utility {
    public static String correct(String dir) {
        if (dir.length() == 0)return dir;
        return dir.substring(0,dir.length()-1);
    }
}
