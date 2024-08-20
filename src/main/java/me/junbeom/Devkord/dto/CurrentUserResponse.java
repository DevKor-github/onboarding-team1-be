package me.junbeom.Devkord.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.junbeom.Devkord.domain.User;

@Getter
@NoArgsConstructor
public class CurrentUserResponse {
    private Long id;
    private String email;
    private String nickname;

    public CurrentUserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }
}
