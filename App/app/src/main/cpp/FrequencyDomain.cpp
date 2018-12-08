#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <SuperpoweredFrequencyDomain.h>
#include <AndroidIO/SuperpoweredAndroidAudioIO.h>
#include <SuperpoweredSimple.h>
#include <SuperpoweredCPU.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <algorithm>
#include <vector>
#include <sstream>
extern "C"{
#include "audioProcessing.h"
}


static float *inputBufferFloat, *left, *right, *data ;
Variables* memoryPointer;
SuperpoweredAndroidAudioIO *audioIO;
static int spectrogram_size , window_sz , block_sz, mel_band_sz;

// This is called periodically by the media server.
static bool audioProcessing(void * __unused clientdata, short int *audioInputOutput, int numberOfSamples, int __unused samplerate) {
    SuperpoweredShortIntToFloat(audioInputOutput, inputBufferFloat, (unsigned int)numberOfSamples); // Converting the 16-bit integer samples to 32-bit floating point.
    SuperpoweredDeInterleave(inputBufferFloat, left, right, (unsigned int)numberOfSamples);
    compute(memoryPointer, left);
    return true;
}

extern "C" JNIEXPORT void Java_com_awesome_app_awesomeapp_util_EventRecognitionService_FrequencyDomain(JNIEnv * __unused javaEnvironment, jobject __unused obj,
                                                                                             jint samplerate, jint buffersize,
                                                                                             jint block_dim, jint num_bands,
                                                                                             jint window_size  ) {

    spectrogram_size = block_dim * num_bands;
    window_sz = window_size;
    block_sz = block_dim;
    mel_band_sz = num_bands;
    inputBufferFloat = (float*)malloc(buffersize * sizeof(float) * 2 + 128);
    left             = (float*)malloc(buffersize * sizeof(float) + 64);
    right            = (float*)malloc(buffersize * sizeof(float) + 64);
    data             = (float*)malloc(sizeof(float)*spectrogram_size);

    memoryPointer = initialize(samplerate, buffersize, window_sz, 1024);

    SuperpoweredCPU::setSustainedPerformanceMode(true);
    audioIO = new SuperpoweredAndroidAudioIO(samplerate, buffersize, true, false, audioProcessing, NULL, -1, -1 , buffersize * 2);
}

extern "C" JNIEXPORT jfloatArray Java_com_awesome_app_awesomeapp_util_EventRecognitionService_GetSpectrogram(JNIEnv * __unused javaEnvironment, jobject __unused obj){
    jfloatArray result;
    result = javaEnvironment->NewFloatArray(spectrogram_size);

    for (int i = 0; i < block_sz; i++) {
        for (int j = 0; j < mel_band_sz; j++) {
            data[mel_band_sz * i + j] = memoryPointer->melSpectrogram->melSpectrogramImage[i][j];
        }
    }
    javaEnvironment->SetFloatArrayRegion(result, 0, spectrogram_size, data);
    return result;
}


extern "C" JNIEXPORT jfloat Java_com_awesome_app_awesomeapp_util_EventRecognitionService_GetSPL(JNIEnv * __unused javaEnvironment, jobject __unused obj){
    std::vector<float> spl_buffer(block_sz);
    float* spl = spl_buffer.data();

    for (int i = 0; i < block_sz; i++) {
        float* block_spl = spl + i;
        *block_spl = 0;
        for (int j = 0; j < mel_band_sz; j++) {
            *block_spl += exp(data[mel_band_sz * i + j]);
        }
        *block_spl = log(*block_spl);
    }

    const float spl_offset = -77.2757f;
    const float lse_ratio  = static_cast<float>(10.0 / log(10.0 / M_E));
    const float lse_offset = 17.f;

    return (*std::max_element(spl, spl + block_sz) + lse_offset) * lse_ratio + spl_offset;
}


extern "C" JNIEXPORT void Java_com_awesome_app_awesomeapp_util_EventRecognitionService_Cleanup(JNIEnv * __unused javaEnvironment, jobject __unused obj){

    if(inputBufferFloat != NULL){
        delete audioIO;
        free(inputBufferFloat);
        free(left);
        free(right);
        inputBufferFloat = NULL;
    }
}