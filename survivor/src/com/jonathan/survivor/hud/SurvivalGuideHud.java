package com.jonathan.survivor.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.jonathan.survivor.World;

public class SurvivalGuideHud extends Hud
{
	/** Stores the amount the list is offset up, relative to the center of the screen. This is the top-most y-position of the text, where 0 will place it at the center of the screen. */
	public static final float LIST_Y_OFFSET = 50;
	/** Holds the amount the list is nudged to the left. This allows the entries in the list to look left-aligned. */
	public static final float LIST_X_OFFSET = 125;
	
	/** Holds the width and height of the scroll pane where entries are displayed in the survival guide. */
	public static final float SCROLL_PANE_WIDTH = 270;	//270
	public static final float SCROLL_PANE_HEIGHT = 150;	//150
	
	/** Stores the x-offset of the "Guide" header relative to the center of the screen. */
	public static final float HEADER_X_OFFSET = 100;	//Unused
	/** Stores the offset between the bottom of the "Guide" header and the top of the listof entries. Adds spacing between the header and the buttons. */
	public static final float HEADER_Y_OFFSET = 10;		//Unused

	/** Stores the offset used to anchor the back button to the bottom-right of the screen with a certain padding. */
	public static final float BACK_BUTTON_X_OFFSET = 20;
	public static final float BACK_BUTTON_Y_OFFSET = 13;
	
	
	/** Stores the image of the survival guide's background. */
	private Image survivalGuideBg;
	
	/** Stores the list of buttons which the user can press to access an entry in the survival guide. */
	private Array<TextButton> entryButtons;
	
	/** Stores a table containing all of the entry buttons, arranged in a vertical list. The user can scroll through it in a scroll pane and select an entry. */
	private Table entryButtonTable;
	
	/** Holds the Listener which registers any button clicks. If an entry button is pressed, the correct description for that pressed entry is shown. */
	private ButtonListener buttonListener;
	
	/** Holds the label displaying the description for the entry the user clicked. */
	private Label entryLabel;
	
	/** Stores the ScrollPane which allows the items in the survival guide to be scollable. */
	private ScrollPane scrollPane;
	
	/** Holds the table where the scroll pane is contained. This is the high-level container for the list. */
	private Table scrollPaneTable;
	
	/** True if the description for an entry is currently being shown. On back, revert to the entry name list. */
	private boolean displayingDescription;
	
	/** Holds the list of entry names that the user can choose from the list. */
	private final String[] entryNames = new String[]{"Exploration Tutorial", "Combat tutorial", "Crafting Tutorial", "How to Escape", "Recipes"}; 
	/** Holds the description of every entry in the survival guide. */
	private final String[] entries = new String[]{"To move left or right, press the directional arrows on the bottom of the screen.\n\n" +
											      "Swipe up to move up a layer, and swipe down to move down a layer.\n\n" + 
												  "Press any object in the world to walk towards it and interact with it.\n",
												  
												  "To enter combat with a zombie, collide with a zombie in the world.\n\n" +
												  "Whilst in combat mode:" +
												  "\nnTo jump, press the green button on the bottom-left.\n\n" +
												  "By hitting the zombie on the head whilst jumping, you deal damage to him.\n\n" +
												  "To melee the zombie when he comes close, press the orange button on the bottom-right.\n\n" +
												  "To fire your ranged weapon, PRESS and HOLD the red button on the bottom-right of the screen.\n\n" +
												  "(Note: each shot requires one bullet)",
												  
												  "To enter the crafting menu, press the 'Crafting' button in the backpack menu.\nn" +
												  "When in the menu, click an item in the left-hand list to add it to the crafting table. " +
												  "\n\nNote that every time an item is pressed, one instance of that item is transfered to the crafting table. " +
												  "\n\nTo remove the item from the crafting table, press on the desired item in the crafting table. " +
												  "\n\nIf the crafting table contains a combination of items in the right quantities, an item will appear " +
												  "below the arrow sign. " +
												  "\n\nTo craft this item and add it to the inventory, press the 'Craft' button.",
			
												  "Build a Teleporter\n" +
												  "- 40 sulfur + 120 wood\n + 100 iron + 40 saltpeter", 
												  
											      "Axe:\n" +
											      " 10 wood + 5 iron\n\n" +
											      "Rifle:\n" +
											      " 15 wood + 10 iron\n\n" +
											      "Bullets:\n" +
											      " 4 gunpowder + 2 iron\n\n" /*+
											      "Gunpowder:\n" +
											      " 6 sulfur + 4 water + 8 charcoal\n + 12 saltpeter\n\n" Player should not know this-- tests his chemistry knowledge.*/};
	
