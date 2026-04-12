package lems.cowshed.service.reissue;

import io.jsonwebtoken.ExpiredJwtException;
import lems.cowshed.config.jwt.JwtUtil;
import lems.cowshed.domain.user.Role;
import lems.cowshed.global.exception.BusinessException;
import lems.cowshed.global.exception.NotFoundException;
import lems.cowshed.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static lems.cowshed.global.exception.Message.*;
import static lems.cowshed.global.exception.Reason.USER_REISSUE_FAIL;

@RequiredArgsConstructor
@Service
public class ReissueService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshRepository;

    public String reissue(String refresh) {
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException ex) {
            throw new BusinessException(USER_REISSUE_FAIL, USER_REFRESH_NOT_EXPIRED);
        }

        String category = jwtUtil.getCategory(refresh);
        if (isNotRefreshToken(category)) {
            throw new BusinessException(USER_REISSUE_FAIL, USER_REFRESH_NOT_MATCH_CATEGORY);
        }

        if (refreshRepository.isNotExists(refresh)) {
            throw new NotFoundException(USER_REISSUE_FAIL, USER_REFRESH_NOT_FOUND);
        }
        return createNewAccessToken(refresh);
    }

    private boolean isNotRefreshToken(String category) {
        return !category.equals("refresh");
    }

    private String createNewAccessToken(String refresh) {
        long userId = jwtUtil.getUserId(refresh);
        String username = jwtUtil.getUsername(refresh);
        String email = jwtUtil.getUserEmail(refresh);
        Role role = jwtUtil.getRole(refresh);
        return jwtUtil.createJwt("access", userId, username, email, role.name(), 10 * 60 * 1000L);
    }
}
