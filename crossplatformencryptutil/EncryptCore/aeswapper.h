#ifndef AES_WAPPER_FOR_ZD
#define AES_WAPPER_FOR_ZD

class AesWapper 
{
public:
	//AES 加密
	bool Encrypt(unsigned char * pInBuffer, int nSize, unsigned char * pOutBuffer);

	//AES 解密
	bool Decrypt(unsigned char * pInBuffer, int nSize, unsigned char * pOutBuffer);

	//获取加密后的buffer大小
	int GetEncryptedSize( int nSrcSize );
private:
	//生成加密KEy 128bit(16)
	void GenerateKey(unsigned char * key, int keySize);
};

#endif