	/** Stores the back button, used to exit out of the backpack hud. */
	private Button backButton;
	
	/** Stores the Table instance where buttons are organized in a grid-like fashion. */
	private Table table;
	
	/** Accepts the stage where widgets are placed. The passed world is unused for this HUD. */
	public SurvivalGuideHud(Stage stage, World world)
	{
		super(stage, world);
		
		//Creates the image displaying the backpack background. Uses the pre-defined TextureRegion displaying the background.
		survivalGuideBg = new Image(assets.survivalGuideBgRegion);
		//Re-scales the background so that it takes the same space on the screen no matter the atlas size chosen.
		survivalGuideBg.setSize(survivalGuideBg.getWidth() / assets.scaleFactor, survivalGuideBg.getHeight() / assets.scaleFactor);
		
		//Instantiates the label that will display the description for an entry in the survival guide.
		entryLabel = new Label("", assets.smallLabelStyle);
		//Wraps the text by words every time the text goes over the scrollPane.
		entryLabel.setWrap(true);
		
		//Creates the table which contains all the buttons which the user can press to access an entry in the survival guide.
		createButtonTable();
		
		//Places the table of entry buttons inside the ScrollPane to add scrolling functionality to that list.
		scrollPane = new ScrollPane(entryButtonTable, assets.inventoryScrollPaneStyle);
		//Modifies the overscroll of the scroll pane. Args: maxOverscrollDistance, minVelocity, maxVelocity
		scrollPane.setupOverscroll(30, 100, 200);
		//Disables scrolling in the x-direction.
		scrollPane.setScrollingDisabled(true, false);
		
		scrollPane.setSmoothScrolling(true);
		
		//Creates the back button using the designated ButtonStyle, which dictates its appearance.
		backButton = new Button(assets.backButtonStyle);
		//Resizes the back button so that, no matter the size of the atlas chosen, the button will occupy the same space in gui coordinates.
		backButton.setSize(backButton.getWidth() / assets.scaleFactor, backButton.getHeight() / assets.scaleFactor);
		
		//Add a ClickListener to the back button
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//Tell the game screen that the Back button was pressed. In turn, the GameScreen will call this class's backButton() method.
				hudListener.onBack();
			}
		});
		
		//Creates a new Table instance to neatly arrange the buttons on the hud.
		table = new Table();
		//Set the table to fit the entire stage.
		table.setFillParent(true);
		//Adds the list of survival guide entries to the table. Pads it to the left so that it is nudged to the right.
		table.add(scrollPane).width(SCROLL_PANE_WIDTH).height(SCROLL_PANE_HEIGHT);;
		
	}
	
	/** Called upon instantiation to create the table which holds buttons. The user can press these buttons to access entries in the survival guide. */
	private void createButtonTable() 
	{
		//Creates the table which will hold the list of all buttons which serve to access an entry in the survival guide.
		entryButtonTable = new Table();
		
		//Instantiates the array which contains all of the entryButtons which serve to access an entry in the survival guide.
		entryButtons = new Array<TextButton>();
		
		//Creates a ButtonListener which will listen to any button clicks coming from the entry buttons.
		buttonListener = new ButtonListener();
		
		//Cycles through all the entries in the survival guide, and creates a button for each one
		for(int i = 0; i < entryNames.length; i++)
		{
			//Creates a new button for the entry that is being cycled through. Uses a pre-defined ButtonStyle to define the button's look.
			TextButton button = new TextButton(entryNames[i], assets.survivalGuideListButtonStyle);
			
			//Makes the button store its index internally. Like this, the button knows which entry to display when it is pressed.
			button.setUserObject(new Integer(i));
			
			//Registers the ButtonListener to the entry button.
			button.addListener(buttonListener);
			
			//Adds the button to the list of entry buttons contained in the survival guide.
			entryButtons.add(button);
			
			//Adds the button to the entry button table.
			entryButtonTable.add(button).width(SCROLL_PANE_WIDTH).height(button.getHeight()).row();
		}
		
	}
	
	/** Registers the entries button which were clicked in the SurvivalGuideHud. */
	private class ButtonListener extends ClickListener
	{
		/** Delegated when the user clicks a button. */
		@Override
		public void clicked(InputEvent event, float x, float y)
		{
			//Retrives the entryButton which was pressed. Since the pressed item is always the label contained in the button, its parent will be the TextButton itself.
			//Note that only entry buttons trigger this clicked() method.
			TextButton entryButton = (TextButton)event.getTarget().getParent();
			
			//Grabs the user object stored in the button, which is the index of the entry which should be displayed. Displays the description for this index.
			showEntryDescription((Integer)entryButton.getUserObject());
		}
	}

	@Override
	public void draw(float deltaTime)
	{
		//Draws the Survival Guide HUD and its widgets to the screen.
		super.draw(deltaTime);
	}
	
	/** Displays the list consisting of entry names. */
	private void showEntryList() 
	{
		//Replaces the entry description with the table of entry buttons. 
		scrollPane.setWidget(entryButtonTable);
		
		//Tells the survival guide HUD it is displaying the list of entry names, and not an entry description
		displayingDescription = false;
		
		//Offsets the table's position so that the beginning of each entry description starts at the same position.
		offsetTablePosition();
		
	}
	
	/** Displays the description for the entry with the given index in the entryNames:String[] array. */
	private void showEntryDescription(int index) 
	{		
		//Set the label to hold the description for the pressed entry
		entryLabel.setText(entries[index]);
		
		//Make the scroll pane display the newly updated entryLabel. The label shows the description for the pressed entry.
		scrollPane.setWidget(entryLabel);
		
		//Tells the survival guide that it is currently displaying the description for an entry. On back button, the list of entry names will be shown.
		displayingDescription = true;
		
		//Offsets the table so that the header and the entry list are in the right position.
		offsetTablePosition();
		
	}

	/** Called either when this pause menu is supposed to be displayed, or when the screen is resized. Parameters indicate the size that the HUD should occupy. */
	@Override
	public void reset(float guiWidth, float guiHeight) 
	{
		//Clear the current contents of the stage to erase the previously-displayed Hud and make way for the pause menu's widgets.
		stage.clear();
		
		//Resizes the table so that it occupies the entire size of the gui to thus occupy the entire stage.
		table.setBounds(0, 0, guiWidth, guiHeight);
		
		//Centers the survival guide background to the stage. Note that the position is the bottom-left of survivalGuideBg's image.
		survivalGuideBg.setPosition(stage.getWidth()/2 - survivalGuideBg.getWidth()/2, stage.getHeight()/2 - survivalGuideBg.getHeight()/2);
		//Anchors the back button to the bottom-right of the survival guide background, using the given offsets. Note that button positions are the bottom-left of the buttons.
		backButton.setPosition(survivalGuideBg.getX() + survivalGuideBg.getWidth() - backButton.getWidth() - BACK_BUTTON_X_OFFSET, survivalGuideBg.getY() + BACK_BUTTON_Y_OFFSET);
		
		//Adds the survival guide background to the center of the stage.
		stage.addActor(survivalGuideBg);
		//Adds the back button to the stage.
		stage.addActor(backButton);
		
		//Adds the table to the stage so that its widgets are drawn to the screen.
		stage.addActor(table);
		
		//Sets the table at the correct position.
		offsetTablePosition();
		
	}

	/** Offsets the position of the table so that the header is at the right position */
	private void offsetTablePosition() 
	{
		//Draws the widgets on the stage to ensure that each widget's size is updated.
		stage.draw();
		
		//Offsets the table so that the text is left aligned at x = LIST_X_OFFSET. 
		table.setX(SCROLL_PANE_WIDTH/2 - LIST_X_OFFSET);
		//Sets the y-position of the table so that the top of the text inside the guide is always at the same height relative to screen's center.
		table.setY(LIST_Y_OFFSET - SCROLL_PANE_HEIGHT/2);
	}
	
	/** Called by the GameScreen when the BACK key is pressed. If the survival guide is showing the description to an entry, the guide reverts back to the entry list. 
	 * @return Returns true if the survival guide was displaying the list of entries. If so, upon pressing the back button, the GameScreen is told that the user should
	 * be reverted back to the Backpack HUD.
	 * */
	public boolean backPressed()
	{
		//If the survival guide is currently showing an entry description.
		if(displayingDescription)
		{
			//Revert back to the list of entry names
			showEntryList();
		}
		//Else, if the SurvivalGuideHud was displaying the list of entries before the BACK button was pressed
		else 
		{
			//Return true. This tells the GameScreen, which calls this method, that the user should go to the backpack HUD, since pressing back makes the user leave the survival guide.
			return true;
		}
		
		//If this statement is reached, the user should stay in the survival guide, despite pressing the Back button. Return false, telling the GameScreen not to switch to the backpack.
		return false;
	}
}
