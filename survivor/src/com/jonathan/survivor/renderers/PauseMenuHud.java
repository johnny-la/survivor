package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.jonathan.survivor.World;

public class PauseMenuHud extends Hud
{
	/** Holds the table used to arrange the buttons in a grid-like fashion. */
	Table table;
	
	/** Stores the spacing between the buttons displayed in a list. */
	public static final float BUTTON_SPACING = 10f;
	
	/** Stores the buttons displayed on the pause menu. */
	TextButton resumeButton;
	TextButton mainMenuButton;
	
	public PauseMenuHud(Stage stage, World world)
	{
		super(stage, world);
		
		//Creates the table instance used to arrange the buttons in a grid-like fashion.
		table = new Table();
		
		//Creates the text buttons displayed on the pause menu.
		resumeButton = new TextButton("Resume", assets.mainMenuButtonStyle);
		mainMenuButton = new TextButton("Main Menu", assets.mainMenuButtonStyle);
		
		//Colors the buttons of the pause menu.
		resumeButton.setColor(Color.GREEN);
		mainMenuButton.setColor(Color.RED);
		
		//Resizes the buttons according to the scale factor. This ensures that, if larger atlases were chosen, the buttons are scaled down accordingly.
		resumeButton.setSize(resumeButton.getWidth() / assets.scaleFactor, resumeButton.getHeight() / assets.scaleFactor);
		mainMenuButton.setSize(mainMenuButton.getWidth() / assets.scaleFactor, mainMenuButton.getHeight() / assets.scaleFactor);
		
		//Adds the buttons to the table
		table.add(resumeButton).pad(BUTTON_SPACING).row();
		table.add(mainMenuButton).pad(BUTTON_SPACING);
	}

	/** Called either when this pause menu is supposed to be displayed, or when the screen is resized. Parameters indicate the size that the HUD should occupy. */
	@Override
	public void reset(float guiWidth, float guiHeight) 
	{
		//Clear the current contents of the stage to erase the previously-displayed Hud and make way for the pause menu's widgets.
		stage.clear();
		
		//Resizes the table so that it occupies the entire size of the gui to thus occupy the entire stage.
		table.setBounds(0, 0, guiWidth, guiHeight);
		
		//Adds the table to the stage so as to display the buttons of the pause menu.
		stage.addActor(table);
		
	}
}