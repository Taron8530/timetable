package com.example.timetable

data class SchoolDepartmentData(
    var schoolMajorinfo: List<SchoolMajorinfo>
) {
    data class SchoolMajorinfo(
        var head: List<Head>,
        var row: List<Row>
    ) {
        data class Head(
            var result: RESULT,
            var list_total_count: Int
        ) {
            class RESULT(
                var CODE: String,
                var MESSAGE: String
            )
        }

        data class Row(
            var ATPT_OFCDC_SC_CODE: String,
            var ATPT_OFCDC_SC_NM: String,
            var DDDEP_NM: String,
            var DGHT_CRSE_SC_NM: String,
            var LOAD_DTM: String,
            var ORD_SC_NM: String,
            var SCHUL_NM: String,
            var SD_SCHUL_CODE: String
        )
    }
}