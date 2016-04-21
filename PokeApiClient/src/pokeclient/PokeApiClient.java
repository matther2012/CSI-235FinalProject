package pokeclient;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class PokeApiClient {
    public static void main(String[] args) throws IOException {
        System.out.println("Pokedex Client");

        FileInputStream fstream = new FileInputStream("pokemon.txt");
        BufferedReader br1 = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        //Read File Line By Line
        while ((strLine = br1.readLine()) != null)   {
            // Print the content on the console
            System.out.println (strLine);
        }

        //Close the input stream
        br1.close();

        try {
            System.out.println("Loading.....");
            InetAddress localAddress = InetAddress.getLocalHost();
            try {
                Socket clientSocket = new Socket(localAddress, 6000);
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String pokemon;
                char selection;
                String selections = "";

                System.out.println("Pokedex Active");
                System.out.print("WHO'S THAT POKEMON! or type 'Random': ");

                Scanner scanner = new Scanner(System.in);
                pokemon = scanner.nextLine();
                pokemon.replace(",", " ");

                System.out.print("Would you like to see abilitties? Y/N: ");
                selection = scanner.nextLine().charAt(0);
                if(Character.toString(selection).equalsIgnoreCase("y")) {
                    selections += 1;
                }
                else{
                    selections += 0;
                }

                System.out.print("Would you like to see this Pokemons moves? Y/N: ");
                selection = scanner.nextLine().charAt(0);
                if(Character.toString(selection).equalsIgnoreCase("y")) {
                    selections += 1;
                }
                else{
                    selections += 0;
                }
                System.out.print("Would you like to see this Pokemons stats? Y/N: ");
                selection = scanner.nextLine().charAt(0);
                if(Character.toString(selection).equalsIgnoreCase("y")) {
                    selections += 1;
                }
                else{
                    selections += 0;
                }

                System.out.println(selections);
                int options = Integer.parseInt(selections, 2);
                System.out.println(pokemon + "," + options);

                System.out.println("Server response: \n");
                String response = br.readLine();

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

