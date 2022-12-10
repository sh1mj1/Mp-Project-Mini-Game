//
// Created by 심지훈 on 2022/11/24.
//
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <jni.h>
#include <stdlib.h>
#include <sys/time.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <CL/opencl.h>
#include <assert.h>

#define CL_FILE "/data/local/tmp/blur.cl"

#define LOG_TAG "DEBUG"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define RGB8888_A(p) ((p & (0xff << 24))      >> 24 )
#define RGB8888_B(p) ((p & (0xff << 16)) >> 16 )
#define RGB8888_G(p) ((p & (0xff << 8))  >> 8 )
#define RGB8888_R(p) (p & (0xff) )

#define CHECK_CL(err) {\
    cl_int er = (err);\
    if(er<0 && er > -64){\
        LOGE("%d line, OpenCL Error:%d\n",__LINE__,er);\
    }\
}

int ledFd = 0;
int interruptFd = 0;

//led 에서 사용하는 JNIMEthod
JNIEXPORT jint JNICALL
Java_com_example_minigame_MemoryActivity_openLedDriver(JNIEnv *env, jclass clazz, jstring path) {
    jboolean iscopy;
    const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
    ledFd = open(path_utf, O_WRONLY);
    (*env)->ReleaseStringUTFChars(env, path, path_utf);

    if(ledFd<0) {return -1;}
    else {return 1;}


}

JNIEXPORT jint JNICALL
Java_com_example_minigame_MemoryActivity_closeLedDirver(JNIEnv *env, jclass clazz) {
    if (ledFd>0) {close(ledFd);}
}

JNIEXPORT void JNICALL
Java_com_example_minigame_MemoryActivity_writeLedDriver(JNIEnv *env, jclass clazz, jbyteArray data,
jint length) {
jbyte *chars = (*env)->GetByteArrayElements(env, data, 0);

if (ledFd > 0){
write(ledFd, (unsigned char *) chars, length);
}

(*env)->ReleaseByteArrayElements(env, data, chars, 0);
}

//Interrupt 에서 사용하는 JNIMEthod

JNIEXPORT jint JNICALL
Java_com_example_minigame_InterruptDriver_openDriver(JNIEnv *env, jclass clazz, jstring path) {
    jboolean iscopy;
    const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
    interruptFd = open(path_utf, O_RDONLY);
    (*env)->ReleaseStringUTFChars(env, path, path_utf);

    if (interruptFd < 0) {
        return -1;
    } else {
        return 1;
    }
}

JNIEXPORT void JNICALL
Java_com_example_minigame_InterruptDriver_closeDriver(JNIEnv *env, jclass clazz) {
if (interruptFd > 0) close(interruptFd);
}

JNIEXPORT jchar JNICALL
Java_com_example_minigame_InterruptDriver_readDriver(JNIEnv *env, jobject thiz) {
    char ch = 0;
    if(interruptFd>0){
        read(interruptFd, &ch, 1);
    }
    return ch;
}

JNIEXPORT jint JNICALL
Java_com_example_minigame_InterruptDriver_getInterrupt(JNIEnv *env, jobject thiz) {
    int ret = 0;
    char value[100];
    char *ch1 = "Up";
    char *ch2 = "Down";
    char *ch3 = "Left";
    char *ch4 = "Right";
    char *ch5 = "CENTER";
    ret = read(interruptFd, &value, 100);
    if(ret<0){
        return -1;
    }else{
        if (strcmp(ch1, value) == 0) {
            return 1;
        } else if (strcmp(ch2, value) == 0) {
            return 2;
        }else if (strcmp(ch3, value) == 0) {
            return 3;
        }else if (strcmp(ch4, value) == 0) {
            return 4;
        }else if (strcmp(ch5, value) == 0) {
            return 5;
        }
        return 0;
    }


}

