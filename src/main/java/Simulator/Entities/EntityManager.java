package Simulator.Entities;

import Simulator.Graphics.GUI.GuiManager;
import Simulator.Graphics.Utils.COLORS;
import Simulator.Graphics.Utils.Shader;
import Simulator.WindowInfo;
import org.joml.Vector2f;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EntityManager {

    float time = 0;
    BacteriaInfo bacteriaInfo;
    Vector2f bacteriaScale = new Vector2f(42.f);
    final int NUM_BACTERIAS_PER_GENERATION = 4;

    int windowW, windowH;
    WindowInfo windowInfo;
    Shader bacteriaShader;

    List<PrimitiveEntity> entityList = new ArrayList<>();

    public EntityManager(Shader bacteriaShader, int windowW, int windowH){

        this.bacteriaShader = bacteriaShader;
        this.windowW = windowW;
        this.windowH = windowH;

        generateBacteria();
        bacteriaInfo = new BacteriaInfo(0, 0, 10, 3);
    }

    public void run(WindowInfo windowInfo){

        time+=windowInfo.deltaTime();

        this.bacteriaInfo = new BacteriaInfo((int) time,  0, 10, 3);
        this.windowInfo = windowInfo;
        windowW = windowInfo.window().getWidth();
        windowH = windowInfo.window().getHeight();
        entityList.forEach(entity -> {
            entity.update(windowInfo);
        });

        // update bacteriaInfo
    }


    public void generateBacteria(){

        for (int numBacteria = 1; numBacteria < NUM_BACTERIAS_PER_GENERATION; numBacteria++) {

            int randomX = ThreadLocalRandom.current().nextInt(0, windowW + 1);
            int randomY = ThreadLocalRandom.current().nextInt(0, windowH + 1);

            Bacteria bacteria = new Bacteria(bacteriaShader, bacteriaScale, COLORS.PURPLE.getRGB());
            bacteria.position = new Vector2f(randomX, randomY);
            entityList.add(bacteria);
        }
    }

    public BacteriaInfo getBacteriaInfo() {
        return bacteriaInfo;
    }
}
