package com.jonathan.survivor.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jonathan.survivor.World;

public class PauseMenuHud extends Hud
{
	/** Holds the table used to arrange the buttons in a grid-like fashion. */
	Table table;
	
	/** Holds the color which overlays the screen below the pause menu. */
	public static final Color OVERLAY_COLOR = new Color(1f, 1f, 1f, 0.5f);
	
	/** Stores the spacing between the buttons displayed in a list. */
	public static final float BUTTON_SPACING = 10f;
	
	/** Stores the amount the table is offset upwards so that the "Paused" label is shown higher up the screen. */
	public static final float TABLE_OFFSET = 0f;
	
	/** Holds the header for the pause menu. */
	private Label headerLabel;
	
	/** Stores the buttons displayed on the pause menu. */
	private TextButton resumeButton;
	private TextButton saveButton;
	private TextButton mainMenuButton;
	
	/** Holds the ConfirmDialog which prompts the user and makes sure he wants to save his profile. */
	private ConfirmDialog saveDialog;
	
	/** Holds the ConfirmDialog which informs the user that his progress will be lost if he quits the game. */
	private ConfirmDialog quitDialog;
	
	/** Holds the ButtonListener which receives events when one of the pause menu's buttons are pressed. */
	private ButtonListener buttonListener;
	
	/** Accepts the stage where widgets are placed. The passed world is unused for this HUD. */
	public PauseMenuHud(Stage stage, World world)
	{
		super(stage, world);
		
		//Creates the table instance used to arrange the buttons in a grid-like fashion.
		table = new Table();
		
		//Creates the header label from the LabelStyle defined in the Assets singleton.
		headerLabel = new Label("Paused", assets.mainMenuHeaderStyle);
		
		//Creates the text buttons displayed on the pause menu.
		resumeButton = new TextButton("Resume", assets.mainMenuButtonStyle);
		saveButton = new TextButton("Save", assets.mainMenuButtonStyle);
		mainMenuButton = new TextButton("Quit", assets.mainMenuButtonStyle);
		
		//Colors the buttons of the pause menu.
		resumeButton.setColor(new Color(0.4f, 0.8f, 0.2f, 1));
		saveButton.setColor(new Color(0.9f, 0.2f, 0.2f, 1));
		mainMenuButton.setColor(new Color(0.2f, 0.5f, 0.8f, 1));
		
		//Resizes the buttons according to the scale factor. This ensures that, if larger atlases were chosen, the buttons are scaled down accordingly.
		resumeButton.setSize(resumeButton.getWidth() / assets.scaleFactor, resumeButton.getHeight() / assets.scaleFactor);
		saveButton.setSize(saveButton.getWidth() / assets.scaleFactor, saveButton.getHeight() / assets.scaleFactor);
		mainMenuButton.setSize(mainMenuButton.getWidth() / assets.scaleFactor, mainMenuButton.getHeight() / assets.scaleFactor);
		
		//Creates the ButtonListener instance which receives all button press events.
		buttonListener = new ButtonListener();
		//Adds the button listener instance to the buttons so that they delegate button events to the listener.
		resumeButton.addListener(buttonListener);
		saveButton.addListener(buttonListener);
		mainMenuButton.addListener(buttonListener);
		
		//Creates the confirmation dialog which opens when the save button is pressed. Constructor accepts title of dialog, along with ClickListener 
		//which gets its clicked() method called when the 'Yes' button is pressed.
		saveDialog = new ConfirmDialog("Are you sure you want\nto save?", new ClickListener() {
			//Called when the "Yes" button is clicked.
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//If the user presses the 'Yes' button, tell the GameScreen to save the player's profile.
				hudListener.saveGame();
			}
		});
		
		//Creates the confirmation dialog which opens when the quit button is pressed. Constructor accepts title of dialog, along with ClickListener 
		//which gets its clicked() method called when the 'Yes' button is pressed.
		quitDialog = new ConfirmDialog("          Quit?\n(All unsaved progress\nwill be lost)", new ClickListener() {
			//Called when the "Yes" button is clicked.
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//If the user presses the 'Yes' button, tell the GameScreen to switch to the main menu and therefore quit the game.
				hudListener.switchToMainMenu();
			}
		});
		
		//Adds the header to the top of the table
		table.add(headerLabel).colspan(2).row();
		//Adds the buttons to the table
		table.add(resumeButton).width(resumeButton.getWidth()).height(resumeButton.getHeight()).pad(BUTTON_SPACING).row();
		table.add(saveButton).width(saveButton.getWidth()).height(saveButton.getHeight()).row();
		table.add(mainMenuButton).width(mainMenuButton.getWidth()).height(mainMenuButton.getHeight()).pad(BUTTON_SPACING);
	}
	
	@Override
	public void draw(float deltaTime)
	{
		//Clears the screen with the overlay color so that the game is hidden behind the pause menu.
		Gdx.gl.glClearColor(OVERLAY_COLOR.r, OVERLAY_COLOR.g, OVERLAY_COLOR.b, OVERLAY_COLOR.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		//Draw the 2d widgets stored in the stage.
		super.draw(deltaTime);
	}
	
	/** Class receiving all button clicking events. */
	class ButtonListener extends ClickListener
	{	
		/** Delegates when a button is clicked. */
		@Override
		public void clicked(InputEvent event, float x, float y)
		{
			//If the resume button was pressed
			if(event.getTarget() == resumeButton || event.getTarget() == resumeButton.getLabel())
			{
				//Tells the GameScreen that the resume button has been pressed by delegating a call to the HudListener.
				hudListener.onBack();
			}
			//Else, if the save button was pressed
			else if(event.getTarget() == saveButton || event.getTarget() == saveButton.getLabel())
			{
				//Open the confirm dialog which asks the user if he's sure he wants to save. If he presses 'Yes', the GameScreen saves the player's profile.
				saveDialog.show(stage);
			}
			//Else, if the main menu button was pressed
			else if(event.getTarget() == mainMenuButton || event.getTarget() == mainMenuButton.getLabel())
			{
				//Open the confirm dialog which asks the user if he's sure he wants to quit the game. If he presses 'Yes', the GameScreen switches to the main menu.
				quitDialog.show(stage);
			}
		}
	}

	/** Called either when this pause menu is supposed to be displayed, or when the screen is resized. Parameters indicate the size that the HUD should occupy. */
	@Override
	public void reset(float guiWidth, float guiHeight) 
	{
		//Clear the current contents of the stage to erase the previously-displayed Hud and make way for the pause menu's widgets.
		stage.clear();
		
		//Resizes the table so that it occupies the entire size of the gui to thus occupy the entire stage.
		table.setBounds(0, TABLE_OFFSET, guiWidth, guiHeight);
		
		//Adds the table to the stage so as to display the buttons of the pause menu.
		stage.addActor(table);
		
	}
}
