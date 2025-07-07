package Simulator;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private long ID;

    private int width;
    private int height;
    private float zoom;
    private float min_zoom = 1.f;
    private float max_zoom = 8.0f;
    private float scrollSpeed = 0.1f;
    private Map<Integer, Boolean> keyState;
    private Matrix4f projection;
    public Window(String title, int width, int height, Matrix4f projection){

        this.width  = width;
        this.height = height;

        glfwInit();
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        this.ID = glfwCreateWindow(width,height,title, NULL , NULL);
        this.zoom = 1.f;
        this.projection = projection;

        if (this.ID == NULL)
        {
            System.err.println("Error creating a window");
            System.exit(1);
        }

        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = glfwGetVideoMode(monitor);

        // Calcular a posição central
        int xPos = (vidMode.width() - width) / 2;
        int yPos = (vidMode.height() - height) / 2;

        // Definir a posição da janela
        glfwSetWindowPos(this.ID, xPos, yPos);

        glfwMakeContextCurrent(this.ID);
        GL.createCapabilities();
        glDisable(GL_DEPTH_TEST); // ? for 2d or 3D?
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Configurar o callback de entrada
        keyState = new HashMap<>();
        org.lwjgl.glfw.GLFW.glfwSetKeyCallback(ID, (window, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                keyState.put(key, true);
            } else if (action == GLFW.GLFW_RELEASE) {
                keyState.put(key, false);
            }
        });

        setupScrollCallback(ID);

    }

    public void setupScrollCallback(long window) {
        glfwSetScrollCallback(window, (windowHandle, xOffset, yOffset) -> {
            float sensitivity = 0.2f;
            float newZoom = this.zoom + (float) yOffset * sensitivity;
            setZoom(newZoom);
        });
    }

    public boolean isKeyPressed(int key) {
        return keyState.getOrDefault(key, false);
    }


    public void updateProjectionMatrix() {
        this.projection = new Matrix4f().ortho2D(
                0, width / zoom,
                0, height / zoom
        );
    }

    public void setZoom(float zoom) {
        this.zoom = Math.max(min_zoom, Math.min(zoom, max_zoom));
        updateProjectionMatrix();
    }

    public long getID() {
        return ID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getZoom() {
        return zoom;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public void setMaxZoom(float max_zoom){
        this.max_zoom = max_zoom;
    }

    public void setMin_zoom(float min_zoom) {
        this.min_zoom = min_zoom;
    }
}
