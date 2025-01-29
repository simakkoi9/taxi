package io.simakkoi9.driverservice.repository

import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.Driver
import io.simakkoi9.driverservice.model.entity.EntryStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface DriverRepository : CrudRepository<Driver, Long> {
    fun existsByEmailAndStatus(number: String, status: EntryStatus) : Boolean

    fun existsByCarAndStatus(car: Car, status: EntryStatus) : Boolean

    fun findByIdAndStatus(id: Long, status: EntryStatus): Optional<Driver>

    fun findAllByStatus(status: EntryStatus): List<Driver>
}