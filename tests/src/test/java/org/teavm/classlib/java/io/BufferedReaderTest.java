/*
 *  Copyright 2014 Alexey Andreev.
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
package org.teavm.classlib.java.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;

@RunWith(TeaVMTestRunner.class)
public class BufferedReaderTest {
    @Test
    public void readsCharacters() throws IOException {
        String str = "foo bar baz";
        BufferedReader reader = new BufferedReader(new StringReader(str));
        char[] chars = new char[100];
        int charsRead = reader.read(chars);
        assertEquals(str.length(), charsRead);
        assertEquals(str.charAt(0), chars[0]);
        assertEquals(str.charAt(charsRead - 1), chars[charsRead - 1]);
        assertEquals(0, chars[charsRead]);
    }

    @Test
    public void readsCharactersOneByOne() throws IOException {
        String str = "foo";
        BufferedReader reader = new BufferedReader(new StringReader(str));
        assertEquals('f', reader.read());
        assertEquals('o', reader.read());
        assertEquals('o', reader.read());
        assertEquals(-1, reader.read());
        assertEquals(-1, reader.read());
    }

    @Test
    public void readsLine() throws IOException {
        String str = "foo\nbar\rbaz\r\nA\n\nB";
        BufferedReader reader = new BufferedReader(new StringReader(str));
        assertEquals("foo", reader.readLine());
        assertEquals("bar", reader.readLine());
        assertEquals("baz", reader.readLine());
        assertEquals("A", reader.readLine());
        assertEquals("", reader.readLine());
        assertEquals("B", reader.readLine());
        assertNull(reader.readLine());
        assertNull(reader.readLine());
    }

    @Test
    public void fillsBuffer() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; ++i) {
            sb.append((char) i);
        }
        BufferedReader reader = new BufferedReader(new StringReader(sb.toString()), 101);
        char[] buffer = new char[500];
        assertEquals(500, reader.read(buffer));
        assertEquals(0, buffer[0]);
        assertEquals(1, buffer[1]);
        assertEquals(499, buffer[499]);
    }

    @Test
    public void leavesMark() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; ++i) {
            sb.append((char) i);
        }
        BufferedReader reader = new BufferedReader(new StringReader(sb.toString()), 100);
        reader.skip(50);
        reader.mark(70);
        reader.skip(60);
        reader.reset();
        char[] buffer = new char[150];
        int charsRead = reader.read(buffer);
        assertEquals(150, charsRead);
        assertEquals(50, buffer[0]);
        assertEquals(51, buffer[1]);
        assertEquals(199, buffer[149]);
    }

    @Test
    public void lines() {
        var reader = new BufferedReader(new StringReader("a\nb\n\n"));
        var lines = reader.lines().toArray(String[]::new);
        assertArrayEquals(new String[] { "a", "b", "" }, lines);

        reader = new BufferedReader(new StringReader("a\nb\n\n"));
        lines = reader.lines().filter(s -> !s.isEmpty()).map(s -> "*" + s).toArray(String[]::new);
        assertArrayEquals(new String[] { "*a", "*b" }, lines);
    }

    @Test
    public void nonGreedyFill() throws IOException {
        var in = new TestReader();
        var reader = new BufferedReader(in);
        var buffer = new char[10];

        assertEquals(5, reader.read(buffer));
        assertArrayEquals("ABCDE".toCharArray(), Arrays.copyOf(buffer, 5));
        assertEquals(5, reader.read(buffer));
        assertArrayEquals("FGHIJ".toCharArray(), Arrays.copyOf(buffer, 5));
        assertEquals(5, reader.read(buffer));
        assertArrayEquals("KLMNO".toCharArray(), Arrays.copyOf(buffer, 5));
        assertEquals(5, reader.read(buffer));
        assertArrayEquals("PABCD".toCharArray(), Arrays.copyOf(buffer, 5));
    }

    private static class TestReader extends Reader {
        int reads;
        private byte lastRead;

        @Override
        public int read() {
            reads++;
            return 'A' + (lastRead++ & 15);
        }

        @Override
        public int read(char[] b, int off, int len) {
            var readBytes = 0;
            while (len-- > 0) {
                reads++;
                readBytes++;
                b[off++] = (char) ('A' + (lastRead++ & 15));
                if (reads % 5 == 0) {
                    break;
                }
            }
            return readBytes;
        }

        @Override
        public void close() {
        }
    }
}
