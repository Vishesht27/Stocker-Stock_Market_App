package com.example.stocker.data.repository

import com.example.stocker.data.local.StockDatabase
import com.example.stocker.data.mapper.toCompanyListing
import com.example.stocker.data.remote.StockApi
import com.example.stocker.domain.model.CompanyListing
import com.example.stocker.domain.repository.StockRepository
import com.example.stocker.util.Resource
import com.opencsv.CSVReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db : StockDatabase
): StockRepository {

    private val dao = db.dao


    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListing = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListing.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListing.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = ! isDbEmpty && !fetchFromRemote
            if(shouldJustLoadFromCache){
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
//                val csvReader = CSVReader(InputStreamReader(response.byteStream()))
            } catch (e:IOException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            } catch (e:HttpException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }
        }
    }
}