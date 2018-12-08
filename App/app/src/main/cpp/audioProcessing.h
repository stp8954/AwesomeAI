//
// Created by sanke on 11/3/2018.
//

#ifndef APP_AUDIOPROCESSING_H
#define APP_AUDIOPROCESSING_H

#include <stdio.h>
#include "Transforms.h"
#include "MelSpectrogram.h"

typedef struct Variables {

    Transform* fft;
    MelSpectrogram* melSpectrogram;
    float* inputBuffer;
    float* frame;
    int stepSize;
    int windowSize;

} Variables;

Variables* initialize(int frequency, int stepsize, int windowSize, int fftSize);
void compute(Variables* memoryPointer, float* input);
void getMelImage(Variables* memoryPointer, float** melImage);

#endif //APP_AUDIOPROCESSING_H
