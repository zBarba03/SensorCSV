// Do not forget to dynamically load the C++ library into your application.
// For instance,
// in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("sensorcsv")
//      }
//    }
#include <jni.h>
#include <android/hardware_buffer_jni.h>
#include <android/sensor.h>
#include <android/log.h>
#include <atomic>
#include <thread>
#include <fstream>
#include <cstring>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "marco", __VA_ARGS__)

// Global state
static std::atomic<bool> running(false);
static std::thread recordingThread;
static size_t lastIndex = 0;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_sensorcsv_MainActivity_startNativeCsvRecording(
    JNIEnv* env,
    jobject,
    jobject hardwareBufferObj,
    jstring pathObj
){
    const char* path = env->GetStringUTFChars(pathObj, nullptr);

    AHardwareBuffer* buffer = AHardwareBuffer_fromHardwareBuffer(env, hardwareBufferObj);

    void* data = nullptr;
    if (AHardwareBuffer_lock(
        buffer,
        AHARDWAREBUFFER_USAGE_CPU_READ_OFTEN,
        -1,
        nullptr,
        &data) != 0
    ){
        LOGI("Failed to lock hardware buffer");
        env->ReleaseStringUTFChars(pathObj, path);
        return;
    }

    ASensorEvent* events = static_cast<ASensorEvent*>(data);

    // Query number of events the buffer can hold
    AHardwareBuffer_Desc desc{};
    AHardwareBuffer_describe(buffer, &desc);
    size_t eventCount = desc.width / sizeof(ASensorEvent);

    running = true;
    lastIndex = 0;

    recordingThread = std::thread([events, eventCount, path]() {
        std::ofstream file(path);
        file << "clock_ns,ax,ay,az\n";

        while (running) {
            // Simple ring-buffer reading
            size_t currentIndex = lastIndex;
            while (currentIndex < eventCount) {
                ASensorEvent& e = events[currentIndex];

                if (e.type == ASENSOR_TYPE_ACCELEROMETER) {
                    file << e.timestamp << ","
                         << e.acceleration.x << ","
                         << e.acceleration.y << ","
                         << e.acceleration.z << "\n";
                }

                currentIndex++;
            }

            lastIndex = 0;  // wrap-around
            file.flush();
            usleep(500);    // throttle loop
        }

        file.close();
    });

    env->ReleaseStringUTFChars(pathObj, path);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_sensorcsv_MainActivity_stopNativeCsvRecording(
    JNIEnv*,
    jobject
) {
    running = false;
    if (recordingThread.joinable())
        recordingThread.join();
}