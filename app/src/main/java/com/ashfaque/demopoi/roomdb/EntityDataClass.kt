package com.ashfaque.demopoi.roomdb

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ashfaque.demopoi.Constants.TABLE_NAME
import org.json.JSONArray
import org.json.JSONObject


@Entity(tableName = TABLE_NAME, indices = [Index(value = ["title"], unique = true)])
data class EntityDataClass(

    @PrimaryKey(autoGenerate = true)
    val id:Long= 0,
    val title: String,
    val ownerName: String,
    val tag: String,
    val establishedDate:String,
    val locationName: String,
    val lat: Double,
    val lng: Double,
    val createdDate:String
)

fun generateGeoJson(feature: EntityDataClass): String {
    // Create the geometry
    val geometry = JSONObject().apply {
        put("type", "Point")
        put("coordinates", JSONArray().apply {
            put(feature.lng)  // Longitude first
            put(feature.lat)   // Latitude second
        })
    }

    // Create the properties
    val properties = JSONObject().apply {
       // put("id", feature.id)
        put("Title", feature.title)
        put("Owner Name", feature.ownerName)
        put("Tag Name", feature.tag)
        put("Established Date", feature.establishedDate)
        put("Location Name", feature.locationName)
        put("Created Date", feature.createdDate)
    }

    // Create the feature
    val geoJsonFeature = JSONObject().apply {
        put("type", "Feature")
        put("geometry", geometry)
        put("properties", properties)
    }

    // Create the FeatureCollection
    val featureCollection = JSONObject().apply {
        put("type", "FeatureCollection")
        put("features", JSONArray().put(geoJsonFeature))
    }

    return featureCollection.toString(2) // Pretty print with indentation
}