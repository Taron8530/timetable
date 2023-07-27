package com.example.timetable

data class SchoolMealData(
    var mealServiceDietInfo: List<MealServiceDietInfo>
) {
    data class MealServiceDietInfo(
        var head: List<Head>,
        var row: List<Row>
    ) {
        data class Head(
            var result: RESULT,
            var list_total_count: Int
        ) {
            data class RESULT(
                var CODE: String,
                var MESSAGE: String
            )
        }

        data class Row(
            var ATPT_OFCDC_SC_CODE: String,
            var ATPT_OFCDC_SC_NM: String,
            var CAL_INFO: String,
            var DDISH_NM: String,
            var MLSV_FGR: Double,
            var MLSV_FROM_YMD: String,
            var MLSV_TO_YMD: String,
            var MLSV_YMD: String,
            var MMEAL_SC_CODE: String,
            var MMEAL_SC_NM: String,
            var NTR_INFO: String,
            var ORPLC_INFO: String,
            var SCHUL_NM: String,
            var SD_SCHUL_CODE: String
        )
    }
}