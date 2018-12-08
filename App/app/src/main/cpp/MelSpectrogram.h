//
// Created by sanke on 11/3/2018.
//

#ifndef APP_MELSPECTROGRAM_H
#define APP_MELSPECTROGRAM_H

#define _USE_MATH_DEFINES

#include <stdlib.h>
#include <math.h>
#include <string.h>

typedef struct MelSpectrogram {
    int nFilt;
    int numFrames;
    int nFFT;
    float** filtBank;
    float* melPower;
    float** melSpectrogramImage;
} MelSpectrogram;

MelSpectrogram* initMelSpectrogram(int nFilt, int numFrames, float freqLow, float freqHigh, int frameSize, int Fs, int nFFT);
float** buildMelMatrix(float l, float h, int numMel, int fftSize, int fs);
float** buildFilterbank(float l, float h, int nFilt, int nFFT, int Fs);
void melCalculate(float* fft, int nFFT, float** filterbank, int nFilt, float* melP);
void melImageCreate(float** melSpectrogramImage, float* melPower, int nFilt, int numFrames);
void updateImage(MelSpectrogram* melSpectrogram, float* fft);


#endif //APP_MELSPECTROGRAM_H
