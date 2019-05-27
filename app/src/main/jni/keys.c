#include <jni.h>

JNIEXPORT jstring JNICALL
Java_dam_com_wenave_managements_MapBoxManagement_getMapboxUrl(JNIEnv *env, jclass type) {

 return (*env)->  NewStringUTF(env, "mapbox_api_key");
}

JNIEXPORT jstring JNICALL
Java_dam_com_wenave_services_callbacks_mapbox_DurationGetterAsync_getMapboxUrl(JNIEnv *env, jclass type) {

    return (*env)->NewStringUTF(env, "mapbox_api_key");
}

JNIEXPORT jstring JNICALL
Java_dam_com_wenave_views_fragments_MainMapFragment_getMapboxUrl(JNIEnv *env, jclass type)
{
    return (*env)->NewStringUTF(env, "mapbox_api_key");
}

JNIEXPORT jstring JNICALL
Java_dam_com_wenave_views_fragments_RouteInfoFragment_getMapboxUrl(JNIEnv *env, jclass type)
{
    return (*env)->NewStringUTF(env, "mapbox_api_key");
}

JNIEXPORT jstring JNICALL
Java_dam_com_wenave_services_DarkSkyService_getDarkskyUrl(JNIEnv *env, jclass type)
{
    return (*env)->NewStringUTF(env, "https://api.darksky.net/forecast/darksky_api_key/lat,lon,mil?exclude=[hourly,daily]&units=auto&lang=");
}


