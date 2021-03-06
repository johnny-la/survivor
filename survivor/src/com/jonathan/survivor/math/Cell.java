package com.jonathan.survivor.math;

/** 
 * Basic implementation of a Cell containing a row and a column
 * @author Jonathan
 *
 */

public class Cell 
{
	/** Stores the row and column of the cell. */
	private int row, col;
	
	/** Creates a cell at (0,0) */
	public Cell()
	{
	}
	
	/** Creates a cell with the specified row and column */
	public Cell(int row, int col)
	{
		this.row = row;
		this.col = col;
	}
	
	/** Moves the cell left by decrementing the column */
	public void moveLeft()
	{
		col--;
	}
	
	/** Moves the cell right by incrementing the column */
	public void moveRight()
	{
		col++;
	}
	
	/** Moves the cell up by incrementing the row */
	public void moveUp()
	{
		row++;
	}
	
	/** Moves the cell down by decrementing the row */
	public void moveDown()
	{
		row--;
	}
	
	/** Sets the row and the column of the cell to the specified values. */
	public void set(int row, int col)
	{
		this.row = row;
		this.col = col;
	}
	
	/** Retrieves the cell's row */
	public int getRow()
	{
		return row;
	}
	
	/** Sets the cell's row */
	public void setRow(int row)
	{
		this.row = row;
	}
	
	/** Returns the cell's column */
	public int getCol()
	{
		return col;
	}
	
	/** Sets the cell's column */
	public void setCol(int col)
	{
		this.col = col;
	}
}
