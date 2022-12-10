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
//#include <CL/opencl.h>
#include <assert.h>

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




