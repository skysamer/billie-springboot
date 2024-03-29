package com.lab.smartmobility.billie.vacation.domain;

public enum ApprovalStatus {
    WAITING("대기중"),
    TEAM("부장승인"),
    FINAL("최종승인"),
    COMPANION("반려"),
    CANCEL("취소");

    private final String value;

    ApprovalStatus(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
