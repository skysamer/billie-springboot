package com.lab.smartmobility.billie.overtime.domain;

public enum ApprovalStatus {
    WAITING("대기중"),
    PRE("사전승인"),
    CONFIRMATION("근무확정"),
    FINAL("최종승인"),
    COMPANION("반려");

    private final String value;

    ApprovalStatus(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
