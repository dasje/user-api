package io.memorix.user

import org.koin.dsl.module

val userDi = module {
    single<UserRepository> {
        UserRepository(
            database = get()
        )
    }
}
