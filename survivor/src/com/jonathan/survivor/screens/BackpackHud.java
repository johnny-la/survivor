package com.jonathan.survivor.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.jonathan.survivor.World;
import com.jonathan.survivor.hud.Hud;

public class BackpackHud extends Hud
{
	/** Stores the spacing between the buttons in the middle of the backpack. */
	public static final float BUTTON_SPACING = 50;
	
	/** Stores the y position of the table nudge the table up, so that the header is at the right position on the backpack. */
	public static final float TABLE_Y_OFFSET = 5;
	/** Stores the offset between the bottom of the "Backpack" header and the top of the buttons. Adds spacing between the header and the buttons. */
	public static final float HEADER_Y_OFFSET = 15;
	
	/** Stores the offset used to anchor the back button to the bottom-right of the screen with a certain padding. */
	public static final float BACK_BUTTON_X_OFFSET = 10;
	public static final float BACK_BUTTON_Y_OFFSET = 5;
	
	
	/** Stores the image of the backpack background. */
	private Image backpackBg;
	
	/** Stores the header displaying "Backpack" on top of the Hud. */
	private Label backpackHeader;
	
	/** Stores the buttons displayed on the Backpack Hud, including the SurvivalGuide, and Crafting buttons. */
	private Button survivalGuideButton;
	private Button craftingButton;
	
	/** Stores the labels displaying the names of each main button. */
	private Label survivalGuideLabel;
	private Label craftingLabel;
	
	/** Stores the back button, used to exit out of the backpack hud. */
	private Button backButton;
	
	/** Stores the Table instance where buttons are organized in a grid-like fashion. */
	private Table table;
	
	/** Accepts the Stage instance where widgets are drawn, and the world, used to manipulate the world according to button presses. */
	public BackpackHud(Stage stage, World world)
	{
		super(stage, world);
		
		//Creates the backpack's background using the TextureRegion stored inside the Assets singleton.
		backpackBg = new Image(assets.backpackBgRegion);
		//Resizes the background according to the scaleFactor of the assets. Makes it so that the image takes the same screen space no matter screen size.
		backpackBg.setSize(backpackBg.getWidth() / assets.scaleFactor, backpackBg.getHeight() / assets.scaleFactor);
		
		//Creates a new header for the backpack using the pre-defined header's label style.
		backpackHeader = new Label("Backpack", assets.hudHeaderStyle);
		
		//Creates the Survival Guide button using the pre-defined button style.
		survivalGuideButton = new Button(assets.survivalGuideButtonStyle);
		//Scales down the survival guide button to ensure that it is the same size on every screen. Ensures that every big atlas is scaled down to target resolution.
		survivalGuideButton.setSize(survivalGuideButton.getWidth() / assets.scaleFactor, survivalGuideButton.getHeight() / assets.scaleFactor);

		//Creates the crafting button using the button style defined in the assets singleton.
		craftingButton = new Button(assets.craftingButtonStyle);
		//Scales down the crafting button to ensure that it is the same size on every screen. Ensures that every big atlas is scaled down to target resolution.
		craftingButton.setSize(craftingButton.getWidth() / assets.scaleFactor, craftingButton.getHeight() / assets.scaleFactor);
		
		//Creates the label saying "Survival Guide" below the survival guide button, using the Hud's universal label style.
		survivalGuideLabel = new Label("Survival Guide", assets.hudLabelStyle);
		//Creates the label saying "Crafting" below the crafting button, using the Hud's universal label style.
		craftingLabel = new Label("Crafting", assets.hudLabelStyle);
		
		//Creates the back button using the designated ButtonStyle, which dictates its appearance.
		backButton = new Button(assets.backButtonStyle);
		//Resizes the back button so that, no matter the size of the atlas chosen, the button will occupy the same space in gui coordinates.
		backButton.setSize(backButton.getWidth() / assets.scaleFactor, backButton.getHeight() / assets.scaleFactor);
		
		//Creates a ButtonListener instance which will receive all of the button's touch events.
		ButtonListener buttonListener = new ButtonListener();
		
		//Registers the buttons to the button listener so that it receieves all button touch events.
		survivalGuideButton.addListener(buttonListener);
		craftingButton.addListener(buttonListener);
		backButton.addListener(buttonListener);
		
		//Creates a new Table instance to neatly arrange the buttons on the hud.
		table = new Table();
		
		//Adds the header to the top of the table, makes it span two columns, centers it, and skips to the next row.
		table.add(backpackHeader).colspan(2).center().padBottom(HEADER_Y_OFFSET).row();
		//Adds the survival guide button to table, adds a padding to the right so that the next button is spaced away, and resizes the cell to the button's size to
		//ensure that the button is its original size.
		table.add(survivalGuideButton).padRight(BUTTON_SPACING).bottom().width(survivalGuideButton.getWidth()).height(survivalGuideButton.getHeight());
		//Adds the survival guide button to table, and resizes the cell to the button's size to ensure that the button is its original size.
		table.add(craftingButton).width(craftingButton.getWidth()).height(craftingButton.getHeight()).top();
		//Skips a row
		table.row();
		//Adds the survival guide button's label, and pads it to the right so that the next label is spaced away from this one.
		table.add(survivalGuideLabel).padRight(BUTTON_SPACING);
		//Adds the crafting label to the table, displaying the name of the crafting button.
		table.add(craftingLabel);
		
	}


	@Override
	public void draw(float deltaTime) 
	{
		//Draws the widgets and the actors to the stage.
		super.draw(deltaTime);
	}
	
	/** Registers any events called by the buttons of the Backpack Hud. */
	class ButtonListener extends InputListener
	{
		/** Called when a button is pressed in the backpack hud. */
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		{
			//Override to return false so that the touchUp method is called.
			return true;
		}
		
		/** Delegated when the user releases a button click. */
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button)
		{
			//If the back button was pressed
			if(event.getTarget() == backButton)
			{
				//Inform the GameScreen that the back button has been pressed by delegating the onBack() method to the listener. Returns to the game.
				hudListener.onBack();
			}
		}
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
		//Clears the stage of its current contents to make way for the backpack's 2d widgets.
		stage.clear();

		//Resizes the table so that it occupies the entire size of the gui to thus occupy the entire stage.
		table.setBounds(0, 0, guiWidth, guiHeight);
		
		//Centers the backpack background to the stage. Note that the position is the bottom-left of backpackBg's image.
		backpackBg.setPosition(stage.getWidth()/2 - backpackBg.getWidth()/2, stage.getHeight()/2 - backpackBg.getHeight()/2);
		//Anchors the back button to the bottom-right of the backpack background, using the given offsets. Note that button positions are the bottom-left of the buttons.
		backButton.setPosition(backpackBg.getX() + backpackBg.getWidth() - backButton.getWidth() - BACK_BUTTON_X_OFFSET, backpackBg.getY() + BACK_BUTTON_Y_OFFSET);
		
		//Offsets the table's bottom y-position up to place the header at the right position on the backpack.
		table.setY(TABLE_Y_OFFSET);
		
		//Adds the backpack background to the center of the stage.
		stage.addActor(backpackBg);
		//Adds the back button to the stage.
		stage.addActor(backButton);
		
		//Adds the table to the stage so that it can display the appropriate backpack widgets.
		stage.addActor(table);
	}
}