JNIEXPORT jobject JNICALL
Java_com_example_minigame_RecordRankActivity_GaussianBlurBitmap(JNIEnv *env, jclass clazz,
                                                                jobject bitmap) {
    // TODO: implement GaussianBlurBitmap_1()
    //getting bitmap info:
    LOGD("reading bitmap info...");
    AndroidBitmapInfo info;
    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return NULL;
    }
    LOGD("width:%d height:%d stride:%d", info.width, info.height, info.stride);
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return NULL;
    }

    //read pixels of bitmap into native memory :
    LOGD("reading bitmap pixels...");
    void *bitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return NULL;
    }
    uint32_t *src = (uint32_t *) bitmapPixels;
    uint32_t *tempPixels = (uint32_t *) malloc(info.height * info.width * 4);
    int pixelsCount = info.height * info.width;
    memcpy(tempPixels, src, sizeof(uint32_t) * pixelsCount);

    // write image processing code here!!
    // input is tempPixels
    // output is bitmapPixels

    int a, r, g, b;

    //
    float red = 0, green = 0, blue = 0;
    int row = 0, col = 0, cnt = 0;
    int m, n, x, y;

    float mask[9][9] = {
            {0.011237, 0.011637, 0.011931, 0.012111, 0.012172, 0.012111, 0.011931, 0.011637, 0.011237},
            {0.011637, 0.012051, 0.012356, 0.012542, 0.012605, 0.012542, 0.012356, 0.012051, 0.011637},
            {0.011931, 0.012356, 0.012668, 0.012860, 0.012924, 0.012860, 0.012668, 0.012356, 0.011931},
            {0.012111, 0.012542, 0.012860, 0.013054, 0.013119, 0.013054, 0.012860, 0.012542, 0.012111},
            {0.012172, 0.012605, 0.012924, 0.013119, 0.013185, 0.013119, 0.012924, 0.012605, 0.012172},
            {0.012111, 0.012542, 0.012860, 0.013054, 0.013119, 0.013054, 0.012860, 0.012542, 0.012111},
            {0.011931, 0.012356, 0.012668, 0.012860, 0.012924, 0.012860, 0.012668, 0.012356, 0.011931},
            {0.011637, 0.012051, 0.012356, 0.012542, 0.012605, 0.012542, 0.012356, 0.012051, 0.011637},
            {0.011237, 0.011637, 0.011931, 0.012111, 0.012172, 0.012111, 0.011931, 0.011637, 0.011237},
    };

    for (col = 0; col < info.width; col++) {
        for (row = 0; row < info.height; row++) {
            blue = 0;
            green = 0;
            red = 0;

            for (m = 0; m < 9; m++) {
                for (n = 0; n < 9; n++) {
                    y = (row + m - 4);
                    x = (col + n - 4);
                    if ((row + m - 4) < 0 || y >= info.height || (col + n - 4) < 0 ||
                        x >= info.width)
                        continue;
                    uint32_t pixel = tempPixels[info.width * y + x];
                    //
                    a = RGB8888_A(pixel);
                    r = RGB8888_R(pixel);
                    g = RGB8888_G(pixel);
                    b = RGB8888_B(pixel);
                    red += r * mask[m][n];
                    green += g * mask[m][n];
                    blue += b * mask[m][n];
                    //LOGD("r : %f   g : %f   b : %f", red, green, blue);
                }
            }
            r = (int) red;
            g = (int) green;
            b = (int) blue;
            uint32_t v = (a << 24) + (b << 16) + (g << 8) + (r);
            src[info.width * row + col] = v;
            cnt++;
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
    //
    // free the native memory used to store the pixels
    //
    free(tempPixels);
    return bitmap;
}


JNIEXPORT jobject JNICALL
Java_com_example_minigame_RecordRankActivity_gaussianBlurBitmap_12(JNIEnv *env, jclass clazz,
                                                                   jobject bitmap) {
    // TODO: implement GaussianBlurBitmap_2()
}

JNIEXPORT jobject JNICALL
Java_com_example_minigame_RecordRankActivity_gaussianBlurBitmap_13(JNIEnv *env, jclass clazz,
                                                                   jobject bitmap) {
    // TODO: implement GaussianBlurBitmap_3()
}


JNIEXPORT jobject JNICALL
Java_com_example_minigame_RecordRankActivity_grayScaleBitmap_11(JNIEnv *env, jclass clazz,
                                                                jobject bitmap) {
    // TODO: implement grayScaleBitmap_1()
}

JNIEXPORT jobject JNICALL
Java_com_example_minigame_RecordRankActivity_grayScaleBitmap_12(JNIEnv *env, jclass clazz,
                                                                jobject bitmap) {
    // TODO: implement grayScaleBitmap_2()
}

JNIEXPORT jobject JNICALL
Java_com_example_minigame_RecordRankActivity_grayScaleBitmap_13(JNIEnv *env, jclass clazz,
                                                                jobject bitmap) {
    // TODO: implement grayScaleBitmap_3()
}





