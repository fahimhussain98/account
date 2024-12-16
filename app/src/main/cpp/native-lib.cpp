#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_wts_com_accountpe_retrofit_RetrofitClient_getAPIHost(
        JNIEnv* env,
        jobject /* this */) {
    std::string apiKey = "http://api.accountpe.in/Vr1.0/198002/kkTTV81554KbGGKG1542TRT/BIGYT5021723TlXssGrsVB19TT7/api/";
    return env->NewStringUTF(apiKey.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_wts_com_accountpe_retrofit_RetrofitClient_getAPI(
        JNIEnv* env,
        jobject /* this */) {
    std::string apiKey = "https://api.accountpe.in/api/";
    return env->NewStringUTF(apiKey.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_wts_com_accountpe_retrofit_RetrofitClient_getAuthKey(
        JNIEnv* env,
        jobject /* this */) {
    std::string apiKey = "Basic d2VidGVjaCMkJV5zb2x1dGlvbiQkJiZAQCZeJmp1bHkyazIxOmJhc2ljJSUjI0AmJmF1dGgmIyYjJiMmQEAjJnBhc1d0UzIwMjE=";
    return env->NewStringUTF(apiKey.c_str());
}
