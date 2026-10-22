package com.example.employeeboard.whitelist;

import com.example.employeeboard.common.BizException;
import com.example.employeeboard.whitelist.mapper.WhitelistMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * WhitelistServiceImpl 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class WhitelistServiceImplTest {

    @Mock
    private WhitelistMapper whitelistMapper;

    @InjectMocks
    private WhitelistServiceImpl whitelistService;

    @Test
    void should_returnTrue_when_empNoInWhitelist() {
        when(whitelistMapper.selectByEmpNo(anyString(), anyString())).thenReturn(new WhitelistDO());

        assertTrue(whitelistService.isAllowed("A1001"));
    }

    @Test
    void should_returnFalse_when_empNoNotInWhitelistOrBlank() {
        when(whitelistMapper.selectByEmpNo(anyString(), anyString())).thenReturn(null);

        assertFalse(whitelistService.isAllowed("A9999"));
        assertFalse(whitelistService.isAllowed(null));
    }

    @Test
    void should_throwWl002_when_addDuplicate() {
        when(whitelistMapper.selectByEmpNo(anyString(), anyString())).thenReturn(new WhitelistDO());

        BizException e = assertThrows(BizException.class,
                () -> whitelistService.add("A1001", "张三", null, "admin"));
        assertEquals("WL_002", e.getErrorCode());
    }

    @Test
    void should_throwWl003_when_addParamInvalid() {
        BizException e = assertThrows(BizException.class,
                () -> whitelistService.add("", "张三", null, "admin"));
        assertEquals("WL_003", e.getErrorCode());
    }

    @Test
    void should_throwWl004_when_removeLastRecord() {
        when(whitelistMapper.selectByEmpNo(anyString(), anyString())).thenReturn(new WhitelistDO());
        when(whitelistMapper.countActive(anyString())).thenReturn(1L);

        BizException e = assertThrows(BizException.class, () -> whitelistService.remove("A1001"));
        assertEquals("WL_004", e.getErrorCode());
    }

    @Test
    void should_remove_when_moreThanOneRecord() {
        when(whitelistMapper.selectByEmpNo(anyString(), anyString())).thenReturn(new WhitelistDO());
        when(whitelistMapper.countActive(anyString())).thenReturn(2L);

        whitelistService.remove("A1001");
    }
}
