package com.jonathan.survivor.hud;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.StringBuilder;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.inventory.Inventory;
import com.jonathan.survivor.managers.ItemManager;

public class InventoryList 
{
	/** Stores the width of the list in pixels for the target (480x320) resolution. */
	public static final float LIST_WIDTH = 210;

	/** Holds the distance between the left of the button and the left starting point of the text. */
	private static final float BUTTON_TEXT_DISTANCE = 42;
	
	/** Holds the distance between the left of the button and the center of each item image. */
	private static final float BUTTON_IMAGE_DISTANCE = 20;

	/** Stores the size of the item box which acts as a background behind each item image. */
	private static final float ITEM_BOX_WIDTH = 32;
	private static final float ITEM_BOX_HEIGHT = 32;
	
	/** Holds the color of the item box which acts as the small box behind each item sprite. */
	private static final Color ITEM_BOX_COLOR = new Color(0.5f, 0.5f, 0.5f, 1);
	
	/** Stores the color of the name of each item. */
	private static final Color TEXT_COLOR = new Color(0.2f, 0.2f, 0.2f, 1);
	/** Stores the color of the name of each item when the item is pressed. */
	private static final Color TEXT_DOWN_COLOR = new Color(0.0f, 0.4f, 0.8f, 1);
	
	/** Holds the inventory from which the list of player items is retrieved. */
	private Inventory inventory;
	
	/** Holds the ItemManager instance from which the Sprites for each item is retrieved. */
	private ItemManager itemManager;
	
	/** Holds the universal Assets singleton used to retrieve the visual assets needed to create the inventory list. */
	private Assets assets = Assets.instance;
	
	/** Stores the ClickListener used by the CraftingHud. All button clicks in the table are delegated to this listener to be handled by the CraftingHud. */
	private ClickListener buttonListener;
	
	/** Stores the ScrollPane which allows the item list stored inside the itemTable to be scrollable. */
	private ScrollPane scrollPane;
	
	/** Stores the table where all item buttons are placed. */
	private Table buttonTable;
	
	/** Holds the table where the scroll pane is contained. This is the high-level container for the list. */
	private Table scrollPaneTable;
	
	/** Holds the height of the inventory list. */
	private float listHeight;
	
	/** Maps an Item subclass with a button displaying this item. */
	private HashMap<Class, ImageTextButton> buttonMap;
	
	/** Accepts the itemManager from which to retrieve the items' sprites, the inventory from which to retrieve the player's items,
	 * the ClickListener to which button clicks will be delegated, and the height of the list.
	 */
	public InventoryList(ItemManager itemManager, Inventory inventory, ClickListener buttonListener, float height)
	{
		//Stores the parameters in their respective member variables
		this.inventory = inventory;
		this.itemManager = itemManager;
		this.buttonListener = buttonListener;
		this.listHeight = height;
		
		//Populates the table with all the item buttons corresponding to the items in the player's inventory.
		generateList();
	}
	
	/** Populates the list with buttons corresponding to all the items in the player's inventory.  */
	public void generateList()
	{
		//Stores the itemMap, which pairs each Item subclass with the quantity of that item in the player's inventory.
		HashMap<Class, Integer> itemMap = inventory.getItemMap();
		
		//Creates the button table which holds all of the item buttons, and that will be later placed into the ScrollPane.
		Table buttonTable = new Table();
		
		//Creates the HashMap linking each item subclass to a button corresponding to the item in the player's inventory.
		buttonMap = new HashMap<Class, ImageTextButton>();
		
		//Cycles through each item stored inside the player's inventory.
		for(Class key : inventory.getItemMap().keySet())
		{
			//Creates a button for the given item subclass, passing in the quantity of that item (the value of the key) as a second argument.
			createItemButton(key, itemMap.get(key));
			
			//Adds the created button into the button table to be displayed in the list.
			buttonTable.add(buttonMap.get(key)).width(LIST_WIDTH).padLeft(3);
			
			//Skips a row for the next item button.
			buttonTable.row();
		}
		
		//Places the subTable containing all of the buttons into a ScrollPane for scrolling functionality.
		scrollPane = new ScrollPane(buttonTable, assets.inventoryScrollPaneStyle);
		//Modifies the overscroll of the scroll pane. Args: maxOverscrollDistance, minVelocity, maxVelocity
		scrollPane.setupOverscroll(30, 100, 200);
		//Disables scrolling in the x-direction.
		scrollPane.setScrollingDisabled(true, false);
		//Always make the scroll bar appear.
		scrollPane.setFadeScrollBars(false);
		
		//Creates the high-level table which acts as a container for the scrollPane.
		scrollPaneTable = new Table();
		
		//Resizes the table to fit its parent (the stage)
		scrollPaneTable.setFillParent(true);
		//Adds the scrollPane to the buttonTable. The buttonTable is the high-level container for the button list. Sets the height of the list to
		//the listHeight passed in as a constructor argument.
		scrollPaneTable.add(scrollPane).width(LIST_WIDTH).height(listHeight);
	}
	
