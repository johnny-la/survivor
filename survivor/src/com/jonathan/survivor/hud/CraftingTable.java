package com.jonathan.survivor.hud;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.inventory.Inventory;
import com.jonathan.survivor.managers.CraftingManager.Item;
import com.jonathan.survivor.managers.ItemManager;

/*
 * Displays a table with six item slots and one crafted item slot. 
 */

public class CraftingTable 
{
	/** Stores the amount of columns of ItemCells in the table. */
	private static final int NUM_COLUMNS = 3;
	
	/** Holds the number of items that can be placed inside the crafting table. */
	private static final int NUM_ITEMS = 6;
	
	/** Stores the width and height of each item button. Note that this is the size of the ItemCells' backgrounds, not of the item's image itself. */
	private static final float BUTTON_WIDTH = 32;
	private static final float BUTTON_HEIGHT = 32;
	
	/** Holds the horizontal distance between each item button. */
	private static final float BUTTON_PAD_RIGHT = 15;
	
	/** Holds the vertical distance between each item button. */
	private static final float BUTTON_PAD_BOTTOM = 7;
	
	/** Holds the color of the item box which acts as the small box behind each item sprite. */
	private static final Color ITEM_BOX_COLOR = new Color(0.5f, 0.5f, 0.5f, 1);
	
	/** Stores the color of the text displaying the quantity of each item in the crafting table. */
	private static final Color TEXT_COLOR = Color.WHITE;
	
	/** Holds the ItemManager instance from which the Sprites for each item is retrieved. */
	private ItemManager itemManager;
	
	/** Holds the inventory from which the list of player items is retrieved. */
	private Inventory inventory;
	
	/** Holds the universal Assets singleton used to retrieve the visual assets needed to create the inventory list. */
	private Assets assets = Assets.instance;
	
	/** Stores the ClickListener used by the CraftingHud. All button clicks in the table are delegated to this listener to be handled by the CraftingHud. */
	private ClickListener buttonListener;
	
	/** Stores the table containing all of the items in the crafting table. */
	private Table table;
	
	/** Holds an array containing the six cells which hold an item in the table. */
	private Array<ItemCell> itemCells;
	
	/** Stores the ItemCell displaying the item that is crafted as a result of the items in the crafting table. */
	private ItemCell craftedItemCell;
	
	/** Holds the image displaying the arrow below the grid of items. */
	private Image arrowImage;
	
	/** Maps an Item subclass with a item cell displaying this item. */
	private HashMap<Class, ItemCell> buttonMap;
	
	/** Accepts the itemManager from which to retrieve the items' sprites, the inventory from which to retrieve the player's items,
	 * the ClickListener to which button clicks will be delegated, and the height of the list.
	 */
	public CraftingTable(ItemManager itemManager, Inventory inventory, ClickListener buttonListener)
	{
		//Stores the parameters in their respective member variables
		this.itemManager = itemManager;
		this.inventory = inventory;
		this.buttonListener = buttonListener;
		
		//Creates and populates the crafting table, along with the widgets inside it.
		generateTable();
	}
	
	/** Called when the crafting table's widgets and its table must be created. */
	public void generateTable() 
	{
		//Creates the table which will contain all of the crafting table's widgets.
		table = new Table();
		
		//Instantiates the array of ItemCells which contains the six cells in the crafting table.
		itemCells = new Array<ItemCell>();
		
		//Populates the itemCells array with as many cells as there are items.
		for(int i = 0; i < NUM_ITEMS; i++)
		{
			//Creates a new, empty item cell to put in the crafting table.
			ItemCell itemCell = new ItemCell();
			
			//Populates each element with an empty item cell.
			itemCells.add(itemCell);
			
			//Adds the ItemCell's button into the table. Sets the width and height of the button to ensure that the cell takes up the right size in the table,
			//and adds appropriate padding to the right and the bottom of the cell's buttons.
			table.add(itemCell.getButton()).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padRight(BUTTON_PAD_RIGHT).padBottom(BUTTON_PAD_BOTTOM);
			
			//If we have filled up a row
			if((i+1) % NUM_COLUMNS == 0 && i != 0)
			{
				//Removes the padding on the right of the button if it is the last button in the row. Like this, no extra width is added on the table.
				table.getCell(itemCell.getButton()).padRight(0);
				
				//Skip a row.
				table.row();
			}
		}
		
		//Creates an Image instance for the picture of the arrow. Created using a sprite inside the hud atlas which is stored inside the hudSkin.
		arrowImage = new Image(assets.hudSkin.getDrawable("CraftingArrow"));
		//Resize the arrow image to ensure that, no matter the atlas size chosen, the arrow takes up the same size on the screen.
		arrowImage.setSize(arrowImage.getWidth() / assets.scaleFactor, arrowImage.getHeight() / assets.scaleFactor);
		
		//Creates a new empty itemCell holding the item which is crafted using the items currently in the crafting table.
		craftedItemCell = new ItemCell();
		
		//Registers the buttonListener to the craftedItemCell's button. As such, since the buttonListener belongs to the CraftingHud, it will be notified of a button click.
		craftedItemCell.getButton().addListener(buttonListener);
		
		//Add the arrow image to the center of the table, making it span as many columns as there are in the table.
		table.add(arrowImage).colspan(NUM_COLUMNS).padBottom(5).center().width(arrowImage.getWidth()).height(arrowImage.getHeight());
		//Skip a row.
		table.row();
		//Add the craftedItemCell's button to the table, make it span as many columns as there are in the table, and center it to the middle of the table.
		table.add(craftedItemCell.getButton()).colspan(NUM_COLUMNS).center().width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
		
		//Set the table to fill the stage. If excluded, the table does not appear.
		table.setFillParent(true);
	}
	
