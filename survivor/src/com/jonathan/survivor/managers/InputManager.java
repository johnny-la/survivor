package com.jonathan.survivor.managers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.jonathan.survivor.World;

public class InputManager implements InputProcessor
{
	/** Stores the world that the manager will modify according to user input. */
	private World world;
	
	/** Stores the worldCamera, used to convert touch points to world coordinates. */
	private OrthographicCamera worldCamera;
	
	/** Helper Vector3 used to store the latest touch point. */
	private Vector3 touchPoint;
	
	/** Holds true if the game is paused, and no input events should be delegated to the world. */
	private boolean paused;
	
	/** Creates an InputManager with the given world. This manager receives all touch events and reacts by calling the appropriate methods for the World. */
	public InputManager(World world, OrthographicCamera worldCamera)
	{
		//Stores the world instance the InputManager will control.
		this.world = world;
		
		//Stores the orthographic camera used to convert screen coordinates to world coordinates.
		this.worldCamera = worldCamera;
		
		//Creates a new Vector3, used to hold the coordinates of the latest touch.
		touchPoint = new Vector3();
	}
	
	/** Called when the user holds a press anywhere on the screen. */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		//Don't handle the touch down event if the game is paused.
		if(paused)
			return false;
		
		//Sets the touchPoint Vector3 to the position of the touch.
		touchPoint.set(screenX, screenY, 0);
		//Convert the touch point into world coordinates
		worldCamera.unproject(touchPoint);
		
		//Delegate the touch coordinates to the world.
		world.touchUp(touchPoint.x, touchPoint.y);
		
		return false; 
	}
	
	/** Pauses the InputManager so that it doesn't call any of the world's methods. Effectively pauses input. */
	public void pause()
	{
		//Set the paused flag to true.
		paused = true;
	}
	
	/** Resumes the InputManager so that it can call the world's methods based on touch events. Effectively resumes input handling. */
	public void resume()
	{
		paused = false;
	}

	/* ..................UNUSED................................ */
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
