package com.qwert2603.base_mvp.model

interface IdentifiableLong {
    companion object {
        val NO_ID = -1L
    }

    val id: Long
}


interface IdentifiableString {
    companion object {
        val NO_ID = "NO_ID"
    }

    val objectId: String
}