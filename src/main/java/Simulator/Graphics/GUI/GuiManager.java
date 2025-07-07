package Simulator.Graphics.GUI;

import Simulator.Entities.BacteriaInfo;
import Simulator.Graphics.Utils.FontsPath;
import Simulator.Graphics.Utils.Shader;
import Simulator.Graphics.Utils.TextureLoader;
import Simulator.WindowInfo;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class GuiManager {

    float textScale;
    private int fontTexture;
    private Shader fontShader;
    private TextRenderer textRenderer;
    private BacteriaInfo bacteriaInfo;

    public GuiManager(){

        fontShader = new Shader("text.vert", "text.frag");
        fontTexture = TextureLoader.loadTexture(FontsPath.BITMAP_FRANKLIN_GOTHIC_MEDIUM);
        textRenderer = new TextRenderer(fontTexture, FontsPath.FNTINFO_FRANKLIN_GOTHIC_MEDIUM, fontShader);
    }

    public void run(WindowInfo windowInfo, BacteriaInfo bacteriaInfo){

        this.bacteriaInfo = bacteriaInfo;
        float halfXScreen =  windowInfo.window().getWidth() / 2.f;
        renderText("Bacteria Simulation", windowInfo, 50.f, windowInfo.window().getHeight() - 50, 128);
        renderText("| time " + bacteriaInfo.time() + " sec", windowInfo, halfXScreen, windowInfo.window().getHeight() - 50, 128);
        renderText("EPOCH " + bacteriaInfo.actualEpoch(), windowInfo, halfXScreen * 1.5f, windowInfo.window().getHeight() - 50, 128);
    }

    private void renderText(String text, WindowInfo windowInfo, float posX, float posY, float bitmapDimension){

        textRenderer.setTextColor(220.f / 255.f, 120.f / 255.f, 120.f / 255.f);
        textRenderer.setScale(10.f);
        textRenderer.setLetterSpacing(1.2f);
        textRenderer.renderText(text, new Vector2f(posX, posY), new Vector2f(bitmapDimension,bitmapDimension), windowInfo.projection(), new Matrix4f().identity());
    }
}
