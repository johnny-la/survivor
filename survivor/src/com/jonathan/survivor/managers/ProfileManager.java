package com.jonathan.survivor.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.jonathan.survivor.Profile;

/*
 * Manages the profiles used by the user. Used to load profiles from the hard drive, create them and save them. Also used to access the current profile used by the 
 * user, or any other profile the user has.
 */

public class ProfileManager
{
	/** Stores the local file path for the profiles. Note that it ends with an underscore as it will be proceeded by "[id].json" */
	private static final String FILE_PATH = "data/profile_";
	
	/** Stores the maximum amount of profiles the user can have. */
	private final int maxProfiles;
	/** Stores an array of every profile that has been read by the ProfileManager to avoid re-reading JSON files. Note that the index into the profiles array for a
	 * given profile is the same as to the profile's id.*/
	private Profile[] profiles;
	/** Stores the current profile being used by the user. */
	private Profile currentProfile;
	
	/** Creates a profile manager, specifying the maximum amount of profiles the user can hold. */
	public ProfileManager(int maxProfiles)
	{
		//Stores the max amount of profiles the user can have.
		this.maxProfiles = maxProfiles;
		
		//Creates a new container for the profiles, with a length equal to the max amount of profiles the user can have.
		profiles = new Profile[maxProfiles];
	}
	
	/** Loads the profiles existing in the hard drive and populates the profiles:Profile[] array. */
	public void loadProfiles()
	{
		//deleteAllProfiles();
		//Cycles through the profiles:Profile[] array and populates it with any profiles which have already been created on the hard drive.
		for(int i = 0; i < maxProfiles; i++)
		{
			//Retrieves the profile for the current index. Second argument specifies that we don't want to create a new profile if it doesn't
			//already exist on the hard drive. We only want to retrieve an already created profile. 
			profiles[i] = getProfile(i, false);
		}
	}

	/** Returns the current profile being used by the user. The current profile is the last one that was retrieved from getProfile(profileId):Profile.
	 *  By default, if that method was never called, the last profile will be retrieved. */
	public Profile getCurrentProfile()
	{
		//If there is no current profile, throw an exception.
		if(currentProfile == null)
			throw new IllegalArgumentException("No profile has been retrieved from the hard drive. Thus, cannot retrieve a profile using ProfileManager.getCurrentProfile().");
		
		//Return the current profile being used by the user.
		return currentProfile;
	}
	
	/** Gets the profile with the given id, ranging from 0 to MAX_PROFILES-1. If the profile doesn't exist, it is not created nor saved to the hard drive. */
	public Profile getProfile(int profileId)
	{
		//Returns the profile with the given id. Second argument specifies that we  don't want to create a new profile and if it doesn't exist.
		return getProfile(profileId, false);
	}
	
