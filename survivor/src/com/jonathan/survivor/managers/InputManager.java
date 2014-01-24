package com.jonathan.survivor.managers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.jonathan.survivor.World;

public class InputManager implements InputProcessor
{
	/** Stores the world that the manager will modify according to user input. */
	private World world;
	
	/** Creates an InputManager with the given world. This manager receives all touch events and reacts by calling the appropriate methods for the World. */
	public InputManager(World world)
	{
		//Stores the world instance the InputManager will control.
		this.world = world;
	}
	
	/** Called when the user clicks then releases anywhere on the screen. */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		
		return false; 
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
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
