/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;

public class LocalShapeInfo extends BulletBase {
	private long swigCPtr;
	
	protected LocalShapeInfo(final String className, long cPtr, boolean cMemoryOwn) {
		super(className, cPtr, cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	/** Construct a new LocalShapeInfo, normally you should not need this constructor it's intended for low-level usage. */ 
	public LocalShapeInfo(long cPtr, boolean cMemoryOwn) {
		this("LocalShapeInfo", cPtr, cMemoryOwn);
		construct();
	}
	
	@Override
	protected void reset(long cPtr, boolean cMemoryOwn) {
		if (!destroyed)
			destroy();
		super.reset(swigCPtr = cPtr, cMemoryOwn);
	}
	
	public static long getCPtr(LocalShapeInfo obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!destroyed)
			destroy();
		super.finalize();
	}

  @Override protected synchronized void delete() {
		if (swigCPtr != 0) {
			if (swigCMemOwn) {
				swigCMemOwn = false;
				CollisionJNI.delete_LocalShapeInfo(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

  public void setShapePart(int value) {
    CollisionJNI.LocalShapeInfo_shapePart_set(swigCPtr, this, value);
  }

  public int getShapePart() {
    return CollisionJNI.LocalShapeInfo_shapePart_get(swigCPtr, this);
  }

  public void setTriangleIndex(int value) {
    CollisionJNI.LocalShapeInfo_triangleIndex_set(swigCPtr, this, value);
  }

  public int getTriangleIndex() {
    return CollisionJNI.LocalShapeInfo_triangleIndex_get(swigCPtr, this);
  }

  public LocalShapeInfo() {
    this(CollisionJNI.new_LocalShapeInfo(), true);
  }

}
