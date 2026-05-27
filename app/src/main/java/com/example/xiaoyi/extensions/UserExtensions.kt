package com.example.xiaoyi.extensions

import com.example.xiaoyi.data.database.entity.UserEntity
import com.example.xiaoyi.model.User


fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        studentId = this.studentId,
        passwordHash = this.passwordHash,
        avatarUrl = this.avatarUrl,
        phone = this.phone,
        school = this.school,
        creditScore = this.creditScore,
        createdAt = this.createdAt,
        token = this.token
    )
}
fun UserEntity.toModel(): User {
    return User(
        id = this.id,
        username = this.username,
        studentId = this.studentId,
        passwordHash = this.passwordHash,
        avatarUrl = this.avatarUrl,
        phone = this.phone,
        school = this.school,
        creditScore = this.creditScore,
        createdAt = this.createdAt,
        token = this.token
    )
}
class UserExtensions {

}