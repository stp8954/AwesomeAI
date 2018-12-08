cmake_minimum_required(VERSION 3.4.1)

# convert SDK path to forward slashes on Windows
file(TO_CMAKE_PATH ${PATH_TO_SUPERPOWERED} PATH_TO_SUPERPOWERED)

include_directories(src/main/cpp)
include_directories(${PATH_TO_SUPERPOWERED})

file(GLOB CPP_FILES "src/main/cpp/*.cpp")
file(GLOB C_FILES "src/main/cpp/*.c")
#src/main/cpp/FrequencyDomain.cpp
# compile frequency domain example to a native library
add_library(
	audioProcessingLib
	SHARED
        ${CPP_FILES}
		${C_FILES}
	${PATH_TO_SUPERPOWERED}/AndroidIO/SuperpoweredAndroidAudioIO.cpp
)

# link the native library against the following libraries
target_link_libraries(
		audioProcessingLib
    log
    android
    OpenSLES
    ${PATH_TO_SUPERPOWERED}/libSuperpoweredAndroid${ANDROID_ABI}.a
)