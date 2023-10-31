package zerobase.dividend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.dividend.model.Member;
import zerobase.dividend.security.TokenProvider;
import zerobase.dividend.service.MemberService;
import zerobase.dividend.web.dto.Auth;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<Member> signup(@RequestBody @Valid Auth.SignUp request) {
        return ResponseEntity.ok(memberService.register(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody @Valid Auth.SignIn request) {
        Member member = memberService.authenticate(request);
        return ResponseEntity.ok(tokenProvider.generateToken(
                member.username(), member.memberRoles()));
    }
}
