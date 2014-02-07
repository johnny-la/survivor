package com.jonathan.survivor.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.World;

public abstract class Hud 
{
	/** Stores the stage where 2d widgets will be placed and drawn. */
	protected Stage stage;
	
	/** Stores the Assets singleton of the game used to fetch assets to draw the HUD. */
	protected Assets assets = Assets.instance;
	
	/** Stores the world that any Hud elements can call methods from in case of a button press. */
	protected World world;
	
	/** Stores the Listener where Hud events are delegated. */
	protected HudListener hudListener;
	
	/** Accepts the stage where 2d widgets will be contained and drawn, and the world, where input events will be dispatched. */
	public Hud(Stage stage, World world)
	{
		//Stores the arguments in their respective member variables.
		this.stage = stage;
		this.world = world;
	}
	
	/** Registers the listener where Hud events will be delegated. */
	public void addHudListener(HudListener hudListener)
	{
		this.hudListener = hudListener;
	}
	
	/** Draws the Hud to the screen using the stage. */
	public void draw(float deltaTime)
	{
		//Update the stage and its actors
		stage.act(deltaTime);
		//Draw the stage, along with its 2d widgets.
		stage.draw();
	}
	
	/** Resets the widgets on the stage. Called when screen is resized. Given parameters are the size that the Hud should occupy in pixels. */
	public abstract void reset(float guiWidth, float guiHeight);
}
