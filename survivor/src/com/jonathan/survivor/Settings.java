package com.jonathan.survivor;

import com.jonathan.survivor.managers.ProfileManager;

public class Settings 
{
	/** Stores the profile where data will be saved. */
	private Profile profile;
	/** Used to save the profile held by the Settings instance. */
	private ProfileManager profileManager;
	/** Stores the world from which we retrieve player data to save. */
	private World world;
	
	/** Creates an empty settings instance */
	public Settings()
	{
	}
	
	/** Creates a Settings instance which uses the given ProfileManager to save the player profile. */
	public Settings(ProfileManager profileManager)
	{
		this(null, profileManager, null);
	}
	
	/** Accepts the profile to save, the ProfileManager used to save the profile, and world from which player data is retrieved. */
	public Settings(Profile profile, ProfileManager profileManager, World world)
	{
		//Stores the profile that will be saved.
		this.profile = profile;
		//Stores the ProfileManager which will save the profile.
		this.profileManager = profileManager;
		
		//Stores the world where player information will be retrieved and saved.
		this.world = world;
	}
	
	/** Saves player information to the profile registered to this instance. */
	public void save()
	{
		if(profile == null || profileManager == null || world == null)
			throw new RuntimeException("The Settings instance could not save the profile. Either the profile, profileManager, or world was not registered to this instance.");
		
		//Updates the profile's cell offset. The TerrainLevel's bottom-left layer coordinate is given, as the level expects this cell coordinate when being re-created.
		profile.setTerrainRowOffset(world.getTerrainLevel().getBottomLeftRow());
		profile.setTerrainColOffset(world.getTerrainLevel().getBottomLeftCol());
		
		//Saves the profile to the hard drive.
		profileManager.saveProfile(profile);
	}

	/** Retrieves the profile where the Settings instance saves player data. */
	public Profile getProfile() {
		return profile;
	}

	/** Sets the profile where player data will be saved. */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/** Gets the ProfileManager used to save the profile to the hard drive. */
	public ProfileManager getProfileManager() {
		return profileManager;
	}

	/** Sets the ProfileManager used to save the profile to the hard drive. */
	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}
	
	/** Gets the World where player information is read and saved to the hard drive. */
	public World getWorld() {
		return world;
	}

	/** Gets the World where player information is read and saved to the hard drive. */
	public void setWorld(World world) {
		this.world = world;
	}
}
