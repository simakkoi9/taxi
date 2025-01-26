package io.simakkoi9.driverservice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import lombok.Getter
import lombok.Setter
import lombok.ToString

@Getter
@Setter
@ToString
@Entity
@Table(name = "drivers")
open class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

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
    @Enumerated(value = EnumType.STRING)
    open var gender: Gender? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    open var car: Car? = null

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    open var status: UserStatus? = UserStatus.ACTIVE
}