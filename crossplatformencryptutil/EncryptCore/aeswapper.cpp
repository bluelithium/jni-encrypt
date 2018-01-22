#include "stdafx.h"
#include "openssl/aes.h"
#include "aeswapper.h"

#define AES_BLOCK_SIZE 16

bool AesWapper::Encrypt(unsigned char * pInBuffer, int nSize, unsigned char * pOutBuffer)
{
	//128bit key buffer
	unsigned char key[16] = { 0 };

	GenerateKey(key, 16);

	//ʹ��AES CBC ���м���
	AES_KEY aesKey;
	memset(&aesKey, 0x00, sizeof(AES_KEY));
	if (AES_set_encrypt_key(key, 128, &aesKey) < 0)
	{
		return false;
	}

	//��������
	unsigned char ivec[AES_BLOCK_SIZE];//���ܵĳ�ʼ������
	memset( ivec, 0, AES_BLOCK_SIZE );

	unsigned char ucCurs = 'b';
	for (int i = 0; i < AES_BLOCK_SIZE; ++i)//���ܽ���Ҫһ������
	{
		ivec[i] = ucCurs++;
	}

	//����
	AES_cbc_encrypt(pInBuffer, pOutBuffer, nSize, &aesKey, ivec, AES_ENCRYPT);

	return true;
}

bool AesWapper::Decrypt(unsigned char * pInBuffer, int nSize, unsigned char * pOutBuffer)
{
	//128bit key buffer
	unsigned char key[16] = { 0 };

	GenerateKey(key, 16);

	//ʹ��AES CBC ���м���
	AES_KEY aesKey;
	memset(&aesKey, 0x00, sizeof(AES_KEY));
	if (AES_set_decrypt_key(key, 128, &aesKey) < 0)
	{
		return false;
	}

	//��������
	unsigned char ivec[AES_BLOCK_SIZE];//���ܵĳ�ʼ������
	memset(ivec, 0, AES_BLOCK_SIZE);

	unsigned char ucCurs = 'b';
	for (int i = 0; i < AES_BLOCK_SIZE; ++i)//���ܽ���Ҫһ������
	{
		ivec[i] = ucCurs++;
	}

	//����
	AES_cbc_encrypt(pInBuffer, pOutBuffer, nSize, &aesKey, ivec, AES_DECRYPT);

	return true;
}


int AesWapper::GetEncryptedSize(int nSrcSize)
{
	int nEncrytpedSize = nSrcSize;
	//�������AES_BLOCK_SIZE������������Ҫ��ȫ
	if ( nSrcSize % AES_BLOCK_SIZE != 0 ) {
		nEncrytpedSize = nSrcSize + AES_BLOCK_SIZE - (nSrcSize % AES_BLOCK_SIZE);
	}
	
	return nEncrytpedSize;
}

unsigned char magicTable[10] = { 0x18, 0x19, 0xff, 0x45, 0x75, 0x53, 0x18, 0xfa, 0x4e, 0x65 };

void AesWapper::GenerateKey(unsigned char * key, int keySize)
{ 
	if ( keySize != 16 || key == NULL) {
		return;
	}

	long long nTemp = 3154298576123032;
	for ( int ix = 0; ix != 16; ix++ )
	{
		key[ix] = nTemp % 10;
		nTemp = nTemp / 10;
	}

	for ( int ix = 0; ix != 16; ix++ ) 
	{
		key[ix] = key[ix] ^ 0xa8;
	}
}