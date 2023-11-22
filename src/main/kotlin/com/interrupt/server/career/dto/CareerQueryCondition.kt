package com.interrupt.server.career.dto

data class CareerQueryCondition(
    val jobGroupIds: List<Long> = emptyList(),
    val jobIds: List<Long> = emptyList(),
    val skillGroupIds: List<Long> = emptyList(),
    val skillIds: List<Long> = emptyList(),
    val minCareerYears: Int? = null,
    val maxCareerYears: Int? = null
) {

}
