package cn.icheer.portal.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author fangweihao
 * @since 2025/7/4 9:30
 */
class AesDecryptUtilsTest {

    @Test
    public void aesDecryptUtilsTest() {
        String content = "77aa12e6b4f8d768c83869de20b2eb90cdcacd6de794b48c6647681e1a3b48cebd42f6e8c7a79b9a82f823b686263416403318e73d26dabab12a85183c4b5ad6d40d06667156a67c6381fd6bc552265010f865b0c7e8e6b9d33183ae5d6b825a5e5500b295de039b2996610151d4ce0bff1feb05c6b35c65d436a8e9983fd5304d63e46b31df77895c4d36812db5f383ad238e866ac7bea8ea6221357f256e7373bfe96ded25740edf00c2bfcd4e514a50aa45bf1433fc5f670e9f45e4845f176a3796f92f2748597d4a454e7fea5279c8df921da6c45e31808d1a9925d1da03b511848c337a06cde6b8734f480e8881f4848970bab788b2eb003f3751596289b60ad0dda12d5619e8c9fca2a73bf755564ae15563fc932cd286615dad555ba7f932dedcc40e14feb624c65f6db5ee35";
        System.out.println(AesDecryptUtils.aesDecrypt(content));
    }

}