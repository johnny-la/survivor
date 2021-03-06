package com.jonathan.survivor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.Settings;
import com.jonathan.survivor.Survivor;
import com.jonathan.survivor.managers.MusicManager;
import com.jonathan.survivor.managers.PreferencesManager;
import com.jonathan.survivor.managers.ProfileManager;
import com.jonathan.survivor.managers.SoundManager;

public abstract class Screen implements com.badlogic.gdx.Screen
{
	/** Stores the game instance which created and manipulates this screen. Used for such things as changing screens using Survivor.setScreen(). */
	protected Survivor game;
	
	/** Holds the dimensions of a GUI camera. All assets drawn by a GUI camera are placed relative to these dimensions. This is the viewing space available for
	 *  the camera. The viewing space is then stretched to fill the screen space. Note that their values are changed in resize() according to aspect ratio. */
	protected float guiWidth = Survivor.DEFAULT_GUI_WIDTH,
					guiHeight = Survivor.DEFAULT_GUI_HEIGHT;
	
	/** Stores the dimensions of a GUI camera. All assets drawn by a GUI camera are placed relative to these dimensions. This is the viewing space available for
	 *  the camera. The viewing space is then stretched to fill the screen space. Note that their values are changed in resize() according to aspect ratio. */
	protected float worldWidth = Survivor.DEFAULT_WORLD_WIDTH,
					worldHeight = Survivor.DEFAULT_WORLD_HEIGHT;
	
	/** Stores the amount we have to scale the x and y axis to fit the screen. That is, the game has a target resolution of DEFAULT_GUI_WIDTH x DEFAULT_GUI_HEIGHT.
	 *  These floats store how much we have to stretch the width and height of this target resolution to fit the device. Used, in part, to scale line thickness. */
	protected float screenScaleX;
	protected float screenScaleY;
	
	/** Holds the singleton instance of the assets class. Allows for screen subclasses to have easier access to the visual/audio assets loaded from the assets
	 *  instance. */
	protected Assets assets = Assets.instance;
	
	/** Stores the universal Music and Sound Manager used by the game controlling this screen. Allows screen to play music and sounds and control their volume. */
	protected MusicManager musicManager;
	protected SoundManager soundManager;
	/** Stores the universal Profile Manager used by the game controlling this screen. Used to access profiles and their data. */
	protected ProfileManager profileManager;
	/** Holds the PreferencesManager instance used to access and modify the user's preferences. */
	protected PreferencesManager prefsManager;
	
	/** Stores the Settings instance used to save player information to the hard drive. */
	protected Settings settings;
	
	/** Stores the SpriteBatch instance used to draw sprites to this screen. */
	protected SpriteBatch batcher;
	
	public Screen(Survivor game)
	{
		//Keep track of the game used to create this screen
		this.game = game;
		
		//Get the game's music and sound managers to allow every screen subclass to play music and sounds at will and control their volume.
		musicManager = game.getMusicManager();
		soundManager = game.getSoundManager();
		//Retrieves the profile manager to allow every screen subclass to access the profiles used by the user.
		profileManager = game.getProfileManager();
		//Stores the PreferencesManager instance used by the game to access and modify the player's preferences (i.e., music/sound volume, etc.)
		prefsManager = game.getPrefsManager();
		
		//Retrieves the Settings instance to allow every screen subclass to save player information to the hard drive.
		settings = game.getSettings();
		
		//Create a new SpriteBatch which can batch one hundred sprites at once. This is a convenience variable, since virtually all screens need a batcher.
		batcher = new SpriteBatch();
	}
	