	/** Adds the given amount of the item inside a cell in the crafting table. */
	public void addItem(Class itemClass, int quantity)
	{		
		//Cycles through each item cell in the crafting table.
		for(int i = 0; i < itemCells.size; i++)
		{
			//Stores the ItemCell the loop is iterating through.
			ItemCell itemCell = itemCells.get(i);
			
			//If the item of the given class is already inside a cell of the crafting table, simply increment the quantity of items in the cell.
			if(itemClass.equals(itemCell.getItemClass()))
			{
				//Add the specified quantity of items to the cell.
				itemCell.addQuantity(quantity);
				
				return;
			}
		}
		
		/* If the above for loop did not return the method, the given item is not already in the crafting table. Thus, add it there. */
		
		//Cycle through all the cells in the crafting table until an empty one is found.
		for(int i = 0; i < itemCells.size; i++)
		{
			//Stores the ItemCell that is being iterated through.
			ItemCell itemCell = itemCells.get(i);
			
			//If the cell is empty, put the item inside this cell.
			if(itemCell.isEmpty())
			{
				//Sets the cell to display the given item belonging to the given item class.
				itemCell.setItemClass(itemClass);
				//Add the given amount of items to the cell.
				itemCell.addQuantity(quantity);
				
				//Only one itemCell has to be filled with the given item.
				return;
			}
		}
	
	}
	
	/** Sets the item displayed in craftedItem slot. This is the ItemCell where the crafted item is shown. If null, the slot is emptied. */
	public void setCraftedItem(Item craftedItem) 
	{
		//If the crafted item is null, empty the craftedItemCell
		if(craftedItem == null)
		{
			//Empty the craftedItemCell.
			craftedItemCell.empty();
		}
		//Else, if the given item is not null, populate the craftedItemCell with the correct item.
		else
		{
			//Empty the craftedItemCell so that the quantity of the item is reset to zero before calling the 'ItemCell.addQuantity()' method.
			craftedItemCell.empty();
			
			//Set the contained item class of the craftedItemCell to that of the Item argument. Ensures that the correct item is displayed inside the cell.
			craftedItemCell.setItemClass(craftedItem.getItem());
			
			//Add the given quantity to the craftedItemCell so that the correct amount of items are displayed in the craftedItemSlot.
			craftedItemCell.addQuantity(craftedItem.getQuantity());
		}
		
	}
	
	/** Called when the user leaves the crafting menu. Removes all of the items in the crafting table, and places them back into the player's inventory. 
	 *  If the boolean argument is true, the items inside the crafting table are put back in the player's inventory. If not, they are lost. */
	public void emptyTable(boolean transferToInventory)
	{
		//Cycles through all of the cells in the crafting table.
		for(int i = 0; i < itemCells.size; i++)
		{
			//Stores the ItemCell that is being cycled through.
			ItemCell itemCell = itemCells.get(i);
			
			//If the itemCell is already empty, skip this itemCell.
			if(itemCell.isEmpty())
				continue;
			
			//Holds the class representing the item placed in the cell.
			Class itemClass = itemCell.getItemClass();
			
			//If the items have to be transfered back to the inventory
			if(transferToInventory)
				//Increments the amount of items in the inventory by the amount that were inside the crafting table's cell. Effectively inserts the items from the cells 
				//back into the inventory.
				inventory.addItem(itemClass, itemCell.quantity);
			
			//Empty the item cell so that no item is displayed on it.
			itemCell.empty();
		}
		
		//Empty the cell where the preview crafted item is placed.
		craftedItemCell.empty();
	}

