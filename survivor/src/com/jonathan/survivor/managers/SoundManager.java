package com.jonathan.survivor.managers;

import com.badlogic.gdx.audio.Sound;

/*
 * Manages sound effects by controlling the volume of every sound effect.
 */

public class SoundManager
{
	/** Stores the volume to play all sound clips at by default */
	private float volume = 1.0f;
	/** Stores whether or not sound is enabled. */
	private boolean soundEnabled = true;
	
	public SoundManager()
	{
	}
	
	/** Plays a sound instance at maximum volume relative to the sound manager's volume. If music is disabled, the music is put on standby until music is enabled. */
	public void play(Sound sound)
	{
		//Plays the sound at the current volume of the sound manager. This is done by passing in 'one'. This will make the sound play at max volume relative to the
		//sound manager's volume. E.g., if the volume member variable is '0.75', the sound will be played at volume 0.75.
		play(sound, 1);
	}
	
	/** Plays a sound instance at a custom volume. If sound is disabled, the sound is not played. */
	public void play(Sound sound, float volume)
	{
		//Throw an unchecked exception if the sound instance is null.
		if(sound == null)
			throw new IllegalArgumentException("Sound instance passed to SoundManager.play(Sound) is null");

		//If sound is enabled on the manager, play the sound.
		if(soundEnabled)
		{
			//Takes the volume passed as a parameter, and multiplies it by the sound manager's volume. So, if the sound wants to be played at 50% volume, the argument
			//will have been 0.5. If the SoundManager's volume is '0.5' also, we get 0.5*0.5 = 0.25. Thus, the sound will play at 0.25 volume, making the volume relative
			//to the sound manager's volume.
			volume *= this.volume;
			//Play the sound file at the volume of the sound manager.
			sound.play(volume);
		}
	}
	
	/** Set the volume of the manager. Any subsequent sound will be played at this volume by default. The volume has range zero (quiet) to 1 (loudest). */
	public void setVolume(float volume)
	{
		//Stores the new volume of the manager. 
		this.volume = volume;
	}
	
	/** Set whether the music is enabled or not. If disabled, music does not play. */
	public void setEnabled(boolean enabled)
	{
		//Change whether or not music is enabled.
		this.soundEnabled = enabled;

	}
	
}
