//
// Created by Irineu Antunes on 19/02/23.
//

#ifndef NODEBR61_A_TOOLS_H
#define NODEBR61_A_TOOLS_H

#include <string.h>
#include <jni.h>
#include <android/log.h>

extern "C" JNIEXPORT jbyteArray Java_com_irineu_nodebr61_CryptoUtils_getSalt(JNIEnv * env, jobject);
extern "C" JNIEXPORT jbyteArray Java_com_irineu_nodebr61_CryptoUtils_getIV(JNIEnv * env, jobject, jbyteArray key);
extern "C" JNIEXPORT jboolean JNICALL Java_com_irineu_nodebr61_MainActivity_nativeCheckRoot(JNIEnv * env, jobject thiz);

#endif //NODEBR61_A_TOOLS_H
