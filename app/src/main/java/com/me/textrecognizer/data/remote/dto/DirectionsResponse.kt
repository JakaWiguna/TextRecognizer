package com.me.textrecognizer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DirectionsResponse(

	@field:SerializedName("destination_addresses")
	val destinationAddresses: List<String?>? = null,

	@field:SerializedName("rows")
	val rows: List<Row?>? = null,

	@field:SerializedName("origin_addresses")
	val originAddresses: List<String?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Duration(

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("value")
	val value: Int? = null
)

data class Row(

	@field:SerializedName("elements")
	val elements: List<Element?>? = null
)

data class Element(

	@field:SerializedName("duration")
	val duration: Duration? = null,

	@field:SerializedName("distance")
	val distance: Distance? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Distance(

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("value")
	val value: Int? = null
)
