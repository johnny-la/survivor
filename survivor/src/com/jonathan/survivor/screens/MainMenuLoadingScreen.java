package com.jonathan.survivor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Survivor;
import com.jonathan.survivor.inventory.MeleeWeapon;
import com.jonathan.survivor.inventory.RangedWeapon;

/**
 * The loading screen which loads all of the heavy assets needed by the main menu. Created and displayed when the user transitions from the game to the main menu, and the 
 * main menu's assets need to be re-loaded.
 */

public class MainMenuLoadingScreen extends Screen
{
	/** Holds the center position of the progress and hint labels, relative to the center of the screen. That is, (0,0) places the labels at the center of the screen. */
	private static final float LOADING_LABEL_X = 0, LOADING_LABEL_Y = -60.5f;	
	private static final float HINT_LABEL_X = 0.5f, HINT_LABEL_Y = -110f;	
	
	/** Holds the left-center position of the player relative to the center of the screen. Note that this measurement is in meters, not in pixels, and is thus much smaller. */
	private static final float PLAYER_X = -1f, PLAYER_Y = -30f;
	
	private static final float HINT_DISPLAY_TIME = 2;	//Holds the amount of time a hint is displayed before switching.
	
	/** Stores the camera displaying the loading screen's GUI. */
	private OrthographicCamera guiCamera;	
	
	/** Holds the label which states "Loading" at the center of the screen. */
	private Label loadingLabel;	
	
	/** Holds the label showing hints which let the user pass time. */
	private Label hintLabel; 	
	
	/** Stores the skeleton instance which draws the player to the screen. */
	private Skeleton playerSkeleton;
	
	/** Stores the list of all possible hints shown in the loading screen. */
	private String[] hints = {"Hint: Save regularly to avoid losing progress\nfrom unexpected zombie encounters", "Hint: Craft bullets and use them sparingly",
							  "Hint: The axe is a fundamental survival tool.\nCraft it as soon as you find the necessary\nresources."};	
	
	/** Stores the amount of time the current hint has been showing. */
	private int hintTime = 0;
	/** Holds the amount of time the player has been playing his current animation. Used to tell Spine which time in the animation to play. */
	private float playerStateTime;	
	/** Stores the amount of time the loading screen has been displayed. */
	private float displayTime = 0; 
	
	/** Helper Array that's passed to the Animation.set() method when the player plays an animation. */
	private Array<Event> events = new Array<Event>();
	
	public MainMenuLoadingScreen(Survivor game)
	{
		super(game);
	}
	
	@Override
	public void show()
	{
		//Create a new camera with the designated size retrieved from the Screen superclass.
		guiCamera = new OrthographicCamera(guiWidth, guiHeight);
		
		//Instantiates the label which states "Loading" at the center of the screen.
		loadingLabel = new Label("Loading", assets.gameOverLabelStyle);
		
		//Instantiates the label which gives the players hints while waiting for loading to complete.
		hintLabel = new Label(hints[(int)(Math.random()*hints.length)], assets.loadingLabelStyle);
		
		//Creates the player's Skeleton, which allows the player to be drawn to the screen. Note that the UI skeletonData is used. It allows the player to be placed in pixel coordinates.
		playerSkeleton = new Skeleton(assets.playerSkeletonData_UI);
		//Ensures that none of the player's unecessary attachments are showing.
		playerSkeleton.setAttachment(RangedWeapon.WEAPON_SLOT_NAME, null);
		playerSkeleton.setAttachment(MeleeWeapon.WEAPON_SLOT_NAME, null);
		
		//Queues the main menu assets for loading. The assets are loaded in the render() method when assets.updateLoading() is called. 
		assets.queueMainMenuAssets();
		
		//Stop the music whilst in the loading screen.
		musicManager.stop();
	}
	
