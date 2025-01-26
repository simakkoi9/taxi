package io.simakkoi9.driverservice.repository;

import io.simakkoi9.driverservice.model.entity.Driver
import io.simakkoi9.driverservice.model.entity.UserStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface DriverRepository : CrudRepository<Driver, Long> {
    fun findByIdAndStatus(id: Long, status: UserStatus): Optional<Driver>

    fun findAllByStatus(status: UserStatus): Iterable<Driver>
}