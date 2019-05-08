package com.akashapps.a3dobjectdecoder.Utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

public class IndexBuffer {
    private final int bufferId;
    private final int BYTES_PER_SHORT = 2;

    public IndexBuffer(short[] indexData) {
        // Allocate a buffer.
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);

        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new index buffer object.");
        }

        bufferId = buffers[0];
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);
        ShortBuffer indexArray = ByteBuffer
                .allocateDirect(indexData.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indexData);
        indexArray.position(0);

        // Transfer data from native memory to the GPU buffer.
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexArray.capacity() * BYTES_PER_SHORT,
                indexArray, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public int getBufferId() {
        return bufferId;
    }
}
