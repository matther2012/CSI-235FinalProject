/*
 * Authors: Matthew Dean and Matthew Fortier
 * 
 * Description:
 * This is the server for an application that serves up pokemon data
 * to client machines. It mostly handles retrieving data from the api
 * endpoint and formatting it into a string that can be returned to the
 * client.
 * 
 * We certify that all this work is original or appropriately cited.
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PokeApiServer {
	
	public static String ENDPOINT_BASE = "http://pokemonapi.azurewebsites.net/api/pokemon/";
	public static int NUMBER_OF_POKEMON = 721;
	
    public static void main(String[] args) {
    	System.out.println("~~| POKEMON DATABASE SERVER |~~\n");
    	Socket socket = null;
    	
    	//connection to client try-catch
    	try
    	{
    		ServerSocket serverSocket = new ServerSocket(4675);
    		System.out.println("Waiting for client connection...");
    		socket = serverSocket.accept();
    		System.out.println("Connected to client.");
    	}
    	catch(IOException ex)
    	{
    		System.out.println(ex.getMessage());
    	}
    	
    	try {
    		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    		String inputLine;
    		String outputLine = "";
    		String pokemonName = "";
    		int options = 0b00000;
    		while ((inputLine = br.readLine()) != null) {
    			System.out.println("Client request: " + inputLine);
    			String[] request = inputLine.split(",");
    			pokemonName = request[0];
    			options = Integer.parseInt(request[1]);
    			
    			out.println(Format(Fetch(pokemonName), options));
    		}
    	}
    	catch(IOException ex) {
    		System.out.println(ex.getMessage());
    	}
    	
    	System.out.println(Format(Fetch("1"), 0));
    }
    
    public static String Fetch(String name)
    {
    	HttpURLConnection conn = null;
    	BufferedReader reader = null;
    	
    	String rawJson = "";
    	
    	if(name == "random")
    	{
    		//add empty string as dirty cast to string
    		name = "" + ThreadLocalRandom.current().nextInt(1, NUMBER_OF_POKEMON + 1);
    	}
    	
    	try {
    		String endpointUrlString = ENDPOINT_BASE + name + "/";
    		URL endpointUrl = new URL(endpointUrlString);
    		
    		conn = (HttpURLConnection) endpointUrl.openConnection();
    		conn.setRequestMethod("GET");
    		conn.connect();
    		
    		InputStream inputStream = conn.getInputStream();
    		reader = new BufferedReader(new InputStreamReader(inputStream));
    		String line;
    		StringBuilder buffer = new StringBuilder();
    		
    		while ((line = reader.readLine()) != null) {
    			buffer.append(line).append("\n");
    		}
    		
    		rawJson = buffer.toString();
    		
    		//Trim off first and last character to make a JSON string
    		rawJson = rawJson.substring(1, rawJson.length() - 2);
    		rawJson = rawJson.replace("\\", "");
    	}
    	catch(IOException ex) {
    		System.out.println(ex.getMessage());
    	}
    	finally {
    		if(conn != null) {
    			conn.disconnect();
    		}
    		if(reader != null) {
    			try {
    				reader.close();
    			}
    			catch(IOException ex) {
    				System.out.println(ex.getMessage());
    			}
    		}
    	}
    	return rawJson;
    }
    
    public static String Format(String rawJson, int options)
    {
    	String returnString = "";
    	
    	try
    	{
    		JSONObject pokeJson = new JSONObject(rawJson);
    		
    		options = 7;
    		
    		//get basic info
    		returnString = "#" + pokeJson.getInt("id") + " ";
    		returnString += pokeJson.getString("name") + " (";
    		JSONArray typeArray = pokeJson.getJSONArray("types");
    		
    		//get all types, separated by a slash
    		for(int i = 0; i < typeArray.length(); i++)
    		{
    			JSONObject typeObj = (JSONObject) typeArray.get(i);
    			JSONObject typeDescriptionObj = (JSONObject) typeObj.get("type");
    			returnString += typeDescriptionObj.getString("name");
    			
    			//we don't want a slash after it if its the last one
    			if(i != typeArray.length() - 1)
    			{
    				returnString += "/";
    			}
    		}
    		returnString += ")\n";
    		
    		//abilities
    		if((options & 4) == 4)
    		{
    			returnString += "\nabilities:\n";
    			
    			JSONArray abilityArray = pokeJson.getJSONArray("abilities");
    			
    			for(int i = 0; i < abilityArray.length(); i++)
    			{
    				JSONObject abilityObj = (JSONObject) abilityArray.get(i);
    				JSONObject abilityDescriptionObj = (JSONObject) abilityObj.get("ability");
    				returnString += "   " + abilityDescriptionObj.getString("name").replace("-", " ") + "\n";
    			}
    		}
    		
    		//moves
    		if((options & 2) == 2)
    		{
    			returnString += "\nmoves:\n";
    			
    			JSONArray moveArray = pokeJson.getJSONArray("moves");
    			
    			for(int i = 0; i < moveArray.length(); i++)
    			{
    				JSONObject moveObj = (JSONObject) moveArray.get(i);
    				JSONObject moveDescriptionObj = (JSONObject) moveObj.get("move");
    				returnString += "   " + moveDescriptionObj.getString("name").replace("-", " ") + "\n";
    			}
    		}
    		
    		//stats
    		if((options & 1) == 1)
    		{
    			returnString += "\nbase stats:\n";
    			
    			JSONArray statArray = pokeJson.getJSONArray("stats");
    			
    			for(int i = 0; i < statArray.length(); i++)
    			{
    				JSONObject statObj = (JSONObject) statArray.getJSONObject(i);
    				JSONObject statDescriptionObj = (JSONObject) statObj.get("stat");
    				returnString += "   " + statObj.getInt("base_stat") + " " + statDescriptionObj.getString("name").replace('-', ' ') + "\n";
    			}
    		}
    	}
    	catch(JSONException ex)
    	{
    		System.out.println(ex.getMessage());
    	}
    	
    	return returnString;
    }
}
