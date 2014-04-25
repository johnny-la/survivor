package com.jonathan.survivor;

/**
 * Sends events to the GameScreen whenever a particular sound needs to be played.
 */

public interface SoundListener 
{
	public enum Sound {
		PLAYER_FOOTSTEP, PLAYER_JUMP, PLAYER_AXE_SWING, PLAYER_HIT, PLAYER_AXE_HIT, PLAYER_FIRE, ZOMBIE_HIT_HEAD,
		EXPLORATION_MUSIC, COMBAT_MUSIC
	}
	
	/** Sends an event to the GameScreen to play the given sound. */
	void play(Sound sound);
}
