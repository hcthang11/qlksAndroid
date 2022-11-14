package com.ming6464.ungdungquanlykhachsanmctl.DTO;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Orders {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int customID,UID,status,total;
    @Nullable
    private String note;

    public Orders(int customID, int UID, int total, @Nullable String note) {
        this.customID = customID;
        this.UID = UID;
        this.status = 0;
        this.total = total;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomID() {
        return customID;
    }

    public void setCustomID(int customID) {
        this.customID = customID;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Nullable
    public String getNote() {
        return note;
    }

    public void setNote(@Nullable String note) {
        this.note = note;
    }
}
