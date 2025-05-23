/*
 *  Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.jso.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSBuffer;
import org.teavm.jso.JSBufferType;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSString;
import org.teavm.jso.typedarrays.BigInt64Array;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Float64Array;
import org.teavm.jso.typedarrays.Int16Array;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Int8Array;
import org.teavm.jso.typedarrays.Uint16Array;
import org.teavm.junit.EachTestCompiledSeparately;
import org.teavm.junit.OnlyPlatform;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;
import org.teavm.junit.TestPlatform;

@RunWith(TeaVMTestRunner.class)
@SkipJVM
@OnlyPlatform({TestPlatform.JAVASCRIPT, TestPlatform.WEBASSEMBLY_GC})
@EachTestCompiledSeparately
public class ConversionTest {
    @Test
    public void convertsPrimitivesToJavaScript() {
        assertEquals("true:2:3:64:4:5.5:6.5:foo:7", combinePrimitives(true, (byte) 2, (short) 3,
                '@', 4, 5.5F, 6.5, "foo", 7L));
    }

    @Test
    public void convertsPrimitivesToJava() {
        Primitives map = getPrimitives();
        assertTrue(map.getA());
        assertEquals(2, map.getB());
        assertEquals(3, map.getC());
        assertEquals('@', map.getD());
        assertEquals(4, map.getE());
        assertEquals(5.5, map.getF(), 0.01);
        assertEquals(6.5, map.getG(), 0.01);
        assertEquals("foo", map.getH());
        assertEquals(7L, map.getI());
    }

    @Test
    public void convertsPrimitiveArraysToJavaScript() {
        assertEquals("true:2:3:64:4:5.5:6.5:foo:7", combinePrimitiveArrays(new boolean[] { true }, new byte[] { 2 },
                new short[] { 3 }, new char[] { '@' }, new int[] { 4 }, new float[] { 5.5F }, new double[] { 6.5 },
                new String[] { "foo" }, new long[] { 7L }));
    }

    @Test
    public void convertsPrimitiveArraysToJava() {
        PrimitiveArrays arrays = getPrimitiveArrays();

        boolean[] booleanArray = arrays.getA();
        assertEquals(1, booleanArray.length);
        assertTrue(booleanArray[0]);

        assertArrayEquals(new byte[] { 2 }, arrays.getB());
        assertArrayEquals(new short[] { 3 }, arrays.getC());
        assertArrayEquals(new char[] { '@' }, arrays.getD());
        assertArrayEquals(new int[] { 4 }, arrays.getE());
        assertArrayEquals(new float[] { 5.5F }, arrays.getF(), 0.01F);
        assertArrayEquals(new double[] { 6.5 }, arrays.getG(), 0.01);
        assertArrayEquals(new String[] { "foo" }, arrays.getH());
        assertArrayEquals(new long[] { 7L }, arrays.getI());
    }

    @Test
    public void convertsPrimitiveTypedArraysToJava() {
        PrimitiveArrays arrays = getPrimitiveTypedArrays();

        boolean[] booleanArray = arrays.getA();
        assertEquals(1, booleanArray.length);
        assertTrue(booleanArray[0]);

        assertArrayEquals(new byte[] { 2 }, arrays.getB());
        assertArrayEquals(new short[] { 3 }, arrays.getC());
        assertArrayEquals(new char[] { '@' }, arrays.getD());
        assertArrayEquals(new int[] { 4 }, arrays.getE());
        assertArrayEquals(new float[] { 5.5F }, arrays.getF(), 0.01F);
        assertArrayEquals(new double[] { 6.5 }, arrays.getG(), 0.01);
        assertArrayEquals(new String[] { "foo" }, arrays.getH());
        assertArrayEquals(new long[] { 7L }, arrays.getI());
    }

    @Test
    public void convertsLargeByteArrayToJava() {
        var array = new byte[16384];
        for (var i = 0; i < array.length; ++i) {
            array[i] = (byte) i;
        }
        var typedArray = Int8Array.copyFromJavaArray(array);
        var arrayCopy = typedArray.copyToJavaArray();
        assertArrayEquals(array, arrayCopy);
    }

    @Test
    public void convertsLargeShortArrayToJava() {
        var array = new short[16384];
        for (var i = 0; i < array.length; ++i) {
            array[i] = (short) i;
        }
        var typedArray = Int16Array.copyFromJavaArray(array);
        var arrayCopy = typedArray.copyToJavaArray();
        assertArrayEquals(array, arrayCopy);
    }

    @Test
    public void convertsLargeCharArrayToJava() {
        var array = new char[16384];
        for (var i = 0; i < array.length; ++i) {
            array[i] = (char) i;
        }
        var typedArray = Uint16Array.copyFromJavaArray(array);
        var arrayCopy = typedArray.copyToJavaArray();
        assertArrayEquals(array, arrayCopy);
    }

    @Test
    public void convertsLargeIntArrayToJava() {
        var array = new int[16384];
        for (var i = 0; i < array.length; ++i) {
            array[i] = i;
        }
        var typedArray = Int32Array.copyFromJavaArray(array);
        var arrayCopy = typedArray.copyToJavaArray();
        assertArrayEquals(array, arrayCopy);
    }

    @Test
    public void convertsLargeLongArrayToJava() {
        var array = new long[16384];
        for (var i = 0; i < array.length; ++i) {
            array[i] = i;
        }
        var typedArray = BigInt64Array.copyFromJavaArray(array);
        var arrayCopy = typedArray.copyToJavaArray();
        assertArrayEquals(array, arrayCopy);
    }

    @Test
    public void convertsLargeFloatArrayToJava() {
        var array = new float[16384];
        for (var i = 0; i < array.length; ++i) {
            array[i] = i;
        }
        var typedArray = Float32Array.copyFromJavaArray(array);
        var arrayCopy = typedArray.copyToJavaArray();
        assertArrayEquals(array, arrayCopy, 0.1f);
    }

    @Test
    public void convertsLargeDoubleArrayToJava() {
        var array = new double[16384];
        for (var i = 0; i < array.length; ++i) {
            array[i] = i;
        }
        var typedArray = Float64Array.copyFromJavaArray(array);
        var arrayCopy = typedArray.copyToJavaArray();
        assertArrayEquals(array, arrayCopy, 0.1);
    }

    @Test
    public void convertsPrimitiveArrays2ToJavaScript() {
        assertEquals("true:2:3:64:4:5.5:6.5:foo:7", combinePrimitiveArrays2(new boolean[][] {{ true }},
                new byte[][] {{ 2 }}, new short[][] {{ 3 }}, new char[][] {{ '@' }}, new int[][] {{ 4 }},
                new float[][] {{ 5.5F }}, new double[][] {{ 6.5 }}, new String[][] {{ "foo" }},
                new long[][] {{ 7L }}));
    }

    @Test
    public void convertsPrimitiveArrays2ToJava() {
        PrimitiveArrays2 arrays = getPrimitiveArrays2();

        boolean[][] booleanArray = arrays.getA();
        assertEquals(1, booleanArray.length);
        assertEquals(1, booleanArray[0].length);
        assertTrue(booleanArray[0][0]);

        assertArrayEquals(new byte[] { 2 }, arrays.getB()[0]);
        assertArrayEquals(new short[] { 3 }, arrays.getC()[0]);
        assertArrayEquals(new char[] { '@' }, arrays.getD()[0]);
        assertArrayEquals(new int[] { 4 }, arrays.getE()[0]);
        assertArrayEquals(new float[] { 5.5F }, arrays.getF()[0], 0.01F);
        assertArrayEquals(new double[] { 6.5 }, arrays.getG()[0], 0.01);
        assertArrayEquals(new String[] { "foo" }, arrays.getH()[0]);
        assertArrayEquals(new long[] { 7 }, arrays.getI()[0]);
    }

    @Test
    public void convertsPrimitiveArrays4ToJavaScript() {
        assertEquals("true:2:3:64:4:5.5:6.5:foo", combinePrimitiveArrays4(new boolean[][][][] {{{{ true }}}},
                new byte[][][][] {{{{ 2 }}}}, new short[][][][] {{{{ 3 }}}}, new char[][][][] {{{{ '@' }}}},
                new int[][][][] {{{{ 4 }}}}, new float[][][][] {{{{ 5.5F }}}}, new double[][][][] {{{{ 6.5 }}}},
                new String[][][][] {{{{ "foo" }}}}));
    }

    @Test
    public void convertsPrimitiveArrays4ToJava() {
        PrimitiveArrays4 arrays = getPrimitiveArrays4();

        boolean[][][][] booleanArray = arrays.getA();
        assertEquals(1, booleanArray.length);
        assertEquals(1, booleanArray[0][0][0].length);
        assertTrue(booleanArray[0][0][0][0]);

        assertArrayEquals(new byte[] { 2 }, arrays.getB()[0][0][0]);
        assertArrayEquals(new short[] { 3 }, arrays.getC()[0][0][0]);
        assertArrayEquals(new char[] { '@' }, arrays.getD()[0][0][0]);
        assertArrayEquals(new int[] { 4 }, arrays.getE()[0][0][0]);
        assertArrayEquals(new float[] { 5.5F }, arrays.getF()[0][0][0], 0.01F);
        assertArrayEquals(new double[] { 6.5 }, arrays.getG()[0][0][0], 0.01);
        assertArrayEquals(new String[] { "foo" }, arrays.getH()[0][0][0]);
    }

    @Test
    public void passesJSObject() {
        assertEquals("(foo)", surround(JSString.valueOf("foo")).stringValue());
    }

    @Test
    public void convertsArrayOfJSObject() {
        assertEquals("(foo)", surround(new JSString[] { JSString.valueOf("foo") })[0].stringValue());
        assertEquals("(foo)", surround(new JSString[][] {{ JSString.valueOf("foo") }})[0][0].stringValue());
        assertEquals("(foo)", surround(new JSString[][][][] {{{{ JSString.valueOf("foo") }}}})[0][0][0][0]
                .stringValue());
    }

    @Test
    public void copiesArray() {
        int[] array = { 23 };
        assertEquals(24, mutate(array));
        assertEquals(23, array[0]);
    }


    @Test
    public void passBuffer() {
        var buffer = ByteBuffer.allocateDirect(8)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();
        buffer.put(0, 2);
        buffer.put(1, 3);
        assertEquals(5, readBuffer(buffer));

        var floatBuffer = ByteBuffer.allocateDirect(8)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        floatBuffer.put(0, 1.1f);
        floatBuffer.put(1, 2.2f);
        assertEquals(3.3, readFloatBuffer(floatBuffer), 0.01);
    }

    @JSBody(params = { "a", "b", "c", "d", "e", "f", "g", "h", "i" }, script = ""
            + "return '' + a + ':' + b + ':' + c + ':' + d + ':' + e + ':' + f.toFixed(1) + ':'"
                    + "+ g.toFixed(1) + ':' + h + ':' + i;")
    private static native String combinePrimitives(boolean a, byte b, short c, char d, int e, float f, double g,
            String h, long i);

    @JSBody(script = "return { a: true, b: 2, c: 3, d: 64, e: 4, f: 5.5, g: 6.5, h : 'foo', i: BigInt(7) };")
    private static native Primitives getPrimitives();

    @JSBody(params = { "a", "b", "c", "d", "e", "f", "g", "h", "i" }, script = ""
            + "return '' + a[0] + ':' + b[0] + ':' + c[0] + ':' + d[0] + ':' + e[0] + ':' + f[0].toFixed(1) + ':'"
                    + "+ g[0].toFixed(1) + ':' + h[0] + ':' + i[0];")
    private static native String combinePrimitiveArrays(boolean[] a, byte[] b, short[] c, char[] d, int[] e, float[] f,
            double[] g, String[] h, long[] i);

    @JSBody(script = "return { a: [true], b: [2], c: [3], d: [64], e: [4], f: [5.5], "
            + "g: [6.5], h: ['foo'], i: [BigInt(7)] };")
    private static native PrimitiveArrays getPrimitiveArrays();

    @JSBody(script = "return { "
            + "a: [true], "
            + "b: new Int8Array([2]), "
            + "c: new Int16Array([3]), "
            + "d: new Uint16Array([64]), "
            + "e: new Int32Array([4]), "
            + "f: new Float32Array([5.5]), "
            + "g: new Float64Array([6.5]), "
            + "h: ['foo'], "
            + "i: new BigInt64Array([BigInt(7)])"
            + "};")
    private static native PrimitiveArrays getPrimitiveTypedArrays();

    @JSBody(params = { "a", "b", "c", "d", "e", "f", "g", "h", "i" }, script = ""
            + "return '' + a[0][0] + ':' + b[0][0] + ':' + c[0][0] + ':' + d[0][0] + ':' + e[0][0] + ':' "
                    + "+ f[0][0].toFixed(1) + ':' + g[0][0].toFixed(1) + ':' + h[0][0] + ':' + i[0][0];")
    private static native String combinePrimitiveArrays2(boolean[][] a, byte[][] b, short[][] c, char[][] d, int[][] e,
            float[][] f, double[][] g, String[][] h, long[][] i);

    @JSBody(script = "return { a: [[true]], b: [[2]], c: [[3]], d: [[64]], e: [[4]], f: [[5.5]], "
            + "g: [[6.5]], h: [['foo']], i: [[BigInt(7)]] };")
    private static native PrimitiveArrays2 getPrimitiveArrays2();

    @JSBody(params = { "a", "b", "c", "d", "e", "f", "g", "h" }, script = ""
            + "return '' + a[0][0][0][0] + ':' + b[0][0][0][0] + ':' + c[0][0][0][0] + ':' + d[0][0][0][0] + ':' "
                    + "+ e[0][0][0][0] + ':' + f[0][0][0][0].toFixed(1) + ':' + g[0][0][0][0].toFixed(1) + ':' "
                    + "+ h[0][0][0][0];")
    private static native String combinePrimitiveArrays4(boolean[][][][] a, byte[][][][] b, short[][][][] c,
            char[][][][] d, int[][][][] e, float[][][][] f, double[][][][] g, String[][][][] h);

    @JSBody(script = "return { a : [[[[true]]]], b : [[[[2]]]], c : [[[[3]]]], d : [[[[64]]]], "
            + "e : [[[[4]]]], f : [[[[5.5]]]], g : [[[[6.5]]]], h : [[[['foo']]]] };")
    private static native PrimitiveArrays4 getPrimitiveArrays4();

    interface Primitives extends JSObject {
        @JSProperty
        boolean getA();

        @JSProperty
        byte getB();

        @JSProperty
        short getC();

        @JSProperty
        char getD();

        @JSProperty
        int getE();

        @JSProperty
        float getF();

        @JSProperty
        double getG();

        @JSProperty
        String getH();

        @JSProperty
        long getI();
    }

    interface PrimitiveArrays extends JSObject {
        @JSProperty
        boolean[] getA();

        @JSProperty
        byte[] getB();

        @JSProperty
        short[] getC();

        @JSProperty
        char[] getD();

        @JSProperty
        int[] getE();

        @JSProperty
        float[] getF();

        @JSProperty
        double[] getG();

        @JSProperty
        String[] getH();

        @JSProperty
        long[] getI();
    }

    interface PrimitiveArrays2 extends JSObject {
        @JSProperty
        boolean[][] getA();

        @JSProperty
        byte[][] getB();

        @JSProperty
        short[][] getC();

        @JSProperty
        char[][] getD();

        @JSProperty
        int[][] getE();

        @JSProperty
        float[][] getF();

        @JSProperty
        double[][] getG();

        @JSProperty
        String[][] getH();

        @JSProperty
        long[][] getI();
    }

    interface PrimitiveArrays4 extends JSObject {
        @JSProperty
        boolean[][][][] getA();

        @JSProperty
        byte[][][][] getB();

        @JSProperty
        short[][][][] getC();

        @JSProperty
        char[][][][] getD();

        @JSProperty
        int[][][][] getE();

        @JSProperty
        float[][][][] getF();

        @JSProperty
        double[][][][] getG();

        @JSProperty
        String[][][][] getH();
    }

    @JSBody(params = "str", script = "return '(' + str + ')';")
    private static native JSString surround(JSString str);

    @JSBody(params = "str", script = "return ['(' + str[0] + ')'];")
    private static native JSString[] surround(JSString[] str);

    @JSBody(params = "str", script = "return [['(' + str[0][0] + ')']];")
    private static native JSString[][] surround(JSString[][] str);

    @JSBody(params = "str", script = "return [[[['(' + str[0][0][0][0] + ')']]]];")
    private static native JSString[][][][] surround(JSString[][][][] str);

    @JSBody(params = "array", script = "array[0]++; return array[0];")
    private static native int mutate(int[] array);


    @JSBody(params = "buffer", script = "return buffer[0] + buffer[1];")
    private static native int readBuffer(IntBuffer buffer);

    @JSBody(params = "buffer", script = "return buffer[0] + buffer[1];")
    private static native float readFloatBuffer(@JSBuffer(JSBufferType.FLOAT32) Buffer buffer);
}
