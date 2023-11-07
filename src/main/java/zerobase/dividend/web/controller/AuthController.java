package zerobase.dividend.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.dividend.model.Member;
import zerobase.dividend.security.TokenProvider;
import zerobase.dividend.service.MemberService;
import zerobase.dividend.web.dto.AuthRequest;
import zerobase.dividend.web.dto.AuthResponse;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid
                                             AuthRequest.SignUp request) {
        Member member = memberService.register(request);
        return ResponseEntity.ok(new AuthResponse(member.username()));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody @Valid
                                             AuthRequest.SignIn request) {
        Member member = memberService.authenticate(request);
        String token = tokenProvider.generateToken(
                member.username(), member.memberRoles());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);

        log.info("user login -> {}", member.username());
        return ResponseEntity.ok()
                .headers(headers)
                .body(new AuthResponse(member.username()));
    }
}
