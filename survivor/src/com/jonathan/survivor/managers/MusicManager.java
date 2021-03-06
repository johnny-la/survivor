package com.jonathan.survivor.managers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
 
/*
 * Plays one music file at a time. Changing the volume of the manager changes the volume of all subsequently-playing music files.
 */

public class MusicManager
{
	/** Stores the music which is currently being played by the manager. Note that, if music is disabled, this variable is populated with the music that will
	 *  be played once the music is re-enabled */
	private Music music;
	
	/** Stores the volume to play all music clips at */
	private float volume = 1.0f;
	/** Stores whether or not music is enabled. */
	private boolean musicEnabled = true;
	
	public MusicManager()
	{
	}
	
	/** Plays a music instance at the managers volume setting. If music is disabled, the music is put on standby until music is enabled. */
	public void play(Music music)
	{
		//If the music instance currently being played is the same as the music instance passed to the method, return. We don't want to re-start music that is
		//already playing.
		if(this.music == music)
			return;
		
		//If a certain music is already playing
		if(this.music != null)
		{
			try
			{
				//Stop the currently-playing music before playing the new song.
				this.music.stop();
			}
			//The music.stop() statement can throw a NullPointerException if the music.dispose() was called, and the music member variable was not emptied
			catch(NullPointerException ex)
			{
				//Nullify the reference to the disposed music instance.
				this.music = null;
			}
		}
		
		//Store the new music instance in the MusicManager. This is the music the manager is set to play.
		this.music = music;

		//Set the volume of the music to the current volume setting of the manager
		music.setVolume(volume);
		//Loop the music.
		music.setLooping(true);
		
		//If music is enabled on the manager
		if(musicEnabled)
			//Play the music file.
			music.play();
	}
	
	
	/** Stop the current playing music. */
	public void stop()
	{
		//If the Music Manager holds a music instance
		if(music != null)
			//Stop the music.
			music.stop();
		
	}
	
	/** Set the volume of the manager. Any subsequent music will be played at this volume, range zero (quiet) to 1 (loudest). */
	public void setVolume(float volume)
	{
		//Stores the new volume of the manager. 
		this.volume = volume;
		//If there is a music file stored inside the manager.
		if(music != null)
			//Set the volume of the music to the new volume.
			music.setVolume(volume);
	}
	
	/** Set whether the music is enabled or not. If disabled, music does not play. */
	public void setEnabled(boolean enabled)
	{
		//Change whether or not music is enabled.
		this.musicEnabled = enabled;
		
		//If the music has been enabled and the music manager contains a music file
		if(musicEnabled && music != null)
			//Play the music stored by the manager.
			music.play();
	}
	
}
