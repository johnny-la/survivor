package com.jonathan.survivor.renderers;

public interface HudListener 
{
	/** Called when the Back Button is pressed on any Hud instance */
	void onBack();

	/** Called when the Backpack Button is pressed. This is the button on the top-left of the screen in exploration mode. */
	void onBackpackButton();
	
	/** Called when the Pause Button is pressed whilst in-game. */
	void onPauseButton();
}