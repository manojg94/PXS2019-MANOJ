package com.manoj.employeeattendance.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class employeeDetails {

    @SerializedName("emp_id")
    @Expose
    private String empId;
    @SerializedName("entry_at")
    @Expose
    private String entryAt;
    @SerializedName("exit_at")
    @Expose
    private String exitAt;

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEntryAt() {
        return entryAt;
    }

    public void setEntryAt(String entryAt) {
        this.entryAt = entryAt;
    }

    public String getExitAt() {
        return exitAt;
    }

    public void setExitAt(String exitAt) {
        this.exitAt = exitAt;
    }
}
