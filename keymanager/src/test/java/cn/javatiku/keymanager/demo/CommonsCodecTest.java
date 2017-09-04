package cn.javatiku.keymanager.demo;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.RefinedSoundex;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.codec.net.BCodec;
import org.apache.commons.codec.net.QCodec;
import org.apache.commons.codec.net.URLCodec;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class CommonsCodecTest {

    private String plaintext;
    private String base32Ciphertext;
    private String base64Ciphertext;
    private String binarytext;
    private String hexCiphertext;
    private String md5Ciphertext;
    private String shaCiphertext;
    private String bCodectext;
    private String qCodectext;
    private String uRLCodectext;


    @Before
    public void setup() {
        plaintext = "hello world...";
        base32Ciphertext = "NBSWY3DPEB3W64TMMQXC4LQ=";
        base64Ciphertext = "aGVsbG8gd29ybGQuLi4=";
        hexCiphertext = "68656c6c6f20776f726c642e2e2e";
        binarytext = "0010111000101110001011100110010001101100011100100110111101110111001000000110111101101100011011000110010101101000";
        md5Ciphertext = "c95f105ab2434587045d9cf1e79ee9ef";
        shaCiphertext = "09979ca598482cc43df62914f11168e97d7314bf";
        bCodectext = "=?UTF-8?B?aGVsbG8gd29ybGQuLi4=?=";
        qCodectext = "=?UTF-8?Q?hello world...?=";
        uRLCodectext = "hello+world...";
    }

    /**
     * 编码规则：任意给定一个二进制数据，以5个位(bit)为一组进行切分(base64以6个位(bit)为一组)，
     * 对切分而成的每个组进行编码得到1个可见字符。Base32编码表字符集中的字符总数为2^5=32个
     * 编码表:
     * [0-31]
     * [A-Z 2-7]
     * 步骤如下：
     * 1)字符“bhst”取ASCII码之后，对其转换成二进制得到“1100010,1101000,1110011,1110100,”共四个字节，28个bit的二进制串。
     * 注：因为base32是属于传输8bit字节代码的编码方式，所以这里要对“bhst”字符串对应的二进制最高位加0变成每组8个bit。
     * 组成32个bit的二进制串。
     * 2)以5个bit为一组对“bhst”字符串对应的二进制串进行切分。得到“01100,01001,10100,00111,00110,11101,00000”7个字节的“bhst”二进制串。
     * 注：每组的二进制串不足5个用0补充。
     * 3)计算每组二进制串所对应的十进制，然后参考标准Base32编码表，找出所对应的编码字符，组合成密文。
     * 注：最后一个分组位数不足4个的时候，则用字符“=”编码。
     */
    @Test
    public void base32Test() {
        Base32 base32 = new Base32();
        String ciphertext = base32.encodeAsString(plaintext.getBytes());
        assertEquals(base32Ciphertext, ciphertext);
        String cleartext = new String(base32.decode(base32Ciphertext));
        assertEquals(plaintext, cleartext);
    }

    /**
     * 编码规则：
     * 64个字符用6个bit位就可以全部表示，一个字节有8个bit 位，剩下两个bit就浪费掉了，这样就不得不牺牲一部分空间了。
     * 这里需要弄明白的就是一个Base64字符是8个bit，但是有效部分只有右边的6个 bit，左边两个永远是0。
     * 编码表:
     * [0-63]
     * [A-Z a-z 0-9 + /]
     * 编码原理:
     * 将3个字节转换成4个字节( (3 X 8) = 24 = (4 X 6) )先读入3个字节,每读一个字节,左移8位,再右移四次,每次6位,这样就有4个字节了
     * 解码原理:
     * 将4个字节转换成3个字节.先读入4个6位(用或运算),每次左移6位,再右移3次,每次8位.这样就还原了.
     * 注意：
     * 面提到编码是以3个字节为单位，当剩下的字符数量不足3个字节时，则应使用0进行填充，相应的，输出字符则使用'='占位，因此编码后输出的文本末尾可能会出现1至2个'='。
     */
    @Test
    public void base64Test() {
        Base64 base64 = new Base64();
        String ciphertext = base64.encodeAsString(plaintext.getBytes());
        assertEquals(base64Ciphertext, ciphertext);
        String cleartext = new String(base64.decode(base64Ciphertext));
        assertEquals(plaintext, cleartext);
    }

    /**
     * hex
     */
    @Test
    public void hexTest() throws EncoderException, DecoderException {
        Hex hex = new Hex();
        String ciphertext = new String(hex.encode(plaintext.getBytes()));
        assertEquals(hexCiphertext,ciphertext);
        String cleartext = new String(hex.decode(hexCiphertext.getBytes()));
        assertEquals(plaintext,cleartext);
    }

    /**
     * 二进制编码
     */
    @Test
    public void binaryCodecTest(){
        BinaryCodec binaryCodec = new BinaryCodec();
        String ciphertext = new String(binaryCodec.encode(plaintext.getBytes()));
        assertEquals(binarytext,ciphertext);
        String cleartext = new String(binaryCodec.decode(binarytext.getBytes()));
        assertEquals(plaintext,cleartext);
    }


    /**
     * md5加密算法
     */
    @Test
    public void md5HexTest() {
        String cipherText = DigestUtils.md5Hex(plaintext);
        assertEquals(md5Ciphertext, cipherText);
    }


    /**
     * sha1加密算法
     */
    @Test
    public void shaTest() {
        String cipherText = DigestUtils.sha1Hex(plaintext.getBytes());
        assertEquals(shaCiphertext, cipherText);
    }

    /**
     * Metaphone及Soundex编码
     *
     * Metaphone 建立出相同的key给发音相似的单字, 比 Soundex 还要准确, 但是 Metaphone 没有固定长度, Soundex 则是固定第一个英文字加上3个数字. 这通常是用在类似音比对, 也可以用在 MP3 的软件开发.
     *
     * Soundex 算法的运作方式是把某个字母表中的每个字母映射成代表它的语音组的一个数字代码。在这个方案里，像 d和 t这样的字母在同一个组里，因为它们发音相近（实际上每个字母都是用类似的机制发出声音的），而元音则一概忽略。通过对整体单词应用这种映射，就产生了单词的语音“键”。发音相近的单词通常会有相同的键。
     *
     * Metaphone算法明确地对英语发音的公共规则进行了编码，而这正是 Soundex 没有解决的问题。
     */
    @Test
    public void languageTest(){
        Metaphone metaphone = new Metaphone();
        RefinedSoundex refinedSoundex = new RefinedSoundex();
        Soundex soundex = new Soundex();
        for (int i = 0; i < 2; i++) {
            String str = (i == 0) ? "resume" : "resin";
            String mString = null;
            String rString = null;
            String sString = null;
            try {
                mString = metaphone.encode(str);
                rString = refinedSoundex.encode(str);
                sString = soundex.encode(str);
            } catch (Exception ex) {
            }
            System.out.println("Original:" + str);
            System.out.println("Metaphone:" + mString);
            System.out.println("RefinedSoundex:" + rString);
            System.out.println("Soundex:" + sString + "\n");
        }
    }

    /**
     *BCodec
     */
    @Test
    public void bCodecTest() throws EncoderException, DecoderException {
        BCodec bCodec = new BCodec();
        String ciphertext = bCodec.encode(plaintext, CharEncoding.UTF_8);
        assertEquals(bCodectext,ciphertext);
        String cleartext = bCodec.decode(ciphertext);
        assertEquals(plaintext,cleartext);
    }

    /**
     * QCodec
     */
    @Test
    public void QCodecTest() throws EncoderException, DecoderException {
        QCodec qCodec = new QCodec();
        String ciphertext = qCodec.encode(plaintext, CharEncoding.UTF_8);
        assertEquals(qCodectext,ciphertext);
        String cleartext = qCodec.decode(ciphertext);
        assertEquals(plaintext,cleartext);
    }

    /**
     * url编码解码
     */
    @Test
    public void URLCodecTest() throws UnsupportedEncodingException, DecoderException {
        URLCodec urlCodec = new URLCodec();
        String ciphertext = urlCodec.encode(plaintext,CharEncoding.UTF_8);
        assertEquals(uRLCodectext,ciphertext);
        String cleartext = urlCodec.decode(ciphertext);
        assertEquals(plaintext,cleartext);
    }

}
