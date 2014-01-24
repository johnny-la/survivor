package com.jonathan.survivor.renderers;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.World;

public abstract class HudRenderer 
{
	/** Stores the stage where 2d widgets will be placed and drawn. */
	protected Stage stage;
	
	/** Stores the Assets singleton of the game used to fetch assets to draw the HUD. */
	protected Assets assets = Assets.instance;
	
	/** Stores the world that any Hud elements can call methods from in case of a button press. */
	protected World world;
	
	/** Accepts the stage where 2d widgets will be contained and drawn, and the world, where input events will be dispatched. */
	public HudRenderer(Stage stage, World world)
	{
		//Stores the arguments in their respective member variables.
		this.stage = stage;
		this.world = world;
	}
	
	/** Draws the Hud to the screen using the stage. */
	public abstract void draw(float deltaTime);
	
	/** Resets the widgets on the stage */
	public abstract void reset();
}
