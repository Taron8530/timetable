package com.example.timetable

data class AcademicCalendarData(
    var SchoolSchedule : List<schoolSchedule>
) {
    data class schoolSchedule(
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
            var AA_YMD: String,
            var ATPT_OFCDC_SC_CODE: String,
            var ATPT_OFCDC_SC_NM: String,
            var AY: String,
            var DGHT_CRSE_SC_NM: String,
            var EVENT_CNTNT: String,
            var EVENT_NM: String,
            var FIV_GRADE_EVENT_YN: String,
            var FR_GRADE_EVENT_YN: String,
            var LOAD_DTM: String,
            var ONE_GRADE_EVENT_YN: String,
            var SBTR_DD_SC_NM: String,
            var SCHUL_CRSE_SC_NM: String,
            var SCHUL_NM: String,
            var SD_SCHUL_CODE: String,
            var SIX_GRADE_EVENT_YN: String,
            var THREE_GRADE_EVENT_YN: String,
            var TW_GRADE_EVENT_YN: String
        )
    }
}