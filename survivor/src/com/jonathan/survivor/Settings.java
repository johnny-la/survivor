package com.jonathan.survivor;

import com.badlogic.gdx.utils.TimeUtils;
import com.jonathan.survivor.World.WorldState;
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
		
		//The profile is no longer new, as it has just been saved. As such, the profile will know that it has no longer just been created.
		profile.setFirstTimeCreate(false);
		
		//Update the profile's date of modification to the current time of the system, since the profile was just saved.
		profile.getDateLastModified().setTime(TimeUtils.millis());
		
		//Updates the profile's cell offset. The TerrainLevel's bottom-left layer coordinate is given, as the level expects this cell coordinate when being re-created.
		profile.setTerrainRowOffset(world.getTerrainLevel().getBottomLeftRow());
		profile.setTerrainColOffset(world.getTerrainLevel().getBottomLeftCol());
		
		//Saves the last x-position of the player relative to his layer inside the profile.
		saveLastXPos();
		
		//Saves the profile to the hard drive.
		profileManager.saveProfile(profile);
	}

	/** Saves the last x-position of the player before saving the profile. Note that this position is relative to the layer where he currently resides. */
	private void saveLastXPos() 
	{
		//Stores the last x-position of the player before saving the profile, relative to the layer where he currently resides.
		float lastXPos = 0;
		
		//Stores the left-most x-position of the layer where the player resides. Note that the player always resides on the center-most layer of the TerrainLevel.
		float layerLeftXPos = world.getTerrainLevel().getCenterLayer().getLeftPoint().x;
		
		//If the player is currently in exploration state, traversing the world
		if(world.getWorldState() == WorldState.EXPLORING)
		{
			//The last x-position of the player is his current x-position, minus the left-most x-position of the layer where he resides ('lastXPos' is relative to the layer's x-position).
			lastXPos = world.getPlayer().getX() - layerLeftXPos;
		}
		//If the player and the world were in COMBAT state upon saving settings, the player's last x-position must be computed a different way
		if(world.getWorldState() == WorldState.COMBAT)
		{
			//The player's last x-position in the world is stored in the 'previousPlayerX' member variable of the CombatLevel. This is subtracted by the left-most x-position of the
			//layer where the player resides, since the 'lastXPos' position is relative to the position of the layer where the player resides.
			lastXPos = world.getCombatLevel().getPreviousPlayerX() - layerLeftXPos;
		}
		
		//Stores the x-position of the player relative to his layer upon saving the profile. Allows the player to respawn at the same place on profile load.
		profile.setLastXPos(lastXPos);
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
