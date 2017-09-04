#ifndef MD5_SIGN_TOOL_FOR_ZD
#define MD5_SIGN_TOOL_FOR_ZD

class Md5SignTool 
{
public:
	std::string Sign( std::vector<std::string> vec_strs );

private:
	unsigned char * GetSalt();
	std::string Byte2Hex(unsigned char bArray[], int bArray_len);
};

#endif // !MD5_SIGN_TOOL_FOR_ZD

