#include <vector>
#include <string>
#include <cstring>

#include "../EncryptCore/aeswapper.h"
#include "../EncryptCore/base64.h"
#include "../EncryptCore/md5signtool.h"
#include "cn_javatiku_keymanager_utils_JniEncryptUtil.h"

#define SRC_LEN_SIZE 4				//保存原始缓存区大小所占的空间

JNIEXPORT jbyteArray JNICALL Java_com_example_encrypt_JniEncryptUtil_decryptString
(JNIEnv *env, jclass, jbyteArray srcBytes)
{
	jbyteArray jbyteRet = NULL;
	AesWapper * pAes = NULL;
	Base64 * pBase64 = NULL;
	unsigned char * pOut = NULL;
	size_t nWillDecryptBufferSize = 0;
	unsigned char * pWillDecryptBuffer = NULL;

	size_t nEncryptedBufferSize = 0;
	unsigned char * pEncryptedBuffer = NULL;

	unsigned char * pBase64Buffer = (unsigned char*)env->GetByteArrayElements(srcBytes, NULL);
	int nBase64Size = (int)env->GetArrayLength(srcBytes);

	//Step 1 : base64 解码
	pBase64 = new Base64();
	if (pBase64 == NULL)
	{
		goto _exit_zd;
	}

	pWillDecryptBuffer = pBase64->Decoder((const unsigned char*)pBase64Buffer, nBase64Size, &nWillDecryptBufferSize);
	if (pWillDecryptBuffer == NULL)
	{
		goto _exit_zd;
	}

	//Step 2 : AES 解密
	pAes = new AesWapper();
	pOut = new unsigned char[nWillDecryptBufferSize];
	memset(pOut, 0, nWillDecryptBufferSize);
	if (pAes->Decrypt(pWillDecryptBuffer, nWillDecryptBufferSize, pOut) == false)
	{
		goto _exit_zd;
	}
	else
	{
		//step 3 : 根据缓存区pWillEncryptBuffer前4字节,得出加密前缓存区大小
		int nSrcLen = 0;
		memcpy(&nSrcLen, pOut, SRC_LEN_SIZE);

		pEncryptedBuffer = new unsigned char[nSrcLen];
		memcpy(pEncryptedBuffer, pOut + SRC_LEN_SIZE, nSrcLen);

		jbyteRet = env->NewByteArray(nSrcLen);
		env->SetByteArrayRegion(jbyteRet, 0, nSrcLen, (jbyte*)pEncryptedBuffer);
	}

_exit_zd:

	if (pBase64 != NULL)
	{
		delete pBase64;
		pBase64 = NULL;
	}

	if (pWillDecryptBuffer != NULL)
	{
		delete[] pWillDecryptBuffer;
		pWillDecryptBuffer = NULL;
	}

	if (pEncryptedBuffer != NULL)
	{
		delete[] pEncryptedBuffer;
		pEncryptedBuffer = NULL;
	}

	if (pAes != NULL)
	{
		delete pAes;
		pAes = NULL;
	}

	if (pOut != NULL)
	{
		delete[] pOut;
		pOut = NULL;
	}

	if (pWillDecryptBuffer != NULL)
	{
		env->ReleaseByteArrayElements(srcBytes, (jbyte*)pWillDecryptBuffer, 0);
		pWillDecryptBuffer = NULL;
	}

	return jbyteRet;
}

