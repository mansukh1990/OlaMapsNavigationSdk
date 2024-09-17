package com.example.olanavigationnew.model


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class OlaSearchAutoCompleteResponse(
    @Json(name = "error_message")
    @Expose
    val errorMessage: String?,
//    @Json(name = "info_messages")
//    @Expose
//    val infoMessages: List<Any?>?,
    @Json(name = "predictions")
    @Expose
    val predictions: List<Prediction?>?,
    @Json(name = "status")
    @Expose
    val status: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Geometry(
    @Json(name = "location")
    @Expose
    val location: Location?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Location(
    @Json(name = "lat")
    @Expose
    val lat: Double?,
    @Json(name = "lng")
    @Expose
    val lng: Double?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class MainTextMatchedSubstring(
    @Json(name = "length")
    @Expose
    val length: Int?,
    @Json(name = "offset")
    @Expose
    val offset: Int?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class MatchedSubstring(
    @Json(name = "length")
    @Expose
    val length: Int?,
    @Json(name = "offset")
    @Expose
    val offset: Int?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Prediction(
    @Json(name = "description")
    @Expose
    val description: String?,
    @Json(name = "distance_meters")
    @Expose
    val distanceMeters: Int?,
    @Json(name = "geometry")
    @Expose
    val geometry: Geometry?,
    @Json(name = "layer")
    @Expose
    val layer: List<String?>?,
    @Json(name = "matched_substrings")
    @Expose
    val matchedSubstrings: List<MatchedSubstring?>?,
    @Json(name = "place_id")
    @Expose
    val placeId: String?,
    @Json(name = "reference")
    @Expose
    val reference: String?,
    @Json(name = "structured_formatting")
    @Expose
    val structuredFormatting: StructuredFormatting?,
    @Json(name = "terms")
    @Expose
    val terms: List<Term?>?,
    @Json(name = "types")
    @Expose
    val types: List<String?>?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class SecondaryTextMatchedSubstring(
    @Json(name = "length")
    @Expose
    val length: Int?,
    @Json(name = "offset")
    @Expose
    val offset: Int?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class StructuredFormatting(
    @Json(name = "main_text")
    @Expose
    val mainText: String?,
    @Json(name = "main_text_matched_substrings")
    @Expose
    val mainTextMatchedSubstrings: List<MainTextMatchedSubstring?>?,
    @Json(name = "secondary_text")
    @Expose
    val secondaryText: String?,
    @Json(name = "secondary_text_matched_substrings")
    @Expose
    val secondaryTextMatchedSubstrings: List<SecondaryTextMatchedSubstring?>?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Term(
    @Json(name = "offset")
    @Expose
    val offset: Int?,
    @Json(name = "value")
    @Expose
    val value: String?
) : Parcelable