package com.jonathan.survivor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;
import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.inventory.Loadout;

public class Profile implements Serializable
{
	/** Stores the max world seed that will be used to create the world. Possibly the higher the value, the more probability in world diversity. */
	public static final int MAX_WORLD_SEED = 50000;
	
	/** Stores the id of the profile, where 0 is the first profile shown in the world selection list. */
	private int profileId;
	/** Stores the date the profile was last modified. */
	private Date dateLastModified;
	/** Helper object used to convert the date last modified into a string. */
	private transient SimpleDateFormat dateFormatter;
	
	/** Stores the row and column offset we should use for the TerrainLayers of the level. These cell coordinates are the coordinates of the bottom-left-
	 *  most layer of the TerrainLevel when the game was saved. Thus, if this offset is specified, the TerrainLevel can choose to define the bottom-left-
	 *  most layer to have these cell coordinates, and the player will be dropped in the same cell he left off in the TerrainLevel. */
	private int terrainRowOffset, terrainColOffset;
	
	/** Stores the world seed. Each profile has a different seed. The same seed creates the same world. */
	private int worldSeed;
	
	/** Stores a HashMap containing lists of scavenged objects in each TerrainLayer. First key is the layer's row, second is the layer's column. The
	 *  array stores the list of objectIds for all GameObjects that have been scavenged on that layer. */
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> scavengedLayerObjects;
	
	/** Stores the player's loadout so that it stays constants when re-entering the game. */
	private Loadout loadout;
	
	/** Creates a default profile with profileId = 0. This constructor will be called when a Profile object is read from a JSON file. */
	public Profile()
	{
		//Populates the 'dateLastModified' variable with a Date instance. The time of the object will be changed in the 'read()' method.
		dateLastModified = new Date();
		//Helper object used to format a string from a date object.
		dateFormatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
	}
	
	/** Creates a new profile starting from the beginning of the game. Called when the user creates a new world.
	 * @param id ID of the profile we want to create. The first profile has ID 0, and is the first shown in the world selection list. 
	 */
	public Profile(int id)
	{
		//Populate the id variable of the Profile.
		this.profileId = id;
		
		//Populates the 'dateLastModified' variable with a Date instance whose time is the current time.
		dateLastModified = new Date();
		//Helper object used to format a date string from a date object.
		dateFormatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
		
		//Creates a random seed for the world, dictating its terrain and layout.
		worldSeed = (int)(Math.random() * MAX_WORLD_SEED);
		
		//Creates the empty HashMap needed to store the GameObjects scavenged by the player.
		scavengedLayerObjects = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		
		//Creates a default, empty loadout for the player.
		loadout = new Loadout();
	}
	
	/** Sets the profile Id of the profile. */
	public void setProfileId(int profileId)
	{
		this.profileId = profileId;
	}
	
	/** Returns the if of the profile, where 0 is the first profile shown in the world selection list */
	public int getProfileId()
	{
		return profileId;
	}
	
	/**  Sets the world seed of the profile. Changing it changes the entire world. Should not be changed after profile creation, or will break save file. */
	public void setWorldSeed(int worldSeed)
	{
		this.worldSeed = worldSeed;
	}
	
	/** Returns the world seed used to procedurally generate the world. Same seed = same world. */
	public int getWorldSeed()
	{
		return worldSeed;
	}
	
	/** Sets the terrain row offset to be used the next time the game is loaded. Set this to the row of the bottom-left-most layer of the level to resume the
	 *  game where the player left off last. */
	public void setTerrainRowOffset(int offset)
	{
		terrainRowOffset = offset;
	}
	
	/** Gets the terrain row offset which was saved to profile. Specify this as the rowOffset of the TerrainLevel to start the game at the same place the user left off. */
	public int getTerrainRowOffset()
	{
		return terrainRowOffset;
	}
	
	/** Sets the terrain column offset to be used the next time the game is loaded. Set this to the column of the bottom-left-most layer of the level to resume the
	 *  game where the player left off last. */
	public void setTerrainColOffset(int offset)
	{
		terrainColOffset = offset;
	}
	
	/** Gets the terrain column offset which was saved to profile. Specify this as the colOffset of the TerrainLevel to start the game at the same place the user left off. */
	public int getTerrainColOffset()
	{
		return terrainColOffset;
	}
	