	/** Returns the table containing all the widgets in the crafting table. */
	public Table getTable()
	{
		return table;
	}
	
	/** A cell representing an item as a button in the crafting table. */
	private class ItemCell
	{
		private Class itemClass;	//Stores the Item subclass represented by this cell.
		private int quantity;	//Holds the amount of items of the same type contained in the cell.
		
		private ImageTextButtonStyle buttonStyle;	//Holds the style which dictates the look of the button.
		private ImageTextButton button;	//Stores the button which displays the given item and its quantity in the crafting table.
		private Image itemImage; //Holds the image displaying the item on the cell.
		Image itemBoxImage; //Stores the image displaying the grey box background to each button.
		
		/** Creates a default ItemCell with no item inside. */
		public ItemCell()
		{
			//Creates the button style which dictates the look of this item cell/button.
			buttonStyle = new ImageTextButtonStyle();
			buttonStyle.font = assets.moonFlowerBold_38;
			buttonStyle.fontColor = TEXT_COLOR;
			buttonStyle.pressedOffsetX = 1;
			buttonStyle.pressedOffsetY = -1.5f;
			
			//Instantiates a button for the image cell using the button style created above. The button will be empty by default.
			button = new ImageTextButton("", buttonStyle);
			//Registers the buttonListener to each button in the crafting table. As such, the CraftingHud will receive all button clicks from the crafting table.
			button.addListener(buttonListener);
			
			//Creates the box which is placed behind the item sprites. Acts as a background to each item image. Uses the "ItemBox" sprite from the HUD atlas.
			itemBoxImage = new Image(assets.hudSkin.getDrawable("ItemBox"));
			
			//Sets the color of the itemBox's image
			itemBoxImage.setColor(ITEM_BOX_COLOR);
			//Sets the size of the item box so that all of them are the same size.
			itemBoxImage.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
			
			//Adds the itemBoxImage before everything else in the button.
			button.addActorAt(0, itemBoxImage);
			
			//Creates the image which will display the item on top of the button.
			itemImage = new Image();
			
			//Adds the itemImage before the text in the button.
			button.addActorBefore(button.getLabel(), itemImage);
		}
		
		/** Adds the given amount of items to this cell. Can be negative */
		public void addQuantity(int amount)
		{
			//Updates the quantity of the items held in this cell.
			quantity += amount;
			
			//Updates the button's text to display the new quantity of items in the cell.
			button.setText(Integer.toString(quantity));
			
			//If the quantity of items in the cell is one or zero, don't display a number on the button
			if(quantity <= 1)
			{
				//Empty the text on the button.
				button.setText("");
				
				//If the item has just been removed from the crafting table
				if(quantity == 0)
				{
					//Empty the cell.
					empty();
				}
			}
			
		}
		
		/** Updates the button to display the image for the given class. */
		public void setItemDrawable(Class itemClass)
		{
			//If the itemClass passed as an argument is null, remove the image from the cell, since no item belongs on it.
			if(itemClass == null)
			{
				//Remove the drawable from the itemImage so that no item picture is displayed on the cell.
				itemImage.setDrawable(null);
				
				return;	//Return this method, since the below statements would cause NullPointerExceptions.
			}
			
			//Optimization.
			/*if(itemImage.getDrawable() != null)
			{
				SpriteDrawable previousDrawable = (SpriteDrawable) itemImage.getDrawable();
				itemManager.freeSprite(previousDrawable.getSprite());
			}*/
			
			//Retrieves the sprite corresponding to the item class, wraps it into a SpriteDrawable, and stores it in a local variable.
			SpriteDrawable drawable = new SpriteDrawable(itemManager.getSprite(itemClass));
			
			//Changes the image being displayed on the button by changing the drawable on the itemImage.
			itemImage.setDrawable(drawable);
			
			//Sets the size of the image to correspond to the size of the sprite. Like this, the item image is always the same size as the item's sprite.
			itemImage.setSize(drawable.getSprite().getWidth(), drawable.getSprite().getHeight());
			
			//Sets the item image's position so that it is at the center of the button.
			itemImage.setPosition(BUTTON_WIDTH/2 - itemImage.getWidth()/2, BUTTON_HEIGHT/2 - itemImage.getHeight()/2);
		}
		
