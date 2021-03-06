package com.jonathan.survivor.utils;

import com.badlogic.gdx.audio.Music;

public class MusicUtils 
{
	/** Stores the amount it takes for music to fade out when the fadeOut(Music):void method is called. */
	public static final float FADE_OUT_TIME = 2f;
	
	/** Stores the time of the application when Music instance passed to fadeOut() has started fading out. */
	private static float fadeOutStart;
	
	/** Fades out the given music in 'MusicUtils.FADE_OUT_TIME' seconds. Note: Only one Music instance can fade out at a time. */
	public static void fadeOut(Music music)
	{
	}
}
