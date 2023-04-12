package com.example.demo.member.oauth2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NamedQuery;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;




@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

        private final MemberRepository memberRepository;
        private final HttpSession httpSession;

        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                log.info("CustomOAuth2UserService 진입");

                OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
                OAuth2User oAuth2User = delegate.loadUser(userRequest);

                OAuth2AccessToken accessToken = userRequest.getAccessToken();
                log.info("accessToken : {}", accessToken);

                Map<String, Object> additionalParameters = userRequest.getAdditionalParameters();
                log.info("Parameters : {}", additionalParameters);

                Map<String, Object> attributes = oAuth2User.getAttributes();
                log.info("attributes : {}", attributes);

                ClientRegistration clientRegistration = userRequest.getClientRegistration();
                log.info("clientRegistration : {}", clientRegistration);



                return oAuth2User;
                // OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
                //                 oAuth2User.getAttributes());
                // Member member = saveOrUpdate(attributes, registrationId);

                // httpSession.setAttribute("member", member);

                // return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("USER")),
                //                 attributes.getAttributes(), attributes.getNameAttributeKey());


        }
        private Member saveOrUpdate(OAuthAttributes attributes, String registrationId) {
                Member member = memberRepository.findByEmail(attributes.getEmail())
                                .map(e -> e.update())
                                .orElse(attributes.toEntity(registrationId));

                return memberRepository.save(member);
        }
}
