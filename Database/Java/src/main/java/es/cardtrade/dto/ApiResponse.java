package es.cardtrade.dto;
public record ApiResponse(boolean correcto, String mensaje, Object datos) {
 public static ApiResponse ok(String mensaje,Object datos){ return new ApiResponse(true,mensaje,datos); }
 public static ApiResponse error(String mensaje){ return new ApiResponse(false,mensaje,null); }
}
