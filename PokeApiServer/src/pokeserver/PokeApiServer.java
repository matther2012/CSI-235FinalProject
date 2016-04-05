package pokeserver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

/**
 * Created by Matthew on 3/10/2016.
 */
public class PokeApiServer {
    public static void main(String[] args) {
        System.out.println("Weather Server ...");
        Socket sSocket = null;
        try {
            ServerSocket serverSocket = new ServerSocket(6000);
            System.out.println("Waiting for connection.....");
            sSocket = serverSocket.accept();
            System.out.println("Connected to client");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(sSocket.getInputStream()));
            PrintWriter out = new PrintWriter(sSocket.getOutputStream(), true);
            String inputLine;
            String outputLine = "";
            int year = 0;
            int numMovies = 0;
            while ((inputLine = br.readLine()) != null) {
                System.out.println("Client request: " + inputLine);
                String[] request = inputLine.split(",");
                year = Integer.parseInt(request[0]); //2015
                numMovies = Integer.parseInt(request[1]); //3
                String wString = fetchData(year);
                Pokemon[] resultObjs = new Pokemon[numMovies];
                resultObjs = Pokemon.parseData(wString, numMovies);
                for (Pokemon m : resultObjs) {
                    outputLine += m.toString() + "`" + "`"; // using '`' as delimiter because ',' appears in some titles
                }

                System.out.println(outputLine); //testing purpose
                out.println(outputLine);

            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static String fetchData(int year) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        String movieJsonStr = null; //Contain the raw JSON response from OpenWeatherMap API
        try {
            // Construct a URL for the OpenWeatherMap query
            String sUrl = "http://api.themoviedb.org/3/discover/movie?primary_release_year=" + year + "&sort_by=vote_average.desc&" +
                    "api_key=2c396035a007527dde52e76695dae223";
            URL url = new URL(sUrl);
            //Setup connection to OpenWeatherMap
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream inputStream = conn.getInputStream(); // Read the input stream
            //Place input stream into a buffered reader
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder buffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            movieJsonStr = buffer.toString(); //Create forecast data from buffer
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println(movieJsonStr);
        }
        return movieJsonStr;
    }
}
