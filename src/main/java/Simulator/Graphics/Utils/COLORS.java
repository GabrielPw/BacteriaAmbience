package Simulator.Graphics.Utils;

import org.joml.Vector3f;

public enum COLORS {
    RED(new Vector3f(1.0f, 0.0f, 0.0f)),          // Vermelho puro
    GREEN(new Vector3f(0.0f, 1.0f, 0.0f)),        // Verde puro
    BLUE(new Vector3f(0.0f, 0.0f, 1.0f)),         // Azul puro
    YELLOW(new Vector3f(1.0f, 1.0f, 0.0f)),       // Amarelo
    CYAN(new Vector3f(0.0f, 1.0f, 1.0f)),         // Ciano
    MAGENTA(new Vector3f(1.0f, 0.0f, 1.0f)),      // Magenta
    WHITE(new Vector3f(1.0f, 1.0f, 1.0f)),        // Branco
    BLACK(new Vector3f(0.0f, 0.0f, 0.0f)),        // Preto
    ORANGE(new Vector3f(1.0f, 0.5f, 0.0f)),       // Laranja
    PURPLE(new Vector3f(0.7f, 0.0f, 0.5f)),       // Roxo
    DARKPURPLE(new Vector3f(0.2f, 0.1f, 0.2f)),   // Roxo ESCURO
    BROWN(new Vector3f(0.6f, 0.3f, 0.1f)),        // Marrom (para alimentos, por exemplo)
    GRAY(new Vector3f(0.5f, 0.5f, 0.5f));         // Cinza

    private final Vector3f rgb;

    COLORS(Vector3f rgb) {
        this.rgb = rgb;
    }

    public Vector3f getRGB() {
        return new Vector3f(rgb); // Retorna uma cópia para evitar modificações
    }
}
