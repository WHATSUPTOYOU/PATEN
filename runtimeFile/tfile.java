
package cn.fxbin.bubble.plugin.token;

import cn.fxbin.bubble.plugin.token.exception.TokenExpiredException;
import java.util.Date;
import cn.fxbin.bubble.fireworks.core.util.StringUtils;
import io.jsonwebtoken.Jwts;
import cn.fxbin.bubble.fireworks.core.util.time.DateUtils;
import cn.fxbin.bubble.fireworks.core.util.SystemClock;
import cn.fxbin.bubble.fireworks.core.util.ObjectUtils;
import cn.fxbin.bubble.fireworks.core.util.CollectionUtils;
import cn.fxbin.bubble.fireworks.core.util.BeanUtils;
import java.util.Map;
import cn.fxbin.bubble.plugin.token.model.TokenPayload;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import io.jsonwebtoken.SignatureAlgorithm;

public class SingleJwt
{
    private long expire;
    private SignatureAlgorithm algorithm;
    private Key key;
    
    
    
    
    
    
    
    
    public TokenPayload parseToken(final String token) {
        final Map<String, Object> mapObj = (Map<String, Object>)Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token).getBody();
        final TokenPayload payload = (TokenPayload)BeanUtils.map2Object((Map)mapObj, (Class)TokenPayload.class);
        final long nowSeconds = SystemClock.INSTANCE.currentTimeMillis() / 1000L;
        if (nowSeconds > payload.getExp()) {
            throw new TokenExpiredException("token is expired");
        }
        return payload;
    }
    
    
}
