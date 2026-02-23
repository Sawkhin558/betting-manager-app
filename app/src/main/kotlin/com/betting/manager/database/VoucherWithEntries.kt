package com.betting.manager.database

import androidx.room.Embedded
import androidx.room.Relation

data class VoucherWithEntries(
    @Embedded
    val voucher: VoucherEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "voucher_id"
    )
    val entries: List<EntryEntity>
)
