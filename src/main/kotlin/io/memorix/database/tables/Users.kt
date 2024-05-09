package io.memorix.database.tables

import org.jetbrains.exposed.sql.Table

object Users: Table() {
    val id = uuid(name = "id")
    val userName = varchar(name = "name", length = 128)
    val userEmail = varchar(name = "email", length = 128)
    val hashedPassword = varchar(name = "password", length = 128)

    override val primaryKey = PrimaryKey(id)
}