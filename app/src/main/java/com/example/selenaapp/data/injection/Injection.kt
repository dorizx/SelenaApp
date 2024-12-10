package com.example.selenaapp.data.injection

import android.content.Context
import com.example.selenaapp.data.preference.UserPreference
import com.example.selenaapp.data.preference.dataStore
import com.example.selenaapp.data.repository.HomeRepository
import com.example.selenaapp.data.repository.UserRepository

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }

    fun provideUserPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }

    fun provideHomeRepository(context: Context): HomeRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return HomeRepository(pref)
    }
}