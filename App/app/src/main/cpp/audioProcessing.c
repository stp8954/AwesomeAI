//
// Created by sanke on 11/3/2018.
//


#include "audioProcessing.h"
#define NUMFRAMES           80
#define NFILT               64
#define FREQLOW             125
#define FREQHIGH            8000

Variables* initialize(int frequency, int stepsize, int windowSize, int fftSize) {

    Variables* inParam = (Variables*) malloc(sizeof(Variables));

    inParam->stepSize = stepsize;
    inParam->windowSize = windowSize;

    inParam->inputBuffer = (float*)calloc(windowSize, sizeof(float));

    inParam->fft = newTransform(windowSize, fftSize);
    inParam->melSpectrogram = initMelSpectrogram(NFILT, NUMFRAMES, FREQLOW, FREQHIGH, windowSize, frequency, inParam->fft->points);

    return inParam;
}

void compute(Variables* memoryPointer, float* input) {
    Variables* inParam = memoryPointer;

    int i, j;

    for (i = 0; i < (inParam->windowSize - inParam->stepSize); i++) {
        inParam->inputBuffer[i] = inParam->inputBuffer[i+inParam->stepSize];
    }
    for (i = (inParam->windowSize - inParam->stepSize) , j = 0; i < inParam->windowSize ; i++, j++ )
    {
        inParam->inputBuffer[i] = input[j];
    }

    ForwardFFT(inParam->fft, inParam->inputBuffer);
    updateImage(inParam->melSpectrogram, inParam->fft->power);
}

void getMelImage(Variables* memoryPointer, float** melImage){
    Variables* inParam = memoryPointer;
    for (size_t i = 0; i < NFILT; i++) {
        for (size_t j = 0; j < NFILT; j++) {
            melImage[i][j] = inParam->melSpectrogram->melSpectrogramImage[i][j];
        }
    }
}
