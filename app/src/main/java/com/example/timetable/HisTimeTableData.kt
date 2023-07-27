package com.example.timetable

data class HisTimeTableData(
    var hisTimetable: List<HisTimetable>
) {
    data class HisTimetable(
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
            var ALL_TI_YMD: String,
            var ATPT_OFCDC_SC_CODE: String,
            var ATPT_OFCDC_SC_NM: String,
            var AY: String,
            var CLASS_NM: String,
            var CLRM_NM: String,
            var DDDEP_NM: String,
            var DGHT_CRSE_SC_NM: String,
            var GRADE: String,
            var ITRT_CNTNT: String,
            var LOAD_DTM: String,
            var ORD_SC_NM: String,
            var PERIO: String,
            var SCHUL_NM: String,
            var SD_SCHUL_CODE: String,
            var SEM: String
        )
    }
}