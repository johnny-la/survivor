package com.jonathan.survivor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.jonathan.survivor.Survivor;
import com.jonathan.survivor.utils.SpriteUtils;

public class LoadingScreen extends Screen
{
	private OrthographicCamera guiCamera;	//Stores the camera displaying the loading screen's GUI.
	private Sprite preloaderSprite;	//Stores the sprite the preloader should be showing. Changes depending on loading progress.
	
	public LoadingScreen(Survivor game)
	{
		super(game);
	}
	
	@Override
	public void show()
	{
		//Create a new camera with the designated size retrieved from the Screen superclass.
		guiCamera = new OrthographicCamera(guiWidth, guiHeight);
		
	}
	
	@Override
	public void render(float deltaTime)
	{
		//Clears the screen.
		super.render(deltaTime);
		
		//Updates the loadings done by the assets singleton. Note that updateLoading() simply continues the loading done by the AssetManager of the Assets instance.
		//It is done on a separate thread. Note that this method returns true if the loading is complete. This method must be called every frame to continue loading.
		boolean loadingComplete = assets.updateLoading();
		
		//Updates the sprite used by the preloader to indicate progress.
		updatePreloaderSprite();
		
		//Draw the GUI for the LoadingScreen.
		drawGUI();
		
		//If the loading is complete
		if(loadingComplete)
		{
			//Load the profiles saved inside the hard drive. This must be done here because the AssetsManager class from the Assets singleton can't load JSON files.
			profileManager.loadProfiles();
			
			//Switch to the main menu screen
			game.setScreen(new MainMenuScreen(game));
			//Return this method call. It is good practice to do so to avoid errors.
			return;
		}
		
	}
	
	private void updatePreloaderSprite()
	{
		//Stores the loading progress of the assets singleton, which loads all of the visual/audible assets for the game.
		float progress = assets.getProgress();
		
		System.out.println("Progress " + progress);
		
		//Depending on the loading progress, display a different sprite for the preloader. Note that assets.preloaderSprite.get(0) shows the preloader
		//bar empty, while assets.preloaderSprite.get(5) shows it full.
		if(progress >= 0.95f)
		{
			preloaderSprite = assets.preloaderSprites.get(5);
		}
		else if(progress > 0.8f)
		{
			preloaderSprite = assets.preloaderSprites.get(4);
		}
		else if(progress > 0.6f)
		{
			preloaderSprite = assets.preloaderSprites.get(3);
		}
		else if(progress > 0.4f)
		{
			preloaderSprite = assets.preloaderSprites.get(2);
		}
		else if(progress > 0.2f)
		{
			preloaderSprite = assets.preloaderSprites.get(1);
		}
		else
		{
			preloaderSprite = assets.preloaderSprites.get(0);
		}
	}
	
	/**  Draws the GUI for the Loading Screen */
	private void drawGUI()
	{
		//Sets the loadingText sprite to be in the center of the screen, a little above the preloader.
		SpriteUtils.setPosition(assets.loadingText, 0, 41);
		//Sets the preloader's center to be blow the loading text.
		SpriteUtils.setPosition(preloaderSprite, 0, -10);
		
		//Sets the projection matrix of the sprite batcher to the camera's.
		batcher.setProjectionMatrix(guiCamera.combined);
		//Begin batching and drawing sprites through the SpriteBatch instance.
		batcher.begin();
		
		//Draw the loading text sprite and the preloader sprite using a SpriteBatch.
		assets.loadingText.draw(batcher);
		preloaderSprite.draw(batcher);
		
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
