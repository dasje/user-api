package io.memorix.modules

import io.memorix.user.UserRepository
import org.koin.dsl.module

val userDi = module {
    single<UserRepository> {
        UserRepository(
            database = get()
        )
    }
}
