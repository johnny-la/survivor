package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.jonathan.survivor.World;

/*
 * An instance of this class will display the HUD whilst in EXPLORATION mode.
 */

public class ExplorationHud extends Hud
{
	/** Stores the offsets of both arrow buttons. Used to anchor the buttons to the corners of the screen. */
	public static final float ARROW_BUTTON_X_OFFSET = 15f;
	public static final float ARROW_BUTTON_Y_OFFSET = 10f;
	
	/** Stores the color of the arrow buttons. */
	public static final Color ARROW_BUTTON_COLOR = new Color(1, 0.1f, 0, 1);
	
	/** Stores the left and right arrow buttons to make the player move left and right. */
	private ImageButton leftArrowButton, rightArrowButton;
	
	/** Stores the offsets of the backpack button. Used to anchor the button to the corner of the screen with a given offset. */
	public static final float BACKPACK_BUTTON_X_OFFSET = 5;
	public static final float BACKPACK_BUTTON_Y_OFFSET = 5;
	
	/** Stores the Backpack button. */
	private Button backpackButton;
	
	/** Stores the listener used to listen for events from the arrow buttons. */
	private ButtonListener buttonListener;
	
	/** Stores the buttons displaying the left and right arrows to move the player. */
	private boolean leftArrowButtonDown, rightArrowButtonDown;
	
	/** Accepts the stage where 2d widgets will be drawn and placed, and the world, which will receive information about
	 *  button presses. */
	public ExplorationHud(Stage stage, World world)
	{
		super(stage, world);
		
		//Creates the left and right arrow buttons for movement.
		leftArrowButton = new ImageButton(assets.leftArrowButtonStyle);
		rightArrowButton = new ImageButton(assets.rightArrowButtonStyle);

		//Creates the backpack button to transition to the backpack.
		backpackButton = new Button(assets.backpackButtonStyle);

		//Resize the buttons according to the scale factor of the screen so that every atlas size creates the button with the same size in world units.
		leftArrowButton.setSize(leftArrowButton.getWidth() / assets.scaleFactor, leftArrowButton.getHeight() / assets.scaleFactor);
		rightArrowButton.setSize(rightArrowButton.getWidth() / assets.scaleFactor, rightArrowButton.getHeight() / assets.scaleFactor);
		
		backpackButton.setSize(backpackButton.getWidth() / assets.scaleFactor, backpackButton.getHeight() / assets.scaleFactor);
		
		//Sets the colors of the buttons.
		leftArrowButton.setColor(ARROW_BUTTON_COLOR);
		rightArrowButton.setColor(ARROW_BUTTON_COLOR);
		
		//Creates a new listener for the buttons
		buttonListener = new ButtonListener();
		
		//Adds the listeners to the buttons to know when they are pressed.
		leftArrowButton.addListener(buttonListener);
		rightArrowButton.addListener(buttonListener);
		
		backpackButton.addListener(buttonListener);

	}
	
	@Override
	public void draw(float deltaTime) 
	{
		//If the right arrow button is being held down
		if(rightArrowButtonDown)
		{
			//Make the player move right.
			world.movePlayerRight();
		}
		//If the left arrow button is being held down
		else if(leftArrowButtonDown)
		{
			//Make the player move left by changing him to his walking state.
			world.movePlayerLeft();
		}
		
		//Draws the widgets to the screen.
		super.draw(deltaTime);
	}
	
	class ButtonListener extends InputListener
	{
		/** Delegates when a button is pressed. */
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		{			
			//If the right arrow button was pressed or its overlaying arrow image was pressed
			if(event.getTarget() == rightArrowButton || event.getTarget() == rightArrowButton.getImage())
			{
				//The right arrow button is currently being pressed
				rightArrowButtonDown = true;
				
			}
			//If the left arrow button was pressed or its overlaying arrow image was pressed
			else if(event.getTarget() == leftArrowButton || event.getTarget() == leftArrowButton.getImage())
			{
				//The left arrow button is currently being pressed
				leftArrowButtonDown = true;
			}
			
			//Returns true so the touchUp can be called when the button is released.
			return true;
		}
		
		/** Delegates when a button is released. */
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button)
		{
			//If the right arrow button was released, stop the player from moving. Note that the image on the button can also be a target.
			if(event.getTarget() == rightArrowButton || event.getTarget() == rightArrowButton.getImage())
			{
				//The right arrow button is no longer being pressed.
				rightArrowButtonDown = false;
				//Stops the player from moving right once the right directional button is unpressed.
				world.stopMoving(world.getPlayer());
			}
			//If the left arrow button was released, stop the player from moving. Note that the image on the button can also be a target.
			else if(event.getTarget() == leftArrowButton || event.getTarget() == leftArrowButton.getImage())
			{
				//The left arrow button is no longer being pressed.
				leftArrowButtonDown = false;
				//Stops the player from moving left once the left directional button is unpressed.
				world.stopMoving(world.getPlayer());

			}
			//Else, if the backpackButton was released, transition to the backpack inventory page.
			else if(event.getTarget() == backpackButton)
			{
				//Tell the GameScreen that the BackpackButton was pressed by delegating the appropriate method call to the HudListener.
				hudListener.onBackpackButton();
			}
			
		}
	}

	/** Called when the stage must be reset to draw the widgets contained in this class. Used when the stage needs to be re-purposed. Also called when the
	 *  screen is resized to re-place the widgets. */
	@Override
	public void reset(float guiWidth, float guiHeight) 
	{
		//Clears the stage and all its widgets to re-purpose the stage to draw the exploration HUD.
		stage.clear();
		
		//Anchor the left arrow button to the bottom left of the screen using the offset constants.
		leftArrowButton.setPosition(ARROW_BUTTON_X_OFFSET, ARROW_BUTTON_Y_OFFSET);
		//Anchor the right arrow button to the bottom right of the screen using the offset constants.
		rightArrowButton.setPosition(stage.getWidth() - rightArrowButton.getWidth() - ARROW_BUTTON_X_OFFSET, ARROW_BUTTON_Y_OFFSET);	
		
		//Anchors the backpack button to the top left of the screen using the offset constants.
		backpackButton.setPosition(BACKPACK_BUTTON_X_OFFSET, stage.getHeight() - backpackButton.getHeight() - BACKPACK_BUTTON_Y_OFFSET);
		
		//Add the widgets to the stage.
		stage.addActor(leftArrowButton);
		stage.addActor(rightArrowButton);
		
		//Adds the backpack button to the stage.
		stage.addActor(backpackButton);
	}
	
}
