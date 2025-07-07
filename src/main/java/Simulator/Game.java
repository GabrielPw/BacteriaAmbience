package Simulator;

import Simulator.Entities.EntityManager;
import Simulator.Graphics.GUI.GuiManager;
import Simulator.Graphics.GUI.TextRenderer;
import Simulator.Graphics.Utils.Shader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class Game {

    private GuiManager guiManager;
    // Window stuff
    private Matrix4f view;
    private WindowInfo windowInfo;
    private Window window;
    private double previousTime;
    private double frameTimeAccumulator;
    private int frameCount;

    // Entity stuff
    private EntityManager entityManager;
    private Shader bacteriaShader;

    public Game(String windowTile, int winWidth, int winHeight){
        view = new Matrix4f().identity();
        Matrix4f projection = new Matrix4f().identity();

        window = new Window(windowTile, winWidth,winHeight, projection);
        previousTime = glfwGetTime();
        frameTimeAccumulator = 0.0; // Acumulador para o tempo decorrido
        frameCount = 0;

        window.setZoom(1.f);
        window.updateProjectionMatrix();

        guiManager     = new GuiManager();
        bacteriaShader = new Shader("quadrado.vert", "quadrado.frag");
        entityManager  = new EntityManager(bacteriaShader, window.getWidth(), window.getHeight());

        glfwSetFramebufferSizeCallback(window.getID(), (windowID, w, h) -> {
            glViewport(0, 0, w, h);
            window.setWidth(w);
            window.setHeight(h);
            window.updateProjectionMatrix();
        });

        GL30.glFrontFace( GL30.GL_CCW );
        GL30.glCullFace(GL30.GL_BACK);
        GL30.glEnable(GL30.GL_CULL_FACE);
    }

    void run(){

        glfwSwapInterval(1); // 1 para ativar o VSinc (60FPS/HZ)
        while (!glfwWindowShouldClose(window.getID())) {
            GL11.glClearColor((20.f / 255), (40.f / 255), (51.f / 255), 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - previousTime); // Calcular deltaTime
            previousTime = currentTime; // Atualizar previousTime

            frameTimeAccumulator += deltaTime; // Acumular o tempo decorrido
            frameCount++;

            if (frameTimeAccumulator >= 1.0) { // Se passou um segundo
                glfwSetWindowTitle(window.getID(), "OpenGL Simulator.Game. FPS[" + frameCount + "]");
                frameCount = 0; // Resetar contagem de frames
                frameTimeAccumulator = 0.0; // Resetar o acumulador
            }

            window.updateProjectionMatrix();

            view.identity();
            windowInfo = new WindowInfo(window, frameCount, view, window.getProjection(), deltaTime);
            entityManager.run(windowInfo);
            guiManager.run(windowInfo, entityManager.getBacteriaInfo());

            glfwPollEvents();
            glfwSwapBuffers(window.getID());
        }

        GL.createCapabilities();
        glfwDestroyWindow(window.getID());
        glfwTerminate();
    }
}
