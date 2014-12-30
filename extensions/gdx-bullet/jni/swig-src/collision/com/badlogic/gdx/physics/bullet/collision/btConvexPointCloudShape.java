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

public class btConvexPointCloudShape extends btPolyhedralConvexAabbCachingShape {
	private long swigCPtr;
	
	protected btConvexPointCloudShape(final String className, long cPtr, boolean cMemoryOwn) {
		super(className, CollisionJNI.btConvexPointCloudShape_SWIGUpcast(cPtr), cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	/** Construct a new btConvexPointCloudShape, normally you should not need this constructor it's intended for low-level usage. */
	public btConvexPointCloudShape(long cPtr, boolean cMemoryOwn) {
		this("btConvexPointCloudShape", cPtr, cMemoryOwn);
		construct();
	}
	
	@Override
	protected void reset(long cPtr, boolean cMemoryOwn) {
		if (!destroyed)
			destroy();
		super.reset(CollisionJNI.btConvexPointCloudShape_SWIGUpcast(swigCPtr = cPtr), cMemoryOwn);
	}
	
	public static long getCPtr(btConvexPointCloudShape obj) {
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
				CollisionJNI.delete_btConvexPointCloudShape(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

  public btConvexPointCloudShape() {
    this(CollisionJNI.new_btConvexPointCloudShape__SWIG_0(), true);
  }

  public btConvexPointCloudShape(btVector3 points, int numPoints, Vector3 localScaling, boolean computeAabb) {
    this(CollisionJNI.new_btConvexPointCloudShape__SWIG_1(btVector3.getCPtr(points), points, numPoints, localScaling, computeAabb), true);
  }

  public btConvexPointCloudShape(btVector3 points, int numPoints, Vector3 localScaling) {
    this(CollisionJNI.new_btConvexPointCloudShape__SWIG_2(btVector3.getCPtr(points), points, numPoints, localScaling), true);
  }

  public void setPoints(btVector3 points, int numPoints, boolean computeAabb, Vector3 localScaling) {
    CollisionJNI.btConvexPointCloudShape_setPoints__SWIG_0(swigCPtr, this, btVector3.getCPtr(points), points, numPoints, computeAabb, localScaling);
  }

  public void setPoints(btVector3 points, int numPoints, boolean computeAabb) {
    CollisionJNI.btConvexPointCloudShape_setPoints__SWIG_1(swigCPtr, this, btVector3.getCPtr(points), points, numPoints, computeAabb);
  }

  public void setPoints(btVector3 points, int numPoints) {
    CollisionJNI.btConvexPointCloudShape_setPoints__SWIG_2(swigCPtr, this, btVector3.getCPtr(points), points, numPoints);
  }

  public btVector3 getUnscaledPoints() {
    long cPtr = CollisionJNI.btConvexPointCloudShape_getUnscaledPoints__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new btVector3(cPtr, false);
  }

  public int getNumPoints() {
    return CollisionJNI.btConvexPointCloudShape_getNumPoints(swigCPtr, this);
  }

  public Vector3 getScaledPoint(int index) {
	return CollisionJNI.btConvexPointCloudShape_getScaledPoint(swigCPtr, this, index);
}

}
