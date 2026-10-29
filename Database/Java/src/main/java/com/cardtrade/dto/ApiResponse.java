package com.cardtrade.dto;
public record ApiResponse<T>(boolean correcto, String mensaje, T datos) {
  public static <T> ApiResponse<T> ok(String mensaje,T datos){ return new ApiResponse<>(true,mensaje,datos); }
  public static ApiResponse<Void> error(String mensaje){ return new ApiResponse<>(false,mensaje,null); }
}
