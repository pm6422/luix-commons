package com.luixtech.springbootframework.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request Redirect Result")
public class RedirectResult {
    @Schema(description = "redirect URL, it represent no redirect if it is empty", example = "http://localhost:5002/client-login?token=7aab3ae8-ec91-4119-b0ed-da8ccf5e77ad")
    private String redirectUrl;
}
