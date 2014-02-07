package com.jonathan.survivor.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jonathan.survivor.World;
import com.jonathan.survivor.managers.ItemManager;

public class CraftingHud extends Hud
{
	/** Holds the ItemManager instance. Its purpose is to give access to Item instances, which give an item's information and sprite. */
	ItemManager itemManager;

	/** Accepts the Stage instance where widgets are drawn, and the world, used to manipulate the world according to button presses. The ItemManager
	 *  is accepted in order to fetch internally-pooled Item instances in order to draw the items in the player's inventory. */
	public CraftingHud(Stage stage, World world, ItemManager itemManager) 
	{
		super(stage, world);
		
		//Stores the itemManager instance used to access Item instances in order to draw the items in the inventory.
		this.itemManager = itemManager;
	}

	/** Called whenever the current Hud of the game switches to the backpack Hud. Used to reset the stage to hold the widgets of the Backpack Hud. Also called
	 *  when the screen is resized. Thus, any widgets can be repositioned or rescaled accordingly.
	 *  
	 *  @param guiWidth The width in pixels that the gui should occupy.
	 *  @param guiHeight the height in pixels that the gui should occupy.
	 */
	@Override
	public void reset(float guiWidth, float guiHeight) 
	{
		//Clears the stage to make room for the crafting hud's widgets.
		stage.clear();
		
	}

}
