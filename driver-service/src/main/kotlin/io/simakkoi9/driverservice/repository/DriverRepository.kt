package io.simakkoi9.driverservice.repository

import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.Driver
import io.simakkoi9.driverservice.model.entity.EntryStatus
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DriverRepository : JpaRepository<Driver, Long> {
    fun existsByEmailAndStatus(number: String, status: EntryStatus): Boolean

    fun existsByCarAndStatus(car: Car, status: EntryStatus): Boolean

    fun findAllByCarAndStatus(car: Car, status: EntryStatus): List<Driver>

    fun findByIdAndStatus(id: Long, status: EntryStatus): Optional<Driver>

    fun findAllByStatus(status: EntryStatus, pageable: Pageable): Page<Driver>
}