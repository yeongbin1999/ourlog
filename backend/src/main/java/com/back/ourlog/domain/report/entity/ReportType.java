package com.back.ourlog.domain.report.entity;

public enum ReportType {
    SPAM("스팸 또는 광고"),
    INAPPROPRIATE("부적절한 콘텐츠"),
    HARASSMENT("괴롭힘 또는 폭력적인 내용"),
    HATE_SPEECH("혐오 발언 또는 차별적 표현"),
    ETC("기타 사유");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

