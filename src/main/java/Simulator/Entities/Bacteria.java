package Simulator.Entities;

import Simulator.Graphics.Utils.COLORS;
import Simulator.Graphics.Utils.Shader;
import Simulator.Window;
import Simulator.WindowInfo;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class Bacteria extends PrimitiveEntity{

    WindowInfo windowInfo;
    private PrimitiveEntity dotFace;
    Vector2f direction     = new Vector2f(0.f, 0.f);
    Vector3f rotationAxis  = new Vector3f(0.f, 0.f, 1.f);

    float         secondsToChangeDirection;
    final float   MIN_SECONDS_CHANGE_DIRECTION = 2.f;
    final float   MAX_SECONDS_CHANGE_DIRECTION = 4.f;
    private float targetAngle               = 0f; // Ângulo alvo para interpolação
    private float transitionProgress        = 1f; // Progresso da transição (0 a 1)
    private float transitionDuration        = 0.5f;
    float         timeElapsed               = 0;
    float         speed                     = 60.f;
    float         rotationAngle             = 0;

    public Bacteria(Shader shader, Vector2f scale, Vector3f color) {
        super(shader, color);
        this.scale = scale;

        this.dotFace = new PrimitiveEntity(shader, COLORS.DARKPURPLE.getRGB()) {
            @Override
            protected void createBuffers() {
                VAO = GL30.glGenVertexArrays();
                GL30.glBindVertexArray(VAO);

                // Vértices para um triângulo (representando a bolinha)
                float[] dotVertices = {
                        -0.5f, -0.5f, color.x, color.y, color.z,  // Base esquerda
                         0.5f, -0.5f, color.x, color.y, color.z,  // Base direita
                         0.0f,  0.5f, color.x, color.y, color.z   // Ponta do triângulo
                };
                int[] dotIndices = {0, 1, 2};

                FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(dotVertices.length);
                verticesBuffer.put(dotVertices).flip();

                VBO = new Simulator.Graphics.Buffers.VBO(verticesBuffer, GL30.GL_STATIC_DRAW);
                EBO = new Simulator.Graphics.Buffers.EBO(dotIndices, GL30.GL_STATIC_DRAW);

                GL30.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 5 * Float.BYTES, 0);
                GL30.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 5 * Float.BYTES, 2 * Float.BYTES);

                GL30.glEnableVertexAttribArray(0);
                GL30.glEnableVertexAttribArray(1);

                verticesBuffer.clear();
            }
        };
        this.dotFace.scale = new Vector2f(scale.x / 3, scale.y / 3);
    }

    protected void update(WindowInfo windowInfo) {

        this.windowInfo = windowInfo;

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture);

        shader.use();

        moveAndRotate(windowInfo.deltaTime());
        model.identity();
        model.translate(new Vector3f(this.position, 0.f));
        float rotationAngleToRadians = (float) (rotationAngle * (Math.PI / 180.f));
        this.model.rotate(rotationAngleToRadians, rotationAxis, model);
        model.scale(this.scale.x, this.scale.y, 1.f);

        shader.addUniform1f("time", windowInfo.frameCount());
        shader.addUniformMatrix4fv("projection", windowInfo.projection());
        shader.addUniformMatrix4fv("view", windowInfo.view());
        shader.addUniformMatrix4fv("model", this.model);

        render();

        dotFace.model.identity();
        dotFace.model.translate(new Vector3f(this.position, 0.f));
        dotFace.model.rotate(rotationAngleToRadians, rotationAxis, dotFace.model);
        dotFace.model.translate(new Vector3f(0.f, scale.y * 0.26f, 0.0f)); // Posicionar na borda frontal
        dotFace.model.scale(this.dotFace.scale.x, this.dotFace.scale.y, 1.f);

        shader.addUniformMatrix4fv("model", dotFace.model);
        dotFace.render();
    }

    private void moveAndRotate(float deltaTime){

        timeElapsed += deltaTime;

        // Verifica se é hora de iniciar uma nova mudança de direção
        if (timeElapsed >= secondsToChangeDirection && transitionProgress >= 1f) {
            secondsToChangeDirection = MIN_SECONDS_CHANGE_DIRECTION + (float)(Math.random() * (MAX_SECONDS_CHANGE_DIRECTION - MIN_SECONDS_CHANGE_DIRECTION));
            timeElapsed = 0f;

            // Define um novo ângulo alvo (intervalo reduzido para evitar mudanças drásticas)
            targetAngle = rotationAngle + (-90f + (float)(Math.random() * (90f - (-90f))));
            transitionProgress = 0f; // Inicia a transição
        }

        // Atualiza o progresso da transição
        if (transitionProgress < 1f) {
            transitionProgress += deltaTime / transitionDuration;
            if (transitionProgress > 1f) {
                transitionProgress = 1f; // Limita o progresso a 1
            }

            // Interpola suavemente entre o ângulo atual e o alvo
            rotationAngle = lerp(rotationAngle, targetAngle, transitionProgress);
        }

        // Converte o ângulo para radianos
        float rotationAngleToRadians = (float) (rotationAngle * (Math.PI / 180f));

        // Atualiza a direção
        direction.set(
                (float) Math.cos(rotationAngleToRadians + Math.PI / 2),
                (float) Math.sin(rotationAngleToRadians + Math.PI / 2)
        );

        // Normaliza a direção e aplica a velocidade
        direction.normalize();
        Vector2f movement = new Vector2f(direction).mul(speed * deltaTime);
        this.position.add(movement);
    }

    private float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }
}
