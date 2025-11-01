// app/src/main/java/com/example/accordionapp/model/User.kt
package com.example.accordionapp.model

enum class UserRole {
    ADMIN,      // Can edit all pages
    EDITOR,     // Can edit specific pages
    VIEWER      // Read-only access
}

data class User(
    val id: String,
    val username: String,
    val password: String, // In a real app, this would be hashed
    val role: UserRole,
    val editablePages: List<Int> = emptyList() // Pages this user can edit (only for EDITOR role)
)
