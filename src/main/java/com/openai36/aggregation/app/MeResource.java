package com.openai36.aggregation.app;

import com.openai36.aggregation.app.rep.MeResponse;
import com.openai36.aggregation.common.GeneralOperationResult;
import com.openai36.aggregation.security.Login;
import com.openai36.aggregation.security.SecurityUtil;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Login
@RestController
public class MeResource {
    @Inject
    private SecurityUtil securityUtil;

    @GetMapping(path = "me")
    public GeneralOperationResult<MeResponse> me(Authentication authentication) {
        MeResponse meResponse = new MeResponse();
        meResponse.setUsername(authentication.getName());

        Integer userId = securityUtil.getUserId(authentication);

        return GeneralOperationResult.success(meResponse);
    }
}