	/** Called when the contents of the inventory list must be updated. Updates the buttons inside the list, along with their quantities. */
	public void updateList()
	{
		//Stores the HashMap linking each item in the inventory with the amount the player has.
		HashMap<Class, Integer> inventoryMap = inventory.getItemMap();
		
		//Cycles through each item which has a button inside the list.
		for(Class key : buttonMap.keySet())
		{
			//If the player still has this item inside his inventory, update the quantity displayed in the list.
			if(inventoryMap.containsKey(key))
			{				
				//Updates the button for the given item to display the correct quantity.
				updateItemButton(key, inventoryMap.get(key));
			}
			//Else, if the button represents an item that is no longer in the inventory, remove the button from the table.
			else
			{
				//Removes the item from the inventory list
				removeItemButton(key);
			}
		}
		
		//Cycles through all of the item classes inside the inventory. Creates buttons for the items if they aren't already in the inventory list.
		for(Class key : inventoryMap.keySet())
		{
			//If the HashMap of buttons doesn't have a button for the item, create the button.
			if(!buttonMap.containsKey(key))
			{
				System.out.println("InventoryList.updateList(): Add the button to the list for item: " + key);
				
				//Create a button for the item using createItemButton(itemClass, quantity):Button. Passes created button into 'addToList()' to be added in the inventory list.
				addToList(createItemButton(key, inventory.getQuantity(key)));
				
				
			}
		}
	}

	/** Adds the given amount of items to the inventory. If a button already exists for the item, the number shown is updated. If the quantity is negative
	 *  and makes the amount in the inventory zero, the button is deleted. */
	public void addItem(Class itemClass, int quantity)
	{
		//Adds the given quantity to the amount already in the inventory.
		int newQuantity = quantity + inventory.getQuantity(itemClass);
		
		//If the item has been removed from the inventory
		if(newQuantity <= 0)
		{
			//Delete the button corresponding to the item, and remove it from the inventory list.
			removeItemButton(itemClass);
		}
		//If the item was empty in the inventory before calling this method, create a new button to display the item in the inventory list.
		else if(inventory.getQuantity(itemClass) == 0)
		{
			//Create a new itemButton corresponding to the itemClass. Second argument is the quantity displayed on the button. The created button is then added to the list.
			addToList(createItemButton(itemClass, newQuantity));
		}
		//Else, if the item already exists in the inventory, update the quantity displayed next to the item
		else
		{
			//Update the corresponding itemButton to display the given quantity of items inside the inventory.
			updateItemButton(itemClass, newQuantity);
		}
	}

	/** Creates a button for the given item, with the given quantity specified in the button's text. Stores it inside the buttonMap HashMap. */
	private ImageTextButton createItemButton(Class itemClass, int quantity) 
	{
		//Creates a new ImageTextButtonStyle to define the look of the item button.
		ImageTextButtonStyle buttonStyle = new ImageTextButtonStyle();
		
		//Stores the sprite displaying the image of the item for the button. Accessed through the ItemManager using the item's class.
		Sprite itemSprite = itemManager.getSprite(itemClass);
		
		//Sets the properties which defines the look of the item button.
		buttonStyle.font = assets.moonFlowerBold_38;
		buttonStyle.fontColor = TEXT_COLOR;
		buttonStyle.downFontColor = TEXT_DOWN_COLOR;
		buttonStyle.imageDown = buttonStyle.imageUp = new SpriteDrawable(itemSprite);
		buttonStyle.pressedOffsetX = 1;
		buttonStyle.pressedOffsetY = -1.5f;
		
		//Stores the text for the item button, which consists of the item's name, follows by the quantity of the item.
		String buttonText = itemManager.obtainItem(itemClass).getName() + " (" + quantity + ")";
		
		//Creates the button displaying the item passed as arguments to this method.
		ImageTextButton itemButton = new ImageTextButton(buttonText, buttonStyle);
		
		//Registers the buttonListener member variable as the button's listener. Since this listener belongs to the CraftingHud, that class will receive the click event.
		itemButton.addListener(buttonListener);
		
		//Aligns all the elements in the button to the left so that the buttons look left-aligned in the list.
		itemButton.left();
		
		//Creates the box which is placed behind the item sprites. Acts as a background to each item image. Uses the "ItemBox" sprite from the HUD atlas.
		Image itemBoxImage = new Image(assets.hudSkin.getDrawable("ItemBox"));
		//Sets the properties of the itemBox's image
		itemBoxImage.setColor(ITEM_BOX_COLOR);
		itemBoxImage.setSize(ITEM_BOX_WIDTH, ITEM_BOX_HEIGHT);
		
		//Places the item box at the center of the item image to act as an appropriate background.
		itemBoxImage.setPosition(BUTTON_IMAGE_DISTANCE - itemBoxImage.getWidth()/2, itemButton.getHeight()/2 - ITEM_BOX_HEIGHT/2);
		
		//Adds the itemBoxImage before the button's image. Since the button's image is an item's sprite, the box acts as a background to the item.
		itemButton.addActorBefore(itemButton.getImage(), itemBoxImage);
		
		//Pads the item image to the left so that its center is at x=BUTTON_IMAGE_DISTANCE relative to the left of the button.
		itemButton.getImageCell().padLeft(BUTTON_IMAGE_DISTANCE - itemSprite.getWidth()/2);
		//Pads the item image to the right so that the text for each button starts at the same x position (x = BUTTON_TEXT_DISTANCE).
		itemButton.getImageCell().padRight(BUTTON_TEXT_DISTANCE - BUTTON_IMAGE_DISTANCE - itemSprite.getWidth()/2);
		
		//Adds the ImageTextButton to the corresponding key inside the button HashMap.
		buttonMap.put(itemClass, itemButton);
		
		//Returns the created button.
		return itemButton;
	}
	