JNIEXPORT jbyteArray JNICALL Java_com_example_encrypt_JniEncryptUtil_encryptString
(JNIEnv *env, jclass, jbyteArray srcBytes)
{
	jbyteArray jbyteRet = NULL;
	int nEncryptedSize = 0;
	unsigned char * pOut = NULL;
	Base64 * pBase64Wapper = NULL;
	int nOutputLen = 0;
	unsigned char * pBase64Buffer = NULL;

	unsigned char * pSrcBuffer = (unsigned char*)env->GetByteArrayElements(srcBytes, NULL);

	//step 1 :将加密缓存区原始长度保存到加密缓存区前4个字节
	int nSrclen = (int)env->GetArrayLength(srcBytes);
	unsigned char * pWillEncryptBuffer = new unsigned char[nSrclen + SRC_LEN_SIZE];//这里加4个长度，是为了保存原始缓存区大小
	memset(pWillEncryptBuffer, 0, nSrclen);
	memcpy(pWillEncryptBuffer, &nSrclen, SRC_LEN_SIZE);
	memcpy(pWillEncryptBuffer + SRC_LEN_SIZE, pSrcBuffer, nSrclen);

	//step 2 : 加密
	AesWapper * pAesWapper = new AesWapper();
	if (pAesWapper == NULL)
	{
		goto _exit_zd;
	}

	// *  这里需要注意　aes cbc 模式加密后的长度会被按照AES块去填充，
	// * 但最多不会超过一个 AES_BLOCK_SIZE，因此这里申请一个最大的填充空间
	nEncryptedSize = pAesWapper->GetEncryptedSize(nSrclen + SRC_LEN_SIZE);
	pOut = new unsigned char[nEncryptedSize];
	if (pOut == NULL)
	{
		goto _exit_zd;
	}

	memset(pOut, 0, nEncryptedSize);

	if (pAesWapper->Encrypt(pWillEncryptBuffer, nSrclen + SRC_LEN_SIZE, pOut) == false)
	{
		goto _exit_zd;
	}

	//step 2 ：base64
	pBase64Wapper = new Base64();
	pBase64Buffer = pBase64Wapper->Encoder((const unsigned char*)pOut, nEncryptedSize, &nOutputLen);
	if (pBase64Buffer == NULL)
	{
		goto _exit_zd;
	}
	else
	{
		jbyteRet = env->NewByteArray(nOutputLen);
		env->SetByteArrayRegion(jbyteRet, 0, nOutputLen, (jbyte*)pBase64Buffer);
	}

_exit_zd:
	if (pAesWapper != NULL)
	{
		delete pAesWapper;
		pAesWapper = NULL;
	}

	if (pWillEncryptBuffer != NULL)
	{
		delete[] pWillEncryptBuffer;
		pWillEncryptBuffer = NULL;
	}

	if (pOut != NULL)
	{
		delete[] pOut;
		pOut = NULL;
	}

	if (pBase64Buffer != NULL)
	{
		delete[] pBase64Buffer;
		pBase64Buffer = NULL;
	}

	if (pSrcBuffer != NULL)
	{
		env->ReleaseByteArrayElements(srcBytes, (jbyte *)pSrcBuffer, 0);
		pSrcBuffer = NULL;
	}

	if (pBase64Wapper != NULL)
	{
		delete pBase64Wapper;
		pBase64Wapper = NULL;
	}
	return jbyteRet;
}

std::string CopyToString(const char * pBuffer, int nLen)
{
	std::string retString = "";
	char * pTemp = new char[nLen + 1];

	if (pTemp != NULL) {
		memset(pTemp, 0, nLen + 1);
		memcpy(pTemp, pBuffer, nLen);
		retString = pTemp;

		delete[] pTemp;
		pTemp = NULL;
	}
	return retString;
}

JNIEXPORT jstring JNICALL Java_com_example_encrypt_JniEncryptUtil_generateUrlSign
(JNIEnv * env, jclass, jbyteArray jbaApi, jbyteArray jbaUrl, jbyteArray jbaTimestamp, jbyteArray jbaToken)
{
	jstring jsRet = NULL;
	std::vector<std::string> vec_strs;

	const char * pApi = (const char*)env->GetByteArrayElements(jbaApi, NULL);
	int nApiLen = env->GetArrayLength(jbaApi);

	const char * pUrl = (const char*)env->GetByteArrayElements(jbaUrl, NULL);
	int nUrlLen = env->GetArrayLength(jbaUrl);

	const char * pTimestamp = (const char*)env->GetByteArrayElements(jbaTimestamp, NULL);
	int nTimestampLen = env->GetArrayLength(jbaTimestamp);

	const char * pToken = (const char*)env->GetByteArrayElements(jbaToken, NULL);
	int nTokenLen = env->GetArrayLength(jbaToken);

	vec_strs.push_back(CopyToString(pApi, nApiLen));
	vec_strs.push_back(CopyToString(pUrl, nUrlLen));
	vec_strs.push_back(CopyToString(pTimestamp, nTimestampLen));
	vec_strs.push_back(CopyToString(pToken, nTokenLen));

	Md5SignTool * pMd5Signtool = new Md5SignTool();
	std::string stMd5Hex = pMd5Signtool->Sign(vec_strs);

	jsRet = env->NewStringUTF((const char*)stMd5Hex.c_str());

	if (pApi != NULL)
	{
		env->ReleaseByteArrayElements(jbaApi, (jbyte*)pApi, 0);
		pApi = NULL;
	}

	if (pUrl != NULL)
	{
		env->ReleaseByteArrayElements(jbaUrl, (jbyte*)pUrl, 0);
		pUrl = NULL;
	}

	if (pTimestamp != NULL)
	{
		env->ReleaseByteArrayElements(jbaTimestamp, (jbyte*)pTimestamp, 0);
		pTimestamp = NULL;
	}

	if (pToken != NULL)
	{
		env->ReleaseByteArrayElements(jbaToken, (jbyte*)pToken, 0);
		pToken = NULL;
	}

	if (pMd5Signtool != NULL)
	{
		delete pMd5Signtool;
		pMd5Signtool = NULL;
	}
	return jsRet;
}

JNIEXPORT jstring JNICALL Java_com_example_encrypt_JniEncryptUtil_getVersion
(JNIEnv * env, jclass)
{
	return env->NewStringUTF("V 1.0");
}