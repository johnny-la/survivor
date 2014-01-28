package com.jonathan.survivor.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.jonathan.survivor.World;
import com.jonathan.survivor.renderers.Hud;

public class BackpackHud extends Hud
{
	/** Stores the spacing between the buttons in the middle of the backpack. */
	public static final float BUTTON_SPACING = 50;
	
	/** Stores the image of the backpack background. */
	private Image backpackBg;
	
	/** Stores the buttons displayed on the Backpack Hud, including the SurvivalGuide, and Crafting buttons. */
	private Button survivalGuideButton;
	private Button craftingButton;
	
	private Label survivalGuideLabel;
	private Label craftingLabel;
	
	/** Stores the Table instance where buttons are organized in a grid-like fashion. */
	private Table table;
	
	/** Accepts the Stage instance where widgets are drawn, and the world, used to manipulate the world according to button presses. */
	public BackpackHud(Stage stage, World world)
	{
		super(stage, world);
		
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
		
		//Creates a new Table instance to neatly arrange the buttons on the hud.
		table = new Table();
		
		table.add(survivalGuideButton).padRight(BUTTON_SPACING).bottom().width(survivalGuideButton.getWidth()).height(survivalGuideButton.getHeight());
		table.add(craftingButton).width(craftingButton.getWidth()).height(craftingButton.getHeight());
		table.row();
		table.add(survivalGuideLabel).padRight(BUTTON_SPACING);
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
		/** Called when the user releases a button click. */
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button)
		{

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
		
		//stage.addActor(backpackBg);
		//Adds the table to the stage so that it can display the appropriate backpack widgets.
		stage.addActor(table);
	}
}
