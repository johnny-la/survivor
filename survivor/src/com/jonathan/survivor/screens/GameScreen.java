package com.jonathan.survivor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jonathan.survivor.Profile;
import com.jonathan.survivor.Survivor;
import com.jonathan.survivor.World;
import com.jonathan.survivor.managers.GestureManager;
import com.jonathan.survivor.managers.InputManager;
import com.jonathan.survivor.renderers.ExplorationRenderer;
import com.jonathan.survivor.renderers.HudRenderer;
import com.jonathan.survivor.renderers.WorldRenderer;

/*
 * Renders the world, updates it, and displays the UI depending on the world's state.
 */

public class GameScreen extends Screen
{
	public enum GameState {
		EXPLORING, PAUSED, GAME_OVER
	};
	
	/** Stores the state of the game, used to determine how to update the world, and how to draw the UI. */
	private GameState gameState;
	
	/** Stores the profile used to create the world. */
	private Profile profile;
	
	/** Stores the world, which controls all game logic. */
	private World world;
	/** Stores the world renderer, which takes the objects in the world, and displays them. */
	private WorldRenderer worldRenderer;
	
	/** Manages all simple input of the game such as "touch ups" and calls method of the world's GameObjects to match user input. */
	private InputManager inputManager;
	/** Manages all gestures input of the game such as "swipes" and calls method of the world's GameObjects to match user input. */
	private GestureManager gestureManager;
	
	/** Stores the stage instance where all hud elements will be placed and drawn. */
	private Stage stage;
	
	/** Class allowing us to set multiple instance of InputListeners to receive input events. */
	private InputMultiplexer inputMultiplexer;
	
	/** Stores the currently active HudRenderer which draws the UI to the screen. */
	private HudRenderer hudRenderer;
	/** Stores the ExplorationRenderer instance which draws the UI when the user is in exploration mode. */
	private ExplorationRenderer explorationRenderer;
	
	/** Creates a game screen. The profile used to create the screen must be specified to load the user's previous save information and update it. */
	public GameScreen(Survivor game, Profile profile)
	{
		super(game);
		
		//Stores the profile used to start the game.
		this.profile = profile;
		
		//Creates a new World instance, which control game logic. The profile is used to load data pertinent to the world and its contained GameObjects.
		world = new World(profile.getWorldSeed(), profile);
		//Creates a world renderer, passing in the world to render, and the SpriteBatcher used to draw the sprites.
		worldRenderer = new WorldRenderer(world, batcher);
		
		//Creates a GestureManager with the given world. This manager receieves all gesture events such as swipes and changes the world's GameObjects accordingly.
		inputManager = new InputManager(world);
		//Creates an InputManager with the given world. This manager receives all touch events and reacts by calling appropriate GameObject methods. 
		gestureManager = new GestureManager(world);
		
		//Creates a new stage where 2d widgets for the ui will be displayed.
		stage = new Stage();
		
		//Creates the multiplexer to link several InputListeners together.
		inputMultiplexer = new InputMultiplexer();
		
		//Sets the InputManager to receive simple input events such as touch up/touch down
		inputMultiplexer.addProcessor(inputManager);
		//Sets the GestureManager instance to receive gesture events likes swipes. Must be wrapped in a GestureDetector before being passed as an argument.
		//Arguments: halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay
		inputMultiplexer.addProcessor(new GestureDetector(10, 0.4f, 0.1f, 5f, gestureManager));
		//Adds the stage to the input multiplexer for it to receive input events.
		inputMultiplexer.addProcessor(stage);
		
		//Registers all the input processors from the multiplexer to receive input events.
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		//Creates an ExplorationRenderer which will display the exploration UI using the stage, and will call methods from the world on button clicks.
		explorationRenderer = new ExplorationRenderer(stage, world);
		
		//The game always starts off in exploration mode. This tells the class to display the exploration UI for the game.
		setGameState(GameState.EXPLORING);
		
	}
	
	@Override 
	public void render(float deltaTime)
	{
		//Updates the world.
		update(deltaTime);
		//Draws the world, along with the UI.
		draw(deltaTime);
	}
	
	/** Updates the world and the world camera. */
	private void update(float deltaTime)
	{
		//Updates the world and its GameObjects. 
		world.update(deltaTime);
		
		//Updates the camera used to view the world.
		worldRenderer.updateCamera();
	}
	
	/** Draws the UI, along with the world and its contained GameObjects. */
	private void draw(float deltaTime)
	{
		//Sets OpenGL to clear the screen with white.
		Gdx.gl.glClearColor(1,1,1,1);
		//Clears the screen.
		super.render(deltaTime);
		
		//Renders and draws the world using the worldRenderer.
		worldRenderer.render();
		
		//Draws the HUD to the screen, depending on game state.
		hudRenderer.draw(deltaTime);
	}
	
	private void drawExploringUI(float deltaTime)
	{
		//Update the stage
		stage.act(deltaTime);
		//Draw the 2d widgets on the stage.
		stage.draw();
	}
	
	/** Sets the GameState. Updates the hudRenderer to draw the correct HUD. */
	private void setGameState(GameState state)
	{
		//Update this instance's gameState.
		gameState = state;
		
		//Switch the gameState to change the HUD being rendered.
		switch(gameState)
		{
		case EXPLORING:
			hudRenderer = explorationRenderer;
			break;
		}
		
		//Tells the renderer to re-place the widgets onto the stage so that the stage contains the correct widgets for the current renderer.
		hudRenderer.reset();
	}
	
	@Override
	public void show() 
	{		
	}

	@Override
	public void pause() 
	{
	}

	@Override
	public void resume()
	{
	}
	
	@Override
	public void resize(int width, int height)
	{
		//Re-compute the value of the gui/worldWidth and gui/worldHeight superclass variables to ensure that the GUI and the world cameras fits the aspect ratio of
		//the screen.
		super.resize(width, height);
		
		stage.setViewport(guiWidth, guiHeight);
		
		hudRenderer.reset();
		
		//Resizes the camera used by the world renderer. We specify the worldWidth and worldHeight. They store the desired size of a camera which displays the world.
		//These values were resized according to the aspect ratio of the screen so that nothing is stretched, and that the world coordinate system stays the same. The
		//third parameter passes the smaller of scaleX and scaleY, telling the renderer to render lines at a larger or smaller thickness depending on how much the
		//smallest dimension had to be scaled. The smallest dimension is taken because the frustum is never stretched more than its smallest dimension.
		worldRenderer.resize(worldWidth, worldHeight, Math.min(screenScaleX, screenScaleY));
	}
	
}
