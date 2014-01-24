/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;

public class btWheelInfoConstructionInfo extends BulletBase {
	private long swigCPtr;
	
	protected btWheelInfoConstructionInfo(final String className, long cPtr, boolean cMemoryOwn) {
		super(className, cPtr, cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	/** Construct a new btWheelInfoConstructionInfo, normally you should not need this constructor it's intended for low-level usage. */ 
	public btWheelInfoConstructionInfo(long cPtr, boolean cMemoryOwn) {
		this("btWheelInfoConstructionInfo", cPtr, cMemoryOwn);
		construct();
	}
	
	@Override
	protected void reset(long cPtr, boolean cMemoryOwn) {
		if (!destroyed)
			destroy();
		super.reset(swigCPtr = cPtr, cMemoryOwn);
	}
	
	public static long getCPtr(btWheelInfoConstructionInfo obj) {
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
				DynamicsJNI.delete_btWheelInfoConstructionInfo(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

  public void setChassisConnectionCS(btVector3 value) {
    DynamicsJNI.btWheelInfoConstructionInfo_chassisConnectionCS_set(swigCPtr, this, btVector3.getCPtr(value), value);
  }

  public btVector3 getChassisConnectionCS() {
    long cPtr = DynamicsJNI.btWheelInfoConstructionInfo_chassisConnectionCS_get(swigCPtr, this);
    return (cPtr == 0) ? null : new btVector3(cPtr, false);
  }

  public void setWheelDirectionCS(btVector3 value) {
    DynamicsJNI.btWheelInfoConstructionInfo_wheelDirectionCS_set(swigCPtr, this, btVector3.getCPtr(value), value);
  }

  public btVector3 getWheelDirectionCS() {
    long cPtr = DynamicsJNI.btWheelInfoConstructionInfo_wheelDirectionCS_get(swigCPtr, this);
    return (cPtr == 0) ? null : new btVector3(cPtr, false);
  }

  public void setWheelAxleCS(btVector3 value) {
    DynamicsJNI.btWheelInfoConstructionInfo_wheelAxleCS_set(swigCPtr, this, btVector3.getCPtr(value), value);
  }

  public btVector3 getWheelAxleCS() {
    long cPtr = DynamicsJNI.btWheelInfoConstructionInfo_wheelAxleCS_get(swigCPtr, this);
    return (cPtr == 0) ? null : new btVector3(cPtr, false);
  }

  public void setSuspensionRestLength(float value) {
    DynamicsJNI.btWheelInfoConstructionInfo_suspensionRestLength_set(swigCPtr, this, value);
  }

  public float getSuspensionRestLength() {
    return DynamicsJNI.btWheelInfoConstructionInfo_suspensionRestLength_get(swigCPtr, this);
  }

  public void setMaxSuspensionTravelCm(float value) {
    DynamicsJNI.btWheelInfoConstructionInfo_maxSuspensionTravelCm_set(swigCPtr, this, value);
  }

  public float getMaxSuspensionTravelCm() {
    return DynamicsJNI.btWheelInfoConstructionInfo_maxSuspensionTravelCm_get(swigCPtr, this);
  }

  public void setWheelRadius(float value) {
    DynamicsJNI.btWheelInfoConstructionInfo_wheelRadius_set(swigCPtr, this, value);
  }

  public float getWheelRadius() {
    return DynamicsJNI.btWheelInfoConstructionInfo_wheelRadius_get(swigCPtr, this);
  }

  public void setSuspensionStiffness(float value) {
    DynamicsJNI.btWheelInfoConstructionInfo_suspensionStiffness_set(swigCPtr, this, value);
  }

  public float getSuspensionStiffness() {
    return DynamicsJNI.btWheelInfoConstructionInfo_suspensionStiffness_get(swigCPtr, this);
  }

  public void setWheelsDampingCompression(float value) {
    DynamicsJNI.btWheelInfoConstructionInfo_wheelsDampingCompression_set(swigCPtr, this, value);
  }

  public float getWheelsDampingCompression() {
    return DynamicsJNI.btWheelInfoConstructionInfo_wheelsDampingCompression_get(swigCPtr, this);
  }

  public void setWheelsDampingRelaxation(float value) {
    DynamicsJNI.btWheelInfoConstructionInfo_wheelsDampingRelaxation_set(swigCPtr, this, value);
  }

  public float getWheelsDampingRelaxation() {
    return DynamicsJNI.btWheelInfoConstructionInfo_wheelsDampingRelaxation_get(swigCPtr, this);
  }

  public void setFrictionSlip(float value) {
    DynamicsJNI.btWheelInfoConstructionInfo_frictionSlip_set(swigCPtr, this, value);
  }

  public float getFrictionSlip() {
    return DynamicsJNI.btWheelInfoConstructionInfo_frictionSlip_get(swigCPtr, this);
  }

  public void setMaxSuspensionForce(float value) {
    DynamicsJNI.btWheelInfoConstructionInfo_maxSuspensionForce_set(swigCPtr, this, value);
  }

  public float getMaxSuspensionForce() {
    return DynamicsJNI.btWheelInfoConstructionInfo_maxSuspensionForce_get(swigCPtr, this);
  }

  public void setBIsFrontWheel(boolean value) {
    DynamicsJNI.btWheelInfoConstructionInfo_bIsFrontWheel_set(swigCPtr, this, value);
  }

  public boolean getBIsFrontWheel() {
    return DynamicsJNI.btWheelInfoConstructionInfo_bIsFrontWheel_get(swigCPtr, this);
  }

  public btWheelInfoConstructionInfo() {
    this(DynamicsJNI.new_btWheelInfoConstructionInfo(), true);
  }

}
