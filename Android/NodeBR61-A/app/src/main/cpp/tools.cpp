//
// Created by Irineu Antunes on 19/02/23.
//

#include "tools.h"
#include <android/log.h>
#include <sys/stat.h>

static const char * TAG = "NATIVE ROOT DETECTOR";

extern "C" jbyteArray Java_com_irineu_nodebr61_CryptoUtils_getSalt(JNIEnv * env, jobject) {
    jbyte* b = new jbyte[8];
    b[0] = 0xFF;
    b[1] = 0xEE;
    b[2] = 0xAD;
    b[3] = 0x66;
    b[4] = 0xE0;
    b[5] = 0xF9;
    b[6] = 0x62;
    b[7] = 0xAE;

    jbyteArray result = env->NewByteArray(8);
        env->SetByteArrayRegion(result, 0, 8, b);
    return result;
}

extern "C" jbyteArray Java_com_irineu_nodebr61_CryptoUtils_getIV(JNIEnv * env, jobject, jbyteArray key) {

    jboolean isCopy = false;
    jbyte* keyBytes = env->GetByteArrayElements(key, &isCopy);

    jbyte* b = new jbyte[16];

    for(int i=0, j=15; i < 16; i++, j--){
        b[i] = ~keyBytes[j];
    }

    jbyteArray result = env->NewByteArray(16);
    env->SetByteArrayRegion(result, 0, 16, b);
    return result;
}

extern "C" jboolean Java_com_irineu_nodebr61_MainActivity_nativeCheckRoot(JNIEnv * env,
                                                                          jobject thiz) {
    char * paths[] = {
            "/system/app/Superuser.apk",
            "/system/etc/init.d/99SuperSUDaemon",
            "/dev/com.koushikdutta.superuser.daemon/",
            "/system/xbin/daemonsu",
            "/sbin/su",
            "/system/bin/su",
            "/system/bin/failsafe/su",
            "/system/xbin/su",
            "/system/xbin/busybox",
            "/system/sd/xbin/su",
            "/data/local/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
    };

    for(char * path : paths){
        struct stat fileAttr;
        int errno = stat(path, &fileAttr);
        if (errno != 0) {
            __android_log_print(ANDROID_LOG_DEBUG, TAG, "NATIVE: file found: [%s], root detected!", strerror(errno));
        } else {
            __android_log_print(ANDROID_LOG_DEBUG, TAG, "NATIVE: file not found [%d]", fileAttr.st_mode);
            return 1;
        }
    }

    return 0;
}