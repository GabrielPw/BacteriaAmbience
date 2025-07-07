package Simulator;

import org.joml.Matrix4f;

public record WindowInfo(Window window, float frameCount, Matrix4f view, Matrix4f projection, float deltaTime) {}
