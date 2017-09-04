#ifndef BASE64_H_FOR_ZD
#define BASE64_H_FOR_ZD

#include <ctype.h>

class Base64
{
public:
	Base64();
	~Base64();

public:
	//base 64 ���뻺����
	//ע����Ҫʹ�� delete [] ret �ͷŻ�����
	unsigned char * Encoder(const unsigned char *src, size_t len, int *outSize);

	//base 64 ���뻺����
	//ע����Ҫʹ��delete [] ret �ͷŻ�����
	unsigned char * Decoder(const unsigned char *src, size_t len, size_t *decsize);

private:
	inline bool is_base64(unsigned char c) {
		return (isalnum(c) || (c == '+') || (c == '/'));
	}
};

#endif
