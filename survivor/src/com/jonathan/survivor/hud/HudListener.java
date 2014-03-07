package com.jonathan.survivor.hud;

public interface HudListener 
{
	/** Called when the Back Button is pressed on any Hud instance */
	void onBack();

	/** Called when the Backpack Button is pressed. This is the button on the top-left of the screen in exploration mode. */
	void onBackpackButton();
	
	/** Called when the Pause Button is pressed whilst in-game. */
	void onPauseButton();
	
	/** Called when the user presses/releases a button on a HUD. Toggles input handling on/off. Allows/disallows input from changing the player's state. */ 
	void toggleInput(boolean on);
	
	/** Called when the main menu button was pressed to transition to the main menu. */
	void switchToMainMenu();

	/** Called when the survival guide button is pressed in the backpack, in order to transition to the survival guide hud. */
	void switchToSurvivalGuide();
}
