package scraping;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter title of a movie");

        String title = myObj.nextLine();

        String url = "https://www.imdb.com/find?q="+title+"&s=tt&ttype=ft&ref_=fn_ft";
        Search search = new Search(url, title);
        search.findMovies();
    }
}