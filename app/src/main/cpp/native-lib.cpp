#include <jni.h>
#include <string>
#include <android/log.h>

void rc4_init(unsigned char *s, unsigned char *key, unsigned long len){
    int i =0, j=0;
    char k[256] = {0};
    unsigned char tmp = 0;
    for (i = 0; i < 256; ++i) {
        s[i] = i;   // Initialization vectors
        k[i] = key[i % len];    // Initialization a temporary vetor according to the key
    }
    for (i = 0; i < 256; ++i) {     // Scramble the state vector according to the key
        j = (j + s[i] + k[i]) % 256;
        tmp = s[i];
        s[i] = s[j];
        s[j] = tmp;
    }
}
void rc4_crypt(unsigned char *s, unsigned char *Data, unsigned long Len){
    int i = 0, j = 0, t = 0;
    unsigned long k = 0;
    unsigned char tmp;
    for (k = 0; k < Len; ++k) {
        i = (i + 1) % 256;
        j = (j + s[i]) % 256;
        tmp = s[i];
        s[i] = s[j];
        s[j] = tmp;     // Obtain a new state vector according to the key
        t = (s[i] + s[j]) % 256;
        Data[k] ^= s[t];
    }
}
void RC4Encrypt(const char *key, unsigned char *content){
    // void rc4_init(unsigned char *s, unsigned char *key, unsigned long len)
    unsigned long Len = strlen(key);
    unsigned char s[256] = {0};
    rc4_init(s, (unsigned char *)key, Len);
    rc4_crypt(s, content, strlen((char*)content));

}
extern "C" JNIEXPORT jstring JNICALL Java_com_xiaoeryu_rc4_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    const char* key = "rc4test";
    const char* content = "hello world";
    __android_log_print(4, "rc4", "content:%s", content);

    unsigned char array[100] = {0};

    strcpy(reinterpret_cast<char *>(array), content);
    RC4Encrypt(key, array);
    __android_log_print(4, "rc4", "encrypt:%s", array);

    RC4Encrypt(key, array);
    __android_log_print(4, "rc4", "decrypt:%s", array);

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}