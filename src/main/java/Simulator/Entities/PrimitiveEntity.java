package Simulator.Entities;

import Simulator.Graphics.Utils.Primitives;
import Simulator.Graphics.Utils.Vertex;
import Simulator.Window;
import Simulator.WindowInfo;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public abstract class PrimitiveEntity {

    protected Vector2f position;
    protected Vector2f scale;
    protected Matrix4f model;
    protected Vector3f color;
    protected int texture;

    protected Simulator.Graphics.Utils.Shader shader;

    protected int VAO;
    protected Simulator.Graphics.Buffers.VBO VBO;
    protected Simulator.Graphics.Buffers.EBO EBO;

    public PrimitiveEntity(Simulator.Graphics.Utils.Shader shader, Vector3f color){

        this.shader = shader;

        this.position  = new Vector2f(0.f, 0.f);
        this.scale     = new Vector2f(1.f, 1.f);
        this.color     = color;
        this.model     = new Matrix4f().identity();

        createBuffers();
    }

    public void render() {

        GL30.glBindVertexArray(VAO);
        VBO.bind();
        EBO.bind();

        GL11.glDrawElements(GL11.GL_TRIANGLES, Primitives.squareIndices.length, GL11.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);
    }

    protected void createBuffers(){

        VAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VAO);

        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer( Primitives.squareVertices.length * 5);

        for (Vertex vertex : Primitives.squareVertices) {
            verticesBuffer.put(vertex.position.x);
            verticesBuffer.put(vertex.position.y);
            verticesBuffer.put(this.color.x);
            verticesBuffer.put(this.color.y);
            verticesBuffer.put(this.color.z);
        }

        verticesBuffer.flip();

        VBO = new Simulator.Graphics.Buffers.VBO(verticesBuffer, GL30.GL_STATIC_DRAW);
        EBO = new Simulator.Graphics.Buffers.EBO(Primitives.squareIndices, GL30.GL_STATIC_DRAW);

        GL30.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 5 * Float.BYTES, 0);
        GL30.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 5 * Float.BYTES, 2 * Float.BYTES);

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        verticesBuffer.clear();
    }

    protected void update(WindowInfo windowInfo){}

    public Vector2f getPosition() {
        return position;
    }
}
