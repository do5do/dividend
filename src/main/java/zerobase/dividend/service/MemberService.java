package zerobase.dividend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zerobase.dividend.model.Member;
import zerobase.dividend.model.constant.Authority;
import zerobase.dividend.persist.MemberRepository;
import zerobase.dividend.persist.entity.MemberEntity;
import zerobase.dividend.persist.entity.MemberRole;
import zerobase.dividend.web.dto.Auth;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "couldn't find user. -> " + username));
    }

    public Member register(Auth.SignUp signUp) {
        if (memberRepository.existsByUsername(signUp.username())) {
            throw new RuntimeException("username is already in use.");
        }

        String encodedPw = passwordEncoder.encode(signUp.password());
        MemberEntity memberEntity = MemberEntity.of(signUp.username(), encodedPw);

        signUp.roles().stream()
                .map(Authority::match)
                .forEach(o -> memberEntity.addMemberRole(MemberRole.of(o)));

        MemberEntity savedMember = memberRepository.save(memberEntity);
        return Member.fromEntity(savedMember);
    }

    public Member authenticate(Auth.SignIn signIn) {
        MemberEntity memberEntity =
                memberRepository.findByUsername(signIn.username())
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "username does not exist. -> "
                                        + signIn.username()));

        boolean matches = passwordEncoder.matches(signIn.password(),
                memberEntity.getPassword());
        if (!matches) {
            throw new RuntimeException("password do not match.");
        }

        return Member.fromEntity(memberEntity);
    }
}
