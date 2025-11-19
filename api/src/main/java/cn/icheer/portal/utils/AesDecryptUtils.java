package cn.icheer.portal.utils;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * @author fangweihao
 * @since 2025/7/3 17:55
 */
public class AesDecryptUtils {
    /**
     * AES解密
     * @return String
     */
    public static String aesDecrypt(String code) {
        if (StrUtil.isBlank(code)) {
            return "";
        }
        String token = "Exp0wellT0ken123";
        // 把token转化为字节（byte）作密钥
        byte[] key = token.getBytes();
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        return aes.decryptStr(code);
    }
}
