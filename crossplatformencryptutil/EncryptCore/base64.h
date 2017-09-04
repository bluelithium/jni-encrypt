#ifndef BASE64_H_FOR_ZHAODONG
#define BASE64_H_FOR_ZHAODONG

#include <ctype.h>

class Base64
{
public:
	Base64();
	~Base64();

public:
	//base 64 编码缓存区
	//注意需要使用 delete [] ret 释放缓存区
	unsigned char * Encoder(const unsigned char *src, size_t len, int *outSize);

	//base 64 解码缓存区
	//注意需要使用delete [] ret 释放缓存区
	unsigned char * Decoder(const unsigned char *src, size_t len, size_t *decsize);

private:
	inline bool is_base64(unsigned char c) {
		return (isalnum(c) || (c == '+') || (c == '/'));
	}
};

#endif