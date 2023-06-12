package com.mt.common.domain.model.validate;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import org.junit.Test;

public class ValidatorTest {
    @Test(expected = DefinedRuntimeException.class)
    public void testWhitelistOnly() {
        Validator.whitelistOnly("<");
    }
    @Test(expected = DefinedRuntimeException.class)
    public void testWhitelistOnly1() {
        Validator.whitelistOnly("a<");
    }
    @Test(expected = DefinedRuntimeException.class)
    public void testWhitelistOnly2() {
        Validator.whitelistOnly("à/è/ì/ò/ù");
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
    @Test(expected = DefinedRuntimeException.class)
    public void testWhitelistOnly7() {
        Validator.whitelistOnly("《");
    }
    @Test(expected = DefinedRuntimeException.class)
    public void testWhitelistOnly8() {
        Validator.whitelistOnly("》");
    }
    @Test(expected = DefinedRuntimeException.class)
    public void testWhitelistOnly9() {
        Validator.whitelistOnly("·");
    }
    @Test(expected = DefinedRuntimeException.class)
    public void testWhitelistOnly10() {
        Validator.whitelistOnly("、");
    }
    @Test(expected = DefinedRuntimeException.class)
    public void testWhitelistOnly11() {
        Validator.whitelistOnly("！");
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