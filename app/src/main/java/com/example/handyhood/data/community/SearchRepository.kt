package com.example.handyhood.data.community

interface SearchRepository {

    suspend fun searchPeople(): List<SearchPerson>

    suspend fun searchProviders(): List<SearchProvider>
}
