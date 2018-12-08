//
// Created by sanke on 11/3/2018.
//

#ifndef APP_TRANSFORMS_H
#define APP_TRANSFORMS_H

#include <stdlib.h>
#include <math.h>
#include <string.h>

typedef struct Transform {
    int points;
    int windowSize;
    float* real;
    float* imaginary;
    float* power;
    float* sine;
    float* cosine;
    float* window;
    float totalPower;
} Transform;

Transform* newTransform(int windowSize, int framesPerSecond);
void ForwardFFT(Transform* fft, float* real);
void destroyTransform(Transform** transform);


#endif //APP_TRANSFORMS_H
