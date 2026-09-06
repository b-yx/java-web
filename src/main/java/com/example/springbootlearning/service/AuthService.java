package com.example.springbootlearning.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootlearning.dto.LoginRequest;
import com.example.springbootlearning.dto.RegisterRequest;
import com.example.springbootlearning.entity.RefreshToken;
import com.example.springbootlearning.entity.User;
import com.example.springbootlearning.mapper.RefreshTokenMapper;
import com.example.springbootlearning.mapper.UserMapper;
import com.example.springbootlearning.security.JwtService;
import com.example.springbootlearning.vo.TokenResponse;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenMapper refreshTokenMapper;

    public AuthService(
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenMapper refreshTokenMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenMapper = refreshTokenMapper;
    }

    // ========== 注册 ==========
    public void register(RegisterRequest request) {
        // 1. 检查用户名是否已存在（你自己补上）
        // 2. 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        userMapper.insert(user);
    }

    // ========== 登录 ==========
    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 1. Spring Security 认证（用户名+密码）
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. ★★★ 认证成功，查询用户获取 userId ★★★
        // 因为 CustomUserDetailsService 里查过一次，但这里我们要拿 userId
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在"); // 理论上不会发生，因为已经认证成功了
        }

        // 3. 生成 Access Token（短有效期，如 15 分钟）
        String accessToken = jwtService.generateToken(user.getUsername());

        // 4. 生成 Refresh Token（随机字符串，长有效期，如 7 天）
        String refreshTokenStr = UUID.randomUUID().toString();

        // 5. 保存 Refresh Token 到数据库
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7天有效期
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshTokenMapper.insert(refreshToken);

        // 6. 返回两个 Token
        return new TokenResponse(accessToken, refreshTokenStr);
    }

    // ========== 刷新 Access Token ==========
    @Transactional
    public TokenResponse refresh(String refreshTokenStr) {
        // 1. 从数据库查询 Refresh Token
        LambdaQueryWrapper<RefreshToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RefreshToken::getToken, refreshTokenStr);
        RefreshToken token = refreshTokenMapper.selectOne(wrapper);

        // 2. 校验
        if (token == null) {
            throw new RuntimeException("Refresh Token 无效");
        }
        if (token.getRevoked()) {
            throw new RuntimeException("Refresh Token 已失效");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh Token 已过期");
        }

        // 3. ★★★ 根据 userId 查出用户，拿到 username 生成新的 Access Token ★★★
        User user = userMapper.selectById(token.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 4. 生成新的 Access Token（有效期 15 分钟）
        String newAccessToken = jwtService.generateToken(user.getUsername());

        // 5. ★★★ 可选：启用 Refresh Token Rotation（文档19）★★★
        // 这里先不实现 Rotation，直接返回旧的 refreshToken（简单模式）
        // 如果启用 Rotation，需要把旧的 revoked=true，生成新的 Refresh Token 存入数据库

        // 6. 返回新的 Access Token + 原有的 Refresh Token
        return new TokenResponse(newAccessToken, refreshTokenStr);
    }
}

// package com.example.springbootlearning.service;

// import java.time.LocalDateTime;

// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// import com.example.springbootlearning.dto.LoginRequest;
// import com.example.springbootlearning.dto.RegisterRequest;
// import com.example.springbootlearning.entity.RefreshToken;
// import com.example.springbootlearning.entity.User;
// import com.example.springbootlearning.mapper.RefreshTokenMapper;
// import com.example.springbootlearning.mapper.UserMapper;
// import com.example.springbootlearning.security.JwtService;
// import com.example.springbootlearning.vo.LoginResponse;
// import com.example.springbootlearning.vo.TokenResponse;

// @Service
// public class AuthService {
//     // 注册
//     private final PasswordEncoder passwordEncoder;
//     private final UserMapper userMapper;
//     // 登录
//     private final AuthenticationManager authenticationManager;
//         // 登录:JWT
//     private final JwtService jwtService;

//     private final  RefreshTokenMapper refreshTokenMapper;

//     public AuthService(
//         PasswordEncoder passwordEncoder,
//         UserMapper userMapper,
//         AuthenticationManager authenticationManager,
//         JwtService jwtService,
//         RefreshTokenMapper refreshTokenMapper
//     ) {
//         this.passwordEncoder = passwordEncoder;
//         this.userMapper = userMapper;
//         this.authenticationManager = authenticationManager;
//         this.jwtService=jwtService;
//         this.refreshTokenMapper= refreshTokenMapper;
//     }

//     // 注册
//     // 现在已经从controller里面拿到了这个request dto.包含username还有password.
//     public void register(RegisterRequest request) {

//         // 1. 检查用户名是否已经存在
//         // 使用你已经掌握的 MyBatis-Plus 查询即可

//         // 2. 创建 Entity
//         User user = new User();

//         user.setUsername(
//                 request.getUsername()
//         );

//         // 3. 密码加密
//         user.setPassword(
//                 passwordEncoder.encode(
//                         request.getPassword()
//                 )
//         );
//         // 3.5
//         user.setRole("USER");

//         // 4. 保存数据库
//         userMapper.insert(user);
//     }

//     // 登录
//     public LoginResponse login( //public void login(
//         LoginRequest request
//     ) {
//         Authentication authentication =
//                 authenticationManager.authenticate(
//                         new UsernamePasswordAuthenticationToken(
//                                 request.getUsername(),
//                                 request.getPassword()
//                         )
//                 );

//         String token =jwtService.generateToken(request.getUsername());
        
//         // refresh token

//         // return new LoginResponse(token);
//     }

    
//     public TokenResponse refresh(String refreshToken) {

//         RefreshToken token =
//                 refreshTokenMapper.selectOne(
//                         new LambdaQueryWrapper<RefreshToken>()
//                                 .eq(
//                                     RefreshToken::getToken,
//                                     refreshToken
//                                 )
//                 );

//         // if (token == null) {
//         //     throw new BusinessException(
//         //             "Refresh Token 无效"
//         //     );
//         // }

//         // if (token.getRevoked()) {
//         //     throw new BusinessException(
//         //             "Refresh Token 已失效"
//         //     );
//         // }

//         // if (token.getExpiresAt().isBefore(
//         //         LocalDateTime.now()
//         // )) {
//         //     throw new BusinessException(
//         //             "Refresh Token 已过期"
//         //     );
//         // }

//         // 先改用RuntimeException
//         if (token == null) {
//             throw new RuntimeException(
//                     "Refresh Token 无效"
//             );
//         }

//         if (token.getRevoked()) {
//             throw new RuntimeException(
//                     "Refresh Token 已失效"
//             );
//         }

//         if (token.getExpiresAt().isBefore(
//                 LocalDateTime.now()
//         )) {
//             throw new RuntimeException(
//                     "Refresh Token 已过期"
//             );
//         }

//         String accessToken =
//                 jwtService.generateToken(
//                         token.getUserId()
//                 );

//         return new TokenResponse(
//                 accessToken,
//                 refreshToken
//         );
//     }
    

// }
