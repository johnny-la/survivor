package com.jonathan.survivor;

/**
 * Sends events to the GameScreen whenever a particular sound needs to be played.
 */

public interface SoundListener 
{
	public enum Sound {
		PLAYER_FOOTSTEP, PLAYER_JUMP, PLAYER_FALL, PLAYER_SWING, PLAYER_HIT, PLAYER_AXE_HIT, PLAYER_HIT_TREE, PLAYER_PULL_OUT_WEAPON, PLAYER_CHARGE, PLAYER_FIRE, ITEM_DROP, ITEM_PICKUP, 
		ZOMBIE_HIT, ZOMBIE_CHARGE_START, ZOMBIE_CHARGE, EARTHQUAKE, 
		EXPLORATION_MUSIC, ZOMBIE_ALERT_MUSIC, ENTER_COMBAT_MUSIC, COMBAT_MUSIC, 
	}
	
	/** Sends an event to the GameScreen to play the given sound. */
	void play(Sound sound);
}
