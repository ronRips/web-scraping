package scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;


public class Search {

    private String input;
    private String url;
    public ArrayList<ArrayList<String>> movies;

    //constructor
    Search(String url, String input) {
        this.url = url;
        this.input = input.toLowerCase();
    }

    //******************
    public void findMovies() throws IOException {
        movies = new ArrayList<>();
        try {
            Document document = Jsoup.connect(this.url).get();
            Elements tables = document.select("td.result_text").not("td:contains(in development)").select("a[href]");

            for (Element element : tables) {
                url = element.attr("abs:href");
                addMovie(url, movies);
            }
        } catch (ConnectException e) {
            System.out.println(e.getMessage());
        }
        WriteOnFile writer = new WriteOnFile(movies);
        writer.write();
    }

    //**************
    public void addMovie(String url, ArrayList<ArrayList<String>> movies) throws IOException {
        try {
            Document document = Jsoup.connect(url).get();
            ArrayList<String> movie = new ArrayList<>();


            String name = document.select("h1.TitleHeader__TitleText-sc-1wu6n3d-0").text();
            if (fuzzy(name, input)) return;

            movie.add(name);
            addGenre(document, movie);
            addRating(document, movie);
            addDuration(document, movie);
            addDirector(document, movie);
            addStars(document, movie);

            movies.add(movie);
        } catch (ConnectException e) {
            System.out.println("connect error");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//*********************
    private void addStars(Document document, ArrayList<String> movie) {
        Elements directorsAndStars = getDirectorsAndStars(document);
        int i = indexOf("Stars", directorsAndStars);
        if (i == -1) {
            movie.add("");
        } else {
            Elements stars = directorsAndStars.get(i).select("a.ipc-metadata-list-item__list-content-item.ipc-metadata-list-item__list-content-item--link");
            addNames(stars, movie);
        }
    }
//********************
    private int indexOf(String title, Elements directorsAndStars) {
        for (int i = 0; i < directorsAndStars.size(); i++) {
            Element a = directorsAndStars.get(i).select("a.ipc-metadata-list-item__label").first();
            Element span = directorsAndStars.get(i).select("span.ipc-metadata-list-item__label").first();

            if (a != null && a.text().equals(title)) return i;
            if (span != null && span.text().equals(title)) return i;

        }
        return -1;
    }

//*****************
    private Elements getDirectorsAndStars(Document document) {
        return document.select("ul.ipc-metadata-list.ipc-metadata-list--dividers-all.title-pc-list.ipc-metadata-list--baseAlt li.ipc-metadata-list__item");
    }
//*******************
    private void addDirector(Document document, ArrayList<String> movie) {

        Elements directorsAndStars = getDirectorsAndStars(document);
        if (directorsAndStars.size() == 0) {
            movie.add("");
            return;
        }
        Element title = directorsAndStars.get(0).select("span.ipc-metadata-list-item__label").first();
        if (title != null && title.text().equals("Director")) {
            Elements directors = directorsAndStars.get(0).select("a.ipc-metadata-list-item__list-content-item.ipc-metadata-list-item__list-content-item--link");
            addNames(directors, movie);
        }
        else movie.add("");
    }
//******************
    private void addNames(Elements names, ArrayList<String> movie) {
        String str = "";
        for (Element name : names) {
            str = str + name.text() + ",";
        }
        str = Utility.correct(str);
        movie.add(str);
    }

    //*****************
    private void addDuration(Document document, ArrayList<String> movie) {
        Elements duration = document.select("ul.ipc-inline-list.ipc-inline-list--show-dividers.TitleBlockMetaData__MetaDataList-sc-12ein40-0 li.ipc-inline-list__item");
        if (duration.size() >= 2) {
            String time = duration.get(duration.size() - 1).text();
            if (time.contains("h") || time.contains("min")) movie.add(time);
            else movie.add("");
        } else movie.add("");
    }

//******************
    private void addRating(Document document, ArrayList<String> movie) {
        Elements rating = document.select("span.TitleBlockMetaData__ListItemText-sc-12ein40-2");
        if (rating.size() < 2) {
            movie.add("");
        } else {
            movie.add(rating.get(1).text());
        }
    }
//***********************
    private void addGenre(Document document, ArrayList<String> movie) {
        Elements genres = document.select("a.GenresAndPlot__GenreChip-cum89p-3 span.ipc-chip__text");
        addNames(genres, movie);
    }
//**************
    private boolean fuzzy(String name, String input) {
        String[] wordsName = name.toLowerCase().split(" ");
        String[] wordsInput = input.split(" ");
        for (String str : wordsInput) {
            boolean contain = false;
            for (String word : wordsName) {
                if (word.contains(str)) {
                    contain = true;
                    break;
                }
            }
            if (!contain) return true;
        }
        return false;
    }
}
