package com.jonathan.survivor.hud;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.jonathan.survivor.World;
import com.jonathan.survivor.inventory.Axe;
import com.jonathan.survivor.inventory.Inventory;
import com.jonathan.survivor.inventory.Rifle;
import com.jonathan.survivor.inventory.Teleporter;
import com.jonathan.survivor.managers.CraftingManager;
import com.jonathan.survivor.managers.CraftingManager.Item;
import com.jonathan.survivor.managers.ItemManager;

public class CraftingHud extends Hud
{
	/** Holds the height of the inventory list holding all of the item buttons. */
	public static final float INVENTORY_LIST_HEIGHT = 180;
	
	/** Holds the offset used to anchor the inventory list to the left of the backpack background. Offsets the list relative to the center of the screen. */
	public static final float INVENTORY_LIST_X_OFFSET = -75;
	public static final float INVENTORY_LIST_Y_OFFSET = -35;
	
	/** Holds the offset used to anchor the crafting table to the left of the backpack background. Offsets the table relative to the center of the screen. */
	public static final float CRAFTING_TABLE_X_OFFSET = 108;
	public static final float CRAFTING_TABLE_Y_OFFSET = -16;
	
	/** Stores the offset used to anchor the craftButton to the bottom-right of the backpack background. Offsets the button relative to the bottom-right of the background*/
	public static final float CRAFT_BUTTON_X_OFFSET = 66;
	public static final float CRAFT_BUTTON_Y_OFFSET = 21;
	
	/** Holds the offset used to anchor the header to the top-center of the screen. Offsets the header relative to the center of the screen. */
	public static final float HEADER_X_OFFSET = -0.5f;
	public static final float HEADER_Y_OFFSET = 105.5f;
	
	/** Stores the offset used to anchor the back button to the bottom-right of the backpack background with a certain padding. */
	public static final float BACK_BUTTON_X_OFFSET = 10;
	public static final float BACK_BUTTON_Y_OFFSET = 5;
	
	/** Holds the CraftingManager singleton which dictates whether or not an item combination forms a certain item. */
	private CraftingManager craftingManager = CraftingManager.instance;
	
	/** Holds an array of each item and their quantity inside the crafting table. Used to dictate if the item combination forms another item. */
	private Array<Item> craftingItems;
	
	/** Stores the item crafted from the items currently inside the crafting table. */
	private Item craftedItem;
	
	/** Stores the player's inventory in order to populate the items in the inventory list. */
	private Inventory inventory;
	
	/** Holds the ItemManager instance. Its purpose is to give access to Item instances, which give an item's information and sprite. */
	private ItemManager itemManager;	
	
	/** Stores the image of the backpack background. */
	private Image backpackBg;
	
	/** Stores the header displaying "Crafting" on top of the Hud. */
	private Label craftingHeader;
	
	/** Stores the list containing all the items in the player's inventory. */
	private InventoryList inventoryList;
	
	/** Holds the crafting table which holds a grid of the items being crafted. */
	private CraftingTable craftingTable;
	
	/** Holds the ConfirmDialog which prompts the user when he wants to craft an item. */
	private ConfirmDialog confirmDialog;
	
	/** Stores the button displaying "Craft". When pressed, the items in the crafting table combine to create a new item. */
	private Button craftButton;
	
	/** Stores the back button, used to exit out of the crafting menu. */
	private Button backButton;

