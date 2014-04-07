package com.jonathan.survivor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jonathan.survivor.TerrainLevel;
import com.jonathan.survivor.Survivor;

public class MainMenuScreen extends Screen
{
	private Stage stage;	//Stores the stage used as a container for the UI widgets. It is essentially the camera that draws the widgets.
	private Table table;	//Stores the table actor. This simply arranges the widgets at the center of the screen in a grid fashion.
	
	private TextButton playButton;	//Stores the play button.
	private TextButton optionsButton;	//Stores the options button.
	
	private LabelStyle labelStyle;	//Stores the LabelStyle instance. This is used to define the font and color used by the labels.
	
	private Image logoImage;	//Stores the image for the logo, displayed at the center of the screen.
	
	public MainMenuScreen(Survivor game)
	{
		super(game);
	}
	
	@Override
	public void show()
	{
		//Create a stage used to draw the UI widgets.
		stage = new Stage();
		
		//Sets the stage to handle any touch/mouse input. Tells the widgets that they can receive touch events.
		Gdx.input.setInputProcessor(stage);
		
		//Creates a table out of the skin. Why the skin is passed is unknown. However, this table is used to organize the widgets in a grid fashion.
		table = new Table(assets.mainMenuSkin);
		
		//Stores and loads the logo image. Note that the logo image is retrieved from the main menu atlas using skin.getDrawable("Sprite Name").
		logoImage = new Image(assets.mainMenuSkin.getDrawable("Logo"));
		//Resize the logo image according to the scale factor. The scale factor is either 4, 2, or 1, depending on which texture size we are using. If we are
		//using @2x size textures, we need to shrink the label to half its orginal size. Like this, the labels are always the same size, no matter which texture
		//atlas is used. This allows the stage to keep a constant size no matter the texture size.
		logoImage.setWidth(logoImage.getWidth()/assets.scaleFactor);
		logoImage.setHeight(logoImage.getHeight()/assets.scaleFactor);

		//Create the play button using the main menu button style. This gives the button the desired image.
		playButton = new TextButton("PLAY", assets.mainMenuButtonStyle);
		//Set the color of the button to red.
		playButton.setColor(Color.RED);
		
		//Resize the play button according to the scale factor. The scale factor is either 4, 2, or 1, depending on which texture size we are using. If we are
		//using @2x size textures, we need to shrink the button to half its orginal size. Like this, the buttons are always the same size, no matter which texture
		//atlas is used. This allows the stage to keep a constant size no matter the texture size.
		playButton.setWidth(playButton.getWidth() / assets.scaleFactor);
		playButton.setHeight(playButton.getHeight() / assets.scaleFactor);
		
		//Add a click listener to the play button
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//Play a click sound effect when the "Play" button is pressed.
				//soundManager.play(assets.buttonClick);
				//Switch to a new world select screen once the play button is pressed.
				game.setScreen(new WorldSelectScreen(game));
			}
		});
		
		//Create the options button using the button style created above. This gives the button the desired image.
		optionsButton = new TextButton("OPTIONS", assets.mainMenuButtonStyle);
		//Sets the options button to be blue.
		optionsButton.setColor(new Color(0, 0.4f, 1, 1));
		
		//Resize the options button according to the scale factor. The scale factor is either 4, 2, or 1, depending on which texture size we are using. If we are
		//using @2x size textures, we need to shrink the button to half its orginal size. Like this, the buttons are always the same size, no matter which texture
		//atlas is used. This allows the stage to keep a constant size no matter the texture size.
		optionsButton.setWidth(optionsButton.getWidth() / assets.scaleFactor);
		optionsButton.setHeight(optionsButton.getHeight() / assets.scaleFactor);
		
		//Creates a new LabelStyle. This will be used to define the labels drawn on top of the buttons.
		labelStyle = new LabelStyle(assets.moonFlowerBold_54, Color.WHITE);
		
		//Adds the logoImage to the top of the table. We make it span two columns so that it is display at the center of the two buttons below.
		table.add(logoImage).colspan(/*2*/1).width(logoImage.getWidth()).height(logoImage.getHeight()).padBottom(5);
		//Skip a row
		table.row();
		//Add the play button to the table, make it right aligned, and give it a 10 pixel blank space to the right. We set the width and height to the button's 
		//width and height to ensure that the button is not scaled when added to the table.
		table.add(playButton)/*.right().pad(10)*/.width(playButton.getWidth()).height(playButton.getHeight());
		//Add the options button to the table, make it left aligned, and give it a 10 pixel blank space to the left. We set the width and height to the button's 
		//width and height to ensure that the button is not scaled when added to the table.
		//table.add(optionsButton).left().pad(10).width(optionsButton.getWidth()).height(optionsButton.getHeight());
		
		//Show the table's debug lines if we are in debug mode
		if(Survivor.DEBUG_MODE)
			table.debug();
		
		//Add the table to the stage, telling the stage to draw all of the widgets inside the table when stage.draw() is called.
		stage.addActor(table);
		
	}
	
	@Override
	public void render(float deltaTime)
	{
		//Sets the screen to be cleared with white
		Gdx.gl.glClearColor(1,1,1,1);
		//Clears the screen.
		super.render(deltaTime);
		
		//Show the table's debug lines if we are in debug mode
		if(Survivor.DEBUG_MODE)
			Table.drawDebug(stage);
		
		//Sets up the stage
		stage.act(deltaTime);
		//Draws the stage, effectively drawing all the UI widgets.
		stage.draw();
		
	}
	
	@Override
	public void resize(int width, int height)
	{
		//Re-compute the value of the guiWidth and guiHeight superclass variables to ensure that the gui fits the new aspect ratio of the screen.
		super.resize(width, height);
		
		//Set the viewport of the stage to the guiWidth/Height member variables. This ensures that the viewable area of the stage is the desired width and height
		//of the GUI. Note that these variables were defined in the superclass to be the desired default width and height of a GUI. However, the smallest dimension
		//is resized to fit the screen's aspect ratio to avoid uneven stretching. If the device's screen is guiWidth x guiHeight, the GUI is pixel perfect.
		stage.setViewport(guiWidth, guiHeight);
		//Sets the bounds of the table. This is essentially the largest the table can be. We set it to the full width of the GUI for the table to fill the screen.
		table.setBounds(0, 0, guiWidth, guiHeight);
	}
	
	@Override
	public void dispose()
	{
		//Disposes of the SpriteBatcher in the superclass.
		super.dispose();
		
		//Dispose of any resources used by the MainMenuScreen.
		stage.dispose();
	}

	@Override
	public void pause() 
	{
	}

	@Override
	public void resume() 
	{
	}
	
}
