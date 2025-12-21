package com.luixtech.utilities.masking;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MaskingUtilsTests {

    @Test
    public void masksIdCardWhenEnoughCharacters() {
        assertThat(MaskingUtils.maskIdCard("420111199001015762"))
                .isEqualTo("420***********5762");
    }

    @Test
    public void returnsOriginalIdCardWhenTooShortToMask() {
        assertThat(MaskingUtils.maskIdCard("1234567"))
                .isEqualTo("1234567");
    }

    @Test
    public void masksMobilePhoneWhenEnoughCharacters() {
        assertThat(MaskingUtils.maskMobilePhone("13812341234"))
                .isEqualTo("138****1234");
    }

    @Test
    public void returnsOriginalMobilePhoneWhenTooShortToMask() {
        assertThat(MaskingUtils.maskMobilePhone("1234567"))
                .isEqualTo("1234567");
    }

    @Test
    public void masksBankCardWhenEnoughCharacters() {
        assertThat(MaskingUtils.maskBankCard("6222600123451234"))
                .isEqualTo("622260******1234");
    }

    @Test
    public void returnsOriginalBankCardWhenTooShortToMask() {
        assertThat(MaskingUtils.maskBankCard("1234567890"))
                .isEqualTo("1234567890");
    }
}