	/** Accepts the Stage instance where widgets are drawn, and the world, used to manipulate the world according to button presses. The ItemManager
	 *  is accepted in order to fetch internally-pooled Item instances in order to draw the items in the player's inventory. */
	public CraftingHud(Stage stage, World world, Inventory inventory, ItemManager itemManager) 
	{
		super(stage, world);
		
		//Holds the array of items in the crafting table. Used to determine whether the items can craft another item. 
		craftingItems = new Array<Item>();
		
		//Populates the inventory member variable with the given argument.
		this.inventory = inventory;
		//Stores the itemManager instance used to access Item instances in order to draw the items in the inventory.
		this.itemManager = itemManager;
		
		//Creates the backpack's background using the TextureRegion stored inside the Assets singleton.
		backpackBg = new Image(assets.backpackBgRegion);
		//Resizes the background according to the scaleFactor of the assets. Makes it so that the image takes the same screen space no matter screen size.
		backpackBg.setSize(backpackBg.getWidth() / assets.scaleFactor, backpackBg.getHeight() / assets.scaleFactor);
		
		//Creates a new header for the crafting HUD using the pre-defined header's label style.
		craftingHeader = new Label("Crafting", assets.hudHeaderStyle);
		
		//Creates a ButtonListener instance which will receive all of the button's touch events.
		ButtonListener buttonListener = new ButtonListener();
		
		//Creates the inventory list displaying a list of every item in the player's inventory. Accepts the itemManager to access item sprites, and accepts with the list's height.
		//All button clicks will be delegated to the given listener.
		inventoryList = new InventoryList(itemManager, inventory, buttonListener, INVENTORY_LIST_HEIGHT);
		
		//Instantiates the crafting table which holds a grid of the items being crafted. Accepts the itemManager to access item sprites. All button clicks will be passed to the
		//given listener.
		craftingTable = new CraftingTable(itemManager, inventory, buttonListener);
		
		//Creates the confirmation dialog which opens when the craft button is pressed and an item can be crafted. Constructor accepts title of dialog, along with ClickListener 
		//which gets its clicked() method called when the 'Yes' button is pressed.
		confirmDialog = new ConfirmDialog("", new ClickListener() {
			//Called when the "Yes" button is clicked.
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//Craft the item using the items in the crafting table.
				craftItem();
			}
		});
		
		//Creates the "Craft" button, which, which pressed, combines together the items in the crafting table.
		craftButton = new Button(assets.craftButtonStyle);
		//Sets the color of the craft button.
		//craftButton.setColor(new Color(1f, 0.6f, 0.12f, 1));	//Orange: 1f, 0.6f, 0.12f, 1, Blue: 0, 0.4f, 0.8f, 1
		
		//Resizes the craftButton so that, no matter the atlas size chosen, the button occupies the same space in gui coordinates.
		craftButton.setSize(craftButton.getWidth() / assets.scaleFactor, craftButton.getHeight() / assets.scaleFactor);
		
		//Creates the back button using the designated ButtonStyle, which dictates its appearance.
		backButton = new Button(assets.backButtonStyle);
		//Resizes the back button so that, no matter the size of the atlas chosen, the button will occupy the same space in gui coordinates.
		backButton.setSize(backButton.getWidth() / assets.scaleFactor, backButton.getHeight() / assets.scaleFactor);
		