	/** Called every frame to update game logic or draw graphics to the screen */
	@Override
	public void render(float deltaTime)
	{
		//Clears the screen.
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

	/** Called when the user switches out of this screen. In this case, we dispose of the memory allocated to the screen. This is because we never store instances
	 *  of screens. Once the user switches out of them, they are lost in memory. Thus, we need to free resources allocated to it. */
	@Override 
	public void hide()
	{
		//Dispose of memory allocated to the screen.
		dispose();
	}
	
	/** Called either when the Application is ended, or when the user switches out of this screen. Here, we need to free resources used by the screen. */
	@Override
	public void dispose()
	{
		//Dispose of the resources used by the batcher before leaving the screen, since we'll never re-enter this screen instance again, since screen instances
		//are never stored in variables, but rather created anonymously.
		if(batcher != null)
			batcher.dispose();
		
		System.out.println("Screen's dispose called");
	}
	
	@Override
	public void resize(int width, int height)
	{
		//Finds the scale on the x and y axes that must be done to stretch the world to fit the target device's screen. For instance, if the default world width
		//is 480, and the default height is 320, this is what happens: Let's say the application is run on a 800x480 device. First, we get the scale value of
		//scaleX = 800/480 = 1.667, and scaleY = 480/320 = 1.5. (Note: width/height store the width/height of the device's screen in pixels).
		screenScaleX = width / Survivor.DEFAULT_WORLD_WIDTH;
		screenScaleY = height / Survivor.DEFAULT_WORLD_HEIGHT;
		
		//Then, we find the amount we have to scale either the width, or the height of the world by. Here, keep in mind that we want to scale the axis which is bigger
		//for the target device than for the world. So, in the example from the previous comment, we would want to scale the world's width. This is because scaleX is 
		//greater than scaleY. This is because the aspect ratio of the target 800x480 device is larger, and thus the screen has a larger horizontal region than the 
		//default aspect ratio of 1.5 (480x320). To find how much we want to scale the larger axis by, we take the maximum scale, and divide it by the minimum. In the
		//previous example, we would get 1.667 / 1.5 = 1.111.  This is how much we want to scale the view world's width by.
		float scaleAmount = Math.max(screenScaleX, screenScaleY) / Math.min(screenScaleX, screenScaleY);
		
		//If the maximum scale is scaleX, we know something important. We know that we would have to scale the world more horizontally than vertically in order to fit
		//the default world on the target device. This means that, with default scaling, we would scale the horizontal axis more, making the picture look horizontally
		//stretched. To avoid this, we make the width of the world bigger by scaleAmount. This will make the world fit the aspect ratio of the target device. When the
		//picture projected from the view frustum of the camera is enlarged, it will have the same aspect ratio as the screen, thus eliminating the stretching.
		if(Math.max(screenScaleX, screenScaleY) == screenScaleX)
		{
			//Multiply the width of the world and the GUI by the scaleAmount. If we are here, it means that we have to multiply the width of the world/gui's camera by a
			//value larger than one in order to meet the aspect ratio of the screen. The value we have to scale it by is 'scaleAmount'. The resulting width will make the 
			//camera's aspect ratio the same as the device's aspect ratio. Thus, when the image projected onto the camera is stretched to fit the device, there will be no
			//stretching on one axis. All that will happen is that the user will see more of the horizontal axis than is seen by default.
			worldWidth = Survivor.DEFAULT_WORLD_WIDTH * scaleAmount;
			guiWidth = Survivor.DEFAULT_GUI_WIDTH * scaleAmount;
			
			//Set the height of the world's camera and the GUI's camera to default. They don't need to be stretched to fit the device's aspect ratio.
			worldHeight = Survivor.DEFAULT_WORLD_HEIGHT;
			guiHeight = Survivor.DEFAULT_GUI_HEIGHT;
		}
		//Else, if the maximum scale is scaleY, we know something important. We know that we would have to scale the world more vertically than horizontally in order to fit
		//the world on the target device. This means that, if the image projected onto the camera were scaled, we would scale the vertical axis more, making the picture look 
		//vertically stretched. To avoid this, we make the height of the world/GUI bigger by scaleAmount. This will make the world/GUI fit the aspect ratio of the target 
		//device. When the picture projected onto the view frustum is enlarged, it will have the same aspect ratio as the screen, thus eliminating the stretching.
		else
		{
			//Multiply the height of the world and GUI's cameras by the scaleAmount. If we are here, it means that we have to multiply the height of the world by a value larger
			//than one in order to meet the aspect ratio of the screen. This is because the device's height is larger than expected. The value we have to scale it by is 'scaleAmount'.
			//The resulting height will make the cameras' aspect ratios the same as the device's aspect ratio. Thus, when the images projected onto the cameras are stretched to
			//fit the device, there will be no stretching. All that will happen is that the user will see more of the vertical axis than is seen by default.
			worldHeight = Survivor.DEFAULT_WORLD_HEIGHT * scaleAmount;
			guiHeight = Survivor.DEFAULT_GUI_HEIGHT * scaleAmount;
			
			//Set the width of the world's camera and the GUI's camera to default. They don't need to be stretched to fit the device's aspect ratio.
			worldWidth = Survivor.DEFAULT_WORLD_WIDTH;
			guiWidth = Survivor.DEFAULT_GUI_WIDTH;
		}
		
	}
	
}
