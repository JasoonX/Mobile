package com.jsports.helpers

class CustomRegex {
    companion object{
        const val USERNAME = """^[a-z0-9_-]{3,16}$"""
        const val FULL_NAME = """^([a-zA-Z0-9]+|[a-zA-Z0-9]+\s[a-zA-Z0-9]+|[a-zA-Z0-9]+\s[a-zA-Z0-9]{3,}\s[a-zA-Z0-9]+)$"""
        const val DATE = """^([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))$"""
    }
}