		//Registers the buttons to the button listener so that it receives all button touch events.
		craftButton.addListener(buttonListener);
		backButton.addListener(buttonListener);
	}
	
	/** Registers any events called by the buttons of the Crafting Hud. */
	class ButtonListener extends ClickListener
	{		
		/** Delegated when the user releases a button click. */
		@Override
		public void clicked(InputEvent event, float x, float y)
		{
			
			//If the button displaying "Craft" on the bottom-right of the screen was pressed
			if(event.getTarget() == craftButton)
			{
				//If an item can be crafted
				if(craftedItem != null)
					//Prompts the user with a confirm dialog to confirm his crafting choice.
					promptCraft();
					
			}
			//Else, if the back button was pressed
			else if(event.getTarget() == backButton)
			{
				//Return to the backpack menu
				onBack();
			}
			//Else, if an 'itemButton' in the crafting table was pressed, handle the event. This type of button represents one of the six items in the crafting table.
			else if(craftingTable.isItemButton(event.getTarget()))
			{
				//Transfers one instance of the item from the pressed button to the inventory. Removes one instance of that item from the crafting table.
				transferToInventory(craftingTable.getItemButtonClass(event.getTarget()), 1);
			}
			//Else, if the clicked button is a button inside the inventory
			else if(inventoryList.contains(event.getTarget()))
			{
		
				//Stores the class belonging to the item which was pressed.
				Class itemClass = inventoryList.getButtonClass(event.getTarget());
				
				//Transfers one instance of that item from the inventory list, to the crafting table.
				transferToCraftingTable(itemClass, 1);
			}
		}

	}
	
	/** Transfers the given quantity of the item from the crafting table to the inventory list. */
	private void transferToInventory(Class itemClass, int quantity) 
	{
		//If an empty crafting table button was pressed, return this method since no item should be transfered to the inventory.
		if(itemClass == null)
			return;
		
		//Adds the given quantity of items into the inventoryList. If a button corresponding to that item already exists, the number next to the item will simply be updated.
		inventoryList.addItem(itemClass, quantity);
		
		//Removes 'quantity' amount of items from the crafting table. Note that this removes items from button which contains the given item by updating the number displayed.
		craftingTable.addItem(itemClass, -quantity);
		
		//Add the given quantity of the item into the inventory. Note that each item is referred to by its class.
		inventory.addItem(itemClass, quantity);
		
		//Subtracts the given item from the array of items in the crafting table. Allows the class to determine if the items in the crafting table can form another item.
		addToItemList(itemClass, -quantity);
		
		//Updates the item that is crafted as a result of the items in the crafting table.
		updateCraftedItem();
	}
	
	/** Transfers the given quantity of the given item from the inventory list to the crafting table. */
	private void transferToCraftingTable(Class itemClass, int quantity) 
	{	
		//If the crafting table is full, and the table does not already contain the item we want to add, the given item can't be transfered to the crafting table. 
		if(craftingTable.isFull() && !craftingTable.containsItem(itemClass))
			return;
		
		//Removes the given quantity of items from the inventoryList. The number next to the item will simply be updated.
		inventoryList.addItem(itemClass, -quantity);
		
		//Adds 'quantity' amount of items to the crafting table. Note that this updates the number next to the given item. If the item isn't already in the crafting table, it is added.
		craftingTable.addItem(itemClass, quantity);
		
		//Subtract the given quantity of the item from the inventory. The item will be transfered to the crafting table.
		inventory.addItem(itemClass, -quantity);
		
		//Adds the given item to the array of items in the crafting table. Allows class to determine if items can form another item.
		addToItemList(itemClass, quantity);
		
		//Updates the item that is crafted as a result of the items in the crafting table.
		updateCraftedItem();
	}
	
	/** Asks the user if he wants to craft the item in the crafting table. Opens up the confirm dialog to ensure of the player's choice. */
	private void promptCraft()
	{
		//Obtains an item sub-instance which holds information about the item which wants to be crafted. itemManager.obtainItem():Item returns an Item type which
		//holds information about a specific item class.
		com.jonathan.survivor.inventory.Item item = itemManager.obtainItem(craftedItem.getItem());
		
		//Update the confirm dialog's message to make sure of the player's decision to craft the item.
		confirmDialog.setMessage("Are you sure you want\nto craft " + craftedItem.getQuantity() + " " + item.getName() + "(s)?");
		
		//Show the confirm dialog which asks the player if he wants to craft the item in the crafting table. If the 'Yes' button is pressed, 'craftItem()' is called.
		confirmDialog.show(stage);
		
	}
	
	/** Crafts the item formed by the items in the crafting table and adds it to the inventory, deleting all the other items that were used to form the item. */
	private void craftItem() 
	{		
		//If the items in the crafting table don't create any new item, return this method to avoid NullPointerExceptions.
		if(craftedItem == null)
			return;
		
		//If the player just crafted an axe
		if(craftedItem.getItem() == Axe.class)
			//Make the player equip an axe
			world.getPlayer().getLoadout().setMeleeWeapon(new Axe());
		//If the player just crafted a rifle
		else if(craftedItem.getItem() == Rifle.class)
			//Make the player equip a rifle
			world.getPlayer().getLoadout().setRangedWeapon(new Rifle());
		//Else, if a Teleporter was just created 
		else if(craftedItem.getItem() == Teleporter.class)
			//Tell the GameScreen to make the player play his TELEPORT animation.
			hudListener.activateTeleporter();
		
		//Adds the craftedItem into the inventoryList. If a button corresponding to that item already exists, the number next to the item will simply be updated.
		inventoryList.addItem(craftedItem.getItem(), craftedItem.getQuantity());

		//Add the given quantity of the crafted item into the inventory. Note: must be called after 'inventoryList.addItem()'
		inventory.addItem(craftedItem.getItem(), craftedItem.getQuantity());
		
		//Empties the crafting table of its current items. False argument specifies that the items in the crafting table will not be put back into the player's inventory.
		emptyCraftingTable(false);
		
		//Empty the craftedItem instance variable, since the item was just crafted, and thus is no longer in the crafting table.
		craftedItem = null;
	}
	
	/** Updates the item shown in the cell below the item grid. Computes if any items can be crafted using the items in the crafting table. If so, the 
	 * crafting box below the item grid is updated with the appropriate picture.
	 */
	private void updateCraftedItem() 
	{
		//Gets the resulting item formed by the items inside 'craftingItems:Array<Item>', which holds the list of items inside the crafting table.
		craftedItem = craftingManager.getResult(craftingItems);
		
		//Set the crafted item slot to contain the given resulting item.
		craftingTable.setCraftedItem(craftedItem);
	}

	/** Adds the given item to the array of items in the crafting table. Allows class to determine if items can form another item. */
	private void addToItemList(Class itemClass, int quantity) 
	{
		
		//Cycles through the list of items held inside the crafting table. 
		for(int i = 0; i < craftingItems.size; i++)
		{
			//If the item we want to add already exists in the craftingItems list, simply update the quantity of that item in the list.
			if(craftingItems.get(i).getItem().equals(itemClass))
			{
				//Add the given quantity to the list of items
				craftingItems.get(i).add(quantity);
				
				//If the item has been removed from the crafting table.
				if(craftingItems.get(i).getQuantity() == 0)
				{
					//Remove the crafting item from the list since it has been removed from the crafting table.
					craftingItems.removeIndex(i);
				}
				
				//Return, since this method has done its job.
				return;
			}
		}
		
		//If this statement is reached, the item is not already inside the list. Thus, add it.
		craftingItems.add(craftingManager.new Item(itemClass, quantity));		
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
		
		//Updates the inventory list to ensure that all the items in the inventory are also in the list, with the right quantity.
		inventoryList.updateList();
		//Creates and populates the crafting table, along with the widgets inside it.
		//craftingTable.generateTable();
		
		//Centers the backpack background to the stage. Note that the position is the bottom-left of backpackBg's image.
		backpackBg.setPosition(stage.getWidth()/2 - backpackBg.getWidth()/2, stage.getHeight()/2 - backpackBg.getHeight()/2);
		//Places the header label at the top-center of the screen. The offset constants position the label relative to the center of the screen.
		craftingHeader.setPosition(stage.getWidth()/2 - craftingHeader.getWidth()/2 + HEADER_X_OFFSET, stage.getHeight()/2 - craftingHeader.getHeight()/2 + HEADER_Y_OFFSET);
				
		//Retrieves the tables which hold the widgets of the the inventory list and the crafting table. 
		Table inventoryTable = inventoryList.getTable();
		Table craftTable = craftingTable.getTable();
		
		//Sets the position of the inventory table by positioning it relative to the center of the screen. Note that position (0,0) places the table at the center of the screen.
		inventoryTable.setPosition(INVENTORY_LIST_X_OFFSET, INVENTORY_LIST_Y_OFFSET);
		//Sets the position of the crafting table positioning it relative to the screen's center.
		craftTable.setPosition(CRAFTING_TABLE_X_OFFSET, CRAFTING_TABLE_Y_OFFSET);
		
		//Positions the craftButton relative to the bottom-right of the backpack background using the offset constants.
		craftButton.setPosition(backpackBg.getX() + backpackBg.getWidth() - craftButton.getWidth() - CRAFT_BUTTON_X_OFFSET, backpackBg.getY() + CRAFT_BUTTON_Y_OFFSET);
		//Anchors the back button to the bottom-right of the backpack background, using the given offsets. Note that button positions are the bottom-left of the buttons.
		backButton.setPosition(backpackBg.getX() + backpackBg.getWidth() - backButton.getWidth() - BACK_BUTTON_X_OFFSET, backpackBg.getY() + BACK_BUTTON_Y_OFFSET);
		
		//Adds the backpack background to the center of the stage.
		stage.addActor(backpackBg);
		//Adds the header label to the screen.
		stage.addActor(craftingHeader);
		//Adds the craft button to the stage.
		stage.addActor(craftButton);
		//Adds the back button to the stage.
		stage.addActor(backButton);
		
		//Adds the inventoryList's button table to the stage, effectively adding the item list to the HUD.
		stage.addActor(inventoryTable);
		//Adds the crafting table to the stage. 
		stage.addActor(craftTable);
	}
	
	/** Removes the items from the crafting table. If the argument is true, the items are put back into the player's inventory. */
	public void emptyCraftingTable(boolean transferToInventory)
	{
		//Remove the items in the crafting table. The argument specifies whether or not the items in the crafting table are put back in the player's inventory.
		craftingTable.emptyTable(transferToInventory);
		
		//Clear the list of items in the crafting table, since they have all been removed.
		craftingItems.clear();
	}
	
	/** Called when the back button is pressed, or when the game exits. Prompts the gameScreen to return to the backpack menu, and removes the elements in the crafting 
	 *  table back into the inventory. */
	public void onBack() 
	{		
		//Empty the crafting table, and put back all the items into the inventory. The argument specifies that the items in the crafting table will be transfered to the inventory.
		emptyCraftingTable(true);
		
		//Prompt the GameScreen to return to the BackpackMenu.
		hudListener.onBack();
	}

}
