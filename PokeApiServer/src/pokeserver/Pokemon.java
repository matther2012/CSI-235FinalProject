package pokeserver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matth on 3/27/2016.
 */
public class Pokemon {
    private String title;
    private String releaseDate;
    private String overview;

    public Pokemon() {
        this.title = "";
        this.releaseDate = "";
        this.overview = "";
    }

    public Pokemon(String title, String release_date, String overview) {
        this.title = title;
        this.releaseDate = release_date;
        this.overview = overview;
    }

    @Override
    public String toString() {
        return "Title = " + this.title + "`Release Date = " + this.releaseDate + "`Overview = " + this.overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    public static Pokemon[] parseData(String movieJsonStr, int numMovies){
        ArrayList<Pokemon> tempMovies = new ArrayList<Pokemon>(); // in case the number of movies available is less than
        // the number of movies provided
        try{
            JSONObject moviesJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray("results");

            for(int i = 0; i < numMovies && i < movieArray.length(); i++)
            {
                String title;
                String release_date;
                String overview;

                JSONObject movieObj = (JSONObject) movieArray.get(i);
                title = movieObj.getString("original_title");
                release_date = movieObj.getString("release_date");
                overview = movieObj.getString("overview");

                Pokemon newPokemon = new Pokemon(title, release_date, overview);
                tempMovies.add(newPokemon);
            }
        }catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        Pokemon[] resultObjs = tempMovies.toArray(new Pokemon[tempMovies.size()]);
        return resultObjs;
    }
}
