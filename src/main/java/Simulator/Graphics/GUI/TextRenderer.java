package Simulator.Graphics.GUI;

import Simulator.Graphics.Utils.Primitives;
import Simulator.Graphics.Utils.Vertex;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class TextRenderer {

    private Map<Character, GlyphData> glyphMap = new HashMap<>();
    private Matrix4f model;
    private Vector2f position;
    private Vector2f scale;
    private int VAO;
    private Simulator.Graphics.Buffers.VBO VBO;
    private Simulator.Graphics.Buffers.EBO EBO;
    private int bitmapTexture;
    private Simulator.Graphics.Utils.Shader shader;
    private final int MAX_CHARACTERS_QUANTITY = 100;
    private Vector3f textColor;
    private int textSize;
    private float letterSpacing = 0.1f; // Novo: espaçamento entre letras (ajustável)

    public TextRenderer(int bitmapTexture, String fntInfoFilePath, Simulator.Graphics.Utils.Shader shader) {
        this.model = new Matrix4f().identity();
        this.position = new Vector2f(400, 400.f);
        this.scale = new Vector2f(12.f, 12.f);
        this.bitmapTexture = bitmapTexture;
        this.textColor = new Vector3f(1.f, 1.f, 1.f); // white default
        this.shader = shader;

        loadGlyphInfoFromBMFont(fntInfoFilePath);

        // buffer stuff
        VAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VAO);

        this.VBO = new Simulator.Graphics.Buffers.VBO((long) (Primitives.squareVertices.length * 4 * MAX_CHARACTERS_QUANTITY) * Float.BYTES, GL30.GL_DYNAMIC_DRAW);
        this.EBO = new Simulator.Graphics.Buffers.EBO((long) (Primitives.squareIndices.length * MAX_CHARACTERS_QUANTITY) * Integer.BYTES, GL30.GL_DYNAMIC_DRAW);

        GL30.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
        GL30.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES); // UV

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
    }

    public void renderText(String text, Vector2f position, Vector2f bitmapDimensions, Matrix4f projection, Matrix4f view) {
        this.position = position;
        int numOfGliphs = text.length();

        if (numOfGliphs > MAX_CHARACTERS_QUANTITY) {
            throw new RuntimeException("Texto a ser renderizado excede a quantidade máxima de caracteres permitidos (" + MAX_CHARACTERS_QUANTITY + ")");
        }

        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(Primitives.squareVertices.length * 4 * numOfGliphs);
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(Primitives.squareIndices.length * numOfGliphs);

        float currentXOffset = 0; // Novo: rastreia o deslocamento horizontal acumulado
        int letterIndex = 0;

        for (char letter : text.toCharArray()) {
            GlyphData glyph = glyphMap.get(letter);
            if (glyph == null) {
                System.err.println("Caractere não encontrado: '" + letter + "' (código: " + (int) letter + ")");
                letterIndex++;
                continue; // Pula caracteres não mapeados
            }

            float paddingXForLetterIAndL =
                    (letter == 'i' || letter == 'l' || letter == 'L' || letter == 'I' || letter == '.')
                            ? 1.f / bitmapDimensions.x : 0;

            float u1 = glyph.x / (float) bitmapDimensions.x - paddingXForLetterIAndL;
            float v1 = glyph.y / (float) bitmapDimensions.y;
            float u2 = (glyph.x + glyph.width) / (float) bitmapDimensions.x + paddingXForLetterIAndL;
            float v2 = (glyph.y + glyph.height) / (float) bitmapDimensions.y;

            for (Vertex squareVertex : Primitives.squareVertices) {
                float mappedU = squareVertex.textureCoord.x == 0.0f ? u1 : u2;
                float mappedV = squareVertex.textureCoord.y == 0.0f ? v1 : v2;

                // Adiciona currentXOffset à posição x do vértice
                verticesBuffer.put(squareVertex.position.x + currentXOffset);
                verticesBuffer.put(squareVertex.position.y);
                verticesBuffer.put(mappedU);
                verticesBuffer.put(mappedV);
            }

            int baseIndex = letterIndex * 4;
            for (int i = 0; i < Primitives.squareIndices.length; i++) {
                indicesBuffer.put(Primitives.squareIndices[i] + baseIndex);
            }

            // Incrementa o deslocamento com base no xadvance do glifo e no espaçamento adicional
            currentXOffset += (glyph.xadvance / bitmapDimensions.x) + letterSpacing;
            letterIndex++;
        }

        verticesBuffer.flip();
        indicesBuffer.flip();

        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, this.bitmapTexture);

        VBO.updateData(verticesBuffer);
        EBO.updateData(indicesBuffer);

        model.identity();
        model.translate(new Vector3f(this.position, 0.f));
        model.scale(this.scale.x, this.scale.y, 1.f);

        System.out.println("TextColor: " + textColor);
        shader.use();
        shader.addUniformMatrix4fv("projection", projection);
        shader.addUniformMatrix4fv("view", view);
        shader.addUniformMatrix4fv("model", this.model);
        shader.addUniform3fv("textColor", this.textColor);
        shader.addUniform1f("textureBitmap", this.bitmapTexture);

        GL30.glBindVertexArray(VAO);
        VBO.bind();
        EBO.bind();

        GL11.glDrawElements(GL11.GL_TRIANGLES, numOfGliphs * Primitives.squareIndices.length, GL11.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);
    }

    private void loadGlyphInfoFromBMFont(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.lines().filter(line -> line.startsWith("char")).forEach(individualLine -> {
                String[] parts = individualLine.split(" ");
                if (parts.length >= 9) {
                    int id = Integer.parseInt(parts[1].split("=")[1]);
                    int x = Integer.parseInt(parts[2].split("=")[1]);
                    int y = Integer.parseInt(parts[3].split("=")[1]);
                    int width = Integer.parseInt(parts[4].split("=")[1]);
                    int height = Integer.parseInt(parts[5].split("=")[1]);
                    int xoffset = Integer.parseInt(parts[6].split("=")[1]);
                    int yoffset = Integer.parseInt(parts[7].split("=")[1]);
                    int xadvance = Integer.parseInt(parts[8].split("=")[1]);

                    if (id >= 32 && id <= 126) {
                        GlyphData glyphData = new GlyphData(x, y, width, height, xoffset, yoffset, xadvance);
                        glyphMap.put((char) id, glyphData);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Simulator.Graphics.Utils.Shader getShader() {
        return shader;
    }

    public void setTextColor(Vector3f textColor) {
        this.textColor = textColor;
    }

    public void setTextColor(float r, float g, float b) {
        this.textColor = new Vector3f(r, g, b);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    // Novo: método para ajustar o espaçamento entre letras
    public void setLetterSpacing(float spacing) {
        this.letterSpacing = spacing;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

    public void setScale(float scale) {
        this.scale = new Vector2f(scale);
    }
}
