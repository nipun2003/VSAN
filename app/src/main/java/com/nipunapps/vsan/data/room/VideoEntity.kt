package com.nipunapps.vsan.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nipunapps.vsan.utils.Constants

@Entity(tableName = Constants.VIDEO_DATABASE)
class VideoEntity(@ColumnInfo(name = Constants.VIDEO_METADATA) val string: String,@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = false) val id : Int) {
}