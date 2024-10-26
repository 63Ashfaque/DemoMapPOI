package com.ashfaque.demopoi.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InterfaceDAO {

    //
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(mDataClass: EntityDataClass):Long

    @Update
    suspend fun updateData(mDataClass: EntityDataClass):Int

    @Delete
    suspend fun deleteData(mDataClass: EntityDataClass):Int

    @Query("DELETE FROM tableName WHERE id = :id")
    fun deleteDataById(id: Long):Int

    @Query("SELECT * FROM tableName")
    fun getAllRecord():LiveData<List<EntityDataClass>>

    // Query to retrieve a specific Note by its ID
    @Query("SELECT * FROM tableName WHERE id = :id")
    fun getRecordById(id: Long): EntityDataClass

    @Query("SELECT * FROM tableName " +
            "WHERE title LIKE '%' || :query || '%' " +
            "OR ownerName LIKE '%' || :query || '%' " +
            "OR locationName LIKE '%' || :query || '%' " +
            "OR lat LIKE '%' || :query || '%' " +
            "OR lng LIKE '%' || :query || '%' " +
            "OR establishedDate LIKE '%' || :query || '%' " +
            "OR tag LIKE '%' || :query || '%' " +
            "OR createdDate LIKE '%' || :query || '%'")
    fun searchRecordBy(query: String): LiveData<List<EntityDataClass>>
}