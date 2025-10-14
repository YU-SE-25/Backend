// 약관의 종류를 코드로 관리하는 Enum

package com.unide.backend.domain.terms.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Terms {
    TERMS_OF_SERVICE("서비스 이용 약관"),
    PRIVACY_POLICY("개인정보 수집 및 이용 동의");

    private final String description;
}
