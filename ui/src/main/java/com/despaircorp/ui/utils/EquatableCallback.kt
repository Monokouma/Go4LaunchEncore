package com.despaircorp.ui.utils

class EquatableCallback(private val id: String, private val callback: () -> Unit) {
    
    override fun equals(other: Any?): Boolean {
        return other is EquatableCallback && other.id == id
    }
    
    override fun hashCode(): Int {
        return id.hashCode()
    }
    
    fun invoke() {
        callback()
    }
}