/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.physics.bullet;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.badlogic.gdx.graphics.g3d.model.Node;

public class Bullet {
	protected static boolean useRefCounting = false;
	protected static boolean enableLogging = true;
	/** Loads the native Bullet native library and initializes the gdx-bullet extension. 
	 * Must be called before any of the bullet classes/methods can be used. */
	public static void init () {
		init(false);
	}
	
	/** Loads the native Bullet native library and initializes the gdx-bullet extension.
	 * Must be called before any of the bullet classes/methods can be used.
	 * @param useRefCounting Whether to use reference counting, causing object to be destroyed when no longer referenced. You must 
	 * use {@link BulletBase#obtain()} and {@link BulletBase#release()} when using reference counting. */
	public static void init (boolean useRefCounting) {
		init(useRefCounting, true);
	}
	
	/** Loads the native Bullet native library and initializes the gdx-bullet extension.
	 * Must be called before any of the bullet classes/methods can be used.
	 * @param useRefCounting Whether to use reference counting, causing object to be destroyed when no longer referenced. You must 
	 * use {@link BulletBase#obtain()} and {@link BulletBase#release()} when using reference counting.
	 * @param logging Whether to log an error on potential errors in the application. */
	public static void init (boolean useRefCounting, boolean logging) {
		Bullet.useRefCounting = useRefCounting;
		Bullet.enableLogging = logging;
		new SharedLibraryLoader().load("gdx-bullet");
	}
	
	protected static class ShapePart {
		public Array<MeshPart> parts = new Array<MeshPart>();
		public Matrix4 transform = new Matrix4();
	}
	
	private final static Pool<ShapePart> shapePartPool = new Pool<ShapePart>() {
		@Override
		protected ShapePart newObject () {
			return new ShapePart();
		}
	};
	private final static Array<ShapePart> shapePartArray = new Array<ShapePart>();
	
	private final static Matrix4 idt = new Matrix4();
	private final static Matrix4 tmpM = new Matrix4();
	
	public static void getShapeParts(final Node node, final boolean applyTransform, final Array<ShapePart> out, final int offset, final Pool<ShapePart> pool) {
		final Matrix4 transform = applyTransform ? node.localTransform : idt;
		if (node.parts.size > 0) {
			ShapePart part = null;
			for (int i = offset, n = out.size; i < n; i++) {
				final ShapePart p = out.get(i); 
				if (Arrays.equals(p.transform.val, transform.val)) {
					part = p;
					break;
				}
			}
			if (part == null) {
				part = pool.obtain();
				part.parts.clear();
				part.transform.set(transform);
				out.add(part);
			}
			for (int i = 0, n = node.parts.size; i < n; i++)
				part.parts.add(node.parts.get(i).meshPart);
		}
		if (node.children.size > 0) {
			final boolean transformed = applyTransform && !Arrays.equals(transform.val, idt.val); 
			final int o = transformed ? out.size : offset;
			getShapeParts(node.children, out, o, pool);
			if (transformed) {
				for (int i = o, n = out.size; i < n; i++) {
					final ShapePart part = out.get(i);
					tmpM.set(part.transform);
					part.transform.set(transform).mul(tmpM);
				}
			}
		}
	}
	
	public static void getShapeParts(final Array<Node> nodes, final Array<ShapePart> out, final int offset, final Pool<ShapePart> pool) {
		final int n = nodes.size;
		for (int i = 0; i < n; i++)
			getShapeParts(nodes.get(i), true, out, offset, pool);
	}

	public static btCollisionShape obtainStaticNodeShape(final Node node, final boolean applyTransform) {
		getShapeParts(node, applyTransform, shapePartArray, 0, shapePartPool);
		btCollisionShape result = obtainStaticShape(shapePartArray);
		shapePartPool.freeAll(shapePartArray);
		shapePartArray.clear();
		return result;
	}
	
	public static btCollisionShape obtainStaticNodeShape(final Array<Node> nodes) {
		getShapeParts(nodes, shapePartArray, 0, shapePartPool);
		btCollisionShape result = obtainStaticShape(shapePartArray);
		shapePartPool.freeAll(shapePartArray);
		shapePartArray.clear();
		return result;
	}
	
	public static btCollisionShape obtainStaticShape(final Array<ShapePart> parts) {
		if (parts.size == 0)
			return null;
		if (parts.size == 1 && Arrays.equals(parts.get(0).transform.val, idt.val))
			return btBvhTriangleMeshShape.obtain(parts.get(0).parts);
		btCompoundShape result = new btCompoundShape();
		result.obtain();
		for (int i = 0, n = parts.size; i < n; i++) {
			final btBvhTriangleMeshShape shape = btBvhTriangleMeshShape.obtain(parts.get(i).parts);
			result.addChildShape(parts.get(i).transform, shape);
			shape.release();
		}
		return result;
	}
}
