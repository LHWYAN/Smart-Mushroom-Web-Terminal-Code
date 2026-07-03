package com.smartmushroom.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ApiResponse} 单元测试。
 * 覆盖 ok(data)、ok(data, total)、fail(message) 三个静态工厂方法。
 */
@DisplayName("ApiResponse - 统一响应体")
class ApiResponseTest {

    @Test
    @DisplayName("ok(data) 返回 code=0, message=success, data=传入值, total=null")
    void ok_withData_returnsSuccessResponse() {
        ApiResponse<String> response = ApiResponse.ok("hello");

        assertAll("成功响应字段校验",
                () -> assertEquals(0, response.getCode(), "code 应为 0"),
                () -> assertEquals("success", response.getMessage(), "message 应为 success"),
                () -> assertEquals("hello", response.getData(), "data 应为传入值"),
                () -> assertNull(response.getTotal(), "total 应为 null")
        );
    }

    @Test
    @DisplayName("ok(data, total) 返回 code=0 且 total 字段正确")
    void ok_withDataAndTotal_returnsSuccessWithTotal() {
        ApiResponse<String> response = ApiResponse.ok("page1", 42L);

        assertAll("带 total 的成功响应字段校验",
                () -> assertEquals(0, response.getCode(), "code 应为 0"),
                () -> assertEquals("success", response.getMessage(), "message 应为 success"),
                () -> assertEquals("page1", response.getData(), "data 应为传入值"),
                () -> assertEquals(42L, response.getTotal(), "total 应为 42")
        );
    }

    @Test
    @DisplayName("ok(data, total=0) total 字段为 0 而非 null")
    void ok_withZeroTotal_totalIsZero() {
        ApiResponse<String> response = ApiResponse.ok("empty", 0L);

        assertEquals(0L, response.getTotal(), "total 应为 0");
    }

    @Test
    @DisplayName("fail(message) 返回 code=-1, data=null, message=传入值")
    void fail_returnsFailureResponse() {
        ApiResponse<Object> response = ApiResponse.fail("操作失败");

        assertAll("失败响应字段校验",
                () -> assertEquals(-1, response.getCode(), "code 应为 -1"),
                () -> assertEquals("操作失败", response.getMessage(), "message 应为传入的错误信息"),
                () -> assertNull(response.getData(), "data 应为 null"),
                () -> assertNull(response.getTotal(), "total 应为 null")
        );
    }

    @Test
    @DisplayName("fail(null) 返回 code=-1, message=null, data=null")
    void fail_withNullMessage_returnsFailureWithNullMessage() {
        ApiResponse<Object> response = ApiResponse.fail(null);

        assertAll("null message 失败响应校验",
                () -> assertEquals(-1, response.getCode()),
                () -> assertNull(response.getMessage()),
                () -> assertNull(response.getData())
        );
    }

    @Test
    @DisplayName("ok 接受 null data")
    void ok_withNullData_returnsSuccessWithNullData() {
        ApiResponse<Object> response = ApiResponse.ok(null);

        assertEquals(0, response.getCode());
        assertNull(response.getData());
    }
}
