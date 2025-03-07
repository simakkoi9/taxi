package io.simakkoi9.driverservice.model.dto.rest

data class PageResponse<T>(
    val content: List<T>?,

    val size: Int,

    val page: Int,

    val totalPages: Int,

    val totalElements: Long
)
