package com.mt.common.domain.model.validate;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import org.junit.jupiter.api.Test;

public class ValidatorTest {
    @Test
    public void testWhitelistOnly() {
        assertThrows(DefinedRuntimeException.class, () -> {
            Validator.whitelistOnly("<");
        });
    }

    @Test
    public void testWhitelistOnly1() {
        assertThrows(DefinedRuntimeException.class, () -> {
            Validator.whitelistOnly("a<");
        });
    }

    @Test
    public void testWhitelistOnly2() {
        assertThrows(DefinedRuntimeException.class, () -> {
            Validator.whitelistOnly("à/è/ì/ò/ù");
        });
    }

    @Test
    public void testWhitelistOnly3() {
        Validator.whitelistOnly("abc");
    }

    @Test
    public void testWhitelistOnly4() {
        Validator.whitelistOnly("测试 ");
    }

    @Test
    public void testWhitelistOnly5() {
        Validator.whitelistOnly("。");
    }

    @Test
    public void testWhitelistOnly6() {
        Validator.whitelistOnly("，");
    }

    @Test
    public void testWhitelistOnly7() {

        assertThrows(DefinedRuntimeException.class, () -> {
            Validator.whitelistOnly("《");
        });
    }

    @Test
    public void testWhitelistOnly8() {
        assertThrows(DefinedRuntimeException.class, () -> {
            Validator.whitelistOnly("》");
        });
    }

    @Test
    public void testWhitelistOnly9() {
        assertThrows(DefinedRuntimeException.class, () -> {
            Validator.whitelistOnly("·");
        });
    }

    @Test
    public void testWhitelistOnly10() {
        assertThrows(DefinedRuntimeException.class, () -> {
            Validator.whitelistOnly("、");
        });
    }

    @Test
    public void testWhitelistOnly11() {
        assertThrows(DefinedRuntimeException.class, () -> {
            Validator.whitelistOnly("！");
        });
    }

    @Test
    public void testWhitelistOnly12() {
        Validator.whitelistOnly("!");
    }

    @Test
    public void testWhitelistOnly13() {
        Validator.whitelistOnly(",");
    }

    @Test
    public void testWhitelistOnly14() {
        Validator.whitelistOnly(".");
    }
}