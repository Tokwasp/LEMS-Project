package lems.cowshed.api.controller.reissue;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.global.exception.NotFoundException;
import lems.cowshed.service.reissue.ReissueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static lems.cowshed.global.exception.Message.USER_REFRESH_NOT_FOUND;
import static lems.cowshed.global.exception.Reason.USER_REISSUE_FAIL;

@RequiredArgsConstructor
@RestController
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<Void>> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = getRefreshCookie(request).orElseThrow(() -> new NotFoundException(USER_REISSUE_FAIL, USER_REFRESH_NOT_FOUND));
        String newAccessToken = reissueService.reissue(refresh);
        response.setHeader("Authorization", newAccessToken);
        return new ResponseEntity<>(CommonResponse.success(), HttpStatus.OK);
    }

    private Optional<String> getRefreshCookie(HttpServletRequest request) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }
        return Optional.ofNullable(refresh);
    }
}

