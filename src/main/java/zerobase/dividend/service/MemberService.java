package zerobase.dividend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.dividend.exception.impl.AlreadyExistUserException;
import zerobase.dividend.exception.impl.NoUserException;
import zerobase.dividend.exception.impl.UnMatchPassword;
import zerobase.dividend.model.Member;
import zerobase.dividend.model.constant.Authority;
import zerobase.dividend.persist.MemberRepository;
import zerobase.dividend.persist.entity.MemberEntity;
import zerobase.dividend.persist.entity.MemberRoleEntity;
import zerobase.dividend.web.dto.AuthRequest;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return memberRepository.findByUsernameFetchJoin(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "couldn't find user. -> " + username));
    }

    @Transactional
    public Member register(AuthRequest.SignUp signUp) {
        if (memberRepository.existsByUsername(signUp.username())) {
            throw new AlreadyExistUserException();
        }

        String encodedPw = passwordEncoder.encode(signUp.password());
        MemberEntity memberEntity = MemberEntity.of(signUp.username(), encodedPw);

        signUp.roles().stream()
                .map(Authority::match)
                .forEach(o -> memberEntity.addMemberRoleEntity(
                        MemberRoleEntity.of(o)));

        MemberEntity savedMember = memberRepository.save(memberEntity);
        return Member.fromEntity(savedMember);
    }

    public Member authenticate(AuthRequest.SignIn signIn) {
        MemberEntity memberEntity =
                memberRepository.findByUsernameFetchJoin(signIn.username())
                        .orElseThrow(
                                () -> new NoUserException());

        if (!passwordEncoder.matches(signIn.password(),
                memberEntity.getPassword())) {
            throw new UnMatchPassword();
        }

        return Member.fromEntity(memberEntity);
    }
}
