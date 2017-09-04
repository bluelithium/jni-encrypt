// AES_CBC.cpp : Defines the entry point for the console application.
//

#include <stdio.h>
#include <cstring>
#include <vector>
#include <string>

#include "aeswapper.h"
#include "base64.h"
#include "md5signtool.h"

#define MSG_LEN 1280

void testUnit();

int main(int argc, char* argv[])
{
	// test memory link
	for (int ix = 0; ix != 100000; ix++) {
		testUnit();
	}
	

	getchar();
	return 0;
}


void testUnit() 
{
	unsigned char plainText[] = { "abcdefghijklmnopqrstuvwxyz" };
	int nMsgBufferSize = sizeof(plainText) / sizeof(char);

	AesWapper * wapper = new AesWapper();
	if (wapper == NULL) {
		goto _exit_;
	}

	nMsgBufferSize = wapper->GetEncryptedSize(nMsgBufferSize);
	unsigned char * encryptedBuffer = new  unsigned char[nMsgBufferSize];
	memset(encryptedBuffer, 0, nMsgBufferSize);

	unsigned char * tmp = new unsigned char[nMsgBufferSize];
	memset(tmp, 0, nMsgBufferSize);

	wapper->Encrypt(plainText, nMsgBufferSize, encryptedBuffer);
	
	wapper->Decrypt(encryptedBuffer, nMsgBufferSize, tmp);


	printf("%s\n", tmp);

	Base64 * base64Wapper = new Base64();
	int nBase64OutSize = 0;
	unsigned char * pBase64Text = base64Wapper->Encoder((const unsigned char*)plainText, nMsgBufferSize, &nBase64OutSize);
	printf("base64 : %s\n", pBase64Text);

	size_t nPlaintTextSize = 0;
	unsigned char * pPlainText = base64Wapper->Decoder(pBase64Text, strlen((const char*)pBase64Text), &nPlaintTextSize);
	printf("base64 decode: %s\n", pPlainText);


	//test MD5
	for(int i=1;i<1000;i++){
		Md5SignTool * pMd5Sign = new Md5SignTool();
		std::vector<std::string> * pVec_strs = new std::vector<std::string>();
		pVec_strs->push_back("1.0");
		pVec_strs->push_back("http://www.test.com/api/home/feed.html?id=1&page=20");
		pVec_strs->push_back("1487122467091");
		pVec_strs->push_back("296931c21a898f9c49d62f99668130");
		
		std::string md5 = pMd5Sign->Sign(*pVec_strs).c_str();
		printf("md5 : %s\n", md5.c_str());
		if (pMd5Sign != NULL)
		{
			delete pMd5Sign;
			pMd5Sign = NULL;
		}

		if (pVec_strs != NULL)
		{
			delete pVec_strs;
			pVec_strs = NULL;
		}
	}

_exit_:

	if (wapper != NULL)
	{
		delete wapper;
		wapper = NULL;
	}

	if (encryptedBuffer != NULL)
	{
		delete[] encryptedBuffer;
		encryptedBuffer = NULL;
	}

	if (tmp != NULL)
	{
		delete[] tmp;
		tmp = NULL;
	}

	if (base64Wapper != NULL)
	{
		delete base64Wapper;
		base64Wapper = NULL;
	}

	if (pBase64Text != NULL)
	{
		delete[] pBase64Text;
		pBase64Text = NULL;
	}

	if (pPlainText != NULL)
	{
		delete[] pPlainText;
		pPlainText = NULL;
	}

	
}