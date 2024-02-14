package dev.verbosemode.spannerlocaltesting.persistence

import com.google.cloud.spring.data.spanner.core.mapping.Column
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey
import com.google.cloud.spring.data.spanner.core.mapping.Table
import dev.verbosemode.spannerlocaltesting.persistence.UserEntity.Companion.TABLE_NAME
import java.util.*

@Table(name = TABLE_NAME)
data class UserEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "name")
    val name: String,

    @Column(name = "email")
    val email: String
) {
    companion object {
        const val TABLE_NAME = "users"
    }
}