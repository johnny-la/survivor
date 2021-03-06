package com.jonathan.survivor;

/* 
 * Listener which delegates important events from the World to the GameScreen. 
 */

public interface WorldListener 
{
	/** Called when an animation plays which overlays the screen. In this case, the GameScreen will pause the game until the animation is done. */
	public void onPlayAnimation();
	
	/** Pauses the currently-active Heads-up-display so that no buttons can be pressed.  */
	void pauseGui();
	
	/** Called when an animation finishes playing. This is for overlay animations which fill the screen. When complete, the GameScreen knows to resume the game. */
	public void onAnimationComplete();
	
	/** Delegated when the player switches to combat mode. Tells the GameScreen to switch to the combat HUD. */
	public void switchToCombat();

	/** Delegated after the KO animation plays in COMBAT mode. Tells the GameScreen to switch the HUD back to the Exploration HUD. */
	public void switchToExploration();
	
	/** Tells the GameScreen to switch to its GameOverHud. */
	public void gameOver();

	/** Called when the player's TELEPORT animation is done playing after crafting a teleporter. Transitions the player back to the main menu. */
	public void winGame();
}

