#include "stdafx.h"
#include "openssl/md5.h"
#include "openssl/aes.h"
#include "md5signtool.h"

std::string Md5SignTool::Byte2Hex(unsigned char bArray[], int nArraySize)
{
	std::string hexstr = "";
	for (int i = 0; i< nArraySize; i++)
	{
		char hex1;
		char hex2;
		int value = bArray[i];
		int v1 = value / 16;
		int v2 = value % 16;

		if (v1 >= 0 && v1 <= 9)
		{
			hex1 = (char)(48 + v1);
		}
		else
		{
			hex1 = (char)(55 + v1);
		}
		
		if (v2 >= 0 && v2 <= 9)
		{
			hex2 = (char)(48 + v2);
		}
		else
		{
			hex2 = (char)(55 + v2);
		}
		hexstr = hexstr + hex1 + hex2;
	}
	return hexstr;
}

std::string Md5SignTool::Sign(std::vector<std::string> vec_strs)
{
	unsigned char szMd5Result[17] = { 0 };

	MD5_CTX md5_ctx;
	MD5_Init(&md5_ctx);

	for ( std::vector<std::string>::iterator ix = vec_strs.begin(); ix != vec_strs.end(); ix++ )
	{
		MD5_Update(&md5_ctx, ix->c_str(), ix->length());
	}

	MD5_Final(szMd5Result, &md5_ctx);  //获取MD5

	//获取Salt
	unsigned char * pSalt = GetSalt();

	for ( int ix = 0; ix != 16; ix++ )
	{
		szMd5Result[ix] = szMd5Result[ix] ^ pSalt[ix] + pSalt[ix] * ix;
	}

	std::string md5HexString = Byte2Hex(szMd5Result, 16);

	if ( pSalt != NULL ) {
		delete[] pSalt;
		pSalt = NULL;
	}

	return md5HexString;
}

unsigned char * Md5SignTool::GetSalt()
{
	unsigned char * pSalt = new unsigned char[16];
	memset(pSalt, 0, 16);

	unsigned char * pMagic = new unsigned char[16];
	memset(pMagic, 0, 16);

	//128bit key buffer
	unsigned char key[16] = { 0 };

	//使用AES CBC 进行加密
	AES_KEY aesKey;
	memset(&aesKey, 0x00, sizeof(AES_KEY));
	if (AES_set_encrypt_key(key, 128, &aesKey) < 0)
	{
		return false;
	}

	//设置向量
	unsigned char ivec[16];//加密的初始化向量
	unsigned char ucCur = 'k';
	for (int i = 0; i < 16; ++i)//加密解密要一样就行
	{
		ivec[i] = (ucCur++) + 16 + (i) % 11;
	}
	
	//加密
	AES_cbc_encrypt(pMagic, pSalt, 16, &aesKey, ivec, AES_ENCRYPT);

	if (pMagic != NULL) {
		delete[] pMagic;
		pMagic = NULL;
	}
	return pSalt;
}
