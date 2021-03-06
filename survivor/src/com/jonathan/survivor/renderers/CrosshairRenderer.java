package com.jonathan.survivor.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jonathan.survivor.World;
import com.jonathan.survivor.entity.Human.State;
import com.jonathan.survivor.entity.Player;
import com.jonathan.survivor.math.Vector2;

/*
 * Draws the trajectory lines used for every ranged weapon that a Human is charging.
 */

public class CrosshairRenderer 
{
	
	/** Stores the default color of the lines used to draw the crosshairs. */
	private static final Color DEFAULT_LINE_COLOR = new Color(1,0,0,0.5f);
	
	/** Holds the default length of a crosshair line/trajectory line. */
	private static final float LINE_LENGTH = 10f;
	
	/** Stores the max angle of the trajectory line when the player's ranged weapon has just begun charging. */
	private static final float MAX_ANGLE = 70;
	
	/** Stores the world whose information we use to render crosshairs. */
	private World world;
	/** Stores the camera where the terrain is drawn. In this case, the world camera. */
	private OrthographicCamera worldCamera;
	
	/** Stores the ShapeRenderer instance used to draw the level geometry. */
	private ShapeRenderer shapeRenderer;
	
	/** Accepts the world, from which information is gathered about the crosshairs to draw, and the camera where the crosshair lines will be drawn. */
	public CrosshairRenderer(World world, OrthographicCamera worldCamera)
	{
		//Stores the constructor arguments in their respective member variables
		this.world = world;
		this.worldCamera = worldCamera;

		//Creates the ShapeRenderer instance used to draw the trajectory lines for the ranged weapons.
		shapeRenderer = new ShapeRenderer();
		
		//Enables OpenGL ES to draw smooth lines to ensure level geometry looks anti-aliased.
		Gdx.gl.glEnable(GL10.GL_LINE_SMOOTH);
	}
	
	/** Renders the given terrainLevel's geometry using OpenGL ES lines. */
	public void render(float deltaTime)
	{
		//Retrieves the world's player, whose crosshairs will be drawn if he is charging his gun.
		Player player = world.getPlayer();
		
		//If the player is not charging his gun, the trajectory line of the gun does not have to be drawn. Therefore, return this method
		if(player.getState() != State.CHARGE)
			return;
		
		//Sets the projection matrix of the ShapeRenderer to the world camera's, so that the shapes get rendered relative to world coordinates.
		shapeRenderer.setProjectionMatrix(worldCamera.combined);

		//Begins the shape rendering batch. We specify to draw lines.
		shapeRenderer.begin(ShapeType.Line);
		//Sets the line to be black.
		shapeRenderer.setColor(DEFAULT_LINE_COLOR);
		
		//Draws a trajectory line at the position of the player's gun.
		drawTrajectoryLine(player);
				
		//Commits the lines to the ShapeRenderer and draws them to the screen.
		shapeRenderer.end();
	}

	/** Draws the player's trajectory line on the tip of his gun. */
	private void drawTrajectoryLine(Player player) 
	{
		//Gets the point where the trajectory line should be drawn on the player's gun.
		Vector2 crosshairPoint = player.getCrosshairPoint();
		
		//Returns a percent completion of the player charging his gun. Computes a normalized value between 0 and 1, where 1 means that the gun has finished charging.
		float chargeCompletion = player.getChargeCompletion();
		
		//If the charge rate is at 100%, and chargeRate=1, the line will be at a 0 degree angle. If chargeRate=0, the line will be at MAX_ANGLE degrees; completely inaccurate.
		float lineAngle = MAX_ANGLE - chargeCompletion*MAX_ANGLE;
		//The range of the player's ranged weapon is the length of the crosshair line.
		float length = player.getRangedWeapon().getRange();
		
		//Computes the position of the tip of the trajectory line based on the range of the player's ranged weapon.
		float lineEndX = crosshairPoint.x + (float)Math.cos(Math.toRadians(lineAngle))*length;
		float lineEndY = crosshairPoint.y + (float)Math.sin(Math.toRadians(lineAngle))*length;
		
		//Draws the top portion of the trajectory line at the tip of the player's gun.
		shapeRenderer.line(crosshairPoint.x, crosshairPoint.y, lineEndX, lineEndY);
		
		//Changes the y end-point of the trajectory line. Changes it to match draw the bottom portion of the trajectory line.
		lineEndY = crosshairPoint.y - (float)Math.sin(Math.toRadians(lineAngle))*LINE_LENGTH;
		
		//Draws the bottom portion of the trajectory line at the tip of the player's gun.
		shapeRenderer.line(crosshairPoint.x, crosshairPoint.y, lineEndX, lineEndY);
	}
}
