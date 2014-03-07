package com.jonathan.survivor.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jonathan.survivor.World;

public class SurvivalGuideHud extends Hud
{
	/** Stores the amount the list is offset up, relative to the center of the screen. This is the top-most y-position of the text, where 0 will place it at the center of the screen. */
	public static final float LIST_Y_OFFSET = 52;
	/** Holds the amount the list is nudged to the left. This allows the entries in the list to look left-aligned. */
	public static final float LIST_X_OFFSET = 118;
	
	/** Stores the x-offset of the "Guide" header relative to the center of the screen. */
	public static final float HEADER_X_OFFSET = 100;	//Unused
	/** Stores the offset between the bottom of the "Guide" header and the top of the listof entries. Adds spacing between the header and the buttons. */
	public static final float HEADER_Y_OFFSET = 10;		//Unused

	
	/** Stores the offset used to anchor the back button to the bottom-right of the screen with a certain padding. */
	public static final float BACK_BUTTON_X_OFFSET = 28;
	public static final float BACK_BUTTON_Y_OFFSET = 13;
	
	
	/** Stores the image of the survival guide's background. */
	private Image survivalGuideBg;
	
	/** Stores the list prompting the user to select an item to view in the survival guide. */
	private List list;
	
	/** Holds the label displaying the description for the entry the user clicked. */
	private Label entryLabel;
	
	/** True if the description for an entry is currently being shown. On back, revert to the entry name list. */
	private boolean displayingDescription;
	
	/** Holds the list of entry names that the user can choose from the list. */
	private final String[] entryNames = new String[]{"How to Escape", "How to Eat", "How to defend yourself"}; 
	/** Holds the description of every entry in the survival guide. */
	private final String[] entries = new String[]{"Build a Time Machine", 
			
												  "Make Bread" +
												  "\n- 1 Wheat " +
												  "\n- 1 Baking Powder", 
												  
											      "Craft a rifle" +
											      "\n- 20 iron"};
	
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
		
		//Creates the list displaying the names of all entries in the survival guide.
		list = new List(entryNames, assets.survivalGuideListStyle);
		//Makes it so that the selection box around the selected item in the list is invisible. No selection box should appear in the entry list.
		list.setColor(Color.CLEAR);
		
		//Instantiates the label that will display the description for an entry in the survival guide.
		entryLabel = new Label("", assets.hudLabelStyle);
		
		//Creates the back button using the designated ButtonStyle, which dictates its appearance.
		backButton = new Button(assets.backButtonStyle);
		//Resizes the back button so that, no matter the size of the atlas chosen, the button will occupy the same space in gui coordinates.
		backButton.setSize(backButton.getWidth() / assets.scaleFactor, backButton.getHeight() / assets.scaleFactor);
		
		//Adds a change listener to the list to detect when an entry name is selected.
		list.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//Show the entry description for the given entry index that was pressed.
				showEntryDescription(list.getSelectedIndex());			
				//Reset the list's index to negative one, so that any item in the list will trigger the changed() method when pressed.
				list.setSelectedIndex(-1);
			}

		});
		
		//Add a ClickListener to the back button
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//If the survival guide is currently showing an entry description.
				if(displayingDescription)
				{
					//Revert back to the list of entry names
					showEntryList();
				}
				//Else, if the list of entry names is being shown
				else
				{
					//Tell the game screen to revert back to the backpack hud.
					hudListener.onBack();
				}
			}
		});
		
		//Creates a new Table instance to neatly arrange the buttons on the hud.
		table = new Table();
		
		//Adds the list of survival guide entries to the table. Pads it to the left so that it is nudged to the right.
		table.add(list);
		
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
		//Removes the label displaying the entry's desciption. We want to revert to the list of all entries.
		table.removeActor(entryLabel);
		//Replaces the entry description with the entry list. 
		table.add(list);
		
		//Tells the survival guide HUD it is displaying the list of entry names, and not an entry description
		displayingDescription = false;
		
		//Offsets the table's position so that the beginning of each entry description starts at the same position.
		offsetTablePosition();
		
	}
	
	/** Displays the description for the entry with the given index in the entryNames:String[] array. */
	private void showEntryDescription(int index) 
	{
		//Removes the list of all entries from the table. We want to add the description for the clicked entry.
		table.removeActor(list);
		
		//Set the label to hold the description for the pressed entry
		entryLabel.setText(entries[index]);
		//Adds the entry label to the table so that the entry's description is displayed instead of the entry list. Pads the text to shift it to the right.
		table.add(entryLabel).left();
		
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
		
		//Stores the width and height of the table depending on whether an entry description or the entry list is being displayed.
		float tableWidth = (displayingDescription)? entryLabel.getWidth() : list.getWidth();
		float tableHeight = (displayingDescription)? entryLabel.getHeight() : list.getHeight();
		
		//Offsets the table so that the text is left aligned at x = LIST_X_OFFSET. 
		table.setX(tableWidth/2 - LIST_X_OFFSET);
		//Sets the y-position of the table so that the top of the text inside the guide is always at the same height relative to screen's center.
		table.setY(LIST_Y_OFFSET - tableHeight/2);
	}
}