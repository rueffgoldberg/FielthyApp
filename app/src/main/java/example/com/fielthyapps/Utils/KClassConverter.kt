package example.com.fielthyapps.Utils

import kotlin.reflect.KClass


inline fun <reified T : Any> getKClass(): KClass<T> = T::class

fun <T : Any> getKClassFromJava(clazz: Class<T>): KClass<T> =
    (clazz.kotlin as KClass<T>)