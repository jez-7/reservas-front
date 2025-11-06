package com.turnos.model

import com.google.gson.annotations.SerializedName


data class PopularServiceDto(
    @SerializedName("serviceName") val serviceName: String,
    @SerializedName("pricePerTurn") val pricePerTurn: Double,
    @SerializedName("totalTurns") val totalTurns: Int,
    @SerializedName("totalRevenue") val totalRevenue: Double
)

data class DailyRevenueDto(
    @SerializedName("day") val day: Int,
    @SerializedName("amount") val amount: Float
)


data class StatisticsResponseDto(

    @SerializedName("totalTurns") val totalTurns: Int,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("uniqueClients") val uniqueClients: Int,
    @SerializedName("cancellations") val cancellations: Int,


    @SerializedName("popularServices") val popularServices: List<PopularServiceDto>,


    @SerializedName("dailyRevenueData") val dailyRevenueData: List<DailyRevenueDto>
)