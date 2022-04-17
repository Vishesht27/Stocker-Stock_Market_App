package com.example.stocker.domain.repository

import com.example.stocker.domain.model.CompanyListing
import com.example.stocker.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote:Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

}