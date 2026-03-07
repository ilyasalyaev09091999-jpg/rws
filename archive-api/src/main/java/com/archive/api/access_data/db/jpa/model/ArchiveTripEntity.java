package com.archive.api.access_data.db.jpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "archive_trip")
@Getter
@Setter
public class ArchiveTripEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_file_name", nullable = false)
    private String sourceFileName;

    @Column(name = "source_row_num", nullable = false)
    private Integer sourceRowNum;

    @Column(name = "source_system", nullable = false)
    private String sourceSystem = "xlsx";

    @Column(name = "voyage_name")
    private String voyageName;

    @Column(name = "trip_type")
    private String tripType;

    @Column(name = "tug_name")
    private String tugName;

    @Column(name = "departure_point", nullable = false)
    private String departurePoint;

    @Column(name = "destination_point", nullable = false)
    private String destinationPoint;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "duration_days", insertable = false, updatable = false)
    private Integer durationDays;

    @Column(name = "cargo_type")
    private String cargoType;

    @Column(name = "cargo_amount")
    private BigDecimal cargoAmount;

    @Column(name = "units_count")
    private Integer unitsCount;

    @Column(name = "draft_m")
    private BigDecimal draftM;

    @Column(name = "counterparty_name")
    private String counterpartyName;

    @Column(name = "counterparty_inn")
    private String counterpartyInn;

    @Column(name = "flag")
    private String flag;

    @Column(name = "region_from")
    private String regionFrom;

    @Column(name = "region_to")
    private String regionTo;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
