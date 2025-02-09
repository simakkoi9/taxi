package io.simakkoi9.driverservice.repository

import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.EntryStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CarRepository : JpaRepository<Car, Long> {
    fun existsByNumberAndStatus(number: String, status: EntryStatus): Boolean

    fun findByIdAndStatus(id: Long, status: EntryStatus): Optional<Car>

    fun findAllByStatus(status: EntryStatus, pageable: Pageable): Page<Car>
}