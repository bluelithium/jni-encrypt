#ifndef AES_WAPPER_FOR_ZD
#define AES_WAPPER_FOR_ZD

class AesWapper 
{
public:
	//����
	bool Encrypt(unsigned char * pInBuffer, int nSize, unsigned char * pOutBuffer);

	//����
	bool Decrypt(unsigned char * pInBuffer, int nSize, unsigned char * pOutBuffer);

	//��ȡ���ܺ󻺳�������
	int GetEncryptedSize( int nSrcSize );
private:
	//������Կ,��Կ���ȱ���Ϊ128bit(16)
	void GenerateKey(unsigned char * key, int keySize);
};

#endif