	/** Gets the profile with the given id, ranging from 0 to MAX_PROFILES-1. Should be called after loadProfiles() to ensure that profiles have been loaded from
	 *  the hard drive.
	 * 
	 * @param profileId	The id of the profile, ranging from 0 to ProfileManager.MAX_PROFILES-1.
	 * @param createNew If true, a new profile will be created if it doesn't exist on the hard drive.
	 * @return The profile with the given ID
	 */
	public Profile getProfile(int profileId, boolean createNew)
	{
		//If the profile id is out of range of the profiles:Profile[] array, throw an exception.
		if(profileId < 0 || profileId >= maxProfiles)
			throw new IllegalArgumentException("Invalid profileId: " + profileId);
		
		//If the profile has already been saved inside the profiles:Profile[] array
		if(profiles[profileId] != null)
		{
			//Get the profile from the profile array, and set it as the current profile being used by the user, since it was the last one read.
			currentProfile = profiles[profileId];
			//Return the profile with the given id passed as an argument.
			return currentProfile;
		}
		
		//Creates a fileHandle pointing to the path of the profile with the given ID. The profile will be saved in a .json file with a path of "FILE_PATH[id].json".
		FileHandle profileFile = Gdx.files.local(FILE_PATH + profileId + ".json");
		
		//If the profile already exists on the hard drive, retrieve it and return it.
		if(profileFile.exists())
		{
			try
			{
				//Creates a new Json object to convert the file into an object.
				Json json = new Json();
				
				//Converts the entire profile file into a string.
				String text = profileFile.readString().trim();
				
				System.out.println("Json text: " + text);
				
				//Converts the text into a Profile object using Json.fromJson(class, fileText):Profile. Stores the new profile as the current profile.
				currentProfile = json.fromJson(Profile.class, text);
				//Stores the profile just created into the correct index (profileId) of the profiles array.
				profiles[profileId] = currentProfile;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				
				//If an exception arises, the profile's JSON file couldn't be read. So, create a new profile, effectively erasing the old one.
				createProfile(profileId);
			}
		}
		//Else, if the profile file doesn't not exist on the hard drive, we check 'createNew'. If this parameter is true, we want to create a new profile if it doesn't exist.
		else if(createNew)
		{
			//Create a new profile with the given ID which starts at the beginning of the game.
			createProfile(profileId);
		}
		
		//Returns the profile we either retrieved from the hard drive or created from scratch.
		return profiles[profileId];
	}
	
	/** Creates a profile with the given profile ID, and saves it to the hard drive. Also sets the created profile to be the current user profile. */
	private void createProfile(int profileId)
	{
		//Create a new profile with the given id passed as a parameter. Sets it as the current profile being used by the user.
		currentProfile = new Profile(profileId);
		//Stores the profile we just created in the correct index of the profiles:Profile[] array.
		profiles[profileId] = currentProfile;
		//Saves the profile we just created to the hard drive as a JSON file.
		saveProfile(profiles[profileId]);
	}
	
	/** Saves the profile to the hard drive. The file name depends on the ID of the profile passed as a parameter. */
	public void saveProfile(Profile profile)
	{
		//If the profile we want to save is null, throw an exception.
		if(profile == null)
			throw new IllegalArgumentException("Attempting to save null Profile");
		
		//Create a fileHandle pointing the file path containing the profile. This file path is: "FILE_PATH[id].json". We will write the profile to this path.
		FileHandle profileFile = Gdx.files.local(FILE_PATH + profile.getProfileId() + ".json");
		
		//Creates a new Json object to convert a Profile object into JSON text.
		Json json = new Json();
		
		//Converts the profile object into a string readable by JSON using Json.toJson(Serializable).
		String text = json.toJson(profile);
		
		//Writes the String containing the Profile object's information into the profile's JSON file, contained by profileFile:FileHandle.  
		profileFile.writeString(text, false);
	}
	
	/** Saves the current profile to the hard drive as a JSON file. */
	public void saveCurrentProfile()
	{
		//Saves the current profile to the hard drive as a JSON file.
		saveProfile(currentProfile);
	}
	
	/** Deletes a profile with the given ID from the hard drive. */
	public void deleteProfile(int profileId)
	{
		//Get a file handle to the profile's JSON file. This is found under the path "FILE_PATH[profileId].json"
		FileHandle profileFile = Gdx.files.local(FILE_PATH + profileId + ".json");
		
		//Delete the profile from the hard drive.
		profileFile.delete();
		
		//Empties the reference to profile from the array. Like this, the profile is lost from memory.
		profiles[profileId] = null;
	}
	
	/** Deletes all profiles from the hard drive, if they exist. */
	public void deleteAllProfiles()
	{
		//Cycles through the profiles
		for(int id = 0; id < maxProfiles; id++)
		{
			//Deletes the profiles
			deleteProfile(id);
		}
	}
	
	/** Gets the maximum amount of profiles the user can have. */
	public int getMaxProfiles()
	{
		return maxProfiles;
	}
}