	/** Updates the quantity displayed on the item button for the given class. */
	private void updateItemButton(Class itemClass, int quantity) 
	{
		//Retrieves the button corresponding to the item in the player's inventory.
		ImageTextButton itemButton = buttonMap.get(itemClass);
		
		//Retrieves the button's current text.
		StringBuilder buttonText = new StringBuilder(itemButton.getText());
		
		//Shaves off the number from the button's text.
		buttonText.delete(buttonText.indexOf("("), buttonText.length());
		//Adds the quantity of the item to the button's text.
		buttonText.append("(" + quantity + ")");
		
		//Updates the button's text to display the new quantity of the item inside the inventory.
		itemButton.setText(buttonText.toString());
		
	}
	
	/** Adds the given button to the ScrollPane. Note that this method must be called AFTER generateTable() is called. */
	private void addToList(ImageTextButton itemButton) 
	{
		//Adds the button into the ScrollPane's widget. Its widget is the buttonTable, where buttons are stored. Pads to the left so as to add spacing between the left 
		//of the button and the left of the list.
		((Table)scrollPane.getWidget()).add(itemButton).width(LIST_WIDTH).padLeft(3);	
		
		//Skips a row for the next item button.
		((Table)scrollPane.getWidget()).row();
	}
	
	/** Removes the item button from the inventory list. Note that items are refered to by their corresponding class. */
	private void removeItemButton(Class itemClass)
	{
		//Removes the button from the table so that it is no longer displayed, since the item corresponding to the button is no longer in the inventory.
		((Table)scrollPane.getWidget()).removeActor(buttonMap.get(itemClass));
		
		//Clears the button of all its functionality. Good practice before disposing of the button.
		buttonMap.get(itemClass).clear();
		
		//Removes the key corresponding to the given itemClass, since the button was removed for the class. Disposes of the reference to the button, since it is no longer needed.
		buttonMap.remove(itemClass);
	}
	
	//Returns the class of the item which corresponds to the given button in the inventory list. 
	public Class getButtonClass(Actor actor)
	{
		//Cycle through each Class key inside the buttonMap. Effectively cycles through each button inside the inventoryList according to its corresponding item class.
		for(Class key : buttonMap.keySet())
		{
			//If the button inside the buttonMap is null, skip to the next key in the buttonMap.
			if(buttonMap.get(key) == null)
				continue;
			
			//If the actor is one of the widgets inside a button in the buttonMap
			if(buttonEquals(actor, buttonMap.get(key)))
			{
				//Return the key, as it holds the item subclass corresponding to the given actor.
				return key;
			}
		}
		
		//If this statement is reached, the button does not exist in the inventory list. Thus, no item class corresponds to it. So, return null
		return null;
	}
	
	//Returns true if the given actor is a button contained inside the inventory. Used by CraftingHud to dictate if a button from the inventory was pressed.
	public boolean contains(Actor actor)
	{
		//Cycle through each Class key inside the buttonMap. Effectively cycles through each button inside the inventoryList according to its corresponding item class.
		for(Class key : buttonMap.keySet())
		{
			//If the button inside the buttonMap is null, skip to the next key in the buttonMap.
			if(buttonMap.get(key) == null)
				continue;
			
			//If the button belonging to the key is the same as the given actor
			if(buttonEquals(actor, buttonMap.get(key)))
			{
				//Return true, since the given actor is inside the inventory list.
				return true;
			}
		}
		
		//Returns true if the button is contained inside the buttonMap, which links each item class to its button in the inventory.
		return buttonMap.containsValue(actor);
	}
	
	/** Returns true if the actor corresponds to the button. That is, if the actor is a widget contained inside the button, the actor is considered to be equal to the given button. 
	 * Used to verify whether the actor which delegated a button click corresponds to a certain button in the inventory. */
	private boolean buttonEquals(Actor actor, ImageTextButton button)
	{
		//Returns true if the actor corresponds to any of the widgets in the given ItemCell.
		if(actor.equals(button) || actor.equals(button.getLabel()) || actor.equals(button.getImage()))
			return true;
		
		//Else, return false if no widgets in the ItemCell correspond to the given actor.
		return false;
	}
	
	/** Returns the table containing all of the buttons in the inventory list. */
	public Table getTable()
	{
		//Returns the table containing all of the item buttons.
		return scrollPaneTable;
	}
}
