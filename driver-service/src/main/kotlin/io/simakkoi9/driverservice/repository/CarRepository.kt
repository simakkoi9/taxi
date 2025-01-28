package io.simakkoi9.driverservice.repository

import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.UserStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CarRepository : CrudRepository<Car, Long> {
    fun existsByNumberAndStatus(number: String, status: UserStatus): Boolean

    fun findByIdAndStatus(id: Long, status: UserStatus): Optional<Car>

    fun findAllByStatus(status: UserStatus): List<Car>
}