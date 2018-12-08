//
// Created by sanke on 11/3/2018.
//

#include "MelSpectrogram.h"
#include <stdio.h>

MelSpectrogram* initMelSpectrogram(int nFilt, int numFrames, float freqLow, float freqHigh, int frameSize, int Fs, int nFFT){
    MelSpectrogram* melSpectrogram = (MelSpectrogram*)malloc(sizeof(MelSpectrogram));

    melSpectrogram->nFilt       = nFilt;
    melSpectrogram->nFFT        = nFFT;
    melSpectrogram->numFrames   = numFrames;

    melSpectrogram->filtBank = buildMelMatrix(freqLow, freqHigh, nFilt, nFFT, Fs);
    melSpectrogram->melPower = (float*)calloc(sizeof(float), nFilt);
    melSpectrogram->melSpectrogramImage = (float**)calloc(sizeof(float*), numFrames);
    for (size_t i = 0; i < numFrames; i++) {
        melSpectrogram->melSpectrogramImage[i] = (float*)calloc(sizeof(float), nFilt);
    }
    return melSpectrogram;
}

float** buildMelMatrix(float l, float h, int numMel, int fftSize, int fs)
{
    float nyquist_hertz = fs / 2.;
    float lowerMel  = 1127.0 * log(1 + l/700.0);
    float higherMel = 1127.0 * log(1 + h/700.0);
    int fft_bin = (int)(fftSize /2 + 1);

    float* band_edges_mel = (float*)calloc(sizeof(float*), numMel + 2);
    float* spectrogram_bins_mel = (float*)calloc(sizeof(float*),fft_bin );

    float freq_step = nyquist_hertz / (int)(fft_bin - 1);
    spectrogram_bins_mel[0] = 0;
    float lastFreq = 0;
    for( size_t i = 1 ; i< fft_bin ; i++)
    {
        lastFreq = lastFreq + freq_step;
        spectrogram_bins_mel[i] = 1127.0 * log(1 + lastFreq/700.0);
    }

    band_edges_mel[0] = lowerMel;
    float step = (higherMel - lowerMel)/(numMel + 1);
    for( size_t i = 1 ; i< numMel + 2 ; i++)
    {
        band_edges_mel[i] = band_edges_mel[i-1] + step;
    }

    int matrix_rows = fft_bin;
    int matrix_cols = numMel;

    float** melMarix = (float**)calloc(sizeof(float*), matrix_rows);
    for (size_t i = 0; i < matrix_rows; i++) {
        melMarix[i] = (float*)calloc(sizeof(float), matrix_cols);
    }

    for (size_t i = 0; i < numMel; i++) {

        float lower_edge_mel = band_edges_mel[i];
        float center_mel = band_edges_mel[i+1];
        float upper_edge_mel = band_edges_mel[i+2];

        for(size_t k = 0; k < matrix_rows ; k++)
        {
            float lower_slope = (spectrogram_bins_mel[k] - lower_edge_mel) / (center_mel - lower_edge_mel);
            float upper_slope = (upper_edge_mel - spectrogram_bins_mel[k]) / (upper_edge_mel - center_mel);

            melMarix[k][i] = fmaxf(0.0, fminf(lower_slope, upper_slope));
        }
        melMarix[0][i] = 0.0;
    }
    return melMarix;
}

void calculateMelDb(float* fft, int nFFT, float** filterbank, int nFilt, float* melP){
    float sum = 0;

    for (size_t i = 0; i < nFilt; i++) {
        for (size_t j = 0; j < floor(nFFT/2); j++) {
            sum += filterbank[j][i] * fft[j];
        }
        melP[i] = log(sum + 1e-8);
        sum = 0;
    }
}

void shiftAndAppendImage(float** melSpectrogramImage, float* melPower, int nFilt, int numFrames){

    for (size_t i = 0; i < numFrames-1; i++) {
        memcpy(melSpectrogramImage[i], melSpectrogramImage[i+1], nFilt*sizeof(float));
    }

    for (size_t j = 0; j < nFilt; j++) {
        melSpectrogramImage[numFrames-1][j] = melPower[j];
    }
}

void updateImage(MelSpectrogram* melSpectrogram, float* fft){

    calculateMelDb(fft, melSpectrogram->nFFT, melSpectrogram->filtBank, melSpectrogram->nFilt, melSpectrogram->melPower);
    shiftAndAppendImage(melSpectrogram->melSpectrogramImage, melSpectrogram->melPower, melSpectrogram->nFilt , melSpectrogram->numFrames);
}