		/** Resets the ItemCell to an empty cell. */
		public void empty()
		{
			//Set the quantity stored in the cell to zero.
			quantity = 0;
			
			//Empty the text on the button.
			button.setText("");
			
			//Remove the itemClass being held by the cell, since the cell is supposed to be emptied.
			setItemClass(null);
		}
		
		/** Returns true if this cell is not filled with an item. */
		public boolean isEmpty()
		{
			//The cell is empty if it does not hold an itemClass.
			return itemClass == null;
		}
		
		/** Sets the item class held by the cell. */
		public void setItemClass(Class itemClass)
		{
			//Updates the item class held by the cell, which dictates which item the cell is holding.
			this.itemClass = itemClass;
			
			//Updates the item image on the button. Retrieves a sprite corresponding to the item class, and places it on the buttonwraps it into a SpriteDrawable, and passes it to setItemDrawable(...).
			setItemDrawable(itemClass);
		}
		
		/** Returns the item class held by the cell. */
		public Class getItemClass()
		{
			return itemClass;
		}
		
		/** Returns the button which displays the item held by this cell. */
		public ImageTextButton getButton()
		{
			return button;
		}
	}
	
	/** Returns true if the given actor is an item button inside the crafting table. An item button is every button in the grid of six buttons the crafting table. */
	public boolean isItemButton(Actor actor)
	{
		//Cycles through each itemCell contained in the crafting table
		for(int i = 0; i < itemCells.size; i++)
		{
			//Return true if the actor corresponds to one of the buttons in the itemCells.
			if(buttonEquals(actor, itemCells.get(i)))
				return true;
		}
		
		//If we are here, the actor is not an itemButton in the crafting table.
		return false;
	}
	
	/** Returns the class belonging to the given button. That is, each itemButton in the crafting table represents an item class. This method returns the class represented
	 *  by the button. */
	public Class getItemButtonClass(Actor actor)
	{
		//Cycles through each itemCell contained in the crafting table
		for(int i = 0; i < itemCells.size; i++)
		{
			//Return true if the actor corresponds to one of the widgets in one of the itemCells.
			if(buttonEquals(actor, itemCells.get(i)))
			{
				//Returns the class which represents the item held by this button.
				return itemCells.get(i).getItemClass();
			}
		}
		
		//Return null if the button is not contained inside the crafting table.
		return null;
	}
	
	/** Returns true if the given Actor is the craftedItemButton. That is, the button which contains the preview of the item crafted from the items in the crafting table. */
	public boolean isCraftedItemButton(Actor actor)
	{
		//Returns true of the given actor is the same as the button in the craftedItemCell.
		return actor.equals(craftedItemCell.getButton());
	}
	
	/** Returns true if the given item is contained inside the crafting table. */
	public boolean containsItem(Class itemClass) 
	{
		//Cycle through all of the item cells contained in the table.
		for(int i = 0; i < NUM_ITEMS; i++)
		{
			//If any of the item cells contain the given item
			if(itemCells.get(i).getItemClass() == itemClass)
				//Return true, since the item is contained in one of the item cells in the crafting table.
				return true;
		}
		
		//If this statement is reached, the given item is not contained in the crafting table. Thus, return false.
		return false;
	}
	
	/** Returns true if the crafting table cannot take any more items. */
	public boolean isFull()
	{
		//Cycle through all of the item cells in the table.
		for(int i = 0; i < NUM_ITEMS; i++)
		{
			//If any of the item cells are empty, the table is not full
			if(itemCells.get(i).getItemClass() == null)
				//Thus, return false;
				return false;
		}
		
		//If this statement is reached, the table is full. Thus, return true.
		return true;
	}
	
	/** Returns true if the actor corresponds to the itemCell. That is, if the actor is contained inside the itemCell's button, the actor is technically equal to the itemCell. 
	 * Used to verify whether the actor which delegated a button click corresponds to a certain ItemCell. */
	private boolean buttonEquals(Actor actor, ItemCell itemCell)
	{
		//Returns true if the actor corresponds to any of the widgets in the given ItemCell.
		if(actor.equals(itemCell.getButton()) || actor.equals(itemCell.itemImage) || actor.equals(itemCell.itemBoxImage) || actor.equals(itemCell.getButton().getLabel()))
			return true;
		
		//Else, return false if no widgets in the ItemCell correspond to the given actor.
		return false;
	}
}
