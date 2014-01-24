package com.jonathan.survivor;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

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
		
	}
}
