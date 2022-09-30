package com.kotlindemo.retrofitclient;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

class ToStringConverter : Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<kotlin.Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        if (String::class.java == type) {
            return Converter<ResponseBody, String> { value -> value.string() }
        }
        return null
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<kotlin.Annotation>?, methodAnnotations: Array<kotlin.Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
        if (String::class.java == type) {
            return Converter<String, RequestBody> { value -> RequestBody.create(MediaType.parse("text/plain"), value) }
        }
        return null
    }
}