	/** Called when this profile has been saved from the hard drive. In this case, we update its date of modification. */
	private void profileSaved()
	{
		//Update the profile's date of modification to the current time of the system, since the profile was just saved.
		dateLastModified.setTime(TimeUtils.millis());
	}
	
	/** Returns a string representation for the profile, used for each item of the world selection list from the world select screen. */
	public String toString()
	{
		//Returns the profileId, followed by the date the profile was last modified. We get it in a readable format using 'SimpleDateFormatter.format(Date):String'.
		//Note that profileId is incremented by one since it is zero-based.
		return (profileId+1) + "- " + dateFormatter.format(dateLastModified);
	}

	/* Methods implemented from Serializable */
	
	/** Indicates how a Profile object is converted to a JSON file. */
	@Override
	public void write(Json json) 
	{
		//Tell the profile that it has just been saved to the hard drive. Updates the date of modification.
		profileSaved();
		
		//Writes the key member variables of the profile into its JSON file.
		json.writeValue("profileId", profileId);
		json.writeValue("timeLastModified", dateLastModified.getTime());
		json.writeValue("worldSeed", worldSeed);
		
		json.writeValue("terrainRowOffset", terrainRowOffset);
		json.writeValue("terrainColOffset", terrainColOffset);
		
		json.writeValue("loadout", loadout);
		writeScavengedLayerObjects(json);
	}

	/** Indicates how a JSON file is read to be converted into a Profile object. Note that the default Profile constructor is called before this method. */
	@Override
	public void read(Json json, JsonValue jsonData)
	{
		//Reads the key member variables from the profile's JSON file. Stores them inside the profile's member variables.
		profileId = json.readValue("profileId", Integer.class, jsonData);
		dateLastModified.setTime(json.readValue("timeLastModified", Long.class, jsonData));
		worldSeed = json.readValue("worldSeed", Integer.class, jsonData);
		
		terrainRowOffset = json.readValue("terrainRowOffset", Integer.class, jsonData);
		terrainColOffset = json.readValue("terrainColOffset", Integer.class, jsonData);
		
		loadout = json.readValue("loadout", Loadout.class, jsonData);
		readScavengedLayerObjects(json, jsonData);
		
		
	}
	
	/** Converts the scavengedLayerObjects HashMap into a String and writes it to the Profile's JSON file. */
	private void writeScavengedLayerObjects(Json json)
	{
		//Stores the String to write inside the JSON file.
		String string = "";
		
		//Stores all of the keys inside the scavengedLayerObjects Hashmap, which each represent containers for a row.
		Set<Integer> rows = scavengedLayerObjects.keySet();
		
		//Cycle through each row in the HashMap and converts its data into a string.
		for(int row:rows)
		{
			//Stores the row number as the first integer in the line
			string += row + " ";
			
			//Creates a set to cycle through each column key in the HashMap
			Set<Integer> cols = scavengedLayerObjects.get(row).keySet();
			
			//Cycles through the columns of the HashMap, which each contain an Integer array.
			for(int col:cols)
			{			
				//Start the column definition with the column number.
				string += col + ": ";
				
				//Stores the Integer array containing all of the objectIds of scavenged objects in the given (row, col)
				ArrayList<Integer> array = scavengedLayerObjects.get(row).get(col);
				
				//Stores the length of the array.
				int len = array.size();
				
				//Each column number is followed by an open bracket to indicate the beginning of an array
				string += "[ ";
				
				//Cycles through the elements of the array.
				for(int i = 0; i < len; i++)
				{
					//Adds each array element into the string sequentially
					string += array.get(i) + " ";
				}
				
				//Ends each array definition with a closed bracket.
				string += "] ";
			}
			
			//Skip a line for each row.
			string += "\n";
		}
		
		//Write the string in the "scavengedLayerObjects" entry of the profile's JSON file.
		json.writeValue("scavengedLayerObjects", string);
	}
	
