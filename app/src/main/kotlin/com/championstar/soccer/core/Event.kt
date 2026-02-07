package com.championstar.soccer.core

/**
 * Wrapper untuk data yang diekspos melalui LiveData yang hanya boleh diobservasi sekali.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Hanya bisa di-set secara internal

    /**
     * Mengembalikan konten dan mencegahnya digunakan lagi.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Mengembalikan konten, bahkan jika sudah ditangani.
     */
    fun peekContent(): T = content
}