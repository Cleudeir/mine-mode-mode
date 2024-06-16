// Source code is decompiled from a .class file using FernFlower decompiler.
package com.github.raverbury.aggroindicator.util;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MathHelper {
   public MathHelper() {
   }

   public static Quaternionf rotationDegrees(Vector3f v3, float angle) {
      angle *= 0.017453292F;
      float f = sin(angle / 2.0F);
      float i = v3.x() * f;
      float j = v3.y() * f;
      float k = v3.z() * f;
      float r = cos(angle / 2.0F);
      return new Quaternionf(i, j, k, r);
   }

   private static float sin(float p_80155_) {
      return (float)Math.sin((double)p_80155_);
   }

   private static float cos(float p_80152_) {
      return (float)Math.cos((double)p_80152_);
   }
}
