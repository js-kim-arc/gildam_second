package com.example.demo.service;

import com.example.demo.api.KakaoApiResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(properties = {
        "KAKAO_REST_API_KEY=67601b0b18e85ccff5d9561f80cd53b1"
})
@ActiveProfiles("local")
class KakaoAddressSearchServiceTest{

    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService;

    @Test
    @DisplayName("address 파라미터 값이 null이면, requestAddressSearch는 null을 리턴한다")
    void returnNullWhenAddressIsNull() {
        // given
        String address = null;

        // when
        KakaoApiResponseDto result = kakaoAddressSearchService.requestAddressSearch(address);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("받은 값이 유효한 주소일 경우, requestAddressSearch는 document 리스트를 반환한다")
    void returnDocumentsWhenAddressIsValid() {
        // given
        String address = "서울 성북구 종암로 10길";

        // when
        KakaoApiResponseDto result = kakaoAddressSearchService.requestAddressSearch(address);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDocumentList()).isNotNull();

        assertThat(result.getMetaDto().getTotalCount()).isGreaterThan(0);

        assertThat(result.getDocumentList())
                .anySatisfy(document -> {
                    assertThat(document.getAddressName()).isNotNull();
                    assertThat(document.getLatitude()).isNotZero();
                    assertThat(document.getLongitude()).isNotZero();
                });
    }

    @ParameterizedTest(name = "[{index}] 주소: {0} → 기대 결과: {1}")
    @MethodSource("addressProvider")
    @DisplayName("주소를 위도/경도로 변환할 수 있는지 확인")
    void convertAddressToLatLng(String inputAddress, boolean expectedResult) {
        // when
        KakaoApiResponseDto searchResult = kakaoAddressSearchService.requestAddressSearch(inputAddress);

        boolean actualResult = searchResult != null && !searchResult.getDocumentList().isEmpty();

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> addressProvider() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of("서울 특별시 성북구 종암동", true),
                org.junit.jupiter.params.provider.Arguments.of("서울 성북구 종암동 91", true),
                org.junit.jupiter.params.provider.Arguments.of("서울 대학로", true),
                org.junit.jupiter.params.provider.Arguments.of("서울 성북구 종암동 잘못된 주소", false),
                org.junit.jupiter.params.provider.Arguments.of("광진구 구의동 251-45", true),
                org.junit.jupiter.params.provider.Arguments.of("광진구 구의동 251-455555", false),
                org.junit.jupiter.params.provider.Arguments.of("", false)
                        );
    }
}
