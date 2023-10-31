package zerobase.dividend.model.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Authority {
    READ("ROLE_READ"),
    WRITE("ROLE_WRITE");

    private final String key;

    public static Authority match(String s) {
        for (Authority authority : Authority.values()) {
            if (authority.getKey().equals(s)) {
                return authority;
            }
        }
        return null;
    }
}

// spring security에서 권한정보를 사용할 때 prefix인 ROLE_ 뒷 부분부터 사용한다.