package io.simakkoi9.driverservice.model.entity

import io.simakkoi9.driverservice.model.converter.EntryStatusConverter
import io.simakkoi9.driverservice.model.converter.GenderConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "drivers")
open class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @NotNull
    @Column(name = "external_id", nullable = false)
    open var externalId: String? = null

    @NotNull
    @Column(name = "name", nullable = false)
    open var name: String? = null

    @NotNull
    @Column(name = "email", nullable = false)
    open var email: String? = null

    @NotNull
    @Column(name = "phone", nullable = false)
    open var phone: String? = null

    @NotNull
    @Column(name = "gender", nullable = false)
    @Convert(converter = GenderConverter::class)
    open var gender: Gender? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    open var car: Car? = null

    @NotNull
    @Column(name = "status", nullable = false)
    @Convert(converter = EntryStatusConverter::class)
    open var status: EntryStatus? = EntryStatus.ACTIVE

    @NotNull
    @Column(name = "created_at", nullable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now()
}