package com.jonathan.survivor.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jonathan.survivor.CombatLevel;
import com.jonathan.survivor.TerrainLayer;
import com.jonathan.survivor.TerrainLayer.TerrainType;
import com.jonathan.survivor.TerrainLevel;
import com.jonathan.survivor.math.Rectangle;
import com.jonathan.survivor.math.Vector2;

public class CombatRenderer 
{
	
	/** Stores the default width of a line used to draw the geometry for the combat level's black line. */
	private static final float DEFAULT_LINE_WIDTH = 0.05f;
	
	/** Stores the camera where the combat level's terrain is drawn. In this case, the world camera. */
	private OrthographicCamera worldCamera;
	
	/** Stores the ShapeRenderer instance used to draw the level geometry of the combat level. */
	private ShapeRenderer shapeRenderer;
	
	/** Accepts the camera where the terrain lines will be drawn. */
	public CombatRenderer(OrthographicCamera worldCamera)
	{
		//Stores the camera where the terrain lines will be drawn.
		this.worldCamera = worldCamera;
		
		//Creates the ShapeRenderer instance used to draw the combat level's geometry with lines.
		shapeRenderer = new ShapeRenderer();
		
		//Enables OpenGL ES to draw smooth lines to ensure level geometry looks anti-aliased.
		Gdx.gl.glEnable(GL10.GL_LINE_SMOOTH);
	}
	
	/** Renders the given combatLevel's geometry using OpenGL ES lines. */
	public void render(CombatLevel level)
	{
		//Sets the projection matrix of the ShapeRenderer to the world camera's, so that the shapes get rendered relative to world coordinates.
		shapeRenderer.setProjectionMatrix(worldCamera.combined);

		//Begins the shape rendering batch. We specify to draw lines.
		shapeRenderer.begin(ShapeType.Line);
		//Sets the line to be black.
		shapeRenderer.setColor(Color.LIGHT_GRAY);
		
		//Draws the straight line behind the player for the combat level.
		shapeRenderer.line(level.getLeftPoint().x, level.getLeftPoint().y, level.getRightPoint().x, level.getRightPoint().y);
		
		//Commits the lines to the ShapeRenderer and draws them to the screen.
		shapeRenderer.end();
	}

}
