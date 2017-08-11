package com.vnext.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public class JwtUtil {
	
	private static final String JWT_SECRET = "7786df7fc3a34e26a61c034d5ec8245d";
	private static final int JWT_EXPIRATION = 60*60*1000;  //60分钟
	
	private static final String CLAIMKEY="claimKey";
	private static final String CLAIMKEYVALUE="eyJzdWIiOiJ7XCJsb2dpbk5hbWVcIValue";
	
	public static String generalSubject(String id,String loginName){
		JSONObject object = new JSONObject();
		object.put("id", id);
		object.put("loginName", loginName);
		return object.toJSONString();
	}
	
    public static String generateToken(String signingKey, String subject) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
		Date expTime = new Date(nowMillis + JwtUtil.JWT_EXPIRATION); 
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expTime)
                .claim(JwtUtil.CLAIMKEY, JwtUtil.CLAIMKEYVALUE)
                .signWith(SignatureAlgorithm.HS256, signingKey.getBytes());
        return builder.compact();
    }
    
    public static boolean isJwtValid(String jwt) {
        try {
            //解析JWT字符串中的数据，并进行最基础的验证
            Claims claims = Jwts.parser()
                    .setSigningKey(JwtUtil.JWT_SECRET.getBytes())   //SECRET_KEY是加密算法对应的密钥，jjwt可以自动判断机密算法
                    .parseClaimsJws(jwt)  //jwt是JWT字符串
                    .getBody();
            String vaule = claims.get(JwtUtil.CLAIMKEY, String.class);//获取自定义字段key
            //判断自定义字段是否正确
            if (JwtUtil.CLAIMKEYVALUE.equals(vaule)) {
                return true;
            } else {
                return false;
            }
        }
        //在解析JWT字符串时，如果密钥不正确，将会解析失败，抛出SignatureException异常，说明该JWT字符串是伪造的
        //在解析JWT字符串时，如果"过期时间字段"已经早于当前时间，将会抛出ExpiredJwtException异常，说明本次请求已经失效
        catch (SignatureException | ExpiredJwtException e) {
        	e.printStackTrace();
            return false;
        }catch (Exception e) {
        	e.printStackTrace();
        	return false;
		}
    }
    
    
    public static Map<String , String> getUserInfo (String token){
    	try {
    		Claims claims = Jwts.parser().setSigningKey(JwtUtil.JWT_SECRET.getBytes()).parseClaimsJws(token).getBody();
    		String subject = claims.getSubject();
    		Map<String, String> maps = new HashMap<String, String>();
    		JSONObject object = JSON.parseObject(subject);
    		String id = object.getString("id");
    		String loginName = object.getString("loginName");
    		maps.put("id", id);
    		maps.put("loginName", loginName);
    		return maps;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
/*    public static SysUser getSysUser (String token){
    	try {
    		Claims claims = Jwts.parser().setSigningKey(JwtUtil.JWT_SECRET.getBytes()).parseClaimsJws(token).getBody();
    		String subject = claims.getSubject();
    		SysUser sysUser = new SysUser();
    		JSONObject object = JSON.parseObject(subject);
    		String id = object.getString("id");
    		String loginName = object.getString("loginName");
    		sysUser.setId(id);
    		sysUser.setLoginName(loginName);
    		return sysUser;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }*/
    
}
