package com.jonathan.survivor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.Survivor;
import com.jonathan.survivor.utils.SpriteUtils;

public class CompanySplashScreen extends Screen
{
	private OrthographicCamera guiCamera;	//Stores the camera displaying the GUI (Splash Image).
	
	private static final float TIME_SHOWN = 1.5f;	//Stores the amount of time the splash screen is shown before moving to the loading screen.
	private float timeElapsed;	//Stores the time elapsed since the splash screen started showing.
	
	
	public CompanySplashScreen(Survivor game)
	{
		super(game);
	}
	
	@Override
	public void show() 
	{
		//Load the assets needed to display the CompanySplashScreen. This loads the splash screen image, along with the textures needed to draw
		//the loading screen right after
		assets.loadInitialAssets();
		
		//Creates a new GUI camera with a default width and height defined by the Screen superclass.
		guiCamera = new OrthographicCamera(guiWidth, guiHeight);
		
	}
	
	@Override
	public void render(float deltaTime)
	{
		//Sets OpenGL to clear the screen with white.
		Gdx.gl.glClearColor(1,1,1,1);
		//Clears the screen
		super.render(deltaTime);
		
		//Increment the time elapsed since the splash screen started showing by deltaTime. This keeps track of when we should exit the screen.
		timeElapsed += deltaTime;
		
		//If the splash screen has been shown for a time greater than the maximum time it should be shown, switch to the loading screen.
		if(timeElapsed > TIME_SHOWN || game.DEBUG_MODE)
		{
			//Switch to the loading screen.
			game.setScreen(new LoadingScreen(game));
			return;
		}
		
		//((Texture)Assets.loadingScreenAtlas.getTextures().toArray()[0]).setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		//Sets the batcher's projection matrix to the camera's (mandatory)
		batcher.setProjectionMatrix(guiCamera.combined);
		//Begin batching sprites.
		batcher.begin();
		
		//Set the center position of the companyLogo sprite to (0,0) (center of the camera).
		SpriteUtils.setPosition(assets.companyLogo, 0, 0);
		//Draw the companyLogo sprite.
		assets.companyLogo.draw(batcher);
		
		//Draw the sprites batched inside the batcher.
		batcher.end();
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
		//Update the super.guiWidth/Height variables
		super.resize(width, height);
		
		//Update the viewport width and height of the GUI camera using the superclass variables guiWidth/Height. Inside super.resize(), these variables
		//were re-computed so that the aspect ratio of the guiCamera would fit the new screen size. This prevents scaling more in one axis than another.
		guiCamera.viewportWidth = guiWidth;
		guiCamera.viewportHeight = guiHeight;
		
		//Update the GUI camera to apply size changes.
		guiCamera.update();
	}
	
}
