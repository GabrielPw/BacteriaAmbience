package Simulator.Graphics.Buffers;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

public class EBO {

    private final int ID;
    public EBO(int[] verticesBuffer, int usage) {

        this.ID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ID);
        GL30.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, verticesBuffer, usage);
    }

    // construtor para caso inicie um VBO vazio.
    public EBO(long bufferSize, int usage) {

        this.ID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ID);
        GL30.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, bufferSize, usage);
    }

    public EBO(int usage) {
        this.ID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ID);
        GL30.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, usage);
    }

    public void bind(){

        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.ID);
    }


    // Atualiza todo o conteúdo do EBO
    public void updateData(IntBuffer verticesBuffer) {
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ID);
        GL30.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, verticesBuffer);
    }

    // Atualiza parte do conteúdo do EBO
    public void subData(int offset, IntBuffer verticesBuffer) {
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ID);
        GL30.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, offset, verticesBuffer);
    }

    public int getID() {
        return ID;
    }
}
