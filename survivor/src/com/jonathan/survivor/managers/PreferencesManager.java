package com.jonathan.survivor.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Manages the player's preferences, such as the last profile saved, and the volume of music and sounds.
 */

public class PreferencesManager 
{
	private static final String PREFS_NAME = "Survivor";
	
	private static final String PREFS_MUSIC_VOLUME = "M_Vol";
	private static final String PREFS_SOUND_VOLUME = "S_Vol";
	private static final String PREFS_PROFILES_SAVED = "N_Profs"; //The amount of profiles saved by the player
	private static final String PREFS_LAST_PROFILE = "L_Prof"; //The last profile loaded by the player.
	
	/** Stores the Preferences instance used to save and retrieve player save information. */
	private Preferences preferences;
	
	/** Creates a default PreferencesManager. */
	public PreferencesManager()
	{
	}
	
	/** Returns the Preferences instance used to modify and access the player's preferences. */
	private Preferences getPrefs()
	{
		//If the preferences data field is null
		if(preferences == null)
		{
			//Populate the Preferences variable by the instance with the given name.
	        preferences = Gdx.app.getPreferences(PREFS_NAME);
	    }
		
		return preferences;
	}
	
	/** Returns the amount of profiles that the user has saved on the hard drive. */
	public int getAmountProfiles()
	{
		return getPrefs().getInteger(PREFS_PROFILES_SAVED, 0); //Returns 0 if the integer is non-existant
	}
	
	/** Sets the amount of profiles that have been saved by the player on the hard drive. */
	public void setAmountProfiles(int numProfiles)
	{
		getPrefs().putInteger(PREFS_PROFILES_SAVED, numProfiles);
	}
	
	/** Returns the profileId of the last profile used by the player. */
	public int getLastProfile()
	{
		return getPrefs().getInteger(PREFS_LAST_PROFILE, 0);	//Returns 0 if the integer is non-existant
	}
	
	/** Saves the profileId of the last profile loaded by the player. */
	public void setLastProfile(int profileId)
	{
		getPrefs().putInteger(PREFS_LAST_PROFILE, profileId);
	}

	/** Called when a new profile is created. Allows the PreferencesManager to update the amount of profiles saved by the player. Also
	 *  allows the manager to record this profile as the last profile the user has loaded so that the user can continue from this profile
	 *  the next time the  game is loaded.
	 *  @param profileId The ID of the profile that has been created
	 */
	public void newProfileCreated(int profileId) 
	{
		//Sets the profile to be loaded when the player presses the "Continue" button in the GameSelectScreen.
		setLastProfile(profileId);
		
		//Increments the amount of profiles created by the player.
		setAmountProfiles(getAmountProfiles()+1);
		
		//Saves the changes made to the preferences.
		savePreferences();
	}
	
	/** Called when a profile is loaded. Accepts the id of the loaded profile. The given profile will be loaded the next time the user presses "Continue". */
	public void profileLoaded(int profileId)
	{
		//Sets the given profileId to be loaded when the player presses the "Continue" button.
		setLastProfile(profileId);
	}
	
	/** Called when a profile is deleted. Decrements the amount of saved profiles by one to ensure that the manager keeps track of the amount of
	 * profiles the user has saved. 
	 * @param profileId The ID of the profile that was deleted. If this profile is the one that is supposed to be loaded when "Continue" is pressed,
	 * the PreferencesManager changes the profile that will be loaded when "Continue" is pressed. */
	public void profileDeleted(int profileId)
	{
		//Decrements the amount of profiles that the user has saved so that the ProfileManager knows how many profiles to load
		setAmountProfiles(getAmountProfiles()-1);
		
		//If the profile that was deleted is the one that was last loaded by the player, the deleted profile is the one that is loaded when the "Continue" button is pressed.
		if(profileId == getLastProfile())
		{
			//Make it so that the profile that will be loaded when the "Continue" button is pressed is the first profile in the profileManager.
			setLastProfile(0);
		}
		//Else, if the id of the deleted profile is less than the ID of the last profile that was loaded
		else if(profileId < getLastProfile())
		{
			//The id of the profile which was last loaded by the user has decremented by one, in order to make up for the profile that was deleted. Therefore, decrement the 
			//lastProfile's id to make up for the fact that it has been shifted back by one by the ProfileManager.shiftProfiles() method.
			setLastProfile(getLastProfile()-1);
		}
		
		//Saves the changes made to the preferences.
		savePreferences();
	}
	
	/** Saves the preferences to the hard drive. */
	public void savePreferences()
	{
		//Ensures that the preferences are saved to the hard drive.
		getPrefs().flush();
	}
}