	/** Reads the String stored inside the JSON file and converts it into a HashMap for the scavengedLayerObjects variable. */
	private void readScavengedLayerObjects(Json json, JsonValue jsonData)
	{
		//Creates a new instance for scavengedLayerObjects, which will be populated as the JSON String is read.
		this.scavengedLayerObjects = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		
		//Creates a Scanner to read the string with name "scavengedLayerObjects" inside the JSON file.
		Scanner scanner = new Scanner(json.readValue("scavengedLayerObjects", String.class, jsonData));
		
		//While the String isn't empty
		while(scanner.hasNext())
		{
			//Read the next line of the String, which represents the values of the first keys inside the scavengedLayerObjects HashMap.
			Scanner line = new Scanner(scanner.nextLine());
			
			//The first integer in the line is the row number.
			int row = line.nextInt();
			
			//Create a new HashMap for the row.
			scavengedLayerObjects.put(row, new HashMap<Integer, ArrayList<Integer>>());
			
			while(line.hasNext())
			{
				//Stores the string containing the next column.
				String columnString = line.next();
				
				//Truncates the colon from the column and converts it into an integer.
				int col = Integer.parseInt(columnString.substring(0, columnString.length()-1));
				
				//Stores the array containing the objectIds of the current row and column.
				ArrayList<Integer> array = new ArrayList<Integer>();
			
				//The next token is an open bracket "[".
				line.next();
			
				//Stores the first element of the array, or a "]", if the array is empty.
				String token = line.next();
				
				//Keep on cycling through the array's elements until the closing bracket is encountered, which indicates the end of the array.
				while(!token.equals("]"))
				{
					//Parse the array element into an integer and add it to the array.
					array.add(Integer.valueOf(token));
					//Cycle to the next element in the array
					token = line.next();
				}
				
				//Place the array inside the correct row and column of the HashMap.
				scavengedLayerObjects.get(row).put(col, array);
			}
			
		}
	}
	
	/** Returns a list of all of the objectIds that have been scavenged on the given TerrainLayer, denoted by its row and column. */
	public ArrayList<Integer> getScavengedLayerObjects(int row, int col)
	{
		//If no HashMap exists for the given row
		if(scavengedLayerObjects.get((Integer)row) == null)
		{
			//Create a new HashMap for the row, where the key is the column number, and the value is a list of scavenged objectIds.
			scavengedLayerObjects.put(new Integer(row), new HashMap<Integer, ArrayList<Integer>>());
		}
		
		//If no Array exists for the given column
		if(scavengedLayerObjects.get((Integer)row).get((Integer)col) == null)
		{
			//Populate the row and column's HashMap with an empty array of integers.
			scavengedLayerObjects.get((Integer)row).put(new Integer(col), new ArrayList<Integer>());
		}
		
		//Returns the Array containing the objectIds of GameObjects scavenged on the TerrainLayer.
		return scavengedLayerObjects.get((Integer)row).get((Integer)col);
	}
	
	/** Adds the given GameObject as a scavenged GameObject. It is added as a scavenged GameObject at the TerrainLayer where it resides, so that the GameObject
	 *  is never instantiated there again. */
	public void addScavengedLayerObject(GameObject gameObject)
	{
		//Delegates the GameObject's layer coordinates and objectId to the correct overloaded method.
		addScavengedLayerObject(gameObject.getTerrainCell().getRow(), gameObject.getTerrainCell().getCol(), gameObject.getObjectId());
	}
	
	/** Adds a scavenged object to the TerrainLayer, specified with the layer's cell coordinates. Accepts the objectId of the scavenged GameObject. 
	 *  Makes it so that the GameObject won't respawn the next time the layer is displayed. */
	public void addScavengedLayerObject(int row, int col, int objectId)
	{
		//Adds the objectId of the scavenged GameObject to the correct position in the HashMap. The first key of the Hashmap indicates the TerrainLayer row
		//where the object resides, and the second key holds the column where the same object resides. The value returned is an array of all objectIds that 
		//have been scavenged in that layer, to which the given objectId is added.
		scavengedLayerObjects.get(row).get(col).add(objectId);
	}
	
	/** Gets the loadout used by the player. */
	public Loadout getLoadout() {
		return loadout;
	}
	
	/** Sets the loadout used by the player. */
	public void setLoadout(Loadout loadout) {
		this.loadout = loadout;
	}

	/** Returns a HashMap containing an array for each TerrainLayer. This array specifies the objectIds of the GameObjects that have been scavenged on that
	 *  layer. First key is the TerrainLayer's row, and second is the TerrainLayer's column. */
	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getScavengedLayerObjects() {
		return scavengedLayerObjects;
	}

	/** Sets the HashMap containing objectId arrays for each TerrainLayer. These array specify the objectIds of the GameObjects that have been scavenged on a
	 *  certain layer. */
	public void setScavengedLayerObjects(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> scavengedLayerObjects) {
		this.scavengedLayerObjects = scavengedLayerObjects;
	}
}
