package com.example.timetable

data class schoolInfoData(
    var schoolInfo: List<SchoolInfo>
) {
    data class SchoolInfo(
        var head: List<Head>,
        var row: List<Row>
    ) {
        data class Head(
            var result : RESULT,
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
            var COEDU_SC_NM: String,
            var DGHT_SC_NM: String,
            var ENE_BFE_SEHF_SC_NM: String,
            var ENG_SCHUL_NM: String,
            var FOAS_MEMRD: String,
            var FOND_SC_NM: String,
            var FOND_YMD: String,
            var HMPG_ADRES: String,
            var HS_GNRL_BUSNS_SC_NM: String,
            var HS_SC_NM: String,
            var INDST_SPECL_CCCCL_EXST_YN: String,
            var JU_ORG_NM: String,
            var LCTN_SC_NM: String,
            var LOAD_DTM: String,
            var ORG_FAXNO: String,
            var ORG_RDNDA: String,
            var ORG_RDNMA: String,
            var ORG_RDNZC: String,
            var ORG_TELNO: String,
            var SCHUL_KND_SC_NM: String,
            var SCHUL_NM: String,
            var SD_SCHUL_CODE: String,
            var SPCLY_PURPS_HS_ORD_NM: Any
        )
    }
}