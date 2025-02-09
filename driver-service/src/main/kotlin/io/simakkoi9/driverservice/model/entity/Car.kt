package io.simakkoi9.driverservice.model.entity

import io.simakkoi9.driverservice.model.converter.EntryStatusConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "cars")
open class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @NotNull
    @Column(name = "brand", nullable = false)
    open var brand: String? = null

    @NotNull
    @Column(name = "model", nullable = false)
    open var model: String? = null

    @NotNull
    @Column(name = "color", nullable = false)
    open var color: String? = null

    @NotNull
    @Column(name = "number", nullable = false)
    open var number: String? = null

    @NotNull
    @Column(name = "status", nullable = false)
    @Convert(converter = EntryStatusConverter::class)
    open var status: EntryStatus = EntryStatus.ACTIVE

    @NotNull
    @Column(name = "created_at", nullable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now()
}