	@Override
	public void render(float deltaTime)
	{
		//Sets OpenGL to clear the screen with blue.
		Gdx.gl.glClearColor(0.1f, 0.2f, 0.3f, 1);
		//Clears the screen.
		super.render(deltaTime);
		
		//Updates the loadings done by the assets singleton. Note that updateLoading() simply continues the loading done by the AssetManager of the Assets instance.
		//It is done on a separate thread. Note that this method returns true if the loading is complete. This method must be called every frame to continue loading.
		boolean loadingComplete = assets.updateLoading();
		
		//Updates the various elements of the loading screen.
		update(deltaTime);
		
		//Draw the GUI for the LoadingScreen.
		drawGUI();
		
		//If the loading is complete
		if(loadingComplete)
		{
			//Stores the assets loaded asynchronously inside the Assets singleton. Stores them inside the singleton's member variables.
			assets.storeMainMenuAssets();
			//Loads the main menu assets which couldn't be loaded asynchronously.
			assets.loadMainMenuAssets();
			
			//Switch to the main menu screen, since loading is complete
			game.setScreen(new MainMenuScreen(game));
			//Return this method call. It is good practice to do so to avoid errors.
			return;
		}
		
	}

	/** Updates the logic of the loading screen, positioning certain widgets and changing certain elements. */
	private void update(float deltaTime) 
	{				
		//Positions the player on top of the bed.
		playerSkeleton.setX(PLAYER_X);
		playerSkeleton.setY(PLAYER_Y);
		
		//Apply the 'Idle' animation to the player's skeleton. Second and third arguments specify how much time the player has been playing his animation, third indicates we want to 
		//loop the animation , and last is an array where any possible animation events are delegated.
		assets.playerIdle_Combat.apply(playerSkeleton, playerStateTime, playerStateTime, true, events);
		
		//Update the player's skeleton before drawing it.
		playerSkeleton.update(deltaTime);
		//Update the player to ensure he is within the viewable region of the world.
		playerSkeleton.updateWorldTransform();
		
		//If the current hint has been showing for long enough
		if(hintTime > HINT_DISPLAY_TIME)
		{
			//Chooses a random new hint to display.
			hintLabel.setText(hints[(int)(Math.random()*hints.length)]);
			
			//Reset the hint time, since the hint has just been changed.
			hintTime = 0;
		}
		
		//Positions the loading label at the center of the screen, using the given offsets.
		loadingLabel.setPosition(LOADING_LABEL_X - loadingLabel.getWidth()/2, LOADING_LABEL_Y - loadingLabel.getHeight()/2);
		
		//Positions the hint label at center position (HINT_LABEL_X, HINT_LABEL_Y) relative to the center of the screen.
		hintLabel.setPosition(HINT_LABEL_X - hintLabel.getWidth()/2, HINT_LABEL_Y - hintLabel.getHeight()/2);
		
		//Increments the amount of time the current hint has been showing
		hintTime += deltaTime;
		
		//Increments the player's state time. This keeps track of which point the player should be in his current animation.
		playerStateTime += deltaTime;
		
		//Increases the amount of time the loading screen has been showing.
		displayTime += deltaTime;
	}

	/**  Draws the GUI for the Loading Screen */
	private void drawGUI()
	{
		//Sets the projection matrix of the sprite batcher to the camera's.
		batcher.setProjectionMatrix(guiCamera.combined);
		//Begin batching and drawing sprites through the SpriteBatch instance.
		batcher.begin();
		
		//Renders the labels to the screen. Second argument specifies to draw the labels at alpha=100%.
		loadingLabel.draw(batcher, 1.0f);
		//progressLabel.draw(batcher, 1.0f);
		hintLabel.draw(batcher, 1.0f);
		
		//Draws the player to the screen.
		assets.skeletonRenderer.draw(batcher, playerSkeleton);
		
		//Draw the sprites batched inside the batcher.
		batcher.end();
		
	}
	
	@Override
	public void resize(int width, int height)
	{
		//Update the super.guiWidth/Height variables
		super.resize(width, height);
		
		//Update the viewport width and height of the GUI camera using the superclass variables guiWidth/Height. Inside super.resize(), these variables
		//were re-computed so that the aspect ratio of the guiCamera would fit the new screen size. This prevents scaling more in one axis than another.
		guiCamera.viewportWidth = guiWidth;
		guiCamera.viewportHeight = guiHeight;
		
		//Update the GUI camera to apply size changes.
		guiCamera.update();
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
