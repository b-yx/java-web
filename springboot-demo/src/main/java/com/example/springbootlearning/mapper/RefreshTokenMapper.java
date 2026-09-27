package com.example.springbootlearning.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootlearning.entity.RefreshToken;


@Mapper 
public interface RefreshTokenMapper extends BaseMapper<RefreshToken> { //BaseMapper<TokenResponse> {
    
}
