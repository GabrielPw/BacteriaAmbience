#version 330 core

out vec4 FragColor;

in vec2 fragPos;
in vec3 fragColor;

uniform float time;

void main()
{

    vec2 center = vec2(0.0, 0.0);

    // Calcula a distância do fragmento ao centro
    float dist = length(fragPos - center);

    // Raio base do círculo (ajuste para controlar o tamanho)
    float baseRadius = 0.45;

    // Ondulação: varia o raio com base no tempo e na direção do fragmento
    float angle = atan(fragPos.y, fragPos.x); // Ângulo do fragmento
    float ripple = 0.05 * sin(5.0 * angle + time * 0.2f); // Ondulação com 6 "ondas"
    float radius = baseRadius + ripple;

    // Transição suave para bordas arredondadas
    float edgeWidth = 0.05; // Largura da transição suave
    float alpha = smoothstep(radius, radius - edgeWidth, dist);

    // Cor final com transparência
    FragColor = vec4(fragColor, alpha);
};